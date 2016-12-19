package org.esiea.mohamed_bemba.myapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.Locale;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

/**
* https://developer.android.com/guide/topics/ui/dialogs.html
* *
* */
public class PreferencesActivity extends PreferenceActivity {

        Spinner spinnerctrl;
        Button btn;
        Locale myLocale;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_preferences);
            spinnerctrl = (Spinner) findViewById(R.id.spinner);
            spinnerctrl.setOnItemSelectedListener(new OnItemSelectedListener() {

                public void onItemSelected(AdapterView<?> parent, View view,
                                           int pos, long id) {


                    String choicee = getResources().getString(R.string.choice_english);
                    String choicef = getResources().getString(R.string.choice_french);
                    if (pos == 1) {

                        Toast.makeText(parent.getContext(),
                                choicef, Toast.LENGTH_SHORT)
                                .show();
                        setLocale("fr");
                    } else if (pos == 2) {

                        Toast.makeText(parent.getContext(),
                                choicee, Toast.LENGTH_SHORT)
                                .show();
                        setLocale("en");
                    }

                }

                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub
                }

            });
        }

        public void setLocale(String lang) {

           /* String country = "FR";
            if(lang.equals("fr"))
             myLocale = new Locale(lang,country);
            else */ myLocale = new Locale(lang);



            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
            Intent refresh = new Intent(this, MainActivity.class);
            finish();
            startActivity(refresh);
        }
    }