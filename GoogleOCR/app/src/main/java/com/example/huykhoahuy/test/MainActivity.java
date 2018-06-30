package com.example.huykhoahuy.test;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int CAMERA_REQUEST = 1997;
    private static final int RESULT_LOAD_IMG = 2018;
    private EditText etCode;
    private EditText etDate;
    private EditText etName;
    Bitmap bitmap = null;
    List<Bitmap> bitmapList;
    Uri mImageUri = null;
    StringBuilder sb = new StringBuilder();
    File photo = null;

    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnTakeImage = (Button)findViewById(R.id.btn_take_image);
        Button btnLoadImage = (Button)findViewById(R.id.btn_load_image);
        etCode = (EditText)findViewById(R.id.et_code);
        etDate = (EditText)findViewById(R.id.et_date);
        etName = (EditText)findViewById(R.id.et_name);


        progressBar = (ProgressBar)findViewById(R.id.prb_loading_form_1);

        btnTakeImage.setOnClickListener(this);
        btnLoadImage.setOnClickListener(this);


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode==RESULT_OK) {
            getContentResolver().notifyChange(mImageUri, null);
            ContentResolver cr = getContentResolver();
            try {
                bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, mImageUri);
                ImageView imgView = (ImageView) findViewById(R.id.iv_my_lottery_image);
                imgView.setImageBitmap(bitmap);
                RetrieveLotteryInfo lotteryInfo = new RetrieveLotteryInfo(getWindow().getDecorView().getRootView(),
                        bitmap,
                        etCode,etDate,etName,
                        progressBar);
                lotteryInfo.execute();
                photo.delete();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),"Failed to load",Toast.LENGTH_SHORT).show();
            }
        }
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgDecodableString = cursor.getString(columnIndex);
                cursor.close();

                ImageView imgView = (ImageView) findViewById(R.id.iv_my_lottery_image);
                // Set the Image in ImageView after decoding the String
                bitmap = BitmapFactory.decodeFile(imgDecodableString);
                imgView.setImageBitmap(bitmap);
                RetrieveLotteryInfo lotteryInfo = new RetrieveLotteryInfo(getWindow().getDecorView().getRootView(),
                        bitmap,
                        etCode,etDate,etName,
                        progressBar);
                lotteryInfo.execute();


            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btn_take_image)
        {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            photo = null;
            try {
                File tempDir = Environment.getExternalStorageDirectory();
                tempDir = new File(tempDir.getAbsolutePath()+"/.temp/");
                if (!tempDir.exists()) {
                    tempDir.mkdirs();
                }
                photo = File.createTempFile("picture",".jpg",tempDir);
                photo.delete();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),"Can't create file to take picture",Toast.LENGTH_SHORT).show();
                return;
            }
            if (photo == null) return;
            mImageUri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider",photo);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);

        }
        if(v.getId()==R.id.btn_load_image)
        {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
        }

    }
}

