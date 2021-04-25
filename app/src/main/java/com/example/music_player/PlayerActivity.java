package com.example.music_player;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;
import com.gauravk.audiovisualizer.visualizer.BlastVisualizer;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class PlayerActivity extends AppCompatActivity {

    SeekBar mSeekBar, volumebar;
    Button playIcon, prevIcon, nextIcon, repeatIcon, shuffleIcon;
    ImageView imageView;
    TextView songTitle;

    ArrayList<File> allSongs;
    static MediaPlayer mediaPlayer;
    int position;
    TextView curTime, totTime;

    Intent playerData;
    Bundle bundle;

    AudioManager audioManager;

    boolean repeat = false;
    boolean shuffle = false;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //set back to home button
            if (item.getItemId() == android.R.id.home) {onBackPressed();}
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        //back home button on top bar
        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setIcon(R.drawable.ic_fav);

        mSeekBar = findViewById(R.id.seekbar);
        songTitle = findViewById(R.id.txtsn);
        curTime = findViewById(R.id.txtsstart);
        totTime = findViewById(R.id.txtsstop);

        imageView = findViewById(R.id.imageview);
        playIcon = findViewById(R.id.playbtn);
        prevIcon = findViewById(R.id.btnprev);
        nextIcon = findViewById(R.id.btnnext);

        repeatIcon = findViewById(R.id.btnrepeat);
        shuffleIcon = findViewById(R.id.btnshuf);

        volumebar = findViewById(R.id.volumebar);


            if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        playerData = getIntent();
        bundle = playerData.getExtras();

        allSongs = (ArrayList) bundle.getParcelableArrayList("songs");
        position = bundle.getInt("position", 0);
        initPlayer(position);




    //        ------------------ play btn -----------------------------
            playIcon.setOnClickListener(v -> play());

    //        ------------------ prev btn -------------------------------
            prevIcon.setOnClickListener(v -> {

            if (position <= 0 && shuffle) {
                position = getShuffle(allSongs.size() - 1);
    //                    position = mysongs.size() - 1;
            } else if (position <= 0 && !shuffle){
                position = allSongs.size() - 1;

            }else {
                position--;
            }
            initPlayer(position);
        });

    //        -------------------- next btn ---------------------------
            nextIcon.setOnClickListener(v -> {
            if (position < allSongs.size() - 1 && shuffle) {
                position = getShuffle(allSongs.size() - 1);


            } else if (position < allSongs.size() - 1 && !shuffle){
                position++;

            }else {
                position = 0;
            }
            initPlayer(position);
        });

        //------------repeat btn----------------------
            repeatIcon.setOnClickListener(v -> {
            repeat = !repeat;
            mediaPlayer.setLooping(repeat);

            if (repeat){
                repeatIcon.setBackgroundResource(R.drawable.ic_repeat_click);
            }
            else {
                repeatIcon.setBackgroundResource(R.drawable.ic_repeat);
            }
        });

        //------------shuffle btn--------------------
            shuffleIcon.setOnClickListener(v -> {
            shuffle = !shuffle;
            if (shuffle){
                shuffleIcon.setBackgroundResource(R.drawable.ic_shuffle_click);
            }
            else {
                shuffleIcon.setBackgroundResource(R.drawable.ic_shuffle);
            }
        });

    }


    private void initPlayer(final int position) {

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.reset();
        }

        String sname = allSongs.get(position).getName().replace(".mp3", "").replace(".m4a", "").replace(".wav", "").replace(".m4b", "");
        songTitle.setText(sname);

        Uri songResourceUri = Uri.parse(allSongs.get(position).toString());

        mediaPlayer = MediaPlayer.create(getApplicationContext(), songResourceUri); // create and load mediaplayer with song resources
        mediaPlayer.setOnPreparedListener(mp -> {
            String totalTime = createTimeLabel(mediaPlayer.getDuration());
            totTime.setText(totalTime);
            mSeekBar.setMax(mediaPlayer.getDuration());
            mediaPlayer.setLooping(repeat);
            mediaPlayer.start();
            playIcon.setBackgroundResource(R.drawable.ic_pause);
            //start animation
            startAnimation(imageView);

        });

        mediaPlayer.setOnCompletionListener(mp -> {
            int curPosition = position;

            if (curPosition < allSongs.size() - 1 && shuffle) {
                initPlayer(getShuffle(allSongs.size() - 1));

            } else if (curPosition < allSongs.size() - 1 && !shuffle){
                curPosition++;
                initPlayer(curPosition);

            } else {
                curPosition = 0;
                initPlayer(curPosition);
            }

            //playIcon.setImageResource(R.drawable.ic_play_arrow_black_24dp);

        });


        //set volume bar
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        //get max volume
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        //get current volume
        int curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        volumebar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.seekLine), PorterDuff.Mode.MULTIPLY);
        volumebar.getThumb().setColorFilter(getResources().getColor(R.color.thumbseek), PorterDuff.Mode.SRC_IN);

        volumebar.setMax(maxVolume);
        volumebar.setProgress(curVolume);
        volumebar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //we just want to set the volume so we just put a zero there: youtube said
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //------------- song time seek bar ----------------------------
        //set seekbar color
        mSeekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.seekLine), PorterDuff.Mode.MULTIPLY);
        mSeekBar.getThumb().setColorFilter(getResources().getColor(R.color.thumbseek), PorterDuff.Mode.SRC_IN);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    mSeekBar.setProgress(progress);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        new Thread(() -> {
            while (mediaPlayer != null) {
                try {
//                        Log.i("Thread ", "Thread Called");
                    // create new message to send to handler
                    if (mediaPlayer.isPlaying()) {
                        Message msg = new Message();
                        msg.what = mediaPlayer.getCurrentPosition();
                        handler.sendMessage(msg);
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            Log.i("handler ", "handler called");
            int current_position = msg.what;
            mSeekBar.setProgress(current_position);
            String cTime = createTimeLabel(current_position);
            curTime.setText(cTime);
        }
    };

    public void startAnimation (View view){
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360f);
        //set for one sec
        animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }


    private void play() {

        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            playIcon.setBackgroundResource(R.drawable.ic_pause);
        } else {
            pause();
        }

    }

    private void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            playIcon.setBackgroundResource(R.drawable.ic_play);

        }

    }


    public String createTimeLabel(int duration) {
        String timeLabel = "";
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;

        timeLabel += min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;


    }

    private int getShuffle(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }

}