package com.example.maria.itplace_android01_hw3;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.CallScreeningService;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static android.R.attr.data;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MyTag";
    public static final String EXTRA_STRING = "extra_string";
    public static final String COLOR_KEY = "COLOR_KEY";
    public static final String IMAGE_KEY = "IMAGE_KEY";

    public static final int REQUEST_CODE_HEX = 0;
    static final int GALLERY_REQUEST = 1;

    Button button;
    TextView textView;
    ImageView imageView;
    //String color;
    Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        getExtras(savedInstanceState);

        Log.d(TAG, "onCreate: Called");
    }

    private void initViews() {
        button = (Button) findViewById(R.id.change_text_button);
        textView = (TextView) findViewById(R.id.our_text);
        imageView = (ImageView) findViewById(R.id.imageView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(ResultActivity.start(MainActivity.this), REQUEST_CODE_HEX);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
            }
        });
    }

    private void getExtras(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }

        textView.setText(savedInstanceState.getString(EXTRA_STRING));

        //color = savedInstanceState.getString(COLOR_KEY);
        //if (savedInstanceState.getInt(COLOR_KEY) != 0) {
            textView.setBackgroundColor(savedInstanceState.getInt(COLOR_KEY)/*Color.parseColor(color)*/);
        //}
        String image_key = savedInstanceState.getString(IMAGE_KEY);
        if (image_key != null) {
            imageUri = Uri.parse(image_key);
            //Bitmap bitmap = null;
            /*try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView.setImageBitmap(bitmap);*/
            imageView.setImageURI(null);  // для сброса кэша
            imageView.setImageURI(imageUri);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: Called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: Called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Called");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: Called");
        ColorDrawable colorDrawable = (ColorDrawable) textView.getBackground();
        if (colorDrawable != null) {
            outState.putInt(COLOR_KEY, /*Integer.toString(*/colorDrawable.getColor()/*)color*/);
        }
        outState.putString(EXTRA_STRING, textView.getText().toString());
        if (imageUri != null) {
            outState.putString(IMAGE_KEY, imageUri.toString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_HEX:
                String color = ResultActivity.getExtraString(data);
                textView.setText(color);
                textView.setBackgroundColor(Color.parseColor(color));
                return;
            case GALLERY_REQUEST:
                imageUri = data.getData();

                /*Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        imageView.setImageBitmap(bitmap); */

                String simageUri = "";
                try {
                    simageUri = getFilePath(this, imageUri)  ;
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                    //imageUri  =  imageUri.parse(simageUri);


                File imgFile = new  File(simageUri);
                if(imgFile.exists()){
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    imageView.setImageBitmap(myBitmap);
                }

                //imageView.setImageURI(null);  // для сброса кэша
                //imageView.setImageURI(imageUri);
                return;
        }
    }

     @SuppressLint("NewApi")
        public /*static*/ String getFilePath(Context context, Uri uri) throws URISyntaxException {
            String selection = null;
            String[] selectionArgs = null;
            // Uri is different in versions after KITKAT (Android 4.4), we need to
            if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                } else if (isDownloadsDocument(uri)) {
                    final String id = DocumentsContract.getDocumentId(uri);
                    uri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                } else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    if ("image".equals(type)) {
                        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    selection = "_id=?";
                    selectionArgs = new String[]{
                            split[1]
                    };
                }
            }
            if ("content".equalsIgnoreCase(uri.getScheme())) {
                String[] projection = {
                        MediaStore.Images.Media.DATA
                };
                Cursor cursor = null;
                try {
                    cursor = context.getContentResolver()
                            .query(uri, projection, selection, selectionArgs, null);
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    if (cursor.moveToFirst()) {
                        return cursor.getString(column_index);
                    }
                } catch (Exception e) {
                }
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
            return null;
        }

        public static boolean isExternalStorageDocument(Uri uri) {
            return "com.android.externalstorage.documents".equals(uri.getAuthority());
        }

        public static boolean isDownloadsDocument(Uri uri) {
            return "com.android.providers.downloads.documents".equals(uri.getAuthority());
        }

        public static boolean isMediaDocument(Uri uri) {
            return "com.android.providers.media.documents".equals(uri.getAuthority());
        }



}
