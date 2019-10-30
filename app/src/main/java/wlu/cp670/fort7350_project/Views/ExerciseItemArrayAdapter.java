package wlu.cp670.fort7350_project.Views;

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

    public ExerciseItemArrayAdapter(int layoutID, ArrayList<ExerciseListItem> itemList) {
        this.listItemLayout = layoutID;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ExerciseItemArrayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(listItemLayout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseItemArrayAdapter.ViewHolder holder, int position) {
        TextView item = holder.item;
        item.setText(itemList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0: itemList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView item;

        public ViewHolder(View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.exerciseItem);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d("onclick", "onClick " + getLayoutPosition() + " " + item.getText());
        }
    }
}
