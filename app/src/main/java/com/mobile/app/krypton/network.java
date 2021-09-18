
//Krytpon

//This is the main program starting point "network" where everything happens from. The android app starts from MainActivity. But that is just the interface.
//Krypton is a peer to peer network that holds Listings rather then coins. But you can think of them as coins or tokens.
//The point of the system is to create a decentralized CraigsList that cannot be censored or taken down.
//Each token or "coin" is really a set of information like price and title that can be used to create a listing like on ebay or craigslist.
//Each token can hold 69 fields used to create a listing. These fields then can be displayed in the app or online. For anyone to see. There is no checkout system it is only a listing.
//Users will have to decided for themselves how best to go about transactions. I think it will be necessary for sellers to build trust in whatever way their buyers trust most.

//To run on mobile phones two main changes had to be made to the bitcoin system. One is a different type of blockchain and the other is the use of Tor.
//Tor is necessary for phones to communicate between each other. Since setting up a server on a phone is pretty much impossible for most users.
//Using Tor allows for all phones to become clients and servers.
//In addition to communication, Tor allows for two other main benefits. First, it hides users from some threats and is a good fit for a system like this.
//But also Tor allows each phone to become a server that can be accessed from regular Tor browsers all over the world.
//So having the app on your phone is not necessary, just a Tor browser can suffice. However, if you want to update tokens or transfer them you would need the app.
//The server in this app can distinguish between requests from other nodes and browsers by the header field. If there is no header it's a node, if there is a header it's a browser.
//If you connect as a node it's JSON if you connect as a browser it's HTML.

//The blockchain has been converted into more of a "worm chain" it moves along though time but it doesn't get any longer.
//Each block from the back is moved to the front, moving it along kind of like how they used to move heavy blocks ages ago with logs.
//The block moves along the logs and when it gets to far the log from the back is moved to the front. That's pretty much how this works.
//The blockchain will complete it's run about every month.
//At that time if any node hasn't updated they would be left out and they wouldn't be able to verity the blocks on their own any longer.
//That's why Bitcoin doesn't do this, it needs more security. But in this case the database is just information and thus not as important as monetary transactions.
//I think this is a worth wile trade off for the benefits it brings to a system like this.
//But if in the future storage isn't an issue with phones it would be possible to save all the transactions like Bitcoin and have the same security. By just a few changes to the code.

//When this class starts it will start "network" which will then start the "client", the "server" and the "mining class" all named the same.
//The rest of the classes are mostly database management services used to move blocks around and confirm them.

//This system uses RSA 2048 bit keys unlike Bitcoin's elliptic curve keys. I think they are easier to use and are more web friendly, no reason they couldn't be the same.
//There are 25,000 tokens in the database for people to use. Originally it was 10,000 but later changed to 25k. I don't see any need to have millions of tokens.
//If people need more tokens new versions or "coins" of the system can be made just like Altcoins. That's more secure anyway then having just one network.

//Mining is also not the same as Bitcoin to mine a user has to have at least one token. Because no tokens are given out though mining I see no need to allow outside users to mine.
//The only people that would want to mine are people that own Listings and want to help the network. Therefore I think this is a good way to protect the network from attack.
//People could still attack the network by buying tokens and dumping a lot of hash power from there.
//But we could update the network to require more coins for miners or perhaps block those users.
//There isn't really any good answer to mining distribution but I think this is a good concept for a system like this that is mobile.

//You can get a small icon to display in search results by adding one of these to your "search 1" field
//DISPLAY_BITCOIN
//DISPLAY_COINS
//DISPLAY_COMPUTER
//DISPLAY_CONDO
//DISPLAY_CONDOM
//DISPLAY_DOWNLOAD
//DISPLAY_ELECTRONICS
//DISPLAY_ENGINE
//DISPLAY_GAS
//DISPLAY_GEARS
//DISPLAY_GIFT
//DISPLAY_GUN
//DISPLAY_JEWEL
//DISPLAY_LAW
//DISPLAY_MAP
//DISPLAY_MEDAL
//DISPLAY_OK
//DISPLAY_PANTS
//DISPLAY_PAPER
//DISPLAY_PILL
//DISPLAY_POISON
//DISPLAY_RING
//DISPLAY_SCIFI
//DISPLAY_SUPERMAN
//DISPLAY_TICKET
//DISPLAY_XXX


