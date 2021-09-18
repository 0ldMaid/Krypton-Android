package com.mobile.app.krypton;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.util.Timer;
import java.util.TimerTask;


public class BlockchainActivity extends PreferenceActivity {

private SharedPreferences.OnSharedPreferenceChangeListener prefListener;

private final static String TAG = "BlockchainActivity";

JSONParser parserx = new JSONParser();
krypton_database_print_blocks pblocks = new krypton_database_print_blocks();

Timer xtimerx;//class loop.
Preference[] blockchain = new Preference[100];

long miningx_speed1 = 0;//Save the mining noose so we can see how many are new the next time around.

boolean runningx = false;




public BlockchainActivity() {}


    //This is the android settings screen were users can change their info and use some simple tools.

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //MyLog.d(TAG, "onCreate");
        //getFragmentManager().beginTransaction().replace(android.R.id.content, new LocationFragment()).commit();

        addPreferencesFromResource(R.xml.content_blocks);


        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(this);

        PreferenceCategory category = new PreferenceCategory(this);
        category.setTitle("Blockchain");

        screen.addPreference(category);

        for (int loop = 0; loop < network.print_blocks_size; loop++) {//****************************

            blockchain[loop] = new Preference(this);
            blockchain[loop].setTitle("Block ID: " + loop);
            blockchain[loop].setSummary("summary");

            category.addPreference(blockchain[loop]);
            setPreferenceScreen(screen);

        }//*****************************************************************************************


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


                    final String[][] blockchainx = pblocks.get_blocks();


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



                            for (int loop = 0; loop < network.print_blocks_size; loop++) {//****************************

                                int this_package_sizex = 0;

                                try {

                                    Object objx = parserx.parse(blockchainx[8][loop]);
                                    JSONArray jsonObjectx_this = (JSONArray) objx;

                                    this_package_sizex = jsonObjectx_this.size();

                                } catch(Exception e) {}


                                //blockchain[loop].set
                                blockchain[loop].setTitle("Block Height: " + blockchainx[0][loop]);
                                blockchain[loop].setSummary(

                                        "Block ID: " + blockchainx[1][loop] + "\n" +
                                        "Block Time: " + blockchainx[2][loop] + "\n" +
                                        "Block Nonce: " + blockchainx[3][loop] + "\n" +
                                        "New Mining ID: " + blockchainx[4][loop].substring(0,20) + "...\n" +
                                        "Old Mining ID: " + blockchainx[5][loop].substring(0,20) + "...\n" +
                                        "Block Hash: " + blockchainx[6][loop].substring(0,20) + "...\n" +
                                        "Previous Hash: " + blockchainx[7][loop].substring(0,20) + "...\n" +
                                        "Package Size: " + this_package_sizex + ""

                                );

                            }//*****************************************************************************************



                            getWindow().getDecorView().findViewById(android.R.id.content).invalidate();

                        }//@Override

                    });



                    //ViewGroup vg = findViewById(R.xml.content_network);
                    //vg.invalidate();

                    miningx_speed1 = mining.noosex;

                    try{Thread.sleep(100000);}catch(InterruptedException e){}

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
            addPreferencesFromResource(R.xml.content_blocks);

        }//**********************************************

    }//**************************************************************



}//last