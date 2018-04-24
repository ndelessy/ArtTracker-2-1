package edu.mdc.entec.north.arttracker.utils;

import android.net.NetworkInfo;

public interface ImageDownloadCallback {
    /**
     * Get the device's active network status in the form of a NetworkInfo object.
     */
    NetworkInfo getActiveNetworkInfo();

    /**
     * Indicates that the download operation has finished. This method is called even if the
     * download hasn't completed successfully.
     */
    void finishDownloading();

}
