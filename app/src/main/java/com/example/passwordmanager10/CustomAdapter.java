package com.example.passwordmanager10;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private Context context;
    private ArrayList web_id, web_name, web_username, web_password, web_image, web_date, web_favourite;

    Animation animation;

    CustomAdapter(Context context, ArrayList web_id, ArrayList web_name, ArrayList web_username,
                  ArrayList web_password, ArrayList web_image, ArrayList web_date, ArrayList web_favourite) {
        this.context = context;
        this.web_id = web_id;
        this.web_name = web_name;
        this.web_username = web_username;
        this.web_password = web_password;
        this.web_image = web_image;
        this.web_date = web_date;
        this.web_favourite = web_favourite;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.RVid.setText(String.valueOf(web_id.get(position)));
        holder.RVname.setText(String.valueOf(web_name.get(position)));
        holder.RVusername.setText(String.valueOf(web_username.get(position)));
        if(web_favourite.get(position).toString().contains("true")){
            holder.RVfavouite.setImageResource(R.drawable.ic_filled_star_small);
        }
        if(web_image.get(position) != null) {
            holder.RVimage.setImageURI(Uri.parse(web_image.get(position).toString()));
        }

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UpdateActivity.class);
                intent.putExtra("id",String.valueOf(web_id.get(position)));
                intent.putExtra("name",String.valueOf(web_name.get(position)));
                intent.putExtra("username",String.valueOf(web_username.get(position)));
                intent.putExtra("password",String.valueOf(web_password.get(position)));
                intent.putExtra("image",String.valueOf(web_image.get(position)));
                intent.putExtra("date",String.valueOf(web_date.get(position)));
                intent.putExtra("favourite",String.valueOf(web_favourite.get(position)));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return web_id.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView RVid, RVname, RVusername;
        ImageView RVimage, RVfavouite;
        LinearLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            RVid = itemView.findViewById(R.id.RVid);
            RVname = itemView.findViewById(R.id.RVname);
            RVusername = itemView.findViewById(R.id.RVusername);
            RVimage = itemView.findViewById(R.id.RVimage);
            RVfavouite = itemView.findViewById(R.id.RVfavourite);
            mainLayout = itemView.findViewById(R.id.mainLayout);

            animation = AnimationUtils.loadAnimation(context, R.anim.animation);
            mainLayout.setAnimation(animation);
        }
    }
}
