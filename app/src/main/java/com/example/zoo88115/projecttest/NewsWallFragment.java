package com.example.zoo88115.projecttest;


import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Blob;
import java.sql.Timestamp;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewsWallFragment extends Fragment {
    java.util.Date now = new java.util.Date();
    float yDown;
    int s=0;//0是無狀態更新 1是要更新adapter
    ListView listView;
    TextView updateText;
    MyAdapter myAdapter;
    ImageView imageView;
    LinearLayout linearLayout;
    Animation animFadein;

    public NewsWallFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_news_wall, container, false);
        imageView=(ImageView)rootView.findViewById(R.id.imageView3);
        linearLayout=(LinearLayout)rootView.findViewById(R.id.linearLayout);
        linearLayout.setVisibility(View.GONE);
        updateText=(TextView)rootView.findViewById(R.id.updateText);
        //updateText.setVisibility(View.GONE);
        listView=(ListView)rootView.findViewById(R.id.listView);
        //myAdapter=new MyAdapter(getActivity(),now.getTime());
        //listView.setAdapter(myAdapter);
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        yDown = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if(listView.getFirstVisiblePosition()==0 && s==0)
                        {
                            float yMove=event.getRawY();
                            int distance = (int) (yMove - yDown);
                            if(distance>150 && listView.getFirstVisiblePosition()==0){
                                //updateText.setVisibility(View.VISIBLE);
                                linearLayout.setVisibility(View.VISIBLE);
                                s=1;
                            }
                        }
                        break;
                    default:
                        if(s==1){
                            java.util.Date now = new java.util.Date();
                            Long longtime=new Long(now.getTime()/1000);
                            Toast.makeText(getActivity(),longtime.toString()+"\n"+new java.text.SimpleDateFormat("yyyy MM-dd HH:mm:ss").format(new java.util.Date (longtime*1000)),Toast.LENGTH_SHORT).show();
                            try {
                                animFadein = AnimationUtils.loadAnimation(getActivity(), R.anim.myanim);
                                imageView.startAnimation(animFadein);
                            }
                            catch(Exception e){

                            }
                            //updateText.setVisibility(View.GONE);
                            linearLayout.setVisibility(View.GONE);
                            myAdapter.newGetData();
                            listView.refreshDrawableState();
                            s=0;
                        }
                        break;
                }
                return false;
            }
        });
        return rootView;
    }

}
