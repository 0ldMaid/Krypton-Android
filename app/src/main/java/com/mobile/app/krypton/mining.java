package com.mobile.app.krypton;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;
import java.util.Timer;
import java.util.TimerTask;
import java.security.MessageDigest;

import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import java.nio.ByteBuffer;


public class mining{

	Timer xtimerx;//class loop.

	JSONParser parser = new JSONParser();

	final protected static char[] hexArray = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

	static boolean mining_stop = false;//Turn off and restart...

	static long noosex = 0;//Nonce.

	int package_block_stage = 0;//1 - 10
	int res = 0;//If the mining hash meets our difficulty.

	byte[] sha256_1;//Mining
	ByteBuffer buffer;//Mining

	BigInteger result;
	BigInteger package_difficultyx = new BigInteger("0");

	String encode = "";
	String encode_date = "";
	String new_block_id = "";
	String new_block_hash = "";
	String old_block_hash = "";
	String old_block_mining_hash = "";
	String mining_noose = "";
	String block_package = "";
	String tokenx[] = new String[network.listing_size];
	String package_listings[][] = new String[network.listing_size][network.block_compress_size];
	String package_miningxs[][] = new String[network.miningx_size][network.block_compress_size];

	static String mx_last_block_mining_idx = "";//Local version of last_block_mining_idx.
	static String mx_last_block_idx = "";//Local version of last_block_idx.

	static boolean mining1 = false;//User choice if we mine or not.
	static boolean mining2 = false;//Their server and us have the same block.
	static boolean mining3 = false;//We are connected to something.
	static boolean mining4 = false;//If the blockchain is correct.

	boolean mining_old_chain = false;//Move the chain along....
	boolean build_package = false;//Should we mind for 1 block or a package
	boolean old_chain_package = false;//




	//Here we do the mining. For individual blocks and also packages. Because no coins are given out for mining it's expected that each user will be mining. No centralization.
    //There are two types of mining:
    //We can mine a new block which is an update that a user has made.
    //And we can mine an old block from the oldest part of the chain to move it along. Like logs carrying a large block we move the old one to the front and reuse it.
    //This way the chain never gets longer.
    //We load new items from mining_new_task
    //And we move the chain along using old blocks with mining_new_task_c

	public mining(){

		while(!network.tor_active){//network.tor_active == 0

			System.out.println("Wait for TOR to mine....");
			try{Thread.sleep(1000);} catch (InterruptedException e){}

		}//*********************************

		xtimerx = new Timer();
		xtimerx.schedule(new RemindTask_mining(), 0);

		network.reset_mining_hash = true;

	}//makek****






	class RemindTask_mining extends TimerTask{
	Runtime rxrunti = Runtime.getRuntime();

