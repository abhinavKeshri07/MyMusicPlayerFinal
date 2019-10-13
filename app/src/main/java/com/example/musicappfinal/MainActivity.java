package com.example.musicappfinal;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String SHARED_PREF = "player_state";
    private static final String CURRENT_SONG = "current_song";
    private AppBarConfiguration mAppBarConfiguration;
    private NotificationManagerCompat notificationManager;

    //Asking for permissions
    public String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECORD_AUDIO
    };
    private int STORAGE_PERMISSION_CODE = 1;



    //Playing songs
    AudioManager audioManager = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        notificationManager = NotificationManagerCompat.from(this);
        Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_1_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Title")
                .setContentText("this is content text.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();
        notificationManager.notify(1, notification);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        //checking for permissions
        if(ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Permissions were given", Toast.LENGTH_LONG).show();
            FavSongsViewModel testViewModel = ViewModelProviders.of(this).get(FavSongsViewModel.class);
            testViewModel.refreshSongs();
        }
        else{
            requestStoragePermission();
        }


    }

    private void requestStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(this)
                    .setTitle("permission needed")
                    .setMessage("This permission is needed to read External storage for music files.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, permissions, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        }else{
            ActivityCompat.requestPermissions(this, permissions, STORAGE_PERMISSION_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == STORAGE_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission granted", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this, "Permission was not given.", Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FavSongsViewModel favSongsViewModel = ViewModelProviders.of(this).get(FavSongsViewModel.class);
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF,MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        try {
            editor.putInt(CURRENT_SONG,favSongsViewModel.getCurrentSong().getValue());
        }catch (NullPointerException e){
            editor.putInt(CURRENT_SONG,-1);
        }


        editor.apply();

    }
}
