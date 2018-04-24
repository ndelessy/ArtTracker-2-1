package edu.mdc.entec.north.arttracker.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Implementation of headless Fragment that runs an AsyncTask to fetch data from the network.
 */
public class ImageDownloadFragment extends Fragment {
    public static final String TAG = ",,ImageDownloadFragment";

    private static final String IMAGEID_KEY = "ImageIDKey";
    private static final String urlPath = "https://arttracker-2d914.firebaseapp.com/images/";

    private ImageDownloadCallback mCallback;
    private ImageDownloadTask mDownloadTask;
    private String[] imageIDs;

    public static ImageDownloadFragment getInstance(FragmentManager fragmentManager, String[] imageIDs) {
        // Recover NetworkFragment in case we are re-creating the Activity due to a config change.
        // This is necessary because ImageDownloadFragment might have a task that began running before
        // the config change and has not finished yet.
        // The NetworkFragment is recoverable via this method because it calls
        // setRetainInstance(true) upon creation.
        ImageDownloadFragment networkFragment = (ImageDownloadFragment) fragmentManager
                .findFragmentByTag(ImageDownloadFragment.TAG);
        if (networkFragment == null) {
            networkFragment = new ImageDownloadFragment();
            Bundle args = new Bundle();
            args.putStringArray(IMAGEID_KEY, imageIDs);
            networkFragment.setArguments(args);
            fragmentManager.beginTransaction().add(networkFragment, TAG).commit();
        }
        return networkFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retain this Fragment across configuration changes in the host Activity.
        setRetainInstance(true);
        imageIDs = getArguments().getStringArray(IMAGEID_KEY);
        startDownload();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Host Activity will handle callbacks from task.
        mCallback = (ImageDownloadCallback)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // Clear reference to host Activity.
        mCallback = null;
    }

    @Override
    public void onDestroy() {
        // Cancel task when Fragment is destroyed.
        cancelDownload();
        super.onDestroy();
    }

    /**
     * Start non-blocking execution of DownloadTask.
     */
    public void startDownload() {
        cancelDownload();
        mDownloadTask = new ImageDownloadTask();
        mDownloadTask.execute(imageIDs);
    }

    /**
     * Cancel (and interrupt if necessary) any ongoing DownloadTask execution.
     */
    public void cancelDownload() {
        if (mDownloadTask != null) {
            mDownloadTask.cancel(true);
            mDownloadTask = null;
        }
    }

    /**
     * Implementation of AsyncTask that runs a network operation on a background thread.
     */
    private class ImageDownloadTask extends AsyncTask<  String, Void, Void   > {



        /**
         * Cancel background network operation if we do not have network connectivity.
         */
        @Override
        protected void onPreExecute() {
            if (mCallback != null) {
                NetworkInfo networkInfo = mCallback.getActiveNetworkInfo();
                if (networkInfo == null || !networkInfo.isConnected() ||
                        (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                                && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
                    // If no connectivity, cancel task and update Callback with null data.
                    cancel(true);
                }
            }
        }

        /**
         * Defines work to perform on the background thread.
         */
        @Override
        protected Void doInBackground(String[] imageIDs) {
            if (!isCancelled() && imageIDs != null && imageIDs.length > 0) {
                for(String imageID: imageIDs) {
                    try {
                        downloadUrl(imageID);
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());

                    }
                }

            }
            return null;
        }

        /**
         * Updates the DownloadCallback with the result.
         */
        @Override
        protected void onPostExecute(Void voids) {
            mCallback.finishDownloading();
        }


        /**
         * Given a URL, sets up a connection and gets the HTTP response body from the server.
         * If the network request is successful, it returns the response body in String form. Otherwise,
         * it will throw an IOException.
         */
        private void downloadUrl(String imageID) throws IOException {
            String urlString = new StringBuilder(urlPath).append(imageID).append(".png").toString();
            URL url = new URL(urlString);

            Log.d(TAG, "Downloading url: "+url);
            InputStream stream = null;
            HttpsURLConnection connection = null;
            String result = null;
            try {
                connection = (HttpsURLConnection) url.openConnection();
                // Timeout for reading InputStream arbitrarily set to 3000ms.
                connection.setReadTimeout(3000);
                // Timeout for connection.connect() arbitrarily set to 3000ms.
                connection.setConnectTimeout(3000);
                // For this use case, set HTTP method to GET.
                connection.setRequestMethod("GET");
                // Already true by default but setting just in case; needs to be true since this request
                // is carrying an input (response) body.
                connection.setDoInput(true);
                // Open communications link (network traffic occurs here).
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode != HttpsURLConnection.HTTP_OK) {
                    throw new IOException("HTTP error code: " + responseCode);
                }

                Log.d(TAG, "HTTP Response code OK");
                // Retrieve the response body as an InputStream.
                stream = connection.getInputStream();
                if (stream != null) {

                    Bitmap bitmap = BitmapFactory.decodeStream(stream);
                    FileOutputStream foStream;
                    try {
                        File dir = new File(getContext().getFilesDir(), "images");
                        if(! dir.exists()){
                            dir.mkdir();
                        }
                        File f = new File(dir, imageID+".png");
                        foStream = new FileOutputStream(f);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 90, foStream);
                        foStream.flush();
                        foStream.close();
                        Log.d(TAG, "File saved under name "+imageID+".png");
                    } catch (Exception e) {
                        Log.d(TAG, "Error saving to a file");
                        e.printStackTrace();
                    }

                }
            } finally {
                // Close Stream and disconnect HTTPS connection.
                if (stream != null) {
                    stream.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }


    }
}

