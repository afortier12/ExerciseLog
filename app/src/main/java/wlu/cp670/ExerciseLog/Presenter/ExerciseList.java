package wlu.cp670.ExerciseLog.Presenter;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.util.Xml;

import androidx.annotation.RequiresApi;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

import wlu.cp670.ExerciseLog.Models.ExerciseListItem;
import wlu.cp670.ExerciseLog.Utils.ExerciseFilter;


/**
 * <h1>ExerciseList</h1>
 * ExerciseList implements IExerciseList.Presenter
 * linking IExerciseList.View with the data contained in the
 * <i>exercise.xml</i> file
 * references:
 *       https://mylearningspace.wlu.ca/d2l/le/content/316534/viewContent/1855346/View
 *       https://www.javatpoint.com/android-XMLPullParser-tutorial
 *       https://medium.com/@owaistnt/getting-most-out-of-asynctask-with-mvp-bfa9cacdf600
 *
 * @author Adam Fortier
 * @version 1.0
 * @since 2019-11-12
 */
public class ExerciseList implements IExerciseList.Presenter {

    private final IExerciseList.View view;
    private XMLParse xmlParse = null;
    private boolean isRunning = false;

    private ArrayList<ExerciseListItem> exerciseList = null;
    private ArrayList<ExerciseListItem> filteredList = null;

    //asset file names
    private static final String fileName = "exercises.xml";

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

    protected String getXmlSource() {
        return xmlSource;
    }

    /**
     * Constructor
     *
     * @param xmlSource
     * @param view
     */
    public ExerciseList(String xmlSource, IExerciseList.View view) {
        this.xmlSource = xmlSource;
        this.view = view;
    }

    /**
     * Implements IExerciseList.Presenter method
     *
     * Called by ExerciseActivity onCreate(Bundle) and onRestoreInstanceState(Bundle)
     *
     * Updates the list in the ExerciseListFragment
     *
     * @param exerciseList
     */
    @Override
    public void updateExerciseList(ArrayList<ExerciseListItem> exerciseList) {
        this.exerciseList = exerciseList;
    }

    /**
     * Implements IExerciseList.Presenter method
     *
     * called from onDestroy() of ExerciseList activity
     */
    @Override
    //
    public void stop(){
        if(xmlParse != null)
            if (xmlParse.getStatus() == AsyncTask.Status.RUNNING)
                xmlParse.cancel(true);

    }

    /**
     * Implements IExerciseListPresenter method
     *
     * Called by ExerciseActivity onResume()
     *
     * Starts AsyncTask to read from exercise.xml file
     */
    @Override
    public void readExerciseListFromFile(){
        xmlParse = new XMLParse(this, view, null);
        xmlParse.execute(xmlSource, fileName);
    }

    /**
     * Implements IExerciseListPresenter method
     *
     *  Called by ExerciseActivity onFilterSelected(Map<ExerciseFilter, String>)
     *
     *  Filters the exercise list by type and/or target
     *
     * @param filter
     */
    @Override
    public void filterExerciseList(Map<ExerciseFilter, String> filter) {

        ArrayList<ExerciseListItem> tempList = new ArrayList<>(exerciseList);

        for (Map.Entry<ExerciseFilter, String> entry : filter.entrySet()) {
            filteredList = new ArrayList<>();
            switch (entry.getKey()) {
                case TARGET:
                    for (ExerciseListItem item : tempList) {
                        if (item.getExerciseTarget().equalsIgnoreCase(entry.getValue()))
                            filteredList.add(new ExerciseListItem(item));
                    }
                    break;
                case TYPE:
                    for (ExerciseListItem item : tempList) {
                        if (item.getExerciseType().equalsIgnoreCase(entry.getValue()))
                            filteredList.add(new ExerciseListItem(item));
                    }
                    break;

                default:
                    break;

            }
            tempList = new ArrayList<>();
            for (ExerciseListItem item: filteredList){
                tempList.add(new ExerciseListItem(item));
            }
        }

        view.updateExerciseList(filteredList);
    }

    /**
     * Implements IExerciseList.Presenter method
     *
     * Called by Exercise Activity onClearFilter()
     *
     */
    @Override
    public void clearExerciseListFilter() {

        filteredList = new ArrayList<>(exerciseList);
        view.updateExerciseList(filteredList);

    }


    /**
     * Implements IExerciseList.Presenter method
     *
     * Called by ExerciseActivity onResume()
     * Loads the default exercises.xml file from the assets directory
     *
     * @param context
     * @return
     */
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

        //default has not been loaded or file does not exist
        if (!existsFlag ){
            //copy the default xml file in assets folder to internal storage
            try{
                AssetManager assetManager = context.getAssets();
                //get InputStream for default xml file
                InputStream defaultXML = assetManager.open(fileName);

                FileOutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                copyDefaultToInternalStorage(defaultXML, outputStream);

                if (outputStream != null) outputStream.close();
                if (defaultXML != null) defaultXML.close();

            }catch(Exception e) {

                Log.e(TAG, "Error: default exercise list is missing. "
                        + e.getMessage());

                retVal =  -1;
            } finally {

            }

        }
        return retVal;
    }

    /**
     * Copies the file from the inputStream to the outputStream
     * @param defaultXML
     * @param outputStream
     */
    private void copyDefaultToInternalStorage(InputStream defaultXML, FileOutputStream outputStream) {

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

    protected XMLParse getXmlParse() {
        return xmlParse;
    }

    protected AsyncTask.Status getAsyncTaskState(){
        return xmlParse.getStatus();
    }

    /**
     * @return ArrayList<ExerciseListItem>
     */
    public ArrayList<ExerciseListItem> getExerciseList(){
        return exerciseList;
    }

    public ArrayList<ExerciseListItem> getFilteredList() {
        return filteredList;
    }

    /**
     * Sets the exerciseList
     * @param exerciseList
     */
    public void setExerciseList(ArrayList<ExerciseListItem> exerciseList) {
        this.exerciseList = exerciseList;
    }

    /**
     * <h1>XMLParse</h1>
     * Extends AsyncTask
     * Uses XmlPullParser to populate ArrayList<ExerciseListItem>
     * with values from the xml file
     *
     * @author Adam Fortier
     * @version 1.0
     * @since 2019-12-10
     */
    protected static class XMLParse extends AsyncTask<String, Integer, ArrayList<ExerciseListItem>> {
        private final IExerciseList.Presenter presenter;
        private final IExerciseList.View view;
        private Listener listener;

        public XMLParse(ExerciseList presenter, IExerciseList.View view, Listener listener) {
            this.presenter = presenter;
            this.view = view;
            this.listener = listener;
        }

        public interface Listener {
            void onComplete(ArrayList<ExerciseListItem> result);
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
                                exercise = new ExerciseListItem(parser.getAttributeValue
                                        (null, "name"), "", "");
                            }
                            Log.i(TAG, "Start tag: " + parser.getName());
                        } else if (eventType == XmlPullParser.END_TAG) {
                            if (parser.getName().equalsIgnoreCase("size"))
                                elementTotal = Integer.parseInt(parser.getAttributeValue(null, "value"));
                            if (parser.getName().equalsIgnoreCase("exercise")) {
                                elementCount++;
                                SystemClock.sleep(100);
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
            view.showProgressBar();
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(ArrayList<ExerciseListItem> exerciseList) {
            if (exerciseList != null) {
                presenter.updateExerciseList(exerciseList);
                view.displayExerciseList(exerciseList);
            }
            if (listener != null) listener.onComplete(exerciseList);
            super.onPostExecute(exerciseList);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            view.updateProgressBar(values[0], values[1]);
        }



    }
}