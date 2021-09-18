package com.mobile.app.krypton;

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
import android.support.v7.app.AlertDialog;
import android.widget.CheckBox;
import android.widget.Toast;


public class SettingsActivity extends PreferenceActivity {

    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;

    private final static String TAG = "SettingsActivity";

    public SettingsActivity() {}


    //This is the android settings screen were users can change their info and use some simple tools.

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //MyLog.d(TAG, "onCreate");
        //getFragmentManager().beginTransaction().replace(android.R.id.content, new LocationFragment()).commit();

        addPreferencesFromResource(R.xml.content_settings);


        //Set the version code.
        Preference buttonid = findPreference("programid");
        buttonid.setSummary(network.versionx);



        Preference button_network = findPreference("network_key");
        button_network.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                //Code for what you want it to do.
                System.out.println("CLICK");

                Intent i = new Intent(getBaseContext(), NetworkActivity.class);
                //i.putExtra("Transfer Token", "Transfer");
                startActivity(i);

                return true;

            }
        });


        //Checkbox.
        Preference checkbox0 = findPreference("user_peer_only");
        checkbox0.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //Code for what you want it to do.
                System.out.println("CLICK");

                network.use_one_peer = MainActivity.settings.getBoolean("user_peer_only", false);

                System.out.println("use_one_peer " + network.use_one_peer);

                return true;
            }
        });

        Preference checkbox1 = findPreference("add_node_onion");
        checkbox1.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //Code for what you want it to do.
                System.out.println("CLICK");

                network.add_node_onion = MainActivity.settings.getBoolean("add_node_onion", false);

                System.out.println("add_node_onion " + network.add_node_onion);

                return true;
            }
        });





        final CheckBoxPreference check2 = (CheckBoxPreference) findPreference("full_node");
        final CheckBoxPreference check3 = (CheckBoxPreference) findPreference("mining_node");
        final CheckBoxPreference check4 = (CheckBoxPreference) findPreference("server_node");

        Preference checkbox2 = findPreference("full_node");
        checkbox2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //Code for what you want it to do.
                System.out.println("CLICK");

                network.full_node = MainActivity.settings.getBoolean("full_node", false);

                System.out.println("full_node " + network.full_node);

                if(!network.full_node){

                    check3.setChecked(false);
                    check4.setChecked(false);

                }//********************

                return true;
            }
        });


        Preference checkbox3 = findPreference("mining_node");
        checkbox3.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //Code for what you want it to do.
                System.out.println("CLICK");

                if(network.full_node && network.hard_token_count == network.hard_token_limit) {

                    network.xmining = MainActivity.settings.getBoolean("mining_node", false);

                    System.out.println("mining_node " + network.xmining);

                }//****************************************************************************
                else{

                    Toast.makeText(getApplicationContext(), (String) "You can not mine unless you are a full node and have the whole Blockchain.", Toast.LENGTH_LONG).show();

                    check3.setChecked(false);

                    return false;

                }//**

                return true;
            }
        });


        final Preference checkbox4 = findPreference("server_node");
        checkbox4.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
            //Code for what you want it to do.
            System.out.println("CLICK");


            if(network.full_node && network.hard_token_count == network.hard_token_limit) {

                network.server = MainActivity.settings.getBoolean("server_node", false);

                System.out.println("server_node " + network.server);

            }//****************************************************************************
            else{

                Toast.makeText(getApplicationContext(), (String) "You can not be a server unless you are a full node and have the whole Blockchain.", Toast.LENGTH_LONG).show();

                check4.setChecked(false);

                return false;

            }//**

                return true;
            }
        });



        //Google doesn't allow mining on the play store so we disable that ability here.
        if(network.play_store_version){

            network.full_node = false;
            network.xmining = false;
            network.server = false;

            check2.setChecked(false);
            check3.setChecked(false);
            check4.setChecked(false);

            checkbox2.setEnabled(false);
            checkbox3.setEnabled(false);
            checkbox4.setEnabled(false);

        }//****************************






        Preference button1 = findPreference("newkey");
        button1.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //Code for what you want it to do
                System.out.println("CLICK");


                AlertDialog.Builder builder = new AlertDialog.Builder(getWindow().getDecorView().getRootView().getContext());

                builder.setTitle("Confirm");
                builder.setMessage("Are you sure you want delete your old account?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        //Do nothing but close the dialog

                        krypton_rebuild_keys keysx = new krypton_rebuild_keys();

                        krypton_database_load loadx = new krypton_database_load();
                        loadx.load();

                        dialog.dismiss();

                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();


                return true;
            }
        });

        Preference button2 = findPreference("getonion");
        button2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //code for what you want it to do.
                System.out.println("CLICK");

                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(".onion", krypton_net_client.serverOnionAddress);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getApplicationContext(), (String) "Server address copied to clipboard", Toast.LENGTH_LONG).show();

                return true;
            }
        });

        Preference button3 = findPreference("getpubkey");
        button3.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //code for what you want it to do.
                System.out.println("CLICK");

                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Public Key", network.base58_id);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getApplicationContext(), (String) "Public Key address copied to clipboard", Toast.LENGTH_LONG).show();

                return true;
            }
        });

        Preference button4 = findPreference("getkey");
        button4.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //code for what you want it to do.
                System.out.println("CLICK");

                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Private Key", network.prv_key_id);//network.settingsx[4]
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getApplicationContext(), (String) "Private Key address copied to clipboard", Toast.LENGTH_LONG).show();

                return true;
            }
        });





        Preference button5 = findPreference("resetblockchain");
        button5.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //Code for what you want it to do
                System.out.println("CLICK");

                AlertDialog.Builder builder = new AlertDialog.Builder(getWindow().getDecorView().getRootView().getContext());

                builder.setTitle("Confirm");
                builder.setMessage("Are you sure you want delete the Blockchain?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        //Do nothing but close the dialog

                        //Sometimes if the miner tries to load the database the system will crash.
                        //When we are trying to delete all the items.
                        mining.mining1 = false;
                        network.reset_db = true;

                        krypton_database_reset_blockchain resetx = new krypton_database_reset_blockchain();
                        resetx.resetBlockchain();

                        krypton_database_load loadx = new krypton_database_load();
                        loadx.load();

                        mining.mining1 = true;
                        network.reset_db = false;

                        dialog.dismiss();

                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //Do nothing
                        dialog.dismiss();

                    }
                });

                AlertDialog alert = builder.create();
                alert.show();


                return true;
            }
        });


        Preference button6 = findPreference("deleteblock");
        button6.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                //Code for what you want it to do.
                System.out.println("CLICK");

                krypton_update_block_stale testx = new krypton_update_block_stale();
                boolean test1 = testx.update();

                Toast.makeText(getApplicationContext(), (String) "Most recent block deleted!", Toast.LENGTH_LONG).show();

                return true;

            }
        });


        Preference button7 = findPreference("deleteunconfirmed");
        button7.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                //Code for what you want it to do.
                System.out.println("CLICK");

                krypton_database_delete_unconfirmed unconfirmedd = new krypton_database_delete_unconfirmed();
                unconfirmedd.deleteAll();

                krypton_database_delete_buffer bufferd = new krypton_database_delete_buffer();
                bufferd.deleteAll();

                krypton_database_load loadx = new krypton_database_load();
                loadx.load();

                Toast.makeText(getApplicationContext(), (String) "Unconfirmed database deleted!", Toast.LENGTH_LONG).show();

                return true;

            }
        });


        Preference button8 = findPreference("printblocks");
        button8.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                //Code for what you want it to do.
                System.out.println("CLICK");

                if (network.full_node) {

                    Intent i = new Intent(getBaseContext(), BlockchainActivity.class);
                    //i.putExtra("Transfer Token", "Transfer");
                    startActivity(i);

                }
                else {

                    Toast.makeText(getApplicationContext(), (String) "No blocks to display. You are NOT a full node!", Toast.LENGTH_LONG).show();

                }

                return true;

            }
        });


        Preference button9 = findPreference("getnodes");
        button9.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                //Code for what you want it to do.
                System.out.println("CLICK");

                krypton_database_node nodes = new krypton_database_node();
                nodes.refreshBlockchain();

                Toast.makeText(getApplicationContext(), (String) "Rebuild node list complete...", Toast.LENGTH_LONG).show();

                return true;

            }
        });





        //Listener on changed sort order preference:
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.context2);

        prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

            System.out.println("CHANGE: " + key);

            if(key.equals("importkey")) {

                String newkey = prefs.getString("importkey", "");

                System.out.println("key: " + newkey.length());

                if(newkey.length() > 0) {//****************

                    krypton_database_import_keys importk = new krypton_database_import_keys(newkey);

                    krypton_database_load_network networkk = new krypton_database_load_network();

                    krypton_database_load loadx = new krypton_database_load();
                    loadx.load();

                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putString("importkey", "");
                    edit.commit();

                }//*****************************************

            }//***************************

            if(key.equals("peerid")) {

                network.onionAddress = MainActivity.settings.getString("peerid", "defaultValue");

            }//***********************

        }//*************************************************************************
        };

        prefs.registerOnSharedPreferenceChangeListener(prefListener);

    }//onCreate


    public static class LocationFragment extends PreferenceFragment {

        private final static String TAG = "SettingsFragment";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //MyLog.d(TAG, "onCreate");
            addPreferencesFromResource(R.xml.content_settings);

        }//**********************************************

    }//**************************************************************



}//last