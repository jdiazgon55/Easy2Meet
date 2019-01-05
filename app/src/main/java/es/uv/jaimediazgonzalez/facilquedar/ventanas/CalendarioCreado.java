package es.uv.jaimediazgonzalez.facilquedar.ventanas;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import es.uv.jaimediazgonzalez.facilquedar.CalendarioObjeto;
import es.uv.jaimediazgonzalez.facilquedar.R;
import es.uv.jaimediazgonzalez.facilquedar.basedatos.EventoDbHelper;
import es.uv.jaimediazgonzalez.facilquedar.listas.Evento;

/**
 * Created by Familia Diaz on 24/12/2017.
 */

public class CalendarioCreado extends AppCompatActivity {

    private Button aceptar, compartir;
    private String codigoUnico;
    private EditText codigo;
    private ArrayList<String> horasSeleccionadas;
    private ArrayList<String> diasSeleccionados;
    private String fechaDesdeString, fechaHastaString, nombreCalendario, apodoUsuario;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario_creado);

        aceptar = (Button) findViewById(R.id.listoCodigo);
        compartir = (Button) findViewById(R.id.compartirBoton);
        codigo = (EditText) findViewById(R.id.codigo);

        //horasSeleccionadas = getIntent().getStringArrayListExtra("horasSeleccionadas");
        //Recogemos los datos del Intent
        fechaDesdeString = getIntent().getStringExtra("fechaDesde");
        fechaHastaString = getIntent().getStringExtra("fechaHasta");
        nombreCalendario = getIntent().getStringExtra("nombreCalendario");
        apodoUsuario = getIntent().getStringExtra("apodoUsuario");
        diasSeleccionados = getIntent().getStringArrayListExtra("diasSeleccionados");

        codigoUnico = createUniqueId();
        codigo.setText(codigoUnico);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(codigoUnico);

        DatabaseReference propiedadesCalendario = myRef.child("PropiedadesCalendario");
        CalendarioObjeto calendarioObjeto = new CalendarioObjeto(nombreCalendario, fechaDesdeString,
                fechaHastaString);
        propiedadesCalendario.setValue(calendarioObjeto);

        DatabaseReference users = myRef.child("Users");
        DatabaseReference userReference = users.child(apodoUsuario);

        if(diasSeleccionados.isEmpty()){
            diasSeleccionados.add(getResources().getString(R.string.sin_seleccionar_dia));
        }

        userReference.setValue(diasSeleccionados);

        ejecutarGuardarEventoAsync(nombreCalendario, apodoUsuario);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-4541521919567374/8544259144");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(interstitialADListener);

        /* LISTENERS */
        compartir.setOnClickListener(compartirListener);
        aceptar.setOnClickListener(volverMenuPrincipalListener);

    }


    /* LISTENERS */
    final View.OnClickListener compartirListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = codigoUnico;
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        }
    };

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
                explicit_intent = new Intent(CalendarioCreado.this, PantallaInicial.class);
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
            explicit_intent = new Intent(CalendarioCreado.this, PantallaInicial.class);
            startActivity(explicit_intent);
        }
    };

    public String createUniqueId() {
        String randId = UUID.randomUUID().toString();
        return randId;
    }

    public void ejecutarGuardarEventoAsync(final String nombreEvento, final String nombreCreador){
        new Thread(new Runnable(){
            @Override
            public void run(){
                guardarEventoBaseDatos(nombreEvento, nombreCreador);
            }
        }).start();
    }

    private void guardarEventoBaseDatos(String nombreEvento, String nombreCreador){
        EventoDbHelper baseDatosEventos = new EventoDbHelper(getApplicationContext());
        int id = baseDatosEventos.getUltimoId() + 1;
        Evento tmpEvento = new Evento(nombreEvento, codigoUnico, nombreCreador, nombreCreador, id);
        baseDatosEventos.InsertarEvento(tmpEvento);
        return;
    }
}
