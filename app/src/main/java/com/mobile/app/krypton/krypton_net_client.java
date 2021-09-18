package com.mobile.app.krypton;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.*;
import java.util.Timer;
import java.util.TimerTask;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONValue;
import com.msopentech.thali.android.toronionproxy.AndroidOnionProxyContext;
import com.msopentech.thali.android.toronionproxy.AndroidOnionProxyManager;
import com.msopentech.thali.toronionproxy.Utilities;
import java.net.Socket;



public class krypton_net_client{

	krypton_update_listings_lite liteclient = new krypton_update_listings_lite();
	krypton_database_load loadx = new krypton_database_load();
	krypton_database_get_unconfirmed_test unconfirmed_test = new krypton_database_get_unconfirmed_test();

	JSONParser parser = new JSONParser();

	//This is the location in the android app were our tor files will be saved.
	static String fileStorageLocation = "torfiles";
	static AndroidOnionProxyManager onionProxyManager = new AndroidOnionProxyManager(MainActivity.context2, fileStorageLocation);

	Timer xtimerx;//class loop.

	static String[] peer1sendBufferB1 = null;
	static String[] peer1sendBufferB2 = null;

	static final String testAddress = "facebook.com";//Test address to make sure Tor is working even if one of our clients is not.
	static final String local_host_connect = "127.0.0.1";
	static String serverOnionAddress = "Server is not active!";
	private static String peer1sendUnconfiremdB1 = null;
	private static String peer1sendPackageB1 = null;
	String new_hash1;
	String new_hash2;
	String new_hash3;
	String new_hash4;

	int client_port_connect = 0000;
	static int breakx1 = 0;
	static int breakx2 = 0;
	static int hiddenServicePort = 80;
	static int localPort = 0;
	static int tor_tries = 0;//3 times and we restart.

	static long client_loop_time = (long) 0;
	long threadId;//There were errors with different threads being called this was to test that.
	long last_block_longstamp_save = (long) 0;//We only want to refresh if there is something new.
    long save_last_block_time = (long) 0;//Save the last block time.

	int status_test = 0;//Test 3 times before we delete what we have.
	//int partial_peer_update_loop = 0;//We only update our listings every few minutes to save resources on the remote server.


    //This is the client system with all the calls that a client needs to communicate with a server.
    //We use a real linux tor client here because it's the one that works the best. I have tried many different systems and this one is the only one that can keep up.
    //But TOR is still extremely difficult to use and get right. It has taken months of tests to get it just to this stage.
    //Here we start the client and the server if it's requested by the user even though this is not the server class. It's easier to do it in one location.
    //The ViewActivity class also uses TOR to download the picture in the listing to make sure that isn't used to expose the user's IP.
    //ViewActivity and SearchActivity also use the TOR client that is started from here if the user is using the lite "SPV" client.

    krypton_net_client(){//*****************************************************************


        //no_peers_time = 0;
		//network.connection_active = 0;
        network.tor_active = false;
        network.tor_starting = true;

        xtimerx = new Timer();
        xtimerx.schedule(new RemindTask_backup(), 0);

        //Start tor.
        try {


            int totalSecondsPerTorStartup = 4 * 60;
            int totalTriesPerTorStartup = 1;
            // Start the Tor Onion Proxy
            if(onionProxyManager.startWithRepeat(totalSecondsPerTorStartup, totalTriesPerTorStartup) == false) {return;}

            //Start a hidden service listener.
            localPort = onionProxyManager.getIPv4LocalHostSocksPort();
            //int localPort = 55555;


            if (network.server) {

                //start hidden service if requested.
                int hiddenServicePort = 80;
                int localPort = network.p2p_port;
                serverOnionAddress = onionProxyManager.publishHiddenService(hiddenServicePort, localPort);

                System.out.println("onionAddress hidden service: " + serverOnionAddress);

                Socket clientSocket = Utilities.socks4aSocketConnection(serverOnionAddress, hiddenServicePort, "127.0.0.1", localPort);

            }//*****************

        } catch (Exception e) {e.printStackTrace();}//******************


        krypton_net_client.client_loop_time = System.currentTimeMillis();

        //We set this to a high number first so that it is manually refreshed when the system stars.
        last_block_longstamp_save = network.last_block_longstamp1;

        //Our chain is the same as the server.
        network.blocks_uptodate = false;

        //I was using client_port_connect already so I just copied the TOR system to it.
        client_port_connect = hiddenServicePort;

        client_loop_time = System.currentTimeMillis();

        breakx1 = 0;
        breakx2 = 0;

        //This is the loop to test if tor is working.
        xtimerx = new Timer();
        xtimerx.schedule(new RemindTask_tor_status(), 0);

        //This is the loop to connect to our peers.
        //xtimerx = new Timer();
        //xtimerx.schedule(new RemindTask_main(), 0);


		//This is the loop to connect to our peers.
		xtimerx = new Timer();
		xtimerx.schedule(new RemindTask_peer0(), 0);

		//This is the loop to connect to our peers.
		xtimerx = new Timer();
		xtimerx.schedule(new RemindTask_peer1(), 500);

		//This is the loop to connect to our peers.
		xtimerx = new Timer();
		xtimerx.schedule(new RemindTask_peer2(), 1000);

		//This is the loop to connect to our peers.
		xtimerx = new Timer();
		xtimerx.schedule(new RemindTask_peer3(), 1500);


    }//*************************************************************************************






	
	//Tor restart if it fails, which does happen! After about 10 - 20 mins
    //Tor is very hard to get right it goes on and off and sometimes doesn't work for hours, get used to it.

	public static void start_tor(){


		//no_peers_time = 0;
		network.tor_active = false;//network.tor_active = 0;
		//network.connection_active = 0;
		network.tor_starting = true;//network.tor_starting = 1;

		//Start tor.
		try {


			//Someone tried to call stop before we had finished registering the receiver.
			//We keep calling stop until the receiver can disconnect.
			try {

				System.out.println("STOP SERVICE");

				System.out.println("threadId " + Thread.currentThread().getId());

				onionProxyManager.stop();

			} catch (Exception e) {

				//Someone tried to call stop before we had finished registering the receiver
				e.printStackTrace();
				System.out.println("PRINT...");
				//onionProxyManager = new AndroidOnionProxyManager(MainActivity.context2, fileStorageLocation);
				//restartApp();

				//try{Thread.sleep(200000);} catch(InterruptedException ex){}

			}//*****************


			int totalSecondsPerTorStartup = 4 * 60;
			int totalTriesPerTorStartup = 1;

			//Start the Tor Onion Proxy
			if (onionProxyManager.startWithRepeat(totalSecondsPerTorStartup, totalTriesPerTorStartup) == false) {return;}

			// Start a hidden service listener
			localPort = onionProxyManager.getIPv4LocalHostSocksPort();
			//int localPort = 55555;

			if (network.server) {

				//Start hidden service if requested.
				int hiddenServicePort = 80;
				int localPort = network.p2p_port;
				serverOnionAddress = onionProxyManager.publishHiddenService(hiddenServicePort, localPort);

				System.out.println("onionAddress hidden service: " + serverOnionAddress);

				Socket clientSocket = Utilities.socks4aSocketConnection(serverOnionAddress, hiddenServicePort, "127.0.0.1", localPort);

			}//***********************

		}
		catch (Exception e) {e.printStackTrace();}
		catch (Error e) {e.printStackTrace();}

		try {Thread.sleep(10000);} catch (InterruptedException e) {}


	}//*********************





    //The system that connects to facebook and the system that connects to other nodes is not the same.
    //If we cannot connect to facebook then we restart Tor, if we cannot connect to a node we change nodes.
    //We assume that the facebook onion address will be up more of the time then any single one of our peers.

	class RemindTask_tor_status extends TimerTask{

		Runtime rxrunti = Runtime.getRuntime();

		public void run(){//************************************************************************************

			threadId = Thread.currentThread().getId();
			System.out.println("threadId " + threadId);

			while (true) {

				try {

					System.out.println("threadId " + threadId + " " + Thread.currentThread().getId());

					//boolean torreadyx = onionProxyManager.torReady();

					boolean torreadyx = onionProxyManager.isBootstrapped();

					//MainActivity.bootstrapStatus = onionProxyManager.bootstrappedStatus();

					System.out.println("TorReady: " + torreadyx);

					if(torreadyx){System.out.println("Tor is ready..."); xx3();}
					else{System.out.println("Tor is not ready...");}

				} catch (Exception e) {e.printStackTrace();}

				try {Thread.sleep(30000);} catch(InterruptedException e) {e.printStackTrace();}

				//System.out.println("Loop 32");

			}//while****


		}//runx*************************************************************************************************

	}//remindtask







    //If one of our connection threads cannot connect to a node we try a different address from our node list.
    //We save the ones that are working and keep using those for as long as they are active.

	static class RemindTask_backup extends TimerTask{

	    Runtime rxrunti = Runtime.getRuntime();

		public void run(){//************************************************************************************

			long thisTick = (long) System.currentTimeMillis();


			while (true) {

				try {

					thisTick = System.currentTimeMillis();

					network.no_connection_time++;
					network.no_peers_time0++;
					if (!network.use_one_peer) {network.no_peers_time1++; network.no_peers_time2++; network.no_peers_time3++;}

					System.out.println("database_in_use            " + network.database_in_use);
					System.out.println("network.no_connection_time " + network.no_connection_time);
					System.out.println("network.no_peers_time0     " + network.no_peers_time0);
					System.out.println("network.no_peers_time1     " + network.no_peers_time1);
                    System.out.println("network.no_peers_time2     " + network.no_peers_time2);
                    System.out.println("network.no_peers_time3     " + network.no_peers_time3);

					if(network.no_connection_time > 40){

						network.programst = "Peer may be offline!";
						start_tor();

						network.no_connection_time = 0;

					}//*********************************


					if (network.network_size > 0 && network.tor_active) {//***************************

						//If the network is active and the other peer is active.
						if (network.no_peers_time0 > 30 && !network.use_one_peer) {

                            //Get a new peer the old one is probably off.
                            network.peerid0 = get_new_peer(0);//network.settingsx[9]
							network.no_peers_time0 = 0;

                        }//********************************************************

                        if (network.network_size > 1) {

						    if (network.no_peers_time1 > 30) {

                                //Get a new peer the old one is probably off.
								network.peerid1 = get_new_peer(1);//network.settingsx[10]
                                network.no_peers_time1 = 0;

                            }//******************************

                        }//**********************************************************

                        if (network.network_size > 2) {

						    if (network.no_peers_time2 > 30) {

                                //Get a new peer the old one is probably off.
                                network.peerid2 = get_new_peer(2);//network.settingsx[11]
                                network.no_peers_time2 = 0;

                            }//******************************

                        }//**********************************************************

                        if (network.network_size > 3) {

						    if (network.no_peers_time3 > 30) {

                                //Get a new peer the old one is probably off.
                                network.peerid3 = get_new_peer(3);//network.settingsx[12]
                                network.no_peers_time3 = 0;

                            }//******************************

                        }//**********************************************************

					}//******************************************************************************


					try {Thread.sleep(10000);} catch (InterruptedException e) {e.printStackTrace();}

					//System.out.println("Loop 32");


				} catch (Exception e) {e.printStackTrace();}//******************

			}//while****


	    }//runx*************************************************************************************************

	}//remindtask







    //Get a new peer
    private static String get_new_peer(int peeridx){

        String new_peer = "";

        //Get a new peer the old one is probably off.
        Random rand = new Random();

        boolean newx = false;

        while (!newx) {

            int n = rand.nextInt(network.network_size);
            new_peer = network.network_list.get(n).toString();

            System.out.println("peeridx " + peeridx);

            newx = true;

            //If we pass all the tests then we break.
            if(peeridx != 0 && new_peer.equals(network.peerid0)){newx = false;}//settingsx[9]
            if(peeridx != 1 && new_peer.equals(network.peerid1)){newx = false;}//settingsx[10]
            if(peeridx != 2 && new_peer.equals(network.peerid2)){newx = false;}//settingsx[11]
            if(peeridx != 3 && new_peer.equals(network.peerid3)){newx = false;}//settingsx[12]


        }//**********

        System.out.println("new_peer " + new_peer);


		SharedPreferences.Editor editor = MainActivity.settings.edit();
		editor.putString("peerid0", network.peerid0);
		editor.putString("peerid1", network.peerid1);
		editor.putString("peerid2", network.peerid2);
		editor.putString("peerid3", network.peerid3);
		editor.commit();

        //krypton_database_save savex = new krypton_database_save();
        //savex.save();

	    return new_peer;

    }//***************************






