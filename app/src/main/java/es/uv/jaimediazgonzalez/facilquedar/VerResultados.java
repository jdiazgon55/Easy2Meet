package es.uv.jaimediazgonzalez.facilquedar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VerResultados extends AppCompatActivity {
    private static final String TAG = "VerResultados";

    private Button listo;
    private ListView comunesList;
    ArrayList<FechaCursor> diasComunesArray;
    private ArrayList<String> horasSeleccionadas;
    private DiasComunesAdapter diasComunesAdapter;
    Context context;
    private ArrayList<String> diasSeleccionados;
    private String nombreUsuario, codigoUnico;

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
        usuarioActual.setValue(diasSeleccionados);

        users.addValueEventListener(retrieveUsersDataListener);

        comunesList = (ListView) findViewById(R.id.listcomunesview);

        /* LISTENERS */
        listo.setOnClickListener(volverMenuPrincipalListener);

    }

    ValueEventListener retrieveUsersDataListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // Get Post object and use the values to update the UI
            HashMap<String, Object> usersHashMap = (HashMap<String, Object>)dataSnapshot.getValue();
            diasComunesArray = new ArrayList<FechaCursor>();
            for (String key : usersHashMap.keySet()){
                ArrayList<String> selectedDates = (ArrayList<String>)usersHashMap.get(key);
                for (String value : selectedDates){
                    String[] split = value.split("/");
                    Integer day = Integer.parseInt(split[0]);
                    Integer month = Integer.parseInt(split[1]);
                    Integer year = Integer.parseInt(split[2]);
                    String stringMonth = monthToString(month);
                    FechaCursor tmpFecha = new FechaCursor(day, stringMonth, year);
                    diasComunesArray.add(tmpFecha);
                }
            }
            diasComunesAdapter = new DiasComunesAdapter(context, diasComunesArray);
            comunesList.setAdapter(diasComunesAdapter);
            Log.d(TAG, "Value is: " + usersHashMap.toString());
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            Toast.makeText(VerResultados.this, "Fallo al descargar el calendario.\n " +
                            "¿Estás conectado a internet?.",
                    Toast.LENGTH_LONG).show();
        }
    };

    private String monthToString(Integer month) {
        switch(month){
            case 1: return getResources().getString(R.string.enero);
            case 2: return getResources().getString(R.string.febrero);
            case 3: return getResources().getString(R.string.marzo);
            case 4: return getResources().getString(R.string.abril);
            case 5: return getResources().getString(R.string.mayo);
            case 6: return getResources().getString(R.string.junio);
            case 7: return getResources().getString(R.string.julio);
            case 8: return getResources().getString(R.string.agosto);
            case 9: return getResources().getString(R.string.septiembre);
            case 10: return getResources().getString(R.string.octubre);
            case 11: return getResources().getString(R.string.noviembre);
            case 12: return getResources().getString(R.string.diciembre);
            default:
                Log.e("", "No existe dicho mes");
                return "error";
        }
    }

    /* LISTENERS */
    final View.OnClickListener volverMenuPrincipalListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            //Declaro el Intent.
            Intent explicit_intent;
            //Instanciamos el Intent dandole:
            explicit_intent = new Intent(VerResultados.this, PantallaInicial.class);
            startActivity(explicit_intent);
        }
    };

}
