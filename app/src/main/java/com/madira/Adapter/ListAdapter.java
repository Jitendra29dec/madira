package com.madira.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.madira.DBHelper.DBHelper;
import com.madira.Model.ListModel;
import com.madira.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.List;
import java.util.StringTokenizer;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder> {
    List<ListModel> ThekaList;
    Context context;
    ListModel listModel;
    DBHelper dbHelper;
    private static final int LIST_AD_DELTA = 5;
    private static final int CONTENT = 0;
    private static final int AD = 1;


    public ListAdapter( Context context, List<ListModel> ThekaList){
        this.context = context;
        this.ThekaList = ThekaList;
        dbHelper = new DBHelper(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == CONTENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent,false);
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_admob, parent, false);
        }

        return new MyViewHolder(view);
    }


    @Override
    public int getItemViewType(int position) {
     /*   if (position > 0 && position % LIST_AD_DELTA == 0) {
            return AD;
        }*/
        return CONTENT;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if (getItemViewType(position) == CONTENT) {
            listModel = ThekaList.get(getRealPosition(position));

            holder.Name.setText(listModel.getName());
            if(listModel.getDistance().startsWith("0")){
                StringTokenizer tokens1 = new StringTokenizer(listModel.getDistance(), " ");
                String first1 = tokens1.nextToken();
                Double Val = Double.parseDouble(first1)* 1000;
                int DistanceByMeter = (int) Math.round(Val);

                holder.Distance.setText(String.valueOf(DistanceByMeter)+" M");
            }else{
                holder.Distance.setText(listModel.getDistance()+" KM");
            }

    /*  if(Double.parseDouble(listModel.getDistance())>=1000){
          String Val = String.valueOf(Double.parseDouble(listModel.getDistance())/1000);
          holder.Distance.setText("Distance:- "+Val+" KM");
      }else{
          holder.Distance.setText("Distance:- "+listModel.getDistance()+" M");
      }*/


            holder.Address.setText(listModel.getAddress());

            holder.GetDirection.setTag(getRealPosition(position));

            holder.GetDirection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try{
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr="+dbHelper.getFieldValueUser("lat")+","+dbHelper.getFieldValueUser("lng")+"&daddr="+
                                ThekaList.get(Integer.parseInt(v.getTag().toString())).getLat()+","+
                                ThekaList.get(Integer.parseInt(v.getTag().toString())).getLng()));
                        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                        context.startActivity(intent);
                    }catch (Exception e){

                    }

                }
            });
        } else {
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
    public int getItemCount() {
        int additionalContent = 0;
        if (ThekaList.size() > 0 && LIST_AD_DELTA > 0 && ThekaList.size() > LIST_AD_DELTA) {
            additionalContent = ThekaList.size() / LIST_AD_DELTA;
        }
        return ThekaList.size() + additionalContent ;
        //return ThekaList == null ? 0 : ThekaList.size();
    }

    private int getRealPosition(int position) {
        if (LIST_AD_DELTA == 0) {
            return position;
        } else {
            return position - position / LIST_AD_DELTA;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView Name, Distance,Address;
        Button GetDirection;
        AdView adView;
        public MyViewHolder(View itemView) {
            super(itemView);

            Name = itemView.findViewById(R.id.name);
            Distance = itemView.findViewById(R.id.distance);
            Address = itemView.findViewById(R.id.address);
            GetDirection = itemView.findViewById(R.id.get_direction);
            adView = itemView.findViewById(R.id.adView_recycler);
        }
    }
}
