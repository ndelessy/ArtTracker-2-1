package edu.mdc.entec.north.arttracker.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.os.Process;

import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;

import java.util.List;
import java.util.UUID;

import edu.mdc.entec.north.arttracker.R;
import edu.mdc.entec.north.arttracker.model.ArtPieceWithArtist;
import edu.mdc.entec.north.arttracker.model.roomdb.AppDatabase;
import edu.mdc.entec.north.arttracker.view.MainActivity;

import static edu.mdc.entec.north.arttracker.view.gallery.GalleryFragment.SHOWING_ART_PIECE;

public class ProximityService extends Service {

    private static final String TAG = "-m-ProximityService";
    private static final String CHANNEL_ID = "ART_PIECE_CLOSE";
    //A UUID (Universal Unique Identifier) is a 128-bit number used to uniquely identify some object or entity on the Internet.
    private static final String MDC_UUID = "B9407F30-F5F8-466E-AFF9-25556B575555";
    private AppDatabase mDb;

    private BeaconManager beaconManager;


    public ProximityService() {
    }

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            Log.d(TAG, "thread handling message");

            beaconManager = new BeaconManager(getApplicationContext());
            beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
                @Override
                public void onServiceReady() {

                    Log.d(TAG, "beaconmanager connected");
                    for(int major = 1; major <= 3; major++) {
                        for(int minor = 1; major <= 2; major++) {
                            beaconManager.startMonitoring(new BeaconRegion(
                                    "Region major=" + major + ", minor" + minor,
                                    UUID.fromString(MDC_UUID),
                                    major, minor));
                        }
                    }
                }
            });
            beaconManager.setMonitoringListener(new BeaconManager.BeaconMonitoringListener() {
                @Override
                public void onEnteredRegion(BeaconRegion region, List<Beacon> beacons) {
                    for(Beacon b: beacons) {
                        List<ArtPieceWithArtist> artPieces = mDb.artPieceModel().findAllArtPiecesCloseTo(MDC_UUID, b.getMajor(), b.getMinor());
                        Log.d(TAG, "on entering region major="+b.getMajor()+", minor="+b.getMinor());
                        for(ArtPieceWithArtist artPiece :artPieces) {
                            showNotification(artPiece);
                        }
                    }
                }
                @Override
                public void onExitedRegion(BeaconRegion region) {
                    Log.d(TAG, "on leaving region");
                    // could add an "exit" notification too if you want (-:
                }
            });



        }
    }

    private void showNotification(ArtPieceWithArtist artPiece) {
        Log.d(TAG, "showing notif");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "ACTION_NOTIFY_ART_PIECE_CLOSE", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("ACTION_NOTIFY_ART_PIECE_CLOSE");
            channel.setLightColor(Color.CYAN);
            channel.canShowBadge();
            channel.setShowBadge(true);
            // Register the channel with the system
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(ProximityService.this, MainActivity.class);
        intent.putExtra("ART_PIECE", artPiece);
        intent.putExtra("SHOWING", SHOWING_ART_PIECE);
        intent.putExtra("SHOWING_LIST", false);


        PendingIntent pi = PendingIntent.getActivity(ProximityService.this, 0,
                intent, 0);

        Notification notification = new NotificationCompat.Builder(ProximityService.this, CHANNEL_ID)
                .setTicker(getString(R.string.close_to_art_piece_notification))
                .setSmallIcon(R.drawable.ic_notification_palette)
                .setContentTitle(getString(R.string.close_to_art_piece_notification))
                .setContentText(artPiece.getName() +" by " + artPiece.getFirstName() + " " + artPiece.getLastName())
                .setContentIntent(pi)
                .setAutoCancel(false)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(artPiece.getDescription()))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ProximityService.this);
        notificationManager.notify(0, notification);
    }

    @Override
    public void onCreate() {

        mDb = AppDatabase.getInstance(ProximityService.this);
        Log.d(TAG, "on create");

        //SystemRequirementsChecker.checkWithDefaultDialogs(this);
        beaconManager = new BeaconManager(getApplicationContext());

        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "service starting");

        // For each start request, send a message to start a job
        Message msg = mServiceHandler.obtainMessage();
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {

        //Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "service done");

    }
}
