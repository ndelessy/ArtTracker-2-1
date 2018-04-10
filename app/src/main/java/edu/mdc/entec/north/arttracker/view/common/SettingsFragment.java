package edu.mdc.entec.north.arttracker.view.common;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import edu.mdc.entec.north.arttracker.R;
import edu.mdc.entec.north.arttracker.service.ProximityService;

import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;

public class SettingsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        // Load the Preferences from the XML file
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)    {
        if (key.equals("pref_notifications")) {
            boolean doNotify = sharedPreferences.getBoolean("pref_notifications", true);
            if(!doNotify) {
                Intent intent = new Intent(getContext(), ProximityService.class);
                getActivity().stopService(intent);
            } else {
                ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
                for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                    if (!ProximityService.class.getName().equals(service.service.getClassName())) {
                        SystemRequirementsChecker.checkWithDefaultDialogs(getActivity());
                        Intent intent = new Intent(getActivity(), ProximityService.class);
                        getActivity().startService(intent);
                    }
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

}