	public void run(){//************************************************************************************

		System.out.println("Start Mining");

		//This always stays on
		while(true){


            //User has set mining off!
			while(mining1){


				//Build_package = false;
				mining_stop = false;

				int testm = 0;

                if(!network.database_in_use){testm++;}
                if(mining1){testm++;}
                if(mining2){testm++;}
                if(mining3){testm++;}
                if(mining4){testm++;}


                System.out.println("db in use " + network.database_in_use);
                System.out.println("mining1   " + mining1);
                System.out.println("mining2   " + mining2);
                System.out.println("mining3   " + mining3);
                System.out.println("mining4   " + mining4);


                if(testm == 5){

					System.out.println("network.database_in_use " + network.database_in_use);
					System.out.println("network.xmining         " + network.xmining);

					if(!network.database_in_use && network.xmining){



                        //For first build network.new_database_start == 1
                        if(network.new_database_start){mining_old_chain = false;}
                        if(network.database_unconfirmed_total == 0){mining_old_chain = true;}
                        if(network.database_unconfirmed_total == 0){mining_old_chain = true;}

                        System.out.println("mining_old_chain " + mining_old_chain);


                        //This is the switch that mines one block new and one block from the end of the chain.
                        //To make sure the "worm" moves along.
                        if(!mining_old_chain){


                            if(network.database_unconfirmed_total >= network.block_compress_size){//

                                System.out.println("Package mining");

                                //Mine for package block only do this if we need a new hash.
								// For example we got a new block or this one is too old.
                                if(network.reset_mining_hash){

                                	//We want to mine for unconfirmed items.
                                    old_chain_package = false;

                                    //Reset the block stage.
                                    package_block_stage = 0;

                                    //Get the info about our items.
                                    build_package_start();

                                    //Reset the mining hash.
                                    network.reset_mining_hash = false;

                                }//***************************

                            }//*********************************************************************
                            else{


                                System.out.println("Mining 1 block");

                                //Mine for 1 block only do this if we need a new hash.
								// For example we got a new block or this one is too old.
                                if(network.reset_mining_hash){

                                	//Get a single item to mine for.
                                    new_hash();

                                    //get a new hash.
                                    network.reset_mining_hash = false;

                                }//***************************


                            }//**


                        }//*********************
						else{


                            System.out.println("Mining package block old");

                            //Mine for 1 block only do this if we need a new hash.
							//For example we got a new block or this one is too old.
                            if(network.reset_mining_hash){

                            	//We want to make a packing using the old items from our database.
                                old_chain_package = true;

                                //Set the package block stage to 0;
                                package_block_stage = 0;

                                //We load the package items.
                                build_move_hash_package();

                                //Get a new mining has.
                                network.reset_mining_hash = false;

                            }//***************************


                        }//**


					}//******************************************************

				}//if******************
				else if(!mining4){

                    //Mining 4 can get stuck if it fails while the system is loading a new package.
                	krypton_database_load loadx = new krypton_database_load();
                	loadx.load();

				}//****************


				System.out.println("MINE >>> " + mining_old_chain);

				//System.out.println("network.xmining " + network.xmining);
				//System.out.println("network.database_listings_total " + network.database_listings_total);

				//Mining test network.xmining == 1
				while(network.xmining && network.hard_token_count == network.hard_token_limit){

					//Someone already found this block or user abort.
					if(mining_stop){System.out.println("Break mining stop 1..."); break;}

                    //Testings for something to mine for.
                    if(!mining1){System.out.println("Break mining stop 1 mining 1..."); break;}

					//Testings for something to mine for.
					if(!mining2){System.out.println("Break mining stop 2 mining 2..."); break;}

					//Testings for something to mine for.
					if(!mining3){System.out.println("Break mining stop 3 mining 3..."); break;}

                    //If the block is ready to send we don't need to mine more.
                    if(!mining4){System.out.println("Break mining stop 4 mining 4..."); break;}

                    //Mining block is ready we don't need to mine more.
                    if(network.mining_block_ready || network.mining_package_ready){System.out.println("Break mining block is ready to send..."); break;}

					//Testings for internet network.new_database_start == 0 network.tor_active == 0
					if(!network.tor_active && !network.new_database_start){System.out.println("Mining net_tor 0..."); break;}

					//Testings for internet network.new_database_start == 0
					if(network.active_peers == 0 && !network.new_database_start){System.out.println("Mining net_client 0..."); break;}

					//Testings for status.
					if(!network.blocks_uptodate){System.out.println("Mining old blocks..."); break;}

					if(network.installing_package){System.out.println("Break installing package..."); break;}

					//Turn on the icon.
					network.mining_status = true;

					//Reset integer so there are no errors.
					//This is a big integer comparison so it can be -1 0 or 1 so we set to 5 so we don't get any confusion.
					//We just can't set to -1 0 or 1.
					res = 5;

					//Package mining or block mining.
					if(!build_package){mine();}
					else{mine_package();}

					//Mining speed this should be set to be used on phones.
					try{Thread.sleep(network.mining_speed);} catch(InterruptedException e){e.printStackTrace();}


				}//while**********************************************************************************


				network.mining_status = false;

				try{Thread.sleep(10000);} catch (InterruptedException e){}

			}//while*******

			try{Thread.sleep(10000);} catch (InterruptedException e){}

		}//while*******

	}//runx*************************************************************************************************
	}//remindtask







