package es.uv.jaimediazgonzalez.facilquedar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static android.R.style.Widget;

/**
 * Created by Familia Diaz on 02/07/2017.
 */


public class Calendario extends AppCompatActivity implements OnDateSelectedListener {

    private MaterialCalendarView calendario;
    private Button guardar;
    private Date fechaDesde, fechaHasta;
    private String horaInicial, horaFinal;
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
        String fechaDesdeString = getIntent().getStringExtra("fechaDesde");
        String fechaHastaString = getIntent().getStringExtra("fechaHasta");
        horaInicial = getIntent().getStringExtra("horaInicial");
        horaFinal = getIntent().getStringExtra("horaFinal");

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        try {
            fechaDesde = format.parse(fechaDesdeString);
            fechaHasta = format.parse(fechaHastaString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        calendario = (MaterialCalendarView) findViewById(R.id.calendarioView);
        calendario.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);

        //Ponemos el rango de fechas al calendario
        calendario.state().edit()
                .setMinimumDate(CalendarDay.from(fechaDesde))
                .setMaximumDate(CalendarDay.from(fechaHasta))
                .commit();

        calendario.setOnDateChangedListener(this);

        guardar = (Button) findViewById(R.id.guardarCalendario);
        if (calendario.getSelectedDates().size() < 1) {
            guardar.setEnabled(false);
            guardar.setBackgroundColor(Color.parseColor("#D3D3D3"));
        }
        guardar.setOnClickListener(calendarioCreadoListener);
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

        //Solo si quieres marcar esa fecha
        if (selected == true) {

            //Array con la lista de las horas
            ArrayList<String> spinnerArray = new ArrayList<String>();
            int horaInicialInt, horaFinalInt;

            horaInicialInt = Integer.parseInt(getFirstTwoCharacters(horaInicial));
            horaFinalInt = Integer.parseInt(getFirstTwoCharacters(horaFinal));

            for (int i = horaInicialInt; i <= horaFinalInt; i++) {
                spinnerArray.add(listaHoras.get(i));
            }
            //Declaro el Intent
            Intent explicit_intent;

            //Instanciamos el Intent dandole:
            explicit_intent = new Intent(Calendario.this, SeleccionarHoras.class);

            //Metemos el array de horas en el intent
            explicit_intent.putExtra("spinnerArray", spinnerArray);

            startActivityForResult(explicit_intent, 1);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                ArrayList<String> listaHorasSeleccionadas = data.getStringArrayListExtra("listaHorasSeleccionadas");
                if (calendario.getSelectedDates().size() >= 1){
                    guardar.setEnabled(true);
                    guardar.setBackgroundColor(Color.parseColor("#0D98FF"));
                }
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
            explicit_intent = new Intent(Calendario.this, CalendarioCreado.class);
            startActivity(explicit_intent);
        }
    };

    private String getFirstTwoCharacters(String time){
        return time.substring(0,2);
    }
}
