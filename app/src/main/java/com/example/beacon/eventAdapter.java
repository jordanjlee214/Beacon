package com.example.beacon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class eventAdapter extends RecyclerView.Adapter<eventAdapter.ViewHolder> {

    ArrayList<String> data;
    //private List<String> mData;

    public eventAdapter(ArrayList<String> data) {
        this.data = data;
    }

    @Override
    public eventAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_item, parent, false);
        //return new ViewHolder(view);
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.event_list_item, parent, false);
        return new eventAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String item = data.get(position);
        holder.mTextView.setText(item);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            mTextView = itemView.findViewById(R.id.event_view);
        }
    }

}