	public void build_package_start(){

		System.out.println("Package setup...");


        build_package = true;

		//Make sure everything is new.
        krypton_database_load loadx = new krypton_database_load();
        loadx.load();

		noosex = 0;

    	krypton_database_get_unconfirmed_package getp = new krypton_database_get_unconfirmed_package();
    	package_listings = getp.getMiningPackage(network.block_compress_size);

        new_block_id = package_listings[0][0];
        new_block_hash = package_listings[1][0];
		old_block_hash = network.last_block_idx;

    	old_block_mining_hash = mx_last_block_mining_idx;

    	build_package_2();

        mining_old_chain = true;


	}//*******************************






	public void build_package_2(){

	    System.out.println("Build package stage 2");


		LinkedList<String> list = new LinkedList<String>();

    	for (int loop = package_block_stage; loop < package_listings[0].length; loop++) {//*********

    		list.add(package_listings[1][loop]);

    	}//*****************************************************************************************

    	block_package = JSONValue.toJSONString(list);

		encode_date = Long.toString(System.currentTimeMillis());
		
		System.out.println("get id " + package_listings[0][package_block_stage]);

        new_block_id = package_listings[0][package_block_stage];
        new_block_hash = package_listings[1][package_block_stage];

		if (package_block_stage > 0) {

			//If this is the 2nd and up blocks in the package then we use the previous hash.
			old_block_hash = package_listings[1][package_block_stage -1];

		}//***************************
		else {

			//If this is the first block then we have to use the system hash.
			old_block_hash = network.last_block_idx;

		}//**

		System.out.println("encode_date                   " + encode_date);
		System.out.println("new_block_id                  " + new_block_id);
		System.out.println("new_block_hash                " + new_block_hash);
		System.out.println("old_block_hash                " + old_block_hash);
		System.out.println("old_block_mining_hash         " + old_block_mining_hash);
		System.out.println("mx_last_block_mining_idx      " + mx_last_block_mining_idx);


	}//***************************








	public void new_hash(){

	    System.out.println("<New Hash> new content");


	    //Reset,
		noosex = 0;
		mining2 = false;
		encode_date = Long.toString(System.currentTimeMillis());

		new_block_id = "";
		new_block_hash = "";
		old_block_hash = "";
		old_block_mining_hash = "";

		int tests1 = 0;

		//Make sure all our info is correct.
        krypton_database_load loadx = new krypton_database_load();
        loadx.load();

        //Get a new listing to mine
		mining_new_task new_x_task = new mining_new_task();
		tokenx = new_x_task.new_task();

        new_block_id = tokenx[0];
        new_block_hash = tokenx[1];
		old_block_hash = network.last_block_idx;


		try{

			tests1 = Integer.parseInt(new_block_id);
			//mining_old_chain = false;

		}catch(Exception e){tests1 = 0; mining_old_chain = true;}



		//For first build network.new_database_start == 1
		if(network.new_database_start){mining_old_chain = false;}
		if(network.database_unconfirmed_total == 0){mining_old_chain = true;}

		System.out.println("tests1 " + tests1);
		System.out.println("mining_old_chain " + mining_old_chain);



		if(tests1 > 0){

			System.out.println("Standard mining");
		
			if(!network.new_database_start){

				System.out.println("");
				krypton_database_verify_id verifyx = new krypton_database_verify_id(new_block_id);
				System.out.println("");
				
			}//if***************************

			System.out.println("tokenx IDX1 " + tokenx[0]);

			//Update block to the new chain
			old_block_mining_hash = mx_last_block_mining_idx;

			//For testing.
			//krypton_database_print_blocks print = new krypton_database_print_blocks();

			block_package = "";

			System.out.println("encode_date                   " + encode_date);
			System.out.println("new_block_id                  " + new_block_id);
			System.out.println("new_block_hash                " + new_block_hash);
			System.out.println("old_block_hash                " + old_block_hash);
			System.out.println("old_block_mining_hash         " + old_block_mining_hash);
			System.out.println("mx_last_block_mining_idx      " + mx_last_block_mining_idx);

            build_package = false;
			mining_old_chain = true;
			mining2 = true;

		}//if*****************


	}//********************






