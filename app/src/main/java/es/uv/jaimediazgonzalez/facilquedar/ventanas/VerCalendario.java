package es.uv.jaimediazgonzalez.facilquedar.ventanas;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import es.uv.jaimediazgonzalez.facilquedar.R;

/**
 * Created by Familia Diaz on 25/12/2017.
 */

public class VerCalendario extends AppCompatActivity {
    private Button guardar;
    private EditText codigo, nombreUsuario;
    private FirebaseDatabase database;
    private DatabaseReference usersDataReference;
    private boolean todoCorrecto = false;

    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;

    FrameLayout progressBarHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_calendario);

        guardar = (Button) findViewById(R.id.listoCodigoVer);
        codigo = (EditText) findViewById(R.id.codigoVer);
        nombreUsuario = (EditText) findViewById(R.id.nombreVer);

        codigo.addTextChangedListener(textWatcher);
        nombreUsuario.addTextChangedListener(textWatcher);
        /* LISTENERS */
        guardar.setOnClickListener(verCalendario);

        guardar.setEnabled(false);
        guardar.setBackgroundColor(Color.parseColor("#D3D3D3"));
    }

    /* LISTENERS */
    final View.OnClickListener verCalendario = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);
            inAnimation = new AlphaAnimation(0f, 1f);
            inAnimation.setDuration(200);
            progressBarHolder.setAnimation(inAnimation);
            progressBarHolder.setVisibility(View.VISIBLE);

            if(isOnline()){
                database = FirebaseDatabase.getInstance();
                usersDataReference = database.getReference().child(codigo.getText().toString()).child("Users");
                usersDataReference.addListenerForSingleValueEvent(retrieveUsersDataListener);
            } else{
                String advertenciaNoInternet = getResources().getString(R.string.advertencia_no_internet);
                Toast.makeText(VerCalendario.this, advertenciaNoInternet,
                        Toast.LENGTH_LONG).show();
                outAnimation = new AlphaAnimation(1f, 0f);
                outAnimation.setDuration(200);
                progressBarHolder.setAnimation(outAnimation);
                progressBarHolder.setVisibility(View.GONE);
            }
        }
    };


    ValueEventListener retrieveUsersDataListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.hasChildren()) {
                HashMap<String, Object> usersHashMap = (HashMap<String, Object>) dataSnapshot.getValue();
                String nombreUsuarioString = nombreUsuario.getText().toString();
                for (String key : usersHashMap.keySet()) {
                    if (key.equals(nombreUsuarioString)) {
                        todoCorrecto = false;
                        String advertenciaUsuario = getResources().getString(R.string.advertencia_usuario_existente);
                        Toast.makeText(VerCalendario.this, advertenciaUsuario,
                                Toast.LENGTH_SHORT).show();
                        outAnimation = new AlphaAnimation(1f, 0f);
                        outAnimation.setDuration(200);
                        progressBarHolder.setAnimation(outAnimation);
                        progressBarHolder.setVisibility(View.GONE);
                        return;
                    }
                }
                todoCorrecto = true;
                outAnimation = new AlphaAnimation(1f, 0f);
                outAnimation.setDuration(200);
                progressBarHolder.setAnimation(outAnimation);
                progressBarHolder.setVisibility(View.GONE);
                if (todoCorrecto) {
                    //Declaro el Intent
                    Intent explicit_intent;
                    //Instanciamos el Intent dandole:
                    explicit_intent = new Intent(VerCalendario.this, CalendarioRecibido.class);
                    explicit_intent.putExtra("codigoUnico", codigo.getText().toString());
                    explicit_intent.putExtra("nombreUsuario", nombreUsuarioString);
                    explicit_intent.putExtra("nombreUsuario", nombreUsuarioString);
                    explicit_intent.putExtra("usuariosHash", usersHashMap);

                    startActivity(explicit_intent);
                }
            } else{
                String advertenciaNoCalendario = getResources().getString(R.string.advertencia_no_calendario);
                Toast.makeText(VerCalendario.this, advertenciaNoCalendario,
                        Toast.LENGTH_LONG).show();
                outAnimation = new AlphaAnimation(1f, 0f);
                outAnimation.setDuration(200);
                progressBarHolder.setAnimation(outAnimation);
                progressBarHolder.setVisibility(View.GONE);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Toast.makeText(VerCalendario.this, "Fallo al descargar el calendario.\n " +
                            "¿Estás conectado a internet?.",
                    Toast.LENGTH_SHORT).show();
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
        String codigoString = codigo.getText().toString();
        String nombreUsuarioString = nombreUsuario.getText().toString();

        if(!codigoString.equals("")&&!nombreUsuarioString.equals(""))
        {
            guardar.setEnabled(true);
            guardar.setBackgroundColor(Color.parseColor("#0D98FF"));
        }
        else{
            guardar.setEnabled(false);
            guardar.setBackgroundColor(Color.parseColor("#D3D3D3"));
        }

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
