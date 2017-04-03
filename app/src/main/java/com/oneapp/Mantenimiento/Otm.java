package com.oneapp.Mantenimiento;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
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

public class Otm extends AppCompatActivity {

    Bundle extras;
    Button guiasol,solOTM;
    Spinner Otm, Asigna;
    Spinner OtmJson, AsignaJson;
    String Tipo,Asignado,Equipo,Averias,Nombre,NomAsignado,Fallo,numero,Nombre20,IP,Origen,envio="null",resultJSON,condicion,NumeroI;
    EnviarSolOTM hilo;
    LinearLayout Laysolotm;
    Obtenerequipo hiloequicierre;
    ProgressDialog progressDialogEqui;
    EnviacreaCierre hilocreaCierre;
    EnviaCierre hiloCierre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otm);

        guiasol=(Button)findViewById(R.id.guiasol);
        OtmJson=(Spinner)findViewById(R.id.Tipo);
        AsignaJson=(Spinner)findViewById(R.id.asigna);
        solOTM=(Button)findViewById(R.id.SolOTM);
        Laysolotm=(LinearLayout)findViewById(R.id.LaySolOTM);

        Intent intent = getIntent();
        extras = intent.getExtras();
        Equipo =extras.getString("Equipo");
        Averias =extras.getString("Averias");
        Nombre=extras.getString("Nombre");
        Fallo=extras.getString("Efectfallo");
        numero=extras.getString("NumOtm");
        IP=extras.getString("IP");
        Origen=extras.getString("Origen");

        Nombre20=Nombre.replace(" ","%20");

        if (Averias.equals("null")){
            Laysolotm.setVisibility(View.GONE);
            guiasol.setVisibility(View.VISIBLE);
        }else{
            Laysolotm.setVisibility(View.VISIBLE);
            guiasol.setVisibility(View.GONE);
        }

        Tarea1 tarea1 = new Tarea1();
        tarea1.cargarcontenido(getApplicationContext());
        tarea1.execute(OtmJson);

        Tarea2 tarea2 = new Tarea2();
        tarea2.cargarcontenido(getApplicationContext());
        tarea2.execute(AsignaJson);

        guiasol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getBaseContext(), Fallo, Toast.LENGTH_SHORT).show();
                Intent ABAverias = new Intent(getApplicationContext(), AveriasOTM.class);
                ABAverias.putExtra("Equipo",Equipo);
                ABAverias.putExtra("Nombre",Nombre);
                ABAverias.putExtra("Fallo",Fallo);
                ABAverias.putExtra("NumOTM",numero);
                ABAverias.putExtra("IP",IP);
                startActivity(ABAverias);
            }
        });

        solOTM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                envio = "echo";
                //Toast.makeText(getBaseContext(), Nombre+" "+Nombre20, Toast.LENGTH_LONG).show();
                if (Averias.equals("null")){
                    Toast.makeText(getBaseContext(), "Ingrese al menos una averia", Toast.LENGTH_SHORT).show();
                }
                else{
                    hilo = new EnviarSolOTM();
                    String cadena = "http://"+IP+"/ACCESS/php/GuardarSolOTM.php?NumeroOTM="+numero+"&Solicita="+Nombre20+"&Codigo="+Equipo+"&Tipo="+Tipo+"&Asignado="+Asignado+"&Ejecutada=No&Clase=Correctivo";
                    hilo.execute(cadena);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (envio.equals("echo")){
            final AlertDialog.Builder alertainspeccion = new AlertDialog.Builder(Otm.this);
            alertainspeccion.setTitle("Advertencia");
            alertainspeccion.setMessage("La inspección fue guardada y no es posible editarla, a que ventana desea ir");
            alertainspeccion.setIcon(R.drawable.logo);
            alertainspeccion.setCancelable(false);

            alertainspeccion.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface alertasalida, int which) {
                    alertasalida.cancel();
                }
            });

            alertainspeccion.setPositiveButton("Equipos", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface alertasalida, int which) {
                    Intent ABEquipos = new Intent(Otm.this, Equipos.class);
                    ABEquipos.putExtra("Nombre", Nombre);
                    ABEquipos.putExtra("IP", IP);
                    ABEquipos.putExtra("Origen", Origen);
                    startActivity(ABEquipos);
                }
            });
            alertainspeccion.setNegativeButton("Inicio", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface alertasalida, int which) {
                    Intent ABopc = new Intent(Otm.this, Actividades.class);
                    ABopc.putExtra("Nombre", Nombre);
                    ABopc.putExtra("IP", IP);
                    ABopc.putExtra("Origen", Origen);
                    startActivity(ABopc);
                }
            });
            alertainspeccion.create();
            alertainspeccion.show();
        }else{
            Toast.makeText(getBaseContext(), "Envie primero la solicitud", Toast.LENGTH_LONG).show();
        }


    }

    class Tarea1 extends AsyncTask<Spinner, Void, ArrayAdapter<String>> {
        Context contexto;
        ArrayList<String> listaservicios = new ArrayList<String>();

        public void cargarcontenido(Context contexto) {
            this.contexto = contexto;
        }

        @Override
        protected ArrayAdapter<String> doInBackground(Spinner... params) {
            Otm = params[0];
            String cli;
            URL url = null;

            try {
                url = new URL("http://"+IP+"/ACCESS/php/Obtipo.php");
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
                    if (resultJSON.equals("1")) {
                        JSONArray arrayJson = respuestaJSON.getJSONArray("tipo");
                        for (int i = 0; i < arrayJson.length(); i++) {
                            JSONObject objetoJson = arrayJson.getJSONObject(i);
                            cli = objetoJson.getString("TipoOTM");
                            listaservicios.add(cli);
                        }
                    } else if (resultJSON.equals("2")) {
                        cli = "NO hay Servicios";
                        listaservicios.add(cli);
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, android.R.layout.simple_list_item_1, listaservicios);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, R.layout.spinner_item,listaservicios);

            return adapter;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //cargacat.getProgress();
        }

        @Override
        protected void onPostExecute(ArrayAdapter<String> result) {
            //cargacat.setVisibility(View.GONE);
            Otm.setAdapter(result);

            Otm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String Select = Otm.getSelectedItem().toString();
                    //Toast.makeText(getBaseContext(), Select, Toast.LENGTH_SHORT).show();
                    Tipo=Select;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    class Tarea2 extends AsyncTask<Spinner, Void, ArrayAdapter<String>> {
        Context contexto;
        InputStream is;
        ArrayList<String> listaservicios = new ArrayList<String>();

        public void cargarcontenido(Context contexto) {
            this.contexto = contexto;
        }

        @Override
        protected ArrayAdapter<String> doInBackground(Spinner... params) {
            Asigna = params[0];
            String cli;
            URL url = null;

            try {
                url = new URL("http://"+IP+"/ACCESS/php/ObOper.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //Abrir la conexión
                connection.setRequestProperty("User-Agent", "Mozilla/5.0" +
                        " (Linux; Android 1.5; es-ES) Ejemplo HTTP");
                //connection.setHeader("content-type", "application/json");

                int respuesta = connection.getResponseCode();
                StringBuilder result = new StringBuilder();

                if (respuesta == HttpURLConnection.HTTP_OK) {


                    InputStream in = new BufferedInputStream(connection.getInputStream());  // preparo la cadena de entrada

                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));  // la introduzco en un BufferedReader

                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);        // Paso toda la entrada al StringBuilder
                    }

                    JSONObject respuestaJSON = new JSONObject(result.toString());   //Creo un JSONObject a partir del StringBuilder pasado a cadena
                    //Accedemos al vector de resultados
                    String resultJSON = respuestaJSON.getString("estado");   // results es el nombre del campo en el JSON

                    if (resultJSON.equals("1")) {
                        JSONArray arrayJson = respuestaJSON.getJSONArray("Nombres");
                        for (int i = 0; i < arrayJson.length(); i++) {
                            JSONObject objetoJson = arrayJson.getJSONObject(i);
                            cli = objetoJson.getString("Nombres");
                            listaservicios.add(cli);
                        }
                    } else if (resultJSON.equals("2")) {
                        cli = "NO hay Servicios";
                        listaservicios.add(cli);
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(contexto, android.R.layout.simple_list_item_1, listaservicios);
            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(contexto, R.layout.spinner_item,listaservicios);

            return adapter2;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //cargacat.getProgress();
        }

        @Override
        protected void onPostExecute(ArrayAdapter<String> result) {
            //cargacat.setVisibility(View.GONE);
            Asigna.setAdapter(result);

            Asigna.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String NAsignado = Asigna.getSelectedItem().toString();
                    //Toast.makeText(getBaseContext(), NAsignado, Toast.LENGTH_SHORT).show();
                    NomAsignado=NAsignado.replaceAll(" ","%20");
                    Asignado=NomAsignado;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    public class EnviarSolOTM extends AsyncTask<String, Void, String> {

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
                connection.getResponseCode();

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

            hiloequicierre = new Obtenerequipo();
            String urlminombre = "http://"+IP+"/ACCESS/php/Obequipoabierto.php?Equipo="+Equipo;

            progressDialogEqui = ProgressDialog.show(Otm.this,
                    "Espere un momneto", "Comprobando Equipo...");

            hiloequicierre.execute(urlminombre);
        }
    }

    public class Obtenerequipo extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];

            try {
                URL url = new URL(cadena);
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
                    resultJSON = respuestaJSON.getString("estado");
                    if (resultJSON.equals("1")) {
                        JSONArray arrayJson = respuestaJSON.getJSONArray("Equipos");
                        if (arrayJson.length()>0){
                            JSONObject objetoJson = arrayJson.getJSONObject(0);
                            NumeroI = objetoJson.getString("Numero");
                            condicion="Abierto";
                        }else{
                            condicion="Cerrado";
                        }
                    }
                }

            } catch (MalformedURLException e) {
                final AlertDialog.Builder alertaconexion = new AlertDialog.Builder(Otm.this);
                alertaconexion.setTitle("Importante");
                alertaconexion.setMessage("No se encuentra conectado a la red de la base de datos, que desea hacer?" );
                alertaconexion.setIcon(R.drawable.logo);
                alertaconexion.setCancelable(false);

                alertaconexion.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface alertasalida, int which) {
                        alertasalida.cancel();
                    }
                });
                alertaconexion.setPositiveButton("Salir", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface alertasalida, int which) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
                alertaconexion.setNegativeButton("Intentar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface alertasalida, int which) {

                    }
                });
                alertaconexion.create();
                alertaconexion.show();
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return condicion;
        }

        @Override
        protected void onPreExecute () {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute (String s){
            progressDialogEqui.dismiss();
            if (s.equals("Abierto")) {
                hiloCierre = new EnviaCierre();
                String urlnum = "http://"+IP+"/ACCESS/php/EditarCierre.php?Numero="+NumeroI+"&Condicion=cerrado";
                hiloCierre.execute(urlnum);
            }

            Toast.makeText(getBaseContext(), "Solicitud realizada Exitosamente", Toast.LENGTH_SHORT).show();
            Intent ABactiv = new Intent(Otm.this, Actividades.class);
            ABactiv.putExtra("Nombre", Nombre);
            ABactiv.putExtra("IP", IP);
            startActivity(ABactiv);
            super.onPostExecute(s);

            super.onPostExecute(s);
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
    public class EnviaCierre extends AsyncTask<String, Void, String> {

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
                    InputStream in = new BufferedInputStream(connection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    JSONObject respuestaJSON = new JSONObject(result.toString());
                    resultJSON = respuestaJSON.getString("estado");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return resultJSON;
        }
        @Override
        protected void onPreExecute () {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute (String s){

            hilocreaCierre = new EnviacreaCierre();
            String urlnum = "http://"+IP+"/ACCESS/php/GuardarCierre.php?Numero="+NumeroI+"&Equipo="+Equipo;
            hilocreaCierre.execute(urlnum);

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

    public class EnviacreaCierre extends AsyncTask<String, Void, String> {

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
                    InputStream in = new BufferedInputStream(connection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    JSONObject respuestaJSON = new JSONObject(result.toString());
                    resultJSON = respuestaJSON.getString("estado");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return resultJSON;
        }
        @Override
        protected void onPreExecute () {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute (String s){

            Toast.makeText(getBaseContext(), "Solicitud realizada Exitosamente", Toast.LENGTH_SHORT).show();
            Intent ABactiv = new Intent(Otm.this, Actividades.class);
            ABactiv.putExtra("Nombre", Nombre);
            ABactiv.putExtra("IP", IP);
            startActivity(ABactiv);
            super.onPostExecute(s);

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
