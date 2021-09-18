package com.mobile.app.krypton;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;




public class krypton_database_get_token extends SQLiteOpenHelper{
    /* the default framework is embedded*/




    @Override
    public void onCreate(SQLiteDatabase db) {
        // this will ensure that all tables are created


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this will upgrade tables, adding columns and new tables.
        // Note that existing columns will not be converted


    }


    //Here we are loading a token from the listings_db these are the "coins" of the system.

    public krypton_database_get_token(){

        super(MainActivity.context2, krypton_database_driver.DATABASE_NAME, null, krypton_database_driver.DATABASE_VERSION);

    }//*********************************







    //Here we get the token using the HASH not the ID

    public String[] getTokenFH(String hash){//**************************************************************************

        String[] token_ssp2 = new String[network.listing_size];

        try {


            //We are working.
            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();


            try {


                System.out.println("[>>>] Get Token From Hash");


                for(int loop1 = 0; loop1 < network.listing_size; loop1++){//***********

                    token_ssp2[loop1] = "error";

                }//********************************************************************



                System.out.println("Load Token..." );

                String query = ("SELECT * FROM listings_db WHERE hash_id=? ORDER BY id ASC");
                Cursor cursor = db.rawQuery(query, new String[]{hash});

                cursor.moveToFirst();

                while(!cursor.isAfterLast()){


                    for(int loop1 = 0; loop1 < network.listing_size; loop1++){//***********

                        token_ssp2[loop1] = cursor.getString((loop1 + 1));

                    }//********************************************************************

                    cursor.moveToNext();

                }//while


                cursor.close();


            } catch (Exception e) {e.printStackTrace();}
            finally {

                db.close();
                System.out.println("finally block executed");

            }


        } catch (Exception e) {e.printStackTrace();}
        finally {

            network.database_in_use = false;

        }

        return token_ssp2;

    }//load






    //These are tokens the program is requesting.

    public String[] getToken(String id){//**************************************************************************

        String[] token_ssp2 = new String[network.listing_size];

        try {


            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();

            int idx = 0;


            try {


                for (int loop1 = 0; loop1 < network.listing_size; loop1++) {//*********

                    token_ssp2[loop1] = "error";

                }//********************************************************************

                //If this fails we don't need to try to load anything.
                idx = Integer.parseInt(id);

                System.out.println("Load Token..." );

                String query = ("SELECT * FROM listings_db WHERE id=? ORDER BY id ASC");
                Cursor cursor = db.rawQuery(query, new String[]{Integer.toString(idx)});

                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {


                    for (int loop1 = 0; loop1 < network.listing_size; loop1++) {//*********

                        token_ssp2[loop1] = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(loop1 + 1)));

                    }//********************************************************************

                    cursor.moveToNext();

                }//while


                cursor.close();


            }catch(Exception e){e.printStackTrace();}
            finally {

                db.close();
                System.out.println("finally block executed");

            }


        } catch (Exception e) {e.printStackTrace();}
        finally {

            network.database_in_use = false;

        }

        return token_ssp2;

    }//load






    //This is the same as above but it is for loading tokens the user is requesting in the app.
    //Android doesn't need this because it can handel more then one request but in Derby we needed a way to have more then one connection at the same time.

    public String[] getToken2(String id){//**************************************************************************

        String[] token_ssp2 = new String[network.listing_size];

        try {


            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();

            int idx = 0;


            try {


                System.out.println("[>>>] Get Token2");

                for (int loop1 = 0; loop1 < network.listing_size; loop1++) {//*********

                    token_ssp2[loop1] = "error";

                }//********************************************************************

                //If this fails then we don't need to try to load anything.
                idx = Integer.parseInt(id);

                System.out.println("Load Token..." );

                String query = ("SELECT * FROM listings_db WHERE id=? ORDER BY id ASC");
                Cursor cursor = db.rawQuery(query, new String[]{Integer.toString(idx)});

                cursor.moveToFirst();

                while(!cursor.isAfterLast()){


                    for(int loop1 = 0; loop1 < network.listing_size; loop1++){//***********

                        token_ssp2[loop1] = cursor.getString((loop1 + 1));

                    }//********************************************************************

                    cursor.moveToNext();

                }//while


                cursor.close();


            }catch(Exception e){e.printStackTrace();}
            finally {

                db.close();
                System.out.println("finally block executed");

            }


        } catch (Exception e) {e.printStackTrace();}
        finally {

            network.database_in_use = false;

        }

        return token_ssp2;

    }//load





}//get
