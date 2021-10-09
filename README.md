# Krypton-Android

This is a mobile fork of my original Krypton system for PCs. This version runs only on user's Android phones with no servers and no other PC nodes in the system. The blockchain has been designed for mobile phones and is able to completely run on Android without the need for any desktop systems or servers at all. I am unaware of any other mobile blockchain systems, so I believe this to be the first. Let's be clear this is not a mobile version of Craigslist, this is a blockchain version of Craigslist.

# Krypton P2P tokens - a decentralized bulletin board

Krypton is a decentralized bulletin board of products or posts. The program, like Bitcoin, runs on each user's mobile phone so there is no central server. The tokens only exist because of the other users on the system. You can view anyone’s tokens but can only edit the ones you own. The system is kind of like Craigslist but built as a blockchain system. There are a total of 25,000 tokens on the system for people to use. That number cannot be changed unless the users of the system choose to update the protocol.

# Krypton token Features:
No selling fees.
Users all over the world can access your items for sale.
You can update your listings anytime using your private key.
Because no site is hosting the listings they cannot be taken down.
Tokens are not free so the quality of products should be high.
Tokens can be linked into other websites to become ad space.
Add search terms to your listings with others to create decentralized stores.

# Why is this needed?
Soon many websites will be forced to comply with government policies like in China and Russia.
https://www.npr.org/sections/thetwo-way/2018/03/23/596460672/craigslist-shuts-down-personals-section-after-congress-passes-bill-on-traffickin

# Program UI:
## Home Screen
On the home screen you can see a display of the most recently updated listings that have a new title. If you go to the search feature at the bottom you can search for a specific item. If you are using a full node this will search your own database. If you are a "Lite Client" it will search the peer you are connected to. If you are not conned to any peers it will say "Tor isn't ready." The QR code on the right is your public key.

Tokens is the number of coins you have.
Unconfirmed is the number of blocks waiting to be confirmed and (0) is the updates you have made but have not been sent to the network yet.
Last Block Time is the time in seconds since the last block.

# Send Screen:
If you click on the SEND (>) button you will see the Send screen. The green number on the top 100100 is the token you are going to transfer to another address. You can enter this manually if you want to transfer a specific ID or you can click GET ID and the program will choose an ID for you that you have. Each token has an ID unlike bitcoin so you have to choose which coin or (Token) you want to send. If you send more then one token the program will go though your list of tokens starting from the lowest number. Get QR code will open the camera so you can scan another user's public key.

# Listings Screen:
If you click on a listing you will see the LISTING Page which looks like the image below. Here you can see the details about the item as well as Contact details at the bottom. Each user will have to come up with the best contact system for them. Using email or some kind of messaging system.

# Edit Screen:
If you click on the EDIT button (Looks like a pencil) from the home screen you will see the image below. Here you can edit any tokens that you have. Adding a picture link from an online image hosting service will allow your listing to have a picture. The Tor system does not have a good URL redirect DNS and so it's good to test your image link first to make sure it will be available for users to see. (Many URLs are not) This service seems to work well: postimg.cc

If you want one of those little icons to appear next to your listing on the home screen you can put one of these commands in the "search 1" field of your listing. If you don't it will just display the standard "coins" icon.

DISPLAY_BITCOIN
DISPLAY_COINS
DISPLAY_COMPUTER
DISPLAY_CONDO
DISPLAY_CONDOM
DISPLAY_DOWNLOAD
DISPLAY_ELECTRONICS
DISPLAY_ENGINE
DISPLAY_GAS
DISPLAY_GEARS
DISPLAY_GIFT
DISPLAY_JEWEL
DISPLAY_LAW
DISPLAY_MAP
DISPLAY_MEDAL
DISPLAY_OK
DISPLAY_PANTS
DISPLAY_PAPER
DISPLAY_PILL
DISPLAY_RING
DISPLAY_SCIFI
DISPLAY_SUPERMAN
DISPLAY_TICKET
DISPLAY_XXX

# Settings:
If you go to the SETTINGS (Wrench) button from the home screen you will see the settings screen. (The Play Store version of the app will not have access to these features because of google play polices. But if you need them you can download the full app version from my link at the bottom of this page).

Full Node Switch from the beginning this system was built for mobile so for many users who don't want to download the blockchain they can just use the "SPV" version. If Full node is on then your phone will have the whole blockchain if it's off you will only connect to other phones for the info.

Mining Switch if you are a Full node and have all the blocks and also have a minimum number of tokens (currently 50) you can be a miner. You will not gain any tokens for mining. It's only to support the system. Google took the app down from the app store because of this feature even though there are no crypto currencies in this app. So the Play Store version doesn't have this button but you can use it on the full version if you download from the link at the bottom of this post.

Server Switch if you are a Full Node and want to be a server you can become a Full Node Server which will allow other users to connect to your phone and download blocks from you, as well as view tokens from the blockchain. How does a phone allow for incoming connections? It uses Tor as a Tor hidden service.

