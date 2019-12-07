package wlu.cp670.fort7350_project.Views;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import wlu.cp670.fort7350_project.Models.ExerciseListItem;
import wlu.cp670.fort7350_project.Models.Set;
import wlu.cp670.fort7350_project.R;

public class ExerciseSetArrayAdapter extends BaseAdapter {

    private ArrayList<Set> setList;
    private FragmentActivity context;
    private final static String TAG = "ExerciseSet_Adapter";
    private View.OnClickListener listener;

    private ImageButton deleteSetButton;
    private TextView setNumber;
    private TextView setReps;
    private TextView setWeight;


    public ExerciseSetArrayAdapter(FragmentActivity context, ArrayList<Set> setList, View.OnClickListener listener) {
        this.context = context;
        this.setList = setList;
        this.listener = listener;

    }

    public void updateList(ArrayList<Set> setArrayList){
        this.setList = setArrayList;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return setList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return setList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.set_row, null);
        }

        setNumber = convertView.findViewById(R.id.set_number);
        setReps = convertView.findViewById(R.id.set_reps);
        setWeight = convertView.findViewById(R.id.set_weight);
        deleteSetButton = convertView.findViewById(R.id.set_delete);
        //editSetButton = convertView.findViewById(R.id.set_edit);

        setNumber.setText(String.valueOf(setList.get(position).getOrderNumber()));
        setReps.setText(String.valueOf(setList.get(position).getReps()));
        setWeight.setText(String.valueOf(setList.get(position).getWeight()));

        deleteSetButton.setOnClickListener(listener);

        return convertView;
    }



}
