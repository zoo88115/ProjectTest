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
public class ChangeNameFragment extends Fragment implements View.OnClickListener{
    EditText changeName;
    Button btn1,btn2;
    private HandlerThread mThread;
    private Handler mThreadHandler;

    public ChangeNameFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        MainActivity parent = (MainActivity)this.getActivity();
        View rootView=inflater.inflate(R.layout.fragment_change_name, container, false);
        changeName=(EditText)rootView.findViewById(R.id.editChangeName);
        changeName.setText(getName(parent.tempEmail));
        btn1=(Button)rootView.findViewById(R.id.changeNameCheck);
        btn2=(Button)rootView.findViewById(R.id.changeNameReturn);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        mThread = new HandlerThread("net");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        return rootView;
    }

    public String getName(String m){
        String a;
        MyDBHelper dbHelper = new MyDBHelper(this.getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor =
                db.query("User", // a. table
                        new String[] {"Name,Email"}, // b. column names
                        "Email = ?",                          // selections
                        new String[] {m},  // selections args
                        null, // e. group by
                        null, // f. having
                        "ID desc", // g. order by
                        null); // h. limit
        cursor.moveToFirst();
        a= cursor.getString(0);
        db.close();
        dbHelper.close();
        return a;
    }

    @Override
    public void onClick(View v){
        if(v.getId()==R.id.changeNameCheck){
            updateName();
            Toast.makeText(v.getContext(), "更新成功", Toast.LENGTH_SHORT).show();
        }else if(v.getId()==R.id.changeNameReturn){
            MainActivity parent = (MainActivity)this.getActivity();
            parent.onNavigationDrawerItemSelected(0);
        }
    }

    public void updateName(){
        MainActivity parent = (MainActivity)this.getActivity();
        MyDBHelper dbHelper = new MyDBHelper(this.getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Name", changeName.getText().toString());
        db.update("User", values, "Email = ?", new String[]{parent.tempEmail});  //給條件 mail相同
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

        if(mThreadHandler != null){
            mThreadHandler.removeCallbacksAndMessages(null);
        }
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                DataStore d = new DataStore();
                String encodeResult = null;
                final boolean result = d.updateUser(tid, tmail,tpa , base, changeName.getText().toString());
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