package com.mobile.app.krypton;

import net.glxn.qrgen.android.QRCode;
import net.glxn.qrgen.core.image.ImageType;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import org.json.simple.JSONObject;
import java.util.ArrayList;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;


public class network{

	Timer xtimerx;//Class loop.

	static krypton_database_driver systemx;

	static boolean new_database_start = false;//Build a new listings database.
	static boolean open_network = false;//Allow new nodes without confirmation.
	static boolean database_active = false;//If the database started.
	static boolean program_ready = false;//Program is done loading.
	static boolean blocks_uptodate = false;//If our blocks are the same as teh servers.
	static boolean reset_mining_hash = false;//Tells the miner to get a new block.
	static boolean updating = false;//If the programing is updating the database.
	//static boolean settings_loaded = false;//If settings are ready to read.
	static boolean database_loaded = false;//If the database is ready to use.
	static boolean peersx0 = false;//If the peer is connected.
	static boolean peersx1 = false;//If the peer is connected.
	static boolean peersx2 = false;//If the peer is connected.
	static boolean peersx3 = false;//If the peer is connected.
	static boolean full_node = false;//If the android app is a full node or just a lite client.
	static boolean mining_block_ready = false;//If the program has a block that is ready to send.
	static boolean mining_package_ready = false;//If the program has a package ready to be sent.
	static boolean tor_active = false;//Is TOR working?
	static boolean tor_starting = false;//Is TOR working?
	static boolean internet_access = false;//Is the standard internet on?
	static boolean server = false;//Is the server on or off.
	static boolean start_server = true;//Args if the server should be started or not.
	static boolean xmining = false;//Is mining on or off the items will still be loaded every 10 seconds.
	static boolean mining_status = false;//Is the program mining or not.
	static boolean database_in_use = false;//Already working.
	static boolean installing_package = false;//We are installing a package don't try to test it.
	static boolean installing_n = false;//We are testing an reverse look up because our database is wrong.
	static boolean add_node_onion = false;//Add our onion address to the blockchain so others can connect to our server.
	static boolean use_one_peer = false;//Only use the user peer not the system peer list.
	static boolean use_old_key = false;//Old base 58 key with no K in front.
	static boolean reset_db = false;//We are resetting the database don't load it during this time.
	static boolean play_store_version = false;//Google doesn't allow mining so we disable it for the play store version from here.

	static List<String> network_list = new ArrayList<String>();//The list of your peers.
	static List<String> my_listings = new ArrayList<String>();//Get my listing ids.

	static String programst = "Loading...";
	//static String settingsx[] = new String[23];//Settings database.
	static String pub_key_id = "";//Public RSA key.
	static String prv_key_id = "";//Private RSA key.
	static String item_layout[];//A map of the item database.
	static String mining_layout[];//A map of the mining database.
	static String block_difficulty_listx[];//Get a few blocks to test.
	static String block_date_listx[];//Get a few blocks to test.
	static String block_hash_listx[];//Get a list of the number of hashes it takes to make a block.
	static String block_noose_listx[];//Get a list of the number of hashes it takes to make a block.
	static String last_block_ql[];//This could speed up sending info about the new block to our peers.
	static String last_minex_ql[];//Not used yet.
	static String html_block_ql[];//Quick load for html pages.

	static String onionAddress;//The onion address is loaded from the settings or sometimes from the database. It is related to user preference that's why it's here.
	static String base58_id = "public key";//Your base 58 key.
	static String versionx = "1.2.8";//Code version. This should be changed in the manifest as well.
	static String idx = "Krypton";//Program version name.
	static String coin_name = "KRC";//Abv of the coin name for display.
	static String version_description = "Krypton P2P Market";//Program version name.
	static String last_block_timestamp = "";//System time of the last block.
	static String last_block_id = "";//Integer.
	static String last_block_idx = "";//Hash.
	static String last_block_mining_idx = "";//Mining Hash.
	static String prev_block_mining_idx = "";//Mining Hash.
	static String last_remote_mining_idx = "";//This is a copy of the remote peer's mining ID.
	static String last_remote_mining_prev_idx = "";//^^ PREV.
	static String last_unconfirmed_idx = "";//Hash.
	static String last_unconfirmed_id = "";//ID 100000.
	static String last_package_x = "";//Last package block ID.
	static String buffered_mining_block = "";//The mining block that is ready to send.
	static String buffered_listing_block = "";//The listing block that is ready to send.
	static String buffered_package_block = "";//The mining package that is ready to send.
	static String peerid0 = "";//onion address of peer1.
	static String peerid1 = "";//onion address of peer2.
	static String peerid2 = "";//onion address of peer3.
	static String peerid3 = "";//onion address of peer4.

