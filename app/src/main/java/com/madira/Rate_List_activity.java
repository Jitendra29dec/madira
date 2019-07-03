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

import com.madira.Adapter.StateAdapter;
import com.madira.Common.ServiceHandler;
import com.madira.Model.State_model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Rate_List_activity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView State_Recycler;
    StateAdapter stateAdapter;
    List<State_model> State_List;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_list);
        toolbar =findViewById(R.id.toolbar_rate_list);
        toolbar.setTitle("Select State");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        State_Recycler = findViewById(R.id.state_recycler);

        new Get_State().execute();
    }


    public class Get_State extends AsyncTask<String, Void, String> {
        String resultServer;
        ProgressDialogFragment pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialogFragment(Rate_List_activity.this);
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            ServiceHandler sh = new ServiceHandler();
            HashMap<String, String> args1 = new HashMap<>();
            args1.put("","");
            State_List = new ArrayList<State_model>();
            resultServer = sh.makePostCall("http://13.234.125.210/madira/api/index.php/Rate-List/ratelist",args1);
            try{
                JSONObject jObj = new JSONObject(resultServer);
                if(jObj.getString("status").equalsIgnoreCase("true")){
                    JSONObject result = jObj.getJSONObject("result");
                    JSONArray StateArray = result.getJSONArray("data");
                    for(int i =0; i<StateArray.length(); i++){
                        State_model list = new State_model();
                        list.setState_Id(StateArray.getJSONObject(i).getString("id"));
                        list.setState_Name(StateArray.getJSONObject(i).getString("location"));
                        State_List.add(list);
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
                if (pd.isShowing()) {
                    pd.dismiss();
                }

                if(State_List.size()>0){
                    State_Recycler.setHasFixedSize(true);
                    State_Recycler.setNestedScrollingEnabled(false);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Rate_List_activity.this);
                    linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                    State_Recycler.setLayoutManager(linearLayoutManager);
                    stateAdapter = new StateAdapter(Rate_List_activity.this,State_List);
                    State_Recycler.setAdapter(stateAdapter);
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
