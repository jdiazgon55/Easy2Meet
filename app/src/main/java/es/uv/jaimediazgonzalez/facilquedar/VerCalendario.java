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

/**
 * Created by Familia Diaz on 25/12/2017.
 */

public class VerCalendario extends AppCompatActivity {
    private Button aceptar;
    private EditText codigo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_calendario);

        aceptar = (Button) findViewById(R.id.listoCodigoVer);
        codigo = (EditText) findViewById(R.id.codigoVer);

        codigo.addTextChangedListener(textWatcher);
        /* LISTENERS */
        aceptar.setOnClickListener(volverMenuPrincipalListener);

        aceptar.setEnabled(false);
        aceptar.setBackgroundColor(Color.parseColor("#D3D3D3"));
    }

    /* LISTENERS */
    final View.OnClickListener volverMenuPrincipalListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            //Declaro el Intent
            Intent explicit_intent;
            //Instanciamos el Intent dandole:
            explicit_intent = new Intent(VerCalendario.this, CalendarioRecibido.class);
            explicit_intent.putExtra("codigoUnico", codigo.getText().toString());
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
            comprobarCodigo();
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    //Si hay algo escrito, entonces continuar
    private void comprobarCodigo() {
        String s1 = codigo.getText().toString();

        if(!s1.equals(""))
        {
            aceptar.setEnabled(true);
            aceptar.setBackgroundColor(Color.parseColor("#0D98FF"));

        }

    }
}
