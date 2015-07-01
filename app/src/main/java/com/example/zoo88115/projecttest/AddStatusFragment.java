package com.example.zoo88115.projecttest;


import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddStatusFragment extends Fragment {
    static int TAKE_PICTURE = 1;
    private String filename;
    Uri fileUri;
    static int PHOTO=2;
    public Button post,addPohot,takePhoto;
    public ImageView photo;
    public EditText status;
    public Spinner chooseUser;
    public Bitmap bitmap2=null;
    public String[] user;
    public Integer[] userId;
    public ArrayAdapter<String> arrayAdapter;
    public EditText addTime;
    Button t;
    EditText tp;

    public AddStatusFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_add_status, container, false);
        addPohot=(Button)view.findViewById(R.id.button8);
        addPohot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,PHOTO);
            }
        });
        tp=(EditText)view.findViewById(R.id.editText3);
        addTime=(EditText)view.findViewById(R.id.addTime);
        takePhoto=(Button)view.findViewById(R.id.button9);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
        post=(Button)view.findViewById(R.id.button10);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    java.util.Date date = new java.util.Date();
                    MyDBHelper myDBHelper = new MyDBHelper(getActivity());
                    SQLiteDatabase db = myDBHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    if(bitmap2!=null) {
                        //===================圖片轉byte
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap2.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte bytes[] = stream.toByteArray();
                        //====================
                        values.put("Photo", bytes);
                    }
                    values.put("Time",date.getTime()+Integer.valueOf(addTime.getText().toString())*1000);
                    if(bitmap2==null)
                    values.put("Content", status.getText().toString());
                    values.put("UserID", userId[chooseUser.getSelectedItemPosition()]);
                    db.insert("Status", null, values);
                    db.close();
                    myDBHelper.close();
                }
                catch(Exception e){
                    Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });
        photo=(ImageView)view.findViewById(R.id.imageView);
        photo.setVisibility(View.GONE);//不保留空間
        status=(EditText)view.findViewById(R.id.editText2);
        chooseUser=(Spinner)view.findViewById(R.id.chooseUser);
        Readdata();
        arrayAdapter=new ArrayAdapter<String>(getActivity(),R.layout.support_simple_spinner_dropdown_item,user);
        chooseUser.setAdapter(arrayAdapter);
        return view;
    }

    public void Readdata(){
        ArrayList<String> user2 = new ArrayList<String>();
        ArrayList<Integer> userId2=new ArrayList<Integer>();
        MyDBHelper myDBHelper=new MyDBHelper(getActivity());
        SQLiteDatabase db=myDBHelper.getReadableDatabase();
        Cursor cursor=db.query("User",
                new String[]{"ID","Name"},
                null,
                null,
                null,
                null,
                null
        );
        if(cursor!=null && cursor.getCount()>0){
            cursor.moveToFirst();
            for(int i=0;i<cursor.getCount();i++){
                userId2.add(cursor.getInt(0));
                user2.add(cursor.getString(1));
                cursor.moveToNext();
            }
        }
        user=user2.toArray(new String[user2.size()]);
        userId=(Integer[]) userId2.toArray(new Integer[userId2.size()]);
    }

    public void takePicture() {
        // 啟動相機元件用的Intent物件

        Intent intentCamera =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String fileName=FileUtil.getUniqueFileName()+".jpg";
        File pictureFile = new File(FileUtil.getExternalStorageDir(FileUtil.APP_DIR),fileName);
        fileUri= Uri.fromFile(pictureFile);
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
        startActivityForResult(intentCamera, TAKE_PICTURE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==TAKE_PICTURE && resultCode==MainActivity.RESULT_OK){
            try {
                MainActivity m = (MainActivity) getActivity();
                bitmap2 = MediaStore.Images.Media.getBitmap(m.getContentResolver(), fileUri);
                bitmap2=comp(bitmap2);
                photo.setImageBitmap(bitmap2);
                photo.setVisibility(View.VISIBLE);
                ViewGroup.LayoutParams params = photo.getLayoutParams();
                params.width = ActionBar.LayoutParams.MATCH_PARENT;
                params.height = 400;
                photo.setLayoutParams(params);
            }
            catch(Exception e){

            }
        }
        else if(requestCode==PHOTO){
            try {
                MainActivity m = (MainActivity) getActivity();
                fileUri = data.getData();
                bitmap2 = MediaStore.Images.Media.getBitmap(m.getContentResolver(), fileUri);
                bitmap2 = comp(bitmap2);
                photo.setImageBitmap(bitmap2);
                photo.setVisibility(View.VISIBLE);
                ViewGroup.LayoutParams params = photo.getLayoutParams();
                params.width = ActionBar.LayoutParams.MATCH_PARENT;
                params.height = 400;
                photo.setLayoutParams(params);
            }
            catch(Exception e){

            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        try {
            MainActivity m = (MainActivity) getActivity();
            bitmap2 = MediaStore.Images.Media.getBitmap(m.getContentResolver(), fileUri);
            bitmap2=comp(bitmap2);
            photo.setImageBitmap(bitmap2);
        }
        catch(Exception e){

        }
    }

    public byte[] iconToByte(){
        if(bitmap2!=null) {
            Bitmap bmp = bitmap2;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte bytes[] = stream.toByteArray();
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
            bitmap= BitmapFactory.decodeByteArray(b, 0, b.length);
            db.close();
            dbHelper.close();
            return bitmap;
        }
        db.close();
        dbHelper.close();
        return null;
    }

    private Bitmap comp(Bitmap image) {//图片按比例大小压缩方法

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if( baos.toByteArray().length / 1024>0.5) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }

    private Bitmap compressImage(Bitmap image) {//质量压缩方法

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while ( baos.toByteArray().length / 1024>100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }
}
