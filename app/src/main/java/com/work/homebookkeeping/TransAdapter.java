package com.work.homebookkeeping;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TransAdapter extends RecyclerView.Adapter<TransAdapter.TransViewHolder> {

    class TransViewHolder extends RecyclerView.ViewHolder {

        TextView date;
        TextView sum;
        TextView sumR;
        TextView val;
        TextView valR;
        EditText comment;

        public TransViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.textViewDateRec);
            sum = itemView.findViewById(R.id.textViewSumRec);
            sumR = itemView.findViewById(R.id.textViewSumRecR);
            val = itemView.findViewById(R.id.textViewValRec);
            valR = itemView.findViewById(R.id.textViewValRecR);
            comment = itemView.findViewById(R.id.editTextCommentRec);
        }

        public void bind(Trans trans) {
            date.setText(trans.getDate());
            sum.setText(String.valueOf(trans.getSum()));
            sumR.setText(String.valueOf(trans.getSumR()));
            val.setText(trans.getVal());
            comment.setText(trans.getComment());
            if (trans.getRub()) {
                valR.setText("");
                sumR.setText("");
            }
            else
                valR.setText(R.string.roubles);
        }
    }

    private List<Trans> transes = new ArrayList<Trans>();

    public void setItems(List<Trans> tmp) {
        transes.addAll(tmp);
        notifyDataSetChanged();
    }

    public void clearItems() {
        transes.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TransViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_trans, parent, false);
        return new TransViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransViewHolder holder, int position) {
        holder.bind(transes.get(position));
    }

    @Override
    public int getItemCount() {
        return transes.size();
    }
}