	//This is the main connection loop which keeps the program connected to peers.
	//There is only one connection to one peer.

	class RemindTask_peer0 extends TimerTask{

		Runtime rxrunti = Runtime.getRuntime();

		public void run(){//************************************************************************************

			while (true) {//**************************************************************************************************

				try {


					if (network.tor_active) {


						System.out.println("Tor loop peer 0");

						//If there are not enough addresses we can't use more then one node.
						System.out.println("network.network_size " + network.network_size);

						//Everyone gets one peer.
						runx(0);

					}//********************
					else {

						System.out.println("Tor is not active... loop >>>>");
						network.peersx0 = false;

					}//**

					System.out.println("breakid " + network.full_node_break_time);

					try {Thread.sleep(network.full_node_break_time);} catch (InterruptedException e) {}


				} catch (Exception e) {e.printStackTrace();}//******************

			}//while***********************************************************************************************************************************

		}//runx*************************************************************************************************

	}//remindtask






	//This is the main connection loop which keeps the program connected to peers.
	//There is only one connection to one peer.

	class RemindTask_peer1 extends TimerTask{

		Runtime rxrunti = Runtime.getRuntime();

		public void run(){//************************************************************************************

			while (true) {//**************************************************************************************************

				try {


					if (network.tor_active) {

						System.out.println("Tor loop peer 1");

						//If there are not enough addresses we can't use more then one node.
						System.out.println("network.network_size " + network.network_size);

						//Four connects are only allowed if you are a full node and have the whole blockchain.
						if (!network.use_one_peer && network.network_size > 1) {

							runx(1);

						}//***************************************************
						else {

							//All other peers are blank.
							network.peersx1 = false;
							network.peerid1 = "";//network.settingsx[10] = "";
							network.no_peers_time1 = 0;

						}//**

					}//********************
					else {

						System.out.println("Tor is not active... loop >>>>");
						network.peersx1 = false;

					}//**

					System.out.println("breakid " + network.full_node_break_time);

					try {Thread.sleep(network.full_node_break_time);} catch(InterruptedException e){}


				} catch (Exception e) {e.printStackTrace();}//******************


			}//while***********************************************************************************************************************************

		}//runx*************************************************************************************************

	}//remindtask






	//This is the main connection loop which keeps the program connected to peers.
	//There is only one connection to one peer.

	class RemindTask_peer2 extends TimerTask{

		Runtime rxrunti = Runtime.getRuntime();

		public void run(){//************************************************************************************

			while (true) {//**************************************************************************************************

				try {


					if (network.tor_active) {

						System.out.println("Tor loop peer 2");

						//If there are not enough addresses we can't use more then one node.
						System.out.println("network.network_size " + network.network_size);

						//Four connects are only allowed if you are a full node and have the whole blockchain.
						if (!network.use_one_peer && network.hard_token_count == network.hard_token_limit) {

							if (network.network_size > 2 && network.full_node) {runx(2);}
							else {network.peersx2 = false; network.no_peers_time2 = 0; network.peerid2 = "";}//network.settingsx[11]

						}//*******************************************************************************
						else {

							//All other peers are blank.
							network.peersx2 = false;
							network.peerid2 = "";//network.settingsx[11] = "";
							network.no_peers_time2 = 0;

						}//**

					}//*************************
					else {

						System.out.println("Tor is not active... loop >>>>");
						network.peersx2 = false;

					}//**

					System.out.println("breakid " + network.full_node_break_time);

					try {Thread.sleep(network.full_node_break_time);} catch (InterruptedException e) {}


				} catch (Exception e) {e.printStackTrace();}//******************


			}//while***********************************************************************************************************************************

		}//runx*************************************************************************************************

	}//remindtask






	//This is the main connection loop which keeps the program connected to peers.
	//There is only one connection to one peer.

	class RemindTask_peer3 extends TimerTask{

		Runtime rxrunti = Runtime.getRuntime();

		public void run(){//************************************************************************************


			while (true) {//**************************************************************************************************


				try {


					if (network.tor_active) {

						System.out.println("Tor loop peer 3");

						//If there are not enough addresses we can't use more then one node.
						System.out.println("network.network_size " + network.network_size);

						//Four connects are only allowed if you are a full node and have the whole blockchain.
						if (!network.use_one_peer && network.hard_token_count == network.hard_token_limit) {

							if (network.network_size > 3 && network.full_node) {runx(3);}
							else {network.peersx3 = false; network.no_peers_time3 = 0; network.peerid3 = "";}//network.settingsx[12] = "";

						}//*******************************************************************************
						else {

							//All other peers are blank.
							network.peersx3 = false;
							network.peerid3 = "";//network.settingsx[12] = "";
							network.no_peers_time3 = 0;

						}//**

					}//*************************
					else {

						System.out.println("Tor is not active... loop >>>>");
						network.peersx3 = false;

					}//**

					System.out.println("breakid " + network.full_node_break_time);

					try {Thread.sleep(network.full_node_break_time);} catch(InterruptedException e){}


				} catch (Exception e) {e.printStackTrace();}//******************


			}//while***********************************************************************************************************************************


		}//runx*************************************************************************************************


	}//remindtask






	//This is the main connection loop which keeps the program connected to peers.
	//There is only one connection to one peer.

	class RemindTask_main extends TimerTask{

	    Runtime rxrunti = Runtime.getRuntime();

        public void run(){//************************************************************************************


            while (true) {//**************************************************************************************************


                try {


                    if (network.tor_active) {

                        System.out.println("tor loop");

                        try{Thread.sleep(1000);}
                        catch(InterruptedException e){}

						//If there are not enough addresses we can't use more then one node.
						System.out.println("network.network_size " + network.network_size);

                        //Everyone gets one peer.
                        runx(0);


						//Four connects are only allowed if you are a full node and have the whole blockchain.
                        if (!network.use_one_peer && network.hard_token_count == network.hard_token_limit){

							if (network.network_size > 1 && network.full_node) {runx(1);}
							else {network.peersx1 = false; network.peerid1 = "";}//network.settingsx[10] = "";

                            if (network.network_size > 2 && network.full_node) {runx(2);}
                            else {network.peersx2 = false; network.peerid2 = "";}//network.settingsx[11] = "";

                            if (network.network_size > 3 && network.full_node) {runx(3);}
                            else {network.peersx3 = false; network.peerid3 = "";}//network.settingsx[12] = "";

                        }//***********************
                        else {

                            //All other peers are blank.
							network.peerid1 = "";//network.settingsx[10] = "";
							network.peerid2 = "";//network.settingsx[11] = "";
							network.peerid3 = "";//network.settingsx[12] = "";

                        }//**

                    }//*************************
                    else {

                        System.out.println("Tor is not active... loop >>>>");
                        network.peersx0 = false;
                        network.peersx1 = false;
                        network.peersx2 = false;
                        network.peersx3 = false;

                    }//**

                    System.out.println("breakid " + 0);

                    int break_time = 30000;

                    if (network.full_node) {break_time = 30000;}
					else if (network.active_peers == 0) {break_time = 20000;}
                    else {break_time = 100000;}

                    try {Thread.sleep(break_time);} catch (InterruptedException e) {}


                } catch (Exception e) {e.printStackTrace();}//******************


            }//while***********************************************************************************************************************************


        }//runx*************************************************************************************************

    }//remindtask








    //This is set up in case we add more peers in the future right now there is only one.

    public void runx(int run_number){//************************************************************************************

        System.out.println("Krypton Network Client: " + run_number);
        int node_match = 1;
        String onionAddress = null;

        //Get the user peer if requested.
        if(network.use_one_peer){onionAddress = network.onionAddress;}
        else if(run_number == 0){onionAddress = network.peerid0;}//network.settingsx[9];
        else if(run_number == 1){onionAddress = network.peerid1;}//network.settingsx[10];
        else if(run_number == 2){onionAddress = network.peerid2;}//network.settingsx[11];
        else if(run_number == 3){onionAddress = network.peerid3;}//network.settingsx[12];
        else{System.out.println("Unknown operation...");}


        System.out.println("onionAddress load " + network.use_one_peer + " " + onionAddress);

        //Don't connect to our self.
        if(onionAddress.equals(serverOnionAddress) && !network.use_one_peer){

            System.out.println("We shouldn't connect to our own server!");
            if(run_number == 0){network.peersx0 = false; network.no_peers_time0 = 30;}
            if(run_number == 1){network.peersx1 = false; network.no_peers_time1 = 30;}
            if(run_number == 2){network.peersx2 = false; network.no_peers_time2 = 30;}
            if(run_number == 3){network.peersx3 = false; network.no_peers_time3 = 30;}

            krypton_database_node node = new krypton_database_node();
            node.deleteNode(onionAddress);

        }//******************************************************************
        else if(!onionAddress.contains(".onion")){

            System.out.println("Not a .onion address!");
            if(run_number == 0){network.peersx0 = false; network.no_peers_time0 = 30;}
            if(run_number == 1){network.peersx1 = false; network.no_peers_time1 = 30;}
            if(run_number == 2){network.peersx2 = false; network.no_peers_time2 = 30;}
            if(run_number == 3){network.peersx3 = false; network.no_peers_time3 = 30;}

        }//***************************************
        else if(!network.tor_active && network.tor_starting){System.out.println("TOR IS NOT READY...");}
        else{


            System.out.println("Other node work...");

            System.out.println("onionAddress " + run_number + " " + onionAddress);

            String status = "";

            //If the user does not want to download the full chain they are just using a partial node.
            if(network.full_node){status = request_status(run_number, onionAddress);}//network.full_node == 1
            else{status = request_status_partial_node(run_number, onionAddress);}

            //Show the status.
            System.out.println("status: " + status);

            //If the node is active.
            if(status.equals("active")){

                //We have 2 peers that can be active.
                if(run_number == 0){network.peersx0 = true;}
                if(run_number == 1){network.peersx1 = true;}
                if(run_number == 2){network.peersx2 = true;}
                if(run_number == 3){network.peersx3 = true;}

                System.out.println("peersx0 " + network.peersx0);
                System.out.println("peersx1 " + network.peersx1);
                System.out.println("peersx2 " + network.peersx2);
                System.out.println("peersx3 " + network.peersx3);

                //System.out.println("nodelist size 4 " + network.network_sizex);
                //network.connection_active = 1;

                //pause
                try{Thread.sleep(3000);}
                catch(InterruptedException e){e.printStackTrace();}

                //Update the blocks.
                if(!network.blocks_uptodate && !network.updating){

                    network.updating = true;

                    if(network.hard_token_count == 0 && network.full_node){download_blockchain(onionAddress);}
                    else{request_blocks_x_update(onionAddress);}

                    network.updating = false;

                }//***********************************************

                //Send any new blocks if they are found. network.mining_block_ready == 1
                if(network.mining_block_ready == true && network.blocks_uptodate){send_new_block_update2();}

                //Send package if we have one network.mining_package_ready == 1
                if(network.mining_package_ready == true && network.blocks_uptodate){send_new_package_update2();}


            }//if**********************
            else{

                if(run_number == 0){network.peersx0 = false;}
                if(run_number == 1){network.peersx1 = false;}
                if(run_number == 2){network.peersx2 = false;}
                if(run_number == 3){network.peersx3 = false;}

            }//else


        }//else

    }//runx*************************************************************************************************







    //This is the system that will restart TOR if it cannot connect to facebook for a test.
    //This happens a lot and will probably be called at least a few times per day.
    //If the internet is bad it could be called constantly.

