package com.example.btlmusicplay;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlayerActivity extends AppCompatActivity {
    Button btnPlay,btnNext, btnPrevious, btnShuffle, btnLoop;
    AppCompatImageView btnReturn;
//    AppCompatImageView btnBack;
    CircleImageView imageView;
    TextView startTime, endTime, songTitle, author;
//    Thread updateSeekBar;
    Runnable updateSeekBar;
    SeekBar seekBar;
    public static final String EXTRA_NAME = "song_name";
    static MediaPlayer mediaPlayer;
    int position;
    ArrayList<File> mySongs;
    String seekBarEndTime;
    final Handler handler = new Handler();
    final int delay = 1000;
    boolean isLooping  = false;
    private boolean isSeekBarUpdating = false;
    private boolean isShuffleEnabled = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        getView();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()== android.R.id.home)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

// Đảm bảo giải phóng tài nguyên khi activity hoặc fragment bị hủy
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void getView(){
        btnReturn = findViewById(R.id.btnReturn);
        btnPlay = findViewById(R.id.btnPlay);
        startTime = findViewById(R.id.startTime);
        endTime = findViewById(R.id.endTime);
        songTitle = findViewById(R.id.songName);
        imageView = findViewById(R.id.circleimageviewPlayer);
        author = findViewById(R.id.authorName);
        btnLoop= findViewById(R.id.btnLoop);
        btnShuffle = findViewById(R.id.btnShuffle);
        btnNext=findViewById(R.id.btnNext);
        btnPrevious=findViewById(R.id.btnPrevious);
        seekBar = findViewById(R.id.seekBar);
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mySongs = (ArrayList) bundle.getParcelableArrayList("songs");
        String songName = intent.getStringExtra("songName");
        position = bundle.getInt("position",0);
        songTitle.setSelected(true);
        Uri uri = Uri.parse(mySongs.get(position).toString());
        songTitle.setText(songName);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();
        updateSeekBar = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying() && isSeekBarUpdating) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                    startTime.setText(createTime(currentPosition));
                    handler.postDelayed(this, 1000);
                }
            }
        };
        seekBar.setMax(mediaPlayer.getDuration());
//        updateSeekBar.start();
        seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.MULTIPLY);
        seekBar.getThumb().setColorFilter(getResources().getColor(R.color.purple_200),PorterDuff.Mode.SRC_IN);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
        seekBarEndTime = createTime(mediaPlayer.getDuration());
        endTime.setText(seekBarEndTime);



        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                String currentTime = createTime(mediaPlayer.getCurrentPosition());
