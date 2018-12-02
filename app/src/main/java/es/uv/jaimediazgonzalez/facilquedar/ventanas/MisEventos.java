package es.uv.jaimediazgonzalez.facilquedar.ventanas;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import es.uv.jaimediazgonzalez.facilquedar.R;
import es.uv.jaimediazgonzalez.facilquedar.basedatos.EventoDbHelper;
import es.uv.jaimediazgonzalez.facilquedar.listas.Evento;
import es.uv.jaimediazgonzalez.facilquedar.listas.EventosCursorAdapter;

public class MisEventos extends AppCompatActivity {
    private static final String TAG = "MisEventos";

    private ListView eventosList;
    private EventosCursorAdapter eventosCursorAdapter;
    Context context;
    private EventoDbHelper baseDatosEventos;
    private boolean todoCorrecto = false;
    private String currentSelectedCodeEvent;
    private String currentSelectedUserName;

    private FirebaseDatabase database ;
    private DatabaseReference usersDataReference;

    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;

    FrameLayout progressBarHolder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_eventos);

        context = this.getApplicationContext();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        eventosList = (ListView) findViewById(R.id.listeventosview);
        eventosList.setEmptyView(findViewById(R.id.emptyElementEventos));
        registerForContextMenu(eventosList);

        baseDatosEventos = new EventoDbHelper(getApplicationContext());
        Cursor eventos = baseDatosEventos.getTodosEventos();
        eventosCursorAdapter = new EventosCursorAdapter(this.getApplicationContext(), eventos);
        eventosList.setAdapter(eventosCursorAdapter);
        eventosList.setOnItemClickListener(onItemClickListener);
    }

    /**
     * MENU
     */

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.listeventosview) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu, menu);
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if (id == R.id.botonBorrar) {
            final Evento eventoTmp = eventosCursorAdapter.getEvento(info.position);
            AlertDialog.Builder adb=new AlertDialog.Builder(MisEventos.this);

            // Si el creador intenta eliminar un evento suyo, lo borramos de la base de datos
            if(eventoTmp.getNombreCreador().equals(eventoTmp.getNombreUsuario())){
                creadorBorraEvento(info, eventoTmp, adb);
            } else {
                usuarioBorraEvento(info, adb);
            }
            adb.show();

            return true;
        } else if (id == R.id.botonCompartir){
            final Evento eventoTmp = eventosCursorAdapter.getEvento(info.position);
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = eventoTmp.getCodigoEvento();
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        }

        closeContextMenu();
        return super.onContextItemSelected(item);
    }

    private void usuarioBorraEvento(final AdapterView.AdapterContextMenuInfo info, AlertDialog.Builder adb) {
        adb.setTitle(R.string.borrar_evento);
        adb.setMessage(R.string.eliminar_evento_confirmacion);

        adb.setNegativeButton(R.string.cancelar_borrado_evento, null);
        adb.setPositiveButton(R.string.aceptar_borrado_evento, new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                eliminarEventoBaseDatos(info.position);

                closeContextMenu();
            }
        });
    }

    private void creadorBorraEvento(final AdapterView.AdapterContextMenuInfo info, final Evento eventoTmp, AlertDialog.Builder adb) {
        adb.setTitle(R.string.borrar_evento);
        adb.setMessage(R.string.eliminar_evento_confirmacion_todos);
        adb.setNegativeButton(R.string.cancelar_borrado_evento, null);
        adb.setPositiveButton(R.string.aceptar_borrado_evento, new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(isOnline()) {
                    database = FirebaseDatabase.getInstance();
                    DatabaseReference referenceEvento = database.getReference(
                            eventoTmp.getCodigoEvento());
                    referenceEvento.removeValue();

                    eliminarEventoBaseDatos(info.position);
                } else {
                    String advertenciaNoInternet = getResources().getString(R.string.advertencia_no_internet);
                    Toast.makeText(MisEventos.this, advertenciaNoInternet,
                            Toast.LENGTH_LONG).show();
                }
                closeContextMenu();
            }
        });
    }

    final AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

            Cursor cursorEvento = (Cursor) adapterView.getItemAtPosition(position);
            // Cogemos el código del evento desde el cursor
            setCurrentSelectedCodeEvent(
                    cursorEvento.getString(cursorEvento.getColumnIndex("codigoEvento")));

            setCurrentSelectedUserName(cursorEvento.getString(cursorEvento.getColumnIndex("nombreUsuario")));

            progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);
            inAnimation = new AlphaAnimation(0f, 1f);
            inAnimation.setDuration(200);
            progressBarHolder.setAnimation(inAnimation);
            progressBarHolder.setVisibility(View.VISIBLE);

            if(isOnline()){
                database = FirebaseDatabase.getInstance();
                usersDataReference = database.getReference().child(getCurrentSelectedCodeEvent()).child("Users");
                usersDataReference.addListenerForSingleValueEvent(retrieveUsersDataListener);
            } else{
                String advertenciaNoInternet = getResources().getString(R.string.advertencia_no_internet);
                Toast.makeText(MisEventos.this, advertenciaNoInternet,
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
                String nombreUsuarioString = getCurrentSelectedUserName();
                todoCorrecto = true;
                outAnimation = new AlphaAnimation(1f, 0f);
                outAnimation.setDuration(200);
                progressBarHolder.setAnimation(outAnimation);
                progressBarHolder.setVisibility(View.GONE);
                if (todoCorrecto) {
                    //Declaro el Intent
                    Intent explicit_intent;
                    //Instanciamos el Intent dandole:
                    explicit_intent = new Intent(MisEventos.this, CalendarioRecibido.class);
                    explicit_intent.putExtra("codigoUnico", getCurrentSelectedCodeEvent());
                    explicit_intent.putExtra("nombreUsuario", nombreUsuarioString);
                    explicit_intent.putExtra("usuariosHash", usersHashMap);

                    startActivity(explicit_intent);
                }
            } else{
                String advertenciaNoCalendario = getResources().getString(R.string.advertencia_no_calendario);
                Toast.makeText(MisEventos.this, advertenciaNoCalendario,
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
            Toast.makeText(MisEventos.this, "Fallo al descargar el calendario.\n " +
                            "¿Estás conectado a internet?.",
                    Toast.LENGTH_SHORT).show();
        }
    };

    private void eliminarEventoBaseDatos(int position){
        int idBorrar = eventosCursorAdapter.getIdEvento(position);
        baseDatosEventos.BorrarEvento(idBorrar);

        Cursor eventos = baseDatosEventos.getTodosEventos();
        eventosCursorAdapter.swapCursor(eventos);

        return;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public String getCurrentSelectedCodeEvent() {
        return currentSelectedCodeEvent;
    }

    public void setCurrentSelectedCodeEvent(String currentSelectedCodeEvent) {
        this.currentSelectedCodeEvent = currentSelectedCodeEvent;
    }

    public String getCurrentSelectedUserName() {
        return currentSelectedUserName;
    }

    public void setCurrentSelectedUserName(String currentSelectedUserName) {
        this.currentSelectedUserName = currentSelectedUserName;
    }
}