    static public void xx3(){

		try{

			String jsonText = "";

			JSONObject obj = new JSONObject();
			obj.put("request","status");

			StringWriter outs = new StringWriter();
			obj.writeJSONString(outs);
			jsonText = outs.toString();
			System.out.println(jsonText);

			System.out.println("testAddress " + testAddress);

			Socket socket = Utilities.socks4aSocketConnection(testAddress, hiddenServicePort, "127.0.0.1", localPort);//127.0.0.1
			//socket.setSoTimeout(20000);

			OutputStream outputStream = socket.getOutputStream();
			PrintWriter outx = new PrintWriter(outputStream);
	    	outx.print(jsonText + "\r\n\r\n");
	    	outx.flush();

	    	System.out.println("socketw");

			InputStream inputStream = socket.getInputStream();
	    	InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
	    	BufferedReader in = new BufferedReader(inputStreamReader);

            String modifiedSentence = "";

			String line;
	    	while ((line = in.readLine()) != null) {

	    		//System.out.println(line);
			  	modifiedSentence = modifiedSentence + line;

	    	}//*************************************

			System.out.println("Connection test: " + modifiedSentence.length());

			tor_tries = 0;

	    	network.tor_active = true;
			network.tor_starting = false;

			network.no_connection_time = 0;

	    	outputStream.close();
	   		outx.close();
	    	in.close();
	    	socket.close();

		} catch (Exception e) {

			e.printStackTrace();
			network.tor_active = false;

			//If the test cannot connect then for sure these can't.
			network.peersx0 = false;
			network.peersx1 = false;

		}//******************

    }//***************







	//This is called from outside if we find a new block it is set in peer1SendBufferB1 and B2 until tor is ready to broadcast.

    public static String send_new_block_update(String[] mining_id, String[] token_id){//*********************************************************

    	System.out.println("SEND NEW BLOCK UPDATE X");

    	network.buffered_mining_block = "";

		String successx = "0";


		try{

			peer1sendBufferB1 = mining_id;
			peer1sendBufferB2 = token_id;

			JSONObject objm = new JSONObject();
			JSONObject objl = new JSONObject();

			String jsonTextM = "";
			String jsonTextL = "";

    		for (int loop = 0; loop < mining_id.length; loop++){//************

				objm.put(loop, mining_id[loop]);

			}//***************************************************************

			StringWriter outM = new StringWriter();
			objm.writeJSONString(outM);
			jsonTextM = outM.toString();
			network.buffered_mining_block = jsonTextM;


    		for (int loop = 0; loop < token_id.length; loop++){//************

				objl.put(loop, token_id[loop]);

			}//**************************************************************

			StringWriter outL = new StringWriter();
			objl.writeJSONString(outL);
			jsonTextL = outL.toString();

			network.buffered_listing_block = jsonTextL;
			network.mining_block_ready = true;//network.mining_block_ready = 1;

            successx = "1";

		} catch (Exception e) {

		    e.printStackTrace();
		    System.out.println("SEND NEW BLOCK UPDATE X ERROR");
		    successx = "0";
		    network.mining_block_ready = false;

		}


		return successx;

    }//*******************************************************************************************************************************************








	//Package update this sends a group of listings at once if the system has too many items waiting to be confirmed.

    public static String send_new_block_package(String[][] mining_id, String[][] token_id){//*********************************************************

    	System.out.println("SEND NEW BLOCK PACKAGE UPDATE X");

    	network.buffered_mining_block = "";

		String successx = "0";

		try{


		    JSONObject obj1 = new JSONObject();
			for(int loop0 = 0; loop0 < mining_id[0].length; loop0++){//*******************************************************************

				String jsonText = "";
				int xxp1 = 0;
				int xxp2 = 0;

				
				JSONObject obj = new JSONObject();

	    		for(int loop1 = 0; loop1 < mining_id.length; loop1++){//*************

					obj.put("m" + Integer.toString(xxp1), mining_id[loop1][loop0]);
					xxp1++;

				}//***************************************************************

	    		for(int loop1 = 0; loop1 < token_id.length; loop1++){//*************

					obj.put("l" + Integer.toString(xxp2), token_id[loop1][loop0]);
					xxp2++;

				}//**************************************************************

				StringWriter out = new StringWriter();
				obj.writeJSONString(out);
				jsonText = out.toString();
				
				//list.add(jsonText);
                obj1.put(Integer.toString(loop0), jsonText);

			}//****************************************************************************************************************************

			System.out.println(JSONValue.toJSONString(obj1));
			peer1sendPackageB1 = JSONValue.toJSONString(obj1);
			network.mining_package_ready = true;

            successx = "1";

		} catch (Exception e) {

		    e.printStackTrace();
            System.out.println("SEND NEW BLOCK PACKAGE UPDATE X ERROR");
            successx = "0";
            network.mining_package_ready = false;

		}//*****************


		return successx;

    }//*******************************************************************************************************************************************







	//Someone sent us a new block and we are going to send it on to our peers if it's good.

	public static void resend_new_block_package(String rspackage){

		peer1sendPackageB1 = rspackage;
        network.mining_package_ready = true;
        network.reset_mining_hash = true;
        mining.mining_stop = true;

	}//***********************************************************






	//Load a new item we have updated and send it to the system.

	public String get_buffer_unconfirmed(){

		System.out.println("Get unconfirmed from buffer.");

		String jsonText = "";


		try{

			String[][] token_id;

			krypton_database_get_buffer bufferx = new krypton_database_get_buffer();
			token_id = bufferx.getTokens();

			JSONObject obj1 = new JSONObject();
			for (int loop = 0; loop < token_id[0].length; loop++) {//*************

				for (int loopx = 0; loopx < token_id.length; loopx++) {//*************

					obj1.put(Integer.toString(loop), token_id[loopx]);

				}//****************************************************************

			}//****************************************************************

			StringWriter outL = new StringWriter();
			obj1.writeJSONString(outL);
			jsonText = outL.toString();


		} catch (Exception e) {e.printStackTrace(); System.out.println("JSON ERROR");}

		return jsonText;

	}//**********************








	//If the database is empty we have to download the blockchain.

	public void download_blockchain(String onionAddress){

		System.out.println("Download blockchain...");

		String jsonText = "";

		try{

			JSONObject obj = new JSONObject();
			obj.put("request","blocks_get_first");

			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			System.out.println(jsonText);

		} catch(Exception e) {System.out.println("JSON ERROR");}



		try {

			System.out.println("address: " + client_port_connect);
			System.out.println("onionAddress: " + onionAddress);

			System.out.println("socket");

			Socket socket = Utilities.socks4aSocketConnection(onionAddress, hiddenServicePort, local_host_connect, localPort);//127.0.0.1
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


            Object obj = null;

            //This sometimes throws an error if we get a response that is corrupted.
            //This will shutdown the app.
            //java.lang.Error: Error: could not match input
            try {

                obj = parser.parse(modifiedSentence);

            } catch(Error e) {System.out.println("Response is unreadable..");}

			JSONObject jsonObject = (JSONObject) obj;

			String response = (String) jsonObject.get("response");
			String message = (String) jsonObject.get("message");
			System.out.println("JSON " + response);

			if (response.equals("1")) {

                Object obj2 = null;

                //This sometimes throws an error if we get a response that is corrupted.
                //This will shutdown the app.
                //java.lang.Error: Error: could not match input
                try {

                    obj2 = parser.parse(message);

                } catch (Error e) {System.out.println("Response is unreadable..");}

				//Object obj2 = parser.parse(message);
				JSONObject jsonObject2 = (JSONObject) obj2;

				String test_new_block = (String) jsonObject2.get("m5");
				String test_old_block = (String) jsonObject2.get("m4");

				System.out.println("test_new_block " + test_new_block);
				System.out.println("test_old_block " + test_old_block);

				network.last_block_mining_idx = test_old_block;

				//Now that we have the first block we can update.
				request_blocks_x_update(onionAddress);


			}//***********************

		} catch (Exception e) {e.printStackTrace();}



	}//*******************************








	//Main stats request asks the network for the current status happens every 10 seconds.

	public String request_status(int node, String onionAddress){//*****************************************************************


		System.out.println("Request Status " + node);
		System.out.println("Installing...  " + network.installing_package);

        String jsonText = "";
		String jsonSentence = "";


		try {

			JSONObject obj = new JSONObject();
			obj.put("request","status");

			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			System.out.println(jsonText);

		} catch (Exception e) {System.out.println("JSON ERROR");}


		try {

			System.out.println("address:      " + client_port_connect);
			System.out.println("onionAddress: " + onionAddress);

			Socket socket = Utilities.socks4aSocketConnection(onionAddress, hiddenServicePort, local_host_connect, localPort);//127.0.0.1
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

			Object obj = null;

			//This sometimes throws an error if we get a response that is corrupted.
			//This will shutdown the app.
			//java.lang.Error: Error: could not match input
			try {

				obj = parser.parse(modifiedSentence);

			} catch (Error e) {e.printStackTrace();}

			JSONObject jsonObject = (JSONObject) obj;

			String response = (String) jsonObject.get("response");
			String message = (String) jsonObject.get("message");
			System.out.println("JSON " + response);

			if (response.equals("1")) {

				jsonSentence = "active";

                Object obj2 = null;

                //This sometimes throws an error if we get a response that is corrupted.
                //This will shutdown the app.
                //java.lang.Error: Error: could not match input
                try {

                    obj2 = parser.parse(message);

                } catch (Error e) {System.out.println("Response is unreadable..");}

				//Object obj2 = parser.parse(message);
				JSONObject jsonObject2 = (JSONObject) obj2;

                //Get strings from the JSON string.
				String block_idx = (String) jsonObject2.get("last_block_idx");
                String block_id = (String) jsonObject2.get("last_block_id");
				String block_old = (String) jsonObject2.get("prev_block_idx");
				String block_timestamp = (String) jsonObject2.get("last_block_timestamp");
				String unconfirmed_block = (String) jsonObject2.get("last_unconfirmed_id");
				String network_size = (String) jsonObject2.get("node_list");
				String network_id = (String) jsonObject2.get("network");
                String block_speed = (String) jsonObject2.get("block_speed");
                String difficultyx = (String) jsonObject2.get("difficulty");
                String unconfirmed = (String) jsonObject2.get("unconfirmed");

				//our program is different then the servers probably another version of the program
				if (!network_id.equals(network.idx)) {network_error(onionAddress);}

                //Display some info.
				System.out.println("block " + block_idx);
				System.out.println("unconfirmed_block    " + unconfirmed_block);
				System.out.println("last_unconfirmed_idx " + network.last_unconfirmed_idx);
				System.out.println("network_size         " + network_size);

                //Teset information.
				if (node == 0) {new_hash1 = block_idx; network.no_peers_time0 = 0;}
				if (node == 1) {new_hash2 = block_idx; network.no_peers_time1 = 0;}
				if (node == 2) {new_hash3 = block_idx; network.no_peers_time2 = 0;}
				if (node == 3) {new_hash4 = block_idx; network.no_peers_time3 = 0;}

				//Here we test to see if the new block we have is different then the server but the old block is the same.
                //That would mean we have a stale block.
				if (!block_idx.equals(network.last_block_mining_idx) && block_old.equals(network.prev_block_mining_idx)) {

					System.out.println("One STALE block.");

                    if(Long.parseLong(block_timestamp) < Long.parseLong(network.last_block_timestamp)){}
					else{test_if_stale(); network.fork_errors_one++;}

				}//*******************************************************************************************************




				try {

				    //This is where we show the problem.
					//Test the new hash to see if we have the most recent.
					if (block_idx.equals(network.last_block_mining_idx)){System.out.println("Blocks up to date..."); mining.mining2 = true; network.blocks_uptodate = true;}
					else if (Long.parseLong(block_timestamp) < Long.parseLong(network.last_block_timestamp)){System.out.println("Blocks are ahead..."); mining.mining2 = true; network.blocks_uptodate = true; reloadx();}
					else {System.out.println("Blocks are not up to date...");mining.mining2 = false; network.blocks_uptodate = false;}

                    //Test the pending database to see if there are new items.
                    if(unconfirmed_block.equals(network.last_unconfirmed_idx) || unconfirmed_block.equals("")){System.out.println("Unconfirmed blocks up to date...");}
                    else{System.out.println("Unconfirmed blocks are not up to date...");}

                    //Remember the servers block in case we need to use it later.
                    //We save the new and old mining block because we need both to get exactly what we need.
                    //Sometimes we need the new one sometimes we need the old one.
                    //So here and in the next part we save both.
                    if(network.last_remote_mining_idx.length() == 0 && !network.blocks_uptodate){network.last_remote_mining_idx = block_idx;}
                    else if(network.blocks_uptodate){network.last_remote_mining_idx = "";}

                    //Remember the servers block in case we need to use it later.
                    if(network.last_remote_mining_prev_idx.length() == 0 && !network.blocks_uptodate){network.last_remote_mining_prev_idx = block_idx;}
                    else if(network.blocks_uptodate){network.last_remote_mining_prev_idx = "";}

                    //These settings are reversed so that we can see what needs to be done by highest priority.
					if(network.installing_package || network.installing_n){System.out.println("Installing package break!");}
					else if(network.blocks_uptodate && network.hard_token_count < network.hard_token_limit){attempt_repair(onionAddress);}
                    else if(Long.parseLong(block_timestamp) < Long.parseLong(network.last_block_timestamp) && network.time_since_last_block > network.last_block_time_error){update_server(block_idx,onionAddress);}//server is behind and old
                    else if(Long.parseLong(block_timestamp) < Long.parseLong(network.last_block_timestamp)){System.out.println("Server is behind...");}//server is behind
                    else if(!block_idx.equals(network.last_block_mining_idx)){network.blocks_uptodate = false; mining.mining2 = false;}//update x will be called
                    else if(unconfirmed_test.testx_hash(network.last_unconfirmed_idx) == 0 && !unconfirmed_block.equals("")){request_unconfirmed_block_update(unconfirmed_block,onionAddress);}
                    else if(network.send_buffer_size > 0){send_unconfirmed_update();}
                    else{System.out.println("Nothing this time...");}

                } catch (Exception e) {mining.mining_stop = true; network.blocks_uptodate = false; System.out.println("Blocks have errors...");}


                try {

                    System.out.println("[installing] status_test " + status_test);

                    //If the server is ahead of us and we can't find a block in a set time we delete our new block it's probably stale.
                    if (Long.parseLong(block_timestamp) > Long.parseLong(network.last_block_timestamp)) {

                        System.out.println("[installing] save_last_block_time    " + save_last_block_time);
                        System.out.println("[installing] network.last_block_time " + network.last_block_time);

                        //If these are the same then update didn't work so we should try deleting our block.
                        if (save_last_block_time == network.last_block_time) {

                            //Test a few times before we delete anything.
                            //This has errors where we request something but nothing is returned.
                            if (status_test > 10) {

                                System.out.println("One STALE block too old.");

                                System.out.println("time_since_last_block  " + network.time_since_last_block);
                                System.out.println("stale_time             " + network.stale_time);

                                if (network.time_since_last_block > network.stale_time && !network.installing_package) {test_if_stale(); network.fork_errors_one++;}

                                status_test = 0;

                            }//*******************

                            status_test++;

                        }//*************************************************
                        else {status_test = 0;}

                    }//********************************************************************************

                } catch (Exception e) {}


                save_last_block_time = network.last_block_time;


            }//***********************
			else{jsonSentence = "error";}


		} catch (Exception e) {

			e.printStackTrace();
			System.out.println("Cannot find node!");
			jsonSentence = "not active!";

		}//******************


		return jsonSentence;

	}//*******************************************************************************************************





