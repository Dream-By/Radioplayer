package com.example.radiogomelfm;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;


import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Resource;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.service.autofill.Validators.and;

public class MainActivity<Webview> extends AppCompatActivity {

    private SimpleExoPlayer player;
    private TextView textView, trackView;
    public String sArtist, sTrack;
    private Timer timer;
    private TimerTask mTimerTask;
    DefaultDataSourceFactory dataSourceFactory;
    MediaSource mediaSource;
    static String RADIO_URL = "http://s1.radioheart.ru:8001/radiogomelfm";
    boolean isEnabled;
    private String TAG = "Жизненный цикл";
    private String sit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        Button button = (Button) findViewById(R.id.button);
        FrameLayout.LayoutParams framelayout = new FrameLayout.LayoutParams(metrics.widthPixels / 4, metrics.widthPixels / 4);
        framelayout.topMargin = (int) (metrics.heightPixels / 2.5);
        framelayout.leftMargin = (metrics.widthPixels / 2) - (metrics.widthPixels / 8);
        button.setLayoutParams(framelayout);
        try {
            player = ExoPlayerFactory.newSimpleInstance(this);
            dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "Gomel FM"));
            mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(RADIO_URL));
            player.prepare(mediaSource);
        } catch (Exception e) {
            e.printStackTrace();
        }

        isEnabled = true;
    }


    //response from radiogomelfm.by
    private void getResponse() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(com.example.radiogomelfm.ApiInterface.JSONURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiInterface api = retrofit.create(ApiInterface.class);

        Call<List<MusicModel>> call = api.getMusic();

        call.enqueue(new Callback<List<MusicModel>>() {
            @Override
            public void onResponse(Call<List<MusicModel>> call, Response<List<MusicModel>> response) {

                if (response.isSuccessful()) {
                    List<MusicModel> musicModelArrayList = response.body();
                    if (musicModelArrayList != null) {
                        showMusicList(musicModelArrayList);
                    } else {
                        Log.w("onResponse", "Returned empty response");
                    }
                } else {
                    Log.w("onResponse", "Not success response: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<MusicModel>> call, Throwable t) {
                Log.e("Retrofit", "onFailure: ", t);

            }

        });
    }

    // parsing ArtistName & TrackName from radiogomelfm.by
    private void showMusicList(List<MusicModel> musicModelArrayList) {
        StringBuilder sb = new StringBuilder();
        sb.append(musicModelArrayList.get(0).getArtistName());
        sb.append(" - ");
        sb.append(musicModelArrayList.get(0).getTrackName());

        textView = findViewById(R.id.textView); //ArtistName & TrackName
        trackView = findViewById(R.id.trackView);

        textView.setText(musicModelArrayList.get(0).getArtistName());
        trackView.setText(musicModelArrayList.get(0).getTrackName());

        sArtist = musicModelArrayList.get(0).getArtistName();
        sTrack = musicModelArrayList.get(0).getTrackName();

    }

    public void getTrackInfo() {
        Retrofit retrofit = NetworkClient.getRetrofitClient();
        MusicInterface musicInterface = retrofit.create(MusicInterface.class);

        //Call callTrack = musicInterface.getTrackInfo("track.getInfo","6c8dc87e402c8f96b8369f927ca0c1be","Cher","Believe","json");
        Call callTrack = musicInterface.getTrackInfo("track.getInfo", "6c8dc87e402c8f96b8369f927ca0c1be", sArtist, sTrack, "json");
        callTrack.enqueue(new Callback<MediaPlayer.TrackInfo>() {
            @Override
            public void onResponse(Call call, Response responseTrackInfo) {

                TrackInfo trackInfo = (TrackInfo) responseTrackInfo.body();
                if (responseTrackInfo.body() != null) {
                    final Track track = trackInfo.getTrack();
                    final StringBuilder sb = new StringBuilder();
                    final Album album = track.getAlbum();
                    final List<Image> image = album.getImage();
                /*if (album.getImage().get(3) !=null) {
                    ImageView img = (ImageView) findViewById(R.id.imageView);
                    Picasso.get().load(image.get(3).getText()).into(img);
                }*/
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                /*ImageView img = (ImageView) findViewById(R.id.imageView);
                Picasso.get().load("microphone.png").into(img);*/

            }
        });

    }


    //logic button system start-stop mediaplaying
    public void didTapButton(View view) {
        if (timer != null) {
            timer.cancel();
        }
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 3);
        myAnim.setInterpolator(interpolator);
        Button button = (Button) findViewById(R.id.button);
        button.startAnimation(myAnim);
        if (isEnabled) {
            player.setPlayWhenReady(true);
            isEnabled = false;
            button.setText("STOP");
            //   StartStop.setText("STOP");
            timer = new Timer();
            mTimerTask = new MyTimerTask();
            {
                timer.schedule(mTimerTask, 50, 1000);
                getResponse(); //parsing ArtistName & TrackName from radiogomelfm.by
                //getTrackInfo(); //parsing photo from Last.FM
            }

        } else {
            player.setPlayWhenReady(false);
            isEnabled = true;
            button.setText("PLAY");
        }
    }

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getResponse(); //parsing ArtistName & TrackName
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isEnabled ==false){
            sit = "isEnabled=false";
        }
        else
        {sit = "isEnabled=true";}

        Toast.makeText(getApplicationContext(), "onDestroy()" + sit, Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onDestroy()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (isEnabled ==false){
            sit = "isEnabled=false";
        }
        else
        {sit = "isEnabled=true";}

        Toast.makeText(getApplicationContext(), "onRestart()" + sit, Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onRestart()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isEnabled ==false){
            sit = "isEnabled=false";
        }
        else
        {sit = "isEnabled=true";}

        Toast.makeText(getApplicationContext(), "onStop()" + sit, Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onStop()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isEnabled ==false){
            sit = "isEnabled=false";
        }
        else
        {sit = "isEnabled=true";}

        Toast.makeText(getApplicationContext(), "onPause()" + sit, Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onPause()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isEnabled ==false){
            sit = "isEnabled=false";
                    }
        else
        {sit = "isEnabled=true";}

        Toast.makeText(getApplicationContext(), "onResume()" + sit, Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onResume()");
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (isEnabled ==false){
            sit = "isEnabled=false";
                   }
        else
        {sit = "isEnabled=true";}

        Toast.makeText(getApplicationContext(), "onStart()" + sit, Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onStart()");
        /*if (isEnabled ==false){
            Button button = (Button) findViewById(R.id.button);
            button.setText("STOP");
            //   StartStop.setText("STOP");
            timer = new Timer();
            mTimerTask = new MyTimerTask();
            {
                timer.schedule(mTimerTask, 50, 1000);
                getResponse();
            }
        }*/
         }
}


