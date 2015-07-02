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
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.Fragment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddFragment extends Fragment {

    Bitmap viewImage=null;

    private Button b,rotateButton;
    private Button testAdd,testUse,open;
    private Button t1,t2;
    private ImageView picture;
    static int TAKE_PICTURE = 1;
    private String filename;
    Uri fileUri;
    static int PHOTO=2;

    private HandlerThread mThread;
    private Handler mThreadHandler;

    public AddFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_add, container, false);

        picture=(ImageView)rootView.findViewById(R.id.addIcon);
        open=(Button)rootView.findViewById(R.id.button3);
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,PHOTO);
            }
        });
        b=(Button)rootView.findViewById(R.id.button5);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
        rotateButton=(Button)rootView.findViewById(R.id.rotateButton);
        rotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewImage!=null) {
                    Bitmap bitmap;
                    bitmap = viewImage;


                    // create new matrix
                    Matrix matrix = new Matrix();
                    // setup rotation degree
                    matrix.postRotate(90);
                    Bitmap bt = Bitmap.createBitmap(bitmap,0,0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    viewImage=bt;
                    picture.setImageBitmap(viewImage);
                }
            }
        });
        testAdd=(Button)rootView.findViewById(R.id.button6);
        testAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] data;
                data=iconToByte();
                if(data!=null) {
                    MyDBHelper dbHelper = new MyDBHelper(getActivity());
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    MainActivity m=(MainActivity)getActivity();
                    ContentValues values = new ContentValues();
                    values.put("Icon", data);
                    db.update("User",values,"ID=?",new String[]{String.valueOf(m.tempId)});
                    db.close();
                    dbHelper.close();

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
                                MainActivity m=(MainActivity)getActivity();
                                m.onNavigationDrawerItemSelected(0);
                            }
                            else{
                                Toast.makeText(getActivity(), "failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else
                    Toast.makeText(getActivity(),"沒圖片新增失敗",Toast.LENGTH_SHORT).show();
            }
        });
        viewImage=getByteToBitmap();
        picture.setImageBitmap(viewImage);

        mThread = new HandlerThread("net");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
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
                viewImage = MediaStore.Images.Media.getBitmap(m.getContentResolver(), fileUri);
                viewImage=comp(viewImage);
                picture.setImageBitmap(viewImage);
            }
            catch(Exception e){

            }
        }
        else if(requestCode==PHOTO){
            try {
                MainActivity m = (MainActivity) getActivity();
                fileUri = data.getData();
                viewImage = MediaStore.Images.Media.getBitmap(m.getContentResolver(), fileUri);
                viewImage = comp(viewImage);
                picture.setImageBitmap(viewImage);
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
            viewImage = MediaStore.Images.Media.getBitmap(m.getContentResolver(), fileUri);
            viewImage=comp(viewImage);
            picture.setImageBitmap(viewImage);
        }
        catch(Exception e){

        }
    }

    public byte[] iconToByte(){
        if(viewImage!=null) {
            Bitmap bmp = viewImage;
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
        MainActivity m=(MainActivity)getActivity();
        MyDBHelper dbHelper = new MyDBHelper(this.getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor=db.query("User",
                new String[]{"ID","Icon"},
                "ID = ?",
                new String[]{m.tempId},
                null,
                null,
                null);
        if(cursor!=null && cursor.getCount()>0){
            cursor.moveToFirst();
            b=cursor.getBlob(1);
            bitmap=BitmapFactory.decodeByteArray(b,0,b.length);
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