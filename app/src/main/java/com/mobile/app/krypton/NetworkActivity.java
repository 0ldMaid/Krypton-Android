package com.mobile.app.krypton;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v7.app.AlertDialog;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;


public class NetworkActivity extends PreferenceActivity {

    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;

    private final static String TAG = "NetworkActivity";

    Timer xtimerx;//class loop.
    Preference network0, network1, network2, network3, network4, network5, network6, network7, network8, network9, network10, network11, network12;

    long miningx_speed1 = 0;//Save the mining noose so we can see how many are new the next time around.

    boolean runningx = false;




public NetworkActivity() {}


    //This is the android settings screen were users can change their info and use some simple tools.

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //MyLog.d(TAG, "onCreate");
        //getFragmentManager().beginTransaction().replace(android.R.id.content, new LocationFragment()).commit();

        addPreferencesFromResource(R.xml.content_network);


        network0 = findPreference("network0");
        network0.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                //Code for what you want it to do.
                System.out.println("CLICK");



                return true;

            }
        });


        network1 = findPreference("network1");
        network2 = findPreference("network2");
        network3 = findPreference("network3");
        network4 = findPreference("network4");
        network5 = findPreference("network5");
        network6 = findPreference("network6");
        network7 = findPreference("network7");
        network8 = findPreference("network8");
        network9 = findPreference("network9");
        network10 = findPreference("network10");
        network11 = findPreference("network11");
        network12 = findPreference("network12");


        runningx = true;

        xtimerx = new Timer();
        xtimerx.schedule(new RemindTask_show_stats(), 0);

    }//onCreate




    public void onBackPressed() {

        super.onBackPressed();
        System.out.println("Exit!");

        runningx = false;

        finish();

    }//***************************




    class RemindTask_show_stats extends TimerTask {

        Runtime rxrunti = Runtime.getRuntime();

        public void run(){//************************************************************************


            while(runningx) {

                try {



                    System.out.println("Show network stats...");


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Long thisTick = System.currentTimeMillis();
                            Long block_timex = (long) 0;

                            try {

                                block_timex = ((thisTick - network.last_block_time) / 1000);

                            } catch (Exception e) {

                                e.printStackTrace();

                            }//********************


                            network0.setSummary("Full node: " + network.full_node);
                            network1.setSummary("Mining active: " + network.xmining + "\nHashes/s: " + Long.toString(network.mining_speed_display));
                            network2.setSummary("Server active: " + network.server + "\nHits: " + Integer.toString(network.website_hits));

                            //Show how many hashes have been done since the last check.
                            network.mining_speed_display = mining.noosex - miningx_speed1;

                            network3.setSummary(network.hard_token_count + " (" + network.hard_token_limit + ")");
                            network4.setSummary(Long.toString(network.show_difficulty));
                            network5.setSummary(Long.toString(network.blocktimesx / 1000) + " (" + (network.target_block_speed / 1000) + ")");
                            network6.setSummary(Long.toString(block_timex));
                            network7.setSummary(network.last_block_id);
                            network8.setSummary("Remote DB(" + network.database_unconfirmed_total + ") Local DB(" + network.send_buffer_size + ")");

                            network9.setSummary(network.peerid0 + "\nActive: " + network.peersx0);
                            network10.setSummary(network.peerid1 + "\nActive: " + network.peersx1);
                            network11.setSummary(network.peerid2 + "\nActive: " + network.peersx2);
                            network12.setSummary(network.peerid3 + "\nActive: " + network.peersx3);

                            getWindow().getDecorView().findViewById(android.R.id.content).invalidate();

                        }//@Override

                    });



                    //ViewGroup vg = findViewById(R.xml.content_network);
                    //vg.invalidate();

                    miningx_speed1 = mining.noosex;

                    try{Thread.sleep(1000);}catch(InterruptedException e){}

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }//while*****

            System.out.println("Exit loop...");

        }//runx*************************************************************************************

    }//remindtask









    public static class LocationFragment extends PreferenceFragment {

        private final static String TAG = "SettingsFragment";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //MyLog.d(TAG, "onCreate");
            addPreferencesFromResource(R.xml.content_network);

        }//**********************************************

    }//**************************************************************



}//last