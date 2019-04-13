package com.example.octopet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    protected static ImageButton imageButton;
    protected static ImageView imgTaken;
    protected static TextView statusText;
    protected static TextView curStatusText;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    protected int status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            imgTaken.setImageBitmap(image);
        }
    }


}
