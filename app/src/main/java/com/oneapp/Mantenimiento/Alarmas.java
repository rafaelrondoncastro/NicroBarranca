package com.oneapp.Mantenimiento;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Alarmas extends AppCompatActivity {

    CompactCalendarView Compactcalendar;
    private SimpleDateFormat dateFormatmonth = new SimpleDateFormat("MMMM-yyyy", Locale.getDefault());
    TextView dia,contenido;
    String dato,IP,BDalarma="",BDfecha="",BDfrec="";
    Obalarma hiloalarma;
    String[] alarma,Ids,fechas,frecue,numeros;
    String fecha,frecu;
    long eventIDn,BDidalarm;
    long[] numero;
    int n, idn=1;
    int [] Frec;
    SQLiteDatabase db;
    Bundle extras;
    ProgressDialog progressDialogAlar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarmas);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent intent=getIntent();
        extras = intent.getExtras();
        IP = extras.getString("IP");

        BDalarmas alarmas =
                new BDalarmas(this, "BDalarmas", null, 1);

        db = alarmas.getWritableDatabase();

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle("Alarmas");

        Compactcalendar = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        dia=(TextView)findViewById(R.id.dia);
        contenido=(TextView)findViewById(R.id.contenido);

        Compactcalendar.setUseThreeLetterAbbreviation(true);

        hiloalarma = new Obalarma();
        String cadena = "http://"+IP+"/ACCESS/php/Obalarmas.php";

        progressDialogAlar = ProgressDialog.show(Alarmas.this,
                "Espere un momento", "Actualizando las Alarmas...");

        hiloalarma.execute(cadena);

        Compactcalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {

                long hoy = dateClicked.getTime();

                List<Event> evento=Compactcalendar.getEvents(hoy);
                String todo=evento.toString();
                //dia.setText(todo);

                if(todo.equals("[]")){
                    dia.setText("No hay actividades para este día");
                }else{
                    int fin = todo.length();
                    String casitodo = todo.substring(0,fin-1);
                    String[] dividido= casitodo.split(",");
                    int m = dividido.length;
                    dia.setText("");

                    if (m > 1) {
                        for (int j = 0; j < (m/3)+1; j++) {
                            int k = (3*j)-1;
                            if (k <= 0) {

                            } else{
                                int findiv = dividido[k].length();
                                String eventomenos=dividido[k].substring(0,findiv-1);
                                String Sevento="evento";
                                String eventoprint=eventomenos.replace("data",Sevento);
                                dia.setText(dia.getText() + "\n" + eventoprint);
                            }
                        }
                    }else{
                        contenido.setText(String.valueOf(m));
                        dia.setText(dato);
                    }
                }
            }
            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                    actionBar.setTitle(dateFormatmonth.format(firstDayOfNewMonth));
            }
        });
    }

    public class Obalarma extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;

            try {
                url = new URL(cadena);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0" + " (Linux; Android 1.5; es-ES) Ejemplo HTTP");
                int respuesta = connection.getResponseCode();
                StringBuilder result = new StringBuilder();

                if (respuesta == HttpURLConnection.HTTP_OK) {
                    InputStream in = new BufferedInputStream(connection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    JSONObject respuestaJSON = new JSONObject(result.toString());
                    String resultJSON = respuestaJSON.getString("estado");

                    if (resultJSON.equals("1")) {
                        JSONArray arrayJson = respuestaJSON.getJSONArray("Alarma");
                        n=arrayJson.length();
                        alarma=new String[n];
                        numero=new long[n];
                        Frec=new int[n];
                        Ids=new String[n];
                        fechas=new String[n];
                        frecue=new String[n];
                        numeros=new String[n];
                        for (int i = 0; i < arrayJson.length(); i++) {
                            JSONObject objetoJson = arrayJson.getJSONObject(i);
                            alarma[i]=objetoJson.getString("Especificacion");
                            fecha=objetoJson.getString("Fecha_hora");
                            Ids[i]=objetoJson.getString("Ids");

                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = null;
                            try {
                                date = df.parse(fecha);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            numero[i] = date.getTime();
                            numeros[i] = String.valueOf(numero[i]);
                            //num = numero.substring();

                            fechas[i]=objetoJson.getString("Fecha_hora");
                            frecu=objetoJson.getString("FrecuenciaDias");
                            frecue[i]=objetoJson.getString("FrecuenciaDias");
                            Frec[i]=Integer.parseInt(frecu);
                        }
                    } else if (resultJSON.equals("2")) {
                        alarma[0]="Nada";
                        //fecha[0]="Nada";
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return alarma;
        }

        @Override
        protected void onPreExecute () {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute (String[] s) {

            progressDialogAlar.dismiss();

            //Toast.makeText(getBaseContext(), alarma[1], Toast.LENGTH_SHORT).show();
            String[] idg = {"1"};
            Cursor cg = db.rawQuery("SELECT * FROM alarmas WHERE id=?", idg);
            if (cg.moveToFirst()) {
                cg.close();
                for (int idn = 0; idn < n; idn++) {
                    String ids = String.valueOf(idn+1);
                    String[] id = {ids};
                    Cursor c = db.rawQuery("SELECT * FROM alarmas WHERE id=?", id);

                    //Toast.makeText(getBaseContext(),ids+","+n, Toast.LENGTH_LONG).show();
                    if (c.moveToFirst()) {

                        BDfecha = c.getString(1);
                        BDalarma = c.getString(2);
                        BDfrec = c.getString(3);
                        BDidalarm = c.getInt(4);

                        String BDidalarms=String.valueOf(BDidalarm);

                        if (BDalarma.compareTo(alarma[idn]) != 0) {
                            //Toast.makeText(getBaseContext(),BDalarma+","+alarma[idn], Toast.LENGTH_LONG).show();
                            db.execSQL("UPDATE alarmas SET alarm='" + alarma[idn] + "' WHERE id=" + idn+1);

                            ContentValues values = new ContentValues();
                            Uri updateUri = null;

                            values.put(CalendarContract.Events.TITLE, alarma[idn]);
                            updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, BDidalarm);
                            getContentResolver().update(updateUri, values, null, null);
                        }
                        if (BDfecha.compareTo(numeros[idn]) != 0) {
                            db.execSQL("UPDATE alarmas SET fecha='" + numeros[idn] + "' WHERE id=" + idn+1);

                            long fechs=numero[idn];
                            long startMillis = 0;

                            java.util.Calendar beginTime = java.util.Calendar.getInstance();
                            beginTime.setTimeInMillis(fechs);
                            startMillis = beginTime.getTimeInMillis();

                            ContentValues values = new ContentValues();
                            Uri updateUri = null;

                            values.put(CalendarContract.Events.DTSTART, startMillis);
                            updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, BDidalarm);
                            getContentResolver().update(updateUri, values, null, null);
                        }
                        if (BDfrec.compareTo(frecue[idn]) != 0) {
                            db.execSQL("UPDATE alarmas SET frec='" + frecue[idn] + "' WHERE id=" + idn+1);

                            int parte =336/Frec[idn];
                            String rruleac ="FREQ=DAILY;INTERVAL="+frecue[idn]+";COUNT="+parte;

                            ContentValues values = new ContentValues();
                            Uri updateUri = null;

                            values.put(CalendarContract.Events.RRULE, rruleac);
                            updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, BDidalarm);
                            getContentResolver().update(updateUri, values, null, null);
                        }
                        //Toast.makeText(getBaseContext(),BDfecha+","+BDalarma+","+BDfrec+","+BDidalarms, Toast.LENGTH_LONG).show();
                    }
                    c.close();
                }

                //Toast.makeText(getBaseContext(),idn+","+n, Toast.LENGTH_LONG).show();

                for (int k = 0; k < n; k++) {
                    int partes = 336 / Frec[k];
                    long cont = 0;
                    for (int g = 0; g < partes; g++) {
                        long fech = numero[k] + cont;
                        Event ev1 = new Event(Color.BLUE, fech, s[k]);
                        Compactcalendar.addEvent(ev1, true);
                        cont = cont + Frec[k] * 86400000L;
                    }
                }
            }else {
                //Toast.makeText(getBaseContext(), "BDvacia", Toast.LENGTH_LONG).show();

                for (int k = 0; k < n; k++) {


                    int partes =336/Frec[k];
                    long cont=0;

                    long fechs=numero[k];
                    long calID = 1;
                    long startMillis = 0;
                    long endMillis = 0;
                    java.util.Calendar beginTime = java.util.Calendar.getInstance();
                    beginTime.setTimeInMillis(fechs);
                    startMillis = beginTime.getTimeInMillis();
                    java.util.Calendar endTime = java.util.Calendar.getInstance();
                    endTime.setTimeInMillis(fechs+3600000L);
                    endMillis = endTime.getTimeInMillis();

                    long duracionMillis = endMillis-startMillis;
                    int duracionMinutos = (int)(duracionMillis/(1000*60));
                    String duracion = "P" + duracionMinutos + "M";

                    String rrule ="FREQ=DAILY;INTERVAL="+frecue[k]+";COUNT="+partes;

                    ContentResolver cr = getContentResolver();
                    ContentValues values = new ContentValues();
                    values.put(CalendarContract.Events.DTSTART, startMillis);
                    values.put(CalendarContract.Events.TITLE, alarma[k]);
                    values.put(CalendarContract.Events.DESCRIPTION, "En la aplicación mantenimiento encontrara las actividades que debera hacer hoy en cada máquina");
                    values.put(CalendarContract.Events.CALENDAR_ID, calID);
                    values.put(CalendarContract.Events.EVENT_TIMEZONE, "America/Los_Angeles");
                    values.put(CalendarContract.Events.DURATION, duracion);
                    values.put(CalendarContract.Events.RRULE, rrule);
                    values.put(CalendarContract.Events.ALL_DAY,0);
                    values.put(CalendarContract.Events.HAS_ALARM,1);

                    if (ActivityCompat.checkSelfPermission(Alarmas.this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        return;
                    }
                    Uri uriid =cr.insert(CalendarContract.Events.CONTENT_URI, values);
                    eventIDn = Long.parseLong(uriid.getLastPathSegment());
                    
                    Uri REMINDERS_URI = Uri.parse(getCalendarUriBase(true) + "reminders");
                    values = new ContentValues();
                    values.put(CalendarContract.Reminders.EVENT_ID, Long.parseLong(uriid.getLastPathSegment()));
                    values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
                    values.put(CalendarContract.Reminders.MINUTES, 10);
                    cr.insert(REMINDERS_URI, values);

                    db.execSQL("INSERT INTO alarmas (id,fecha,alarm,frec,idalarm)" + "VALUES(" + Ids[k] + ", '" + numero[k] + "', '"+alarma[k]+"', '"+frecue[k]+"', "+eventIDn+")");

                    for (int g = 0; g < partes; g++){
                    long fech = numero[k]+ cont;
                    Event ev1 = new Event(Color.BLUE, fech, s[k]);
                    Compactcalendar.addEvent(ev1, true);
                    cont=cont + Frec[k]*86400000L;
                    }
                }
            }
            db.close();
        }
        @Override
        protected void onProgressUpdate (Void...values){
        }
    }

    private String getCalendarUriBase(boolean b) {
        Uri calendarURI = null;
        try {
            if (android.os.Build.VERSION.SDK_INT <= 7) {
                calendarURI = (b) ? Uri.parse("content://calendar/") : Uri.parse("content://calendar/calendars");
            } else {
                calendarURI = (b) ? Uri.parse("content://com.android.calendar/") : Uri
                        .parse("content://com.android.calendar/calendars");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return calendarURI.toString();
    }
}
