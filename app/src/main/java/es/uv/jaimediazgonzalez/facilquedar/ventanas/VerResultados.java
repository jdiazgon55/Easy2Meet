package es.uv.jaimediazgonzalez.facilquedar.ventanas;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.uv.jaimediazgonzalez.facilquedar.listas.DiasComunesAdapter;
import es.uv.jaimediazgonzalez.facilquedar.listas.FechaCursor;
import es.uv.jaimediazgonzalez.facilquedar.R;

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
        this.setDynamicHeight(comunesList);
        comunesList.setEmptyView(findViewById(R.id.emptyElement));

        layout = (RelativeLayout) findViewById(R.id.relative_layout);

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

            leerDiasComunes(usersHashMap);
            guardarEnAdapter(diasComunesTemp);
            crearListaDiasUsuario(usersHashMap);

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

    private void crearListaDiasUsuario(HashMap<String, Object> usersHashMap) {
        TextView anteriorUsuarioNombre = new TextView(this);
        TextView usuarioNombre;

        int bandera = 55;
        for (String key : usersHashMap.keySet()){
            usuarioNombre = new TextView(this);
            usuarioNombre.setText(key);
            // Poner el textView debajo de otro
            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            if (bandera == 55){
                anteriorUsuarioNombre.setText(key);
                p.addRule(RelativeLayout.BELOW, R.id.listcomunesview);
                anteriorUsuarioNombre.setLayoutParams(p);
                anteriorUsuarioNombre.setId(bandera);
                layout.addView(anteriorUsuarioNombre);

            } else{
                p.addRule(RelativeLayout.BELOW, anteriorUsuarioNombre.getId());
                usuarioNombre.setLayoutParams(p);
                layout.addView(usuarioNombre);
                anteriorUsuarioNombre = usuarioNombre;
                anteriorUsuarioNombre.setId(bandera);
            }

            bandera++;
        }
    }

    private void guardarEnAdapter(List<String> diasComunesTemp) {
        for(String diaComun : this.diasComunesTemp){
            String[] split = diaComun.split("/");

            Integer day = Integer.parseInt(split[0]);
            Integer month = Integer.parseInt(split[1]);
            Integer year = Integer.parseInt(split[2]);

            String stringMonth = monthToString(month);
            FechaCursor tmpFecha = new FechaCursor(day, stringMonth, year);

            diasComunesArray.add(tmpFecha);
        }
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
                Log.e("", "No existe dicho mes");
                return "error";
        }
    }

    /**
     * Set listview height based on listview children
     *
     * @param listView
     */
    public static void setDynamicHeight(ListView listView) {
        ListAdapter adapter = listView.getAdapter();
        //check adapter if null
        if (adapter == null) {
            return;
        }
        int height = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            height += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams layoutParams = listView.getLayoutParams();
        layoutParams.height = height + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(layoutParams);
        listView.requestLayout();
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
