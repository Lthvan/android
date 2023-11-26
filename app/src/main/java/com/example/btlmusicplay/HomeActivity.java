package com.example.btlmusicplay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.btlmusicplay.model.Song;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

public class HomeActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    EditText editTextSearch;
    Button btnSearch;
    String[] items;
    TextView textView;
    TextView author;
    ArrayList<File> songs;
    ArrayList<Song> songList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        recyclerView = findViewById(R.id.recyclerViewSong);
        editTextSearch = findViewById(R.id.editTextSearch);
        btnSearch = findViewById(R.id.btnSearch);
        runTimePermission();
        recyclerView = findViewById(R.id.recyclerViewSong);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchKeyword = editTextSearch.getText().toString().toLowerCase().trim();
                if (!searchKeyword.isEmpty()) {
                    ArrayList<File> filteredSongs = filterSongsByKeyword(songs, searchKeyword);
                    updateRecyclerView(filteredSongs);
                } else {
                    // Nếu ô tìm kiếm trống, hiển thị toàn bộ danh sách
                    updateRecyclerView(songs);
                }
            }
        });
    }
    private ArrayList<File> filterSongsByKeyword(ArrayList<File> songs, String keyword) {
        ArrayList<File> filteredSongs = new ArrayList<>();
        for (File song : songs) {
            String songName = song.getName().replace(".mp3", "").replace(".wav", "").toLowerCase();
            if (songName.contains(keyword)) {
                filteredSongs.add(song);
            }
        }
        return filteredSongs;
    }
    private void updateRecyclerView(ArrayList<File> updatedSongs) {
        // Chuyển đổi danh sách File sang danh sách Song
        songList = new ArrayList<>();
        ArrayList<File> fileArrayAfterSearch = new ArrayList<>();
        int positionInFiles = 0;
        for (File file : updatedSongs) {
            String title = file.getName().replace(".mp3", "").replace(".wav", "");
            AudioFile audioFile = null;
            String artist ="";
            String songDuration = "";
            try {
                audioFile = AudioFileIO.read(file);
                Tag tag = audioFile.getTag();
                artist = tag.getFirst(FieldKey.ARTIST);
                // Get the audio header
                org.jaudiotagger.audio.AudioHeader audioHeader = audioFile.getAudioHeader();
                songDuration = convertSecondsToMinutes(audioHeader.getTrackLength());
            } catch (CannotReadException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (TagException e) {
                throw new RuntimeException(e);
            } catch (ReadOnlyFileException e) {
                throw new RuntimeException(e);
            } catch (InvalidAudioFrameException e) {
                throw new RuntimeException(e);
            }
            String path = file.getAbsolutePath();
            fileArrayAfterSearch.add(file);
            Song song = new Song(title, artist, path, songDuration);
            positionInFiles++;
            songList.add(song);
        }

        // Sử dụng adapter với danh sách Song
        if(songList.size()<songs.size()){
            playSongs(songList,fileArrayAfterSearch);
        }else{
            playSongs();
        }
    }
    public void runTimePermission(){
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        playSongs();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }
    public ArrayList<File> findSong(File file){
        ArrayList<File> arrayStoredFile = new ArrayList<>();
        File[] files = file.listFiles();
        if(files != null){
            for (File file1:files){
                if(file1.isDirectory() && !file1.isHidden()){
                    arrayStoredFile.addAll(findSong(file1));
                }else {
                    if (file1.getName().endsWith(".mp3") || file1.getName().endsWith(".wav")) {
                        arrayStoredFile.add(file1);
                    }
                }
            }

        }
        return arrayStoredFile;
    }
    void playSongs(){
        songs = findSong(Environment.getExternalStorageDirectory());
        songList = new ArrayList<>();
        String title = "";
        String artist = "";
        String songDuration = "";
        for (File file : songs) {
            try {
                AudioFile audioFile = AudioFileIO.read(file);
                // Get the tag from the audio file
                Tag tag = audioFile.getTag();
                title = tag.getFirst(FieldKey.TITLE);
                artist = tag.getFirst(FieldKey.ARTIST);
                // Get the audio header
                org.jaudiotagger.audio.AudioHeader audioHeader = audioFile.getAudioHeader();
                songDuration = convertSecondsToMinutes(audioHeader.getTrackLength());
            } catch (CannotReadException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (TagException e) {
                throw new RuntimeException(e);
            } catch (ReadOnlyFileException e) {
                throw new RuntimeException(e);
            } catch (InvalidAudioFrameException e) {
                throw new RuntimeException(e);
            }
            String path = file.getAbsolutePath();
            Song song = new Song(title, artist, path, songDuration);
            songList.add(song);
        }
        // sap xep theo thoi luong giam dan
//        sortSongsByDuration();
        CustomAdapter customAdapter = new CustomAdapter(songList);
        recyclerView.setAdapter(customAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                @Override
                public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                    View child = rv.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && e.getAction() == MotionEvent.ACTION_UP) {
                        int position = rv.getChildAdapterPosition(child);
                        String songName = songList.get(position).getTitle().replace(".mp3", "").replace(".wav", "");
                        String songArtist = songList.get(position).getArtist();
                        startActivity(new Intent(getApplicationContext(), PlayerActivity.class)
                                .putExtra("songs", songs)
                                .putExtra("songList",songList)
                                .putExtra("songName", songName)
                                .putExtra("songArtist",songArtist)
                                .putExtra("position", position)
                        );
                    }
                    return false;
                }

                @Override
                public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                }

                @Override
                public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
                }
            });
    }
    // ham chuyen tu dang mm:ss sang giay
    private int convertDurationToSeconds(String duration) {
        // Convert "mm:ss" format to total seconds
        String[] parts = duration.split(":");
        int minutes = Integer.parseInt(parts[0]);
        int seconds = Integer.parseInt(parts[1]);
        return minutes * 60 + seconds;
    }
    private void sortSongsByDuration() {
        // Sort the songList by duration
        Collections.sort(songList, new Comparator<Song>() {
            @Override
            public int compare(Song song1, Song song2) {
                int duration1 = convertDurationToSeconds(song1.getDuration());
                int duration2 = convertDurationToSeconds(song2.getDuration());
// 2- 1 giam dan
                return Integer.compare(duration2, duration1);
            }
        });
        Collections.sort(songs, new Comparator<File>() {
            @Override
            public int compare(File file1, File file2) {
                long duration1 = getMp3Duration(file1);
                long duration2 = getMp3Duration(file2);

                return Long.compare(duration2, duration1);
            }
        });

    }
    // lay ra thoi luong file mp3 tu path
    private long getMp3Duration(File file) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(file.getPath());

        // Retrieve the duration in milliseconds
        String durationString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

        // Convert duration from string to long
        long duration = Long.parseLong(durationString);

        // Release the MediaMetadataRetriever
        retriever.release();

        return duration;
    }
    public String convertSecondsToMinutes(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        // Format chuỗi để có dạng mm:ss
        String formattedTime = String.format("%02d:%02d", minutes, seconds);

        return formattedTime;
    }
    void playSongs(ArrayList<Song> songsAfterSearch,ArrayList<File> filesAfterSearch){
        CustomAdapter customAdapter = new CustomAdapter(songsAfterSearch);

        recyclerView.setAdapter(customAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && e.getAction() == MotionEvent.ACTION_UP) {
                    int position = rv.getChildAdapterPosition(child);
                    String songName = songsAfterSearch.get(position).getTitle().replace(".mp3", "").replace(".wav", "");
                    String songArtist = songsAfterSearch.get(position).getArtist();
                    startActivity(new Intent(getApplicationContext(), PlayerActivity.class)
                            .putExtra("songs", filesAfterSearch)
                            .putExtra("songList",songList)
                            .putExtra("songName", songName)
                            .putExtra("songArtist",songArtist)
                            .putExtra("position", position)
                    );
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });
    }
    class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder>{
        private ArrayList<Song> songs;

        public CustomAdapter(ArrayList<Song> songs) {
            this.songs = songs;
        }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_song, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Song currentsong = songs.get(position);
            String songTitleC = currentsong.getTitle();
            String songArtistC = currentsong.getArtist();
            String songDuration = currentsong.getDuration();
            holder.songName.setText(songTitleC);
//            holder.songArtist.setText(songArtistC);
//            holder.songDuration.setText(songDuration);
        }

        @Override
        public int getItemCount() {
            return songs.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView songName;
            TextView songArtist;
            TextView songDuration;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                songName = itemView.findViewById(R.id.txtSongName);
//                songArtist = itemView.findViewById(R.id.txtSongArtist);
//                songDuration = itemView.findViewById((R.id.txtSongDuration));

            }
        }
    }
}