//                startTime.setText(currentTime);
//                handler.postDelayed(this,delay);
                if (mediaPlayer != null && mediaPlayer.isPlaying() && isSeekBarUpdating) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                    startTime.setText(createTime(currentPosition));
                    seekBarEndTime = createTime(mediaPlayer.getDuration());
                    endTime.setText(seekBarEndTime);
                    handler.postDelayed(this, delay);
                }
            }
        },delay);

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(PlayerActivity.this, HomeActivity.class);
//                startActivity(intent);
//                Log.d("IntentDebug", "Intent: " + intent.toString());
//                finish();  // Kết thúc PlayerActivity để không quay lại khi nhấn nút back
                onBackPressed();
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
                    btnPlay.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);
                    mediaPlayer.pause();
                }else {
                    btnPlay.setBackgroundResource(R.drawable.ic_pause_black_24dp);
                    mediaPlayer.start();
                }
            }
        });
        //next listener
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                btnNext.performClick();
            }
        });
        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Implement shuffle logic here
                isShuffleEnabled = !isShuffleEnabled;
                // Toggle shuffle status
                if (isShuffleEnabled) {
                    btnShuffle.setBackgroundResource(R.drawable.ic_shuffle); // Set enabled state drawable
                } else {
                    btnShuffle.setBackgroundResource(R.drawable.ic_shuffle_disabled); // Set disabled state drawable
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();

//                if (isShuffleEnabled()) {
//                    // If shuffle is enabled, shuffle the songs and play the first song
//                    shuffleSongs();
//                } else {
//                    // If shuffle is not enabled, proceed with the normal next song logic
//                    position = (position + 1) % mySongs.size();
//                }


                position = ((position+1)%mySongs.size());
                Uri uriNextSong = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),uriNextSong);
                songTitle.setText(mySongs.get(position).getName());
                btnPlay.setBackgroundResource(R.drawable.ic_pause_black_24dp);
                endTime.setText(createTime(mediaPlayer.getDuration()));
                mediaPlayer.start();
                startSeekBarUpdate(updateSeekBar);
            }
        });
        btnPrevious.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if(position > 0){
                    position = position - 1;
                }else {
                    position = mySongs.size()-1;
                }
                Uri uriPreviousSong = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),uriPreviousSong);
                songTitle.setText(mySongs.get(position).getName());
                btnPlay.setBackgroundResource(R.drawable.ic_pause_black_24dp);
                endTime.setText(createTime(mediaPlayer.getDuration()));
                mediaPlayer.start();
                startSeekBarUpdate(updateSeekBar);
            }
        });
        btnLoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isLooping) {
                    // Nếu không đang lặp lại, bật lặp lại và thay đổi hình ảnh nút
                    isLooping = true;
                    btnLoop.setBackgroundResource(R.drawable.ic_loop); // Thay đổi hình ảnh nút lặp lại đã bật
                } else {
                    // Nếu đang lặp lại, tắt lặp lại và thay đổi hình ảnh nút
                    isLooping = false;
                    btnLoop.setBackgroundResource(R.drawable.ic_loop_non_nactive); // Thay đổi hình ảnh nút lặp lại đã tắt
                }
                mediaPlayer.setLooping(isLooping); // Tắt chế độ lặp lại của MediaPlayer
            }
        });

    }
    public String createTime(int duration){
//        String time = "";
//        int min = duration/1000/60;
//        int second = duration/1000%60;
//        time = min+":";
//        if(second<10){
//            time = time+"0";
//        }
//        time+=second;
//        return time;
        String time = "";
        int seconds = duration / 1000; // chuyển đổi từ mili giây sang giây
        int min = seconds / 60;
        int second = seconds % 60;
        time = min + ":";
        if (second < 10) {
            time = time + "0";
        }
        time += second;
        return time;
    }
    @Override
    public void onBackPressed() {
        // Xử lý khi nhấn nút Back
        super.onBackPressed();
    }
//    private void startSeekBarUpdateThread() {
//        updateSeekBar = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                int totalDuration = mediaPlayer.getDuration();
//                int currentPosition = 0;
//                while (currentPosition < totalDuration) {
//                    try {
//                        currentPosition = mediaPlayer.getCurrentPosition();
//                        seekBar.setProgress(currentPosition);
//                    } catch (IllegalStateException exception) {
//                        exception.printStackTrace();
//                    }
//                }
//            }
//        });
//        seekBar.setMax(mediaPlayer.getDuration());
//        updateSeekBar.start();
//    }
//    private void startSeekBarUpdateThread() {
//        handler.removeCallbacks(updateSeekBar);
//        handler.postDelayed(updateSeekBar, 1000);
//    }
    private void startSeekBarUpdate(Runnable updateSeekBar) {
        isSeekBarUpdating = true;
        handler.postDelayed(updateSeekBar, 1000);
    }

    private void shuffleSongs() {
        Collections.shuffle(mySongs);
        playSong(position); // Play the first song in the shuffled list
    }
    private void playSong(int position) {
        mediaPlayer.stop();
        mediaPlayer.release();
        Uri uri = Uri.parse(mySongs.get(position).toString());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        songTitle.setText(mySongs.get(position).getName());
        btnPlay.setBackgroundResource(R.drawable.ic_pause_black_24dp);
        mediaPlayer.start();
        startSeekBarUpdate(updateSeekBar);
    }
    private boolean isShuffleEnabled() {
        return isShuffleEnabled;
    }
    private void updateShuffleButton(){
        if (isShuffleEnabled) {
            btnShuffle.setBackgroundResource(R.drawable.ic_shuffle); // Set enabled state drawable
        } else {
            btnShuffle.setBackgroundResource(R.drawable.ic_shuffle_disabled); // Set disabled state drawable
        }
    }

}
