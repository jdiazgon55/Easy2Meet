package es.uv.jaimediazgonzalez.facilquedar.ventanas;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import es.uv.jaimediazgonzalez.facilquedar.listas.DiasComunesAdapter;
import es.uv.jaimediazgonzalez.facilquedar.listas.FechaCursor;
import es.uv.jaimediazgonzalez.facilquedar.R;
import es.uv.jaimediazgonzalez.facilquedar.listas.ListaUtil;

public class VerResultados extends AppCompatActivity {
    private static final String TAG = "VerResultados";

    private Button listo;
    private ListView comunesList;
    private ArrayList<FechaCursor> diasComunesArray;
    private List<String> diasComunesTemp;
    private RelativeLayout layout;
    private ArrayList<String> diasNoBorrar;
    private DiasComunesAdapter diasComunesAdapter;
    Context context;
    private ArrayList<String> diasSeleccionados;
    private String nombreUsuario, codigoUnico;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_resultados);

        listo = (Button) findViewById(R.id.listoCodigo);

        context = this.getApplicationContext();

        //horasSeleccionadas = getIntent().getStringArrayListExtra("horasSeleccionadas");
        //Recogemos los datos del Intent

        nombreUsuario = getIntent().getStringExtra("nombreUsuario");
        // Para el caso de querer ver solo los resultados
        if(getIntent().hasExtra("diasSeleccionados")) {
            diasSeleccionados = getIntent().getStringArrayListExtra("diasSeleccionados");
        }

        nombreUsuario = getIntent().getStringExtra("nombreUsuario");
        codigoUnico = getIntent().getStringExtra("codigoUnico");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(codigoUnico);

        DatabaseReference users = myRef.child("Users");
        DatabaseReference usuarioActual = users.child(nombreUsuario);

        if(diasSeleccionados.isEmpty()){
            diasSeleccionados.add(getResources().getString(R.string.sin_seleccionar_dia));
        }
        usuarioActual.setValue(diasSeleccionados);

        users.addValueEventListener(retrieveUsersDataListener);

        comunesList = (ListView) findViewById(R.id.listcomunesview);

        comunesList.setEmptyView(findViewById(R.id.emptyElement));

        layout = (RelativeLayout) findViewById(R.id.relative_layout);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-4541521919567374/8544259144");
        mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice("C42B273B43CE4B989BF1D9B059A047C3").build());
        mInterstitialAd.setAdListener(interstitialADListener);

        /* LISTENERS */
        listo.setOnClickListener(volverMenuPrincipalListener);

    }

    ValueEventListener retrieveUsersDataListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // Get Post object and use the values to update the UI
            HashMap<String, Object> usersHashMap = (HashMap<String, Object>)dataSnapshot.getValue();
            diasComunesTemp = new ArrayList<String>();
            diasComunesArray = new ArrayList<FechaCursor>();

            if(usersHashMap != null) {
                leerDiasComunes(usersHashMap);
                diasComunesArray = guardarEnAdapter(diasComunesTemp);
                crearListView(usersHashMap);

                diasComunesAdapter = new DiasComunesAdapter(context, diasComunesArray);
                diasComunesAdapter.setDiasComunes(diasComunesArray);
                comunesList.setAdapter(diasComunesAdapter);
                ListaUtil.setListViewHeightBasedOnChildren(comunesList);
                //Log.d(TAG, "Value is: " + usersHashMap.toString());
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            Toast.makeText(VerResultados.this, "Fallo al descargar el calendario.\n " +
                            "¿Estás conectado a internet?.",
                    Toast.LENGTH_LONG).show();
        }
    };

    private void leerDiasComunes(HashMap<String, Object> usersHashMap) {
        int bandera = 0;
        for (String key : usersHashMap.keySet()){
            diasNoBorrar = new ArrayList<String>();
            ArrayList<String> selectedDates = (ArrayList<String>)usersHashMap.get(key);
            bandera++;
            for (String value : selectedDates){
                if(bandera != 1) {
                    comprobarDia(value);
                } else{
                    diasComunesTemp.add(value);
                }
            }
            if (bandera != 1){
                diasComunesTemp = diasNoBorrar;
            }
        }
    }

    private void crearListView(HashMap<String, Object> usersHashMap) {
        TextView anteriorUsuarioNombre = new TextView(this);
        TextView usuarioNombre;

        int bandera = 55;
        for (String key : usersHashMap.keySet()){
            usuarioNombre = new TextView(this);
            usuarioNombre.setText(key + " :");
            usuarioNombre.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);


            // Poner el textView debajo de otro
            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            if (bandera == 55){
                anteriorUsuarioNombre.setText(key + " :");
                anteriorUsuarioNombre.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

                p.addRule(RelativeLayout.BELOW, R.id.listcomunesview);

                int dp = (int) (16 * Resources.getSystem().getDisplayMetrics().density);
                p.setMargins(0, 65, 0, dp);
                anteriorUsuarioNombre.setLayoutParams(p);
                anteriorUsuarioNombre.setId(bandera);
                layout.addView(anteriorUsuarioNombre);

                bandera = crearListView(usersHashMap, key, anteriorUsuarioNombre, bandera);
                // Hemos creado una nueva lista con id bandera

            } else{
                p.addRule(RelativeLayout.BELOW, bandera);
                int dp = (int) (16 * Resources.getSystem().getDisplayMetrics().density);
                p.setMargins(0, dp, 0, dp);

                bandera++;
                usuarioNombre.setLayoutParams(p);
                layout.addView(usuarioNombre);
                anteriorUsuarioNombre = usuarioNombre;
                anteriorUsuarioNombre.setId(bandera);

                bandera = crearListView(usersHashMap, key, usuarioNombre, bandera);
            }
        }
    }

    private int crearListView(HashMap<String, Object> usersHashMap, String key, TextView textView,
                               int bandera) {

        RelativeLayout.LayoutParams paramsList = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        ArrayList<FechaCursor> diasUsuarioCursor = new ArrayList<FechaCursor>();
        diasUsuarioCursor = guardarEnAdapter((ArrayList<String>)usersHashMap.get(key));

        DiasComunesAdapter diasUsuarioAdapter = new DiasComunesAdapter(context, diasUsuarioCursor);
        diasUsuarioAdapter.setDiasComunes(diasComunesArray);
        ListView listaUsuario = new ListView(this);

        listaUsuario.setAdapter(diasUsuarioAdapter);
        paramsList.addRule(RelativeLayout.BELOW, bandera);
        listaUsuario.setLayoutParams(paramsList);

        Drawable divider = getResources().getDrawable(R.drawable.divider);
        listaUsuario.setDivider(divider);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        int dp = (int) (16 * Resources.getSystem().getDisplayMetrics().density);
        listaUsuario.setDividerHeight(dp);

        bandera++;
        listaUsuario.setId(bandera);

        layout.addView(listaUsuario);
        ListaUtil.setListViewHeightBasedOnChildren(listaUsuario);

        return bandera;
    }

    private ArrayList<FechaCursor> guardarEnAdapter(List<String> diasComunesTemp) {
        ArrayList<FechaCursor> diasCursorTemp = new ArrayList<FechaCursor>();

        if (isSelectedDatesNotEmpty(diasComunesTemp)) {
            for (String diaComun : diasComunesTemp) {
                String[] split = diaComun.split("/");

                Integer day = Integer.parseInt(split[0]);
                Integer month = Integer.parseInt(split[1]);
                Integer year = Integer.parseInt(split[2]);

                Calendar c = Calendar.getInstance();
                c.set(year, month, day, 0, 0);

                String stringMonth = monthToString(month);
                String stringWeekDay = getWeekDay(c.get(Calendar.DAY_OF_WEEK));
                FechaCursor tmpFecha = new FechaCursor(day, stringMonth, year, stringWeekDay);

                diasCursorTemp.add(tmpFecha);
            }
        }
        return diasCursorTemp;
    }

    private String getWeekDay(int weekDay) {
        switch(weekDay){
            case 1: return getResources().getString(R.string.domingo);
            case 2: return getResources().getString(R.string.lunes);
            case 3: return getResources().getString(R.string.martes);
            case 4: return getResources().getString(R.string.miercoles);
            case 5: return getResources().getString(R.string.jueves);
            case 6: return getResources().getString(R.string.viernes);
            case 7: return getResources().getString(R.string.sabado);
            default:
                //Log.e("", "No existe dicho mes");
                return "error";
        }
    }

    private boolean isSelectedDatesNotEmpty(List<String> selectedDates) {
        if(!selectedDates.isEmpty()) {
            if(selectedDates.get(0).equals(getResources().getString(R.string.sin_seleccionar_dia))){
                if(selectedDates.size() == 1)
                    return false;
                else {
                    selectedDates.remove(0);
                    return true;
                }
            } else
                return true;
        }
        return false;
    }

    private Boolean comprobarDia(String value) {

        for(String diaComun : diasComunesTemp){
            if(diaComun.equals(value)){
                diasNoBorrar.add(value);
                return true;
            }
        }
        return false;
    }

    private String monthToString(Integer month) {
        switch(month){
            case 0: return getResources().getString(R.string.enero);
            case 1: return getResources().getString(R.string.febrero);
            case 2: return getResources().getString(R.string.marzo);
            case 3: return getResources().getString(R.string.abril);
            case 4: return getResources().getString(R.string.mayo);
            case 5: return getResources().getString(R.string.junio);
            case 6: return getResources().getString(R.string.julio);
            case 7: return getResources().getString(R.string.agosto);
            case 8: return getResources().getString(R.string.septiembre);
            case 9: return getResources().getString(R.string.octubre);
            case 10: return getResources().getString(R.string.noviembre);
            case 11: return getResources().getString(R.string.diciembre);
            default:
                //Log.e("", "No existe dicho mes");
                return "error";
        }
    }

    /* LISTENERS */
    final View.OnClickListener volverMenuPrincipalListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                //Log.d("TAG", "The interstitial wasn't loaded yet.");
                //Declaro el Intent.
                Intent explicit_intent;
                //Instanciamos el Intent dandole:
                explicit_intent = new Intent(VerResultados.this, PantallaInicial.class);
                startActivity(explicit_intent);
            }
        }
    };

    AdListener interstitialADListener = new AdListener(){
        @Override
        public void onAdClosed() {
            // Code to be executed when when the interstitial ad is closed.
            //Declaro el Intent.
            Intent explicit_intent;
            //Instanciamos el Intent dandole:
            explicit_intent = new Intent(VerResultados.this, PantallaInicial.class);
            startActivity(explicit_intent);
        }
    };
}
