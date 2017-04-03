package com.oneapp.Mantenimiento;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
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

public class ListMant extends AppCompatActivity {

    Bundle extras;
    String IP, Nombre, Equipo, TipoMant, Activ, NumOtm, Nombre20, resultJSON, Actividad,mantnorealizado;
    ListView listaJson, list;
    ArrayList<String> listamant = new ArrayList<String>();
    ProgressDialog progressDialogmant;
    TextView titulo,mensaje;
    Button Iniciar;
    ObtnumOTM hiloNum;
    EnviarSolOTM hiloenvsol;
    ProgressDialog progressDialognum,progressDialogguarda,progressDialogguardaaverias;
    int NumAct,ordenes;
    EnviarAverias hiloaverias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_mant);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        listaJson = (ListView) findViewById(R.id.listmant);
        titulo = (TextView) findViewById(R.id.titulo);
        Iniciar = (Button) findViewById(R.id.Iniciar);
        mensaje = (TextView) findViewById(R.id.mensaje);

        Intent intent = getIntent();
        extras = intent.getExtras();
        Nombre = extras.getString("Nombre");
        IP = extras.getString("IP");
        Equipo = extras.getString("Equipo");
        TipoMant = extras.getString("TipoMant");

        Iniciar.setVisibility(View.GONE);

        Nombre20 = Nombre.replace(" ", "%20");

        titulo.setText("Mantenimiento " + TipoMant + " del Equipo " + Equipo);

        CargarActividades cargaactividades = new CargarActividades();
        cargaactividades.cargarcontenido(getApplicationContext());
        progressDialogmant = ProgressDialog.show(this,
                "Espere un momneto", "Cargando Actividades...");

        cargaactividades.execute(listaJson);
    }

    class CargarActividades extends AsyncTask<ListView, Void, ArrayAdapter<String>> {
        Context contexto;

        public void cargarcontenido(Context contexto) {
            this.contexto = contexto;
        }

        @Override
        protected ArrayAdapter<String> doInBackground(ListView... params) {
            list = params[0];

            try {
                URL url = new URL("http://" + IP + "/ACCESS/php/Obmantenimientos.php?Equipo=" + Equipo + "&Frecuencia=" + TipoMant);
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
                        JSONArray arrayJson = respuestaJSON.getJSONArray("Actividad");
                        NumAct = arrayJson.length();
                        for (int i = 0; i < arrayJson.length(); i++) {
                            JSONObject objetoJson = arrayJson.getJSONObject(i);
                            Activ = objetoJson.getString("Actividad");
                            listamant.add(Activ);
                        }
                    } else if (resultJSON.equals("2")) {
                        Activ = "NO hay Equipos";
                        listamant.add(Activ);
                    }
                }
            } catch (MalformedURLException e) {
                Toast.makeText(getBaseContext(), "error de conexión", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, android.R.layout.simple_list_item_1, listamant);

            return adapter;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayAdapter<String> result) {
            progressDialogmant.dismiss();
            if (result.getCount() != 0) {
                list.setAdapter(result);
                result.notifyDataSetChanged();
            } else {
                Toast.makeText(getBaseContext(), "Compruebe que la base de datos se encuentra cerrada", Toast.LENGTH_LONG).show();
            }

            hiloNum = new ObtnumOTM();
            String urlnum = "http://" + IP + "/ACCESS/php/ObnumOTM.php";

            progressDialognum = ProgressDialog.show(ListMant.this,
                    "Espere un momneto", "Obteniendo numero de OTM...");

            hiloNum.execute(urlnum);

        }

        @Override
        protected void onProgressUpdate(Void... values) {
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
                    Num = resultJSON;
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
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {

            int num = Integer.parseInt(s);
            int numasuno = num + 1;
            NumOtm = String.valueOf(numasuno);
            progressDialognum.dismiss();

            Confirmant confirmant = new Confirmant();
            confirmant.cargarcontenido(getApplicationContext());
            progressDialogmant = ProgressDialog.show(ListMant.this,
                    "Espere un momneto", "Confirmando OTM's...");
            confirmant.execute(listaJson);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }

    }

    class Confirmant extends AsyncTask<ListView, Void, ArrayAdapter<String>> {
        Context contexto;

        public void cargarcontenido(Context contexto) {
            this.contexto = contexto;
        }

        @Override
        protected ArrayAdapter<String> doInBackground(ListView... params) {
            list = params[0];

            try {
                URL url = new URL("http://"+IP+"/ACCESS/php/Confirmarmant.php?Codigo="+Equipo);
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
                        JSONArray arrayJson = respuestaJSON.getJSONArray("OTMs");
                        ordenes = arrayJson.length();

                        if (ordenes==0){
                            mantnorealizado="";
                        }else{
                            for (int i = 0; i < arrayJson.length(); i++) {
                                JSONObject objetoJson = arrayJson.getJSONObject(i);
                                mantnorealizado = objetoJson.getString("Clase");
                                Activ = objetoJson.getString("Clase");
                                listamant.add(Activ);
                            }
                        }

                    } else if (resultJSON.equals("2")) {
                        Activ = "NO hay Equipos";
                        listamant.add(Activ);
                    }
                }
            } catch (MalformedURLException e) {
                Toast.makeText(getBaseContext(), "error de conexión", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, android.R.layout.simple_list_item_1, listamant);

            return adapter;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayAdapter<String> result) {

            progressDialogmant.dismiss();

            if (mantnorealizado.equals("")){
                Iniciar.setVisibility(View.VISIBLE);
                Iniciar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hiloenvsol = new EnviarSolOTM();
                        String cadena = "http://" + IP + "/ACCESS/php/GuardarSolOTM.php?NumeroOTM=" + NumOtm + "&Solicita=" + Nombre20 + "&Codigo=" + Equipo + "&Tipo=Normal&Asignado=" + Nombre20 + "&Ejecutada=No&Clase=Preventivo";

                        progressDialogguarda = ProgressDialog.show(ListMant.this,
                                "Espere un momneto", "Guardando solicitud de OTM...");

                        hiloenvsol.execute(cadena);
                    }
                });
            }else{
                mensaje.setText("Este equipo tiene una orden de trabajo pendiente de tipo "+mantnorealizado+" es necesario realizarla antes de solicitar una nueva orden de trabajo");
            }


        }

        @Override
        protected void onProgressUpdate(Void... values) {
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
            progressDialogguarda.dismiss();
            Toast.makeText(getBaseContext(), "Solicitud realizada Exitosamente", Toast.LENGTH_SHORT).show();

            for (int i = 0; i < NumAct; i++) {
                Actividad=list.getItemAtPosition(i).toString().replace(" ","%20");
                //Toast.makeText(getBaseContext(), Actividad, Toast.LENGTH_SHORT).show();
                hiloaverias = new EnviarAverias();
                String cadena = "http://"+IP+"/ACCESS/php/GuardarAverias.php?NumOTM=" + NumOtm + "&EfectoPresentado=No%20aplica&ModoEnQueSePresnto=No%20aplica&Componente=No%20aplica&Causa=No%20aplica&ControlesRealizados="+Actividad;

                progressDialogguardaaverias = ProgressDialog.show(ListMant.this,
                "Espere un momneto", "Guardando actividades...");

                hiloaverias.execute(cadena);
            }
        }
    }

    public class EnviarAverias extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            String Guarda = "";

            try {
                URL url = new URL(cadena);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0" +
                        " (Linux; Android 1.5; es-ES) Ejemplo HTTP");
                connection.getResponseCode();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Guarda;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialogguardaaverias.dismiss();
            Intent ABactiv = new Intent(ListMant.this, Actividades.class);
            ABactiv.putExtra("Nombre", Nombre);
            ABactiv.putExtra("IP", IP);
            startActivity(ABactiv);
            super.onPostExecute(s);
        }
    }
}
