package com.example.octopet;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
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
    protected int status;

    SharedPreferences sharedPrefs;
    static final String welcomeScreenShownPref = "welcomeScreenShown";
    static final String petName = "petNameKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean welcomeScreenShown = sharedPrefs.getBoolean(welcomeScreenShownPref, false);

        if (!welcomeScreenShown) {
            //EditText petNameField = findViewById(R.id.petNameEntry);
            /*String whatsNewTitle = getResources().getString(R.string.whatsNewTitle);
            //String whatsNewText = getResources().getString(R.string.whatsNewText);
            new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)//.setTitle(whatsNewTitle).setMessage(whatsNewText)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putBoolean(welcomeScreenShownPref, true);
            editor.commit();*/
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Welcome to Octopet!");
            alertDialog.setMessage("Please select a name for your pet\n\n\n");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putBoolean(welcomeScreenShownPref, true);
            editor.commit();
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
                            in.putExtra("food",labels.get(0).getText());
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


}