	static long thisTick = (long) 0;//Time now
	static long seconds = (long) 0;//Divided by 1000 to show in terms of seconds not milliseconds.
	static long loaddbx_longstamp = (long) 0;//Used to calculate last block time.
	static long dbxadd_longstamp = (long) 0;//Used to calculate last block time.
	static long dbxmine_longstamp = (long) 0;//Used to calculate last block time.
	static long last_block_longstamp1 = (long) 0;//Used to calculate last block time.
	static long last_block_longstamp2 = (long) 0;//Used to calculate last block time.
	static long starttime = (long) 100;//The time the program started.
	static long block_date_spam = (long) 1200000;//This is the time required to prevent network spamming.
	static long blocktimesx = (long) 60;//Time between blocks.
	static long last_block_time = (long) 0;//The time when the last block was found.
	static long time_since_last_block = (long) 0;//The time since we found the last block.
	static long last_block_time_error = (long) 3000000;//If the server is behind and we can't update we send our block.
	static long time_block_added = (long) 0;//Time since last block was added to THIS database.
	static long show_difficulty = (long) 0;//How hard it is to find a block.
	static long stale_time = (long) 1000000;//If the server is ahead and we can't find a block then we delete our stale block.
	static long mining_speed_display = 0;//Show the hash per second mining speed.

		  static BigInteger difficultyx =         new BigInteger("299295930943847810097134831044996426106091988208008541100428817054318500000");//mining difficulty this will change as new peers start mining
	final static BigInteger difficultyx_limit =   new BigInteger("277777777777777777777777777777777777777777777777777777777777777777777777777");//mining difficulty limit, the first premined blocks will have this as the difficulty liimit
	final static BigInteger difficultyx_package = new BigInteger("266666666666666666666666666666666666666666666666666666666666666666666666666");//package difficulty limit, packages will have this as the difficulty limit

	static float mining_x2_adjustment = (float) 1.25;

