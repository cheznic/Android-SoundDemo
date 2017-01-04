package com.whimsygames.sounddemo;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private boolean adjustingProgress = false;
    private SeekBar progressControl;
    private Timer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaPlayer = MediaPlayer.create(this, R.raw.kidlaugh);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        SeekBar volumeControl = (SeekBar) findViewById(R.id.volume);
        volumeControl.setMax(maxVolume);
        volumeControl.setProgress(currentVolume);

        volumeControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        progressControl = (SeekBar) findViewById(R.id.progress);
        progressControl.setMax(mediaPlayer.getDuration());

        progressControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int progress;

            @Override
            public void onProgressChanged(SeekBar progressBar, int progress, boolean b) {
                this.progress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar progressBar) {
                adjustingProgress = true;
                mediaPlayer.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar progressBar) {
                mediaPlayer.seekTo(progress);
                adjustingProgress = false;
                mediaPlayer.start();
            }
        });
    }

    protected void play(View view) {
        mediaPlayer.start();

        if (timer == null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (!adjustingProgress) {
                        progressControl.setProgress(mediaPlayer.getCurrentPosition());
                    }

                    if (mediaPlayer.getDuration() <= mediaPlayer.getCurrentPosition()) {
                        mediaPlayer.pause();
                        mediaPlayer.seekTo(0);
                        progressControl.setProgress(0);
                    }
                }
            }, 0, 80);
            // an algorithm is needed to set the optimal refresh rate based on
            // duration.  Longer duration less frequent updates.  80 is hard coded for this demo.
        }
    }

    protected void pause(View view) {

        mediaPlayer.pause();
    }

    protected void stop(View view) {
        timer.cancel();
        timer = null;
        mediaPlayer.pause();
        mediaPlayer.seekTo(0);
        progressControl.setProgress(0);
    }
}
