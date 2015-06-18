package com.example.zoo88115.projecttest;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.widget.Toast;

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

    private Handler mUIHandler = new Handler();
    private HandlerThread mThread;
    private Handler mThreadHandler;
    private String uriAPI = "http://203.64.84.122/httppostjson.php";
    private Context mContext;

    public DataRetrieve(Context context){
        this.mContext = context;
        mThread = new HandlerThread("net");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
    }

    public final void reNew(String input){
        try{
            JSONArray jsonArray = new JSONArray(input);
            ArrayList<HashMap<String, Object>> users = new ArrayList<HashMap<String, Object>>();
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, Object> h2 = new HashMap<String, Object>();
                h2.put("A1", jsonObject.getString("A1"));
                h2.put("A2", jsonObject.getString("A2"));
                h2.put("A3", jsonObject.getString("A3"));
                users.add(h2);
                Toast.makeText(mContext, users.get(0).get("A1").toString() + " " + users.get(0).get("A2").toString() + " " + users.get(0).get("A3").toString(), Toast.LENGTH_SHORT).show();
            }
        }
        catch (JSONException e){
            Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
    private String executeQuery(String query){
        String result = "";

        try{
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(uriAPI);
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("data", query));
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
            Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
        }
        return result;
    }
    public void getData(String query){
        final String txt = query;
        mThreadHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                final String jsonString = executeQuery(txt);
                mUIHandler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        reNew(jsonString);
                    }
                });
            }
        });
    }
}