	static int base_int = 100000;//Base id the lowest number a listing can have.
	static int network_size = 0;//Number of nodes in network chain.
	static int p2p_port = 55555;//The remote peers connection port.
	static int database_listings_owner = 0;//How many tokens I have.
	static int database_listings_for_edit = 0;//How many tokens we have that we can edit.
	static int database_unconfirmed_total = 0;//Number of unconfirmed items in the database.
	static int system_peers = 4;//How many peers we allow. Changing this is not enough, other programing would have to be added.
	static int active_peers = 0;//The number of active peers we are connected to could be 0 - 4.
	static int listing_size = 69;//Listing token sections.
	static int miningx_size = 12;//Mining sections.
	static int mining_speed = 1;//Time between hashes.
	static int block_difficulty_reset = 100;//How many blocks before reset.
	static int block_difficulty_test = 0;//Test for enough blocks to build test > difficulty reset...
	static int hard_token_limit = 25000;//Total number of tokens to allow.
	static int hard_token_count = 0;//Total number of tokens in the database could be less then 25k if the app is new.
	static int incoming_tokens = 0;//The number of tokens in the unconfirmed db coming to us.
	static int outgoing_tokens = 0;//The number of tokens in the unconfirmed db we are sending.
	static int target_block_speed = 600000;//Target speed 600 seconds.
	static int target_block_adjustment = 3;//Percent increase or decrease of mining difficulty.
	static int confirm_before_delete = 100000;//How many confirmations before we delete the old blocks.
	static int no_connection_time = 0;//How long since last connection to test address facebook?.
	static int no_peers_time0 = 0;//How long since last peer connection to peer1.
	static int no_peers_time1 = 0;//How long since last peer connection to peer2.
	static int no_peers_time2 = 0;//How long since last peer connection to peer1.
	static int no_peers_time3 = 0;//How long since last peer connection to peer2.
	static int package_block_size = 500;//How many blocks to send per package. This is when blocks are being sent between nodes.
	static int block_compress_size = 10;//How many blocks can be put together into one set. This is when the block package is being built.
	static int send_buffer_size = 0;//How many blocks are in the send buffer.
	static int website_hits = 0;//Number of pages requested.
	static int website_searches = 0;//Number of search requests.
	static int blockchain_errors = 0;//The number of times we had to roll back.
	static int block_n_errors = 0;//The number of times we had to call for a N block update.
	static int fork_errors_one = 0;//If we have a new block and the server has a different new block.
	static int test_chain_errors = 0;//The number of times we have real errors in the blockchain.
	static int test_chain_package = 0;//The number of times we have real errors package installation.
	static int test_db_fork_history = 2000;//The number of blocks we will go back to find a fork.
	static int user_token_list_limit = 100;//The number of tokens we will return to a peer that is NOT a full node.
	static int new_item_search_limit = 100;//The number of items to save in the "what's new" list.
	static int mining_token_limit = 50;//The number of tokens you need to have to be a miner.
	static int base_58_id_size = 45;//The size the public key should be after we do a base 58 conversion. 45 for new version.
	static int mining_distribution_test = 0;//The miner cannot mining more then this number of blocks in a row.
	static int full_node_break_time	= 30000;//The time between connection requests.
	static int partial_node_listings_update = 5;//After 5 status requests we do a full listing request which takes longer.
	static int print_blocks_size = 100;//How many blocks to print for display in the print blocks activity.


    //This is the main class were everything is started from. Now that it's an android application technically it starts from MainActivity but that class just stars this one.

    public network(){

        //Here we run the steps to start up the system.

        //Start time.
        programst = "Loading";
        starttime = System.currentTimeMillis();


        //Get the template ready for use.
        build_item_array();


        //Start the database drivers
        programst = "Start database";
        systemx = new krypton_database_driver(MainActivity.context2);


        //Test JSON
        //This was used on desktop to make sure the user had JSON .jar but on Android it's not needed.
        try{

            JSONObject jsonObject = null;
            System.out.println("Test JSON");

        }catch(Exception e){System.out.println("JSON ERROR");}


        //Load the chain
        programst = "Load settings";
        krypton_database_load_network xxn = new krypton_database_load_network();
        System.out.println("database_active " + database_active);


        //load database
        programst = "Load database";
		krypton_update_listings_lite loadl = new krypton_update_listings_lite();
        krypton_database_load loadx = new krypton_database_load();
        if(network.full_node){loadx.load();}
        else{loadl.loadLiteDB();}


        //If the database is active great if not rebuild it.
        System.out.println("database_active " + database_active);
        if(database_active){

            //idx = settingsx[0];

            //System.out.println("Description: " + settingsx[2]);
			//System.out.println("DB version : " + settingsx[1]);

			//This isn't used anymore.
			//It was the first version of what nodes to allow. Now node are part of each token.
            if(open_network){System.out.println("Network OPEN");}
            else{System.out.println("Network CLOSED");}

        }//***********************


        //How many peer addresses we have.
        System.out.println("network size " + network_list.size());


        //This is the public base 58 key that is used to send us tokens or to display the QR code in the app.
        programst = "Get API key";
        if(use_old_key){get_base58_key();}
        else{get_new_base58_key();}


        //Tell the rest of the system that we are ready.
        program_ready = true;


        //This is the program look that will update info as it is available.
        programst = "Start network";
        xtimerx = new Timer();
        xtimerx.schedule(new RemindTask_engine(), 0);


        //The server system.
        //This will start the server so that other people can connect to us. The user can turn this off from the settings.
        programst = "Start server";
        if(start_server && !new_database_start){

            try{

                krypton_net_server server = new krypton_net_server();

            }catch(Exception e){e.printStackTrace();}

        }//*******************


        //Client start
        //This is the client system that will connect to a remote server.
        if(!new_database_start){//new_database_start == 0

            try{

                krypton_net_client client = new krypton_net_client();

            }catch(Exception e){e.printStackTrace();}

        }//if***********************


        //Start the mining class it won't run unless the user turns it on and the blocks are loaded.
        programst = "Ready";
        mining x4 = new mining();


    }//network







