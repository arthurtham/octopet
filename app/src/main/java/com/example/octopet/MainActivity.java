package com.example.octopet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;

import java.util.List;

import androidx.annotation.NonNull;

public class MainActivity extends AppCompatActivity {


    protected static ImageButton imageButton;
    protected static ImageView imgTaken;
    protected static TextView statusText;
    protected static TextView curStatusText;
    protected static FirebaseVisionImage fireImage;
    public static Bitmap imageBitmap;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    protected static int status = 0;
    protected static int health = 90;
    public static final String MY_PREFS_NAME = "PetState";
    SharedPreferences myPrefs;
    SharedPreferences.Editor myEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myPrefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        myEditor = myPrefs.edit();
        String restoredState = myPrefs.getString(MY_PREFS_NAME, null);
        if (restoredState != null) {
            status = myPrefs.getInt("status", 0);
            health = myPrefs.getInt("health", 90);
        }

        imageButton = findViewById(R.id.camera);
        imgTaken = (ImageView)findViewById(R.id.imageView);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        });


        TextView statusText = findViewById(R.id.status);
        TextView curStatusText = findViewById(R.id.currentStatus);

        setStatus();

        System.out.println("Health: " + health);
        if (status == 0) {
            curStatusText.setText("GOOD");
        }
        else if (status == 1) {
            curStatusText.setText("FINE");
        }
        else if (status == 2) {
            curStatusText.setText("DISTRESSED");
        }
        else if (status == 3) {
            curStatusText.setText("DYING");
        }





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            imgTaken.setImageBitmap(image);
        }
    }*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            //Bitmap
            imageBitmap = (Bitmap) extras.get("data");
            imgTaken.setImageBitmap(imageBitmap);


            fireImage = FirebaseVisionImage.fromBitmap(imageBitmap);

            FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance()
                    //.getOnDeviceImageLabeler();
                    .getCloudImageLabeler();
            labeler.processImage(fireImage)
                    .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                        @Override
                        public void onSuccess(List<FirebaseVisionImageLabel> labels) {
                            for (FirebaseVisionImageLabel label: labels) {
                                String text = label.getText();
                                String entityId = label.getEntityId();
                                float confidence = label.getConfidence();
                                System.out.println(text + ", " + entityId + ", " + confidence);
                            }
                            Intent in = new Intent(MainActivity.this, Confirm.class);

                            for (int _i = 0; _i < 3; ++_i) {
                                try {
                                    in.putExtra("option"+(_i + 1), labels.get(_i).getText());
                                } catch (Exception e){
                                    continue;
                                }
                            }

                            startActivity(in);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Task failed with an exception
                            // ...
                        }
                    });

        }
    }

    public void increasePoints() {
        //SharedPreferences.Editor myEditor = myPrefs.edit();
        health += 10;
        myEditor.putInt("health", health+10).commit();
    }

    public void decreasePoints() {
        health -= 10;
        myEditor.putInt("health", health-10).commit();
    }

    public void setStatus() {
        //SharedPreferences.Editor myEditor = myPrefs.edit();
        if (health >= 100 ) {
            status = 0;
            myEditor.putInt("status", 0).commit();
        }
        else if (health >=75) {
            status = 1;
            myEditor.putInt("status", 1).commit();
        }
        else if (health >= 50) {
            status = 2;
            myEditor.putInt("status", 2).commit();
        }
        else if (health >= 25) {
            status = 3;
            myEditor.putInt("status", 3).commit();
        }
        else {
            status = 4;
            myEditor.putInt("status", 4).commit();
        }
    }


}
