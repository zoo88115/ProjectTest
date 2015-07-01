package com.example.zoo88115.projecttest;


import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class PostNewsFragment extends Fragment implements LocationListener{

    static int TAKE_PICTURE = 1;
    private String filename;
    Uri fileUri;
    static int PHOTO=2;
    public Bitmap bitmap2=null;
    TextView myLocation;
    public static Location currentLocation;
    public static LocationManager myLocationManager;
    private Handler mUIHandler = new Handler();
    private HandlerThread mThread;
    private Handler mThreadHandler;
    String encoded=null,tempLocation=null;
    java.util.Date date = new java.util.Date();


    Button adPhoto,post,takeCamera,rotateButton,addLocation;
    LinearLayout l;
    ImageView imageView;
    EditText editText;
    public PostNewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_post_news, container, false);
        myLocationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        mThread = new HandlerThread("net");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        addLocation=(Button)v.findViewById(R.id.addLocation);
        myLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,100,0,this);
        myLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 0,this);
        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(currentLocation != null) {
                        double d1, d2;
                        d1 = currentLocation.getLongitude();
                        d2 = currentLocation.getLatitude();

                        Geocoder geo = new Geocoder(getActivity(), Locale.TAIWAN);
                        try {
                            List<Address> addrs = geo.getFromLocation(d2, d1, 1);

                            if (addrs.size() != 0) {
                                myLocation.setText("From：" + addrs.get(0).getLocality());
                                tempLocation=addrs.get(0).getLocality();
                            } else {
                                Toast.makeText(getActivity(), "請位於良好通訊位置", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("error", e.toString());
                        }
                    }
                    else{
                        Toast.makeText(getActivity(), "請開啟訂位服務，或請位於良好通訊位置", Toast.LENGTH_SHORT).show();
                    }
                }
        });
        myLocation=(TextView)v.findViewById(R.id.myLocation);
        imageView=(ImageView)v.findViewById(R.id.imageView2);
        adPhoto=(Button)v.findViewById(R.id.button);
        adPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,PHOTO);
            }
        });
        post=(Button)v.findViewById(R.id.button2);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().toString().equals("")==false || bitmap2!=null) {
                    try {
                        MyDBHelper myDBHelper = new MyDBHelper(getActivity());
                        SQLiteDatabase db = myDBHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        if (bitmap2 != null) {
                            //===================圖片轉byte
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap2.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            byte bytes[] = stream.toByteArray();
                            //====================
                            values.put("Photo", bytes);
                        }
                        values.put("Time", date.getTime() + (Integer.valueOf("10".toString()) * 1000));//設設延遲五秒
                        Toast.makeText(getActivity(),""+date.getTime(),Toast.LENGTH_SHORT).show();
                        if (editText.getText().toString().equals("") == false)
                            values.put("Content", editText.getText().toString());
                        MainActivity m = (MainActivity) getActivity();
                        values.put("UserID", m.tempId);
                        values.put("Location", tempLocation);
                        db.insert("Status", null, values);
                        db.close();
                        //=================遠端發文================================
                        DataStore dataStore=new DataStore();
                        if(bitmap2 != null) {
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap2.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            byte bytes[] = stream.toByteArray();
                            encoded = Base64.encodeToString(bytes, Base64.DEFAULT);
                        }
                        //存入 內文 id 圖片 時間========
                        if(mThreadHandler != null){
                            mThreadHandler.removeCallbacksAndMessages(null);
                        }
                        mThreadHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                DataStore d = new DataStore();
                                MainActivity m=(MainActivity)getActivity();
                                String encodeResult = null;
                                try {
                                    final boolean result = d.postArticle(editText.getText().toString(),Integer.valueOf(m.tempId),encoded,String.valueOf(date.getTime()),tempLocation);
                                    if (result) {
                                        Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getActivity(), "failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                catch (Exception e){
                                    Log.e("error", e.toString());
                                }
                            }
                        });
                        //===========================

                        //========================================================================
                        myDBHelper.close();
                        m.onNavigationDrawerItemSelected(0);
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
                else
                    Toast.makeText(getActivity(),"沒有內文或圖片",Toast.LENGTH_SHORT).show();
            }
        });
        rotateButton=(Button)v.findViewById(R.id.button4);
        rotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bitmap2!=null) {
                    Bitmap bitmap;
                    bitmap = bitmap2;


                    // create new matrix
                    Matrix matrix = new Matrix();
                    // setup rotation degree
                    matrix.postRotate(90);
                    Bitmap bt = Bitmap.createBitmap(bitmap,0,0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    bitmap2=bt;
                    imageView.setImageBitmap(bitmap2);
                }
            }
        });
        takeCamera=(Button)v.findViewById(R.id.button7);
        takeCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
        l=(LinearLayout)v.findViewById(R.id.linear2);
        l.setVisibility(View.GONE);
        editText=(EditText)v.findViewById(R.id.editText);

        return v;
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
                imageView.setImageBitmap(bitmap2);
                ViewGroup.LayoutParams params = imageView.getLayoutParams();
                params.width = ActionBar.LayoutParams.MATCH_PARENT;
                params.height = 400;
                imageView.setLayoutParams(params);
                l.setVisibility(View.VISIBLE);
            }
            catch(Exception e){
                l.setVisibility(View.GONE);
            }
        }
        else if(requestCode==PHOTO){
            try {
                MainActivity m = (MainActivity) getActivity();
                fileUri = data.getData();
                bitmap2 = MediaStore.Images.Media.getBitmap(m.getContentResolver(), fileUri);
                bitmap2 = comp(bitmap2);
                imageView.setImageBitmap(bitmap2);
                ViewGroup.LayoutParams params = imageView.getLayoutParams();
                params.width = ActionBar.LayoutParams.MATCH_PARENT;
                params.height = 400;
                imageView.setLayoutParams(params);
                l.setVisibility(View.VISIBLE);
            }
            catch(Exception e){
                l.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.e("fragment : ", "resume");
        myLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        myLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        try {
            MainActivity m = (MainActivity) getActivity();
            bitmap2 = MediaStore.Images.Media.getBitmap(m.getContentResolver(), fileUri);
            bitmap2=comp(bitmap2);
            imageView.setImageBitmap(bitmap2);
        }
        catch(Exception e){

        }
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.e("fragment : ", "pause");
        myLocationManager.removeUpdates(this);
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

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
