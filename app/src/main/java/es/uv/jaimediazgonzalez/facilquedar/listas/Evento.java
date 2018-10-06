package es.uv.jaimediazgonzalez.facilquedar.listas;

import android.os.Parcel;
import android.os.Parcelable;

public class Evento implements Parcelable{
    
    private String nombreEvento, codigoEvento, nombreCreador;
    private int id;

    public Evento(String nombreEvento, String codigoEvento, String nombreCreador, int id) {
        this.nombreEvento = nombreEvento;
        this.codigoEvento = codigoEvento;
        this.nombreCreador = nombreCreador;
        this.id = id;
    }
    
    public Evento(Parcel in) {
        nombreEvento = in.readString();
        codigoEvento = in.readString();
        nombreCreador = in.readString();
        id = in.readInt();
    }


    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(nombreEvento);
        parcel.writeString(nombreCreador);
        parcel.writeString(codigoEvento);
        parcel.writeInt(id);
    }

    public static final Parcelable.Creator<Evento> CREATOR = new Parcelable.Creator<Evento>() {
        @Override
        public Evento createFromParcel(Parcel in) {
            return new Evento(in);
        }

        @Override
        public Evento[] newArray(int size) {
            return new Evento[size];
        }
    };

    public String getNombreEvento() {
        return nombreEvento;
    }

    public void setNombreEvento(String nombreEvento) {
        this.nombreEvento = nombreEvento;
    }

    public String getCodigoEvento() {
        return codigoEvento;
    }

    public void setCodigoEvento(String codigoEvento) {
        this.codigoEvento = codigoEvento;
    }

    public String getNombreCreador() {
        return nombreCreador;
    }

    public void setNombreCreador(String nombreCreador) {
        this.nombreCreador = nombreCreador;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