    public void build_move_hash(){

        System.out.println("<New Hash> old chain");


        //Reset
        noosex = 0;
        mining2 = false;
        encode_date = Long.toString(System.currentTimeMillis());

        new_block_id = ("");
        new_block_hash = ("");
        old_block_mining_hash = ("");


        //Make sure all our info is correct.
        krypton_database_load loadx = new krypton_database_load();
        loadx.load();

        //For first build network.new_database_start == 1
        if(network.new_database_start){mining_old_chain = false;}
        if(network.database_unconfirmed_total == 0){mining_old_chain = true;}

        System.out.println("mining_old_chain " + mining_old_chain);


        //If nothing to work on or we need to move the chain along.
        //This used to be single blocks but it has now been converted to packages.
        //This well keep the chain moving along quicker.
        System.out.println("Moving chain");

        mining_new_task_c new_x_task_c = new mining_new_task_c();
        tokenx = new_x_task_c.new_task_c();

        new_block_id = tokenx[0];
        new_block_hash = tokenx[1];

        System.out.println("tokenx IDX2 " + tokenx[0]);

        //Update block to the new chain
        old_block_mining_hash = mx_last_block_mining_idx;

        //krypton_database_print_blocks print = new krypton_database_print_blocks();

        block_package = "";

        System.out.println("encode_date              " + encode_date);
        System.out.println("new_block_id             " + new_block_id);
        System.out.println("new_block_hash           " + new_block_hash);
		System.out.println("old_block_hash                " + old_block_hash);
        System.out.println("old_block_mining_hash    " + old_block_mining_hash);
        System.out.println("mx_last_block_mining_idx " + mx_last_block_mining_idx);

        build_package = false;
        mining_old_chain = false;
        mining2 = true;


    }//***************************







    public void build_move_hash_package(){

        System.out.println("Package setup...");


        build_package = true;

        //make sure everything is new.
        krypton_database_load loadx = new krypton_database_load();
        loadx.load();

        noosex = 0;

        mining_new_task_c_package new_x_task_c = new mining_new_task_c_package();
        package_listings = new_x_task_c.new_task_c_package2();

        new_block_id = package_listings[0][0];
        new_block_hash = package_listings[1][0];

        old_block_mining_hash = mx_last_block_mining_idx;

        build_package_2();

        mining_old_chain = false;


    }//***************************







	public void mine(){


		encode_date = Long.toString(System.currentTimeMillis());

		encode = encode_date + old_block_mining_hash + new_block_hash + Long.toString(noosex) + block_package;

		mining_noose = Long.toString(noosex);

		
		try{


			sha256_1 = MessageDigest.getInstance("SHA-256").digest(encode.getBytes());
			//System.out.println("SHA1 " + bytesToHex(sha256_1));

			result = new BigInteger(1, sha256_1);

			//System.out.println("value " + value);
			//System.out.println("result1 " + result);
			//System.out.println("result2 " + network.difficultyx);
			//System.out.println("SHA1 " + bytesToHex(sha256_1));

			encode = bytesToHex(sha256_1);

			res = result.compareTo(network.difficultyx);

			//if(result < network.difficultyx && result > 0){
			if(res == -1){

				try{update();}
				catch(Exception e){mining_stop = true;}

			}//********************************************
			else{noosex++;}


		}catch(Exception e){e.printStackTrace(); mining_stop = true;}


	}//****************








