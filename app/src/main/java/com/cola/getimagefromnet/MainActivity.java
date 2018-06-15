package com.cola.getimagefromnet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;

public class MainActivity extends AppCompatActivity {

    private ImageView mImageView;
    private static final String stringUrl = "http://img.my.csdn.net/uploads/201508/05/1438760726_8364.jpg";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = findViewById(R.id.image_view_iv);
        Log.e("tag", "onCreate");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bitmap = getImageFromNet(stringUrl);
                    if (bitmap != null){
                        mImageView.setImageBitmap(bitmap);
                    }else {
                        Log.e("tag", "null");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * 获取网络数据，直接返回bitmap
     * @param stringUrl
     * @throws Exception
     */
    private Bitmap getImageFromNet(String stringUrl)throws Exception{
        URL url = new URL(stringUrl);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        int code = conn.getResponseCode();
        int length = conn.getContentLength();
        InputStream inputStream = null;
        if (code == 200){
            Log.e("tag", "suc");
            inputStream = conn.getInputStream();  // 拿到从网络传来的输入流
            // 使用下面的方法将输入流转化为字节数组

            byte[] bytes = inputStreamTobytes(inputStream, length);
            // 使用下面的方法将字节数组转换成bitmap
            Bitmap bitmap = bytesToBitmap(bytes);
            inputStream.close();
            conn.disconnect();
            return bitmap;
        }else {
            Log.e("tag", "fail");
        }
        return null;
    }

    /**
     * 生成位图
     */
    private Bitmap bytesToBitmap(byte[] bytes){
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }

    /**
     * 字节数组的长度设置为文件的长度
     * 将输入流转化为byte[] 字节数组
     * @param inputStream：传入一个输入流
     * @param length ：可以传入1024或者2048，自定义长度
     * @return
     *
     */
    private byte[] inputStreamTobytes(InputStream inputStream, int length) throws Exception{
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bytes = new byte[length];  // 根据文件的长度来创建字节数组，以文件的长度为字节数组的长度，一次就能将输入流读完
        int len = 0;
        while((len = inputStream.read(bytes)) != -1){  // 等于-1的时候已经读取到输入流的末尾了
            byteArrayOutputStream.write(bytes, 0, len);  // 这里表示将bytes中的内容写到byteArrayOutputStream中
        }
        byteArrayOutputStream.close();
        return byteArrayOutputStream.toByteArray();  // 将输出流转换成字节数组
    }

}
