package wlu.cp670.ExerciseLog.Views;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.OpenableColumns;
import android.text.Html;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.google.android.material.snackbar.Snackbar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import wlu.cp670.ExerciseLog.BuildConfig;
import wlu.cp670.ExerciseLog.R;
import wlu.cp670.ExerciseLog.Utils.ExerciseTarget;
import wlu.cp670.ExerciseLog.Utils.ExerciseType;

import static android.os.Environment.getExternalStorageDirectory;


/*
https://developer.android.com/guide/topics/providers/document-provider.html
 */

/**
 * Class ImportExportActivity
 *
 * layout: activity_import_export.xml
 *
 * @author fort7350
 * @version 1.0
 */
public class ImportExportActivity extends AppCompatActivity  {

    public static final String TAG = "SummaryActivity";
    private static final String fileName = "exercises.xml";
    public static final int REQUEST_CODE_IMPORT = 1;
    public static final int REQUEST_CODE_EXPORT = 2;
    public static final String RESULT_CODE_IMPORT = "IMPORT_RESULT";

    Toolbar appToolbar;
    InputStream importedStream;
    ProgressDialog progressDialog;

    private boolean showFilter = false;
    private boolean showHistory = false;
    private boolean showList = true;
    private boolean importSuccessful = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_export);

        appToolbar = findViewById(R.id.app_toolbar);
        setSupportActionBar(appToolbar);
        invalidateOptionsMenu();

        progressDialog = new ProgressDialog(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.action_list) {
            Log.d(TAG, "list selected");

            Intent intent = new Intent(this, ExerciseActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra(RESULT_CODE_IMPORT, importSuccessful);
            startActivity(intent);


        } else if (id == R.id.action_tools) {

        } else if (id == R.id.action_history){

        }else if (id == R.id.action_about){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.about_message_title));
            builder.setMessage(Html.fromHtml(getResources().getString(R.string.about_message)));
            builder.setPositiveButton("Yes",null);

            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra(RESULT_CODE_IMPORT, importSuccessful);
        setResult(RESULT_OK, intent);

        super.finish();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        menu.findItem(R.id.action_history).setVisible(showHistory);
        menu.findItem(R.id.action_filter).setVisible(showFilter);
        menu.findItem(R.id.action_list).setVisible(showList);
        return super.onPrepareOptionsMenu(menu);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_IMPORT:
                if (resultCode == RESULT_OK  && data != null) {
                    Uri uri = data.getData();
                    try {
                        String[] pathSplit = uri.getPath().split(":");
                        String path = pathSplit[1];

                        String fileName = getFileName(uri);
                        File importedFile = new File(path);

                        importedStream = new FileInputStream(importedFile);

                        XMLValidate xmlValidate = new XMLValidate(this);
                        xmlValidate.execute(path);

                    } catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                }
                break;
            case REQUEST_CODE_EXPORT:
                if (resultCode == RESULT_OK)
                    Snackbar.make(findViewById(R.id.exportButtonLabel), R.string.export_ok,
                                    Snackbar.LENGTH_LONG).show();
                else
                    Snackbar.make(findViewById(R.id.exportButtonLabel), R.string.export_nok,
                                    Snackbar.LENGTH_LONG).show();
            default:
                break;
        }
    }


    public void onExport(View view) {

        File file = new File(this.getFilesDir(), fileName);
        Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, file);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("type/xml");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (intent.resolveActivity(this.getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_EXPORT);
        }
    }

    /**
     * Sends and ACTION_GET_CONTENT request to the installed
     * file manager
     *
     * @param view
     */
    public void onImport(View view) {

        Uri uri = Uri.parse(getExternalStorageDirectory() + "/Download/");
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setDataAndType(uri, "*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (intent.resolveActivity(this.getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_IMPORT);
        }
    }

    /**
     * Retreives the file name selected from the ACTION_GET_CONTENT
     * Intent result data
     *
     * @param uri
     * @return
     */
    public String getFileName(Uri uri) {
        String result = "";
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        return result;
    }


    /**
     *
     * Called by XMLValidate class
     *
     * Shows the layout from the ProgressDialog class
     *
     */
    public void showProgressBar(int value){
        progressDialog.show();
    }

    /**
     * Called by the XMLValidate class
     *
     * Updates the layout from the ProgressDialog class
     *
     * @param count
     * @param total
     */
    public void updateProgressBar(int count, int total){
        progressDialog.updateCounts(count, total);
    }

    /**
     * Called by the XMLValidate class onPostExecute method
     *
     * @param result
     */
    public void displayResult(boolean result){
        progressDialog.dismiss();
        if (result) {
            importSuccessful = copyImportedFile();
            if (importSuccessful)
                Snackbar.make(findViewById(R.id.importButtonLabel), R.string.import_ok,
                        Snackbar.LENGTH_LONG).show();
            else
                Snackbar.make(findViewById(R.id.importButtonLabel), R.string.import_nok,
                        Snackbar.LENGTH_LONG).show();
        }
    }

    private boolean copyImportedFile(){

        try {
            FileOutputStream outputStream = this.openFileOutput(fileName,
                    Context.MODE_PRIVATE);
            int read = 0;
            while ((read = importedStream.read()) != -1) {
                outputStream.write(read);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * <h1>XMLValidate</h1>
     * Extends AsyncTask
     * Checks that the file selected to be imported matches the expected
     * xml file structure
     *
     * @author Adam Fortier
     * @version 1.0
     * @since 2019-12-10
     */
    protected static class XMLValidate extends AsyncTask<String, Integer, Boolean> {

        private ImportExportActivity view;

        public XMLValidate(ImportExportActivity view) {
            this.view = view;
        }


        @Override
        protected Boolean doInBackground(String... strings) {
            String elementText = "";
            int elementTotal = 0;

            boolean xmlValidationResult = true;

            boolean foundRoot = false;
            boolean foundSize = false;
            boolean foundSizeValue = false;
            boolean foundTag = false;
            boolean foundTagName = false;
            boolean foundTarget = false;
            boolean foundType = false;

            try {
                //strings[0] - imported xml file with path
                File file = new File(strings[0]);

                //get InputStream from file
                InputStream xmlStream = new FileInputStream(file);

                try {
                    XmlPullParser parser = Xml.newPullParser();
                    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    parser.setInput(xmlStream, null);

                    int eventType;
                    int elementCount = 0;
                    while ((eventType = parser.getEventType()) != XmlPullParser.END_DOCUMENT) {
                        if (eventType == XmlPullParser.START_TAG) {
                            if (parser.getName().equalsIgnoreCase("size")) {
                                foundSize = true;
                                if (parser.getAttributeValue(null,
                                        "value").length() > 0)
                                    foundSizeValue = true;
                            } else if (parser.getName().equalsIgnoreCase("exercise")) {
                                foundTag = true;
                                if (parser.getAttributeValue(null,
                                        "name").length() > 0)
                                    foundTagName = true;
                            } else if (parser.getName().equalsIgnoreCase("exercises")){
                                foundRoot = true;
                            } else if (parser.getName().equalsIgnoreCase("target") &&
                                    parser.getAttributeCount() == 0) {
                                foundTarget = true;
                            } else if (parser.getName().equalsIgnoreCase("type") &&
                                    parser.getAttributeCount() == 0) {
                                foundType = true;
                            }
                        } else if (eventType == XmlPullParser.END_TAG) {
                            if (parser.getName().equalsIgnoreCase("size"))
                                if (!foundSizeValue || !foundSize) {
                                    xmlValidationResult = false;
                                    break;
                                } else {
                                    try {
                                        elementTotal = Integer.parseInt(
                                                parser.getAttributeValue(null, "value"));
                                    } catch (NumberFormatException nfe){
                                        xmlValidationResult = false;
                                        break;
                                    }
                                }
                            else if (parser.getName().equalsIgnoreCase("exercise")) {
                                elementCount++;
                                SystemClock.sleep(100);
                                publishProgress(elementCount, elementTotal);
                                if (!foundTag || !foundTagName || !foundTarget || !foundType) {
                                    xmlValidationResult = false;
                                    break;
                                } else {
                                    foundTag = foundTagName = foundTarget = foundType = false;
                                }
                            } else if (parser.getName().equalsIgnoreCase("target")){
                                if (!checkTarget(elementText)) {
                                    xmlValidationResult = false;
                                    break;
                                }
                            } else if (parser.getName().equalsIgnoreCase("type")){
                            if (!checkType(elementText)) {
                                xmlValidationResult = false;
                                break;
                            }
                        }
                        } else if (eventType == XmlPullParser.TEXT) {
                            elementText = parser.getText();
                        }
                        parser.next();
                    }
                } catch (XmlPullParserException e) {
                    Log.e(TAG, "doBackground: " + e.getMessage());
                } finally {
                    xmlStream.close();
                }

            } catch (IOException e) {
                Log.e(TAG, "doBackground: " + e.getMessage());
            }
            return xmlValidationResult;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            view.showProgressBar(View.VISIBLE);
        }


        @Override
        protected void onPostExecute(Boolean result) {
            view.displayResult(result);
            super.onPostExecute(result);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            view.updateProgressBar(values[0], values[1]);
        }

        private boolean checkTarget(String targetName){

            for (ExerciseTarget target: ExerciseTarget.values()){
                if (target.toString().equalsIgnoreCase(targetName))
                    return true;
            }
            return false;
        }

        private boolean checkType(String typeName){

            for (ExerciseType type: ExerciseType.values()){
                if (type.toString().equalsIgnoreCase(typeName))
                    return true;
            }
            return false;
        }

    }

    class ProgressDialog extends Dialog {

        TextView count;
        TextView total;

        public ProgressDialog(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.progress_cyclic_dialog_import);
            setCancelable(false);

            count = findViewById(R.id.progress_count);
            count.setText("0");
            total = findViewById(R.id.progress_total);
            total.setText("0");

        }

        public void updateCounts(int countValue, int totalValue){
            count.setText(String.valueOf(countValue));
            total.setText(String.valueOf(totalValue));
        }
    }
}
