package com.example.zoo88115.projecttest;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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
            else
                changeAcitvity();
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
        Intent myIntent = new Intent(this, MainActivity.class);
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

        EditText email,password;
        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_login, container, false);
            Button login = (Button)rootView.findViewById(R.id.login);
            Button register = (Button)rootView.findViewById(R.id.register);
            login.setOnClickListener(this);
            register.setOnClickListener(this);
            email=(EditText)rootView.findViewById(R.id.email);
            password=(EditText)rootView.findViewById(R.id.password);
            return rootView;
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.login){
                if(valid(email.getText().toString(),password.getText().toString())==true) {
                    MainActivity2Activity parent = (MainActivity2Activity) this.getActivity();
                    parent.changeAcitvity();
                }
                else{
                    Toast.makeText(getActivity(),"輸入錯誤",Toast.LENGTH_SHORT).show();
                }
            }
            else if(v.getId() == R.id.register){
                MainActivity2Activity parent = (MainActivity2Activity)this.getActivity();
                parent.switchFragment(1);
            }
        }
        private boolean valid(String e,String p){
            MyDBHelper myDBHelper=new MyDBHelper(this.getActivity());
            SQLiteDatabase db=myDBHelper.getReadableDatabase();
            Cursor cursor =db.query("User", // a. table
                    new String[] {"ID", "Email","Password"}, // b. column names
                    "Email = ? and Password=?",                          // selections
                    new String[] {e,p},  // selections args
                    null, // e. group by
                    null, // f. having
                    "ID desc", // g. order by
                    null);
            if(cursor.getCount()>0){
                cursor.moveToFirst();
                store(cursor.getInt(0),cursor.getString(1));
                cursor.close();
                myDBHelper.close();
                return true;
            }
            cursor.close();
            myDBHelper.close();
            return false;
        }

        private void store(int id,String email){
            MyDBHelper myDBHelper=new MyDBHelper(this.getActivity());
            SQLiteDatabase db=myDBHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id",id);
            values.put("Email",email);
            db.insert("Temp",null,values);
            db.close();
            myDBHelper.close();
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
