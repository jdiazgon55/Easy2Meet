package es.uv.jaimediazgonzalez.facilquedar.ventanas;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import es.uv.jaimediazgonzalez.facilquedar.R;
import es.uv.jaimediazgonzalez.facilquedar.basedatos.EventoDbHelper;
import es.uv.jaimediazgonzalez.facilquedar.listas.EventosCursorAdapter;
import es.uv.jaimediazgonzalez.facilquedar.listas.FechaCursor;

public class MisEventos extends AppCompatActivity {
    private static final String TAG = "MisEventos";

    private ListView eventosList;
    private ArrayList<FechaCursor> eventosArray;
    private EventosCursorAdapter eventosCursorAdapter;
    Context context;
    private String nombreCreador, codigoEvento, nombreEvento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_eventos);

        context = this.getApplicationContext();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //DatabaseReference myRef = database.getReference(codigoUnico);

        eventosList = (ListView) findViewById(R.id.listeventosview);
        eventosList.setEmptyView(findViewById(R.id.emptyElementEventos));

        EventoDbHelper baseDatosEventos = new EventoDbHelper(getApplicationContext());
        Cursor eventos = baseDatosEventos.getTodosEventos();
        eventosCursorAdapter = new EventosCursorAdapter(this.getApplicationContext(), eventos);
        eventosList.setAdapter(eventosCursorAdapter);

        eventosList.setOnItemClickListener(listaEventosListener);
    }

    AdapterView.OnItemClickListener listaEventosListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        }
    };

}

