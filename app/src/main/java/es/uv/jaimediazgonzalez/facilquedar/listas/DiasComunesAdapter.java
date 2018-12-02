package es.uv.jaimediazgonzalez.facilquedar.listas;

import android.app.Service;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import es.uv.jaimediazgonzalez.facilquedar.R;

public class DiasComunesAdapter extends BaseAdapter {
    private ArrayList<FechaCursor> arrayDiasSeleccionados;
    private ArrayList<FechaCursor> arrayDiasComunes = new ArrayList<>();
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
        arrayDiasSeleccionados = listaDiasComunes;
    }

    @Override
    public int getCount() {
        return arrayDiasSeleccionados.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayDiasSeleccionados.get(position);
    }

    @Override
    public long getItemId(int position) {
        return arrayDiasSeleccionados.get(position).getDia();
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
        FechaCursor fecha = arrayDiasSeleccionados.get(position);
        if(fecha != null)
        {
            holder.diaText.setText(String.valueOf(fecha.getDia()));
            holder.mesText.setText(fecha.getMes());
            holder.anyoText.setText(String.valueOf(fecha.getAnyo()));

            // Si es un dia común, lo pintamos de verde
            if(isFechaComun(fecha)){
                holder.diaText.setTextColor(Color.rgb(79, 178, 9));
                holder.mesText.setTextColor(Color.rgb(79, 178, 9));
                holder.anyoText.setTextColor(Color.rgb(79, 178, 9));
            } else{
                holder.diaText.setTextColor(Color.rgb(255, 86, 25));
                holder.mesText.setTextColor(Color.rgb(255, 86, 25));
                holder.anyoText.setTextColor(Color.rgb(255, 86, 25));
            }
        }
        return v;
    }

    private boolean isFechaComun(FechaCursor fecha) {
        for (FechaCursor tmp : this.arrayDiasComunes){
            if(fecha.getAnyo() == tmp.getAnyo())
                if(fecha.getMes() == tmp.getMes())
                    if(fecha.getDia() == tmp.getDia())
                        return true;
        }
        return false;
    }

    public void setDiasComunes(ArrayList<FechaCursor> dias){
        this.arrayDiasComunes = dias;
    }


}
