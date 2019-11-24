package wlu.cp670.fort7350_project.Views;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import wlu.cp670.fort7350_project.Models.ExerciseListItem;
import wlu.cp670.fort7350_project.R;

public class ExerciseItemArrayAdapter extends RecyclerView.Adapter<ExerciseItemArrayAdapter.ViewHolder>{

    private int listItemLayout;
    private ArrayList<ExerciseListItem> itemList;
    private Context context;
    private ExerciseListFragment.OnExerciseItemSelectedListener listener;
    private final static String TAG = "ExerciseItem_Adapter";


    public ExerciseItemArrayAdapter(Context context, ArrayList<ExerciseListItem> itemList, ExerciseListFragment.OnExerciseItemSelectedListener listener) {
        this.context = context;
        this.itemList = itemList;
        this.listener = listener;

    }

    @NonNull
    @Override
    public ExerciseItemArrayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "In onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercise_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, itemList, listener);

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
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0: itemList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView item;
        public TextView type;
        public TextView target;
        private Context context;
        private ExerciseListFragment.OnExerciseItemSelectedListener listener;
        private ArrayList<ExerciseListItem> itemList;

        public ViewHolder(View itemView, ArrayList<ExerciseListItem> itemList, ExerciseListFragment.OnExerciseItemSelectedListener listener) {
            super(itemView);
            item = itemView.findViewById(R.id.exerciseItem);
            type = itemView.findViewById(R.id.exerciseType);
            target = itemView.findViewById(R.id.exerciseTarget);

            context = itemView.getContext();
            this.itemList = itemList;
            this.listener = listener;

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            Log.d("onclick", "onClick " + getLayoutPosition() + " " + item.getText());
            int itemPosition = getLayoutPosition();
            listener.onExerciseItemSelected(itemPosition, itemList);
        }
    }

}
