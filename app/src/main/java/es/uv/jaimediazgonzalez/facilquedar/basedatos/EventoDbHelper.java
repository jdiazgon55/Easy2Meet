package es.uv.jaimediazgonzalez.facilquedar.basedatos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import es.uv.jaimediazgonzalez.facilquedar.listas.Evento;

public class EventoDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "LectorEventos.db";
    private Evento evento;

    private final String idEvento = "idEvento", nombreEvento = "nombreEvento", 
            codigoEvento = "codigoEvento", nombreCreador = "nombreCreador",
            nombreUsuario ="nombreUsuario", tablaEventos = "TablaEventos";

    public EventoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + tablaEventos + " ("
                + " _id" + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + idEvento + " INTEGER,"
                + nombreEvento + " TEXT NOT NULL,"
                + codigoEvento + " TEXT NOT NULL,"
                + nombreCreador + " TEXT NOT NULL,"
                + nombreUsuario + " TEXT NOT NULL,"
                + "UNIQUE (" + idEvento + "))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean InsertarEvento(Evento tmpEvento){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(idEvento, tmpEvento.getId());
        values.put(nombreEvento, tmpEvento.getNombreEvento());
        values.put(codigoEvento, tmpEvento.getCodigoEvento());
        values.put(nombreCreador, tmpEvento.getNombreCreador());
        values.put(nombreUsuario, tmpEvento.getNombreUsuario());

        db.beginTransaction();
        db.insert(tablaEventos, null, values);
        db.setTransactionSuccessful();
        if (db != null)
            db.endTransaction();
        return true;
    }

    public Integer BorrarEvento (int idEvento) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(tablaEventos,
                "idEvento = ? ",
                new String[] { Integer.toString(idEvento) });
    }

    public Cursor getEventosByCodigo(String codigo){
        Cursor c = getReadableDatabase().query(
                tablaEventos,
                null,
                codigoEvento + " = ?",
                new String[]{codigo},
                null,
                null,
                null);

        return c;
    }

    public Cursor getTodosEventos(){
        Cursor c = getReadableDatabase().query(
                tablaEventos,
                null,
                null,
                null,
                null,
                null,
                null);
        return c;
    }

    public int getUltimoId(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCount= db.rawQuery("SELECT * \n" +
                "    FROM    TablaEventos\n" +
                "    WHERE   idEvento = (SELECT MAX(idEvento)  FROM TablaEventos);", null);
        int count = 0;
        if(mCount.moveToNext())
            count= mCount.getInt(0);
        mCount.close();
        return count;
    }

}
