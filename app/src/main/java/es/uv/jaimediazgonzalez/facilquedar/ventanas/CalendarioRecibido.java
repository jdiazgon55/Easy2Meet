package es.uv.jaimediazgonzalez.facilquedar.ventanas;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import es.uv.jaimediazgonzalez.facilquedar.CalendarioObjeto;
import es.uv.jaimediazgonzalez.facilquedar.EventDecorator;
import es.uv.jaimediazgonzalez.facilquedar.R;
import es.uv.jaimediazgonzalez.facilquedar.basedatos.EventoDbHelper;
import es.uv.jaimediazgonzalez.facilquedar.listas.Evento;

/**
 * Created by Familia Diaz on 02/07/2017.
 */


public class CalendarioRecibido extends AppCompatActivity implements OnDateSelectedListener {

    private static final String TAG = "CalendarioRecibido";

    private MaterialCalendarView calendario;
    private EventDecorator eventDecoratorComunes, eventDecoratorSeleccionados;
    private CalendarDay currentSelectedDate = new CalendarDay();

    private FirebaseDatabase database;
    private DatabaseReference usersDataReference, propiedadesCalendarioReference;

    private Button guardar;
    HashMap<String, Object> usersHashMapRecogido;
    private Boolean isFirstTimeUsers = true, isFirstTimeProperties = true;
    private TextView nombreCalendario;
    private Date fechaDesde, fechaHasta;
    private String codigoUnico,
            nombreUsuario;
    private ArrayList<String> diasComunesTemp;
    private ArrayList<String> diasNoBorrar;
    private ArrayList<String> listaHorasSeleccionadas;
    /*
    private final ArrayList<String> listaHoras = new ArrayList<String>(
            Arrays.asList("00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00"
                    , "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00"
                    , "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00"
                    , "21:00", "22:00", "23:00"));
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);

        //Recogemos los datos del Intent
        codigoUnico = getIntent().getStringExtra("codigoUnico");
        nombreUsuario = getIntent().getStringExtra("nombreUsuario");
        usersHashMapRecogido = (HashMap<String, Object>)getIntent().getSerializableExtra("usuariosHash");

        nombreCalendario = (TextView) findViewById(R.id.text_view_mantener_dia);
        calendario = (MaterialCalendarView) findViewById(R.id.calendarioView);
        calendario.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);

        //Ponemos el rango de fechas al calendario
        calendario.state().edit()
                .setMinimumDate(CalendarDay.from(fechaDesde))
                .setMaximumDate(CalendarDay.from(fechaHasta))
                .commit();

        this.mostrarDatosCalendario(usersHashMapRecogido);

        calendario.setOnDateChangedListener(this);

        guardar = (Button) findViewById(R.id.guardarCalendario);

        guardar.setOnClickListener(guardarDatosCalendario);

        database = FirebaseDatabase.getInstance();

        usersDataReference = database.getReference().child(codigoUnico).child("Users");
        usersDataReference.addValueEventListener(retrieveUsersDataListener);

        propiedadesCalendarioReference = database.getReference().child(codigoUnico)
                .child("PropiedadesCalendario");
        propiedadesCalendarioReference.addListenerForSingleValueEvent
                (retrievePropiedadesCalendarioDataListener);
    }

    // Recoge los datos pasados por la otra ventana y los muestra
    private void mostrarDatosCalendario(HashMap<String, Object> usersHashMap){
        List<CalendarDay> diasSeleccionados = new ArrayList<CalendarDay>();
        List<CalendarDay> diasComunes = new ArrayList<CalendarDay>();
        diasComunesTemp = new ArrayList<String>();

        leerDiasComunes(usersHashMap);
        for (String key : usersHashMap.keySet()){
            ArrayList<String> selectedDates = (ArrayList<String>)usersHashMap.get(key);
            convertirStringToCalendarDay(diasSeleccionados, selectedDates);
        }

        convertirStringToCalendarDay(diasComunes, diasComunesTemp);
        eventDecoratorSeleccionados = new EventDecorator(Color.rgb(255, 86, 25), diasSeleccionados);
        eventDecoratorComunes = new EventDecorator(Color.rgb(79, 178, 9), diasComunes);

        calendario.addDecorator(eventDecoratorSeleccionados);
        calendario.addDecorator(eventDecoratorComunes);
        //Log.d(TAG, "Value is: " + usersHashMap.toString());
    }

    ValueEventListener retrieveUsersDataListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            List<CalendarDay> diasSeleccionadosUsuario = new ArrayList<CalendarDay>();
            if(isFirstTimeUsers){
                isFirstTimeUsers = false;
                for (String key : usersHashMapRecogido.keySet()){
                    if (key.equals(nombreUsuario)){
                        ArrayList<String> selectedDates = (ArrayList<String>)usersHashMapRecogido.get(key);
                        convertirStringToCalendarDay(diasSeleccionadosUsuario, selectedDates);
                        ponerDiasSeleccionadosUsuario(diasSeleccionadosUsuario);
                    }
                }
                return;
            }
            // Get Post object and use the values to update the UI
            HashMap<String, Object> usersHashMap = (HashMap<String, Object>)dataSnapshot.getValue();
            List<CalendarDay> diasSeleccionadosTodos = new ArrayList<CalendarDay>();
            List<CalendarDay> diasComunes = new ArrayList<CalendarDay>();
            diasComunesTemp = new ArrayList<String>();

            if(usersHashMap != null) {
                leerDiasComunes(usersHashMap);
                for (String key : usersHashMap.keySet()) {
                    ArrayList<String> selectedDates = (ArrayList<String>) usersHashMap.get(key);
                    convertirStringToCalendarDay(diasSeleccionadosTodos, selectedDates);
                }

                convertirStringToCalendarDay(diasComunes, diasComunesTemp);
                eventDecoratorSeleccionados = new EventDecorator(Color.rgb(255, 86, 25), diasSeleccionadosTodos);
                eventDecoratorComunes = new EventDecorator(Color.rgb(79, 178, 9), diasComunes);

                calendario.addDecorator(eventDecoratorSeleccionados);
                calendario.addDecorator(eventDecoratorComunes);
                //Log.d(TAG, "Value is: " + usersHashMap.toString());
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            Toast.makeText(CalendarioRecibido.this, "Fallo al descargar el calendario.\n " +
                            "¿Estás conectado a internet?.",
                    Toast.LENGTH_LONG).show();
        }
    };

    private void ponerDiasSeleccionadosUsuario(List<CalendarDay> diasSeleccionadosUsuario) {
        for (CalendarDay day : diasSeleccionadosUsuario){
            calendario.setDateSelected(day, true);
        }
    }

    private void convertirStringToCalendarDay(List<CalendarDay> diasSeleccionados, ArrayList<String> selectedDates) {
        // La lista necesita dias
        if(isSelectedDatesNotEmpty(selectedDates)) {
            for (String value : selectedDates) {
                String[] split = value.split("/");
                Integer day = Integer.parseInt(split[0]);
                Integer month = Integer.parseInt(split[1]);
                Integer year = Integer.parseInt(split[2]);
                CalendarDay tmpCalendarDay = CalendarDay.from(year, month, day);
                diasSeleccionados.add(tmpCalendarDay);
            }
        }
    }

    private boolean isSelectedDatesNotEmpty(ArrayList<String> selectedDates) {
        if(!selectedDates.isEmpty()) {
            if(selectedDates.get(0).equals(getResources().getString(R.string.sin_seleccionar_dia))){
                if(selectedDates.size() == 1)
                    return false;
                else {
                    selectedDates.remove(0);
                    return true;
                }
            } else
                return true;
        }
        return false;
    }

    ValueEventListener retrievePropiedadesCalendarioDataListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // Get Post object and use the values to update the UI
            HashMap<String, Object> propiedadesCalendarioHashMap = (HashMap<String, Object>)dataSnapshot
                    .getValue();

            CalendarioObjeto calendarioObjeto = new CalendarioObjeto(
                    (String) propiedadesCalendarioHashMap.get("nombreCalendario"),
                    (String) propiedadesCalendarioHashMap.get("fechaDesdeString"),
                    (String) propiedadesCalendarioHashMap.get("fechaHastaString"));
            fechaDesde = calendarioObjeto.getFechaDesdeDate();
            fechaHasta = calendarioObjeto.getFechaHastaDate();
            nombreCalendario.setText(calendarioObjeto.getNombreCalendario());

            if(isFirstTimeProperties){
                ejecutarGuardarEventoAsync(usersHashMapRecogido, calendarioObjeto.getNombreCalendario());
                isFirstTimeProperties = false;
            }

            //Ponemos el rango de fechas al calendario
            calendario.state().edit()
                    .setMinimumDate(CalendarDay.from(fechaDesde))
                    .setMaximumDate(CalendarDay.from(fechaHasta))
                    .commit();

            String advertenciaElegirDia = getResources().getString(R.string.advertencia_elegir_dia);
            Toast.makeText(CalendarioRecibido.this, advertenciaElegirDia,
                    Toast.LENGTH_LONG).show();

            //Log.d(TAG, "Value is: " + propiedadesCalendarioHashMap.toString());
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            Toast.makeText(CalendarioRecibido.this, "Fallo al descargar el calendario.\n " +
                            "¿Estás conectado a internet?.",
                    Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

        //Solo si quieres marcar esa fecha
        if (selected == true) {

            if (calendario.getSelectedDates().size() >= 1){
                guardar.setEnabled(true);
                guardar.setBackgroundColor(Color.parseColor("#0D98FF"));
            }
        }
    }


    /* LISTENERS */
    final View.OnClickListener guardarDatosCalendario = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            //Declaro el Intent
            Intent explicit_intent;
            //Instanciamos el Intent dandole:
            explicit_intent = new Intent(CalendarioRecibido.this, VerResultados.class);

