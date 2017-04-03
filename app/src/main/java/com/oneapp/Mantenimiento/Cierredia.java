package com.oneapp.Mantenimiento;

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
import android.widget.ListView;
import android.widget.ProgressBar;
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

import static android.R.attr.x;

public class Cierredia extends AppCompatActivity {

    Bundle extras;
    String Nombre,cli,resultJSON,Nombre20,IP,NumeroI,Equipo;
    ListView listaJson;
    int conf;
    EnviaCierre hiloCierre;
    ArrayList<String> listaequipos =new ArrayList<String>();
    String[] listanumeros;
    EnviacreaCierre hilocreaCierre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cierredia);

        listaJson=(ListView)findViewById(R.id.equiposabiertos);

        Intent intent=getIntent();
        extras = intent.getExtras();
        Nombre = extras.getString("Nombre");
        IP = extras.getString("IP");
        Nombre20 = Nombre.replace(" ","%20");

        CargaEquipos cargaequipos = new CargaEquipos();
        cargaequipos.cargarcontenido(getApplicationContext());
        cargaequipos.execute(listaJson);
    }

    class CargaEquipos extends AsyncTask<ListView, Void, ArrayAdapter<String>> {
        Context contexto;
        ListView list;

        public void cargarcontenido(Context contexto){
            this.contexto = contexto;
        }

        @Override
        protected ArrayAdapter<String> doInBackground(ListView... params) {
            list=params[0];

            try {
                URL url = new URL("http://"+IP+"/ACCESS/php/Obequiposabiertos.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0" +
                        " (Linux; Android 1.5; es-ES) Ejemplo HTTP");

                int respuesta = connection.getResponseCode();
                StringBuilder result = new StringBuilder();
                conf=respuesta;
                if (respuesta == HttpURLConnection.HTTP_OK) {


                    InputStream in = new BufferedInputStream(connection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    JSONObject respuestaJSON = new JSONObject(result.toString());
                    resultJSON = respuestaJSON.getString("estado");

                    listanumeros=new String[0];
                    listaequipos.clear();
                    if (resultJSON.equals("1")) {
                        JSONArray arrayJson = respuestaJSON.getJSONArray("Equipos");
                        listanumeros=new String[arrayJson.length()];
                        for (int i = 0; i < arrayJson.length(); i++) {
                            JSONObject objetoJson = arrayJson.getJSONObject(i);
                            cli = objetoJson.getString("Codigo");
                            listanumeros[i] = objetoJson.getString("Numero");
                            listaequipos.add(cli);
                        }
                    } else if (resultJSON.equals("2")) {
                        cli = "NO Tiene Solicitudes para OTM";
                        listaequipos.add(cli);
                    }
                }
            } catch (MalformedURLException e) {
                Toast.makeText(getBaseContext(),"error de conexión",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, android.R.layout.simple_list_item_1, listaequipos);

            return adapter;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayAdapter<String> result) {
            //Toast.makeText(getBaseContext(),resultJSON,Toast.LENGTH_SHORT).show();
            if (conf!=200){
                final AlertDialog.Builder alertaconexion = new AlertDialog.Builder(Cierredia.this);
                alertaconexion.setTitle("Importante");
                alertaconexion.setMessage("No se encuentra conectado a la red de la base de datos, ¿que desea hacer?" );
                alertaconexion.setIcon(R.drawable.logo);
                alertaconexion.setCancelable(false);

                alertaconexion.setNeutralButton("atras", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface alertasalida, int which) {
                        Intent ABactividades = new Intent(getApplicationContext(), Actividades.class);
                        ABactividades.putExtra("Nombre",Nombre);
                        ABactividades.putExtra("IP",IP);
                        startActivity(ABactividades);
                    }
                });
                alertaconexion.setPositiveButton("Salir", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface alertasalida, int which) {
                        finish();
                    }
                });
                alertaconexion.setNegativeButton("Intentar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface alertasalida, int which) {
                        alertasalida.cancel();
                    }
                });
                alertaconexion.create();
                alertaconexion.show();
            }

            list.setAdapter(result);
            result.notifyDataSetChanged();

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Equipo = (String) list.getItemAtPosition(position);
                    NumeroI = listanumeros[position];
                    //Toast.makeText(getBaseContext(), NumeroIns, Toast.LENGTH_SHORT).show();
                    String condicion = "Cerrado";
                    hiloCierre = new EnviaCierre();
                    String urlnum = "http://"+IP+"/ACCESS/php/EditarCierre.php?Numero="+NumeroI+"&Condicion="+condicion;
                    hiloCierre.execute(urlnum);
                }
            });
        }

        @Override
        protected void onProgressUpdate(Void... values) {
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

            CargaEquipos cargaequipos = new CargaEquipos();
            cargaequipos.cargarcontenido(getApplicationContext());
            cargaequipos.execute(listaJson);
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
