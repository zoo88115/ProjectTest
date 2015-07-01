package com.example.zoo88115.projecttest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by zoo88115 on 2015/6/10 0010.
 */
public class MyAdapter extends BaseAdapter{
    java.util.Date now = new java.util.Date();
    private Context mContext;
    private static LayoutInflater inflater = null;
    ArrayList<Integer> sID=new ArrayList<Integer>();
    ArrayList<String> sTime=new  ArrayList<String>();
    ArrayList<byte[]> sPhoto=new ArrayList<byte[]>();
    ArrayList<String> sContent=new  ArrayList<String>();
    ArrayList<Integer> uID=new ArrayList<Integer>();
    ArrayList<byte[]> uIcon=new ArrayList<byte[]>();
    ArrayList<String> uName=new  ArrayList<String>();
    ArrayList<String> location=new  ArrayList<String>();
    ArrayList<View> v=new ArrayList<View>();
    public int count=3;
    long lastTime;
    Handler mUIHandler = new Handler();
    HandlerThread mThread;
    Handler mThreadHandler;

    public MyAdapter(Context c,long lastTime) {
        mContext = c;
        inflater = (LayoutInflater)
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.lastTime=lastTime;
        firstGetData();
    }


    @Override
    public int getCount() {
        return count+1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View tempView=v.get(position);
        if(tempView == null){
            if(position == 0 ){
                convertView=inflater.inflate(R.layout.post_layout,null);
                TextView post=(TextView)convertView.findViewById(R.id.postTextView);
                post.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        MainActivity m=(MainActivity)mContext;
                        m.onNavigationDrawerItemSelected(99);
                    }
                });
            }
            else{
                convertView=inflater.inflate(R.layout.status_layout,null);
                ImageView stickerView=(ImageView)convertView.findViewById(R.id.imageView);
                TextView nameView=(TextView)convertView.findViewById(R.id.nameView);
                TextView timeView=(TextView)convertView.findViewById(R.id.timeView);
                ImageView photoView=(ImageView)convertView.findViewById(R.id.photoView);
                TextView contentView=(TextView)convertView.findViewById(R.id.content);
                TextView locationView=(TextView)convertView.findViewById(R.id.locationView);
                if(location.get(position-1)!=null){
                    locationView.setText("From:"+location.get(position-1));
                }

                if(uIcon.get(position-1)!=null){
                    Bitmap bitmap= BitmapFactory.decodeByteArray(uIcon.get(position-1), 0, uIcon.get(position-1).length);
                    stickerView.setImageBitmap(bitmap);
                }
                nameView.setText(uName.get(position-1));
                timeView.setText(sTime.get(position-1).toString());
                if(sPhoto.get(position-1)==null){
                    photoView.setVisibility(View.GONE);//不可見，且不占用布局空間
                }
                else{
                    Bitmap bitmap= BitmapFactory.decodeByteArray(sPhoto.get(position-1), 0, sPhoto.get(position-1).length);
                    photoView.setImageBitmap(bitmap);
                    photoView.setVisibility(View.VISIBLE);
                }
                if(sContent.get(position-1)==null){
                    contentView.setVisibility(View.VISIBLE);
                }
                else {
                    contentView.setText(sContent.get(position - 1));
                    contentView.setVisibility(View.VISIBLE);
                }

            }
            v.add(position,convertView);
        }
        return v.get(position);
    }

    public void firstGetData() {
//        //=============================遠端資料庫===================
//        MyDBHelper d=new MyDBHelper(mContext);
//        SQLiteDatabase d2=d.getWritableDatabase();
//        d2.execSQL("DELETE FROM Status");
//        d2.close();
//        d.close();
//        //===================先清除本地端
//        mThread = new HandlerThread("net");
//        mThread.start();
//        mThreadHandler = new Handler(mThread.getLooper());
//
//        if(mThreadHandler != null){
//            mThreadHandler.removeCallbacksAndMessages(null);
//        }
//        mThreadHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                DataRetrieve d = new DataRetrieve();
//                try {
//                    final ArrayList<HashMap<String, Object>> result = d.getStatus(10);
//                    if (result != null) {
//                        mUIHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                MyDBHelper d3=new MyDBHelper(mContext);
//                                SQLiteDatabase d4=d3.getWritableDatabase();
//                                MainActivity m2=(MainActivity)mContext;
//                                for(int i=0;i<10;i++){
//                                    ContentValues values = new ContentValues();
//                                    values.put("Time",Long.parseLong(result.get(i).get("Time").toString()));
//                                    String temp=result.get(i).get("Photo").toString();
//                                    byte[] bytes=Base64.decode(temp,Base64.DEFAULT);
//                                    values.put("Photo",bytes);
//                                    values.put("Content",result.get(i).get("Content").toString());
//                                    values.put("UserID",m2.tempId);
//                                }
//                            }
//                        });
//                        Toast.makeText(mContext,"成功", Toast.LENGTH_SHORT).show();
//                    }
//                    else {
//                        Toast.makeText(mContext, "no status!", Toast.LENGTH_SHORT).show();
//                    }
//                }
//                catch (Exception e){
//                    Log.e("error", e.toString());
//                }
//            }
//        });
//        //==========================================================
        try {
            MyDBHelper myDBHelper = new MyDBHelper(mContext);
            SQLiteDatabase db = myDBHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT Status.ID,Status.Time,Status.Photo,Status.Content,Status.UserID,User.Icon,User.Name,Status.Location " +
                    "FROM Status,User " +
                    "WHERE Status.UserID=User.ID and Status.Time<=" +lastTime+" "+
                    "ORDER BY Status.Time DESC", null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                if(cursor.getCount()<count)
                    count=cursor.getCount();
                for (int i = 0; i < count; i++) {
                    sID.add(cursor.getInt(0));
                    sTime.add(new java.text.SimpleDateFormat("yyyy MM-dd HH:mm:ss").format(new java.util.Date (cursor.getLong(1))));
                    sPhoto.add(cursor.getBlob(2));
                    sContent.add(cursor.getString(3));
                    uID.add(cursor.getInt(4));
                    uIcon.add(cursor.getBlob(5));
                    uName.add(cursor.getString(6));
                    String l=cursor.getString(7);
                    location.add(cursor.getString(7));
                    v.add(null);
                    cursor.moveToNext();
                }
            }
            else{
                count=0;
            }
            db.close();
            myDBHelper.close();
        }
        catch (Exception e){
            Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void newGetData() {
        java.util.Date now = new java.util.Date();
        long nowTime=now.getTime();
        try {
            MyDBHelper myDBHelper = new MyDBHelper(mContext);
            SQLiteDatabase db = myDBHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT Status.ID,Status.Time,Status.Photo,Status.Content,Status.UserID,User.Icon,User.Name,Status.Location " +
                    "FROM Status,User " +
                    "WHERE Status.UserID=User.ID and Status.Time>" +lastTime+" and Status.Time<="+nowTime+" "+
                    "ORDER BY Status.Time ASC", null);//按時間順序
            if (cursor != null && cursor.getCount() > 0) {
                count+=cursor.getCount();//新增增加count量
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {//新增資料全從第一個開始
                    sID.add(0,cursor.getInt(0));
                    sTime.add(0,new java.text.SimpleDateFormat("yyyy MM-dd HH:mm:ss").format(new java.util.Date(cursor.getLong(1))));
                    sPhoto.add(0,cursor.getBlob(2));
                    sContent.add(0,cursor.getString(3));
                    uID.add(0,cursor.getInt(4));
                    uIcon.add(0,cursor.getBlob(5));
                    uName.add(0,cursor.getString(6));
                    location.add(0,cursor.getString(7));
                    v.add(1,null);
                    cursor.moveToNext();
                }
            }
            lastTime=nowTime;
            db.close();
            myDBHelper.close();
        }
        catch (Exception e){
            Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
