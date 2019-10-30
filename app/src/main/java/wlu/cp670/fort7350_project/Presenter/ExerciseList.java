package wlu.cp670.fort7350_project.Presenter;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.util.Xml;
import android.view.View;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import wlu.cp670.fort7350_project.Models.Exercise;
import wlu.cp670.fort7350_project.Models.ExerciseListItem;
import wlu.cp670.fort7350_project.Utils.ExerciseFilter;


//references:
//  Lesson10: Parsing XML with XMLPullParser
//      https://mylearningspace.wlu.ca/d2l/le/content/316534/viewContent/1855346/View
//  https://www.javatpoint.com/android-XMLPullParser-tutorial
//  https://medium.com/@owaistnt/getting-most-out-of-asynctask-with-mvp-bfa9cacdf600

public class ExerciseList implements IExerciseList.Presenter {

    private final IExerciseList.View view;
    private XMLParse xmlParse = null;

    private ArrayList<ExerciseListItem> exerciseList = null;

    //get reference to exercises.xml file
    private String fileName = "exercises.xml";
    private String xmlSource = "";

    private static final String TAG = "ExerciseList";

    public ExerciseList(String xmlSource, IExerciseList.View view) {
        this.xmlSource = xmlSource;
        this.view = view;
    }

    @Override
    public void openExerciseEditor(Exercise exercise) {

    }

    @Override
    public void updateExerciseList(ArrayList<ExerciseListItem> exerciseList) {

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
            int progressPercent = (int) (values[0]/values[1]*100);
            view.updateProgressBar(progressPercent);
        }


    }
}