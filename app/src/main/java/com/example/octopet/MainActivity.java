package com.example.octopet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.webkit.WebView;

import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import androidx.annotation.NonNull;

//Volley
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
//End import volley

public class MainActivity extends AppCompatActivity {

    protected static ImageButton imageButton;
    protected static ImageView imgTaken;
    protected static WebView octopet;
    protected static TextView statusText;
    protected static TextView curStatusText;
    protected static FirebaseVisionImage fireImage;
    public static Bitmap imageBitmap;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    protected static int status = 0;
    protected static int health = 90;


    private void animateGif(int status) {
        //Key (to implement soon):
        //     0 = good... 4 = bad (status)
        //    -1 = excite (positive points)
        //    -2 = disappoint (negative points)

        JSONObject parameters = new JSONObject();
        JSONObject query = new JSONObject();
        try {
            query.put("query", "cat");
            parameters.put("parameters",query);
        } catch (JSONException e) {
            Log.e("error","JSONException");
            //pass
        }

        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        String url = "https://api.transposit.com/app/thamaj/octopet_giphy/api/v1/execute/search_gifs";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST,url,parameters,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject data = response.getJSONObject("result");
                        JSONArray result = data.getJSONArray("results");
                        JSONObject result2 = (JSONObject) result.getJSONObject(0);
                        //System.out.println(result2.get("url")); //This gives me the url
                        //octopet = (ImageView)findViewById(R.id.octopet);
                        //octopet.setImageDrawable(drawableFromUrl((String) result2.get("url")));
                        octopet = findViewById(R.id.octopet);
                        //octopet.setInitialScale(30);
                        System.out.println((String) result2.get("id"));
                        octopet.loadUrl("https://i.giphy.com/media/" + result2.get("id") + "/200.gif");
                        //octopet.loadUrl((String) result2.get("url"));
                        //setContentView(octopet );
                    } catch (Exception e) {
                        Log.e("error",e.getMessage());
                    //pass
                    }
                    //System.out.println(response);
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("error","VolleyError");
                }
            });
        MyRequestQueue.add(jsObjRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //StrictMode.setThreadPolicy(policy);

        animateGif(status);

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
        health += 10;
    }

    public void decreasePoints() {
        health -= 10;
    }

    public void setStatus() {
        if (health >= 100 ) {
            status = 0;
        }
        else if (health >=75) {
            status = 1;
        }
        else if (health >= 50) {
            status = 2;
        }
        else if (health >= 25) {
            status = 3;
        }
        else {
            status = 4;
        }
    }

    /*private static Drawable drawableFromUrl(String url) throws java.net.MalformedURLException, java.io.IOException {
        Bitmap x;

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.connect();
        InputStream input = connection.getInputStream();

        x = BitmapFactory.decodeStream(input);
        return new BitmapDrawable(x);
    }*/



}