	public void mine_package(){


		encode_date = Long.toString(System.currentTimeMillis());

		encode = encode_date + old_block_mining_hash + new_block_hash + Long.toString(noosex) + block_package;

		mining_noose = Long.toString(noosex);

		
		try{


			sha256_1 = MessageDigest.getInstance("SHA-256").digest(encode.getBytes());
			//System.out.println("SHA1 " + bytesToHex(sha256_1));

			result = new BigInteger(1, sha256_1);

			//System.out.println("value " + value);
			//System.out.println("result " + result);
			//System.out.println("result2 " + result2);
			//System.out.println("SHA1 " + bytesToHex(sha256_1));

			encode = bytesToHex(sha256_1);

			//if we are building a package then the other items do not need a hard difficulty. 
			if(package_block_stage == 0){package_difficultyx = network.difficultyx;}
			else{package_difficultyx = network.difficultyx_package;}

			res = result.compareTo(package_difficultyx);

			//if(result < package_difficultyx && result > 0){
			if(res == -1){

                //We have to sign the block we found so that network knows it came from someone with tokens.

                byte[] sha256_1x = MessageDigest.getInstance("SHA-256").digest(encode.getBytes());
                System.out.println(org.spongycastle.util.encoders.Base64.toBase64String(sha256_1x));

                byte[] message = org.spongycastle.util.encoders.Base64.toBase64String(sha256_1x).getBytes("UTF8");

                //Build private key and test
                byte[] clear = org.spongycastle.util.encoders.Base64.decode(network.prv_key_id);//network.settingsx[4]
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
                KeyFactory fact = KeyFactory.getInstance("RSA");
                PrivateKey priv = fact.generatePrivate(keySpec);
                Arrays.fill(clear, (byte) 0);

                Signature sigx = Signature.getInstance("SHA1WithRSA");//MD5WithRSA
                sigx.initSign(priv);
                sigx.update(message);
                byte[] signatureBytesx = sigx.sign();
                //System.out.println("Public: " + Base64.toBase64String(pub.getEncoded()));
                System.out.println("Signature: " + org.spongycastle.util.encoders.Base64.toBase64String(signatureBytesx));

                String signxx = org.spongycastle.util.encoders.Base64.toBase64String(signatureBytesx);

                //We have to sign the block we found so that network knows it came from someone with tokens.
                krypton_database_get_token_mining_id miningxid = new krypton_database_get_token_mining_id();

                //Add all the info to the block package.
				package_miningxs[0][package_block_stage] = package_listings[0][package_block_stage];
            	package_miningxs[1][package_block_stage] = encode_date;
            	package_miningxs[2][package_block_stage] = package_difficultyx.toString();
            	package_miningxs[3][package_block_stage] = mining_noose;
            	package_miningxs[4][package_block_stage] = old_block_mining_hash;
            	package_miningxs[5][package_block_stage] = encode;
            	package_miningxs[6][package_block_stage] = old_block_hash;
            	package_miningxs[7][package_block_stage] = new_block_hash;
            	package_miningxs[8][package_block_stage] = package_listings[2][package_block_stage];
            	package_miningxs[9][package_block_stage] = block_package;//+ All the other blocks in the package.
                package_miningxs[10][package_block_stage] = miningxid.getMiningID();//The first block we own.
                package_miningxs[11][package_block_stage] = signxx;//Our sig of the new hash.

				//When the next block is made we need to use this new hash as that one's old hash.
            	old_block_mining_hash = encode;

            	//Increase the package increment.
				package_block_stage++;

				//Reset the nunce.
				noosex = 0;

				//If we are at the last block in the package then we send it off to the network.
				if(package_block_stage >= package_listings[0].length){

					//Send the package to the network.
					krypton_net_client.send_new_block_package(package_miningxs, package_listings);

					//Get a new package.
                    network.reset_mining_hash = true;

					//Someone has found the block, go to the next task.
					mining_stop = true;

					try{Thread.sleep(30000);} catch (InterruptedException e){}

				}//***************************************************
				else{build_package_2();}


			}//********************************************
			else{noosex++;}


		}catch(Exception e){e.printStackTrace(); network.reset_mining_hash = true; mining_stop = true;}


	}//****************









