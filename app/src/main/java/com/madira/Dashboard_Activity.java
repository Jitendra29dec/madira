package com.madira;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.madira.Common.GPSTracker;
import com.madira.Common.MonitorService;
import com.madira.Common.ServiceHandler;
import com.madira.DBHelper.DBHelper;
import com.madira.Model.DashboardModel;
import com.madira.Util.ConnectionDetector;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Random;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class Dashboard_Activity extends AppCompatActivity {

    // List<DashboardModel> Dashboard_List;
    // DashboardAdapter dashboardAdapter;
    // RecyclerView Dash_Recycler;
    // GridLayoutManager layoutManager;
    ImageView Notifi, Enable_Disable;
    Dialog mBottomSheetDialog;
    DBHelper dbHelper;
    InterstitialAd mInterstitialAd;
    RelativeLayout WineShops, Bars, Restaurants, RateList, DryDays;
    String IsShowRate="0";
    ConnectionDetector cd;
    AdView mAdView;
    private static final Random rand = new Random();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        dbHelper= new DBHelper(Dashboard_Activity.this);
        cd = new ConnectionDetector(Dashboard_Activity.this);
        //Dash_Recycler= findViewById(R.id.dash_recycler);
        Notifi = findViewById(R.id.notification);
        WineShops = findViewById(R.id.winelocator_lay);
        Bars = findViewById(R.id.barlocator_lay);
        Restaurants = findViewById(R.id.resturants_lay);
        RateList = findViewById(R.id.rate_list_lay);
        DryDays = findViewById(R.id.dryday_lay);
        Enable_Disable = findViewById(R.id.enable_disable_img);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_full_screen));

        if(dbHelper.getFieldValueUser("enable_disable")!=null){
            if(dbHelper.getFieldValueUser("enable_disable").equalsIgnoreCase("enable")){
                Enable_Disable.setImageResource(R.drawable.success);
             if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                 Log.e( "onCreate: ","Ente23r" );
                startForegroundService(new Intent(this, GPSTracker.class));
                startForegroundService(new Intent(this, MonitorService.class));
             }else{
                startService(new Intent(getApplicationContext(), GPSTracker.class));
                startService(new Intent(getApplicationContext(), MonitorService.class));
            }}else{
                Enable_Disable.setImageResource(R.drawable.fail);
                stopService(new Intent(getApplicationContext(), GPSTracker.class));
                stopService(new Intent(getApplicationContext(), MonitorService.class));

            }
        }


        Notifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Enable_Disable_Notification();
            }
        });


        if(cd.isConnectingToInternet()){
            new CheckvMenu().execute();
        }



        mAdView = (AdView) findViewById(R.id.adView);
        // mAdView.setAdSize(AdSize.BANNER);
