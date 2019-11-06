package com.example.guess_celeb;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.graphics.*;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    ImageView image;
    Button optA;
    Button optB;
    Button optC;
    Button optD;
    int correctLoc;
    ArrayList<String> celebNames = new ArrayList<>();
    ArrayList<String> celebUrl = new ArrayList<>();
    ArrayList<String> options = new ArrayList<>();
    public void chosenCeleb(View view){
        if(view.getTag().toString().equals(Integer.toString(correctLoc))){
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"Oops! It's "+options.get(correctLoc),Toast.LENGTH_SHORT).show();
        }
        newQuestion();
    }
    public static class ImgDownloader extends AsyncTask<String ,Void,Bitmap> {  //Class to get the image of Celebritites

        @Override
        protected Bitmap doInBackground(String... urls) {

            try{
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream input = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(input);
                Bitmap img =BitmapFactory.decodeStream(input);
                return img;
            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }
    public static class Task extends AsyncTask<String,Void,String>{ // Class to get the names of celebrities
        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream input = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(input);
                StringBuilder sb = new StringBuilder();
                int data =reader.read();
                char c ;
                while(data!=-1){
                    c = (char)data;
                    sb.append(c);
                    data = reader.read();
                }
                return sb.toString();
            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }

    }
    public void newQuestion(){
        try{
            // choose whom to display randomly =>get the image => get the options => set the option in buttons
            Random random = new Random();
            int chosenCeleb = random.nextInt(celebUrl.size());
            Log.i(celebNames.get(chosenCeleb),"clicked");
            ImgDownloader celebImg = new ImgDownloader();
            Bitmap celebMap = celebImg.execute(celebUrl.get(chosenCeleb)).get();
            image.setImageBitmap(celebMap);
            String correctName = celebNames.get(chosenCeleb);

            correctLoc = random.nextInt(4);
            int wrongLoc;
            String wrongName ;
            options.clear();
            for(int i=0;i<4;i++){
                if(i==correctLoc){
                    options.add(correctName);
                }else{
                    wrongLoc = random.nextInt(celebUrl.size());
                    wrongName = celebNames.get(wrongLoc);
                    while(wrongLoc == correctLoc && checkDuplicate(wrongName) ){
                        wrongLoc = random.nextInt(celebUrl.size());
                    }
                    options.add(wrongName);
                }
            }

            optA.setText(options.get(0));
            optB.setText(options.get(1));
            optC.setText(options.get(2));
            optD.setText(options.get(3));

        }catch(Exception e){
            e.printStackTrace();
        }

    }
    public boolean checkDuplicate(String name){
        int size = options.size();
        for(int i=0;i<size;i++){
            if(options.get(i).equals(name))
                return true;  //duplicate found
        }
        return false;  // no duplicates
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image = findViewById(R.id.imageView);
        optA = findViewById(R.id.optA);
        optB = findViewById(R.id.optB);
        optC = findViewById(R.id.optC);
        optD = findViewById(R.id.optD);

        String names= null;
        Task task = new Task();
        try {
         names = task.execute("http://www.posh24.se/kandisar").get();
         String[] split = names.split("<div class=\"listedArticles\">");
         Pattern p = Pattern.compile("img src=\"(.*?)\"");
         Matcher m = p.matcher(split[0]);
         while(m.find()){
             celebUrl.add(m.group(1));
         }
         p = Pattern.compile("alt=\"(.*?)\"");
         m = p.matcher(split[0]);

         while(m.find()){
             celebNames.add(m.group(1));
         }
         newQuestion();

        }catch(Exception e){
            e.printStackTrace();

        }
    }


}
