package org.esiea.mohamed_bemba.myapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SecondeActivity extends AppCompatActivity {

    TextView tvName, tvDescription, tvNote, tvCountry, tvCategory;
    ProgressBar circle;
   // Button bFavorite;
    CheckBox star;
    BiersDAO biersDAO;
    Toast toast = null;

    public static final String COUNTRY_UPDATE = "com.octip.cours.inf4042_11.COUNTRY_UPDATE";

    public static final String CATEGORY_UPDATE = "com.octip.cours.inf4042_11.CATEGORY_UPDATE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seconde);
        circle= (ProgressBar) findViewById(R.id.circle);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvDescription = (TextView)findViewById(R.id.tv_description);
        tvNote = (TextView)findViewById(R.id.tv_note);
        tvCountry = (TextView)findViewById(R.id.tv_country);
        tvCategory = (TextView)findViewById(R.id.tv_category);
        star= (CheckBox) findViewById(R.id.star);

        star.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                Log.v("star","ischecked ="+isChecked);
                if(isChecked==false)
                    star.setBackgroundResource(R.drawable.checkbox_star_down);
                else  star.setBackgroundResource(R.drawable.checkbox_star);
            }
        }
        );

        biersDAO = new BiersDAO(this);
        biersDAO.open();
        if(biersDAO.isAlreadyFavorite(getIntent().getStringExtra("name")))
            //star.setBackgroundResource(R.drawable.checkbox_star_down);
            star.setChecked(true);
        else
        //star.setChecked(false);
            star.setBackgroundResource(R.drawable.checkbox_star);
        biersDAO.close();
        //partie country et category
        GetBiersServices.startActionGetCountry(this,getIntent().getStringExtra("country_id"));
        GetBiersServices.startActionGetCategory(this,getIntent().getStringExtra("category_id"));
        IntentFilter intentFilter = new IntentFilter(COUNTRY_UPDATE);
        intentFilter.addAction(CATEGORY_UPDATE);

        LocalBroadcastManager.getInstance(this).registerReceiver(new CountryUpdate(),intentFilter);


        //affichage des données
        tvName.setText(getIntent().getStringExtra("name"));
        String mark = getResources().getString(R.string.note);
        tvDescription.setText("Description : "+getIntent().getStringExtra("description"));
        tvNote.setText(mark+" : "+getIntent().getStringExtra("note"));



    }

    //réagit à l'update
    public class CountryUpdate extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("secondeactivity", intent.getAction());

            GetBiersServices gbs = new GetBiersServices();
            JSONObject objCountry = gbs.getCountryFromFile(context);

            JSONObject objCategory = gbs.getCategoryFromFile(context);

            try {
                String country = getResources().getString(R.string.country);
                String category = getResources().getString(R.string.category);
                String countryName = objCountry.getString("name");
                tvCountry.setText(country+" : "+countryName);
                String categoryName = objCategory.getString("name");
                tvCategory.setText(category+" : "+categoryName);

            } catch (JSONException e) {
                e.printStackTrace();
            }



            String update = getResources().getString(R.string.update);
           // Toast.makeText(getApplicationContext(),update,Toast.LENGTH_SHORT).show();
            if (toast != null) {
               // toast.cancel();
            }
            else{
                toast = Toast.makeText(context, update, Toast.LENGTH_SHORT);
                toast.show();
            }


            circle.setVisibility(View.GONE);
            tvCategory.setVisibility(View.VISIBLE);
            tvCountry.setVisibility(View.VISIBLE);
            tvDescription.setVisibility(View.VISIBLE);
            tvName.setVisibility(View.VISIBLE);
            tvNote.setVisibility(View.VISIBLE);
            star.setVisibility(View.VISIBLE);

        }




    }


    public void createFavorite(View v){
        biersDAO.open();
        if(!biersDAO.isAlreadyFavorite(getIntent().getStringExtra("name"))) {
            biersDAO.createBier(getIntent().getStringExtra("name"));
            String added = getResources().getString(R.string.added_favorite);
            Toast.makeText(getApplicationContext(),added,Toast.LENGTH_SHORT).show();
         // star.setRating(1);
        }
        else {
            biersDAO.deleteBier(getIntent().getStringExtra("name"));
            String removed = getResources().getString(R.string.removed_favorite);
            Toast.makeText(getApplicationContext(),removed,Toast.LENGTH_SHORT).show();
           // star.setRating(0);
        }


       //int taille = biersDAO.getAllBiers().size();

        //Toast.makeText(getApplicationContext(),"Taille de la liste : "+taille,Toast.LENGTH_LONG).show();
        biersDAO.close();


    }


}
