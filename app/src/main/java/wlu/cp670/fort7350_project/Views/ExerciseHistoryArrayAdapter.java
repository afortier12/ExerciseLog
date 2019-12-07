package wlu.cp670.fort7350_project.Views;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import wlu.cp670.fort7350_project.Models.Set;
import wlu.cp670.fort7350_project.R;


public class ExerciseHistoryArrayAdapter extends RecyclerView.Adapter<ExerciseHistoryArrayAdapter.ViewHolder> {

    private ArrayList<Set> itemList;
    private final static String TAG = "ExerciseHistoryAdapter";
    private final static String TIMESTAMP_FORMAT_DB = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_FORMAT_DISPLAY = "MM/dd/yyyy";
    private static final String DATE_FORMAT_DB = "yyyy-MM-dd";
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private String lastDate = "";

    public ExerciseHistoryArrayAdapter(ArrayList<Set> itemList){
        this.itemList= itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "In onCreateViewHolder");
        View view;
        if (viewType == TYPE_HEADER)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_row_header, parent, false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_row, parent, false);

        ViewHolder viewHolder = new ViewHolder(view, viewType);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseHistoryArrayAdapter.ViewHolder holder, int position) {

        if (holder.viewType == TYPE_ITEM) {
            TextView dateSeparator = holder.dateSeparator;
            TextView setNumber = holder.setNumber;
            TextView weight = holder.weight;
            TextView reps = holder.reps;

            String date = getDateFromString(itemList.get(position).getDateTime());

            if (date.equalsIgnoreCase(lastDate)) {
                dateSeparator.setVisibility(View.GONE);
            } else {
                dateSeparator.setText(date);
                dateSeparator.setVisibility(View.VISIBLE);
                lastDate = date;
            }
            setNumber.setText(String.valueOf(itemList.get(position).getOrderNumber()));
            weight.setText(String.valueOf(itemList.get(position).getWeight()));
            reps.setText(String.valueOf(itemList.get(position).getReps()));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_HEADER;
        else
            return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0: itemList.size();
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

    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView dateSeparator;
        public TextView setNumber;
        public TextView weight;
        public TextView reps;
        private int viewType;


        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;

            if (viewType == TYPE_ITEM) {
                dateSeparator = itemView.findViewById(R.id.separator);
                setNumber = itemView.findViewById(R.id.history_set_number);
                weight = itemView.findViewById(R.id.history_set_weight);
                reps = itemView.findViewById(R.id.history_set_reps);
            }

        }
    }




}

