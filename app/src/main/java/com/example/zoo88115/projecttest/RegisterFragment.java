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
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegisterFragment extends Fragment implements View.OnClickListener{
    public EditText i1, i2, i3, i4;
    public String e;
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
        return rootView;
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.btnCheck){
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
                values.put("Name", i4.getText().toString());
                values.put("Email", i1.getText().toString());
                values.put("Password", i2.getText().toString());
                values.put("Icon",bitMapData);
                db.insert("User", null, values);
                Toast.makeText(v.getContext(), "註冊成功", Toast.LENGTH_SHORT).show();
                MainActivity2Activity p = (MainActivity2Activity)this.getActivity();
                p.switchFragment(0);//返回登入頁面
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
}
