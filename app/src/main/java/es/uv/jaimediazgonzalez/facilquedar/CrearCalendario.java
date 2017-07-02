package es.uv.jaimediazgonzalez.facilquedar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Familia Diaz on 27/06/2017.
 */

public class CrearCalendario  extends AppCompatActivity {
    private Button listo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_calendario);

        listo = (Button) findViewById(R.id.listo);
        /* LISTENERS */
        listo.setOnClickListener(listoCalendarioListener);
    }

    public void onStart(){
        super.onStart();
        DateDialog dateDialogDesde = new DateDialog(this, R.id.desdeFechaPicker);
        DateDialog dateDialogHasta = new DateDialog(this, R.id.hastaFechaPicker);
    }

    /* LISTENERS */
    final View.OnClickListener listoCalendarioListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            //Declaro el Intent
            Intent explicit_intent;
            //Instanciamos el Intent dandole:
            explicit_intent = new Intent(CrearCalendario.this, Calendario.class);
            startActivity(explicit_intent);
        }
    };
}
