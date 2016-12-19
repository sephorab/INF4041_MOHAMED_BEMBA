package org.esiea.mohamed_bemba.myapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Locale;

public class StartActivity extends AppCompatActivity {
Locale current = null;


    @Override
    protected void onStart() {

        super.onStart();
        //vérifie juste la langue actuelle
        SharedPreferences prefs = getSharedPreferences(MainActivity.MyPREFERENCES, MODE_PRIVATE);
        String restoredText = prefs.getString(MainActivity.LANG, null);
        if (restoredText != null) {
            setLocale(restoredText);
        }
        current = getResources().getConfiguration().locale;
        Log.v("StartActivity","DANS ONCREATE =========== locale = "+current+" ===== clic detecte = "+getResources().getString(R.string.msg));

        setContentView(R.layout.activity_start);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    public void start(View v){
        Intent i = new Intent(this, MainActivity.class);
        GetBiersServices gbs = new GetBiersServices();
        if(isNetworkAvailable()) {
            startActivity(i);
        }
        else{

            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage(getResources().getString(R.string.error_network));
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();

                        }
                    });


            AlertDialog alert11 = builder1.create();
            alert11.show();
        }


    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public void setLocale(String lang) {

           /* String country = "FR";
            if(lang.equals("fr"))
             myLocale = new Locale(lang,country);
            else */ current = new Locale(lang);



        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = current;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, StartActivity.class);
        //finish();
        //startActivity(refresh);
    }

}
