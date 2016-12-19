package org.esiea.mohamed_bemba.myapp;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.nfc.Tag;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.PublicKey;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.database.*;

public class MainActivity extends AppCompatActivity {

    DatePickerDialog dpd;
    public static final String BIERS_UPDATE = "com.octip.cours.inf4042_11.BIERS_UPDATE";
    public RecyclerView recyclerView;
    private GetBiersServices gbs;
    private Context context;
    private BiersDAO biersDAO = null;
    private ArrayList<String> listNames;
    private BierAdapter bierAdapter = null;
    private TextView tv_hw = null;
    private CheckBox star = null;
    Locale current = null;
    ArrayList listFavorites = null;
    public static boolean checked = false;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String LANG = "langKey";

    @Override
    protected void onStart() {

        super.onStart();
       bierAdapter.notifyDataSetChanged();//raffraichit les données quand on revient sur l'activité liste

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //vérifie juste la langue actuelle
         current = getResources().getConfiguration().locale;
        Log.v("Mainactivity","DANS ONCREATE =========== locale = "+current+" ===== clic detecte = "+getResources().getString(R.string.msg));



        //TextView date
        tv_hw = (TextView) findViewById(R.id.tv_hello_world);
        //DateFormat df = new SimpleDateFormat("dd/MM/yyyy", current);
        DateFormat df;
        df = DateFormat.getDateInstance(DateFormat.FULL, current);
        Date today = Calendar.getInstance().getTime();
        String now = df.format(today);
       // String now = DateUtils.formatDateTime(getApplicationContext(),(new Date()).getTime(), DateFormat.FULL);
        tv_hw.setText("Date : "+now);
context = this;
        //star
        star= (CheckBox) findViewById(R.id.starDAO);

        star.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                            @Override
                                            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                                                Log.v("star","ischecked ="+isChecked);
                                                if(isChecked==false){

                                                   // Toast.makeText(getApplicationContext(),"is checked = false",Toast.LENGTH_LONG).show();

                                                    star.setBackgroundResource(R.drawable.checkbox_star_down);
                                                    star.setTextColor(Color.WHITE);
                                                    star.setWidth(50);
                                                    star.setBackgroundColor(getResources().getColor(R.color.greenDark));
                                                    checked=false;

                                                   showFavorites(new View(context));
                                                }
                                                else  {

                                                   // Toast.makeText(getApplicationContext(),"is checked = true",Toast.LENGTH_LONG).show();

                                                    star.setBackgroundResource(R.drawable.checkbox_star);
                                                    star.setWidth(50);

                                                    star.setTextColor(getResources().getColor(R.color.yellow));
                                                    star.setBackgroundColor(getResources().getColor(R.color.greenDark));
                                                    checked=true;
                                                    showFavorites(new View(context));

                                                }
                                            }
                                        }
        );
        /*
        //choisir une date dans le calendrier
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                tv_hw.setText(dayOfMonth+"/"+monthOfYear+ "/"+year);
            }
        };
        dpd = new DatePickerDialog(this,onDateSetListener,2016,11,14);
        */

        //bdd
        Log.d("mainactivity", "Reremplit la listNames - dans onCreate");
        biersDAO = new BiersDAO(this);
        Log.d("mainactivity", "apres constructeur biersdao");
        biersDAO.open();
        Log.d("mainactivity", "apres open");
        listNames = biersDAO.getAllBiers();
        Log.d("mainactivity", "avant close");
        biersDAO.close();



        //partie biers
        GetBiersServices.startActionGetAllBiers(this);
        IntentFilter intentFilter = new IntentFilter(BIERS_UPDATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(new BierUpdate(),intentFilter);

        //liste bières

        recyclerView= (RecyclerView) findViewById(R.id.rv_biere);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        gbs = new GetBiersServices();


        if(checked)star.setChecked(true);
        else star.setChecked(false);
        if(listFavorites==null) listFavorites = new ArrayList();

        if(checked==false) bierAdapter = new BierAdapter(gbs.getBiersFromFile(this));
        else bierAdapter = new BierAdapter(new JSONArray(listFavorites));

        recyclerView.setAdapter(bierAdapter);
        context = this;

        showFavorites(new View(this));

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

    }

    //action clic button - quand on modifie la langue
    public void showToast(View v){

        Toast.makeText(getApplicationContext(),getString(R.string.msg),Toast.LENGTH_LONG).show();
        notification_test();
       // launchActivity2();

    }
    //action clic texte
    public void onClick(View v){
        dpd.show();
    }

    //fait apparaitre une notification, mettre une notif quand on change de language by default
    public void notification_test(){
        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this) ;
        notifBuilder.setSmallIcon(R.drawable.ic_notif);
        notifBuilder.setContentTitle(getResources().getString(R.string.notif_lang));
        notifBuilder.setContentText(getResources().getString(R.string.notif_lang_msg));
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        nm.notify(1,notifBuilder.build());

    }

    //se déclenche dès qu'on sélectionne un item du menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.toast)
       // Toast.makeText(getApplicationContext(),"Vous avez cliqué sur l'action bar !",Toast.LENGTH_LONG).show();
        launchApp(new View(this));//lance le site
        else{//ite change language dialog box
            int selected =0;
            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setTitle(R.string.choice_language);
            builder1.setCancelable(true);

            final String[] items = getResources().getStringArray(R.array.languages);


            OnClickListenerDialog onClickListener= new OnClickListenerDialog();
            builder1.setSingleChoiceItems(items, 0, onClickListener);

           // Log.v("dialog","taille ites = "+items.length);

            builder1.setPositiveButton(
                    getResources().getString(R.string.yes),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //on veut valider le français
                           // Log.v("dialog","taille ites = "+items.length+" : "+items+".\n which = "+which);
                           /* Toast.makeText(context,
                                    "ites = "+items[OnClickListenerDialog.selected], Toast.LENGTH_SHORT)
                                    .show();*/
                            if(items[OnClickListenerDialog.selected].equals("French") || items[OnClickListenerDialog.selected].equals("Français")) {

                                setLocale("fr");

                                String choicef = getResources().getString(R.string.choice_french);

                                Toast.makeText(context,
                                        choicef, Toast.LENGTH_SHORT)
                                        .show();
                                notification_test();
                                SharedPreferences.Editor editor = sharedpreferences.edit();

                                editor.putString(LANG, "fr");
                                editor.commit();
                                OnClickListenerDialog.selected=0;
                            }
                            else // on veut valider l'english
                            {


                                setLocale("en");

                                final String choicee = getResources().getString(R.string.choice_english);
                                Toast.makeText(context,
                                        choicee, Toast.LENGTH_SHORT)
                                        .show();
                                notification_test();
                                SharedPreferences.Editor editor = sharedpreferences.edit();

                                editor.putString(LANG, "en");
                                editor.commit();

                                OnClickListenerDialog.selected=0;
                            }
                         //   bierAdapter.notifyDataSetChanged();
                        }
                    });

            builder1.setNegativeButton(
                    getResources().getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();



            /*
            Intent i = new Intent(this,PreferencesActivity.class) ;
            startActivity(i);*/
        }

        return super.onOptionsItemSelected(item);

    }

    //récupère le menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //lance l'activité 2
    public void launchActivity2(int pos){
        Intent i = new Intent(this,SecondeActivity.class) ;
        JSONObject obj = null;
        try {
            if(star.isChecked() == false){

                obj = gbs.getBiersFromFile(this).getJSONObject(pos);
            }
            else{
                obj = gbs.getBierWithName(gbs.getBiersFromFile(this),(String) listFavorites.get(pos));

            }
            i.putExtra("name",obj.getString("name"));
            i.putExtra("description",obj.getString("description"));
            i.putExtra("note",obj.getString("note"));
            i.putExtra("country_id", obj.getString("country_id"));
            i.putExtra("category_id", obj.getString("category_id"));
            startActivity(i);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    //lance google map
    public void launchApp(View v){
       // startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=Londre")));
        Intent viewIntent =
                new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.beerworld.ch/"));
        startActivity(viewIntent);
    }


    //réagit à l'update
    public class BierUpdate extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
          //  Log.d("mainactivity", getIntent().getAction());
            ((BierAdapter)recyclerView.getAdapter()).setNewBier();
           // Toast.makeText(getApplicationContext(),"BierUpdate a bien reçu l'update ",Toast.LENGTH_LONG).show();


        }


    }


    private class BierAdapter extends RecyclerView.Adapter<BierAdapter.BierHolder>{

        private JSONArray biers;

        public BierAdapter(JSONArray biers){
            this.biers = biers;
            Log.v("mainactivity","Taille du json array biers = "+biers.length());
        }

        //appelé quand veut créer un élément de liste
        @Override
        public BierHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            biersDAO.open();
            listNames = biersDAO.getAllBiers();
            biersDAO.close();
            BierHolder bh = new BierHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_bier_element,parent,false));

            //Log.v("mainactivity","a crée un viewholder : "+bh.button.getText());
            return bh;

        }

        @Override
        public void onBindViewHolder(BierHolder holder, int position) {

            //traiter les données pour remplir le bierholder
            try {
                if(checked==false) {
                    JSONObject obj = biers.getJSONObject(position);
                    holder.name.setText(obj.getString("name"));
                    holder.name.setTypeface(Typeface.DEFAULT);
                    holder.icon.setVisibility(View.INVISIBLE);
                    biersDAO.open();
                    listNames = biersDAO.getAllBiers();
                    biersDAO.close();
                    String textTv;
                    for (int i = 0; i < listNames.size(); i++) {
                        textTv = obj.getString("name");
                        //textTv = holder.name.getText().toString();
                      //  Log.i("JFL", "holder.name " + textTv);
                        if (textTv.equals(listNames.get(i))) {
                            holder.name.setTypeface(Typeface.DEFAULT_BOLD);
                            holder.icon.setVisibility(View.VISIBLE);
                          //  Log.i("JFL", "gras");
                        }

                    }
                }
                else{


                    try {

                        holder.name.setText(biers.getString(position));
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    holder.name.setTypeface(Typeface.DEFAULT);
                    holder.icon.setVisibility(View.INVISIBLE);
                    biersDAO.open();
                    listNames = biersDAO.getAllBiers();
                    biersDAO.close();
                    String textTv="";
                    for(int i = 0; i<listNames.size();i++){
                        try {
                            textTv = biers.getString(position);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        //textTv = holder.name.getText().toString();
                     //   Log.i("JFL", "holder.name " + textTv);
                        if(textTv.equals(listNames.get(i))){
                            holder.name.setTypeface(Typeface.DEFAULT_BOLD);
                            holder.icon.setVisibility(View.VISIBLE);
                          //  Log.i("JFL", "gras" );
                        }

                    }




                }
                //Log.i("JFL", "bind ! : " + holder.button.getText());


                //holder.button.setText(obj"");
            } catch (JSONException e) {
                e.printStackTrace();

/*
                try {
                    holder.name.setText(biers.getString(position));
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                holder.name.setTypeface(Typeface.DEFAULT);
                holder.icon.setVisibility(View.INVISIBLE);
                biersDAO.open();
                listNames = biersDAO.getAllBiers();
                biersDAO.close();
                String textTv="";
                for(int i = 0; i<listNames.size();i++){
                    try {
                        textTv = biers.getString(position);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    //textTv = holder.name.getText().toString();
                    Log.i("JFL", "holder.name " + textTv);
                    if(textTv.equals(listNames.get(i))){
                        holder.name.setTypeface(Typeface.DEFAULT_BOLD);
                        holder.icon.setVisibility(View.VISIBLE);
                        Log.i("JFL", "gras" );
                    }

                }
*/





            }

           // Log.v("mainactivity","a modifié le texte d'un viewholder, position = "+position);
            //Log.v("mainactivity","Taille du json array biers = "+biers.length());
           // holder.name.setText("Biere par défaut");
           // notifyItemChanged(position);

        }

        @Override
        public int getItemCount() {
            return  biers.length();
        }

        public void setNewBier(){
            if(checked==false) biers = gbs.getBiersFromFile(context);
           notifyDataSetChanged();
        }

        public class BierHolder extends RecyclerView.ViewHolder implements  View.OnClickListener
        {

            public TextView name;
            //public Button button;
            public ImageView icon;

            public BierHolder(View itemView) {
                super(itemView);
                View element_de_liste = itemView;
                name = (TextView) element_de_liste.findViewById(R.id.rv_bier_element_name);
                //button = (Button) element_de_liste.findViewById(R.id.rv_bier_element_button_details);
                icon = (ImageView) element_de_liste.findViewById(R.id.rv_bier_element_icon);
                //element_de_liste.setOnClickListener(this);
                name.setOnClickListener(this);
                //Log.d("event","ONTOUCH nnnnnnnnnn:");
            }


            @Override
            public void onClick(View v) {
               // v.setBackgroundColor(Color.rgb(255,153,51));
                v.setSelected(true);

                //Log.d("event","ONTOUCH nnnnnnnnnn:");
                int pos = getAdapterPosition();
                launchActivity2(pos);

            }


        }
    }
/*
    public static String formatTime(Date time, Locale locale){
        String timeFormat = UserSettingManager.getUserSetting(UserSettingManager.PREF_TIME_FORMAT);
        SimpleDateFormat formatter;

        try {
            formatter = new SimpleDateFormat(timeFormat, locale);
        } catch(Exception e) {
            formatter = new SimpleDateFormat(DEFAULT_TIME_FORMAT, locale);
        }
        return formatter.format(time);
    }
*/
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
        Intent refresh = new Intent(this, MainActivity.class);
        finish();
        startActivity(refresh);
    }


    /** Affiche que les favoris ou non **/
    public void showFavorites(View v){
        biersDAO.open();

        //on veut afficher que les favoris
        if(checked) {

           JSONArray all = gbs.getBiersFromFile(this);
             listFavorites = new ArrayList();//get all favorites in order

            for(int i = 0; i<all.length();i++){
                for(int j = 0; j<listNames.size();j++) {
                    JSONObject obj = null;
                    try {
                        obj = all.getJSONObject(i);
                       if(obj.getString("name").equals(listNames.get(j))){
                           listFavorites.add(obj.getString("name"));


                           break;
                       }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            bierAdapter = new BierAdapter(new JSONArray(listFavorites));
            recyclerView.setAdapter(bierAdapter);
            bierAdapter.notifyDataSetChanged();
            // star.setRating(1);
        }
        else {
            //biersDAO.deleteBier(getIntent().getStringExtra("name"));
            bierAdapter = new BierAdapter(gbs.getBiersFromFile(this));
            recyclerView.setAdapter(bierAdapter);
            bierAdapter.notifyDataSetChanged();
            // star.setRating(0);
        }


        //int taille = biersDAO.getAllBiers().size();

        //Toast.makeText(getApplicationContext(),"Taille de la liste : "+taille,Toast.LENGTH_LONG).show();
        biersDAO.close();


    }

}
