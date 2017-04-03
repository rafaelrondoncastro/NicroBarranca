package com.oneapp.Mantenimiento;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.CalendarContract;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContentResolverCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.EventLog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Actividades extends AppCompatActivity {

    TextView nombre,infocal,idevent,textoip;
    Button InspDiaria,solicitaotm,ejecutaotm,cronograma,mantenimientos,Ip,Cierredia;
    Bundle extras;
    String Nombre,IP;
    EditText txtIp;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividades);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        extras = intent.getExtras();
        Nombre = extras.getString("Nombre");
        IP = extras.getString("IP");

        solicitaotm = (Button) findViewById(R.id.GeneraOTM);
        ejecutaotm = (Button) findViewById(R.id.EjecutaOTM);
        InspDiaria = (Button) findViewById(R.id.InspDiaria);
        cronograma = (Button) findViewById(R.id.Cronograma);
        mantenimientos = (Button) findViewById(R.id.manten);
        Cierredia = (Button) findViewById(R.id.CierreDia);
        nombre = (TextView) findViewById(R.id.nombre);
        infocal = (TextView) findViewById(R.id.infocal);
        idevent = (TextView) findViewById(R.id.idevent);
        Ip=(Button)findViewById(R.id.Ip);
        txtIp=(EditText)findViewById(R.id.txtIp);
        textoip=(TextView)findViewById(R.id.textoip);

        if (IP!=null){
            textoip.setText(IP);
        }else{
            IP="";
        }

        textoip.setVisibility(View.VISIBLE);
        txtIp.setVisibility(View.GONE);
        nombre.setText("Usuario: "+Nombre);

        BDip ip =
                new BDip(this, "BDip", null, 1);

        db = ip.getWritableDatabase();

        String[] id={"1"};
        final Cursor c = db.rawQuery("SELECT * FROM Ip WHERE id=?",id);

        int ids=1;
        if (c.moveToFirst()){
            do{
                IP= c.getString(1);
                if (IP.equals("")){
                    textoip.setText("192.168.1.52");
                }else{
                    textoip.setText(IP);
                }

            }while (c.moveToNext());
        }else{
            if (IP.equals("")){
                db.execSQL("INSERT INTO ip (id,ip)" + "VALUES(" + ids + ", '192.168.1.52')");
                IP="192.168.1.52";
            }else{
                db.execSQL("INSERT INTO ip (id,ip)" + "VALUES(" + ids + ", '"+IP+"')");
            }

        }

        Ip.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                txtIp.setVisibility(View.VISIBLE);
                textoip.setVisibility(View.GONE);
                txtIp.setText(IP);
                return true;
            }
        });
        Ip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textoip.setVisibility(View.VISIBLE);
                String IPs=txtIp.getText().toString();
                txtIp.setVisibility(View.GONE);
                if (IPs.equals("")){
                    db.execSQL("UPDATE Ip SET ip='192.168.1.52' WHERE id=1");
                }else{
                    IP=txtIp.getText().toString();
                    db.execSQL("UPDATE Ip SET ip='"+ IP +"' WHERE id=1");
                    textoip.setText(IP);
                }
            }
        });

        InspDiaria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IP=textoip.getText().toString();
                Intent ABEquipos = new Intent(Actividades.this, Equipos.class);
                ABEquipos.putExtra("Nombre", Nombre);
                ABEquipos.putExtra("Origen", "Ins");
                ABEquipos.putExtra("IP",IP);
                startActivity(ABEquipos);
            }
        });
        Cierredia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IP=textoip.getText().toString();
                Intent ABCierredia = new Intent(Actividades.this, Cierredia.class);
                ABCierredia.putExtra("Nombre", Nombre);
                ABCierredia.putExtra("IP",IP);
                startActivity(ABCierredia);
            }
        });
        solicitaotm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IP=textoip.getText().toString();
                Intent ABEquipos = new Intent(Actividades.this, Equipos.class);
                ABEquipos.putExtra("Nombre", Nombre);
                ABEquipos.putExtra("Origen", "Sol");
                ABEquipos.putExtra("IP",IP);
                startActivity(ABEquipos);
            }
        });
        ejecutaotm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IP=textoip.getText().toString();
                Intent ABMisotm = new Intent(Actividades.this, MisOtm.class);
                ABMisotm.putExtra("Nombre",Nombre);
                ABMisotm.putExtra("IP",IP);
                startActivity(ABMisotm);
            }
        });
        mantenimientos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IP=textoip.getText().toString();
                Intent ABEquipos = new Intent(Actividades.this, Equipos.class);
                ABEquipos.putExtra("Nombre", Nombre);
                ABEquipos.putExtra("Origen", "Mant");
                ABEquipos.putExtra("IP",IP);
                startActivity(ABEquipos);

            }
        });
        cronograma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IP=textoip.getText().toString();
                Intent ABcalendario=new Intent(Actividades.this, Alarmas.class);
                ABcalendario.putExtra("Nombre",Nombre);
                ABcalendario.putExtra("IP",IP);
                startActivity(ABcalendario);
            }
        });
    }

    public void onBackPressed() {

        final AlertDialog.Builder alertasalida = new AlertDialog.Builder(Actividades.this);
        alertasalida.setTitle("Confirmar");
        alertasalida.setMessage("Si continúa cerrará la sesión como usuario. ¿Desea continuar?");
        alertasalida.setIcon(R.drawable.logo);
        alertasalida.setCancelable(false);

        alertasalida.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface alertasalida, int which) {
                alertasalida.cancel();
            }
        });
        alertasalida.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface alertasalida, int which) {
                IP=textoip.getText().toString();
                Intent ABingreso = new Intent(getApplicationContext(), ingreso.class);
                ABingreso.putExtra("regreso","true");
                ABingreso.putExtra("IP",IP);
                startActivity(ABingreso);
            }
        });
        alertasalida.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface alertasalida, int which) {

            }
        });
        alertasalida.create();
        alertasalida.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menuinicio, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.i("ActionBar", "Atrás!");
                finish();
                return true;
            case R.id.insdia:
                IP=textoip.getText().toString();
                Intent ABEquipos = new Intent(Actividades.this, Equipos.class);
                ABEquipos.putExtra("Nombre", Nombre);
                ABEquipos.putExtra("Origen", "Ins");
                ABEquipos.putExtra("IP",IP);
                startActivity(ABEquipos);
                return true;
            case R.id.genotm:
                IP=textoip.getText().toString();
                Intent ABEquiposg = new Intent(Actividades.this, Equipos.class);
                ABEquiposg.putExtra("Nombre", Nombre);
                ABEquiposg.putExtra("Origen", "Sol");
                ABEquiposg.putExtra("IP",IP);
                startActivity(ABEquiposg);
                return true;
            case R.id.ejeotm:
                IP=textoip.getText().toString();
                Intent ABMisotm = new Intent(Actividades.this, MisOtm.class);
                ABMisotm.putExtra("Nombre",Nombre);
                ABMisotm.putExtra("IP",IP);
                startActivity(ABMisotm);
                return true;
            case R.id.calendario:
                IP=textoip.getText().toString();
                Intent ABcalendario=new Intent(Actividades.this, Alarmas.class);
                ABcalendario.putExtra("Nombre",Nombre);
                ABcalendario.putExtra("IP",IP);
                startActivity(ABcalendario);
                return true;
            case R.id.mantenimientos:
                IP=textoip.getText().toString();
                Intent ABEquiposm = new Intent(Actividades.this, Equipos.class);
                ABEquiposm.putExtra("Nombre", Nombre);
                ABEquiposm.putExtra("Origen", "Mant");
                ABEquiposm.putExtra("IP",IP);
                startActivity(ABEquiposm);
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
}