Use one selected peer this will force the app to connect to only one peer that you choose in the field below. If unchecked then the program will connect to random peers in the database.

Peer address this is the address of the peer the program should connect to it should be a .onion address not an IP. It's possible to connect to an IP but this version of the program will not allow you to do so.

Insert .onion into the blockchain if you want to be a server then you can add your .onion address into a section of your listing this way other nodes can find you by looking you up in the database not by a separate IP list. This way only people who actually have listings become hosts not just random users.

Get new keys if you want to get a new public private account key you can do that here although if you haven't backed up your old key you will lose all your tokens.

Copy server address if your server is active you can copy the .onion address of your phone here.

Copy public key same as on the home screen this allows you to copy your account public key so others can send you tokens.

Get private key this copies your private key to the phone's clipboard so you can back it up in case of loss. Don't give this to anyone or they will steal your tokens.

Import private key if you want to import a private key you saved on paper or on another computer you can restore your account here. These are RSA 2048 bit key pairs not Elliptic curve keys like bitcoin.

These blockchain tools are mostly for testing and probably will never be needed by users.

Reset blockchain this will delete your blockchain history and you will have to re-download it.

Delete last block this will delete just the last block from the blockchain in case it has errors or is stale.

Delete unconfirmed this will delete the unconfirmed items you have pending to send to the network. This can be used if you made a mistake.

Print blocks if you are using an android studio system you can view the block history with a printout here. This will not show visually in the app.

Contact details (Public!) these are your contact details that will be inserted into each of your listings as you update them. You can post whatever details are needed for your buyers to contact you but remember they are public and so you should not put anything here that is sensitive.

# Tor System
To run on mobile phones two main changes had to be made to the bitcoin system. One is a different type of blockchain and the other is the use of Tor. Tor is necessary for phones to communicate between each other. Since setting up a server on a phone is pretty much impossible for most users. Using Tor allows for all phones to become clients and servers. In addition to communication, Tor allows for two other main benefits. First, it hides users from some threats and is a good fit for a system like this. But also Tor allows each phone to become a server that can be accessed from regular Tor browsers all over the world. So having the app on your phone is not necessary, just a Tor browser can suffice. However, if you want to update tokens or transfer them you would need the app. The server in this app can distinguish between requests from other nodes and browsers by the header field. If there is no header it's a node, if there is a header it's a browser. If you connect as a node it's JSON if you connect as a browser it's HTML.

If users want to connect to the system but they don't have the app they can just connect to the phone by going to the phone's .onion address like this: http://md4kofpseowoo2hp.onion this allows for anyone anywhere in the world to view the status of the system from a regular PC without using the app. This could become much more valuable later on in building decentralized community websites.



# Blockchain
The blockchain has been converted into more of a "worm chain" it moves along though time but it doesn't get any longer. Each block from the back is moved to the front, moving it along kind of like how they used to move heavy blocks ages ago with logs. The block moves along the logs and when it gets to far the log from the back is moved to the front. That's pretty much how this works. The blockchain will complete it's run about every month. At that time if any node hasn't updated they would be left out and they wouldn't be able to verity the blocks on their own any longer. That's why Bitcoin doesn't do this, it needs more security. But in this case the database is just information and thus not as important as monetary transactions. I think this is a worthwhile trade off for the benefits it brings to a system like this. But if in the future storage isn't an issue with phones it would be possible to save all the transactions like Bitcoin and have the same security. By just a few changes to the code.



# FAQ:
Any relation to Craigslist the site?: None.
What is the mining algorithm: SHA256 same as Bitcoin.
Are there any mining pools: no the mining system is designed to be mined on phones no tokens are given out though mining so there's no need for a pool.
Is this going to be on any exchanges: I don't think so it's not a currency and so I don't think anyone would want to trade it as such. If anyone wanted to buy or sell a token they could just use the system itself. Why pay fees?
What's the coin limit: 25,000 KRC
Why so few coins?: the blockchain system for this grows as the number of tokens goes up so the more tokens there are the bigger the database will be and for a phone it's better to keep it as small as possible. In this system the blockchain should be less then 1GB total forever.
Are you asking for money to build this system?: no it's already done. But some large scale testing hasn't been done yet.
Are there tokens for sale: yes I'm going to give away the first 1,000 for free and the next 1,000 for $1 and so on. If you want free tokens send me a message.
Are there any other ways to get tokens: I will give away free tokens to people who want to be servers and testers.
Is this on iphone?: no I don't know anything about iphone programming I'm not sure if the tor libraries are available for iphone but I would guess they are.
Does it work on ARC Welder?: no I think because the app needs Linux for Tor it doesn't work.



# Timeline:
Full version release: (July 12, 2018) You can download the APK here: http://www.mediafire.com/file/enx6e68sxgadsn6/Krypton_1.2.5.apk/file
Public app release: (August 19, 2018):

