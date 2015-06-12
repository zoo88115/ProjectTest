package com.example.zoo88115.projecttest;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddFragment extends Fragment {

    float angle=0;
    private EditText n;
    private Button b,rotateButton;
    private Button testAdd,testUse,open;
    private Button t1,t2;
    private ImageView picture,testPic;
    static int TAKE_PICTURE = 1;
    private String filename;
    Uri fileUri;
    static int PHOTO=2;

    public AddFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_add, container, false);
        t1=(Button)rootView.findViewById(R.id.button4);
        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] data;
                data=iconToByte();
                if(data!=null && n.getText().toString().equals("")!=true) {
                    MyDBHelper dbHelper = new MyDBHelper(getActivity());
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("TestIcon", data);
                    db.insert("Test", null, values);
                    db.close();
                    dbHelper.close();
                    Toast.makeText(getActivity(),"新增成功",Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(getActivity(),"沒圖片新增失敗",Toast.LENGTH_SHORT).show();
                }
        });
        testPic=(ImageView)rootView.findViewById(R.id.imageView2);
        picture=(ImageView)rootView.findViewById(R.id.addIcon);
        //testPic=(ImageView)rootView.findViewById(R.id.imageView2);
        open=(Button)rootView.findViewById(R.id.button3);
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        b=(Button)rootView.findViewById(R.id.button5);
        n=(EditText)rootView.findViewById(R.id.addName);
        rotateButton=(Button)rootView.findViewById(R.id.rotateButton);
        rotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(picture.getDrawable()!=null) {
                    Bitmap bitmap;
                    picture.setDrawingCacheEnabled(true);
                    bitmap = picture.getDrawingCache();


                    // create new matrix
                    Matrix matrix = new Matrix();
                    // setup rotation degree
                    angle+=90;
                    if(angle==360)
                        angle=0;
                    matrix.postRotate(angle);
                    Bitmap bt = Bitmap.createBitmap(bitmap,0,0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    picture.setImageBitmap(bt);
                    picture.setDrawingCacheEnabled(false);
                }
            }
        });
        testAdd=(Button)rootView.findViewById(R.id.button6);
        testAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] data;
                data=iconToByte();
                if(data!=null && n.getText().toString().equals("")!=true) {
                    MyDBHelper dbHelper = new MyDBHelper(getActivity());
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("Name",n.getText().toString());
                    values.put("Icon", data);
                    db.insert("User", null, values);
                    db.close();
                    dbHelper.close();
                    Toast.makeText(getActivity(),"新增成功",Toast.LENGTH_SHORT).show();
                    MainActivity m=(MainActivity)getActivity();
                    m.onNavigationDrawerItemSelected(0);
                }
                else
                    Toast.makeText(getActivity(),"沒圖片新增失敗",Toast.LENGTH_SHORT).show();
            }
        });
        //下面是使用資料庫圖片
        t2=(Button)rootView.findViewById(R.id.button7);
        t2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap;
                bitmap=getByteToBitmap();
                if(bitmap!=null){
                    testPic.setImageBitmap(bitmap);
                }
                else
                    Toast.makeText(getActivity(),"no data",Toast.LENGTH_SHORT).show();
            }
        });
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
        return rootView;
    }

    public void takePicture() {
        // 啟動相機元件用的Intent物件

        Intent intentCamera =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String fileName=FileUtil.getUniqueFileName()+".jpg";
        File pictureFile = new File(FileUtil.getExternalStorageDir(FileUtil.APP_DIR),fileName);
        fileUri=Uri.fromFile(pictureFile);
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
        startActivityForResult(intentCamera, TAKE_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==TAKE_PICTURE && resultCode==MainActivity.RESULT_OK){
            try {
                MainActivity m = (MainActivity) getActivity();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(m.getContentResolver(), fileUri);
                picture.setImageBitmap(bitmap);
                angle=0;
            }
            catch(Exception e){

            }
        }
    }

    public byte[] iconToByte(){
        if(picture.getDrawable()!=null) {
            picture.setDrawingCacheEnabled(true);
            Bitmap bmp = picture.getDrawingCache();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte bytes[] = stream.toByteArray();
            picture.setDrawingCacheEnabled(false);
            return bytes;
        }
        return null;
    }
    public Bitmap getByteToBitmap(){
        byte[] b;
        Bitmap bitmap;
        MyDBHelper dbHelper = new MyDBHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor=db.query("Test",
                new String[]{"TestIcon"},
                null,
                null,
                null,
                null,
                null
        );
        if(cursor!=null && cursor.getCount()>0){
            cursor.moveToLast();
            b=cursor.getBlob(0);
            bitmap=BitmapFactory.decodeByteArray(b,0,b.length);
            db.close();
            dbHelper.close();
            return bitmap;
        }
        db.close();
        dbHelper.close();
        return null;
    }
}