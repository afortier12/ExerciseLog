package wlu.cp670.fort7350_project.Views;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import wlu.cp670.fort7350_project.R;

public class SummaryCursorAdapter extends CursorAdapter {

    private final static String TAG = "SummaryCursorAdapter";
    private final static String TIMESTAMP_FORMAT_DB = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_FORMAT_DISPLAY = "MM/dd/yyyy";

    private String lastDate = "";


    public SummaryCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.summary_row, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView dateTextView = view.findViewById(R.id.separator);
        TextView targetTextView = view.findViewById(R.id.summary_target);
        TextView weightTextView = view.findViewById(R.id.summary_weight);
        TextView repsTextView = view.findViewById(R.id.summary_reps);

        String date = getDateFromString(cursor.getString(cursor.getColumnIndex(
                SummaryActivity.COLUMN_DATE)));

        if (date.equalsIgnoreCase(lastDate)) {
            dateTextView.setVisibility(View.GONE);
        } else {
            dateTextView.setText(date);
            dateTextView.setVisibility(View.VISIBLE);
            lastDate = date;
        }


        targetTextView.setText(cursor.getString(cursor.getColumnIndex(SummaryActivity.COLUMN_TARGET)));
        weightTextView.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(
                SummaryActivity.COLUMN_TOTAL_WEIGHT))));
        repsTextView.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(
                SummaryActivity.COLUMN_TOTAL_REPS))));
    }

    private String getDateFromString(String date){

        SimpleDateFormat dateFormat = new SimpleDateFormat(TIMESTAMP_FORMAT_DB,
                Locale.getDefault());
        try {
            Date formattedDate = dateFormat.parse(date);
            dateFormat.applyLocalizedPattern(DATE_FORMAT_DISPLAY);
            return dateFormat.format(formattedDate);
        }catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

}
