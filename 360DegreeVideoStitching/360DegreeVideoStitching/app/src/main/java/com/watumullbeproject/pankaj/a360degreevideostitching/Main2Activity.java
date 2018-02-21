package com.watumullbeproject.pankaj.a360degreevideostitching;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.vr.sdk.widgets.video.VrVideoEventListener;
import com.google.vr.sdk.widgets.video.VrVideoView;

import java.util.Locale;

public class Main2Activity extends AppCompatActivity {

    private static final String TAG = "Main2Activity";
    /**
     * Preserve the video's state and duration when rotating the phone. This improves
     * performance when rotating or reloading the video.
     */
    private static final String STATE_IS_PAUSED = "isPaused";
    private static final String STATE_VIDEO_DURATION = "videoDuration";
    private static final String STATE_PROGRESS_TIME = "progressTime";
    String myDataFromActivity;
    Uri uname;
    /**
     * The video view and its custom UI elements.
     */
    private VrVideoView videoWidgetView;
    /**
     * Seeking UI & progress indicator. The seekBar's progress value represents milliseconds in the
     * video.
     */
    private SeekBar seekBar;
    private TextView statusText;

    /**
     * By default, the video will start playing as soon as it is loaded.
     */
    private boolean isPaused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Bundle bundle = getIntent().getExtras();
        myDataFromActivity = bundle.getString("videofilename");
        Log.i("Player name FileName: ", myDataFromActivity);

        seekBar = (SeekBar) findViewById(R.id.seek_bar1);
        statusText = (TextView) findViewById(R.id.status_text1);
        videoWidgetView = (VrVideoView) findViewById(R.id.video_view1);

        try {
            if ((videoWidgetView != null ? videoWidgetView.getDuration() : 0) <= 0) {
                Log.i(TAG, "onCreate: " + myDataFromActivity);
                if (myDataFromActivity != null) {
                    uname = Uri.parse(myDataFromActivity);
                    Log.i(TAG, "onCreate if ... : " + myDataFromActivity);
                }
                videoWidgetView.loadVideo(uname, new VrVideoView.Options());
            }
        } catch (Exception e) {
            Log.i(TAG, "setUserVisibleHint: " + e.getMessage());
            Toast.makeText(this, "Error opening video: " + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }

        // Add the restore state code here.
        if (savedInstanceState != null) {
            long progressTime = savedInstanceState.getLong(STATE_PROGRESS_TIME);
            videoWidgetView.seekTo(progressTime);
            seekBar.setMax((int) savedInstanceState.getLong(STATE_VIDEO_DURATION));
            seekBar.setProgress((int) progressTime);

            isPaused = savedInstanceState.getBoolean(STATE_IS_PAUSED);
            if (isPaused) {
                videoWidgetView.pauseVideo();
            }
        } else {
            seekBar.setEnabled(false);
        }

        // Add the seekbar listener here.
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                // if the user changed the position, seek to the new position.
                if (fromUser) {
                    videoWidgetView.seekTo(progress);
                    updateStatusText();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // ignore for now.
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // ignore for now.
            }
        });

        // Add the VrVideoView listener here
        videoWidgetView.setEventListener(new VrVideoEventListener() {
            /**
             * Called by video widget on the UI thread when it's done loading the video.
             */
            @Override
            public void onLoadSuccess() {
                Log.i(TAG, "Successfully loaded video " + videoWidgetView.getDuration());
                seekBar.setMax((int) videoWidgetView.getDuration());
                seekBar.setEnabled(true);
                updateStatusText();
            }

            /**
             * Called by video widget on the UI thread on any asynchronous error.
             */
            @Override
            public void onLoadError(String errorMessage) {
                //Toast.makeText(this, "Error loading video: " + errorMessage, Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error loading video: " + errorMessage);
            }

            @Override
            public void onClick() {
                if (isPaused) {
                    videoWidgetView.playVideo();
                } else {
                    videoWidgetView.pauseVideo();
                }

                isPaused = !isPaused;
                updateStatusText();
            }

            /**
             * Update the UI every frame.
             */
            @Override
            public void onNewFrame() {
                updateStatusText();
                seekBar.setProgress((int) videoWidgetView.getCurrentPosition());
            }

            /**
             * Make the video play in a loop. This method could also be used to move to the next video in
             * a playlist.
             */
            @Override
            public void onCompletion() {
                videoWidgetView.seekTo(0);
            }
        });

    }

    private void updateStatusText() {
        String status = (isPaused ? "Paused: " : "Playing: ") +
                String.format(Locale.getDefault(), "%.2f", videoWidgetView.getCurrentPosition() / 1000f) +
                " / " +
                videoWidgetView.getDuration() / 1000f +
                " seconds.";
        statusText.setText(status);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putLong(STATE_PROGRESS_TIME, videoWidgetView.getCurrentPosition());
        savedInstanceState.putLong(STATE_VIDEO_DURATION, videoWidgetView.getDuration());
        savedInstanceState.putBoolean(STATE_IS_PAUSED, isPaused);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Prevent the view from rendering continuously when in the background.
        videoWidgetView.pauseRendering();
        // If the video was playing when onPause() is called, the default behavior will be to pause
        // the video and keep it paused when onResume() is called.
        isPaused = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Resume the 3D rendering.
        videoWidgetView.resumeRendering();
        // Update the text to account for the paused video in onPause().
        updateStatusText();
    }

    @Override
    public void onDestroy() {
        // Destroy the widget and free memory.
        videoWidgetView.shutdown();
        super.onDestroy();
    }
}
