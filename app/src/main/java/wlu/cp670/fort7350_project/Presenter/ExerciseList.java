package wlu.cp670.fort7350_project.Presenter;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.util.Xml;
import android.view.View;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import wlu.cp670.fort7350_project.Models.DatabaseTables;
import wlu.cp670.fort7350_project.Models.Exercise;
import wlu.cp670.fort7350_project.Models.ExerciseListItem;
import wlu.cp670.fort7350_project.R;
import wlu.cp670.fort7350_project.Utils.ExerciseFilter;
import wlu.cp670.fort7350_project.Utils.XMLValidator;
import wlu.cp670.fort7350_project.Views.ExerciseDataFragment;
import wlu.cp670.fort7350_project.Views.ExerciseListFragment;


/*references:
  Lesson10: Parsing XML with XMLPullParser
      https://mylearningspace.wlu.ca/d2l/le/content/316534/viewContent/1855346/View
      https://www.javatpoint.com/android-XMLPullParser-tutorial
      https://medium.com/@owaistnt/getting-most-out-of-asynctask-with-mvp-bfa9cacdf600

*/

/**
 * <h1>ExerciseList</h1>
 * ExerciseList implements IExerciseList.Presenter
 * linking IExerciseList.View with the data contained in the
 * <i>exercise.xml</i> file
 *
 * @author Adam Fortier
 * @version 1.0
 * @since 2019-11-12
 */
public class ExerciseList implements IExerciseList.Presenter {

    private final IExerciseList.View view;
    private XMLParse xmlParse = null;

    private ArrayList<ExerciseListItem> exerciseList = null;

    //asset file names
    private static final String fileName = "exercises.xml";
    private static final String schemaFileName = "exercises.xsd";

    private String xmlSource;
    private static final String TAG = "ExerciseList";

    /**
     * Getter method for view associated with this
     * ExerciseList object
     *
     * @return  IExerciseList.View
     */

    public IExerciseList.View getView() {
        return view;
    }

    public String getFileName() {
        return fileName;
    }


    public String getXmlSource() {
        return xmlSource;
    }

    public ExerciseList(String xmlSource, IExerciseList.View view) {
        this.xmlSource = xmlSource;
        this.view = view;
    }

    @Override
    public void updateExerciseList(ArrayList<ExerciseListItem> exerciseList) {
        this.exerciseList = exerciseList;
    }

    @Override
    //called from onDestroy() of ExerciseList activity
    public void stop(){
        if (xmlParse != null || xmlParse.getStatus() == AsyncTask.Status.RUNNING){
            xmlParse.cancel(true);
        }
    }

    public void getExerciseList(){
        xmlParse = new XMLParse(this, view);
        xmlParse.execute(xmlSource, fileName);
    }

    @Override
    public void filterExerciseList(ExerciseFilter filter, String filterValue) {

    }


    public int loadDefaults(Context context){
        /* NOV4 remove sharedPreferences
        //get the shared preference file
        String preferenceFile = context.getString(R.string.preference_name);
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceFile, Context.MODE_PRIVATE);  //context.MODE_PRIVATE = 0

        //get the key and value
        String loadedKey = context.getString(R.string.preference_key_default_loaded);
        boolean keyValue = sharedPreferences.getBoolean(loadedKey, false );
        */
        int retVal = 0;
        //get reference to exercises.xml file
        File file = new File(context.getFilesDir(), fileName);
        boolean existsFlag = file.exists();

        if (existsFlag){
            boolean deleteResult = context.deleteFile("exercise.xml");
            String temp[] = context.fileList();
            if (deleteResult) existsFlag = false;
        }

        //default has not been loaded or file does not exist
        if (!existsFlag ){
            /* NOV4 removed sharedPreferences
            //get editor for shared preference file
            SharedPreferences.Editor preferenceEditor = sharedPreferences.edit();
            preferenceEditor.clear();
            */
            //copy the default xml file in assets folder to internal storage
            try{
                AssetManager assetManager = context.getAssets();
                //get InputStream for default xml file
                InputStream defaultXML = assetManager.open(fileName);
                //get InputStream for xml schema
                InputStream xmlSchema = assetManager.open(schemaFileName);

                FileOutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                copyDefaultToInternalStorage(defaultXML, outputStream);

                InputStream inputStream = context.openFileInput(fileName);
                if (outputStream != null) outputStream.close();
                if (defaultXML != null) defaultXML.close();

                if (XMLValidator.againstSchema(schemaFileName, fileName, context)) {
                } else {
                    retVal = -1;
                }
                if (xmlSchema != null) xmlSchema.close();
                if (inputStream != null) inputStream.close();
            }catch(Exception e) {

                Log.e(TAG, "Error: default exercise list is missing. "
                        + e.getMessage());

                retVal =  -1;
            } finally {

            }

            /* NOV4 removed sharedPreferences
            preferenceEditor.putBoolean(loadedKey, true);
            preferenceEditor.commit();
            */
        }
        return retVal;
    }

