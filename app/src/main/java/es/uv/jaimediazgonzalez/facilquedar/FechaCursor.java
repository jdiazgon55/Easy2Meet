package es.uv.jaimediazgonzalez.facilquedar;

import android.os.Parcel;
import android.os.Parcelable;

public class FechaCursor implements Parcelable {
    private String mes;
    private int dia, anyo;

    public FechaCursor(int dia, String mes, int anyo) {
        this.dia = dia;
        this.mes = mes;
        this.anyo = anyo;
    }

    public FechaCursor(Parcel in) {
        dia = in.readInt();
        mes = in.readString();
        anyo = in.readInt();
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public int getAnyo() {
        return anyo;
    }

    public void setAnyo(int anyo) {
        this.anyo = anyo;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(dia);
        dest.writeString(mes);
        dest.writeInt(anyo);
    }

    public static final Parcelable.Creator<FechaCursor> CREATOR = new Parcelable.Creator<FechaCursor>() {
        @Override
        public FechaCursor createFromParcel(Parcel in) {
            return new FechaCursor(in);
        }

        @Override
        public FechaCursor[] newArray(int size) {
            return new FechaCursor[size];
        }
    };
}
