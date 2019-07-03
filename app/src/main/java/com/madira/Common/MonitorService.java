package com.madira.Common;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.madira.DBHelper.DBHelper;
import com.madira.DataParser;
import com.madira.DownloadUrl;
import com.madira.ProgressDialogFragment;
import com.madira.R;
import com.madira.Util.ConnectionDetector;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class MonitorService extends Service {
    private ConnectivityManager cnnxManager;
    DBHelper dbHelper;
    long interval;
    Timer timer;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;
    Handler handler, mHandler;
    Message msg;
    GPSTracker gps;
    ConnectionDetector cd;
    double lat, lng;
    private int PROXIMITY_RADIUS = 5000;
    public static final int NOTIFICATION_ID = 100;
    List<HashMap<String, String>> nearbyPlacesList = null;
    TextToSpeech Speak_Notification;
    static double PI_RAD = Math.PI / 180.0;

    @Override
    public void onCreate() {
        super.onCreate();
        cnnxManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        dbHelper = new DBHelper(this);
        gps = new GPSTracker(this);
        cd = new ConnectionDetector(this);
        Speak_Notification = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    Speak_Notification.setLanguage(Locale.US);
                }
            }
        });
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_app";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "MyApp", NotificationManager.IMPORTANCE_LOW);
            channel.setSound(null,null);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSound(null)
                    .setContentTitle("")
                    .setContentText("").build();
            startForeground(1, notification);
        }//for starting service in oreo n above
       /* builder = new AlertDialog.Builder(this);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = "Please Switch On GPS on high accuracy!";

        builder.setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                Intent i = new Intent(action);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                d.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        alertDialog = builder.create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);*/

        handler = new Handler(new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                if (msg.arg1 == 1) {
                    displayPromptForEnablingGPS();
                }
                return false;
            }
        });

        //misc = new Misc(this);
        timer = new Timer();
        if (dbHelper.getFieldValue("delayseconds") == null) {
            interval = 30;
        } else {
            interval = Integer.parseInt(dbHelper.getFieldValue("delayseconds"));
        }
        mHandler = new Handler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        /*timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				if(!smartLocation.location().state().locationServicesEnabled() && (dbHelper.getFieldValue("SysUserID")!=null)){
					msg = new Message();
					msg.arg1=1;
					handler.sendMessage(msg);
				}
				GetDataExecutor();
			}
		}, 0, interval*1000);*/
        Log.e( "onCreate: ","service Called" );
        startRepeatingTask();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
        if (Speak_Notification != null) {
            Speak_Notification.stop();
            Speak_Notification.shutdown();
        }
    }

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        handler.removeCallbacks(mStatusChecker);
    }

