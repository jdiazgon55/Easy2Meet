package es.uv.jaimediazgonzalez.facilquedar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Familia Diaz on 02/07/2017.
 */


public class CalendarioRecibido extends AppCompatActivity implements OnDateSelectedListener {

    private static final String TAG = "CalendarioRecibido";

    private MaterialCalendarView calendario;
    private EventDecorator eventDecorator;
    private CalendarDay currentSelectedDate = new CalendarDay();

    private FirebaseDatabase database;
    private DatabaseReference usersDataReference, propiedadesCalendarioReference;

    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;

    FrameLayout progressBarHolder;

    private Button guardar;
    private TextView nombreCalendario;
    private Date fechaDesde, fechaHasta;
    private String horaInicial, horaFinal, fechaDesdeString, fechaHastaString, codigoUnico;
    private ArrayList<String> listaHorasSeleccionadas;
    private final ArrayList<String> listaHoras = new ArrayList<String>(
            Arrays.asList("00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00"
                    , "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00"
                    , "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00"
                    , "21:00", "22:00", "23:00"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);

        //Recogemos los datos del Intent
        codigoUnico = getIntent().getStringExtra("codigoUnico");

        nombreCalendario = (TextView) findViewById(R.id.text_view_mantener_dia);
        calendario = (MaterialCalendarView) findViewById(R.id.calendarioView);
        calendario.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);

        //Ponemos el rango de fechas al calendario
        calendario.state().edit()
                .setMinimumDate(CalendarDay.from(fechaDesde))
                .setMaximumDate(CalendarDay.from(fechaHasta))
                .commit();

        calendario.setOnDateChangedListener(this);

        guardar = (Button) findViewById(R.id.guardarCalendario);

        guardar.setOnClickListener(calendarioCreadoListener);

        progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);
        inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressBarHolder.setAnimation(inAnimation);
        progressBarHolder.setVisibility(View.VISIBLE);

        database = FirebaseDatabase.getInstance();

        usersDataReference = database.getReference().child(codigoUnico).child("Users");
        usersDataReference.addValueEventListener(retrieveUsersDataListener);

        propiedadesCalendarioReference = database.getReference().child(codigoUnico)
                .child("PropiedadesCalendario");
        propiedadesCalendarioReference.addListenerForSingleValueEvent
                (retrievePropiedadesCalendarioDataListener);
    }

    ValueEventListener retrieveUsersDataListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // Get Post object and use the values to update the UI
            HashMap<String, Object> usersHashMap = (HashMap<String, Object>)dataSnapshot.getValue();
            List<CalendarDay> diasSeleccionados = new ArrayList<CalendarDay>();
            for (String key : usersHashMap.keySet()){
                ArrayList<String> selectedDates = (ArrayList<String>)usersHashMap.get(key);
                for (String value : selectedDates){
                    String[] split = value.split("/");
                    Integer day = Integer.parseInt(split[0]);
                    Integer month = Integer.parseInt(split[1]);
                    Integer year = Integer.parseInt(split[2]);
                    CalendarDay tmpCalendarDay = CalendarDay.from(day, month, year);
                    diasSeleccionados.add(tmpCalendarDay);
                }
            }
            eventDecorator = new EventDecorator(Color.rgb(13, 255, 92), diasSeleccionados);
            calendario.addDecorator(eventDecorator);
            Log.d(TAG, "Value is: " + usersHashMap.toString());
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            Toast.makeText(CalendarioRecibido.this, "Fallo al descargar el calendario.\n " +
                            "¿Estás conectado a internet?.",
                    Toast.LENGTH_SHORT).show();
        }
    };

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

            //Ponemos el rango de fechas al calendario
            calendario.state().edit()
                    .setMinimumDate(CalendarDay.from(fechaDesde))
                    .setMaximumDate(CalendarDay.from(fechaHasta))
                    .commit();

            outAnimation = new AlphaAnimation(1f, 0f);
            outAnimation.setDuration(200);
            progressBarHolder.setAnimation(outAnimation);
            progressBarHolder.setVisibility(View.GONE);

            Log.d(TAG, "Value is: " + propiedadesCalendarioHashMap.toString());
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
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
    final View.OnClickListener calendarioCreadoListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            //Declaro el Intent
            Intent explicit_intent;
            //Instanciamos el Intent dandole:
            explicit_intent = new Intent(CalendarioRecibido.this, PantallaInicial.class);

            List<CalendarDay> diasSeleccionados = calendario.getSelectedDates();
            ArrayList<String> dias = new ArrayList<String>();
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            for (CalendarDay dia: diasSeleccionados) {
                String fecha = dia.getDay() + "/" + dia.getMonth() + "/" + dia.getYear();
                dias.add(fecha);
            }
            explicit_intent.putStringArrayListExtra("diasSeleccionados", dias);
            //explicit_intent.putStringArrayListExtra("horasSeleccionadas", listaHorasSeleccionadas);

            startActivity(explicit_intent);
        }
    };

    private String getFirstTwoCharacters(String time){
        return time.substring(0,2);
    }

    private Date castStringToDate(String date){
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public CalendarDay getCurrentSelectedDate(){
        return this.currentSelectedDate;
    }

    public void setCurrentSelectedDate(CalendarDay day){
        this.currentSelectedDate = day;
    }
}
