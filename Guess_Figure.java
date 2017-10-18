package com.guess_figure.mohammad.guessfigure;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Guess_Figure extends AppCompatActivity {
    int count;
    TextView score;
    TextView YHS,text;
    int testwith;
    ImageView img;
    MediaPlayer mediaPlayer,mediaPlayer1;
    ImageButton changeName;
    TextView name;
    TextView userName;
    TextView kingName,kingScore;
    Boolean testInternet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guess__figure);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.correct);
        mediaPlayer1 = MediaPlayer.create(getApplicationContext(), R.raw.wrong);
        testwith=2;

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-1661339085652575/2157399849");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        kingName = (TextView)findViewById(R.id.kingName);
        kingScore = (TextView)findViewById(R.id.kingScore);
        userName=(TextView)findViewById(R.id.userName);
        userName.setText(SharedPreferencesHelper.getStringSharedPref(getApplicationContext(),"guess_figure_name")+"");
        changeName = (ImageButton)findViewById(R.id.changeName);
        changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater1 = LayoutInflater.from(Guess_Figure.this);
                final View promptView1 = layoutInflater1.inflate(R.layout.name, null);
                final AlertDialog alertD1 = new AlertDialog.Builder(Guess_Figure.this).create();
                name =(TextView)promptView1.findViewById(R.id.name);
                Button toTime = (Button) promptView1.findViewById(R.id.save);
                toTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferencesHelper.setSharePref(getApplicationContext(),"guess_figure_name",name.getText().toString());
                        userName.setText(SharedPreferencesHelper.getStringSharedPref(getApplicationContext(),"guess_figure_name")+"");
                        alertD1.dismiss();
                    }
                });
                alertD1.setView(promptView1);
                alertD1.show();
            }
        });
        img = (ImageView) findViewById(R.id.image);
        score = (TextView)findViewById(R.id.score);
        text = (TextView)findViewById(R.id.text);
        text.setText("Guess The Number Between \n 0 - "+testwith);
        YHS=(TextView)findViewById(R.id.YHS);
        YHS.setText(SharedPreferencesHelper.getIntSharedPref(getApplicationContext(),"guess_figure_number")+"");
        Button go = (Button)findViewById(R.id.go);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText getN = (EditText)findViewById(R.id.getN);
                if (getN.getText().toString().isEmpty()){
                    getN.setText(0+"");
                }
                final int theN = Integer.parseInt(getN.getText().toString());
                int aNumber = (int) (testwith * Math.random()) + 1;
                if (theN==aNumber){
                    mediaPlayer.start();
                    img.setImageResource(R.drawable.trueicon);
                    count= Integer.parseInt(score.getText().toString());
                    count++;


                    if (count > SharedPreferencesHelper.getIntSharedPref(getApplicationContext(),"guess_figure_number"))
                    SharedPreferencesHelper.setSharePref(getApplicationContext(),"guess_figure_number",count);
                    score.setText(count+"");
                }
                else {
                    mediaPlayer1.start();
                    img.setImageResource(R.drawable.falseicon);
                    count=0;
                    testwith=2;
                    text.setText("Guess The Number Between \n 0 - "+testwith);
                    score.setText(count+"");
                }
                if (Integer.parseInt(YHS.getText().toString())<Integer.parseInt(score.getText().toString()))
                {
                    int x = Integer.parseInt(score.getText().toString()) ;
                    YHS.setText(x+"");
                    if (testwith*2<Integer.parseInt(YHS.getText().toString())){
                        testwith++;
                        text.setText("Guess The Number Between \n 0 - "+testwith);
                    }
                }


                if (!testInternet) {
                    if (isInternetConnected(getApplicationContext())) {
                        String url = "http://www.kufermalik.com/GuessDB.php";
                        new MyAsyncTaskgetNews().execute(url);
                        testInternet = true;
                    } else {
                        testInternet = false;
                        kingName.setText("No Internet Connection");
                        kingScore.setText("?");
                    }
                }

                 int guess_figure_number = SharedPreferencesHelper.getIntSharedPref(getApplicationContext(),"guess_figure_number");
                 int king_guess_figure_number = SharedPreferencesHelper.getIntSharedPref(getApplicationContext(),"king_guess_figure_number");

                if (guess_figure_number==king_guess_figure_number)
                {
                    String url="http://www.kufermalik.com/GuessDB.php";
                    new MyAsyncTaskgetNews().execute(url);
                }

                if (guess_figure_number > king_guess_figure_number)
                {
                    String urlUpDate= null;
                    try {
                        urlUpDate = "http://kufermalik.com/GuessUpdate.php?name="+
                                URLEncoder.encode(SharedPreferencesHelper.getStringSharedPref(getApplicationContext(),"guess_figure_name"), "utf-8")
                                +"&score="+guess_figure_number+"";
                    } catch (UnsupportedEncodingException e) {
                    }
                    new Guess_Figure.MyAsyncTaskget().execute(urlUpDate);

                    String url="http://www.kufermalik.com/GuessDB.php";
                    new MyAsyncTaskgetNews().execute(url);

                }


                getN.setText("");
            }
        });



        if(isInternetConnected(getApplicationContext())){
            String url="http://www.kufermalik.com/GuessDB.php";
            new MyAsyncTaskgetNews().execute(url);
            testInternet=true;
        }
        else
        {
            testInternet = false;
            kingName.setText("No Internet Connection");
            kingScore.setText("?");
        }


//        String urlUpDate="http://kufermalik.com/GuessUpdate.php?name=noname&score=0";
//        new Guess_Figure.MyAsyncTaskget().execute(urlUpDate);

    }

    public class MyAsyncTaskgetNews extends AsyncTask<String, String, String> {
        @Override
        protected String  doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {
                String NewsData;
                URL url = new URL(params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(7000);
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    NewsData = ConvertInputToStringNoChange(in);
                    publishProgress(NewsData);
                } finally {
                    urlConnection.disconnect();
                }

            }catch (Exception ex){}
            return null;
        }
        protected void onProgressUpdate(String... progress) {
            try {
                JSONArray json=new JSONArray(progress[0]);
                for(int i=0;i<json.length();i++) {
                    JSONObject user=json.getJSONObject(i);
                    kingScore.setText(user.getString("score"));
                    kingName.setText(user.getString("name"));
                    SharedPreferencesHelper.setSharePref(getApplicationContext(),"king_guess_figure_name",user.getString("name"));
                    SharedPreferencesHelper.setSharePref(getApplicationContext(),"king_guess_figure_number",user.getInt("score"));
                }

            } catch (Exception ex) {
            }
        }
        protected void onPostExecute(String  result2){
        }
    }

    public static String ConvertInputToStringNoChange(InputStream inputStream) {

        BufferedReader bureader=new BufferedReader( new InputStreamReader(inputStream));
        String line ;
        String linereultcal="";

        try{
            while((line=bureader.readLine())!=null) {

                linereultcal+=line;

            }
            inputStream.close();


        }catch (Exception ex){}

        return linereultcal;
    }

    public static boolean isInternetConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();}


    public class MyAsyncTaskget extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            //before works
        }
        @Override
        protected String  doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {
                String NewsData;
                URL url = new URL(params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(7000);
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    NewsData = ConvertInputToStringNoChange(in);
                    publishProgress(NewsData);
                } finally {
                    urlConnection.disconnect();
                }

            }catch (Exception ex){}
            return null;
        }
        protected void onProgressUpdate(String... progress) {

        }

        protected void onPostExecute(String  result2){

        }


    }



}
