//This is just the android interface. The real start to the program is "network"


package com.mobile.app.krypton;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.view.MenuItem;
import android.content.Context;
import com.msopentech.thali.android.toronionproxy.AndroidOnionProxyManager;
import com.msopentech.thali.toronionproxy.Utilities;
//import com.msopentech.thali.android.toronionproxy.AndroidOnionProxyManager;

import java.io.File;
import java.io.*;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;



public class MainActivity extends AppCompatActivity {

    Timer xtimerx;//class loop.

    static Context context2;
    AssetManager assetManager;

    long last_exit_click = 0l;//We want to double click to exit in case the user makes a mistake.

    static int hiddenServicePort;
    static int localPort;
    static AndroidOnionProxyManager onionProxyManager;

    TableLayout tableLayout;
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(40, 15);
    LinearLayout.LayoutParams tableParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

    private SwipeRefreshLayout swipeRefreshLayout;
    //private ProgressBar spinner;
    ScrollView scrollview;
    TableLayout tx1;
    String[][] carbon;
    TextView texttest;
    TextView[][] textView = new TextView[4][0];
    TableRow[] tableRow = new TableRow[0];
    TextView[] textspace = new TextView[0];
    Button[][] votex = new Button[2][0];

    TextView textView1;
    TextView textView2;
    TextView textView3;
    TextView textView6;

    CustomTextView searchx;

    Button imageView;
    Bitmap bitmap;

    boolean qr_loaded = false;//The QR image is loaded.
    //boolean startupx = false;//When we first start up we don't want the switches to fire.
    //boolean pause_switches = false;//Stop the user from switching on and off too much.

    static String path = "";//The path to our save folder.
    String picture_hash = "";//This is the current picture we are using. If the hash changes we need to update our picture.
    String coin_transactions_display = "";//Display (+1) (-2) to the user.
    static String bootstrapStatus = "0%";//Show the status of TOR start up.

    static SharedPreferences settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // Make to run your application only in portrait mode
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // Make to run your application only in LANDSCAPE mode
        //setContentView(R.layout.disable_android_orientation_change);

        context2 = this.getApplicationContext();
        assetManager = this.getAssets();

        //These settings are set in the settings screen but we load a few of them here because the system needs them.
        settings = PreferenceManager.getDefaultSharedPreferences(context2);

        network.onionAddress = settings.getString("peerid", "defaultValue");
        network.use_one_peer = settings.getBoolean("user_peer_only", false);
        network.add_node_onion = settings.getBoolean("add_node_onion", false);

        System.out.println(network.onionAddress + " " + network.use_one_peer);  // EditText

        network.full_node = settings.getBoolean("full_node", false);
        network.xmining = settings.getBoolean("mining_node", false);
        network.server = settings.getBoolean("server_node", false);

        System.out.println("network.full_node " + network.full_node);
        System.out.println("network.xmining   " + network.xmining);
        System.out.println("network.server    " + network.server);

        network.peerid0 = settings.getString("peerid0", "");
        network.peerid1 = settings.getString("peerid1", "");
        network.peerid2 = settings.getString("peerid2", "");
        network.peerid3 = settings.getString("peerid3", "");

        network.prv_key_id = settings.getString("prv_key_id", "");
        network.pub_key_id = settings.getString("pub_key_id", "");

        System.out.println("network.prv_key_id " + network.prv_key_id);
        System.out.println("network.pub_key_id " + network.pub_key_id);



        //New item list display table.
        scrollview = ((ScrollView) findViewById(R.id.mainView1));

        tx1 = (TableLayout) findViewById(R.id.mainTableView1);
        tx1.setShrinkAllColumns(true);
        tx1.setPadding(0,20,0,10);

        //spinner = (ProgressBar)findViewById(R.id.mainProgressBar);
        //spinner.setVisibility(View.GONE);

        tableLayout = new TableLayout(getApplicationContext());
        tableLayout.setLayoutParams(tableParams);
        tableLayout.setOrientation(LinearLayout.VERTICAL);



