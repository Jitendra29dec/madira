package com.madira;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.madira.Adapter.StateRate_Adapter;
import com.madira.Common.ServiceHandler;
import com.madira.Model.StateRate_Model;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Rate_StateWise_Activity extends AppCompatActivity {
    String State_Id;
    Toolbar toolbar;
    RecyclerView Rate_Recycler;
    StateRate_Adapter stateRate_adapter;
    List<StateRate_Model> Rate_List;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_statewise);

        Intent intent = getIntent();
        Bundle bundle=intent.getExtras();
        State_Id = bundle.getString("state_id");

        toolbar =findViewById(R.id.toolbar_rate_statewise);
        toolbar.setTitle("Rate List");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Rate_Recycler = findViewById(R.id.rate_recycler);

        new GetRateList().execute();
    }


    public class GetRateList extends AsyncTask<String, Void, String> {
        String resultServer;
        ProgressDialogFragment pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialogFragment(Rate_StateWise_Activity.this);
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            ServiceHandler sh = new ServiceHandler();
            HashMap<String, String> args1 = new HashMap<>();
            args1.put("","");
            Rate_List = new ArrayList<StateRate_Model>();
            resultServer = sh.makePostCall("http://13.234.125.210/madira/api/index.php/Rate_List_Detail/ratelistdetail/list_id/"+State_Id,args1);
            try{
                    JSONArray RateArray = new JSONArray(resultServer);
                    for(int i =0; i<RateArray.length(); i++){
                        StateRate_Model list = new StateRate_Model();
                        list.setBrand(RateArray.getJSONObject(i).getString("brand_name"));
                        list.setLiq_type(RateArray.getJSONObject(i).getString("liqueur_type"));
                        list.setPrice(RateArray.getJSONObject(i).getString("retail_price"));
                        list.setSize(RateArray.getJSONObject(i).getString("size_value"));
                        list.setUnit(RateArray.getJSONObject(i).getString("size_unit"));
                        Rate_List.add(list);
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

                if(Rate_List.size()>0){
                    Rate_Recycler.setHasFixedSize(true);
                    Rate_Recycler.setNestedScrollingEnabled(false);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Rate_StateWise_Activity.this);
                    linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                    Rate_Recycler.setLayoutManager(linearLayoutManager);
                    stateRate_adapter = new StateRate_Adapter(Rate_StateWise_Activity.this,Rate_List);
                    Rate_Recycler.setAdapter(stateRate_adapter);
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
