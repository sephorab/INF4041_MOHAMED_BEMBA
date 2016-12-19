package org.esiea.mohamed_bemba.myapp;

import android.app.IntentService;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class GetBiersServices extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_GET_ALL_BIERS = "org.esiea.mohamed_bemba.myapp.action.get_all_biers";
    private static final String ACTION_GET_COUNTRY = "org.esiea.mohamed_bemba.myapp.action.get_country";
    private static final String ACTION_GET_CATEGORY = "org.esiea.mohamed_bemba.myapp.action.get_category";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "org.esiea.mohamed_bemba.myapp.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "org.esiea.mohamed_bemba.myapp.extra.PARAM2";

    public GetBiersServices() {
        super("GetBiersServices");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionGetAllBiers(Context context/*, String param1, String param2*/) {
        Intent intent = new Intent(context, GetBiersServices.class);
        intent.setAction(ACTION_GET_ALL_BIERS);
        //intent.putExtra(EXTRA_PARAM1, param1);
        //intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionGetCountry(Context context,String country_id) {
        Intent intent = new Intent(context, GetBiersServices.class);
        intent.setAction(ACTION_GET_COUNTRY);
        intent.putExtra("country_id",country_id);
        context.startService(intent);
        Log.i("startactiongetcountry","service country démarré ");
    }


    public static void startActionGetCategory(Context context,String category_id) {
        Intent intent = new Intent(context, GetBiersServices.class);
        intent.setAction(ACTION_GET_CATEGORY);
        intent.putExtra("category_id",category_id);
        context.startService(intent);
        Log.i("startactiongetcategory","service category démarré ");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET_ALL_BIERS.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionGetAllBiers(param1, param2);
            } else if (ACTION_GET_COUNTRY.equals(action)) {
                handleActionGetCountry(intent.getStringExtra("country_id"));

                Log.i("onhandleintent","action country bien détectée ");
            }
         else if (ACTION_GET_CATEGORY.equals(action)) {
            handleActionGetCategory(intent.getStringExtra("category_id"));

            Log.i("onhandleintent","action category bien détectée ");
        }


        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionGetAllBiers(String param1, String param2) {
        // TODO: Handle action Foo
        Log.i("getallbiers","Log de handleActionGetAllBiers");
        Log.i("getallbiers","Thread service name : "+Thread.currentThread().getName());
        URL url = null;

        try {

            url = new URL("http://binouze.fabrigli.fr/bieres.json");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if(HttpURLConnection.HTTP_OK == conn.getResponseCode()){
                copyInputStreamToFile(conn.getInputStream(), new File(getCacheDir(),"bieres.json"));
                Log.d("getallbiers","Bieres json downloaded");
            }

            //lance un intent pour signaler l'update
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(MainActivity.BIERS_UPDATE));

        }catch(MalformedURLException e){
            e.printStackTrace();


        } catch (IOException e) {
            e.printStackTrace();


        }

    }

    private void handleActionGetCountry(String id) {
        // TODO: Handle action Foo

        Log.i("getcountry","Thread service name : "+Thread.currentThread().getName());
        URL url = null;

        try {

            url = new URL("http://binouze.fabrigli.fr/countries/"+id+".json");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if(HttpURLConnection.HTTP_OK == conn.getResponseCode()){
                copyInputStreamToFile(conn.getInputStream(), new File(getCacheDir(),"country.json"));
                Log.d("getcountry","Country json downloaded");
            }
            else{
                Log.d("getcountry","Country json NOT downloaded. HttpURLConnection.HTTP_OK = "+HttpURLConnection.HTTP_OK+ " et conn.getResponseCode() = "+conn.getResponseCode());
            }

            //lance un intent pour signaler l'update
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(SecondeActivity.COUNTRY_UPDATE));

        }catch(MalformedURLException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void handleActionGetCategory(String id) {
        // TODO: Handle action Foo

        Log.i("getcountry","Thread service name : "+Thread.currentThread().getName());
        URL url = null;

        try {

            url = new URL("http://binouze.fabrigli.fr/categories/"+id+".json");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if(HttpURLConnection.HTTP_OK == conn.getResponseCode()){
                copyInputStreamToFile(conn.getInputStream(), new File(getCacheDir(),"category.json"));
                Log.d("getcategory","Country json downloaded");
            }
            else{
                Log.d("getcategory","Country json NOT downloaded. HttpURLConnection.HTTP_OK = "+HttpURLConnection.HTTP_OK+ " et conn.getResponseCode() = "+conn.getResponseCode());
            }

            //lance un intent pour signaler l'update
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(SecondeActivity.CATEGORY_UPDATE));

        }catch(MalformedURLException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    private void copyInputStreamToFile(InputStream in, File file){

        try{
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new  byte[1024];

            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            in.close();


        }catch(Exception e){
            e.printStackTrace();
        }


    }


    public  JSONArray getBiersFromFile(Context context){
        try{
            InputStream is = new FileInputStream(context.getCacheDir()+"/"+"bieres.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            return new JSONArray(new String(buffer,"UTF-8"));
        }catch(IOException e){
            e.printStackTrace();
            return new JSONArray();
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    public  JSONObject getBierWithName(JSONArray array, String str){

        JSONObject  obj = null;
        for(int i = 0; i<array.length();i++){

            try {
                JSONObject obj2 = array.getJSONObject(i);

                if(obj2.getString("name").equals(str)) {
                    obj = obj2;
                    break;
                }


                } catch (JSONException e) {
                e.printStackTrace();
            }
        }



        return obj;


    }

    public  JSONObject getCountryFromFile(Context context){
        try{
            InputStream is = new FileInputStream(context.getCacheDir()+"/"+"country.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            return new JSONObject(new String(buffer,"UTF-8"));
        }catch(IOException e){
            e.printStackTrace();
            return new JSONObject();
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    public  JSONObject getCategoryFromFile(Context context){
        try{
            InputStream is = new FileInputStream(context.getCacheDir()+"/"+"category.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            return new JSONObject(new String(buffer,"UTF-8"));
        }catch(IOException e){
            e.printStackTrace();
            return new JSONObject();
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }



    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }



}
