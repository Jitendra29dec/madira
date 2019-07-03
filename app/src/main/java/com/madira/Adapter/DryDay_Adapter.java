package com.madira.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.madira.Model.DryDay_Model;
import com.madira.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.List;

public class DryDay_Adapter  extends RecyclerView.Adapter<DryDay_Adapter.MyViewHolder> {
    List<DryDay_Model> DryDay_List;
    Context context;
    DryDay_Model dryDay_model;
    private static final int LIST_AD_DELTA = 5;
    private static final int CONTENT = 0;
    private static final int AD = 1;

    public DryDay_Adapter( Context context, List<DryDay_Model> DryDay_List){
        this.context = context;
        this.DryDay_List = DryDay_List;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == CONTENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_drydays, parent,false);
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_admob, parent, false);
        }
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (getItemViewType(position) == CONTENT) {
            dryDay_model = DryDay_List.get(position);

            holder.Name.setText(dryDay_model.getDay_name());
            holder.Date.setText(dryDay_model.getDate());
        }else {
            AdRequest adRequest = new AdRequest.Builder()
                    //.addTestDevice("2695850186D664EAA4DFBCC30CB38F8C")
                    .build();
            // mAdView.loadAd(adRequest);


            holder.adView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    // Code to be executed when an ad request fails.
                }

                @Override
                public void onAdOpened() {
                    // Code to be executed when an ad opens an overlay that
                    // covers the screen.
                }

                @Override
                public void onAdLeftApplication() {
                    // Code to be executed when the user has left the app.
                }

                @Override
                public void onAdClosed() {
                    // Code to be executed when when the user is about to return
                    // to the app after tapping on an ad.
                }
            });

            holder.adView.loadAd(adRequest);
        }


        }

    @Override
    public int getItemViewType(int position) {
  /*      if (position > 0 && position % LIST_AD_DELTA == 0) {
            return AD;
        }*/
        return CONTENT;

    }

    @Override
    public int getItemCount() {
        return DryDay_List == null ? 0 : DryDay_List.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView Name,Date;
        AdView adView;
        public MyViewHolder(View itemView) {
            super(itemView);

            Name = itemView.findViewById(R.id.day_name);
            Date = itemView.findViewById(R.id.day_date);
            adView = itemView.findViewById(R.id.adView_recycler);

        }
    }
}

