package com.madira;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.madira.Adapter.ListAdapter;
import com.madira.DBHelper.DBHelper;
import com.madira.Model.ListModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.protocol.BasicHttpContext;
import cz.msebera.android.httpclient.protocol.HttpContext;

import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class FragmentListView extends Fragment {
    RecyclerView Theka_Recycler;
    ListAdapter listAdapter;
    List<ListModel> List_Theka;
    DBHelper dbHelper;
    static double PI_RAD = Math.PI / 180.0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listview, container, false);

        dbHelper = new DBHelper(getActivity());
        Theka_Recycler = view.findViewById(R.id.list_recycler);


        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Do your Work
            if(dbHelper.getFieldValueUser("placeArray")!=null){
                new GetList().execute();
            }


        } else {
            // Do your Work
        }
    }


    public class GetList extends AsyncTask<String, Void, String>{
        ProgressDialogFragment pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialogFragment(getActivity());
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            List_Theka = new ArrayList<ListModel>();
            List<HashMap<String, String>> nearbyPlacesList = null;
            DataParser dataParser = new DataParser();
            nearbyPlacesList = dataParser.parse(dbHelper.getFieldValueUser("placeArray"));
            for (int i = 0; i < nearbyPlacesList.size(); i++) {
                Log.d("onPostExecute","Entered into showing locations");
                MarkerOptions markerOptions = new MarkerOptions();
                HashMap<String, String> googlePlace = nearbyPlacesList.get(i);
                ListModel list = new ListModel();
                list.setLat(Double.parseDouble(googlePlace.get("lat")));
                list.setLng(Double.parseDouble(googlePlace.get("lng")));
                list.setName(googlePlace.get("place_name"));
                list.setAddress(googlePlace.get("vicinity"));


                list.setDistance(String.format("%.1f",greatCircleInKilometers(
                        Double.parseDouble(dbHelper.getFieldValueUser("lat")),
                        Double.parseDouble(dbHelper.getFieldValueUser("lng")),
                        Double.parseDouble(googlePlace.get("lat")),
                        Double.parseDouble(googlePlace.get("lng")))));


                List_Theka.add(list);

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

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                Theka_Recycler.setLayoutManager(linearLayoutManager);
                listAdapter = new ListAdapter(getActivity(),getSortedJobCandidateByAge());
                Theka_Recycler.setAdapter(listAdapter);

               // System.out.print(getSortedJobCandidateByAge());
               // System.out.print("hello");


            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    public Double Distance(double latitude, double longitude,
                           double prelatitute, double prelongitude){
        Location startPoint=new Location("locationA");
        startPoint.setLatitude(latitude);
        startPoint.setLongitude(longitude);

        Location endPoint=new Location("locationA");
        endPoint.setLatitude(prelatitute);
        endPoint.setLongitude(prelongitude);

        double distance=startPoint.distanceTo(endPoint);
        return distance;
    }




    private String getDistanceOnRoad(double latitude, double longitude,
                                     double prelatitute, double prelongitude) {
        String result_in_kms = "";
        String url = "https://maps.google.com/maps/api/directions/xml?key=AIzaSyCQ29lSU6NVdK74dEucBLvLmQDNG1M1iEo&origin="
                + latitude + "," + longitude + "&destination=" + prelatitute
                + "," + prelongitude + "&sensor=false&units=metric";
        String tag[] = { "text" };
        HttpResponse response = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(url);
            response = httpClient.execute(httpPost, localContext);
            InputStream is = response.getEntity().getContent();
            DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            Document doc = builder.parse(is);
            if (doc != null) {
                NodeList nl;
                ArrayList args = new ArrayList();
                for (String s : tag) {
                    nl = doc.getElementsByTagName(s);
                    if (nl.getLength() > 0) {
                        Node node = nl.item(nl.getLength() - 1);
                        args.add(node.getTextContent());
                    } else {
                        args.add(" - ");
                    }
                }
                result_in_kms = String.format("%s", args.get(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result_in_kms;
    }


    public static Comparator<ListModel> ageComparator = new Comparator<ListModel>() {
        @Override
        public int compare(ListModel jc1, ListModel jc2) {
            return (jc1.getDistance().compareTo(jc2.getDistance()));
        }
    };


    public List<ListModel> getSortedJobCandidateByAge() {
        Collections.sort(List_Theka, ageComparator);
        return List_Theka;
    }



    public double greatCircleInFeet(LatLng latLng1, LatLng latLng2) {
        return greatCircleInKilometers(latLng1.latitude, latLng1.longitude, latLng2.latitude,
                latLng2.longitude) * 3280.84;
    }

    /**
     * Use Great Circle distance formula to calculate distance between 2 coordinates in meters.
     */
    public double greatCircleInMeters(LatLng latLng1, LatLng latLng2) {
        return greatCircleInKilometers(latLng1.latitude, latLng1.longitude, latLng2.latitude,
                latLng2.longitude) * 1000;
    }

    /**
     * Use Great Circle distance formula to calculate distance between 2 coordinates in kilometers.
     * https://software.intel.com/en-us/blogs/2012/11/25/calculating-geographic-distances-in-location-aware-apps
     */
    public double greatCircleInKilometers(double lat1, double long1, double lat2, double long2) {
        double phi1 = lat1 * PI_RAD;
        double phi2 = lat2 * PI_RAD;
        double lam1 = long1 * PI_RAD;
        double lam2 = long2 * PI_RAD;

        return 6371.01 * acos(sin(phi1) * sin(phi2) + cos(phi1) * cos(phi2) * cos(lam2 - lam1));
    }



}