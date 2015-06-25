package com.example.zoo88115.projecttest;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Timestamp;
import java.util.ArrayList;

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
    ArrayList<View> v=new ArrayList<View>();
    public int count=3;
    long lastTime;
    int p=0;

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
                contentView.setText(sContent.get(position-1));
            }
            v.add(position,convertView);
        }
        return v.get(position);
    }

    public void firstGetData() {
        try {
            MyDBHelper myDBHelper = new MyDBHelper(mContext);
            SQLiteDatabase db = myDBHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT Status.ID,Status.Time,Status.Photo,Status.Content,Status.UserID,User.Icon,User.Name " +
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
                    v.add(null);
                    cursor.moveToNext();
                }
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
            Cursor cursor = db.rawQuery("SELECT Status.ID,Status.Time,Status.Photo,Status.Content,Status.UserID,User.Icon,User.Name " +
                    "FROM Status,User " +
                    "WHERE Status.UserID=User.ID and Status.Time>" +lastTime+" and Status.Time<="+nowTime+" "+
                    "ORDER BY Status.Time ASC", null);//按時間順序
            if (cursor != null && cursor.getCount() > 0) {
                int round=3;
                if(cursor.getCount()<round)
                    round=cursor.getCount();
                count+=round;//新增增加count量
                cursor.moveToFirst();
                for (int i = 0; i < round; i++) {//新增資料全從第一個開始
                    sID.add(0,cursor.getInt(0));
                    sTime.add(0,new java.text.SimpleDateFormat("yyyy MM-dd HH:mm:ss").format(new java.util.Date(cursor.getLong(1))));
                    sPhoto.add(0,cursor.getBlob(2));
                    sContent.add(0,cursor.getString(3));
                    uID.add(0,cursor.getInt(4));
                    uIcon.add(0,cursor.getBlob(5));
                    uName.add(0,cursor.getString(6));
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
