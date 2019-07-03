package com.madira.Common;

/**
 * Created by anurag on 2/1/2017.
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

public class ServiceHandler {
    public ServiceHandler() {
    }

    public String makePostCall(String url, Map<String, String> params) {
        URL obj;
        try {
            obj = new URL(url);

            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setUseCaches(false);
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setReadTimeout(50000);
            con.setConnectTimeout(50000);
            con.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible )");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            StringBuffer requestParams = new StringBuffer();
            if (params != null && params.size() > 0) {

                Iterator<String> paramIterator = params.keySet().iterator();
                while (paramIterator.hasNext()) {
                    String key = paramIterator.next();
                    String value = params.get(key);
                    requestParams.append(URLEncoder.encode(key, "UTF-8"));
                    requestParams.append("=").append(
                            URLEncoder.encode(value, "UTF-8"));
                    requestParams.append("&");
                }
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(requestParams.toString());
                wr.flush();
                wr.close();

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return response.toString();
            } else {
                return null;
            }
        }catch ( java.net.SocketTimeoutException e ) {
            String Temp = "{\"Error\":\"121\"}";
            return Temp.toString( );
        } catch ( org.apache.http.conn.ConnectTimeoutException e ) {
            String Temp = "{\"Error\":\"122\"}";
            return Temp.toString( );
        } catch (Exception e) {
            return null;
        }
    }

    public String makeGetCall(String url, Map<String, String> params) {
        URL obj;
        try {
            StringBuffer requestParams = new StringBuffer();
            if (params != null && params.size() > 0) {

                Iterator<String> paramIterator = params.keySet().iterator();
                while (paramIterator.hasNext()) {
                    String key = paramIterator.next();
                    String value = params.get(key);
                    requestParams.append(URLEncoder.encode(key, "UTF-8"));
                    requestParams.append("=").append(
                            URLEncoder.encode(value, "UTF-8"));
                    requestParams.append("&");
                }
                obj = new URL(url+"?"+requestParams.toString());
            }else{
                obj = new URL(url);
            }
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setUseCaches(false);
            con.setDoInput(true);
            con.setDoOutput(false);
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
