package com.example.octopet;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.util.IOUtils;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Confirm extends MainActivity {

    Button option1;
    Button option2;
    Button option3;
    Button buttonCancel;
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
        option1 = (Button)findViewById(R.id.button1);
        option2 = (Button)findViewById(R.id.button2);
        option3 = (Button)findViewById(R.id.button3);

        Bundle extra = getIntent().getExtras();
        if (extra == null) {
            food = "whatever that is";
        }
        else {
            food = "yes!";
            try {

                option1.setText(extra.getString("option1"));
                System.out.println(extra.getString("option1"));

                option2.setText(extra.getString("option2"));
                System.out.println(extra.getString("option2"));

                option3.setText(extra.getString("option3"));
                System.out.println(extra.getString("option3"));
            } catch (Exception e){

            }

        }

        buttonCancel = (Button)findViewById(R.id.buttonCancel);

        imgTaken = (ImageView)findViewById(R.id.imageView);
        imgTaken.setImageBitmap(MainActivity.imageBitmap);


        //yesButton = (Button)findViewById(R.id.yesButton);
        //noButton = (Button) findViewById(R.id.noButton);




        confirmation = (TextView) findViewById(R.id.question) ;

        String prompt = "Do you want to feed " + name + " the " + food + "?";
        confirmation.setText(prompt);
        System.out.println("Set confirmation text to '"+prompt+"'");

        option1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (patternIsGood((String) option1.getText())) {
                    increasePoints();
                    System.out.println("Good choice!");
                } else {
                    decreasePoints();
                    System.out.println("I don't feel so good chief");
                }
                setStatus();
                startActivity(new Intent(Confirm.this, MainActivity.class));
            }
        });

        option2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (patternIsGood((String) option2.getText())) {
                    increasePoints();
                    System.out.println("Good choice!");
                } else {
                    decreasePoints();
                    System.out.println("I don't feel so good chief");
                }
                setStatus();
                startActivity(new Intent(Confirm.this, MainActivity.class));
            }
        });

        option3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (patternIsGood((String) option3.getText())) {
                    increasePoints();
                    System.out.println("Good choice!");
                } else {
                    decreasePoints();
                    System.out.println("I don't feel so good chief");
                }
                setStatus();
                startActivity(new Intent(Confirm.this, MainActivity.class));
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                startActivity(new Intent(Confirm.this, MainActivity.class));
            }
        });

    }

    private String openFile(String fileName) {
        try {
            InputStream is = getApplicationContext().getAssets().open(fileName);
            Scanner s = new Scanner(is).useDelimiter("\\A");
            String result = s.hasNext() ? s.next() : "";
            result = result.replaceAll("[^A-Za-z0-9]", " ");
            //System.out.println(result);
            return result;
        } catch (Exception e) {
            //File load error
            return "error";
        }
    }

    private Boolean stringMatch(String patternStr, String fileStr) {
        String[] patternArray = patternStr.split("[^A-Za-z0-9]");
        for (String patternToken : patternArray) {
            //System.out.println("Pattern:" + patternToken.toLowerCase());
            //System.out.println("fileStr:" + fileStr);
            if (fileStr.contains(patternToken.toLowerCase())) {
                System.out.println("The pattern matched!");
                return true;
            }
        }
        System.out.println("No patterns matched.");
        return false;
    }

    private Boolean patternIsGood(String pattern) {
        System.out.println("Checking bad list...");
        String badList = openFile("bad.txt");
        if (stringMatch(pattern,badList)) {
            System.out.println("This item is in the bad list.");
            return false; // it's a bad item
        }
        System.out.println("Checking good list...");
        String goodList = openFile("good.txt");
        return (stringMatch(pattern,goodList)); //true: it's a good item. false: it's a bad item
    }

}