/*	@Override
	public void onLocationUpdated(Location location) {
		dbHelper.insertField("CurrentLat", location.getLatitude()+"");
		dbHelper.insertField("CurrentLong", location.getLongitude()+"");
	}*/

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void displayPromptForEnablingGPS() {
        /*if (!alertDialog.isShowing()) {
            try {
                alertDialog.show();
            } catch (Exception e) {

            }
        }*/
    }

    Runnable mStatusChecker = new Runnable() {
        String jsonStr = null;

        @Override
        public void run() {
            try {

                if (!gps.canGetLocation()) {
                    msg = new Message();
                    msg.arg1 = 1;
                    handler.sendMessage(msg);
                }

                boolean isNetworkAvailable = false;
                try {

                    for (int i = 0; i < 6; i++) {
                        if (cd.isConnectingToInternet()) {
                            break;
                        }
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            Thread.interrupted();
                        }
                    }

                    if (cd.isConnectingToInternet()) {
                        if (dbHelper.getFieldValueUser("enable_disable") != null) {
                            if (dbHelper.getFieldValueUser("enable_disable").equalsIgnoreCase("enable")) {
                                Location loc = gps.getLocation();
                                String url = getUrl(loc.getLatitude(), loc.getLongitude(), "liquor_store|bar");
                                Object[] DataTransfer = new Object[3];
                                DataTransfer[0] = url;
                                DataTransfer[1] = loc.getLatitude();
                                DataTransfer[2] = loc.getLongitude();
                                Log.d("onClick", url);
                                // GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
                                //new GetNearbyPlacesData().execute(DataTransfer);
                                new GetNearbyPlacesData().execute(DataTransfer);
                            }
                        }

                    }

                } catch (Exception e) {
                    System.err.print(e.getMessage());
                }
            } finally {
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        new Thread(mStatusChecker).start();
                    }
                }, interval * 1000);
                //mHandler.postDelayed(mStatusChecker, interval * 1000);
            }
        }
    };


    private String getUrl(double latitude, double longitude, String nearbyPlace) {

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyCQ29lSU6NVdK74dEucBLvLmQDNG1M1iEo");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }

    public class GetNearbyPlacesData extends AsyncTask<Object, String, String> {

        String googlePlacesData;
        GoogleMap mMap;
        String url;
        Double Lat, Lng;
        Double Distance;
        ProgressDialogFragment pd;


        @Override
        protected String doInBackground(Object... params) {
            try {
                Log.d("GetNearbyPlacesData", "doInBackground entered");
                url = (String) params[0];
                Lat = (Double) params[1];
                Lng = (Double) params[2];
                DownloadUrl downloadUrl = new DownloadUrl();
                googlePlacesData = downloadUrl.readUrl(url);
                Log.d("GooglePlacesReadTask", "doInBackground Exit");
            } catch (Exception e) {
                Log.d("GooglePlacesReadTask", e.toString());
            }
            return googlePlacesData;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("GooglePlacesReadTask", "onPostExecute Entered");
            //List<HashMap<String, String>> nearbyPlacesList = null;
            DataParser dataParser = new DataParser();
            nearbyPlacesList = dataParser.parse(result);
            new Thread(new GetPlaces()).start();

            //ShowNearbyPlaces(nearbyPlacesList);
            Log.d("GooglePlacesReadTask", "onPostExecute Exit");
        }


        class GetPlaces implements Runnable {

            @Override
            public void run() {
                ShowNearbyPlaces(nearbyPlacesList);
            }
        }

        private void ShowNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {
            for (int i = 0; i < nearbyPlacesList.size(); i++) {
                Log.d("onPostExecute", "Entered into showing locations");
                HashMap<String, String> googlePlace = nearbyPlacesList.get(i);
                lat = Double.parseDouble(googlePlace.get("lat"));
                lng = Double.parseDouble(googlePlace.get("lng"));
                String placeName = googlePlace.get("place_name");
                //String vicinity = googlePlace.get("vicinity");
                //LatLng latLng = new LatLng(lat, lng);
              /*  if(i==0){
                    Distance = 99l;

               }else{*/
                LatLng lng1 = new LatLng(Lat, Lng);
                LatLng lng2 = new LatLng(lat, lng);
                // Distance = getDistanceOnRoad(Lat, Lng, lat, lng);
                Distance = greatCircleInKilometers(Lat, Lng, lat, lng) * 1000;
                //  }
                /* Dis = getDistanceBetween(Lat, Lng, lat, lng);*/
                Log.e( "ShowNearbyPlaces: ",Distance+"" +"places");

                try {
                    if (Distance <= 550.0 && Distance > 0) {
                        Log.e( "ShowNearbyPlaces: ","found" );
                        lat = Double.parseDouble(googlePlace.get("lat"));
                        lng = Double.parseDouble(googlePlace.get("lng"));
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=" + Lat + "," + Lng + "&daddr=" +
                                lat + "," +
                                lng));
                        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                        PendingIntent pi = PendingIntent.getActivity(MonitorService.this, 0, intent,
                                PendingIntent.FLAG_UPDATE_CURRENT);

                        Speak_Notification.speak("Hello A Wine Shop found near you", TextToSpeech.QUEUE_FLUSH, null);
                        NotificationManager nManager = (NotificationManager) MonitorService.this.getSystemService(Context.NOTIFICATION_SERVICE);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            NotificationChannel channel = new NotificationChannel("Madira1", "Madira_noti", NotificationManager.IMPORTANCE_HIGH);
                            nManager.createNotificationChannel(channel);
                        }
                        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
                        bigText.bigText("Wine Shop found near you");
                        bigText.setBigContentTitle(placeName);
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MonitorService.this)
                                .setLargeIcon(BitmapFactory.decodeResource(MonitorService.this.getResources(), R.mipmap.ic_launcher))
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(placeName)
                                .setTicker(placeName)
                                .setContentText("Wine Shop found near you")
                                .setChannelId("Madira1")
                                .setStyle(bigText)
                                .addAction(R.mipmap.ic_action_directions, "Start Navigation", pi)
                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                // .setContentIntent(mClick)
                                .setAutoCancel(true)
                                .setOnlyAlertOnce(true);

                        nManager.notify(NOTIFICATION_ID, mBuilder.build());


                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


        private long getDistanceOnRoad(double latitude, double longitude,
                                       double prelatitute, double prelongitude) {
            String result_in_kms = "";
            String url = "https://maps.google.com/maps/api/directions/xml?key=AIzaSyCQ29lSU6NVdK74dEucBLvLmQDNG1M1iEo&origin="
                    + latitude + "," + longitude + "&destination=" + prelatitute
                    + "," + prelongitude + "&sensor=false&units=metric";
            String tag[] = {"text"};
            try {
                URL urlz = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) urlz.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
			/*
			if(inputStream != null)
				result = convertInputStreamToString(inputStream);
			else
				result = "Did not work!";*/
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

            long distanceinmeters = 0L;

            try {
                String ex = result_in_kms.replace(" km", "");
                distanceinmeters = (long) (Float.parseFloat(ex) * 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return distanceinmeters;
        }
    }


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