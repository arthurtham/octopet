package com.example.octopet;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Confirm extends MainActivity {

    Button yesButton;
    Button noButton;
    TextView confirmation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_food);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (1 * width), (int) (1 * height));

        final String food;
        Bundle extra = getIntent().getExtras();
        if (extra == null) {
            food = "whatever that is";
        }
        else {
            food = extra.getString("food");
        }

        imgTaken = (ImageView)findViewById(R.id.imageView);
        imgTaken.setImageBitmap(MainActivity.imageBitmap);


        yesButton = (Button)findViewById(R.id.yesButton);
        noButton = (Button) findViewById(R.id.noButton);
        confirmation = (TextView) findViewById(R.id.question) ;

        String prompt = "Do you want to feed Tofu the " + food + "?";
        confirmation.setText(prompt);
        System.out.println("Set confirmation text to '"+prompt+"'");
        yesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                startActivity(new Intent(Confirm.this, MainActivity.class));
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                startActivity(new Intent(Confirm.this, MainActivity.class));
            }
        });

    }

}