    //Sometimes the server has a new block and we have a new block but they are not the same.
    //However, if our previous blocks are the same then it's a simple matter of two blocks being mined at the same time.
    //We delete ours if theirs is older.

	public void test_if_stale(){

		krypton_update_block_stale testx = new krypton_update_block_stale();
		boolean test1 = testx.update();

	}//**************************


    public void reloadx(){

	    //krypton_database_load loadx = new krypton_database_load();
	    loadx.load();

    }//*******************


    //If the server has a different network version when we delete them from our node list.

	public void network_error(String onionAddress){

		System.out.println("Connected to the wrong network.");

		System.out.println("Delete network node: " + onionAddress);

		krypton_database_node nodex = new krypton_database_node();
		nodex.deleteNode(onionAddress);

	}//************************






	//Main stats request asks the network for the current status happens every 10 seconds.

	public String request_status_partial_node(int node,  String onionAddress){//*****************************************************************


		System.out.println("Request Status partial node...");

        String jsonText = "";
		String jsonSentence = "";


        try {

            JSONObject obj = new JSONObject();
            obj.put("request","status");

            StringWriter out = new StringWriter();
            obj.writeJSONString(out);
            jsonText = out.toString();
            System.out.println(jsonText);

        } catch (Exception e) {System.out.println("JSON ERROR");}


        try {

            System.out.println("address: " + client_port_connect);
            System.out.println("onionAddress: " + onionAddress);

            Socket socket = Utilities.socks4aSocketConnection(onionAddress, hiddenServicePort, local_host_connect, localPort);//127.0.0.1
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


			Object obj = null;

			//This sometimes throws an error if we get a response that is corrupted.
			//This will shutdown the app.
			//java.lang.Error: Error: could not match input
			try {

				obj = parser.parse(modifiedSentence);

			} catch (Error e) {e.printStackTrace();}

			JSONObject jsonObject = (JSONObject) obj;

            String response = (String) jsonObject.get("response");
            String message = (String) jsonObject.get("message");
            System.out.println("JSON " + response);

            if (response.equals("1")) {

                jsonSentence = "active";

                Object obj2 = null;

                //This sometimes throws an error if we get a response that is corrupted.
                //This will shutdown the app.
                //java.lang.Error: Error: could not match input
                try {

                    obj2 = parser.parse(message);

                } catch (Error e) {System.out.println("Response is unreadable..");}

                //Object obj2 = parser.parse(message);
                JSONObject jsonObject2 = (JSONObject) obj2;

                //Get strings from the JSON string.
                String block_idx = (String) jsonObject2.get("last_block_idx");
                String block_id = (String) jsonObject2.get("last_block_id");
                String block_old = (String) jsonObject2.get("prev_block_idx");
                String block_timestamp = (String) jsonObject2.get("last_block_timestamp");
                String unconfirmed_block = (String) jsonObject2.get("last_unconfirmed_id");
                String network_size = (String) jsonObject2.get("node_list");
                String network_id = (String) jsonObject2.get("network");
                String block_speed = (String) jsonObject2.get("block_speed");
                String difficultyx = (String) jsonObject2.get("difficulty");
                String unconfirmed = (String) jsonObject2.get("unconfirmed");

                //Display some info.
                System.out.println("block             " + block_idx);
                System.out.println("unconfirmed_block " + unconfirmed_block);
                System.out.println("network_size      " + network_size);

				//Update all the connection information for the system and the user.
				if(node == 0){new_hash1 = block_idx; network.peersx0 = true; network.no_peers_time0 = 0;}
                if(node == 1){new_hash2 = block_idx; network.peersx1 = true; network.no_peers_time1 = 0;}
                if(node == 2){new_hash3 = block_idx; network.peersx2 = true; network.no_peers_time2 = 0;}
                if(node == 3){new_hash4 = block_idx; network.peersx3 = true; network.no_peers_time3 = 0;}

                //No mining or updating in this system.
                System.out.println("Blocks up to date...");
                mining.mining_stop = false;
                network.blocks_uptodate = true;

                //Test to see if there are new peers we may want. network.open_network.
                if(network.network_size >= Integer.parseInt(network_size) || !network.open_network){System.out.println("Network is up to date...");}
                else{System.out.println("Network is missing nodes...");}


                //Update our display for the user.
                network.last_block_id = block_id;
                network.last_block_longstamp1 = Long.parseLong(block_timestamp);
                network.blocktimesx = Long.parseLong(block_speed);
                network.difficultyx = new BigInteger(difficultyx);
                network.database_unconfirmed_total = Integer.parseInt(unconfirmed);


                //krypton_update_listings_lite liteclient = new krypton_update_listings_lite();
                //liteclient.loadLiteDB();//Load the listings so the user can see.

				//We only do this every few loops to save server resources.
				System.out.println("last_block_longstamp_save:     " + last_block_longstamp_save);
                System.out.println("network.last_block_longstamp1: " + network.last_block_longstamp1);
				if(network.last_block_longstamp1 > last_block_longstamp_save) {

					request_user_listings(onionAddress);
					//partial_peer_update_loop = 0;
                    last_block_longstamp_save = network.last_block_longstamp1;

				}//************************************************************
                else {

                    //krypton_update_listings_lite liteclient = new krypton_update_listings_lite();
                    liteclient.loadLiteDB();//Load the listings so the user can see.

                }

				//partial_peer_update_loop++;

                //If we have something to send, send it.
                if(network.send_buffer_size > 0){send_unconfirmed_update();}


            }//***********************
            else {jsonSentence = "not active!";}


        } catch (Exception e) {

            e.printStackTrace();
            System.out.println("Cannot find node!");
            jsonSentence = "Error!";

        }//******************


		//network.tor_in_use = 0;
		return jsonSentence;

	}//*******************************************************************************************************








	public void attempt_repair(String onionAddress){

		//Some old phones have trouble with adding and removing database items. Could be an error but it doesn't seem to happen with faster phones.
		//So this can be useful.

		System.out.println("Attempt repair 1...");

		krypton_database_repair_tools tools = new krypton_database_repair_tools();

		List<String> blistx = tools.getBlockchainList();

		if(blistx.size() != network.hard_token_limit){System.out.println("Error database can not be repaired...");}
		else{

			System.out.println("Attempt repair 2...");

			List<String> tlistx = tools.getTokenList();

			Set<String> set1 = new HashSet<String>(blistx);

			System.out.println("Set1: " + set1.size());

			set1.removeAll(tlistx);

			System.out.println("Set2: " + set1.size());

			if(set1.size() > 0){

				System.out.println("Attempt repair 3...");

				List<String> elistx = new ArrayList<String>();

				elistx.addAll(set1);

				for (int loop = 0; loop < elistx.size(); loop++) {//*************

					System.out.println("Find: " + elistx.get(loop));

					String[] listingx = get_listing_from_hash(elistx.get(loop), onionAddress);

					boolean testx = tools.addMissingToken(listingx);

					System.out.println("Install: " + testx);

				}//**************************************************************

			}//*****************


			//Refresh.
			//krypton_database_load loadx = new krypton_database_load();
			loadx.load();


		}//**


	}//***************************







    //Here we are using a lite "SPV" wallet and we need to see if the server has anything new relating to us.

