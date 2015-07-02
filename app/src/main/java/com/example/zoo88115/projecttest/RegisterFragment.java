package com.example.zoo88115.projecttest;

import android.content.ContentValues;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegisterFragment extends Fragment implements View.OnClickListener{
    public EditText i1, i2, i3, i4;
    public String e;
    private Handler mUIHandler = new Handler();
    private HandlerThread mThread;
    private Handler mThreadHandler;
    private Handler mUIHandler2 = new Handler();
    private HandlerThread mThread2;
    private Handler mThreadHandler2;
    String encoded;
    public RegisterFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);
        i1 = (EditText)rootView.findViewById(R.id.editEmail);
        i2 = (EditText)rootView.findViewById(R.id.editPassword);
        i3 = (EditText)rootView.findViewById(R.id.editPasswordCheck);
        i4 = (EditText)rootView.findViewById(R.id.editName);
        Button register = (Button)rootView.findViewById(R.id.btnCheck);
        register.setOnClickListener(this);
        Button clear = (Button)rootView.findViewById(R.id.btnClear);
        clear.setOnClickListener(this);
        Button returnMain = (Button)rootView.findViewById(R.id.returnMain);
        returnMain.setOnClickListener(this);
        mThread = new HandlerThread("net");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThread2 = new HandlerThread("net");
        mThread2.start();
        mThreadHandler2 = new Handler(mThread2.getLooper());
        return rootView;
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.btnCheck){
            //=============
           updateTable();
            //=============
            e = "";
            MyDBHelper dbHelper = new MyDBHelper(this.getActivity());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            e = isEmpty();
            if(!e.equals("\n")){
                Toast.makeText(this.getActivity().getApplicationContext(), e, Toast.LENGTH_SHORT).show();
            }
            else if(isValidEmail()==false){
                Toast.makeText(v.getContext(), "帳號格式錯誤!", Toast.LENGTH_SHORT).show();
            }
            else if(isSame()==false){
                Toast.makeText(v.getContext(), "密碼不一致!", Toast.LENGTH_SHORT).show();
            }
            else if(ifExist(i1.getText().toString())){
                Toast.makeText(v.getContext(), "此帳號已存在!!", Toast.LENGTH_SHORT).show();
            }
            else{
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.defaulticon);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] bitMapData = stream.toByteArray();
                encoded = Base64.encodeToString(bitMapData, Base64.DEFAULT);
                values.put("Name", i4.getText().toString());
                values.put("Email", i1.getText().toString());
                values.put("Password", i2.getText().toString());
                values.put("Icon",bitMapData);
                db.insert("User", null, values);
                //==============遠端資料庫更新====================
                if(mThreadHandler != null){
                    mThreadHandler.removeCallbacksAndMessages(null);
                }
                mThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        DataStore d = new DataStore();
                        String encodeResult = null;
                        final boolean result = d.register(i1.getText().toString(), i2.getText().toString(), encoded, i4.getText().toString());
                        if(result){
                            Toast.makeText(getActivity(), "註冊成功", Toast.LENGTH_SHORT).show();
                            MainActivity2Activity p = (MainActivity2Activity)getActivity();
                            p.switchFragment(0);//返回登入頁面
                        }
                        else{
                            Toast.makeText(getActivity(), "failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //================================================
            }
            db.close();
            dbHelper.close();

        }
        else if(v.getId() == R.id.btnClear){
            i1.setText("");
            i2.setText("");
            i3.setText("");
            i4.setText("");
        }
        else if(v.getId()==R.id.returnMain){
            MainActivity2Activity p = (MainActivity2Activity)this.getActivity();
            p.switchFragment(0);//返回登入頁面
        }
    }
    public boolean ifExist(String account) {
        MyDBHelper dbHelper = new MyDBHelper(this.getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor =
                db.query("User", // a. table
                        new String[] {"ID", "Email"}, // b. column names
                        "Email = ?",                          // selections
                        new String[] {account},  // selections args
                        null, // e. group by
                        null, // f. having
                        "ID desc", // g. order by
                        null); // h. limit

        Toast.makeText(getActivity(),String.valueOf(cursor.getCount()),Toast.LENGTH_SHORT).show();
        if (cursor != null && cursor.getCount() > 0) {
            db.close();
            dbHelper.close();
            return true;
        }
        db.close();
        dbHelper.close();
        return false;
    }
    public String isEmpty(){
        String error = "\n";
        if(i1.getText().toString().equals("")){
            error += "請輸入帳號\n";
        }
        if(i2.getText().toString().equals("")){
            error += "請輸入密碼\n";
        }
        if(i3.getText().toString().equals("")){
            error += "請確認密碼\n";
        }
        if(i4.getText().toString().equals("")){
            error += "請輸入姓名\n";
        }
        return error;
    }
    public boolean isSame(){
        if(i3.getText().toString().equals(i2.getText().toString())){
            return true;
        }
        return false;
    }
    private boolean isValidEmail(){
        String EMAIL_PATTERN="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern=Pattern.compile(EMAIL_PATTERN);
        Matcher matcher=pattern.matcher(i1.getText().toString());
        return matcher.matches();

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
