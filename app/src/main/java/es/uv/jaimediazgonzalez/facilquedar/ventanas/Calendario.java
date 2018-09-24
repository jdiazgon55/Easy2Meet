package es.uv.jaimediazgonzalez.facilquedar.ventanas;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import es.uv.jaimediazgonzalez.facilquedar.R;

/**
 * Created by Familia Diaz on 02/07/2017.
 */


public class Calendario extends AppCompatActivity implements OnDateSelectedListener {

    private MaterialCalendarView calendario;
    private Button guardar;
    private CalendarDay currentSelectedDate = new CalendarDay();
    private Date fechaDesde, fechaHasta;
    private String horaInicial, horaFinal, nombreCalendario, fechaDesdeString, fechaHastaString,
    apodoUsuario;
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
        fechaDesdeString = getIntent().getStringExtra("fechaDesde");
        fechaHastaString = getIntent().getStringExtra("fechaHasta");
        nombreCalendario = getIntent().getStringExtra("nombreCalendario");
        apodoUsuario = getIntent().getStringExtra("apodoUsuario");
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

        String advertenciaElegirDia = getResources().getString(R.string.advertencia_elegir_dia);
        Toast.makeText(Calendario.this, advertenciaElegirDia,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

        //Solo si quieres marcar esa fecha
        if (selected == true) {

            if (calendario.getSelectedDates().size() >= 1){
                guardar.setEnabled(true);
                guardar.setBackgroundColor(Color.parseColor("#0D98FF"));
            }

            // Activar cuando quieras implementar horas
            //Array con la lista de las horas
            /* ArrayList<String> spinnerArray = new ArrayList<String>();
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

            //Sirve para saber si tenemos que deseleccionar esta fecha

            startActivityForResult(explicit_intent, 1);
            this.setCurrentSelectedDate(date);
            */
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                listaHorasSeleccionadas = data.getStringArrayListExtra("listaHorasSeleccionadas");
                if (calendario.getSelectedDates().size() >= 1){
                    guardar.setEnabled(true);
                    guardar.setBackgroundColor(Color.parseColor("#0D98FF"));
                }
            }
            else{
                this.calendario.setDateSelected(this.getCurrentSelectedDate(), false);
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

            if (isOnline()) {

                List<CalendarDay> diasSeleccionados = calendario.getSelectedDates();
                ArrayList<String> dias = new ArrayList<String>();
                diasSeleccionados = ordenarDias(diasSeleccionados);

                for (CalendarDay dia : diasSeleccionados) {
                    String fecha = dia.getDay() + "/" + dia.getMonth() + "/" + dia.getYear();
                    dias.add(fecha);
                }
                explicit_intent.putStringArrayListExtra("diasSeleccionados", dias);
                explicit_intent.putExtra("nombreCalendario", nombreCalendario);
                explicit_intent.putExtra("fechaDesde", fechaDesdeString);
                explicit_intent.putExtra("fechaHasta", fechaHastaString);
                explicit_intent.putExtra("apodoUsuario", apodoUsuario);
                //explicit_intent.putStringArrayListExtra("horasSeleccionadas", listaHorasSeleccionadas);

                startActivity(explicit_intent);
            } else {
                String advertenciaNoInternet = getResources().getString(R.string.advertencia_no_internet);
                Toast.makeText(Calendario.this, advertenciaNoInternet,
                        Toast.LENGTH_LONG).show();
            }
        }
    };


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



    private String getFirstTwoCharacters(String time){
        return time.substring(0,2);
    }

    public CalendarDay getCurrentSelectedDate(){
        return this.currentSelectedDate;
    }

    public void setCurrentSelectedDate(CalendarDay day){
        this.currentSelectedDate = day;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
