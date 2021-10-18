package wlu.cp670.ExerciseLog.Views;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import wlu.cp670.ExerciseLog.R;

/**
 * Class SummaryCursorAdapter
 *
 * extends CursorAdapter
 *
 * @author fort7350
 * @version 1.0
 */
public class SummaryCursorAdapter extends CursorAdapter {

    private final static String TAG = "SummaryCursorAdapter";
    private final static String TIMESTAMP_FORMAT_DB = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_FORMAT_DISPLAY = "MM/dd/yyyy";

    private String lastDate = "";
    private String lastTarget = "";


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
        TextView targetTextView = view.findViewById(R.id.separator2);
        TextView exerciseTextView = view.findViewById(R.id.summary_exercise);
        TextView maxWeightTextView = view.findViewById(R.id.summary_weight_max);
        TextView minWeightTextView = view.findViewById(R.id.summary_weight_min);
        TextView maxRepsTextView = view.findViewById(R.id.summary_reps_max);
        TextView minRepsTextView = view.findViewById(R.id.summary_reps_min);

        String date = getDateFromString(cursor.getString(cursor.getColumnIndex(
                SummaryActivity.COLUMN_DATE)));

        if (date.equalsIgnoreCase(lastDate)) {
            dateTextView.setVisibility(View.GONE);
        } else {
            dateTextView.setText(date);
            dateTextView.setVisibility(View.VISIBLE);
            lastDate = date;
            lastTarget = "";
        }

        String target = cursor.getString(cursor.getColumnIndex(SummaryActivity.COLUMN_TARGET));
        if (target.equalsIgnoreCase(lastTarget)) {
            targetTextView.setVisibility(View.GONE);
        } else {
            targetTextView.setText(target);
            targetTextView.setVisibility(View.VISIBLE);
            lastTarget = target;
        }

        exerciseTextView.setText(cursor.getString(cursor.getColumnIndex(
                SummaryActivity.COLUMN_EXERCISE)));
        maxWeightTextView.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(
                SummaryActivity.COLUMN_MAX_WEIGHT))));
        minWeightTextView.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(
                SummaryActivity.COLUMN_MIN_WEIGHT))));
        maxRepsTextView.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(
                SummaryActivity.COLUMN_MAX_REPS))));
        minRepsTextView.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(
                SummaryActivity.COLUMN_MIN_REPS))));

    }

    /**
     * Changes the date in a String from the
     * timestamp format - yyyy-MM-dd HH:mm:ss
     * to the display format - MM/dd/yyyy
     *
     * @param date
     * @return
     */
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