    public String request_user_listings(String onionAddress){//*****************************************************************


        System.out.println("Request User Listings");

        String jsonText = "";
        String jsonSentence = "";


        try {

            JSONObject obj = new JSONObject();
            obj.put("request","get_user_listings");
            obj.put("key_id", network.base58_id);

            StringWriter out = new StringWriter();
            obj.writeJSONString(out);
            jsonText = out.toString();
            System.out.println(jsonText);

        } catch (Exception e) {System.out.println("JSON ERROR");}


        try {

            System.out.println("address: " + client_port_connect);
            System.out.println("onionAddress: " + onionAddress);

            System.out.println("socket");

            Socket socket = Utilities.socks4aSocketConnection(onionAddress, hiddenServicePort, local_host_connect, localPort);//127.0.0.1
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

			Object obj = null;

			//This sometimes throws an error if we get a response that is corrupted.
			//This will shutdown the app.
			//java.lang.Error: Error: could not match input
			try {

				obj = parser.parse(modifiedSentence);

			} catch (Error e) {e.printStackTrace();}

			JSONObject jsonObject = (JSONObject) obj;

            String response = (String) jsonObject.get("response");
            String message = (String) jsonObject.get("message");
            System.out.println("JSON " + response);

            Object obj2 = null;

            //This sometimes throws an error if we get a response that is corrupted.
            //This will shutdown the app.
            //java.lang.Error: Error: could not match input
            try {

                obj2 = parser.parse(message);

            } catch (Error e) {System.out.println("Response is unreadable..");}

            //Object obj2 = parser.parse(message);
            JSONObject jsonObject2 = (JSONObject) obj2;

            System.out.println("Size1: " + jsonObject2.size());

            String[] remote_list = new String[jsonObject2.size()];

            krypton_database_get_user_token_list list2 = new krypton_database_get_user_token_list();
            String[][] list2x = list2.getTokenXnodeList();

            System.out.println("Size2: " + list2x[0].length);

            //krypton_update_listings_lite liteclient = new krypton_update_listings_lite();

            //First we test the new items against ours to add the ones we don't have.
            for (int loop = 0; loop < jsonObject2.size(); loop++){//********************************

                String bufferp = (String) jsonObject2.get(Integer.toString(loop));
                System.out.println("loop " + loop);
                //System.out.println("break up " + bufferp);

                Object obj3 = null;

                //This sometimes throws an error if we get a response that is corrupted.
                //This will shutdown the app.
                //java.lang.Error: Error: could not match input
                try {

                    obj3 = parser.parse(bufferp);

                } catch (Error e) {System.out.println("Response is unreadable..");}

                //Object obj3 = parser.parse(bufferp);
                JSONObject jsonObject3 = (JSONObject) obj3;

                String id = (String) jsonObject3.get("id");
                String hash = (String) jsonObject3.get("hash");

                //save this for the next test.
                remote_list[loop] = hash;

                System.out.println("ID: " + id + " " + hash);

                boolean found = false;
                for (int loop2 = 0; loop2 < list2x[0].length; loop2++){//*************

                    if(list2x[1][loop2].equals(hash)){found = true; break;}

                }//for****************************************************************

                System.out.println("found " + found);
                if(!found){download_listing(hash, onionAddress);}

                //Reload the lite "SPV" system.
                liteclient.loadLiteDB();

            }//for**********************************************************************************


            //Now we test our listings against the new ones to see which ones we should delete.
            for (int loop = 0; loop < list2x[0].length; loop++){//*************

                boolean found = false;
                for (int loop2 = 0; loop2 < jsonObject2.size(); loop2++) {//************************

                    if(remote_list[loop2].equals(list2x[1][loop])) {found = true; break;}

                }//*********************************************************************************

                System.out.println("found " + list2x[0][loop] + " " + found);
                if(!found){liteclient.deleteToken(list2x[1][loop]);}

                //Reload the lite "SPV" system.
                liteclient.loadLiteDB();

            }//for*************************************************************


        } catch (Exception e) {

            e.printStackTrace();
            System.out.println("Cannot find node!");
            jsonSentence = "0";

        }//*****************


        return jsonSentence;

    }//***********************************************************************************************






    //If we find something new that has been sent to use or changed we need to update our lite database.

    public String download_listing(String hash, String onionAddress){//*****************************************************************


        String jsonText = "";
        String jsonSentence = "";


        try{

            JSONObject obj = new JSONObject();
            obj.put("request","get_token_idx");
            obj.put("hash_id", hash);

            StringWriter out = new StringWriter();
            obj.writeJSONString(out);
            jsonText = out.toString();
            System.out.println(jsonText);

        } catch (Exception e) {System.out.println("JSON ERROR");}


        try {

            System.out.println("address: " + client_port_connect);
            System.out.println("onionAddress: " + onionAddress);

            System.out.println("socket");

            Socket socket = Utilities.socks4aSocketConnection(onionAddress, hiddenServicePort, local_host_connect, localPort);//127.0.0.1
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

			Object obj = null;

			//This sometimes throws an error if we get a response that is corrupted.
			//This will shutdown the app.
			//java.lang.Error: Error: could not match input
			try {

				obj = parser.parse(modifiedSentence);

			} catch (Error e) {e.printStackTrace();}

			JSONObject jsonObject = (JSONObject) obj;

            String response = (String) jsonObject.get("response");
            String message = (String) jsonObject.get("message");
            System.out.println("JSON " + response);

            String update_token[] = new String[network.listing_size];

            Object obj2 = null;

            //This sometimes throws an error if we get a response that is corrupted.
            //This will shutdown the app.
            //java.lang.Error: Error: could not match input
            try {

                obj2 = parser.parse(message);

            } catch (Error e) {System.out.println("Response is unreadable..");}

            //Object obj2 = parser.parse(message);
            JSONObject jsonObject2 = (JSONObject) obj2;

            for(int loop = 0; loop < network.listing_size; loop++) {//******************************

                update_token[loop] = (String) jsonObject2.get("l" + Integer.toString(loop));
                //System.out.println("convert " + update_token[loop]);

            }//*************************************************************************************

            //krypton_update_listings_lite liteclient = new krypton_update_listings_lite();
            liteclient.addToken(update_token);


        } catch (Exception e) {

            e.printStackTrace();
            System.out.println("Cannot find node!");
            jsonSentence = "0";

        }//*****************


        return jsonSentence;

    }//***********************************************************************************************







	//If we find something new that has been sent to use or changed we need to update our lite database.

	public String[] get_listing_from_hash(String hash, String onionAddress){//*****************************************************************


		String jsonText = "";
		String jsonSentence[] = null;


		try{

			JSONObject obj = new JSONObject();
			obj.put("request","get_token_idx");
			obj.put("hash_id", hash);

			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			System.out.println(jsonText);

		} catch (Exception e) {System.out.println("JSON ERROR");}


		try {

			System.out.println("address: " + client_port_connect);
			System.out.println("onionAddress: " + onionAddress);

			System.out.println("socket");

			Socket socket = Utilities.socks4aSocketConnection(onionAddress, hiddenServicePort, local_host_connect, localPort);//127.0.0.1
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

			Object obj = null;

			//This sometimes throws an error if we get a response that is corrupted.
			//This will shutdown the app.
			//java.lang.Error: Error: could not match input
			try {

				obj = parser.parse(modifiedSentence);

			} catch (Error e) {e.printStackTrace();}

			JSONObject jsonObject = (JSONObject) obj;

			String response = (String) jsonObject.get("response");
			String message = (String) jsonObject.get("message");
			System.out.println("JSON " + response);

			String update_token[] = new String[network.listing_size];

			Object obj2 = null;

			//This sometimes throws an error if we get a response that is corrupted.
			//This will shutdown the app.
			//java.lang.Error: Error: could not match input
			try {

				obj2 = parser.parse(message);

			} catch (Error e) {System.out.println("Response is unreadable..");}

			//Object obj2 = parser.parse(message);
			JSONObject jsonObject2 = (JSONObject) obj2;

			for(int loop = 0; loop < network.listing_size; loop++) {//******************************

				update_token[loop] = (String) jsonObject2.get("l" + Integer.toString(loop));
				//System.out.println("convert " + update_token[loop]);

			}//*************************************************************************************


			jsonSentence = update_token;

		} catch (Exception e) {

			e.printStackTrace();
			System.out.println("Cannot find node!");

		}//*****************


		return jsonSentence;

	}//***********************************************************************************************







	//Broadcast to the network any changes to our tokens.

	public String send_unconfirmed_update(){//*****************************************************************


		System.out.println("Send unconfirmed package update...");

        String[][] token_id = null;

		String jsonText = "";
		String jsonSentence = "";
        JSONArray array = null;

		try{


			krypton_database_get_buffer bufferx = new krypton_database_get_buffer();
			token_id = bufferx.getTokens();

			JSONObject obj2 = new JSONObject();

			for (int loop1 = 0; loop1 < token_id[0].length; loop1++) {//***********

				JSONObject obj1 = new JSONObject();

				for (int loopx = 0; loopx < token_id.length; loopx++) {//****************

					obj1.put(Integer.toString(loopx), token_id[loopx][loop1]);

				}//**********************************************************************


				StringWriter out1 = new StringWriter();
				obj1.writeJSONString(out1);
				String jsonTextx = out1.toString();
				System.out.println(jsonText);

				obj2.put(Integer.toString(loop1), jsonTextx);

			}//********************************************************************

			//token_id_save = token_id[0][0];

			JSONObject obj = new JSONObject();
			obj.put("request","add_new_unconfirmed");
			obj.put("unconfirmed_package", obj2);

			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			System.out.println(jsonText);


		} catch (Exception e) {System.out.println("JSON ERROR");}


		try {


			for (int loop = 0; loop < network.system_peers; loop++){//**********************************************************************************************************

				String onionAddress = null;

				//Go through the list of active peers and send them our block.
				if(network.use_one_peer){onionAddress = network.onionAddress;}
				else if(loop == 0 && network.peersx0){onionAddress = network.peerid0;}//network.settingsx[9];
				else if(loop == 1 && network.peersx1){onionAddress = network.peerid1;}//network.settingsx[10];
				else if(loop == 2 && network.peersx2){onionAddress = network.peerid2;}//network.settingsx[11];
				else if(loop == 3 && network.peersx3){onionAddress = network.peerid3;}//network.settingsx[12];

				System.out.println("Send to " + loop + " " + onionAddress);

				//If it's null then we skip to the next one.
				if(onionAddress == null){continue;}

				System.out.println("socket");

				Socket socket = Utilities.socks4aSocketConnection(onionAddress, hiddenServicePort, local_host_connect, localPort);//127.0.0.1
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

				Object obj = null;

				//This sometimes throws an error if we get a response that is corrupted.
				//This will shutdown the app.
				//java.lang.Error: Error: could not match input
				try {

					obj = parser.parse(modifiedSentence);

				} catch (Error e) {e.printStackTrace();}

				JSONObject jsonObject = (JSONObject) obj;

				String response = (String) jsonObject.get("response");
				String message = (String) jsonObject.get("message");
				System.out.println("JSON " + response);

				if (response.equals("1")) {

                    jsonSentence = "Unconfirmed package added.";

                    Object obj2 = null;

                    //This sometimes throws an error if we get a response that is corrupted.
                    //This will shutdown the app.
                    //java.lang.Error: Error: could not match input
                    try {

                        obj2 = parser.parse(message);

                    } catch (Error e) {System.out.println("Response is unreadable..");}

					//Object obj2 = parser.parse(message);
					array = (JSONArray) obj2;

					System.out.println(array.size());

					for (int loopx = 0; loopx < array.size(); loopx++) {


						String tokenxid = array.get(loopx).toString();

						System.out.println("array " + tokenxid);

						//Get the net buffer token.
						krypton_database_get_buffer getbx = new krypton_database_get_buffer();
						String new_token[] = getbx.getTokenID(tokenxid);

						//Get the old token.
						krypton_database_get_token getxt = new krypton_database_get_token();
						String old_token[] = getxt.getToken(tokenxid);

						System.out.println("ID " + new_token[0] + " = " + tokenxid);


						if (new_token[0] != null) {


                            //We cannot add this to our unconfirmed list if we are not a full node.
                            //It will be stuck there and not be able to be deleted.
                            if (network.full_node) {

                                //Try to add the new token.
                                //This will be called many times as we go through our peer list.
                                krypton_update_token_remote remotex = new krypton_update_token_remote();
                                boolean test = remotex.update(new_token, old_token);

                            }//**************************


                            //Delete from the pending list.
                            krypton_database_delete_buffer bufferd = new krypton_database_delete_buffer();
                            bufferd.deleteID(tokenxid);


                        }//**************************



					}//***********************************************


				}//***********************
				else{}


			}//for*******************************************************************************************************************************************



			//We can't load this if we are a lite node because it will show the user the wrong data.
			if(network.full_node) {

				//krypton_database_load loadx = new krypton_database_load();
				loadx.load();

			}//********************

			peer1sendUnconfiremdB1 = null;


		} catch (Exception e) {

			e.printStackTrace();
			System.out.println("Cannot find node!");
            jsonSentence = "Not active!";

		}//******************


		return jsonSentence;

	}//***************************************************************************************************************






