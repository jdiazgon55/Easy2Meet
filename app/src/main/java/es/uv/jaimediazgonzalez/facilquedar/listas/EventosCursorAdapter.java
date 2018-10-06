package es.uv.jaimediazgonzalez.facilquedar.listas;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import es.uv.jaimediazgonzalez.facilquedar.R;

public class EventosCursorAdapter extends CursorAdapter {
    private ArrayList<Integer> idEventos;
    private ArrayList<Evento> arrayEventos;
    Context con;

    public EventosCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        init(context);
    }

    void init(Context context){
        this.con = context;
        idEventos = new ArrayList<Integer>();
        arrayEventos = new ArrayList<Evento>();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.listeventosview, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView nombreEventoText = (TextView) view.findViewById(R.id.nombreEvento);
        TextView nombreCreadorText = (TextView) view.findViewById(R.id.nombreCreador);

        String nombreEvento = cursor.getString(cursor.getColumnIndex("nombreEvento"));
        String codigoEvento = cursor.getString(cursor.getColumnIndex("codigoEvento"));
        int idEvento = cursor.getInt(cursor.getColumnIndex("idEvento"));
        String nombreCreador = cursor.getString(cursor.getColumnIndex("nombreCreador"));

        Evento tmpEvento = new Evento(nombreEvento, codigoEvento, nombreCreador, idEvento);
        arrayEventos.add(tmpEvento);

        idEventos.add(idEvento);

        nombreEventoText.setText(nombreEvento);
        nombreCreadorText.setText(nombreCreador);
    }

    public int getIdEvento(int indice){
        return idEventos.get(indice);
    }

    public Evento getEvento(int indice){
        return arrayEventos.get(indice);
    }
    
}
