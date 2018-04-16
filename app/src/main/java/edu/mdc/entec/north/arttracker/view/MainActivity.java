package edu.mdc.entec.north.arttracker.view;


import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;


import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import edu.mdc.entec.north.arttracker.Config;
import edu.mdc.entec.north.arttracker.service.ProximityService;
import edu.mdc.entec.north.arttracker.model.ArtPieceWithArtist;
import edu.mdc.entec.north.arttracker.model.db.AppDatabase;
import edu.mdc.entec.north.arttracker.R;
import edu.mdc.entec.north.arttracker.view.common.GetNameDialogFragment;
import edu.mdc.entec.north.arttracker.view.common.SettingsFragment;
import edu.mdc.entec.north.arttracker.view.gallery.ArtFragmentPagerAdapter;
import edu.mdc.entec.north.arttracker.view.gallery.ArtistFragment;
import edu.mdc.entec.north.arttracker.view.gallery.GalleryFragment;
import edu.mdc.entec.north.arttracker.view.map.MapFragment;
import edu.mdc.entec.north.arttracker.view.quiz.QuizFragment;

import static edu.mdc.entec.north.arttracker.view.gallery.ArtistFragment.YOUTUBE_RECOVERY_REQUEST;


public class MainActivity extends AppCompatActivity
implements GetNameDialogFragment.OnGetNameListener {
    private static final String TAG = "--MainActivity";

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ArtFragmentPagerAdapter adapter;
    private SharedPreferences sharedPref;

    private ArtPieceWithArtist artPiece;
    private int showing;
    private boolean showingList;
    private boolean settingsIsOpen;


    private SettingsFragment f = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        settingsIsOpen = false;

        Log.d(TAG, "-------------------------In the onCreate() method");
        super.onCreate(savedInstanceState);
        artPiece = this.getIntent().getParcelableExtra("ART_PIECE" );
        showing = this.getIntent().getIntExtra("SHOWING", GalleryFragment.SHOWING_ART_PIECE);
        showingList = this.getIntent().getBooleanExtra("SHOWING_LIST", true);


        AppDatabase.getInstance(this);

        setContentView(R.layout.activity_main);

        setUpTabsLayout();

        sayHelloToUser();

        startService();

    }

    private void startService() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean doNotify = sharedPref.getBoolean("pref_notifications", true);
        if(doNotify) {
            Log.d(TAG, "Notifications enabled");
            ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (!ProximityService.class.getName().equals(service.service.getClassName())) {
                    SystemRequirementsChecker.checkWithDefaultDialogs(this);
                    Intent intent = new Intent(this, ProximityService.class);
                    startService(intent);
                }
            }
        } else
            Log.d(TAG, "No notifications");
    }

    private void setUpTabsLayout() {
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);



        // Create the adapter that will return a fragment for each
        // primary sections of the activity.
        adapter = new ArtFragmentPagerAdapter
                (getSupportFragmentManager(), showingList, showing, artPiece);

        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Adding menu icon and icon to Toolbar
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setIcon(R.mipmap.ic_launcher);
        }

    }


    private void sayHelloToUser() {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String savedUsername = sharedPref.getString("pref_username", null);

        if(savedUsername == null){
            DialogFragment customFragment = GetNameDialogFragment.newInstance();
            customFragment.show(getSupportFragmentManager(), "custom");
        } else {
            Toast toast = Toast.makeText(this, "Hello " + savedUsername + "!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
            toast.show();
        }


    }

    @Override
    public void onStart() {
        Log.d(TAG, "-------------------------In the onStart() method");
        //This is called at each reconfiguration change
        super.onStart();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        Log.d(TAG, "-------------------------In the onRestart() method");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "-------------------------In the onResume() method fragment"+(GalleryFragment) adapter.getItem(0));
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "-------------------------In the onPause() method");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "-------------------------In the onStop() method");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Intent intent = new Intent(this, ProximityService.class);
        //stopService(intent);
        Log.d(TAG, "-------------------------In the onDestroy() method-------------------------------------------");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        Log.d(TAG, "-------------------------In the onSaveInstanceState() method");
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "-------------------------In the onRestoreInstanceState() method");
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                if(!settingsIsOpen) {
                    f = new SettingsFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.outer, f)
                            .commit();
                } else {
                    getSupportFragmentManager().beginTransaction()
                            .remove(f)
                            .commit();
                }
                settingsIsOpen = !settingsIsOpen;
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public static int getContentViewId() {
        return Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH ? android.R.id.content : R.id.action_bar_activity_content;
    }

    @Override
    public void onGetName(String name) {
        //Write username to shared Preferences
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("pref_username", name);
        editor.commit();
        Toast toast = Toast.makeText(this, "Hello " + name + "!", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
        toast.show();
        Log.d(TAG, "pref_username=" + sharedPref.getString("pref_username", "??????????"));
    }

    @Override
    public void onBackPressed() {

        Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + viewPager.getCurrentItem());

        if(fragment != null && fragment instanceof GalleryFragment){
            if(  !((GalleryFragment) fragment).onBackPressed()  )
                super.onBackPressed();
        } else if(fragment != null && fragment instanceof QuizFragment){
            super.onBackPressed();
        } else if(fragment != null && fragment instanceof MapFragment){
            super.onBackPressed();
        } else
            super.onBackPressed();
    }

    public ArtFragmentPagerAdapter getAdapter() {
        return adapter;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == YOUTUBE_RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            ArtistFragment fragment = (ArtistFragment) getSupportFragmentManager().findFragmentByTag("artistFragment");
            try {
                fragment.getYouTubePlayerProvider().initialize(Config.YOUTUBE_API_KEY, fragment);
            } catch (RuntimeException e) {
                Log.d(TAG, "Problem playing th youtube video");
            }
        }
    }

}
