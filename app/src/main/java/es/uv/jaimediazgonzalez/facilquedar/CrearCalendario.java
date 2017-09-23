package es.uv.jaimediazgonzalez.facilquedar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;

/**
 * Created by Familia Diaz on 27/06/2017.
 */

public class CrearCalendario  extends AppCompatActivity {
    private Button listo;
    private EditText fechaDesde, fechaHasta;
    private Spinner horaInicial, horaFinal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_calendario);

        listo = (Button) findViewById(R.id.listo);
        listo.setEnabled(false);
        listo.setBackgroundColor(Color.parseColor("#D3D3D3"));

        horaInicial = (Spinner) findViewById(R.id.spinnerHoraCalendarioInicial);
        horaFinal = (Spinner) findViewById(R.id.spinnerHoraCalendarioFinal);
        //Ponemos hora final por defecto
        horaFinal.setSelection(23);

        fechaDesde = (EditText) findViewById(R.id.desdeFechaPicker);
        fechaHasta = (EditText) findViewById(R.id.hastaFechaPicker);

        /* LISTENERS */
        listo.setOnClickListener(listoCalendarioListener);
        fechaDesde.addTextChangedListener(textWatcher);
        fechaHasta.addTextChangedListener(textWatcher);
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

            //Metemos las fechas en el intent
            explicit_intent.putExtra("fechaDesde", fechaDesde.getText().toString());
            explicit_intent.putExtra("fechaHasta", fechaHasta.getText().toString());
            explicit_intent.putExtra("horaInicial", horaInicial.getSelectedItem().toString());
            explicit_intent.putExtra("horaFinal", horaFinal.getSelectedItem().toString());

            startActivity(explicit_intent);
        }
    };

    //TextWatcher para los editText de fecha desde y hasta
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3)
        {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            comprobarFechaDesdeHasta();
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    //Si tiene fecha válida, entonces podemos activar el botón.
    private void comprobarFechaDesdeHasta() {
        String s1 = fechaDesde.getText().toString();
        String s2 = fechaHasta.getText().toString();

        if(s1.equals("") && s2.equals(""))
        {
            listo.setEnabled(false);
            listo.setBackgroundColor(Color.parseColor("#D3D3D3"));
        }

        else if(!s1.equals("")&&s2.equals("")){
            listo.setEnabled(false);
            listo.setBackgroundColor(Color.parseColor("#D3D3D3"));
        }

        else if(!s2.equals("")&&s1.equals(""))
        {
            listo.setEnabled(false);
            listo.setBackgroundColor(Color.parseColor("#D3D3D3"));
        }

        else
        {
            listo.setEnabled(true);
            listo.setBackgroundColor(Color.parseColor("#0D98FF"));
        }
    }
}