	//if we find a new block it calls send_new_block_update, then we call send_new_block_update2 when tor is ready.

	public String send_new_block_update2(){//*****************************************************************


		String[] mining_id = peer1sendBufferB1;
		String[] token_id = peer1sendBufferB2;

		String jsonText = "";
		String jsonSentence = "";


		try{


			JSONObject obj1 = new JSONObject();
			int xxp1 = 0;
			int xxp2 = 0;

			for (int loop = 0; loop < mining_id.length; loop++){//************

				obj1.put("m" + Integer.toString(xxp1), mining_id[loop]);
				xxp1++;

			}//***************************************************************

			for (int loop = 0; loop < token_id.length; loop++){//*************

				obj1.put("l" + Integer.toString(xxp2), token_id[loop]);
				xxp2++;

			}//***************************************************************

			JSONObject obj = new JSONObject();
			obj.put("request","add_new_block");
			obj.put("token", obj1);

			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			System.out.println(jsonText);


		} catch (Exception e) {

			System.out.println("JSON ERROR");
			network.mining_block_ready = false;//network.mining_block_ready = 0;

		}//*****************


		try{

			//This loop is in case we add more peers in the future right now there is only 1.
			for (int loop = 0; loop < network.system_peers; loop++){//***************************************************************************************

				String onionAddress = null;

				//Go through the list of active peers and send them our block.
				if(network.use_one_peer){onionAddress = network.onionAddress;}
				else if(loop == 0 && network.peersx0){onionAddress = network.peerid0;}//network.settingsx[9];
				else if(loop == 1 && network.peersx1){onionAddress = network.peerid1;}//network.settingsx[10];
				else if(loop == 2 && network.peersx2){onionAddress = network.peerid2;}//network.settingsx[11];
				else if(loop == 3 && network.peersx3){onionAddress = network.peerid3;}//network.settingsx[12];

				System.out.println("Send to " + loop + " " + onionAddress);

				//If it's null then we skip to the next one.
				if(onionAddress == null){continue;}

				System.out.println("socket");

				Socket socket = Utilities.socks4aSocketConnection(onionAddress, hiddenServicePort, local_host_connect, localPort);//127.0.0.1
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


				Object obj = null;

				//This sometimes throws an error if we get a response that is corrupted.
				//This will shutdown the app.
				//java.lang.Error: Error: could not match input
				try {

					obj = parser.parse(modifiedSentence);

				} catch (Error e) {e.printStackTrace();}

				JSONObject jsonObject = (JSONObject) obj;


				String response = (String) jsonObject.get("response");
				String message = (String) jsonObject.get("message");
				System.out.println("JSON " + response);

				if (response.equals("1")) {

					System.out.println("message " + message);

					//if the server accepts our block then we know we are not the only one and we can update our chain...

					//get the last token
					krypton_database_get_token getxt = new krypton_database_get_token();

					String req_id = token_id[0];
					String[] old_token = getxt.getToken(req_id);

					//add the new block to the chain
					krypton_update_new_block_remote remotex = new krypton_update_new_block_remote();
					boolean test = remotex.update(token_id, mining_id, old_token);

					//Reload to get all the new info from the update
					//krypton_database_load loadx = new krypton_database_load();
                    loadx.load();

					jsonSentence = "1";

				}//***********************
				else{jsonSentence = "error";}


				System.out.println("1335 Delete from unconfirmed...");
				krypton_database_delete_unconfirmed bufferd = new krypton_database_delete_unconfirmed();
				bufferd.deleteID(token_id[0]);


			}//for*******************************************************************************************************************************************


		} catch (Exception e) {

			e.printStackTrace();
			System.out.println("Cannot find node!");
			jsonSentence = "not active!";

		}


		peer1sendBufferB1 = null;
		peer1sendBufferB2 = null;


		//network.tor_in_use = 0;
		return jsonSentence;

	}//***********************************************************************************************





	//Same as send_new_block_update except there we send a package of blocks not just one.

	public String send_new_package_update2(){//*****************************************************************


        System.out.println("Send new package update 2");

		String jsonText = "";
		String jsonSentence = "";


		try{

			JSONObject obj = new JSONObject();
			obj.put("request","add_new_package");
			obj.put("package", peer1sendPackageB1);

			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			System.out.println(jsonText);

		} catch (Exception e) {System.out.println("JSON ERROR");}


		try{


			for (int loop = 0; loop < network.system_peers; loop++){//******************************

                String onionAddress = null;

                //Go through the list of active peers and send them our block.
                if(network.use_one_peer){onionAddress = network.onionAddress;}
                else if(loop == 0 && network.peersx0){onionAddress = network.peerid0;}//network.settingsx[9];
                else if(loop == 1 && network.peersx1){onionAddress = network.peerid1;}//network.settingsx[10];
                else if(loop == 2 && network.peersx2){onionAddress = network.peerid2;}//network.settingsx[11];
                else if(loop == 3 && network.peersx3){onionAddress = network.peerid3;}//network.settingsx[12];

                System.out.println("Send to " + loop + " " + onionAddress);

                //If it's null then we skip to the next one.
                if(onionAddress == null){continue;}

                System.out.println("socket");

				Socket socket = Utilities.socks4aSocketConnection(onionAddress, hiddenServicePort, local_host_connect, localPort);//127.0.0.1
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


				Object obj = null;

				//This sometimes throws an error if we get a response that is corrupted.
				//This will shutdown the app.
				//java.lang.Error: Error: could not match input
				try {

					obj = parser.parse(modifiedSentence);

				} catch (Error e) {e.printStackTrace();}

				JSONObject jsonObject = (JSONObject) obj;

				String response = (String) jsonObject.get("response");
				String message = (String) jsonObject.get("message");
				System.out.println("JSON " + response);

				if (response.equals("1")) {

					System.out.println("message " + message);

					jsonSentence = "1";

				}//***********************
				else {jsonSentence = "error";}


				//System.out.println("1336 Delete from unconfirmed...");
				//krypton_database_delete_unconfirmed bufferd = new krypton_database_delete_unconfirmed();
				//bufferd.deleteFirst();

			}//for**********************************************************************************


		} catch (Exception e) {

			e.printStackTrace();
			System.out.println("Cannot find node!");
			jsonSentence = "not active!";

		}


		peer1sendPackageB1 = null;
		network.mining_package_ready = false;

		return jsonSentence;

	}//***********************************************************************************************







    //In some cases the server will be behind ours, if that's the case we can update them with our blocks.
    //This is the case when the server has an incoming connection but for some reason they cannot connect to anyone.
    //Maybe they don't have any node addresses or they have some connection problem.
    //In most cases though the outgoing TOR works better then incoming.

	public void update_server(String idx, String onionAddress){

		System.out.println("Server is behind, update with our blocks.");

		System.out.println("last_block_time       " + network.time_since_last_block);
		System.out.println("last_block_time_error " + network.last_block_time_error);

		//Sometimes we just need to reload to solves these problems.
		//krypton_database_load loadx = new krypton_database_load();
        loadx.load();

		String jsonText = "";

		boolean test_statex = false;


		try{


			String[][] token_array;

			System.out.println("idx " + idx);
			System.out.println("last_block_mining_idx " + network.last_block_mining_idx);

			//idx = "00002D476806AD5C56DEA0BB487E54AEA27FF60DEB1D2833010788B6C0C2C8F0";


			System.out.println("SLOW LOAD");
			krypton_database_get_token_fmh_x fmhx = new krypton_database_get_token_fmh_x();
			token_array = fmhx.get_tokens(idx, network.package_block_size);

			System.out.println("token_array[0].length " + token_array[0].length);

			if(token_array[0].length != 0 && !token_array[0][0].equals("error")){

				test_statex = true;

				JSONObject obj1 = new JSONObject();
				for (int loop1 = 0; loop1 < token_array[0].length; loop1++){//************

					//String jxsonarry = "";

					JSONObject obj2 = new JSONObject();
					int xxp1 = 0;
					int xxp2 = 0;

					for (int loop = 0; loop < network.miningx_size; loop++){//*************

						obj2.put("m" + Integer.toString(xxp1), token_array[loop][loop1]);
						//System.out.println("m" + token_array[loop][loop1]);
						xxp1++;

					}//********************************************************************

					for (int loop = network.miningx_size; loop < token_array.length; loop++){//*************

						obj2.put("l" + Integer.toString(xxp2), token_array[loop][loop1]);
						//System.out.println("l" + token_array[loop][loop1]);
						xxp2++;

					}//*************************************************************************************


					StringWriter out = new StringWriter();
					obj2.writeJSONString(out);
					String jsonTextx = out.toString();
					System.out.println(jsonTextx);

					//jxsonarry = JSONValue.toJSONString(obj2);

					obj1.put(Integer.toString(loop1), JSONValue.toJSONString(obj2));

				}//***********************************************************************


                JSONObject obj = new JSONObject();
                obj.put("request","add_new_package");
                obj.put("package", obj1);

                StringWriter out = new StringWriter();
                obj.writeJSONString(out);
                jsonText = out.toString();
                System.out.println(jsonText);


			}//if**********************************
			else{test_statex = false;}


		} catch (Exception e) {e.printStackTrace();}


		try {

			System.out.println("address: " + client_port_connect);
			System.out.println("onionAddress: " + onionAddress);

			System.out.println("socket");

			Socket socket = Utilities.socks4aSocketConnection(onionAddress, hiddenServicePort, local_host_connect, localPort);//127.0.0.1
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


			Object obj = null;

			//This sometimes throws an error if we get a response that is corrupted.
			//This will shutdown the app.
			//java.lang.Error: Error: could not match input
			try {

				obj = parser.parse(modifiedSentence);

			} catch (Error e) {e.printStackTrace();}

			JSONObject jsonObject = (JSONObject) obj;


		} catch (Exception e) {

			e.printStackTrace();
			System.out.println("Cannot find node!");

		}//*****************


	}//********************************************







	//Package block update we can request a package of blocks so that updating if faster.

	public String request_blocks_x_update(String onionAddress){//*****************************************************************


		String jsonSentence = "0";
		String response =  "0";

		//We don't want to waste time calling this if we are already updating.
		if (!network.installing_package) {response = request_blocks_x(onionAddress);}

		System.out.println(response);


		if (response.equals("0") || response.equals("2")) {

			System.out.println("Nothing quit...");
			//The remote server cannot find this block id

		}//***********************
		else if (response.equals("N")) {

			//N update is when database has forked and we have to go back in time and get the old chain.
			System.out.println("Stale");

		}//***************************
		else if (!network.installing_package) {

			//Here we got what we expected and will add to our chain.

			System.out.println("Installing package truex...");

			network.installing_package = true;

			try {

				Object obj = null;

				//This sometimes throws an error if we get a response that is corrupted.
				//This will shutdown the app.
				//java.lang.Error: Error: could not match input
				try {

					obj = parser.parse(response);

				} catch (Error e) {

					System.out.println("Response is unreadable..");

				}//****************

				//Object objxl = parser.parse(response);
				JSONObject jsonObjectxl = (JSONObject) obj;

				System.out.println("Block Package Size: " + jsonObjectxl.size());

				for (int loop = 0; loop < jsonObjectxl.size(); loop++) {//***********************************************************************************

					String bufferp = (String) jsonObjectxl.get(Integer.toString(loop));
					System.out.println("break up " + bufferp);
					System.out.println("loop " + loop);

					if (bufferp != null) {

						jsonSentence = "active";
						String testx = "";

						//If the database is empty we have to update using the server's database.
						if (network.hard_token_count < network.hard_token_limit) {

							//We are just starting the network and need to catch up.
							testx = set_new_block_rebuild(bufferp);

						}//*******************************************************
						else {

							//We have the blockchain and we are just keeping up with the network.
							testx = set_new_block(bufferp);

						}//***

						System.out.println("blocks x testx " + testx);

						if (testx.equals("1")) {

							System.out.println("BLOCK ADDED");

						}//*********************
						else {

							System.out.println("[installing] BLOCK REJECTED");

                            jsonSentence = "error";

							//The blockchain has an error abort.
							jsonObjectxl = null;

							//Block had an error maybe we just need to reload.
							//krypton_database_load loadx = new krypton_database_load();
							//loadx.load();

							break;

						}//**


					}//***********************
					else {

						jsonSentence = "error";

                        //The blockchain has an error abort.
                        jsonObjectxl = null;

						break;

					}//***


				}//for**************************************************************************************************************************************


			} catch (Exception e) {e.printStackTrace();}
			finally {

                //After the loop of adding blocks we do a full reload to make sure everything is correct.
                //krypton_database_load loadx = new krypton_database_load();
                loadx.load();


                //We need the flag set after loading here so that we don't test for integrity.
                //Integrity can only be tested if the chain is up to date. Here it may not be.
				network.installing_package = false;

			}//*********

		}//else**************************************************************************************

		return jsonSentence;

	}//**************************