//        mAdView.setAdUnitId(getString(R.string.banner_home_footer));
        //mAdView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");

        AdRequest adRequest1 = new AdRequest.Builder()
                //.addTestDevice("2695850186D664EAA4DFBCC30CB38F8C")
                .build();
        // mAdView.loadAd(adRequest);


        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.e( "onAdLoaded: ", "loaded");
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Log.e("onAdFailedToLoad: ","fai" );            }

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

        mAdView.loadAd(adRequest1);

        // set the ad unit ID
        //mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));
        int randInt = rand.nextInt(100)+1;
        if(randInt % 2 == 0){
            AdRequest adRequest = new AdRequest.Builder()
                    //.addTestDevice("2695850186D664EAA4DFBCC30CB38F8C")
                    .build();

            // Load ads into Interstitial Ads
            mInterstitialAd.loadAd(adRequest);

            mInterstitialAd.setAdListener(new AdListener() {
                public void onAdLoaded() {
                    showInterstitial();
                }
            });
        }




        WineShops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i  = new Intent(Dashboard_Activity.this, MapList_Activity.class);
                dbHelper.insertFielduser("type","liquor_store");
                startActivity(i);
            }
        });


        Bars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i  = new Intent(Dashboard_Activity.this, MapList_Activity.class);
                dbHelper.insertFielduser("type","bar");
                startActivity(i);
            }
        });

        Restaurants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i  = new Intent(Dashboard_Activity.this, MapList_Activity.class);
                dbHelper.insertFielduser("type","restaurant");
                startActivity(i);
            }
        });


        RateList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i  = new Intent(Dashboard_Activity.this, Rate_List_activity.class);
                startActivity(i);
            }
        });


        DryDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i  = new Intent(Dashboard_Activity.this, DryDay_Activity.class);
                startActivity(i);
            }
        });


        // single example
        new MaterialShowcaseView.Builder(this)
                .setTarget(Notifi)
                .setDismissText("GOT IT")
                .setContentText("You can Enable/Disable the Notification from here.")
                .setDelay(500) // optional but starting animations immediately in onCreate can make them choppy
                .singleUse("1") // provide a unique ID used to ensure it is only shown once
                .show();


        //layoutManager = new GridLayoutManager(this, 3);

        /*layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position < 2)
                return 2;
                else
                return 3;
            }
        });*/

       /* layoutManager = new GridLayoutManager(this, 6);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL); // set Horizontal Orientation
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                // 5 is the sum of items in one repeated section
                switch (position % 5) {
                    // first two items span 3 columns each
                    case 0:
                    case 1:
                        return 3;
                    // next 3 items span 2 columns each
                    case 2:
                    case 3:
                    case 4:
                        return 2;
                }
                throw new IllegalStateException("internal error");
            }
        });

        GetDashList();
*/    }


    public void GetDashList(){
        //Dashboard_List= new ArrayList<DashboardModel>();
        for(int i = 0; i<5; i++){
            DashboardModel list = new DashboardModel();
            if(i==0){
                list.setProfile_Img("https://www.motherearthbrewco.com/wp-content/uploads/2017/03/4S_Spring_2018_BottleGlass_800x1040-788x1024.png");
                list.setProfile_text("Wine Shops");
            }

            if(i==1){
                list.setProfile_Img("https://www.motherearthbrewco.com/wp-content/uploads/2017/03/4S_Spring_2018_BottleGlass_800x1040-788x1024.png");
                list.setProfile_text("Bars");
            }

            if(i==2){
                list.setProfile_Img("https://www.motherearthbrewco.com/wp-content/uploads/2017/03/4S_Spring_2018_BottleGlass_800x1040-788x1024.png");
                list.setProfile_text("Drydays List");
            }

            if(i==3){
                list.setProfile_Img("https://www.motherearthbrewco.com/wp-content/uploads/2017/03/4S_Spring_2018_BottleGlass_800x1040-788x1024.png");
                list.setProfile_text("Rate List");
            }
            if(i==4){
                list.setProfile_Img("https://www.motherearthbrewco.com/wp-content/uploads/2017/03/4S_Spring_2018_BottleGlass_800x1040-788x1024.png");
                list.setProfile_text("Restaurants");
            }

            //Dashboard_List.add(list);
        }

       /* Dash_Recycler.setNestedScrollingEnabled(false);
        Dash_Recycler.setHasFixedSize(false);
        //GridLayoutManager gridLayoutManager = new GridLayoutManager(Dashboard_Activity.this,2);
        //gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL); // set Horizontal Orientation
        Dash_Recycler.setLayoutManager(layoutManager); // set LayoutManager to RecyclerView
        dashboardAdapter = new DashboardAdapter(Dashboard_Activity.this,Dashboard_List);
        Dash_Recycler.setAdapter(dashboardAdapter);*/

    }


    public void Enable_Disable_Notification(){

        if(mBottomSheetDialog!=null){
            if(mBottomSheetDialog.isShowing()){
                mBottomSheetDialog.dismiss();
            }
        }
        View view = getLayoutInflater ().inflate (R.layout.popup_notification_enable_disable, null);
        mBottomSheetDialog = new Dialog(Dashboard_Activity.this, R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView (view);
        mBottomSheetDialog.setCanceledOnTouchOutside (false);
        mBottomSheetDialog.setCancelable (false);
        mBottomSheetDialog.getWindow ().setLayout (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow ().setGravity (Gravity.CENTER);
        mBottomSheetDialog.show ();

        ImageView ImageResponse = mBottomSheetDialog.findViewById(R.id.response_img);
        TextView Response = mBottomSheetDialog.findViewById(R.id.response_txt);
        Button Ok = mBottomSheetDialog.findViewById(R.id.button_response);


        if(dbHelper.getFieldValueUser("enable_disable")!=null) {
            if (dbHelper.getFieldValueUser("enable_disable").equalsIgnoreCase("enable")) {
                Response.setText(this.getResources().getString(R.string.disable_notification));
            }else{
                Response.setText(this.getResources().getString(R.string.enable_notification));
            }
        }else{
            Response.setText(this.getResources().getString(R.string.enable_notification));
        }

        ImageResponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.dismiss();
            }
        });

        Ok.setText("Ok");
        Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.dismiss();
                if(dbHelper.getFieldValueUser("enable_disable")!=null){
                    if(dbHelper.getFieldValueUser("enable_disable").equalsIgnoreCase("enable")){
                        dbHelper.insertFielduser("enable_disable","disable");
                        Enable_Disable.setImageResource(R.drawable.fail);
                        stopService(new Intent(getApplicationContext(), GPSTracker.class));
                        stopService(new Intent(getApplicationContext(), MonitorService.class));
                    }else{
                        dbHelper.insertFielduser("enable_disable","enable");
                        Enable_Disable.setImageResource(R.drawable.success);
                        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
                            Log.e( "startservice: ","servicst" );

                            startForegroundService(new Intent(Dashboard_Activity.this, GPSTracker.class));
                            startForegroundService(new Intent(Dashboard_Activity.this, MonitorService.class));


                        }else {
                            startService(new Intent(getApplicationContext(), GPSTracker.class));
                            startService(new Intent(getApplicationContext(), MonitorService.class));
                        }
                    }
                }else{
                    dbHelper.insertFielduser("enable_disable","enable");
                    Enable_Disable.setImageResource(R.drawable.success);
                    startService(new Intent(getApplicationContext(), GPSTracker.class));
                    startService(new Intent(getApplicationContext(), MonitorService.class));
                }
            }
        });


    }

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }


    public class CheckvMenu extends AsyncTask<String, Void, String>{
        String resultServer;
        ProgressDialogFragment pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          /*  pd = new ProgressDialogFragment(Dashboard_Activity.this);
            pd.setCancelable(false);
            pd.show();*/
        }

        @Override
        protected String doInBackground(String... strings) {
            ServiceHandler sh = new ServiceHandler();
            HashMap<String, String> args1 = new HashMap<>();
            args1.put("","");


            resultServer = sh.makePostCall("http://13.234.125.210/madira/api/index.php/Menus/menulist",args1);
            try{
                JSONArray HomeArray = new JSONArray(resultServer);
                for(int i = 0;  i<HomeArray.length(); i++){
                    if(HomeArray.getJSONObject(i).getString("name").equalsIgnoreCase("Rate List")){
                        IsShowRate = HomeArray.getJSONObject(i).getString("is_active");
                    }

                }

            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
               /* if (pd.isShowing()) {
                    pd.dismiss();
                }*/

                if(IsShowRate.equalsIgnoreCase("1")){
                    RateList.setVisibility(View.VISIBLE);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
}
