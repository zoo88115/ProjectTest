package com.example.zoo88115.projecttest;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity2Activity extends ActionBarActivity {

    public int tempId;
    public String tempEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity2);
        if (savedInstanceState == null) {
            if(useStore()==false) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container2, new PlaceholderFragment())
                        .commit();
            }
            else {
                changeAcitvity();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void switchFragment(int page){
        Fragment newFragment = null;
        if(page == 0){
            newFragment = new PlaceholderFragment();
        }
        else if(page == 1){
            newFragment = new RegisterFragment();
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container2, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void changeAcitvity(){

        Bundle bundle = new Bundle();
        bundle.putString("tempId",String.valueOf(tempId));
        bundle.putString("tempEmail",tempEmail);
        Intent myIntent = new Intent(this, MainActivity.class);
        myIntent.putExtras(bundle);
        startActivity(myIntent);
        finish();
    }

    public boolean useStore(){
        MyDBHelper myDBHelper=new MyDBHelper(this);
        SQLiteDatabase db=myDBHelper.getReadableDatabase();
        Cursor cursor =db.query("Temp", // a. table
                new String[] {"ID", "Email"}, // b. column names
                null,                          // selections
                null,  // selections args
                null, // e. group by
                null, // f. having
                "ID desc", // g. order by
                null);
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            tempId=cursor.getInt(0);
            tempEmail=cursor.getString(1);
            cursor.close();
            myDBHelper.close();
            return true;
        }
        cursor.close();
        myDBHelper.close();
        return false;
    }
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements View.OnClickListener{

        private Handler mUIHandler = new Handler();
        private HandlerThread mThread;
        private Handler mThreadHandler;
        private Handler mUIHandler2 = new Handler();
        private HandlerThread mThread2;
        private Handler mThreadHandler2;
        EditText email,password;
        Button login2,register2;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_login, container, false);
            mThread = new HandlerThread("net");
            mThread.start();
            mThreadHandler = new Handler(mThread.getLooper());
            mThread2 = new HandlerThread("net");
            mThread2.start();
            mThreadHandler2 = new Handler(mThread2.getLooper());
            login2 = (Button)rootView.findViewById(R.id.login);
            register2 = (Button)rootView.findViewById(R.id.register);
            login2.setOnClickListener(this);
            register2.setOnClickListener(this);
            email=(EditText)rootView.findViewById(R.id.email);
            password=(EditText)rootView.findViewById(R.id.password);
            updateTable();
            return rootView;
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.login){
                login2.setClickable(false);
                register2.setClickable(false);
                //====================================登入判斷
                //使用遠端資料庫
                if(mThreadHandler != null){
                    mThreadHandler.removeCallbacksAndMessages(null);
                }
                mThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        DataRetrieve d = new DataRetrieve();
                        final ArrayList<HashMap<String, Object>> result = d.login(email.getText().toString(), password.getText().toString());
                        if(result != null) {
                            mUIHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    MainActivity2Activity m2=(MainActivity2Activity)getActivity();
                                    String s1 = result.get(0).get("ID").toString();
                                    m2.tempId=Integer.valueOf(s1);
                                    String s2 = result.get(0).get("Account").toString();
                                    m2.tempEmail=s2;
                                    store();
                                    login2.setClickable(true);
                                    register2.setClickable(true);
                                    m2.changeAcitvity();
                                }
                            });
                        }
                        else{
                            Toast.makeText(getActivity(), "輸入錯誤!", Toast.LENGTH_SHORT).show();
                            login2.setClickable(true);
                            register2.setClickable(true);
                        }
                    }
                });
                //====================================
//                if(valid(email.getText().toString(),password.getText().toString())==true) {
//                    MainActivity2Activity parent = (MainActivity2Activity) this.getActivity();
//                    parent.useStore();
//                    parent.changeAcitvity();
//                }
//                else{
//                    Toast.makeText(getActivity(),"輸入錯誤",Toast.LENGTH_SHORT).show();
//                }
            }
            else if(v.getId() == R.id.register){
                MainActivity2Activity parent = (MainActivity2Activity)this.getActivity();
                parent.switchFragment(1);
            }
        }
