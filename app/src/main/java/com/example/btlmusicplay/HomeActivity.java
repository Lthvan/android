package com.example.btlmusicplay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
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

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    EditText editTextSearch;
    Button btnSearch;
    String[] items;
    TextView textView;
    TextView author;
    ArrayList<File> songs;
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
        CustomAdapter updatedAdapter = new CustomAdapter(updatedSongs);
        recyclerView.setAdapter(updatedAdapter);
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
        items = new String[songs.size()];
        for (int i = 0; i < songs.size(); i++) {
            items[i] = songs.get(i).getName()
                    .replace(".mp3","")
                    .replace(".wav","");
            CustomAdapter customAdapter = new CustomAdapter(songs);
            recyclerView.setAdapter(customAdapter);
            recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                @Override
                public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                    View child = rv.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && e.getAction() == MotionEvent.ACTION_UP) {
                        int position = rv.getChildAdapterPosition(child);
                        String songName = songs.get(position).getName().replace(".mp3", "").replace(".wav", "");
                        startActivity(new Intent(getApplicationContext(), PlayerActivity.class)
                                .putExtra("songs", songs)
                                .putExtra("songName", songName)
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

    }
    class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder>{
        private ArrayList<File> songs;

        public CustomAdapter(ArrayList<File> songs) {
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
            String songName = songs.get(position).getName().replace(".mp3", "").replace(".wav", "");
            holder.songName.setText(songName);
        }

        @Override
        public int getItemCount() {
            return songs.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView songName;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                songName = itemView.findViewById(R.id.txtSongName);
            }
        }
    }
}