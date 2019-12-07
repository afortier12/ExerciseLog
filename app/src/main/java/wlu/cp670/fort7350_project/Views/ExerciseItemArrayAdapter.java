package wlu.cp670.fort7350_project.Views;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import wlu.cp670.fort7350_project.Models.ExerciseListItem;
import wlu.cp670.fort7350_project.R;

public class ExerciseItemArrayAdapter extends RecyclerView.Adapter<ExerciseItemArrayAdapter.ViewHolder>{

    private ArrayList<ExerciseListItem> itemList;
    private Context context;
    private OnItemClickListener listener;
    private final static String TAG = "ExerciseItem_Adapter";

    public interface OnItemClickListener {
        void onItemClick(ExerciseListItem item);
    }

    public ExerciseItemArrayAdapter(Context context, ArrayList<ExerciseListItem> itemList,
                                    OnItemClickListener listener) {
        this.context = context;
        this.itemList = itemList;
        this.listener = listener;

    }

    @NonNull
    @Override
    public ExerciseItemArrayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "In onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercise_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseItemArrayAdapter.ViewHolder holder, int position) {
        TextView item = holder.item;
        TextView type = holder.type;
        TextView target = holder.target;

        item.setText(itemList.get(position).getName());
        type.setText(itemList.get(position).getExerciseType());
        target.setText(itemList.get(position).getExerciseTarget());

        holder.bind(itemList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0: itemList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView item;
        public TextView type;
        public TextView target;


        public ViewHolder(View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.exerciseItem);
            type = itemView.findViewById(R.id.exerciseType);
            target = itemView.findViewById(R.id.exerciseTarget);

        }


        public void bind(ExerciseListItem exerciseListItem, OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(exerciseListItem);
                }
            });
        }
    }

}