        //Here the user can scroll up to refresh the listings.
        //If they are a full node then it will just refresh from their database.
        //If they are a lite client it will connect to a peer to download the listings.

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                if(!network.full_node){remote_db_new();}
                else{local_db_new();}

            }

        });



        //If the user wants to search for a specific item they can use the text field at the bottom.

        searchx = (CustomTextView)findViewById(R.id.searchx);
        searchx.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                v.setFocusable(true);
                v.setFocusableInTouchMode(true);


                return false;
            }
        });

        searchx.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    //Check if no view has focus:

                    if(!network.full_node){remote_db_search();}
                    else{local_db_search();}

                    //return false;
                }

                return false;

            }
        });



        //These are the floating action buttons next to the QR code they launch activities in the Android app.

        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getBaseContext(), SettingsActivity.class);
                //i.putExtra("Transfer Token", "Transfer");
                startActivity(i);

            }
        });

        FloatingActionButton fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getBaseContext(), EditActivity.class);
                i.putExtra("Edit Token", "Edit");
                startActivity(i);

            }
        });

        FloatingActionButton fab4 = (FloatingActionButton) findViewById(R.id.fab4);
        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getBaseContext(), TransferActivity.class);
                i.putExtra("Transfer Token", "Transfer");
                startActivity(i);

            }
        });




        //MyApplication.context = getApplicationContext();
        path = getApplicationInfo().dataDir;


        //Here we are getting all the references to the many edittexts and switches on the front screen
        textView1 = (TextView) findViewById(R.id.textView1);//tokens
        textView2 = (TextView) findViewById(R.id.textView2);//total
        textView3 = (TextView) findViewById(R.id.textView3);//unconfirmed
        textView6 = (TextView) findViewById(R.id.textView6);//last block time


        //textViewTor = (TextView) findViewById(R.id.textViewTor);//difficulty
        //textViewBlockchain = (TextView) findViewById(R.id.textViewBlockchain);//speed
        //textViewMining = (TextView) findViewById(R.id.textViewMining);//last block time
        //textViewNode = (TextView) findViewById(R.id.textViewNode);//version



        imageView = (Button) findViewById(R.id.imageView1);
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {


                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Public Key", network.base58_id);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getApplicationContext(), (String) "Public Key copied to clipboard.", Toast.LENGTH_LONG).show();

            }

        });



        xtimerx = new Timer();
        xtimerx.schedule(new RemindTask_NetworkFeedTask(), 0);

        xtimerx = new Timer();
        xtimerx.schedule(new RemindTask_showListings(), 0);

        xtimerx = new Timer();
        xtimerx.schedule(new StatusFeedTask(), 0);



    }//last








    public void onBackPressed() {

        System.out.println("Exit!");

        System.out.println("System.currentTimeMillis() " + System.currentTimeMillis());
        System.out.println("last_exit_click            " + last_exit_click);

        System.out.println("difference                 " + (System.currentTimeMillis() - last_exit_click));


        if((System.currentTimeMillis() - last_exit_click) < 1000){System.exit(0);}
        else{Toast.makeText(getApplicationContext(), (String) "Double click to exit.", Toast.LENGTH_LONG).show();}


        last_exit_click = System.currentTimeMillis();


    }//***************************










    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;

    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);

    }






    class RemindTask_RestartServer extends TimerTask{

        Runtime rxrunti = Runtime.getRuntime();

        public void run(){//********************************

            krypton_net_client.start_tor();//restart tor with the new settings

        }//runx*********************************************

    }//remindtask




    class RemindTask_NetworkFeedTask extends TimerTask{

        Runtime rxrunti = Runtime.getRuntime();

        public void run(){//********************************

            network net1 = new network();

        }//runx*********************************************

    }//remindtask






    //Here we get settings for the main switches on the front screen of the app and we set them on or off.
    //The problem is when we set them on or off they get called so we have to protect against that using !startupx

    class RemindTask_showListings extends TimerTask{

        Runtime rxrunti = Runtime.getRuntime();


        public void run(){//********************************

            //Display the new listings.
            System.out.println("Display new listings...");

            try {


                if(!network.full_node) {


                    swipeRefreshLayout.setRefreshing(true);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            texttest = new TextView(context2);
                            //spinner.setVisibility(View.GONE);
                            texttest.setText("Tor is starting...");
                            texttest.setTextColor(Color.parseColor("#000000"));
                            //texttest.setLayoutParams(rowParams);
                            texttest.setTextSize(15);
                            tx1.addView(texttest);

                        }//@Override

                    });

                    while (network.active_peers == 0) {

                        System.out.println("Waiting for tor...");

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }

                        if (network.full_node) {break;}

                    }//******************************

                    remote_db_new();

                }//*************************
                else{

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            texttest = new TextView(context2);
                            //spinner.setVisibility(View.GONE);
                            texttest.setText("Blockchain is downloading...");
                            texttest.setTextColor(Color.parseColor("#000000"));
                            //texttest.setLayoutParams(rowParams);
                            texttest.setTextSize(15);
                            tx1.addView(texttest);

                        }//@Override

                    });


                    while (network.hard_token_count != network.hard_token_limit) {

                        System.out.println("Waiting for blockchain...");

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }

                        if (network.full_node) {break;}

                    }//******************************

                    //If we have all the blocks we show the new listings.
                    local_db_new();

                }//**


            } catch (Exception e) {}



        }//runx*********************************************

    }//remindtask







    //Here we are updating all the info we get from network to display to the user.
    //Originally this was a desktop application it has been converted to android.
    //The network class can run on desktops and phones this MainActivity class is only for android.

    class StatusFeedTask extends TimerTask{

        Runtime rxrunti = Runtime.getRuntime();

        int pause_switch_int = 0;

        public void run(){//********************************


            System.out.println("Run TOR...");


            while(true){

                try {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            try {

                                Long thisTick = System.currentTimeMillis();
                                Long block_timex = (long) 0;

                                try {

                                    block_timex = ((thisTick - network.last_block_time) / 1000);

                                } catch (Exception e) {

                                    e.printStackTrace();

                                }//********************


                                //Show (+1) (-1) incoming outgoing transactions to the user.
                                coin_transactions_display = " ";
                                if(network.incoming_tokens > 0){coin_transactions_display = coin_transactions_display + "(+" + Integer.toString(network.incoming_tokens) + ") ";}
                                if(network.outgoing_tokens > 0){coin_transactions_display = coin_transactions_display + "(-" + Integer.toString(network.outgoing_tokens) + ") ";}

                                //Show coin information to the user in the top left.
                                textView1.setText(network.database_listings_owner + coin_transactions_display + network.coin_name);
                                textView2.setText("Peer Connections: (" + Integer.toString(network.active_peers) + ")");
                                textView3.setText("Unconfirmed: " + network.database_unconfirmed_total + " (" + network.send_buffer_size + ")");
                                textView6.setText("Last Block Time: " + Long.toString(block_timex));


                                //Show how many hashes have been done since the last check.
                                //network.mining_speed_display = mining.noosex - miningx_speed1;



                            } catch (Exception e) {System.out.println("Display error 0008");}


                            //43 will be OK for the new key and also the old key without the K
                            if (network.base58_id.length() == network.base_58_id_size && !qr_loaded) {

                                try {

                                    System.out.println("Load QR code " + network.base58_id);
                                    File sdCard = Environment.getExternalStorageDirectory();
                                    File file = new File(path, network.base58_id + ".png");//Or any other format supported
                                    FileInputStream streamIn = new FileInputStream(file);
                                    bitmap = BitmapFactory.decodeStream(streamIn);//This gets the image

                                    //Set image
                                    BitmapDrawable bdrawable = new BitmapDrawable(context2.getResources(), bitmap);
                                    imageView.setBackground(bdrawable);

                                    streamIn.close();

                                    qr_loaded = true;

                                    //Save the one we are displaying.
                                    picture_hash = network.base58_id;

                                } catch(Exception e) {e.printStackTrace();}

                            }//***********************************************************************


                            //If the key changes, update our picture. Tokens have already been lost to this error.
                            if (!picture_hash.equals(network.base58_id)) {qr_loaded = false;}




                            //Load mining and server first in case node overrides them.
                            if (network.xmining) {//network.settingsx[7]

                                //switch2.setChecked(true);
                                mining.mining1 = true;

                                if (network.database_listings_owner == 0) {

                                    //Toast.makeText(getApplicationContext(), (String) "You cannot mine unless you have at least " + network.mining_token_limit + " token!", Toast.LENGTH_LONG).show();
                                    mining.mining1 = false;

                                }//**************************************

                            }//**********************************


                        }//@Override

                    });


                    //miningx_speed1 = mining.noosex;

                    try {Thread.sleep(1000);} catch (InterruptedException e){}


                } catch (Exception e) {e.printStackTrace();}

            }//while

        }//runx*********************************************

    }//remindtask








    public void local_db_new(){

        krypton_database_search_new search = new krypton_database_search_new();
        carbon = search.search();

        //Show size.
        new MainActivity.build_chain().execute();

    }//*******************



    public void local_db_search(){


        krypton_database_search search = new krypton_database_search();
        carbon = search.search(searchx.getText().toString());

        //Show size.
        new MainActivity.build_chain().execute();

        final View view = this.getCurrentFocus();
        if (view != null) {

            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        }//****************

        searchx.setFocusable(false);
        searchx.setText("");


    }//***************************










    public void remote_db_new(){

        if (network.active_peers > 0) {

            swipeRefreshLayout.setRefreshing(true);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    tx1.removeAllViews();
                    tableLayout.removeAllViews();

                }//@Override

            });

            xtimerx = new Timer();
            xtimerx.schedule(new MainActivity.RemindTask_tor_new(), 0);


        }//***************************
        else {

            Toast.makeText(getApplicationContext(), (String) "Peer is not ready.", Toast.LENGTH_LONG).show();

            swipeRefreshLayout.setRefreshing(false);

        }//**

    }//********************




    public void remote_db_search(){


        if (network.active_peers > 0) {

            if (searchx.getText().toString().length() > 0) {


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        tx1.removeAllViews();
                        tableLayout.removeAllViews();

                    }//@Override

                });


                swipeRefreshLayout.setRefreshing(true);

                xtimerx = new Timer();
                xtimerx.schedule(new MainActivity.RemindTask_tor_search(), 0);

                final View view = this.getCurrentFocus();
                if (view != null) {

                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                }//****************

                searchx.setFocusable(false);
                searchx.setText("");


            }//*********************
            else {

                Toast.makeText(getApplicationContext(), (String) "You need to search for something!", Toast.LENGTH_LONG).show();

            }//**

        }//*********************
        else {

            Toast.makeText(getApplicationContext(), (String) "Peer is not ready!", Toast.LENGTH_LONG).show();

        }//**


    }//********************















    class RemindTask_tor_new extends TimerTask {

        Runtime rxrunti = Runtime.getRuntime();

        public void run(){//************************************************************************

            try {

                System.out.println("Start TOR new...");

                carbon = request_new_remote();

                //Show size.
                new MainActivity.build_chain().execute();

            } catch(Exception e) {e.printStackTrace();}

        }//runx*************************************************************************************

    }//remindtask




    class RemindTask_tor_search extends TimerTask {

        Runtime rxrunti = Runtime.getRuntime();

        public void run(){//************************************************************************************

            try {


                System.out.println("Start TOR search...");

                carbon = request_search_remote(searchx.getText().toString());

                //Show size.
                new MainActivity.build_chain().execute();


            }catch(Exception e){e.printStackTrace();}

        }//runx*************************************************************************************************

    }//remindtask












    public String[][] request_new_remote(){//********************************************************


        String jsonText = "";
        String[][] jsonText2 = null;


        try{

            JSONObject obj = new JSONObject();
            obj.put("request","search_listings_new");
            //obj.put("text", search);

            StringWriter out = new StringWriter();
            obj.writeJSONString(out);
            jsonText = out.toString();
            System.out.println(jsonText);

        }catch(Exception e){System.out.println("JSON ERROR");}


        try{

            String onionAddress = "";

            if(network.use_one_peer){onionAddress = network.onionAddress;}
            else if(network.peersx0){onionAddress = network.peerid0;}//network.settingsx[9];
            else if(network.peersx1){onionAddress = network.peerid1;}//network.settingsx[10];
            else if(network.peersx2){onionAddress = network.peerid2;}//network.settingsx[11];
            else if(network.peersx3){onionAddress = network.peerid3;}//network.settingsx[12];

            //System.out.println("address: " + krypton_net_client.client_port_connect);
            System.out.println("onionAddress: " + onionAddress);

            System.out.println("socket");

            Socket socket = Utilities.socks4aSocketConnection(onionAddress, krypton_net_client.hiddenServicePort, krypton_net_client.local_host_connect, krypton_net_client.localPort);//127.0.0.1
            //socket.setSoTimeout(20000);

            System.out.println("socketg");

            OutputStream outputStream = socket.getOutputStream();
            PrintWriter outx = new PrintWriter(outputStream);
            outx.print(jsonText + "\r\n\r\n");
            outx.flush();
            InputStream inputStream = socket.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader in = new BufferedReader(inputStreamReader);

            System.out.println("socketw");

            String modifiedSentence = "";

            String line;
            while ((line = in.readLine()) != null) {

                System.out.println(line);
                modifiedSentence = line;

            }//*************************************

            outputStream.close();
            outx.close();
            in.close();
            socket.close();


            JSONParser parser = new JSONParser();
            Object obj = parser.parse(modifiedSentence);
            JSONObject jsonObject = (JSONObject) obj;

            String response = (String) jsonObject.get("response");
            String message = (String) jsonObject.get("message");
            System.out.println("JSON " + response);

            if(response.equals("1")){

                Object obj2 = parser.parse(message);
                JSONObject jsonObject2 = (JSONObject) obj2;

                jsonText2 = new String[8][jsonObject2.size()];

                //first we test the new items against ours to add the ones we don't have
                for (int loop = 0; loop < jsonObject2.size(); loop++) {//***************************

                    String bufferp = (String) jsonObject2.get(Integer.toString(loop));
                    System.out.println("loop " + loop);
                    //System.out.println("break up " + bufferp);

                    Object obj3 = parser.parse(bufferp);
                    JSONObject jsonObject3 = (JSONObject) obj3;

                    jsonText2[0][loop] = (String) jsonObject3.get("0");
                    jsonText2[1][loop] = (String) jsonObject3.get("1");
                    jsonText2[2][loop] = (String) jsonObject3.get("2");
                    jsonText2[3][loop] = (String) jsonObject3.get("3");
                    jsonText2[4][loop] = (String) jsonObject3.get("4");
                    jsonText2[5][loop] = (String) jsonObject3.get("5");
                    jsonText2[6][loop] = (String) jsonObject3.get("6");
                    jsonText2[7][loop] = (String) jsonObject3.get("7");

                }//*********************************************************************************

            }//**********************
            else{}


        }catch(Exception e){

            e.printStackTrace();
            System.out.println("Cannot find node!");

        }//*****************


        return jsonText2;

    }//*********************************************************************************************










    public String[][] request_search_remote(String search){//********************************************************


        String jsonText = "";
        String[][] jsonText2 = null;


        try{

            JSONObject obj = new JSONObject();
            obj.put("request","search_listings");
            obj.put("text", search);

            StringWriter out = new StringWriter();
            obj.writeJSONString(out);
            jsonText = out.toString();
            System.out.println(jsonText);

        }catch(Exception e){System.out.println("JSON ERROR");}


        try{

            String onionAddress = "";

            if(network.use_one_peer){onionAddress = network.onionAddress;}
            else if(network.peersx0){onionAddress = network.peerid0;}//network.settingsx[9];
            else if(network.peersx1){onionAddress = network.peerid1;}//network.settingsx[10];
            else if(network.peersx2){onionAddress = network.peerid2;}//network.settingsx[11];
            else if(network.peersx3){onionAddress = network.peerid3;}//network.settingsx[12];

            //System.out.println("address: " + krypton_net_client.client_port_connect);
            System.out.println("onionAddress: " + onionAddress);

            System.out.println("socket");

            Socket socket = Utilities.socks4aSocketConnection(onionAddress, krypton_net_client.hiddenServicePort, krypton_net_client.local_host_connect, krypton_net_client.localPort);//127.0.0.1
            //socket.setSoTimeout(20000);

            System.out.println("socketg");

            OutputStream outputStream = socket.getOutputStream();
            PrintWriter outx = new PrintWriter(outputStream);
            outx.print(jsonText + "\r\n\r\n");
            outx.flush();
            InputStream inputStream = socket.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader in = new BufferedReader(inputStreamReader);

            System.out.println("socketw");

            String modifiedSentence = "";

            String line;
            while ((line = in.readLine()) != null) {

                System.out.println(line);
                modifiedSentence = line;

            }//*************************************

            outputStream.close();
            outx.close();
            in.close();
            socket.close();


            JSONParser parser = new JSONParser();
            Object obj = parser.parse(modifiedSentence);
            JSONObject jsonObject = (JSONObject) obj;

            String response = (String) jsonObject.get("response");
            String message = (String) jsonObject.get("message");
            System.out.println("JSON " + response);

            if(response.equals("1")){

                Object obj2 = parser.parse(message);
                JSONObject jsonObject2 = (JSONObject) obj2;

                jsonText2 = new String[8][jsonObject2.size()];

                //first we test the new items against ours to add the ones we don't have
                for (int loop = 0; loop < jsonObject2.size(); loop++) {//***************************

                    String bufferp = (String) jsonObject2.get(Integer.toString(loop));
                    System.out.println("loop " + loop);
                    //System.out.println("break up " + bufferp);

                    Object obj3 = parser.parse(bufferp);
                    JSONObject jsonObject3 = (JSONObject) obj3;

                    jsonText2[0][loop] = (String) jsonObject3.get("0");
                    jsonText2[1][loop] = (String) jsonObject3.get("1");
                    jsonText2[2][loop] = (String) jsonObject3.get("2");
                    jsonText2[3][loop] = (String) jsonObject3.get("3");
                    jsonText2[4][loop] = (String) jsonObject3.get("4");
                    jsonText2[5][loop] = (String) jsonObject3.get("5");
                    jsonText2[6][loop] = (String) jsonObject3.get("6");
                    jsonText2[7][loop] = (String) jsonObject3.get("7");

                }//*********************************************************************************

            }//**********************
            else{}


        }catch(Exception e){

            e.printStackTrace();
            System.out.println("Cannot find node!");

        }//*****************


        return jsonText2;

    }//*********************************************************************************************









    private class build_chain extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Showing progress dialog.

            System.out.println("Searching1...");

        }


        @Override
        protected Void doInBackground(Void... params) {


            System.out.println("Searching2...");

            try {


                //test print this for errors
                System.out.println(carbon[0][0]);

                textView = new TextView[5][carbon[0].length];
                tableRow = new TableRow[carbon[0].length];
                textspace = new TextView[carbon[0].length];
                votex = new Button[2][carbon[0].length];

                System.out.println("build new chainx " + carbon[0].length);

                System.out.println("ping1");


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        tx1.removeAllViews();
                        tableLayout.removeAllViews();

                    }//@Override

                });

                System.out.println("ping2");




                for(int xloop1 = 0; xloop1 < carbon[0].length; xloop1++){//***********


                    System.out.println("ping4");

                    TableRow.LayoutParams rowParams1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 0.1f);
                    TableRow.LayoutParams rowParams2 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 0.8f);

                    TableRow.LayoutParams paramsx = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1.0f);
                    paramsx.span = 2;

                    tableRow[xloop1] = new TableRow(context2);
                    //tableRow[xloop1].setLayoutParams(rowParams);
                    tableRow[xloop1].setPadding(3,0,3,0);
                    //tableRow[xloop1].setBackgroundColor(Color.parseColor("#000000"));
                    tableRow[xloop1].setOrientation(LinearLayout.HORIZONTAL);

                    String save_title = "";
                    if(carbon[1][xloop1].length() > 47){save_title = carbon[1][xloop1].substring(0, 47) + "...";}
                    else{save_title = carbon[1][xloop1];}

                    System.out.println("Title: " + save_title);



                    TableLayout tx1l = new TableLayout(context2);
                    tx1l.setLayoutParams(rowParams1);
                    //tx1l.setBackgroundColor(Color.parseColor("#FFFFFF"));

                    TableRow x1 = new TableRow(context2);
                    TableRow x2 = new TableRow(context2);
                    TableRow x3 = new TableRow(context2);
                    TableRow x4 = new TableRow(context2);

                    final int showx = Integer.parseInt(carbon[0][xloop1]);

                    tx1l.setOnClickListener( new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub

                            Intent i = new Intent(getBaseContext(), ViewActivity.class);
                            i.putExtra("ID", showx);
                            startActivity(i);

                        }
                    });



                    textView[1][xloop1] = new TextView(context2);
                    textView[1][xloop1].setText(save_title);
                    textView[1][xloop1].setSingleLine(true);
                    textView[1][xloop1].setTextColor(getResources().getColor(R.color.black));
                    textView[1][xloop1].setLayoutParams(rowParams1);
                    textView[1][xloop1].setPadding(0,0,0,0);
                    //textView[1][xloop1].setTypeface(textView[1][xloop1].getTypeface(), Typeface.BOLD);
                    //textView[1][xloop1].setHeight(10);
                    textView[1][xloop1].setTextSize(15);

                    textView[2][xloop1] = new TextView(context2);
                    textView[2][xloop1].setText("" + carbon[2][xloop1] + " " + carbon[3][xloop1]);
                    textView[2][xloop1].setSingleLine(true);
                    textView[2][xloop1].setTextColor(getResources().getColor(R.color.colorAccent));
                    textView[2][xloop1].setLayoutParams(rowParams1);
                    textView[2][xloop1].setTypeface(textView[1][xloop1].getTypeface(), Typeface.BOLD);
                    textView[2][xloop1].setGravity(Gravity.LEFT | Gravity.CENTER);
                    textView[2][xloop1].setHeight(70);
                    textView[2][xloop1].setTextSize(13);

                    textView[3][xloop1] = new TextView(context2);
                    textView[3][xloop1].setText("Location: " + carbon[4][xloop1]);
                    textView[3][xloop1].setSingleLine(true);
                    textView[3][xloop1].setTextColor(getResources().getColor(R.color.dkgrayxx));
                    textView[3][xloop1].setLayoutParams(rowParams1);
                    //textView[3][xloop1].setHeight(10);
                    textView[3][xloop1].setTextSize(10);

                    textView[4][xloop1] = new TextView(context2);
                    textView[4][xloop1].setText("");
                    textView[4][xloop1].setSingleLine(true);
                    //textView[4][xloop1].setTextColor(Color.parseColor("#000000"));
                    textView[4][xloop1].setLayoutParams(rowParams1);
                    textView[4][xloop1].setHeight(40);
                    textView[4][xloop1].setTextSize(10);



                    TableLayout tx1r = new TableLayout(context2);
                    tx1r.setGravity(Gravity.CENTER | Gravity.CENTER);
                    tx1r.setLayoutParams(rowParams2);
                    //tx1r.setBackgroundColor(Color.parseColor("#31416C"));


                    try{

                        String langx = "display_nothing.png";

                        if(carbon[5][xloop1].equals("DISPLAY_BITCOIN")){langx = "display_bitcoin.png";}
                        else if(carbon[5][xloop1].equals("DISPLAY_COINS")){langx = "display_coins.png";}
                        else if(carbon[5][xloop1].equals("DISPLAY_COMPUTER")){langx = "display_computer.png";}
                        else if(carbon[5][xloop1].equals("DISPLAY_CONDO")){langx = "display_condo.png";}
                        else if(carbon[5][xloop1].equals("DISPLAY_CONDOM")){langx = "display_condom.png";}
                        else if(carbon[5][xloop1].equals("DISPLAY_DOWNLOAD")){langx = "display_download.png";}
                        else if(carbon[5][xloop1].equals("DISPLAY_ELECTRONICS")){langx = "display_electronics.png";}
                        else if(carbon[5][xloop1].equals("DISPLAY_ENGINE")){langx = "display_engine.png";}
                        else if(carbon[5][xloop1].equals("DISPLAY_GAS")){langx = "display_gas.png";}
                        else if(carbon[5][xloop1].equals("DISPLAY_GEARS")){langx = "display_gears.png";}
                        else if(carbon[5][xloop1].equals("DISPLAY_GIFT")){langx = "display_gift.png";}
                        else if(carbon[5][xloop1].equals("DISPLAY_GUN")){langx = "display_gun.png";}
                        else if(carbon[5][xloop1].equals("DISPLAY_JEWEL")){langx = "display_jewel.png";}
                        else if(carbon[5][xloop1].equals("DISPLAY_LAW")){langx = "display_law.png";}
                        else if(carbon[5][xloop1].equals("DISPLAY_MAP")){langx = "display_map.png";}
                        else if(carbon[5][xloop1].equals("DISPLAY_MEDAL")){langx = "display_medal.png";}
                        else if(carbon[5][xloop1].equals("DISPLAY_OK")){langx = "display_ok.png";}
                        else if(carbon[5][xloop1].equals("DISPLAY_PANTS")){langx = "display_pants.png";}
                        else if(carbon[5][xloop1].equals("DISPLAY_PAPER")){langx = "display_paper.png";}
                        else if(carbon[5][xloop1].equals("DISPLAY_PILL")){langx = "display_pill.png";}
                        else if(carbon[5][xloop1].equals("DISPLAY_POISON")){langx = "display_poison.png";}
                        else if(carbon[5][xloop1].equals("DISPLAY_RING")){langx = "display_ring.png";}
                        else if(carbon[5][xloop1].equals("DISPLAY_SCIFI")){langx = "display_scifi.png";}
                        else if(carbon[5][xloop1].equals("DISPLAY_SUPERMAN")){langx = "display_superman.png";}
                        else if(carbon[5][xloop1].equals("DISPLAY_TICKET")){langx = "display_ticket.png";}
                        else if(carbon[5][xloop1].equals("DISPLAY_XXX")){langx = "display_xxx.png";}
                        else{langx = "display_coins.png";}

                        System.out.println(langx);
                        InputStream istr = assetManager.open(langx);
                        Bitmap bmImg = BitmapFactory.decodeStream(istr);
                        ImageView iv = new ImageView(getApplicationContext());
                        iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        iv.setImageBitmap(bmImg);
                        iv.setLayoutParams(new ViewGroup.LayoutParams(60, 60));
                        tx1r.addView(iv);

                    }catch(Exception e){e.printStackTrace(); System.out.println("Error Loading Icon.");}


                    x1.addView(textView[1][xloop1]);//Title

                    //x2.setBackgroundColor(Color.BLACK);
                    //x2.setGravity(Gravity.CENTER | Gravity.CENTER);
                    x2.addView(tx1r);//Image
                    x2.addView(textView[2][xloop1]);//Price

                    x3.addView(textView[3][xloop1]);//Location
                    x4.addView(textView[4][xloop1]);//Space

                    tx1l.removeAllViews();
                    tx1l.addView(x1);
                    tx1l.addView(x2);
                    tx1l.addView(x3);
                    tx1l.addView(x4);


                    tableRow[xloop1].addView(tx1l);



                    final int passxloop = xloop1;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            tableLayout.addView(tableRow[passxloop]);

                        }//@Override

                    });

                    //tableLayout.setbackgroundresources(R.drawable.ic_launcher);

                }//for*****************************************************************



                //Build the table on the main thread so we don't get errors.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        tx1.addView(tableLayout);

                        swipeRefreshLayout.setRefreshing(false);

                    }//@Override

                });


            }catch(Exception e){

                e.printStackTrace();
                System.out.println("Print Error");

                if(!network.full_node) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            texttest = new TextView(context2);
                            texttest.setText("Server returned nothing, It could be busy...");
                            texttest.setTextColor(Color.parseColor("#000000"));
                            texttest.setTextSize(15);
                            tx1.addView(texttest);

                            swipeRefreshLayout.setRefreshing(false);

                        }//@Override

                    });

                }//*********************
                else{

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            texttest = new TextView(context2);
                            texttest.setText("Nothing to display yet. Check back in a bit.");
                            texttest.setTextColor(Color.parseColor("#000000"));
                            texttest.setTextSize(15);
                            tx1.addView(texttest);

                            swipeRefreshLayout.setRefreshing(false);

                        }//@Override

                    });

                }//***



            }//catch



            return null;

        }//do in background()

    }//build_chain2**************************************************





}//last

