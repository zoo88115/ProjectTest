package com.example.zoo88115.projecttest;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tcumi_H505 on 2015/6/18.
 */
public class DataRetrieve {

    private String uriAPI = "http://203.64.84.122/login.php";
    private String uriAPI2 = "http://203.64.84.122/getstatus.php";
    private String uriAPI3="http://203.64.84.122/getusers.php";

    public DataRetrieve(){
    }
    private ArrayList<HashMap<String, Object>> parseJson(String input){
        try{
            JSONArray jsonArray = new JSONArray(input);
            ArrayList<HashMap<String, Object>> users = new ArrayList<HashMap<String, Object>>();
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, Object> h2 = new HashMap<String, Object>();
                h2.put("ID", jsonObject.getString("ID"));
                h2.put("Icon", jsonObject.getString("Icon"));
                h2.put("Account", jsonObject.getString("Account"));
                users.add(h2);
            }
            return users;
        }
        catch (JSONException e){
            Log.e("Failed!", e.toString());
        }
        return null;
    }
    private ArrayList<HashMap<String, Object>> parseJson2(String input){
        try{
            JSONArray jsonArray = new JSONArray(input);
            ArrayList<HashMap<String, Object>> users = new ArrayList<HashMap<String, Object>>();
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, Object> h2 = new HashMap<String, Object>();
                h2.put("Time", jsonObject.getString("Time"));
                h2.put("Photo", jsonObject.getString("Photo"));
                h2.put("Content", jsonObject.getString("Content"));
                users.add(h2);
            }
            return users;
        }
        catch (JSONException e){
            Log.e("Failed!", e.toString());
        }
        return null;
    }

    private ArrayList<HashMap<String, Object>> parseJson3(String input){
        try{
            JSONArray jsonArray = new JSONArray(input);
            ArrayList<HashMap<String, Object>> users = new ArrayList<HashMap<String, Object>>();
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, Object> h2 = new HashMap<String, Object>();
                h2.put("ID", jsonObject.getString("ID"));
                h2.put("Icon", jsonObject.getString("Icon"));
                h2.put("Account", jsonObject.getString("Account"));
                h2.put("Name",jsonObject.getString("Name"));
                h2.put("Password",jsonObject.get("Password"));
                users.add(h2);
            }
            return users;
        }
        catch (JSONException e){
            Log.e("Failed!", e.toString());
        }
        return null;
    }

    private String executeQuery(String query, String query2){
        String result = "";

        try{
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = null;
            if(query.equals("*")&&query2.equals("*")){
                httpPost = new HttpPost(uriAPI3);
            }
            else if(!query2.equals("")){
                httpPost = new HttpPost(uriAPI);
            }
            else{
                httpPost = new HttpPost(uriAPI2);
            }
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            if(query.equals("*")&&query2.equals("*")){
                nameValuePairs.add(new BasicNameValuePair("data", query));
            }
            else if(!query2.equals("")){
                nameValuePairs.add(new BasicNameValuePair("account", query));
                nameValuePairs.add(new BasicNameValuePair("password", query2));
            }
            else{
                nameValuePairs.add(new BasicNameValuePair("count", query));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();

            InputStream inputStream = httpEntity.getContent();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null)
            {
                stringBuilder.append(line + "\n");
            }
            inputStream.close();
            result = stringBuilder.toString();
        }
        catch (Exception e){
            Log.e("Failed!", e.toString());
        }
        return result;
    }
    public ArrayList<HashMap<String, Object>> login(String account, String password){
        final String result = executeQuery(account, password);
        if(!result.equals("")){
            final ArrayList<HashMap<String,Object>> output = parseJson(result);
            if(output != null){
                return output;
            }
        }
        return null;
    }
    public ArrayList<HashMap<String, Object>> getStatus(int count){
        final String result = executeQuery(Integer.toString(count), "");
        if(!result.equals("")){
            final ArrayList<HashMap<String, Object>> output = parseJson2(result);
            if(output != null){
                return output;
            }
        }
        return null;
    }
    public ArrayList<HashMap<String, Object>> getAllUser(){
        final String result = executeQuery("*","*");
        if(!result.equals("")){
            final ArrayList<HashMap<String,Object>> output = parseJson3(result);
            if(output != null){
                return output;
            }
        }
        return null;
    }
}