//        private boolean valid(String e,String p){
//            MyDBHelper myDBHelper=new MyDBHelper(this.getActivity());
//            SQLiteDatabase db=myDBHelper.getReadableDatabase();
//            Cursor cursor =db.query("User", // a. table
//                    new String[] {"ID", "Email","Password"}, // b. column names
//                    "Email = ? and Password=?",                          // selections
//                    new String[] {e,p},  // selections args
//                    null, // e. group by
//                    null, // f. having
//                    "ID desc", // g. order by
//                    null);
//            if(cursor.getCount()>0){
//                cursor.moveToFirst();
//                store(cursor.getInt(0),cursor.getString(1));
//                cursor.close();
//                myDBHelper.close();
//                return true;
//            }
//            cursor.close();
//            myDBHelper.close();
//            return false;
//        }

        private void store(){
            MainActivity2Activity m2=(MainActivity2Activity)getActivity();
            MyDBHelper myDBHelper=new MyDBHelper(this.getActivity());
            SQLiteDatabase db=myDBHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id",m2.tempId);
            values.put("Email",m2.tempEmail);
            db.insert("Temp",null,values);
            db.close();
            myDBHelper.close();
        }

        public void updateTable(){
            //================================先清除資料表
            try {
                MyDBHelper myDBHelper = new MyDBHelper(getActivity());
                SQLiteDatabase db = myDBHelper.getWritableDatabase();
                db.execSQL("DELETE FROM User");
                db.close();
                myDBHelper.close();
            }
            catch (Exception e){
                Toast.makeText(getActivity(),"ERROR",Toast.LENGTH_SHORT).show();
            }
            //===================================
            if(mThreadHandler2 != null){
                mThreadHandler2.removeCallbacksAndMessages(null);
            }
            mThreadHandler2.post(new Runnable() {
                @Override
                public void run() {
                    DataRetrieve d = new DataRetrieve();
                    try {
                        final ArrayList<HashMap<String, Object>> result = d.getAllUser();
                        if (result != null) {
                            mUIHandler2.post(new Runnable() {
                                @Override
                                public void run() {
                                    for(int i=0;i<result.size();i++){
                                        MyDBHelper myDBHelper = new MyDBHelper(getActivity());
                                        SQLiteDatabase db = myDBHelper.getWritableDatabase();
                                        ContentValues values=new ContentValues();
                                        values.put("ID",result.get(i).get("ID").toString());
                                        values.put("Email",result.get(i).get("Account").toString());
                                        values.put("Name",result.get(i).get("Name").toString());
                                        values.put("Password",result.get(i).get("Password").toString());
                                        byte[] bytes=Base64.decode(result.get(i).get("Icon").toString(), Base64.DEFAULT);
                                        values.put("Icon",bytes);
                                        db.insert("User", null, values);
                                        Toast.makeText(getActivity(),result.get(i).get("ID").toString()+"\n"+result.get(i).get("Account").toString()+"\n"+result.get(i).get("Name").toString()+"\n"+result.get(i).get("Icon").toString(),Toast.LENGTH_SHORT).show();
                                        db.close();
                                        myDBHelper.close();
                                    }
                                }
                            });
                            mThread2.join();
                            Toast.makeText(getActivity(),"結束",Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(getActivity(), "no status!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (Exception e){
                        Log.e("error", e.toString());
                    }
                }
            });
        }
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            new AlertDialog.Builder(MainActivity2Activity.this)
                    .setTitle("確認視窗")
                    .setMessage("確定要結束應用程式嗎?")
                            //.setIcon(R.drawable.ic_launcher)
                    .setPositiveButton("確定",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    finish();
                                }
                            })
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub

                                }
                            }).show();
        }
        return true;
    }

}
