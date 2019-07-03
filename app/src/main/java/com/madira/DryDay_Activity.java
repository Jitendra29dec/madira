package com.madira;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.madira.Adapter.DryDay_Adapter;
import com.madira.Common.ServiceHandler;
import com.madira.Model.DryDay_Model;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DryDay_Activity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView Dry_day_recycler_Recycler;
    DryDay_Adapter dryDay_adapter;
    List<DryDay_Model> Dryday_List;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drydays_list);
        toolbar =findViewById(R.id.toolbar_dry_list);
        toolbar.setTitle("Dry Days List");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        Dry_day_recycler_Recycler = findViewById(R.id.dry_day_recycler);


        new GetDry_DaysList().execute();


    }


    public class GetDry_DaysList extends AsyncTask<String, Void, String>{
        String resultServer;
        ProgressDialogFragment pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialogFragment(DryDay_Activity.this);
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            ServiceHandler sh = new ServiceHandler();
            HashMap<String, String> args1 = new HashMap<>();
            args1.put("","");
            Dryday_List = new ArrayList<DryDay_Model>();
            resultServer = sh.makePostCall("http://13.234.125.210/madira/api/index.php/Dry-Days/getdrydays",args1);
            try{

                try{
                        JSONArray DaysArray = new JSONArray(resultServer);

                        for( int i =0; i< DaysArray.length(); i++){
                            DryDay_Model list = new DryDay_Model();
                            list.setDay_name(DaysArray.getJSONObject(i).getString("day_name"));
                            list.setDate(DaysArray.getJSONObject(i).getString("date"));
                            Dryday_List.add(list);
                        }




                }catch (Exception e){
                    e.printStackTrace();
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
                if (pd.isShowing()) {
                    pd.dismiss();
                }

                if(Dryday_List.size()>0){
                    Dry_day_recycler_Recycler.setHasFixedSize(true);
                    Dry_day_recycler_Recycler.setNestedScrollingEnabled(false);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DryDay_Activity.this);
                    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    Dry_day_recycler_Recycler.setLayoutManager(linearLayoutManager);
                    dryDay_adapter = new DryDay_Adapter(DryDay_Activity.this,Dryday_List);
                    Dry_day_recycler_Recycler.setAdapter(dryDay_adapter);
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
