package com.mobile.app.krypton;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class krypton_database_get_unconfirmed_test extends SQLiteOpenHelper {



    @Override
    public void onCreate(SQLiteDatabase db) {
        // This will ensure that all tables are created


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This will upgrade tables, adding columns and new tables.
        // Note that existing columns will not be converted


    }


    //Here we test if the item listing is already in the unconfirmed database before we add a new one.
    //Sometimes users will try to update their listing many times before it's confirmed.
    //We don't want this because it's spam and also they may be trying to trick someone.

    public krypton_database_get_unconfirmed_test(){

        super(MainActivity.context2, krypton_database_driver.DATABASE_NAME, null, krypton_database_driver.DATABASE_VERSION);

    }//********************************************



    public int testx(String id){//**************************************************************************

        int ix0 = 0;

        try {


            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();


            try {


                System.out.println("[>>>] Load unconfirmed_db test" );

                String query = ("SELECT xd FROM unconfirmed_db WHERE id=? ORDER BY id ASC");
                Cursor cursor = db.rawQuery(query, new String[]{id});

                ix0 = cursor.getCount();

                System.out.println("In the task list? " + ix0);

                cursor.close();


            } catch (Exception e){e.printStackTrace();}
            finally {

                db.close();
                System.out.println("finally block executed");

            }


        } catch (Exception e) {e.printStackTrace();}
        finally {

            network.database_in_use = false;

        }

        return ix0;

    }//load




    public int testx_hash(String hash){//**************************************************************************

        int ix0 = 0;

        try {


            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();


            try{


                System.out.println("[>>>] Load unconfirmed_db test" );

                String query = ("SELECT xd FROM unconfirmed_db WHERE hash_id=? ORDER BY id ASC");
                Cursor cursor = db.rawQuery(query, new String[]{hash});

                ix0 = cursor.getCount();

                System.out.println("In the task list? " + ix0);

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

        return ix0;

    }//load


 
}//class
