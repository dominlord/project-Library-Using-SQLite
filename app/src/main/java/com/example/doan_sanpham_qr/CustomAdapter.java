package com.example.doan_sanpham_qr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    Context context;
    Activity activity;
    ArrayList<String> project_id, project_title, project_author, project_activity, project_progress , project_place;

    public CustomAdapter(Activity activity,
                         Context context,
                         ArrayList<String> project_id,
                         ArrayList<String> project_title,
                         ArrayList<String> project_author,
                         ArrayList<String> project_activity,
                         ArrayList<String> project_place,
                         ArrayList<String> project_progress) {
        this.context = context;
        this.project_id = project_id;
        this.project_title = project_title;
        this.project_author = project_author;
        this.project_activity = project_activity;
        this.project_progress = project_progress;
        this.project_place = project_place;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.projectIdText.setText(String.valueOf(project_id.get(position)));
        holder.projectTitleText.setText(String.valueOf(project_title.get(position)));
        holder.projectAuthorText.setText(String.valueOf(project_author.get(position)));
        holder.projectActivityText.setText(String.valueOf(project_activity.get(position)));
        holder.projectProgressText.setText(String.valueOf(project_progress.get(position)));
        holder.projectPlaceText.setText(String.valueOf(project_place.get(position)));


        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent11 = new Intent(context, UpdateActivity.class);
                intent11.putExtra("id", String.valueOf(project_id.get(position)));
                intent11.putExtra("title", String.valueOf(project_title.get(position)));
                intent11.putExtra("author", String.valueOf(project_author.get(position)));
                intent11.putExtra("activity", String.valueOf(project_activity.get(position)));
                intent11.putExtra("progress", String.valueOf(project_progress.get(position)));
                intent11.putExtra("place", String.valueOf(project_place.get(position)));
                activity.startActivityForResult(intent11, 1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return project_id.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView projectIdText, projectTitleText, projectAuthorText, projectActivityText, projectProgressText, projectPlaceText;
        LinearLayout mainLayout ;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            projectIdText = itemView.findViewById(R.id.project_id_txt);
            projectTitleText = itemView.findViewById(R.id.project_title_txt);
            projectAuthorText = itemView.findViewById(R.id.project_author_txt);
            projectActivityText = itemView.findViewById(R.id.project_activity_txt);
            projectProgressText = itemView.findViewById(R.id.project_progress_txt);
            projectPlaceText = itemView.findViewById(R.id.project_place_txt);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }
}
