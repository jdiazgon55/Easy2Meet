package es.uv.jaimediazgonzalez.facilquedar;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.R.style.Widget;

/**
 * Created by Familia Diaz on 02/07/2017.
 */


public class Calendario extends AppCompatActivity implements OnDateSelectedListener {

    private MaterialCalendarView calendario;
    private Date fechaDesde, fechaHasta;
    private String horaInicial, horaFinal;

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
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        LinearLayout layout = new LinearLayout(Calendario.this);

        ArrayList<Integer> spinnerArray = new ArrayList<Integer>();
        int horaInicialInt, horaFinalInt;

        horaInicialInt = Integer.parseInt(getFirstTwoCharacters(horaInicial));
        horaFinalInt = Integer.parseInt(getFirstTwoCharacters(horaFinal));

        for (int i = horaInicialInt; i <= horaFinalInt; i++){
            spinnerArray.add(i);
        }
    }

    private String getFirstTwoCharacters(String time){
        return time.substring(0,2);
    }
}
