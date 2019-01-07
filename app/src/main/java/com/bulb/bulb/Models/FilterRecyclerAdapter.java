package com.bulb.bulb.Models;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bulb.bulb.ForumActivity;
import com.bulb.bulb.MainActivity;
import com.bulb.bulb.NewPostActivity;
import com.bulb.bulb.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class FilterRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<String> filters;
    private Context mContext;
    private ForumRecyclerAdapter forumRecyclerAdapter;
    private int selectedPos;
    public FilterRecyclerAdapter(Context context, ArrayList<String> filters) {
        mContext = context;
        this.filters = filters;
        this.forumRecyclerAdapter = forumRecyclerAdapter;
        selectedPos = 0;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.filter_cell, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int i) {
        Log.d("Bulb", "onBindViewHolder: called.");
        ((ViewHolder) viewHolder).filterTextView.setText(filters.get(i));
        if (selectedPos == i) {
            ((ViewHolder) viewHolder).filterTextView.setTextColor(Color.parseColor("#F5A67E"));
        } else {
            ((ViewHolder) viewHolder).filterTextView.setTextColor(Color.parseColor("#000000"));
        }
        ((ViewHolder) viewHolder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> tags = new ArrayList<>();
                tags.add(((ViewHolder) viewHolder).filterTextView.getText().toString());
                ((MainActivity) mContext).filterPosts(tags);
                Log.d("Bulb", "Filtering:" + tags.toString());
                notifyItemChanged(selectedPos);
                selectedPos = i;
                notifyItemChanged(selectedPos);
            }
        });
    }


    @Override
    public int getItemCount() {
        return filters.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView filterTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            filterTextView = itemView.findViewById(R.id.textView);
        }
    }
}

