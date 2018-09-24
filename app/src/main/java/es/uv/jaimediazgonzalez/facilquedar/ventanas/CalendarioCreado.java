package es.uv.jaimediazgonzalez.facilquedar.ventanas;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.UUID;

import es.uv.jaimediazgonzalez.facilquedar.CalendarioObjeto;
import es.uv.jaimediazgonzalez.facilquedar.R;

/**
 * Created by Familia Diaz on 24/12/2017.
 */

public class CalendarioCreado extends AppCompatActivity {

    private Button aceptar, copiar, compartir;
    private String codigoUnico;
    private EditText codigo;
    private ArrayList<String> horasSeleccionadas;
    private ArrayList<String> diasSeleccionados;
    private String fechaDesdeString, fechaHastaString, nombreCalendario, apodoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario_creado);

        aceptar = (Button) findViewById(R.id.listoCodigo);
        copiar = (Button) findViewById(R.id.copiarBoton);
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
        DatabaseReference admin = users.child(apodoUsuario);

        admin.setValue(diasSeleccionados);

        /* LISTENERS */
        copiar.setOnClickListener(copiarPortapapeles);
        compartir.setOnClickListener(compartirListener);
        aceptar.setOnClickListener(volverMenuPrincipalListener);

    }


    /* LISTENERS */
    final View.OnClickListener copiarPortapapeles = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            ClipboardManager myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            String textoCodigo;
            textoCodigo = codigo.getText().toString();

            ClipData myClip = ClipData.newPlainText("textoCodigo", textoCodigo);
            myClipboard.setPrimaryClip(myClip);

            Toast.makeText(getApplicationContext(), getString(R.string.toast_copiar) ,Toast.LENGTH_SHORT).show();
        }
    };

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
}
