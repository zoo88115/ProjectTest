package com.example.zoo88115.projecttest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Created by zoo88115 on 2015/6/10 0010.
 */
public class MyAdapter extends BaseAdapter{

    private Context mContext;
    private static LayoutInflater inflater = null;
    ArrayList<Integer> sID;
    ArrayList<String> sTime;
    ArrayList<byte[]> sPhoto;
    ArrayList<String> sContent;
    ArrayList<Integer> uID;
    ArrayList<byte[]> uIcon;
    ArrayList<String> uName;
    View[] v;
    public int count;
    int p=0;

    public MyAdapter(Context c,ArrayList<Integer> sID,ArrayList<String> sTime,ArrayList<byte[]> sPhoto,ArrayList<String> sContent,ArrayList<Integer> uID,
                     ArrayList<byte[]> uIcon,ArrayList<String> uName,int count) {
        mContext = c;
        inflater = (LayoutInflater)
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.sID=sID;
        this.sTime=sTime;
        this.sPhoto=sPhoto;
        this.sContent=sContent;
        this.uID=uID;
        this.uIcon=uIcon;
        this.uName=uName;
        this.count=count;
        this.v=new View[count+1];
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

        if(v[position] == null){
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
            v[position]=convertView;
        }
        return v[position];
    }
}
