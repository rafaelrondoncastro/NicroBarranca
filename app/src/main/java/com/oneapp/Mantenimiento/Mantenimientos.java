package com.oneapp.Mantenimiento;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Mantenimientos extends AppCompatActivity {

    Button semanal,quincenal,mensual,trimestral,semestral,anual;
    Bundle extras;
    String IP,Nombre,Equipo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mantenimientos);

        semanal=(Button)findViewById(R.id.semanal);
        quincenal=(Button)findViewById(R.id.quincenal);
        mensual=(Button)findViewById(R.id.mensual);
        trimestral=(Button)findViewById(R.id.trimestral);
        semestral=(Button)findViewById(R.id.semestral);
        anual=(Button)findViewById(R.id.anual);

        Intent intent = getIntent();
        extras = intent.getExtras();
        Nombre = extras.getString("Nombre");
        IP = extras.getString("IP");
        Equipo = extras.getString("Equipo");

        semanal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ABlistmant = new Intent(Mantenimientos.this, ListMant.class);
                ABlistmant.putExtra("Nombre",Nombre);
                ABlistmant.putExtra("IP",IP);
                ABlistmant.putExtra("TipoMant","Semanal");
                ABlistmant.putExtra("Equipo",Equipo);
                startActivity(ABlistmant);
            }
        });

        quincenal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ABlistmant = new Intent(Mantenimientos.this, ListMant.class);
                ABlistmant.putExtra("Nombre",Nombre);
                ABlistmant.putExtra("IP",IP);
                ABlistmant.putExtra("TipoMant","Quincenal");
                ABlistmant.putExtra("Equipo",Equipo);
                startActivity(ABlistmant);
            }
        });
        mensual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ABlistmant = new Intent(Mantenimientos.this, ListMant.class);
                ABlistmant.putExtra("Nombre",Nombre);
                ABlistmant.putExtra("IP",IP);
                ABlistmant.putExtra("TipoMant","Mensual");
                ABlistmant.putExtra("Equipo",Equipo);
                startActivity(ABlistmant);
            }
        });
        trimestral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ABlistmant = new Intent(Mantenimientos.this, ListMant.class);
                ABlistmant.putExtra("Nombre",Nombre);
                ABlistmant.putExtra("IP",IP);
                ABlistmant.putExtra("TipoMant","Trimestral");
                ABlistmant.putExtra("Equipo",Equipo);
                startActivity(ABlistmant);
            }
        });
        semestral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ABlistmant = new Intent(Mantenimientos.this, ListMant.class);
                ABlistmant.putExtra("Nombre",Nombre);
                ABlistmant.putExtra("IP",IP);
                ABlistmant.putExtra("TipoMant","Semestral");
                ABlistmant.putExtra("Equipo",Equipo);
                startActivity(ABlistmant);;
            }
        });
        anual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ABlistmant = new Intent(Mantenimientos.this, ListMant.class);
                ABlistmant.putExtra("Nombre",Nombre);
                ABlistmant.putExtra("IP",IP);
                ABlistmant.putExtra("TipoMant","Anual");
                ABlistmant.putExtra("Equipo",Equipo);
                startActivity(ABlistmant);
            }
        });
    }
}
