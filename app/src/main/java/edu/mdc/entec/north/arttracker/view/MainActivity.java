package edu.mdc.entec.north.arttracker.view;


import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityManagerCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import edu.mdc.entec.north.arttracker.ProximityService;
import edu.mdc.entec.north.arttracker.model.ArtPieceWithArtist;
import edu.mdc.entec.north.arttracker.model.db.AppDatabase;
import edu.mdc.entec.north.arttracker.R;
import edu.mdc.entec.north.arttracker.view.common.GetNameDialogFragment;
import edu.mdc.entec.north.arttracker.view.gallery.ArtFragmentPagerAdapter;
import edu.mdc.entec.north.arttracker.view.gallery.GalleryFragment;
import edu.mdc.entec.north.arttracker.view.map.MapFragment;
import edu.mdc.entec.north.arttracker.view.quiz.QuizFragment;


public class MainActivity extends AppCompatActivity
implements GetNameDialogFragment.OnGetNameListener {
    private static final String TAG = "--MainActivity";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ArtFragmentPagerAdapter adapter;
    private SharedPreferences sharedPref;

    private ArtPieceWithArtist artPiece;
    private int showing;
    private boolean showingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "-------------------------In the onCreate() method");
        super.onCreate(savedInstanceState);
        artPiece = this.getIntent().getParcelableExtra("ART_PIECE" );
        showing = this.getIntent().getIntExtra("SHOWING", GalleryFragment.SHOWING_ART_PIECE);
        showingList = this.getIntent().getBooleanExtra("SHOWING_LIST", true);


        AppDatabase.getInstance(this);

        setContentView(R.layout.activity_main);

        setUpTabsLayout();

        setUpDrawerLayout();

        sayHelloToUser();

        startService();

    }

    private void startService() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service: manager.getRunningServices(Integer.MAX_VALUE)){
            if(!ProximityService.class.getName().equals(service.service.getClassName())){
                SystemRequirementsChecker.checkWithDefaultDialogs(this);
                Intent intent = new Intent(this, ProximityService.class);
                startService(intent);
            }
        }
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

    }

    private void setUpDrawerLayout() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Adding menu icon and icon to Toolbar
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setIcon(R.mipmap.ic_launcher);

            ab.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            ab.setDisplayHomeAsUpEnabled(true);
        }

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
                if(menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()){
                    //Replacing the main content with ContentFragment Which is  View;
                    case R.id.home2:
                        Log.d(TAG, "drawer home clicked ");
                        return true;
                    case R.id.favorite:
                        Log.d(TAG, "favorite home clicked ");
                        return true;
                    case R.id.bookmark:
                        Log.d(TAG, "bookmark home clicked ");
                        return true;
                    default:
                        Log.d(TAG, "?? ");
                        return true;
                }
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout,myToolbar,R.string.openDrawer, R.string.closeDrawer){

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes
                // as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as
                // we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    private void sayHelloToUser() {
        sharedPref = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        //read the username property from the sharedPreferences
        String savedUsername = sharedPref.getString("userName", null);

        if(savedUsername == null){
            DialogFragment customFragment = GetNameDialogFragment.newInstance();
            customFragment.show(getSupportFragmentManager(), "custom");
        } else {
            FileInputStream fin = null;
            StringBuilder temp = new StringBuilder();
            try {
                fin = openFileInput("secret");

                int c;
                while( (c = fin.read()) != -1){
                    temp.append(Character.toString((char)c));
                }
                fin.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
                // User chose the "Settings" item, show the app settings UI...
                Log.d(TAG, "Setting menu item clicked");
                return true;

            case R.id.action_favorite:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                Log.d(TAG, "Favorite menu item clicked");
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onGetName(String name) {
        //Write username to shared Preferences
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("userName", name);
        editor.commit();


        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput("secret", Context.MODE_PRIVATE);
            outputStream.write("password".getBytes());
            outputStream.close();

        } catch (FileNotFoundException e) {
            Log.w("Warning", "The file secret was not found");
        } catch (IOException e) {
            Log.w("Warning", "Error reading The file ");
        }


        Toast toast = Toast.makeText(this, "Hello " + name + "! Your password is " + "password", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 10, 10);
        toast.show();
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
}
