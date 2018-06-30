package com.example.huykhoahuy.test;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class RetrieveLotteryInfo extends AsyncTask<Void,Void,StringBuilder>{

    private View view;
    private Bitmap bitmap;
    private EditText etCode;
    private EditText etDate;
    private EditText etName;
    private ProgressBar progressBar;

    public RetrieveLotteryInfo(View view, Bitmap bitmap, EditText etCode, EditText etDate, EditText etName, ProgressBar progressBar) {
        this.view = view;
        this.bitmap = bitmap;
        this.etCode = etCode;
        this.etDate = etDate;
        this.etName = etName;
        this.progressBar = progressBar;
    }

    private ArrayList<String> listResult =new ArrayList<>();



    private ArrayList<Bitmap> CropImage(Bitmap bitmap) {

        ArrayList<Bitmap> bitmapList = new ArrayList<>();
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int h1 = h/2;
        int h2 = h/4;
        int h3 = h/8;
        int w1 = w/2;
        int w2 = w/4;
        int w3 = w/8;

        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        Bitmap bmp1 = Bitmap.createBitmap(bitmap,0,0,w1,h2);
        bitmapList.add(bmp1);
        Bitmap bmp2 = Bitmap.createBitmap(bitmap,0,0,w3,h); //need rotate
        Bitmap bmp2r = Bitmap.createBitmap(bmp2,0,0,bmp2.getWidth(), bmp2.getHeight(),matrix,true);
        bitmapList.add(bmp2r);
        Bitmap bmp3 = Bitmap.createBitmap(bitmap,0,0,w2,h1);
        bitmapList.add(bmp3);
        Bitmap bmp4 = Bitmap.createBitmap(bitmap,w-w1,0,w1,h2);
        bitmapList.add(bmp4);
        Bitmap bmp5 = Bitmap.createBitmap(bitmap,w-w1,h2,w1,h2);
        bitmapList.add(bmp5);
        Bitmap bmp6 = Bitmap.createBitmap(bitmap,w1-50,h-h2-50,w1+50,h2+50);
        bitmapList.add(bmp6);
        Bitmap bmp7 = Bitmap.createBitmap(bitmap,0,h-h1,w1,h1);
        bitmapList.add(bmp7);
        Bitmap bmp8 = Bitmap.createBitmap(bitmap,w2,0,w2,h1);
        bitmapList.add(bmp8);
        Bitmap bmp9 = Bitmap.createBitmap(bitmap,0,0,w,h2);
        bitmapList.add(bmp9);
        Bitmap bmp10 = Bitmap.createBitmap(bitmap,0,h-h1,w,h1);
        bitmapList.add(bmp10);
        return bitmapList;
    }

    public StringBuilder getRawInfo(Bitmap bitmap)
    {
        StringBuilder sb = new StringBuilder();
        ArrayList<Bitmap> bitmapList = CropImage(bitmap);
        TextRecognizer textRecognizer = new TextRecognizer.Builder(view.getContext()).build();
        for(int i=0;i<bitmapList.size();i++) {
            Bitmap bmp = bitmapList.get(i);

            sb.append("["+String.valueOf(i)+"]");
            sb.append("\n");
            if(!textRecognizer.isOperational() || bmp == null)
            {
                Toast.makeText(view.getContext(),"No Text",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Frame frame = new Frame.Builder().setBitmap(bmp).build();
                SparseArray<TextBlock> items = textRecognizer.detect(frame);
                StringBuilder temp = new StringBuilder();
                for(int j= 0;j<items.size();++j)
                {
                    TextBlock myItems = items.valueAt(j);

                    sb.append(myItems.getValue());
                    temp.append(myItems.getValue());

                    sb.append("\n");
                    temp.append("\n");
                }
                listResult.add(temp.toString());
            }

        }
        return sb;
    }


    @Override
    protected StringBuilder doInBackground(Void... voids) {
        progressBar.setVisibility(View.VISIBLE);
        try{
            return getRawInfo(bitmap);

        }catch (Exception e){
            Log.e("ERROR", e.getMessage(), e);
            return null;
        }
    }

    public void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void onPostExecute(StringBuilder sb)
    {
        progressBar.setVisibility(View.GONE);
        //Phần đưa thông tin vào
        //etCode.setText(........);
        //etDate.setText(........);
        //etName.setText(........);


        // Dùng để ghi các file cho Huy
//        try {
//            String h = DateFormat.format("MM-dd-yyyyy-h-mmssaa", System.currentTimeMillis()).toString();
//            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
//            if (!root.exists()) {
//                root.mkdirs(); // this will create folder.
//            }
//            File filepath = new File(root,  h+".txt");  // file path to save
//            FileWriter writer = new FileWriter(filepath);
//            writer.append(sb.toString());
//            writer.flush();
//            writer.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

}