    public void get_new_base58_key(){

        try{

            String base58 = pub_key_id;//settingsx[5]

            int len = base58.length();
            byte[] data = new byte[len / 2];

            for (int i = 0; i < len; i += 2) {
                data[i / 2] = (byte) ((Character.digit(base58.charAt(i), 16) << 4) + Character.digit(base58.charAt(i+1), 16));
            }//*******************************

            byte[] sha256_1 = MessageDigest.getInstance("SHA-256").digest(data);

            byte[] a = "=".getBytes();
            byte[] b = sha256_1;

            byte[] result = new byte[a.length + b.length];
            System.arraycopy(a, 0, result, 0, a.length);
            System.arraycopy(b, 0, result, a.length, b.length);

            base58 = Base58Encode.encode(result);

            System.out.println("base58 " + base58 + " " + base58.length());

            base58_id = base58;

        }catch(Exception e){e.printStackTrace();}

    }//**************************







	public void get_base58_key(){

		try{

        	String base58 = pub_key_id;//settingsx[5]

        	int len = base58.length();
        	byte[] data = new byte[len / 2];

        	for (int i = 0; i < len; i += 2) {
            	data[i / 2] = (byte) ((Character.digit(base58.charAt(i), 16) << 4) + Character.digit(base58.charAt(i+1), 16));
        	}//*******************************

        	byte[] sha256_1 = MessageDigest.getInstance("SHA-256").digest(data);

        	base58 = Base58Encode.encode(sha256_1);

        	base58_id = base58;

    	}catch(Exception e){e.printStackTrace();}

	}//**************************






	public static void save_picture(){


		System.out.println("base58 " + base58_id);

		if(base58_id.length() == 44) {

			ByteArrayOutputStream out = QRCode.from(base58_id).to(ImageType.PNG).withSize(156, 156).stream();

			try {

				FileOutputStream fout = new FileOutputStream(new File(base58_id + ".png"));

				fout.write(out.toByteArray());

				fout.flush();
				fout.close();

			} catch (FileNotFoundException e) {
				System.out.println(e.getMessage());
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}

		}//**************************


	}//*************************









	static class RemindTask_engine extends TimerTask{
	Runtime rxrunti = Runtime.getRuntime();

        public void run(){//************************************************************************************


            while(true){


                //System.out.println("threadId engine " + Thread.currentThread().getId());

                //Test difficulty
                long difficulty = (long) 0;

                //to calculate difficulty we first cut down the big numbers to a smaller size and then subtract them

                try{


                    //First we need the number of 0 places that are separating the two
                    int difficultyil = difficultyx_limit.toString().length();
                    int difficultyix = difficultyx.toString().length();

                    BigInteger difficulty_diff = difficultyx_limit.divide(difficultyx);
                    //System.out.println("difficulty " + difficulty_diff);

                    difficulty = difficulty_diff.longValue();

                    //When the program is reloading this will show 0 for 1 second
                    if(difficulty != 0){show_difficulty = difficulty;}


                }catch(Exception e){e.printStackTrace();}//this has errors we don't care



                try{


                    //Calculate the time since the last frame.
                    thisTick = System.currentTimeMillis();
                    seconds = (thisTick - starttime) / 1000;

                    //int requests = send_requests + inbox_requests + get_requests;
                    long last_block_tl = (thisTick - last_block_longstamp2) / 1000;

                    //Get the time of the last block
                    last_block_time = last_block_longstamp1;

                    //Calculate the time since the last block.
                    time_since_last_block = thisTick - last_block_time;


                    //Display the number of active peers.
                    network.active_peers = 0;

                    if(network.peersx0){active_peers++;}
                    if(network.peersx1){active_peers++;}
                    if(network.peersx2){active_peers++;}
                    if(network.peersx3){active_peers++;}

                    if(active_peers == 0){mining.mining3 = false;}
                    else{mining.mining3 = true;}

                    try{Thread.sleep(1000);} catch(InterruptedException e){}


                } catch (Exception e) {e.printStackTrace();}

            }//*********


        }//runx***************************************************************************************************
    }//remindtask








