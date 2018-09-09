package es.uv.jaimediazgonzalez.facilquedar.listas;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import es.uv.jaimediazgonzalez.facilquedar.R;

public class DiasComunesAdapter extends BaseAdapter {
    private ArrayList<FechaCursor> arrayDiasComunes;
    Context context;

    static class ViewHolder {
        TextView diaText;
        TextView mesText;
        TextView anyoText;
    }

    public DiasComunesAdapter(Context context, ArrayList<FechaCursor> tmpDiasComunes) {
        this.context = context;
        init(tmpDiasComunes);
    }

    void init(ArrayList<FechaCursor> listaDiasComunes){
        arrayDiasComunes = listaDiasComunes;
    }

    @Override
    public int getCount() {
        return arrayDiasComunes.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayDiasComunes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return arrayDiasComunes.get(position).getDia();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Usamos el patrón del ViewHolder para almacenar las vistas de cada elemento de la lista para
        //mostrarlos más rápido al desplazar la lista
        View v = convertView;
        ViewHolder holder = null;
        if (v == null) {
            // Si es null la creamos a partir de layout
            LayoutInflater li =
                    (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
            v = li.inflate(R.layout.listdiasview, null);
            holder = new ViewHolder();
            holder.diaText = (TextView) v.findViewById(R.id.dia);
            holder.mesText = (TextView) v.findViewById(R.id.mes);
            holder.anyoText = (TextView) v.findViewById(R.id.anyo);
            v.setTag(holder);
        } else {
            // Si no es null la reutilizamos para actualizarla
            holder = (ViewHolder) v.getTag();
        }
        //Rellenar el holder con la información de la parada que está en la posicion position del ArrayList
        FechaCursor fecha = arrayDiasComunes.get(position);
        if(fecha != null)
        {
            holder.diaText.setText(String.valueOf(fecha.getDia()));
            holder.mesText.setText(fecha.getMes());
            holder.anyoText.setText(String.valueOf(fecha.getAnyo()));
        }
        return v;
    }


}
