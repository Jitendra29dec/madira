package com.madira.Adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.madira.DBHelper.DBHelper;
import com.madira.DryDay_Activity;
import com.madira.MapList_Activity;
import com.madira.Model.DashboardModel;
import com.madira.R;
import com.madira.Rate_List_activity;
import com.madira.Util.ConnectionDetector;

import java.util.List;

public class DashboardAdapter  extends RecyclerView.Adapter<DashboardAdapter.MyViewHolder> {

    List<DashboardModel> Dashboard_List;
    Context context;
    DashboardModel dashboardModel;
    ConnectionDetector cd;
    DBHelper dbHelper;

    public DashboardAdapter( Context context, List<DashboardModel> Dashboard_List){
        this.context = context;
        this.Dashboard_List = Dashboard_List;
        cd = new ConnectionDetector(context);
        dbHelper = new DBHelper(context);

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dashboard, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        dashboardModel = Dashboard_List.get(position);

        try{

            Glide.with(context).load(dashboardModel.getProfile_Img())
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.Profile_Img);

        }catch (Exception e){
            e.printStackTrace();
        }

        holder.Profile_txt.setText(dashboardModel.getProfile_text());


        holder.Main_lay.setTag(position);




        holder.Main_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cd.isConnectingToInternet()){
                    if(holder.Profile_txt.getText().toString().equalsIgnoreCase("Wine Shops")){
                        Intent i  = new Intent(context, MapList_Activity.class);
                        dbHelper.insertFielduser("type","liquor_store");
                        context.startActivity(i);
                    }

                    if(holder.Profile_txt.getText().toString().equalsIgnoreCase("Bars")){
                        Intent i  = new Intent(context, MapList_Activity.class);
                        dbHelper.insertFielduser("type","bar");
                        context.startActivity(i);
                    }
                    if(holder.Profile_txt.getText().toString().equalsIgnoreCase("Drydays List")){
                        Intent i  = new Intent(context, DryDay_Activity.class);
                        context.startActivity(i);
                    }if(holder.Profile_txt.getText().toString().equalsIgnoreCase("Rate List")){
                        Intent i  = new Intent(context, Rate_List_activity.class);
                        context.startActivity(i);
                    }if(holder.Profile_txt.getText().toString().equalsIgnoreCase("Restaurants")){
                        Intent i  = new Intent(context, MapList_Activity.class);
                        dbHelper.insertFielduser("type","restaurant");
                        context.startActivity(i);
                    }


                }else{
                    Toast.makeText(context,"No Internet Connection",Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return Dashboard_List == null ? 0 : Dashboard_List.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView Profile_Img;
        TextView Profile_txt;
        LinearLayout Main_lay;
        public MyViewHolder(View itemView) {
            super(itemView);

            Profile_Img = itemView.findViewById(R.id.profile_img);
            Profile_txt = itemView.findViewById(R.id.text_profile);
            Main_lay = itemView.findViewById(R.id.main_profile_lay);
        }
    }
}