	public void build_item_array(){

		//link_id TEXT, mining_date LONG, mining_difficulty TEXT, mining_noose TEXT, mining_old_block TEXT, mining_new_block TEXT, previous_hash_id TEXT, hash_id TEXT, sig_id TEXT, package TEXT)";

		//The layout helps when we are adding items to the database or building maps.
		mining_layout = new String[miningx_size];

		mining_layout[0] = "link_id";
		mining_layout[1] = "mining_date";
		mining_layout[2] = "mining_difficulty";
		mining_layout[3] = "mining_noose";
		mining_layout[4] = "mining_old_block";
		mining_layout[5] = "mining_new_block";
		mining_layout[6] = "previous_hash_id";
		mining_layout[7] = "hash_id";
		mining_layout[8] = "sig_id";
		mining_layout[9] = "package";
        mining_layout[10] = "mining_pkey_link";
        mining_layout[11] = "mining_sig";


		//the layout helps when we are adding items to the database or building maps
		item_layout = new String[listing_size];

		item_layout[0] = "id";
		item_layout[1] = "hash_id";
		item_layout[2] = "sig_id";
		item_layout[3] = "date_id";
		item_layout[4] = "owner_id";
		item_layout[5] = "owner_rating";
		item_layout[6] = "currency";
		item_layout[7] = "custom_template";
		item_layout[8] = "custom_1";
		item_layout[9] = "custom_2";
		item_layout[10] = "custom_3";
		item_layout[11] = "item_errors";
		item_layout[12] = "item_date_listed";
		item_layout[13] = "item_date_listed_day";
		item_layout[14] = "item_date_listed_int";
		item_layout[15] = "item_hits";
		item_layout[16] = "item_confirm_code";
		item_layout[17] = "item_confirmed";
		item_layout[18] = "item_cost";
		item_layout[19] = "item_description";
		item_layout[20] = "item_id";
		item_layout[21] = "item_price";
		item_layout[22] = "item_weight";
		item_layout[23] = "item_listing_id";
		item_layout[24] = "item_notes";
		item_layout[25] = "item_package_d";
		item_layout[26] = "item_package_l";
		item_layout[27] = "item_package_w";
		item_layout[28] = "item_part_number";
		item_layout[29] = "item_title";
		item_layout[30] = "item_title_url";
		item_layout[31] = "item_type";
		item_layout[32] = "item_search_1";
		item_layout[33] = "item_search_2";
		item_layout[34] = "item_search_3";
		item_layout[35] = "item_site_id";
		item_layout[36] = "item_site_url";
		item_layout[37] = "item_picture_1";
		item_layout[38] = "item_total_on_hand";
		item_layout[39] = "sale_payment_address";
		item_layout[40] = "sale_payment_type";
		item_layout[41] = "sale_fees";
		item_layout[42] = "sale_id";
		item_layout[43] = "sale_seller_id";
		item_layout[44] = "sale_status";
		item_layout[45] = "sale_tax";
		item_layout[46] = "sale_shipping_company";
		item_layout[47] = "sale_shipping_in";
		item_layout[48] = "sale_shipping_out";
		item_layout[49] = "sale_source_of_sale";
		item_layout[50] = "sale_total_sale_amount";
		item_layout[51] = "sale_tracking_number";
		item_layout[52] = "sale_transaction_id";
		item_layout[53] = "sale_transaction_info";
		item_layout[54] = "seller_address_1";
		item_layout[55] = "seller_address_2";
		item_layout[56] = "seller_address_city";
		item_layout[57] = "seller_address_state";
		item_layout[58] = "seller_address_zip";
		item_layout[59] = "seller_address_country";
		item_layout[60] = "seller_id";
		item_layout[61] = "seller_ip";
		item_layout[62] = "seller_email";
		item_layout[63] = "seller_first_name";
		item_layout[64] = "seller_last_name";
		item_layout[65] = "seller_notes";
		item_layout[66] = "seller_phone";
		item_layout[67] = "seller_logo";
		item_layout[68] = "seller_url";


	}//****************************








	//start the program.
    public static void main(String[] args) {

		network black = new network();

    }//main




}//last
