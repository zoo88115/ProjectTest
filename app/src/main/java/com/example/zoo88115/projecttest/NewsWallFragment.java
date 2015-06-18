package com.example.zoo88115.projecttest;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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
    float yDown;
    int s=0;//0是無狀態更新 1是要更新adapter
    ListView listView;
    TextView updateText;
    ArrayList<Integer> sID=new ArrayList<Integer>();
    ArrayList<String> sTime=new ArrayList<String>();
    ArrayList<byte[]> sPhoto=new ArrayList<byte[]>();
    ArrayList<String> sContent=new ArrayList<String>();
    ArrayList<Integer> uID=new ArrayList<Integer>();
    ArrayList<byte[]> uIcon=new ArrayList<byte[]>();
    ArrayList<String> uName=new ArrayList<String>();
    int count=0;

    public NewsWallFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getData();
        View rootView= inflater.inflate(R.layout.fragment_news_wall, container, false);
        updateText=(TextView)rootView.findViewById(R.id.updateText);
        updateText.setVisibility(View.GONE);
        listView=(ListView)rootView.findViewById(R.id.listView);
        MyAdapter myAdapter=new MyAdapter(getActivity(),sID,sTime,sPhoto,sContent,uID,uIcon,uName,count);
        listView.setAdapter(myAdapter);
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
                                updateText.setVisibility(View.VISIBLE);
                                s=1;
                            }
                        }
                        else if(listView.getLastVisiblePosition()==count && s==0){
                            float yMove=event.getRawY();
                            int distance = (int) (yMove - yDown);
                            if(distance<-150){
                                s=2;
                            }
                        }
                        break;
                    default:
                        if(s==1){
                            java.util.Date now = new java.util.Date();
                            Long longtime=new Long(now.getTime()/1000);
                            Toast.makeText(getActivity(),longtime.toString()+"\n"+new java.text.SimpleDateFormat("yyyy MM-dd HH:mm:ss").format(new java.util.Date (longtime*1000)),Toast.LENGTH_SHORT).show();
                            updateText.setVisibility(View.GONE);
                            sID.clear();
                            sTime.clear();
                            sPhoto.clear();
                            sContent.clear();
                            uID.clear();
                            uIcon.clear();
                            uName.clear();
                            getData();
                            MyAdapter myAdapter=new MyAdapter(getActivity(),sID,sTime,sPhoto,sContent,uID,uIcon,uName,count);
                            myAdapter.count=1;
                            count=1;
                            listView.setAdapter(myAdapter);
                            s=0;
                        }
                        else if(s==2){
                            java.util.Date now = new java.util.Date();
                            Long longtime=new Long(now.getTime()/1000);
                            Toast.makeText(getActivity(),longtime.toString()+"\n"+new java.text.SimpleDateFormat("yyyy MM-dd HH:mm:ss").format(new java.util.Date (longtime*1000)),Toast.LENGTH_SHORT).show();
                            MyAdapter myAdapter=new MyAdapter(getActivity(),sID,sTime,sPhoto,sContent,uID,uIcon,uName,count);
                            myAdapter.count=4;
                            count=4;
                            listView.setAdapter(myAdapter);
                            s=0;
                        }
                        break;
                }
                return false;
            }
        });
        return rootView;
    }

    public void getData() {
        java.util.Date date = new java.util.Date();
        Long now=date.getTime();
        String a=now.toString();
        try {
            MyDBHelper myDBHelper = new MyDBHelper(getActivity());
            SQLiteDatabase db = myDBHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT Status.ID,Status.Time,Status.Photo,Status.Content,Status.UserID,User.Icon,User.Name " +
                    "FROM Status,User " +
                    "WHERE Status.UserID=User.ID and Status.Time<" +a+" "+
                    "ORDER BY Status.Time DESC", null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    sID.add(cursor.getInt(0));
                    sTime.add(new java.text.SimpleDateFormat("yyyy MM-dd HH:mm:ss").format(new java.util.Date (cursor.getLong(1))));
                    sPhoto.add(cursor.getBlob(2));
                    sContent.add(cursor.getString(3));
                    uID.add(cursor.getInt(4));
                    uIcon.add(cursor.getBlob(5));
                    uName.add(cursor.getString(6));
                    cursor.moveToNext();
                }
            }
            count = cursor.getCount();
            db.close();
            myDBHelper.close();
        }
        catch (Exception e){
            Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_SHORT).show();
        }
    }

}
