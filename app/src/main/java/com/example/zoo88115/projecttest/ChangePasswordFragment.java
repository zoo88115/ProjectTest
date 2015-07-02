package com.example.zoo88115.projecttest;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class ChangePasswordFragment extends Fragment implements View.OnClickListener{
    EditText t1,t2,t3;
    private HandlerThread mThread;
    private Handler mThreadHandler;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_change_password, container, false);

        Button btn1=(Button)rootView.findViewById(R.id.checkBtn);
        Button btn2=(Button)rootView.findViewById(R.id.returnSetting);
        t1=(EditText)rootView.findViewById(R.id.oldPassword);
        t2=(EditText)rootView.findViewById(R.id.newPassword);
        t3=(EditText)rootView.findViewById(R.id.newPasswordCheck);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        mThread = new HandlerThread("net");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        return rootView;
    }

    @Override
    public void onClick(View v){
        if(v.getId()==R.id.checkBtn){
            if(ifOldPasswordCorrect()==true){
                if(ifEqual()==true){
                    updatePassword();
                    Toast.makeText(getActivity(), "更新成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else if(v.getId()==R.id.returnSetting){
            MainActivity p=(MainActivity)getActivity();
            p.onNavigationDrawerItemSelected(0);
        }
    }

    private boolean ifOldPasswordCorrect(){
        MainActivity p=(MainActivity)getActivity();
        MyDBHelper dbHelper = new MyDBHelper(this.getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor =
                db.query("User", // a. table
                        new String[] {"Email", "Password"}, // b. column names
                        "Email = ? and Password=?",                          // selections
                        new String[] {p.tempEmail,t1.getText().toString()},  // selections args
                        null, // e. group by
                        null, // f. having
                        "ID desc", // g. order by
                        null); // h. limit
        if(cursor != null && cursor.getCount() > 0){
            db.close();
            dbHelper.close();
            return true;
        }
        Toast.makeText(getActivity(), "舊密碼輸入錯誤", Toast.LENGTH_SHORT).show();
        db.close();
        dbHelper.close();
        return false;
    }
    private boolean ifEqual(){
        if(t2.getText().toString().equals(t3.getText().toString()) && !t2.getText().equals("")){
            return true;
        }
        Toast.makeText(getActivity(), "新密碼輸入不相符", Toast.LENGTH_SHORT).show();
        return false;
    }

    private void updatePassword(){
        MainActivity p=(MainActivity)getActivity();
        MyDBHelper dbHelper = new MyDBHelper(this.getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Password",t2.getText().toString());
        db.update("User", values, "ID = ?", new String[]{p.tempId});  //給條件 mail相同
        db.close();
        dbHelper.close();

        MainActivity m=(MainActivity)getActivity();
        MyDBHelper dbHelper2=new MyDBHelper(getActivity());
        SQLiteDatabase db2=dbHelper2.getReadableDatabase();
        Cursor cursor2 = db2.query("User",
                new String[]{"ID","Email","Password","Icon","Name"},
                "ID=? ",
                new String[]{m.tempId},
                null,
                null,
                null);
        cursor2.moveToFirst();
        final int tid=cursor2.getInt(0);
        final String tmail=cursor2.getString(1);
        final String tpa=cursor2.getString(2);
        byte[] tb=cursor2.getBlob(3);
        final String base= Base64.encodeToString(tb, Base64.DEFAULT);
        final String tn=cursor2.getString(4);

        if(mThreadHandler != null){
            mThreadHandler.removeCallbacksAndMessages(null);
        }
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                DataStore d = new DataStore();
                String encodeResult = null;
                final boolean result = d.updateUser(tid, tmail,tpa , base, tn);
                if(result){
                    Toast.makeText(getActivity(), "更新成功", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getActivity(), "failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}