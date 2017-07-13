package es.uv.jaimediazgonzalez.facilquedar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

/**
 * Created by Familia Diaz on 02/07/2017.
 */


public class Calendario extends AppCompatActivity {

    private MaterialCalendarView calendario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);

        calendario = (MaterialCalendarView) findViewById(R.id.calendarioView);
        calendario.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);
    }
}
