package com.oneapp.Mantenimiento;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

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
import java.util.ArrayList;

public class Inspeccion extends AppCompatActivity {

    Bundle extras;
    EnviarInspeccion hilo;
    RadioButton existente,nuevo;
    LinearLayout Ldescripcion, Lsolu, Ltrabaja, Lotm;
    EditText txtfallo;
    Button guardarinsp;
    String Origen,IP,numero,avanzar="No",Averias="null",Equipo,Nombre20,Nombre,Inconv,Select,Llenado="",confnuevo="",
            Estado="sin%20inconveniente",Inconveniente="No%20Aplica",Solucion="No%20Aplica",Trabaja="Si",otm="No",NumI;
    Spinner list;
    Spinner listaJson;
    ObtnumOTM hiloNum;
    int numotm;
    ProgressDialog progressDialogEfect,progressDialogenv,progressDialognum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspeccion);

        Ldescripcion = (LinearLayout) findViewById(R.id.Laydescripcion);
        Lsolu = (LinearLayout) findViewById(R.id.Laysolu);
        Ltrabaja = (LinearLayout) findViewById(R.id.Laytrabaja);
        Lotm = (LinearLayout) findViewById(R.id.Layotm);
        guardarinsp = (Button) findViewById(R.id.guardarinsp);
        listaJson = (Spinner) findViewById(R.id.Fallos);
        txtfallo = (EditText) findViewById(R.id.txtFallos);
        existente = (RadioButton) findViewById(R.id.existente);
        nuevo = (RadioButton) findViewById(R.id.nuevo);

        listaJson.setVisibility(View.GONE);
        txtfallo.setVisibility(View.GONE);

        Intent intent = getIntent();
        extras = intent.getExtras();
        Equipo = extras.getString("Equipo");
        Nombre = extras.getString("Nombre");
        IP = extras.getString("IP");
        Origen = extras.getString("Origen");
        NumI = extras.getString("NumI");
        Nombre20=Nombre.replaceAll(" ","%20");


        ObtenerEfectos obtenerefectos = new ObtenerEfectos();
        obtenerefectos.cargarcontenido(getApplicationContext());

        progressDialogEfect = ProgressDialog.show(this,
                "Espere un momneto", "Cargando Efectos de Falla...");

        obtenerefectos.execute(listaJson);

        guardarinsp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (otm.equals("Si")){
                    hilo = new EnviarInspeccion();
                    String cadena = "http://"+IP+"/ACCESS/php/GuardarInspeccion.php?Numero="+NumI+"&Codigo=" + Equipo + "&Realizo=" + Nombre20 + "&Estado=" + Estado + "&Inconveniente=" + Inconveniente + "&Solucion=" + Solucion + "&Trabaja=" + Trabaja + "&GenerarOTM=" + otm+"&Condicion=Cerrado";

                    progressDialogenv = ProgressDialog.show(Inspeccion.this,
                            "Espere un momneto", "Guardando Inspacción...");

                    hilo.execute(cadena);

                }else{
                    hilo = new EnviarInspeccion();
                    String cadena = "http://"+IP+"/ACCESS/php/GuardarInspeccion.php?Numero="+NumI+"&Codigo=" + Equipo + "&Realizo=" + Nombre20 + "&Estado=" + Estado + "&Inconveniente=" + Inconveniente + "&Solucion=" + Solucion + "&Trabaja=" + Trabaja + "&GenerarOTM=" + otm+"&Condicion=Abierto";

                    progressDialogenv = ProgressDialog.show(Inspeccion.this,
                            "Espere un momneto", "Guardando Inspacción...");

                    hilo.execute(cadena);
                }

            }
        });
    }

    public void onBackPressed() {
        Intent ABequipos = new Intent(getApplicationContext(), Equipos.class);
        ABequipos.putExtra("Nombre",Nombre);
        ABequipos.putExtra("IP",IP);
        ABequipos.putExtra("Origen",Origen);
        startActivity(ABequipos);
    }

    public void estadoequipo(View view) {
        switch (view.getId()) {
            case R.id.sininconv:
                Ldescripcion.setVisibility(View.GONE);
                Lsolu.setVisibility(View.GONE);
                Ltrabaja.setVisibility(View.GONE);
                Lotm.setVisibility(View.GONE);
                Estado = "Sin%20inconvenientes";
                avanzar="Ok";
                break;
            case R.id.coninconv:
                Ldescripcion.setVisibility(View.VISIBLE);
                Lsolu.setVisibility(View.VISIBLE);
                Inconveniente="No%20arranca";
                Estado = "Con%20inconvenientes";
                break;
        }
    }

    public void efecto(View view) {
        switch (view.getId()) {
            case R.id.nuevo:
                listaJson.setVisibility(View.GONE);
                txtfallo.setVisibility(View.VISIBLE);
                confnuevo="True";
                break;
            case R.id.existente:
                listaJson.setVisibility(View.VISIBLE);
                txtfallo.setVisibility(View.GONE);
                break;
        }
    }

    public void solu(View view) {
        switch (view.getId()) {
            case R.id.sisolu:
                Ltrabaja.setVisibility(View.GONE);
                Lotm.setVisibility(View.GONE);
                Solucion = "Si";
                avanzar="Ok";
                break;
            case R.id.nosolu:
                Ltrabaja.setVisibility(View.VISIBLE);
                Solucion = "No";
                break;
        }
    }

    public void trabaja(View view) {
        switch (view.getId()) {
            case R.id.sitrabaja:
                Lotm.setVisibility(View.GONE);
                Trabaja = "Si";
                avanzar="Ok";
                break;
            case R.id.notrabaja:
                Lotm.setVisibility(View.VISIBLE);
                Trabaja = "No";
                break;
        }
    }

    public void otm(View view) {
        switch (view.getId()) {
            case R.id.siotm:
                otm = "Si";
                avanzar="Ok";
                break;
            case R.id.nootm:
                Lotm.setVisibility(View.VISIBLE);
                otm = "No";
                avanzar="Ok";
                break;
        }
    }

    public class EnviarInspeccion extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url;
            String Nombres = "";

            try {
                url = new URL(cadena);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0" +
                        " (Linux; Android 1.5; es-ES) Ejemplo HTTP");
                int resp=connection.getResponseCode();
                if (resp==HttpURLConnection.HTTP_OK){

                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Nombres;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {

            progressDialogenv.dismiss();

            if (avanzar.equals("Ok")) {

                if (Llenado.equals("False")||confnuevo.equals("True")) {
                    Select = txtfallo.getText().toString();
                    Inconv = Select.replaceAll(" ", "%20");
                    Inconveniente = Inconv;
                }

                if (otm.equals("Si")) {

                    hiloNum = new ObtnumOTM();
                    String urlnum = "http://"+IP+"/ACCESS/php/ObnumOTM.php";

                    progressDialognum = ProgressDialog.show(Inspeccion.this,
                            "Espere un momneto", "Obteniendo numero de OTM...");

                    hiloNum.execute(urlnum);

                } else {
                    Toast.makeText(getBaseContext(), "inspección guardada exitosamente", Toast.LENGTH_SHORT).show();
                    Intent ABactividades = new Intent(Inspeccion.this, Actividades.class);
                    ABactividades.putExtra("Nombre", Nombre);
                    ABactividades.putExtra("IP", IP);
                    startActivity(ABactividades);
                }
                super.onPostExecute(s);
            }else{
                Toast.makeText(getBaseContext(), "Seleccione al menos una opción", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class ObtenerEfectos extends AsyncTask<Spinner, Void, ArrayAdapter<String>> {
        Context contexto;
        ArrayList<String> listaefectos = new ArrayList<String>();

        public void cargarcontenido(Context contexto) {
            this.contexto = contexto;
        }

        @Override
        protected ArrayAdapter<String> doInBackground(Spinner... params) {

            list = params[0];
            String cli;
            URL url = null;

            try {
                url = new URL("http://"+IP+"/ACCESS/php/Obefectofalla.php?Equipo="+Equipo);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0" +
                        " (Linux; Android 1.5; es-ES) Ejemplo HTTP");

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

                    listaefectos.clear();
                    if (resultJSON.equals("1")) {
                        JSONArray arrayJson = respuestaJSON.getJSONArray("efecto");
                        for (int i = 0; i < arrayJson.length(); i++) {
                            JSONObject objetoJson = arrayJson.getJSONObject(i);
                            cli = objetoJson.getString("Efectos_de_Falla");
                            listaefectos.add(cli);
                        }
                    } else if (resultJSON.equals("2")) {
                        cli = "NO hay efectos de falla registrados";
                        listaefectos.add(cli);
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, android.R.layout.simple_list_item_1, listaefectos);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, R.layout.spinner_item,listaefectos);

            return adapter;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

        @Override
        protected void onPostExecute(ArrayAdapter<String> result) {
            progressDialogEfect.dismiss();

            if (result.getCount()!=0){

                listaJson.setVisibility(View.VISIBLE);
                txtfallo.setVisibility(View.GONE);
                Llenado="True";

            list.setAdapter(result);
            list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Select = list.getSelectedItem().toString();
                    //Toast.makeText(getBaseContext(), Select, Toast.LENGTH_SHORT).show();
                    Inconv=Select.replaceAll(" ","%20");
                    Inconveniente=Inconv;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            }else{
                txtfallo.setVisibility(View.VISIBLE);
                listaJson.setVisibility(View.GONE);
                Llenado="False";
            }
            if (Llenado.equals("False")){
                existente.setVisibility(View.GONE);
                nuevo.setVisibility(View.VISIBLE);
            }
        }
    }

    public class ObtnumOTM extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            String Num = "";

            try {
                URL url = new URL(cadena);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0" + " (Linux; Android 1.5; es-ES) Ejemplo HTTP");
                int respuesta = connection.getResponseCode();
                StringBuilder result = new StringBuilder();

                if (respuesta == HttpURLConnection.HTTP_OK) {
                    //progressDialog.dismiss();
                    InputStream in = new BufferedInputStream(connection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    JSONObject respuestaJSON = new JSONObject(result.toString());
                    String resultJSON = respuestaJSON.getString("NumOTM");
                    Num= resultJSON;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return Num;
        }
        @Override
        protected void onPreExecute () {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute (String s){

            progressDialognum.dismiss();

            numotm=Integer.parseInt(s)+1;
            numero=String.valueOf(numotm);
            //Toast.makeText(getBaseContext(), Inconv, Toast.LENGTH_SHORT).show();
            Toast.makeText(getBaseContext(), "inspección guardada exitosamente", Toast.LENGTH_SHORT).show();
            Intent ABOtm = new Intent(Inspeccion.this, Otm.class);
            ABOtm.putExtra("Equipo", Equipo);
            ABOtm.putExtra("Averias", Averias);
            ABOtm.putExtra("Nombre", Nombre);
            ABOtm.putExtra("Efectfallo",Inconv);
            ABOtm.putExtra("NumOtm",numero);
            ABOtm.putExtra("IP",IP);
            ABOtm.putExtra("Origen",Origen);
            startActivity(ABOtm);
        }

        @Override
        protected void onProgressUpdate (Void...values){
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled (String s){
            super.onCancelled(s);
        }

    }
}
