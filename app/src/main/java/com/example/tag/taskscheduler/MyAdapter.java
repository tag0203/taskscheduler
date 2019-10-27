package com.example.tag.taskscheduler;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private ArrayList<String> task_name = new ArrayList<>();
    private ArrayList<String> timeDataset = new ArrayList<>();
    private ArrayList<String> cutDataset = new ArrayList<>();
    private ArrayList<String> cutnDataset = new ArrayList<>();
    private OnRecyclerListener mListener;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        // each data item is just a string in this case
        public TextView mTextView;
        public TextView mTextTime;
        public TextView cTextView;
        public TextView cnTextView;

        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView)v.findViewById(R.id.text_view);
            mTextTime = (TextView)v.findViewById(R.id.text_time);
            cTextView = (TextView)v.findViewById(R.id.text_cut);
            cnTextView = (TextView)v.findViewById(R.id.text_cutn);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)

    public MyAdapter(ArrayList<String> myDataset, ArrayList<String> tDataset, OnRecyclerListener listener){
        task_name = myDataset;
        timeDataset = tDataset;
        mListener = listener;
    }

    public MyAdapter(ArrayList<String> myDataset, ArrayList<String> tDataset,
                     ArrayList<String> cutData, ArrayList<String> cutnData){
        task_name = myDataset;
        timeDataset = tDataset;
        cutDataset = cutData;
        cutnDataset = cutnData;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_text, parent, false);

        // set the view's size, margins, paddings and layout parameters


        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if(task_name != null && task_name.size() > position && task_name.get(position) != null){
            holder.mTextView.setText(task_name.get(position));
            holder.mTextTime.setText(timeDataset.get(position));
            holder.cTextView.setText(cutDataset.get(position));
            holder.cnTextView.setText(cutnDataset.get(position));
        }

        // クリック処理
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mListener.onRecyclerClicked(v, position);
//            }
//        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return task_name.size();
    }
}