    //We ask the server for it's latest block, if it's not the same as ours, then we ask for an update starting from the last block we have.
    //This is normal operation.

	public String request_blocks_x(String onionAddress){//*****************************************************************


        System.out.println("Request blocks x");

		String jsonText = "";
		String jsonSentence = "0";


		try{

			JSONObject obj = new JSONObject();
			obj.put("request","blocks_x_update");
			obj.put("block_id", network.last_block_mining_idx);

			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			System.out.println(jsonText);

		} catch (Exception e) {System.out.println("JSON ERROR");}


		try {

			System.out.println("address: " + client_port_connect);
			System.out.println("onionAddress: " + onionAddress);

			System.out.println("socket");

			Socket socket = Utilities.socks4aSocketConnection(onionAddress, hiddenServicePort, local_host_connect, localPort);//127.0.0.1
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


			Object obj = null;

			//This sometimes throws an error if we get a response that is corrupted.
			//This will shutdown the app.
			//java.lang.Error: Error: could not match input
			try {

				obj = parser.parse(modifiedSentence);

			} catch (Error e) {e.printStackTrace();}

			JSONObject jsonObject = (JSONObject) obj;
  
			String response = (String) jsonObject.get("response");
			String message = (String) jsonObject.get("message");
			System.out.println("JSON " + response);

			if (response.equals("1")) {

                Object obj2 = null;

                //This sometimes throws an error if we get a response that is corrupted.
                //This will shutdown the app.
                //java.lang.Error: Error: could not match input
                try {

                    obj2 = parser.parse(message);

                } catch (Error e) {System.out.println("Response is unreadable..");}

				//Object objxl = parser.parse(message);
				JSONObject jsonObjectxl = (JSONObject) obj2;

				System.out.println("Block Package Size: " + jsonObjectxl.size());

				return message;

			}//***********************
			else if (response.equals("0")) {

				System.out.println("BLOCK STALE X");

				//We don't request reverse look ups unless we have a full database.
                //More then likely this is just a short term error.
				if(network.installing_n){System.out.println("Already testing N update...");}
                else if(network.hard_token_count == network.hard_token_limit){blocks_are_stale(onionAddress);}
                else{System.out.println("Blocks are not up to date so we are not going to request N...");}

				return "N";

			}//***************************

		} catch (Exception e) {

			e.printStackTrace(); 
			System.out.println("Cannot find node!"); 
			jsonSentence = "0";

		}//********************


		return jsonSentence;

	}//***********************************************************************************************





    //Blocks are stale so we have to request a negative block update from the new one back until we find one that matches what we have.

    public String blocks_are_stale(String onionAddress){

        //N update is when database has forked and we have to go back in time and get the old chain

		network.installing_n = true;

        String jsonText = "";

        System.out.println("Stale");
        String response2 = request_blocks_n(onionAddress);


        try{

            Object obj = null;

            //This sometimes throws an error if we get a response that is corrupted.
            //This will shutdown the app.
            //java.lang.Error: Error: could not match input
            try {

                obj = parser.parse(response2);

            } catch (Error e) {System.out.println("Response is unreadable..");}

            //Object objxl = parser.parse(response2);
            JSONObject jsonObjectxl = (JSONObject) obj;

            System.out.println("Block Package Size: " + jsonObjectxl.size());

            //If the package is stuck then we have to reset.
            network.installing_package = false;

            for (int loop = 0; loop < jsonObjectxl.size(); loop++) {//***********************************************************************************

                String bufferp = (String) jsonObjectxl.get(Integer.toString(loop));
                System.out.println("break up " + bufferp);
                System.out.println("loop " + loop);

                if (bufferp != null) {

                    jsonText = "active";

                    String testx = set_new_test_block(bufferp);

                    System.out.println("testx.length: " + testx.length());

                    if (testx.equals("1")) {

                        System.out.println("BLOCK ADDED TO STALE DB");

                    }//*******************
                    else {

                        System.out.println("BLOCK REJECTED");
                        break;

                    }//**


                }//***********************
                else{jsonText = "error"; break;}


            }//for**************************************************************************************************************************************

        } catch (Exception e) {e.printStackTrace();}
		finally {

			network.installing_n = false;

		}//*****

        return jsonText;

    }//******************************





    //First we make a request to the server, if our blocks are old then we ask for the new blocks starting from our last block.
    //If the server cannot find those blocks then the chain has forked. We now call a reverse chain look up starting from the server's newest back until we find a link.
    //That fork block is were we can test from. N stands for negative lookup.

	public String request_blocks_n(String onionAddress){//*****************************************************************


        System.out.println("Request blocks n");

		String jsonText = "";
		String jsonSentence = "";


		try {

			JSONObject obj = new JSONObject();
			obj.put("request","blocks_n_update");
			obj.put("block_id", network.last_remote_mining_prev_idx);

			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			System.out.println(jsonText);

		} catch (Exception e) {System.out.println("JSON ERROR");}


		try {

			System.out.println("address: " + client_port_connect);
			System.out.println("onionAddress: " + onionAddress);

			System.out.println("socket");

			Socket socket = Utilities.socks4aSocketConnection(onionAddress, hiddenServicePort, local_host_connect, localPort);//127.0.0.1
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


			Object obj = null;

			//This sometimes throws an error if we get a response that is corrupted.
			//This will shutdown the app.
			//java.lang.Error: Error: could not match input
			try {

				obj = parser.parse(modifiedSentence);

			} catch (Error e) {e.printStackTrace();}

            //Object obj = parser.parse(modifiedSentence);
			JSONObject jsonObject = (JSONObject) obj;
  
			String response = (String) jsonObject.get("response");
			String message = (String) jsonObject.get("message");
			System.out.println("JSON " + response);

			if (response.equals("1")) {

                Object obj2 = null;

                //This sometimes throws an error if we get a response that is corrupted.
                //This will shutdown the app.
                //java.lang.Error: Error: could not match input
                try {

                    obj2 = parser.parse(message);

                } catch (Error e) {System.out.println("Response is unreadable..");}

				//Object objxl = parser.parse(message);
				JSONObject jsonObjectxl = (JSONObject) obj2;

				System.out.println("Block Package Size: " + jsonObjectxl.size());

				return message;

			}//if
			else if (response.equals("0")) {

				System.out.println("BLOCK STALE N");
				//update_stale_api();

                //The server can't find what we are looking for so reset. Maybe we have something stuck.
                network.last_remote_mining_idx = "";
                network.last_remote_mining_prev_idx = "";

                return "N";

			}//**

		} catch (Exception e) {

			e.printStackTrace(); 
			System.out.println("Cannot find node!"); 
			jsonSentence = "0";

		}//*****************


		return jsonSentence;

	}//***********************************************************************************************








    //This will add a new block from the peer into our database (krypton_update_new_block_remote)

    public String set_new_block(String array){

	    System.out.println("Set new block");

        String jsonText = "";

        try{

            Object obj = null;

            //This sometimes throws an error if we get a response that is corrupted.
            //This will shutdown the app.
            //java.lang.Error: Error: could not match input
            try {

                obj = parser.parse(array);

            } catch (Error e) {System.out.println("Response is unreadable..");}

            //Object objx = parser.parse(array);
            JSONObject jsonObjectx = (JSONObject) obj;

            System.out.println(array);

            String update_token[] = new String[network.listing_size];
            String mining_token[] = new String[network.miningx_size];

            for (int loop = 0; loop < network.listing_size; loop++){//************

                update_token[loop] = (String) jsonObjectx.get("l" + Integer.toString(loop));
                //System.out.println("convert " + loop + " " + update_token[loop]);

            }//*******************************************************************

            for (int loop = 0; loop < network.miningx_size; loop++){//************

                mining_token[loop] = (String) jsonObjectx.get("m" + Integer.toString(loop));
                //System.out.println("convert " + mining_token[loop]);

            }//******************************************************************


            //get the last token
            krypton_database_get_token getxt = new krypton_database_get_token();
            String req_id = update_token[0];
            String old_token[] = getxt.getToken(req_id);

            boolean gott = false;
            try {

                System.out.println("GOT TOKENX: " + Integer.parseInt(old_token[0]));
                gott = true;

            } catch (Exception e) {gott = false;}

            System.out.println("gott " + gott);

            //try to add the new token
            krypton_update_new_block_remote remotex = new krypton_update_new_block_remote();
            boolean test = remotex.update(update_token, mining_token, old_token);

            if (test) {

                //Delete from buffer.
                krypton_database_delete_buffer bufferd = new krypton_database_delete_buffer();
                bufferd.deleteID(update_token[0]);

                //Reload system.
                //krypton_database_load loadx = new krypton_database_load();
				loadx.load_lite();//Simple loading to increase speed.

            }//*******

			if(test){jsonText = "1";}
			else{jsonText = "0";}

            //while the network is updating sometimes it takes longer then 300 seconds
            network.no_peers_time0 = 0;
            network.no_peers_time1 = 0;

        } catch (Exception e) {e.printStackTrace(); jsonText = "error";}


        return jsonText;

    }//***************************************






    //When the system first starts we have no blocks so we have to download them from the server.
    //We cannot verify the information yet so we have to trust what we get.

    public String set_new_block_rebuild(String array){

        System.out.println("Set new block rebuild");

        String jsontext = "";

        try{

            Object obj = null;

            //This sometimes throws an error if we get a response that is corrupted.
            //This will shutdown the app.
            //java.lang.Error: Error: could not match input
            try {

                obj = parser.parse(array);

            } catch (Error e) {System.out.println("Response is unreadable..");}

            //Object objx = parser.parse(array);
            JSONObject jsonObjectx = (JSONObject) obj;

            System.out.println(array);

            String update_token[] = new String[network.listing_size];
            String mining_token[] = new String[network.miningx_size];

            for (int loop = 0; loop < network.listing_size; loop++){//************

                update_token[loop] = (String) jsonObjectx.get("l" + Integer.toString(loop));
                //System.out.println("convert " + update_token[loop]);

            }//*******************************************************************

            for (int loop = 0; loop < network.miningx_size; loop++){//************

                mining_token[loop] = (String) jsonObjectx.get("m" + Integer.toString(loop));
                //System.out.println("convert " + mining_token[loop]);

            }//******************************************************************


            //try to add the new token
            krypton_update_rebuild_block_remote remotex = new krypton_update_rebuild_block_remote();
            boolean test = remotex.update(update_token, mining_token);

            //krypton_database_load loadx = new krypton_database_load();
            loadx.load_lite();

            if(test){jsontext = "1";}
            else{jsontext = "0";}

            System.out.println("jsontext " + jsontext);

        } catch (Exception e) {e.printStackTrace(); jsontext = "0";}


        return jsontext;

    }//***************************************





    //This will be called when a new blocks needs to be added to our system.
    //This is when the server has a different blockchain then use.