            if (isOnline()) {
                List<CalendarDay> diasSeleccionados = calendario.getSelectedDates();
                diasSeleccionados = ordenarDias(diasSeleccionados);

                ArrayList<String> dias = new ArrayList<String>();
                for (CalendarDay dia : diasSeleccionados) {
                    String fecha = dia.getDay() + "/" + dia.getMonth() + "/" + dia.getYear();
                    dias.add(fecha);
                }
                explicit_intent.putStringArrayListExtra("diasSeleccionados", dias);
                explicit_intent.putExtra("nombreUsuario", nombreUsuario);
                explicit_intent.putExtra("codigoUnico", codigoUnico);
                //explicit_intent.putStringArrayListExtra("horasSeleccionadas", listaHorasSeleccionadas);

                startActivity(explicit_intent);
            }else {
                String advertenciaNoInternet = getResources().getString(R.string.advertencia_no_internet);
                Toast.makeText(CalendarioRecibido.this, advertenciaNoInternet,
                        Toast.LENGTH_LONG).show();
            }
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

    private Boolean comprobarDia(String value) {

        for(String diaComun : diasComunesTemp){
            if(diaComun.equals(value)){
                diasNoBorrar.add(value);
                return true;
            }
        }
        return false;
    }

    private List<CalendarDay> ordenarDias(List<CalendarDay> diasSeleccionados){

        CalendarDay temp;
        List<CalendarDay> listaDiasTemp = new ArrayList<CalendarDay>();
        for (CalendarDay diaTemp : diasSeleccionados){
            listaDiasTemp.add(diaTemp);
        }
        for (int i = 1; i < listaDiasTemp.size(); i++) {
            for(int j = i ; j > 0 ; j--){
                if(listaDiasTemp.get(j).isBefore(listaDiasTemp.get(j-1))){
                    temp = listaDiasTemp.get(j);
                    listaDiasTemp.remove(j);
                    listaDiasTemp.add(j, listaDiasTemp.get(j-1));
                    listaDiasTemp.remove(j-1);
                    listaDiasTemp.add(j-1, temp);
                }
            }
        }
        return listaDiasTemp;
    }

    public void ejecutarGuardarEventoAsync(final HashMap<String, Object> usersHashMap, final String nombreEvento){
        new Thread(new Runnable(){
            @Override
            public void run(){
                guardarEventoBaseDatos(usersHashMap, nombreEvento);
            }
        }).start();
    }

    private void guardarEventoBaseDatos(HashMap<String, Object> usersHashMap, String nombreEvento){
        EventoDbHelper baseDatosEventos = new EventoDbHelper(getApplicationContext());

        String nombreCreador = usersHashMap.keySet().iterator().next();

        Cursor eventos = baseDatosEventos.getEventosByCodigo(codigoUnico);
        // Si no existe ningún evento como este en la base de datos
        if (eventos.getCount() == 0) {
            int id = baseDatosEventos.getUltimoId() + 1;
            Evento tmpEvento = new Evento(nombreEvento, codigoUnico, nombreCreador, nombreUsuario, id);
            baseDatosEventos.InsertarEvento(tmpEvento);
            return;
        }
        return;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
