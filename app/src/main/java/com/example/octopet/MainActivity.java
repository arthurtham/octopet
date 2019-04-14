package com.example.octopet;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.webkit.WebView;

import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;

import android.widget.TextView.OnEditorActionListener;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;


import org.w3c.dom.Text;
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
    protected static TextView nameText;
    protected static FirebaseVisionImage fireImage;
    public static Bitmap imageBitmap;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    protected static int status = 0;
    protected static int health = 90;
    protected static String name = "Tomo";
    protected static final String MY_PREFS_NAME = "PetState";
    SharedPreferences myPrefs;
    SharedPreferences.Editor myEditor;


    private void animateGif(int status) {
        //Key (to implement soon):
        //     0 = good... 4 = bad (status)
        //    -1 = excite (positive points)
        //    -2 = disappoint (negative points)

        //Random number
        double randomDouble = Math.random();
        randomDouble = randomDouble * 3;
        int randomInt = (int) randomDouble;
        System.out.println("randomint: " + randomInt);

        String mood;
        String bugCatURL = "";

        switch (status) {
            case 4: // dead
                mood = "fat";
                switch(randomInt) {
                    case 0:
                        bugCatURL = "1ezBmYXT1ccSbh0fep";
                        break;
                    case 1:
                        bugCatURL = "8clM9axkgqoFzy2HjD";
                        break;
                    case 2:
                        bugCatURL = "DBH3acVby8yT3geiJD";
                        break;
                }
                break;
            case 2: // sad
                mood = "sad";
                switch(randomInt) {
                    case 0:
                        bugCatURL = "14SGx6CtrLrj7dvOa3";
                        break;
                    case 1:
                        bugCatURL = "uWzRXTQRoQzxDO9W0p";
                        break;
                    case 2:
                        bugCatURL = "l4FGpa3DuEFMrghKE";
                        break;
                }
                break;
            case 3: mood = "mad"; //mad
                switch(randomInt) {
                    case 0:
                        bugCatURL = "65Th0K9yQJtKcxeYyN";
                        break;
                    case 1:
                        bugCatURL = "uBn5A3rxwD7N8nZvlw";
                        break;
                    case 2:
                        bugCatURL = "oy9hVQl8Hq7o8T3tER";
                        break;
                }
                break;
            case 0: mood = "happy"; //happy
                switch(randomInt) {
                    case 0:
                        bugCatURL = "fngeQvy995JpJhoMgz";
                        break;
                    case 1:
                        bugCatURL = "sRHOAgD3AA1DnFTR25";
                        break;
                    case 2:
                        bugCatURL = "9V5fArpd99fLoemwn3";
                        break;
                }
                break;
            case 1: mood = "excited";
                switch(randomInt) {
                    case 0:
                        bugCatURL = "MX5tWoGn9B3iU1riZJ";
                        break;
                    case 1:
                        bugCatURL = "piTZCzZKam8JXpTjZ1";
                        break;
                    case 2:
                        bugCatURL = "2mzRDsekJ4VqZIa2Cd";
                        break;
                }
                break;
            default: mood = "okay"; break;
        }

        JSONObject parameters = new JSONObject();
        JSONObject query = new JSONObject();
        try {
            query.put("query", bugCatURL);
            parameters.put("parameters",query);
        } catch (JSONException e) {
            Log.e("error","JSONException");
            //pass
        }

        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        String url = "https://api.transposit.com/app/thamaj/octopet_giphy/api/v1/execute/get_gif";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST,url,parameters,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject data = response.getJSONObject("result");
                        JSONArray result = data.getJSONArray("results");
                        JSONObject result2 = (JSONObject) result.getJSONObject( 0);
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

        myPrefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        //myEditor = myPrefs.edit();

        System.out.println("NAME before IS: " + name);

        status = myPrefs.getInt("status",0);
        health = myPrefs.getInt("health",90);
        name = myPrefs.getString("name", "Tomo");
        System.out.println("NAME IS: " + name);
        //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //StrictMode.setThreadPolicy(policy);

        nameText = (EditText) findViewById(R.id.name);
        nameText.setText(name);
        System.out.println("Name should be Tomo is actually: " + nameText.getText());
        /*
        nameText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    name = nameText.getText().toString();
                    myEditor.putString("name", name).commit();
                    Toast.makeText(MainActivity.this, nameText.getText(), Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });*/

        nameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (i1 == 0 && i2 == 1 && charSequence.charAt(i) == '\n') {

                    //b.performClick();
                    name = nameText.getText().toString(); //.replace(i, i + 1, ""); //remove the <enter>
                    System.out.println("On text changed: " + name);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                name = nameText.getText().toString();
                System.out.println("After text changed: " + name);
                myEditor = myPrefs.edit();
                myEditor.putString("name", name).commit();
            }

        });



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

        animateGif(status);


        System.out.println("Health: " + health);
        if (status == 0) {
            curStatusText.setText("good");
        }
        else if (status == 1) {
            curStatusText.setText("fine");
        }
        else if (status == 2) {
            curStatusText.setText("distressed");
        }
        else if (status == 3) {
            curStatusText.setText("dying");
        }
        else if (status == 4) {
            curStatusText.setText("dead");
        }





    }

    private void setDelayedAnimation(int status) {
        final Handler handler = new Handler();
        final int status2 = status;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animateGif(status2);
            }
        }, 3000);
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
        myEditor = myPrefs.edit();
        health += 10;
        status = -1; // means good
        myEditor.putInt("health", health+10).commit();
        showDialogBox("Good moves!","Your octopet " + name
        + " really liked it.");
    }

    public void decreasePoints() {
        myEditor = myPrefs.edit();
        health -= 10;
        status = -2; // means bad
        myEditor.putInt("health", health-10).commit();
        showDialogBox("Uh oh","Your octopet " + name
                + "'s body didn't really like it...");

    }

    public void setStatus() {
        myEditor = myPrefs.edit();
        if (health >= 75 ) {
            status = 0;
            myEditor.putInt("status", 0).commit();
        }
        else if (health >=50) {
            status = 1;
            myEditor.putInt("status", 1).commit();
        }
        else if (health >= 25) {
            status = 2;
            myEditor.putInt("status", 2).commit();
        }
        else if (health >= 0) {
            status = 3;
            myEditor.putInt("status", 3).commit();
        }
        else {
            status = 4;
            myEditor.putInt("status", 4).commit();
        }
    }

    class DoneOnEditorActionListener implements OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;
            }
            return false;
        }
    }

    protected void showDialogBox(String title, String message) {
        /*AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();*/
        Toast.makeText(getApplicationContext(),title + ": " + message,Toast.LENGTH_LONG).show();
    }


}