    private void copyDefaultToInternalStorage(InputStream defaultXML, FileOutputStream outputStream) throws IOException {

        try {
            int read = 0;
            while ((read = defaultXML.read()) != -1) {
                outputStream.write(read);
            }

        } catch (IOException e) {
            Log.e(TAG, "Error creating exercise.xml "
                    + e.getMessage());
        }

    }



    protected static class XMLParse extends AsyncTask<String, Integer, ArrayList<ExerciseListItem>> {
        private final IExerciseList.Presenter presenter;
        private final IExerciseList.View view;

        public XMLParse(ExerciseList presenter, IExerciseList.View view) {
            this.presenter = presenter;
            this.view = view;
        }

        @Override
        protected ArrayList<ExerciseListItem> doInBackground(String... strings) {
            ArrayList<ExerciseListItem> exerciseList = new ArrayList<>();
            ExerciseListItem exercise = null;
            String elementText = "";
            int elementTotal = 0;

            //strings[0] - xmlSource, strings[1] - fileName
            File file = new File(strings[0], strings[1]);

            try {
                //get InputStream from file
                InputStream xmlStream = new FileInputStream(file);

                try {
                    XmlPullParser parser = Xml.newPullParser();
                    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    parser.setInput(xmlStream, null);

                    int eventType;
                    int elementCount = 0;
                    while((eventType = parser.getEventType()) != XmlPullParser.END_DOCUMENT) {
                        if (eventType == XmlPullParser.START_DOCUMENT) {
                            Log.i(TAG, "Start document");
                        } else if (eventType == XmlPullParser.END_DOCUMENT) {
                            Log.i(TAG, "End document");
                        } else if (eventType == XmlPullParser.START_TAG) {
                            if (parser.getName().equalsIgnoreCase("exercise")){
                                exercise = new ExerciseListItem();
                                exercise.setName(parser.getAttributeValue(null, "name"));
                            }
                            Log.i(TAG, "Start tag: " + parser.getName());
                        } else if (eventType == XmlPullParser.END_TAG) {
                            if (parser.getName().equalsIgnoreCase("size"))
                                elementTotal = Integer.parseInt(parser.getAttributeValue(null, "value"));
                            if (parser.getName().equalsIgnoreCase("exercise")) {
                                elementCount++;
                                SystemClock.sleep(1000);
                                publishProgress(elementCount, elementTotal);
                                exerciseList.add(exercise);
                            }
                            if (parser.getName().equalsIgnoreCase("target"))
                                exercise.setExerciseTarget(elementText);
                            if (parser.getName().equalsIgnoreCase("type"))
                                exercise.setExerciseType(elementText);
                            Log.i(TAG, "End tag: " + parser.getName());
                        } else if (eventType == XmlPullParser.TEXT) {
                            elementText = parser.getText();
                            Log.i(TAG, "Text: " + parser.getText());
                        }
                        parser.next();
                    }
                }catch (XmlPullParserException e){
                    Log.e(TAG, "doBackground: " + e.getMessage());
                }finally {
                    xmlStream.close();
                }

            }catch (IOException e){
                Log.e(TAG, "doBackground: " + e.getMessage());
            }
            return exerciseList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            view.showProgressBar(View.VISIBLE);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(ArrayList<ExerciseListItem> exerciseList) {
            if (exerciseList != null) {
                presenter.updateExerciseList(exerciseList);
                view.showProgressBar(View.INVISIBLE);
                view.displayExerciseList(exerciseList);
            }
            super.onPostExecute(exerciseList);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int progressPercent = (int) (values[0]*100/values[1]);
            view.updateProgressBar(progressPercent);
        }


    }
}