package es.uv.jaimediazgonzalez.facilquedar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Familia Diaz on 24/12/2017.
 */

public class CalendarioCreado extends AppCompatActivity {

    private Button aceptar;
    private EditText codigo;
    private static AtomicLong idCounter = new AtomicLong();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario_creado);

        aceptar = (Button) findViewById(R.id.listoCodigo);
        codigo = (EditText) findViewById(R.id.codigo);

        String codigoUnico = createUniqueId();
        codigo.setText(codigoUnico);
        /* LISTENERS */
        aceptar.setOnClickListener(volverMenuPrincipalListener);

    }

    /* LISTENERS */
    final View.OnClickListener volverMenuPrincipalListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            //Declaro el Intent
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
