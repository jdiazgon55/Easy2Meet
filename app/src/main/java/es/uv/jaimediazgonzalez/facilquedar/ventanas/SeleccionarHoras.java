package es.uv.jaimediazgonzalez.facilquedar.ventanas;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Arrays;

import es.uv.jaimediazgonzalez.facilquedar.MultiSpinner;
import es.uv.jaimediazgonzalez.facilquedar.R;

/**
 * Created by Familia Diaz on 28/10/2017.
 */

public class SeleccionarHoras extends AppCompatActivity implements MultiSpinner.MultiSpinnerListener {

    private Button guardarHoras;
    private Handler h;
    private MultiSpinner multiSpinner;
    private ArrayList<String> listaHorasSeleccionadas = new ArrayList<String>();
    private ArrayList<String> horasArray = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_horas);
        h = new Handler();

        //Recogemos los datos del Intent
        horasArray = getIntent().getStringArrayListExtra("spinnerArray");
        listaHorasSeleccionadas = horasArray;

        guardarHoras = (Button) findViewById(R.id.guardarHoras);

        /* LISTENERS */
        guardarHoras.setOnClickListener(crearCalendarioListener);

        multiSpinner = (MultiSpinner) findViewById(R.id.multispinner);
        multiSpinner.setItems(horasArray, getString(R.string.string_spinner_horas_calendario), this);

        // Abrimos el spinner automáticamente
        // Spawn a thread that triggers the Spinner to open after 0.35 seconds...
        new Thread(new Runnable() {
            public void run() {
                // DO NOT ATTEMPT TO DIRECTLY UPDATE THE UI HERE, IT WON'T WORK!
                // YOU MUST POST THE WORK TO THE UI THREAD'S HANDLER
                h.postDelayed(new Runnable() {
                    public void run() {
                        // Open the Spinner...
                        multiSpinner.performClick();
                    }
                }, 350);
            }
        }).start();
        multiSpinner.setOnItemSelectedListener(new itemSelectedListener());
    }

    /* LISTENERS */
    final View.OnClickListener crearCalendarioListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.putExtra("listaHorasSeleccionadas", listaHorasSeleccionadas);
            setResult(RESULT_OK, intent);
            finish();
        }
    };

    public class itemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            String selected = parent.getItemAtPosition(pos).toString();
            if (selected.equals("Elige horario")){
                listaHorasSeleccionadas = horasArray;
            }
            else
                listaHorasSeleccionadas =  new ArrayList<String>(Arrays.asList(selected.split(", ")));
        }

        public void onNothingSelected(AdapterView parent) {
            // Do nothing.
        }
    }

    @Override
    public void onItemsSelected(boolean[] selected) {

    }
}
