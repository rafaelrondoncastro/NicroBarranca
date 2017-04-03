package com.oneapp.Mantenimiento;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Prueba extends AppCompatActivity {

    Long fechs;
    TextView text;
    EditText edit;
    Button Actualiza;
    Long eventIDn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba);

        text=(TextView)findViewById(R.id.text);
        edit=(EditText)findViewById(R.id.edit);
        Actualiza=(Button)findViewById(R.id.Actualizar);
        fechs=1485166440000L;

        Actualiza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String editado = edit.getText().toString();

                ContentResolver cr = getContentResolver();
                ContentValues values = new ContentValues();
                Uri updateUri = null;

                values.put(CalendarContract.Events.TITLE, editado);
                updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventIDn);
                getContentResolver().update(updateUri, values, null, null);
            }
        });

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

        String rrule ="FREQ=DAILY;INTERVAL="+3+";COUNT="+10;

        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.TITLE, "Editar 1");
        values.put(CalendarContract.Events.DESCRIPTION, "mira la pagina 1");
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "America/Los_Angeles");
        values.put(CalendarContract.Events.DURATION, duracion);
        values.put(CalendarContract.Events.RRULE, rrule);
        if (ActivityCompat.checkSelfPermission(Prueba.this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        Uri uriid =cr.insert(CalendarContract.Events.CONTENT_URI, values);
        String eventID = uriid.getLastPathSegment();
        eventIDn = Long.parseLong(uriid.getLastPathSegment());

        text.setText("Alarma: "+eventID);
    }
}