	public void update(){

		Long thisTick = System.currentTimeMillis();


		//what are we sending?
		System.out.println("encode_date           " + encode_date);
		System.out.println("old_block_mining_hash " + old_block_mining_hash);
		System.out.println("new_block_hash        " + new_block_hash);
		System.out.println("noosex                " + Long.toString(noosex));


		System.out.println("new block! " + encode);
		network.programst = "New block! (" + tokenx[0] + ")";

		try{

			if(encode.length() > 0 && new_block_id.length() > 0){

			    //we have to sign the block we found so that network knows it came from someone with tokens.

                byte[] sha256_1x = MessageDigest.getInstance("SHA-256").digest(encode.getBytes());
                System.out.println(org.spongycastle.util.encoders.Base64.toBase64String(sha256_1x));

                byte[] message = org.spongycastle.util.encoders.Base64.toBase64String(sha256_1x).getBytes("UTF8");

                //build private key and test
                byte[] clear = org.spongycastle.util.encoders.Base64.decode(network.prv_key_id);//network.settingsx[4]
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
                KeyFactory fact = KeyFactory.getInstance("RSA");
                PrivateKey priv = fact.generatePrivate(keySpec);
                Arrays.fill(clear, (byte) 0);

                Signature sigx = Signature.getInstance("SHA1WithRSA");//MD5WithRSA
                sigx.initSign(priv);
                sigx.update(message);
                byte[] signatureBytesx = sigx.sign();
                //System.out.println("Public: " + Base64.toBase64String(pub.getEncoded()));
                System.out.println("Singature: " + org.spongycastle.util.encoders.Base64.toBase64String(signatureBytesx));

                String signxx = org.spongycastle.util.encoders.Base64.toBase64String(signatureBytesx);

                //we have to sign the block we found so that network knows it came from someone with tokens.

                krypton_database_get_token_mining_id miningxid = new krypton_database_get_token_mining_id();

            	String[] move_item = tokenx;
            	String[] mining_item = new String[network.miningx_size];

				mining_item[0] = move_item[0];
            	mining_item[1] = encode_date;
            	mining_item[2] = network.difficultyx.toString();
            	mining_item[3] = mining_noose;
            	mining_item[4] = old_block_mining_hash;
            	mining_item[5] = encode;
            	mining_item[6] = old_block_hash;
            	mining_item[7] = new_block_hash;
            	mining_item[8] = move_item[2];
            	mining_item[9] = block_package;//+ all the other blocks in the package
                mining_item[10] = miningxid.getMiningID();//the first block we own
                mining_item[11] = signxx;//our sig of the new hash


                if(!network.new_database_start){//network.new_database_start == 0

					String testerx = "error";

					for (int loop = 0; loop < network.listing_size; loop++){//************

						try{ if(move_item[loop].equals("0")){} }
						catch(Exception e){testerx = "1";}

					}//*******************************************************************


					int testxp = 0;
					while(!testerx.equals("1")){

						mining_stop = true;//someone has found the block, go to the next task.

						System.out.println("Mining send new block update >>>>");

						String testg = krypton_net_client.send_new_block_update(mining_item, move_item);
						System.out.println("testg " + testg);

						if(testg.equals("1") || testg.equals("0")){System.out.println("BREAK"); break;}
						if(testxp > 3){break;}

						try{Thread.sleep(30000);} catch (InterruptedException e){}

						testxp++;

					}//while

				}//*********************************
				else{

					System.out.println("UPDATE TT");

					System.out.println("move_item[0] " + move_item[0]);

					krypton_database_get_token getxt = new krypton_database_get_token();
					String req_id = move_item[0];
					String[] old_token = getxt.getToken(req_id);

					System.out.println("old_token " + old_token[0]);

                    krypton_update_new_block_remote remotexu2 = new krypton_update_new_block_remote();
                    boolean test2 = remotexu2.update(move_item, mining_item, old_token);

					noosex = 0;
					new_hash();
					System.out.println("UPDATE TTD");

				}//**


			}//if*******************************************************
			else{System.out.println("Add block ERROR 0003"); network.reset_mining_hash = true; mining_stop = true;}

		}catch(Exception e){e.printStackTrace(); network.reset_mining_hash = true; mining_stop = true;}

		network.dbxmine_longstamp = System.currentTimeMillis() - thisTick;

	}//******************






	public static String bytesToHex(byte[] bytes) {

		char[] hexChars = new char[bytes.length * 2];

		for ( int j = 0; j < bytes.length; j++ ) {

			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];

		}//***************************************

		return new String(hexChars);

	}//********************************************






	//start the program.
    public static void main(String[] args) {

		mining gold = new mining();

    }//main







}//last