    public String set_new_test_block(String array){

        System.out.println("Set new test block");

        String jsontext = "";

        try{

            Object obj = null;

            //This sometimes throws an error if we get a response that is corrupted.
            //This will shutdown the app.
            //java.lang.Error: Error: could not match input
            try {

                obj = parser.parse(array);

            } catch (Error e) {System.out.println("Response is unreadable..");}

            //Object objx = parser.parse(array);
            JSONObject jsonObjectx = (JSONObject) obj;

            System.out.println(array);

            String update_token[] = new String[network.listing_size];
            String mining_token[] = new String[network.miningx_size];

            for (int loop = 0; loop < network.listing_size; loop++){//************

                update_token[loop] = (String) jsonObjectx.get("l" + Integer.toString(loop));
                //System.out.println("convert " + update_token[loop]);

            }//*******************************************************************

            for (int loop = 0; loop < network.miningx_size; loop++){//************

                mining_token[loop] = (String) jsonObjectx.get("m" + Integer.toString(loop));
                //System.out.println("convert " + mining_token[loop]);

            }//******************************************************************


            //Get the last token
            krypton_database_get_token getxt = new krypton_database_get_token();
            String req_id = update_token[0];
            String old_token[] = new String[network.listing_size];
            old_token = getxt.getToken(req_id);

            boolean gott = false;
            try {

                System.out.println("GOT TOKENX: " + Integer.parseInt(old_token[0]));
                gott = true;

            } catch (Exception e) {gott = false;}

            System.out.println("gott " + gott);

            //Try to add the new token
            krypton_update_test_block_remote remotex = new krypton_update_test_block_remote();
            boolean test = remotex.update(update_token, mining_token);

            //krypton_database_load loadx = new krypton_database_load();
            loadx.load_lite();

            if(test){jsontext = "1";}
            else{jsontext = "0";}

        } catch (Exception e) {e.printStackTrace(); jsontext = "error";}


        return jsontext;

    }//***************************************







    //Other nodes could have items waiting to be mined we request their items here.

	public String request_unconfirmed_block_update(String unconfirmed_id, String onionAddress){//*****************************************************************


		System.out.println("Request unconfirmed block update...");

		String jsonText = "";
		String jsonSentence = "";


		if(network.last_unconfirmed_idx.equals("")){network.last_unconfirmed_idx = unconfirmed_id;}

		try{

			JSONObject obj = new JSONObject();
			obj.put("request","unconfirmed_block_update");
			obj.put("unconfirmed_id", network.last_unconfirmed_idx);

			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			System.out.println(jsonText);

		} catch (Exception e) {System.out.println("JSON ERROR");}


		try {

			System.out.println("address: " + client_port_connect);
			System.out.println("onionAddress: " + onionAddress);

			System.out.println("socket");

			Socket socket = Utilities.socks4aSocketConnection(onionAddress, hiddenServicePort, local_host_connect, localPort);//127.0.0.1
			//socket.setSoTimeout(20000);

			System.out.println("socketg");

    		OutputStream outputStream = socket.getOutputStream();
    		PrintWriter outx = new PrintWriter(outputStream);
    		outx.print(jsonText + "\r\n\r\n");
    		outx.flush();
    		InputStream inputStream = socket.getInputStream();
    		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
    		BufferedReader in = new BufferedReader(inputStreamReader);

            String modifiedSentence = "";

			System.out.println("socketw");

    		String line;
    		while ((line = in.readLine()) != null) {

    		  System.out.println(line);
		  	  modifiedSentence = line;

    		}//*************************************

			outputStream.close();
   			outx.close();
    		in.close();
    		socket.close();


			Object obj = null;

			//This sometimes throws an error if we get a response that is corrupted.
			//This will shutdown the app.
			//java.lang.Error: Error: could not match input
			try {

				obj = parser.parse(modifiedSentence);

			} catch (Error e) {e.printStackTrace();}

			JSONObject jsonObject = (JSONObject) obj;
  
			String response = (String) jsonObject.get("response");
			String message = (String) jsonObject.get("message");
			System.out.println("JSON " + response);

			if (response.equals("1")) {

				jsonSentence = "Success";

                Object objs = null;

                //This sometimes throws an error if we get a response that is corrupted.
                //This will shutdown the app.
                //java.lang.Error: Error: could not match input
                try {

                    objs = parser.parse(message);

                } catch (Error e) {System.out.println("Response is unreadable..");}

                //Object objs = parser.parse(message);
                JSONObject jsonObjects = (JSONObject) objs;

                System.out.println("SIZE: " + jsonObjects.size());

                //this has to be reversed because the server will give us new items starting from the newest.
				for (int loop1 = (jsonObjects.size() -1); loop1 > -1; loop1--) {//******************

					//String test2 = update_unconfirmed_api(message);

					String splitx = (String) jsonObjects.get(Integer.toString(loop1));
					System.out.println("splitx " + Integer.toString(loop1));
					System.out.println(splitx);

					//no more items left
					if(splitx == null){break;}

                    Object objx = null;

                    //This sometimes throws an error if we get a response that is corrupted.
                    //This will shutdown the app.
                    //java.lang.Error: Error: could not match input
                    try {

                        objx = parser.parse(splitx);

                    } catch (Error e) {System.out.println("Response is unreadable..");}

					//Object objx = parser.parse(splitx);
					JSONObject jsonObjectx = (JSONObject) objx;

					String update_token[] = new String[network.listing_size];

					for (int loop = 0; loop < network.listing_size; loop++) {//************

						update_token[loop] = (String) jsonObjectx.get(Integer.toString(loop));
						//System.out.println("convert " + update_token[loop]);

					}//********************************************************************

                    System.out.println("UPDATE ID: " + update_token[1]);

					//get the last token
					krypton_database_get_token getxt = new krypton_database_get_token();
					String req_id = update_token[0];
					String[] old_token = getxt.getToken(req_id);

					boolean gott = false;
					try {

						System.out.println("GOT TOKENX: " + Integer.parseInt(old_token[0]));
						gott = true;

					} catch (Exception e) {
						gott = false;
					}

					System.out.println("gott " + gott);


					//try to add the new token.
					krypton_update_token_remote remotexu2 = new krypton_update_token_remote();
					boolean test2 = remotexu2.update(update_token, old_token);

					//delete from our buffer.
					krypton_database_delete_buffer bufferd = new krypton_database_delete_buffer();
					bufferd.deleteID(update_token[0]);

					//krypton_database_load loadx = new krypton_database_load();
                    loadx.load();


					if (test2) {
						System.out.println("ADDED!");
					}

				}//for******************************************************************************

			}//***********************
			else{jsonSentence = "error";}


		} catch (Exception e) {

			e.printStackTrace(); 
			System.out.println("Cannot find node!"); 
			jsonSentence = "not active!";

		}

		return jsonSentence;

	}//***********************************************************************************************






	//Other nodes could have onion IDs that we can use to connect to we can update our list here.

	public String update_network_list(String onionAddress){


		//called from request_status cannot test

        System.out.println("Update network list");

		String jsonText = "";
		String jsonSentence = "";


		try{

			JSONObject obj = new JSONObject();
			obj.put("request","get_network");

			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			System.out.println(jsonText);

		} catch (Exception e) {System.out.println("JSON ERROR");}


		try {

			System.out.println("address: " + client_port_connect);
			System.out.println("onionAddress: " + onionAddress);

			System.out.println("socket");

			Socket socket = Utilities.socks4aSocketConnection(onionAddress, hiddenServicePort, local_host_connect, localPort);//127.0.0.1
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


			Object obj = null;

			//This sometimes throws an error if we get a response that is corrupted.
			//This will shutdown the app.
			//java.lang.Error: Error: could not match input
			try {

				obj = parser.parse(modifiedSentence);

			} catch (Error e) {e.printStackTrace();}

			JSONObject jsonObject = (JSONObject) obj;
  
			String response = (String) jsonObject.get("response");
			String message = (String) jsonObject.get("message");
			System.out.println("JSON " + response);

			if (response.equals("1")) {

				jsonSentence = "active";

                Object obj2 = null;

                //This sometimes throws an error if we get a response that is corrupted.
                //This will shutdown the app.
                //java.lang.Error: Error: could not match input
                try {

                    obj2 = parser.parse(message);

                } catch (Error e) {System.out.println("Response is unreadable..");}

				//Object obj2 = parser.parse(message);
				//JSONObject jsonObject2 = (JSONObject) obj2;
				JSONArray array = (JSONArray) obj2;

  				System.out.println(array.size());


				for(int loop = 0; loop < array.size(); loop++){

        			System.out.println("new nodes " + array.get(loop));
        			krypton_database_node add_node = new krypton_database_node();
                    add_node.addNode(array.get(loop).toString());

				}//********************************************

				krypton_database_load_network nodesx = new krypton_database_load_network();

			}//***********************
			else{jsonSentence = "error";}

		


		} catch (Exception e) {

			e.printStackTrace(); 
			System.out.println("Cannot find node!"); 
			jsonSentence = "not active!";

		}

		
		return jsonSentence;

	}//*******************************





	//Test if the internet is working. Could give away our position...
    //So it's not used anymore.

	public void test_for_internet(){


		System.out.println("Testing");


		network.internet_access = false;//network.internet_access = 0;

		String cx0i = "";

		// Construct data
		try {


    		//String data = URLEncoder.encode("item_id", "UTF-8") + "=" + URLEncoder.encode(nm.carbon[0][part_number_xx][ix1], "UTF-8");

    		// Send data
			//URL url = new URL("http://www.yahoo.com");
    		//URL url = new URL("http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22nome%2C%20ak%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys");
    		URL url = new URL("http://www.bing.com/");
   			URLConnection conn = url.openConnection();
   			conn.setDoOutput(true);
    		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
    		//wr.write(data);
    		wr.flush();

    		// Get the response
    		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    		String line;
    		while ((line = rd.readLine()) != null){

       			cx0i = line;

				//System.out.println(cx0i);
				if(cx0i.length() > 0){network.internet_access = false;}//network.internet_access = 1;
				else{network.internet_access = false;}//network.internet_access = 0; network.status9.setText("No internet test!"); network.status9.setIcon(network.imx0);


    		}//************************************
    		wr.close();
   			rd.close();
    		//conn.close();

		} catch (Exception e) {

			e.printStackTrace();
			network.internet_access = false;//network.internet_access = 0;
			System.out.println("URL falure.");

		}//*****************


	}//updates******************






    //This was an old test system for TOR

    public class RetrieveFeedTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog

        }

        @Override
        protected Void doInBackground(Void... params) {

            System.out.println("Run TOR...");

            try {

                String jsonText = "";

                JSONObject obj = new JSONObject();
                obj.put("request","status");

                StringWriter outs = new StringWriter();
                obj.writeJSONString(outs);
                jsonText = outs.toString();
                System.out.println(jsonText);


                String fileStorageLocation = "torfiles";
                onionProxyManager = new AndroidOnionProxyManager(MainActivity.context2, fileStorageLocation);
                int totalSecondsPerTorStartup = 4 * 60;
                int totalTriesPerTorStartup = 5;

                // Start the Tor Onion Proxy
                if (onionProxyManager.startWithRepeat(totalSecondsPerTorStartup, totalTriesPerTorStartup) == false) {
                    Log.e("TorTest", "Couldn't start Tor!");
                }//**************************************************************************************************

                // Start a hidden service listener
                hiddenServicePort = 80;

                network.onionAddress = "kmvgael2yyyb2zk2.onion";
                localPort = onionProxyManager.getIPv4LocalHostSocksPort();//localPort = 9343;


                Socket socket = Utilities.socks4aSocketConnection(network.onionAddress, hiddenServicePort, "127.0.0.1", localPort);


                OutputStream outputStream = socket.getOutputStream();
                PrintWriter outx = new PrintWriter(outputStream);
                outx.print(jsonText + "\r\n\r\n");
                outx.flush();

                System.out.println("socketw");

                InputStream inputStream = socket.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader in = new BufferedReader(inputStreamReader);

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


            } catch (Exception e) {e.printStackTrace();}

            return null;
        }


    }//RetrieveFeedTask





}//last
