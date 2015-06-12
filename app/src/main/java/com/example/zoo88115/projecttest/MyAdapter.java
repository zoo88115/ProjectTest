package com.example.zoo88115.projecttest;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by zoo88115 on 2015/6/10 0010.
 */
public class MyAdapter extends BaseAdapter{

    private Context mContext;
    private static LayoutInflater inflater = null;

    String[] name={"陳重堯","蕭方娸","林怡君","卓秉賢","戴睿紘","許嘉祐"};
    Image[] icon={null,null,null,null,null};
    String[] time={"2015-06-10_23:10:00","2015-06-10_23:09:00","2015-06-10_23:08:00","2015-06-10_23:07:00","2015-06-10_23:06:00"};
    Image[] photo={null,null,null,null,null};
    String[] text={"沒事無聊發發文","沒事無聊發發文沒事無聊發發文","沒事無聊發發文\n沒事無聊發發文","沒事無聊發發文沒事無聊發發文沒事無聊發發文","沒事無聊發發文\n沒事無聊發發文\n沒事無聊發發文"};

    public MyAdapter(Context c) {
        mContext = c;
        inflater = (LayoutInflater)
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return 6;
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
        if(convertView == null){
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
                if(icon[position-1]==null){

                }
                nameView.setText(name[position-1]);
                timeView.setText(time[position-1]);
                if(photo[position-1]==null){
                    photoView.setVisibility(View.GONE);//不可見，且不占用布局空間
                }
                contentView.setText(text[position-1]);
            }
        }
        return convertView;
    }
}
