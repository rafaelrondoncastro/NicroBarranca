package com.oneapp.Mantenimiento;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

public class InsumosOTM extends AppCompatActivity {

    Bundle extras;
    String IP,NumOtm,resultJSON,efec,insumos,insumootm,refe,referencias,referenciasotm,precioinsumo,
            Cantidadinsumo,numresp,Nombre,Envio="noecho",Codigo,Prec,Precios,Origen,TipoMant,Equipo,cant;
    Spinner insumo,referencia;
    Spinner insumoJson,referenciaJson;
    Button regrasaaejecutotm,envinsumos;
    EnviarInsumo hiloEnv;
    EditText Cantidad;
    TextView Preci;
    ArrayList<String> listainsumo = new ArrayList<String>();
    ArrayList<String> listareferencia = new ArrayList<String>();
    String[] listaPrecios;
    int n,cantnum,Newcant,Cantidadinsumonum;
    ObtCantInsumo hiloCant;
    ProgressDialog progressDialogCant;
    Envianewcant hilonewcant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insumos_otm);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        insumoJson=(Spinner)findViewById(R.id.insumos);
        referenciaJson=(Spinner)findViewById(R.id.referencias);
        regrasaaejecutotm=(Button)findViewById(R.id.regresar);
        envinsumos=(Button)findViewById(R.id.enviar);
        Preci=(TextView) findViewById(R.id.Preci);
        Cantidad=(EditText)findViewById(R.id.cantidad);

        Intent intent=getIntent();
        extras = intent.getExtras();
        NumOtm = extras.getString("NumOtm");
        Nombre = extras.getString("Nombre");
        Codigo = extras.getString("Codigo");
        Origen = extras.getString("Origen");

            if (Origen.equals("Prev")){
                TipoMant = extras.getString("TipoMant");
                Equipo = extras.getString("Equipo");
            }
        IP = extras.getString("IP");

        Obtinsumos obpieza = new Obtinsumos();
        obpieza.cargarcontenido(getApplicationContext());
        obpieza.execute(insumoJson);

        envinsumos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                precioinsumo=Preci.getText().toString();
                Cantidadinsumo=Cantidad.getText().toString();

                if (precioinsumo.equals("") || Cantidadinsumo.equals("")){
                    Toast.makeText(getBaseContext(), "Ingrese la cantidad y el precio por unidad", Toast.LENGTH_SHORT).show();
                }
                else{
                    hiloEnv = new EnviarInsumo();
                    String urlnum = "http://"+IP+"/ACCESS/php/GuardarInsumos.php?NumOTM="+NumOtm+"&Insumos="+insumootm+"&Referencias="+referenciasotm+"&Cantidad="+Cantidadinsumo+"&Costo="+precioinsumo;
                    hiloEnv.execute(urlnum);
                }
            }
        });

        regrasaaejecutotm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Envio.equals("echo")){
                    if (Origen.equals("Corr")){
                        Intent ABejecotm = new Intent(getApplicationContext(), Ejecutaotm.class);
                        ABejecotm.putExtra("NumOtm",NumOtm);
                        ABejecotm.putExtra("Nombre",Nombre);
                        ABejecotm.putExtra("Insumos","Lleno");
                        ABejecotm.putExtra("IP",IP);
                        startActivity(ABejecotm);
                    }else if (Origen.equals("Prev")){
                        Intent ABlistMant = new Intent(getApplicationContext(), ListMant.class);
                        ABlistMant.putExtra("NumOtm",NumOtm);
                        ABlistMant.putExtra("Nombre",Nombre);
                        ABlistMant.putExtra("Insumos","Lleno");
                        ABlistMant.putExtra("IP",IP);
                        ABlistMant.putExtra("TipoMant",TipoMant);
                        ABlistMant.putExtra("Equipo",Equipo);
                        startActivity(ABlistMant);
                    }
                }else{
                    Toast.makeText(getBaseContext(), "Es necesario enviar almenos un insumo", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    class Obtinsumos extends AsyncTask<Spinner, Void, ArrayAdapter<String>> {
        Context contexto;
        private void cargarcontenido(Context contexto) {
            this.contexto = contexto;
        }
        @Override
        protected ArrayAdapter<String> doInBackground(Spinner... params) {
            insumo = params[0];

            try {
                URL url = new URL("http://"+IP+"/ACCESS/php/Obinsumos.php");
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
                    resultJSON = respuestaJSON.getString("estado");

                    if (resultJSON.equals("1")) {
                        JSONArray arrayJson = respuestaJSON.getJSONArray("insumo");
                        for (int i = 0; i < arrayJson.length(); i++) {
                            JSONObject objetoJson = arrayJson.getJSONObject(i);
                            efec = objetoJson.getString("Insumos");
                            listainsumo.add(efec);
                        }
                    } else if (resultJSON.equals("2")) {
                        efec = "NO se encontraron insumos";
                        listainsumo.add(efec);

                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, android.R.layout.simple_list_item_1, listainsumo);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, R.layout.spinner_item,listainsumo);
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

            //Toast.makeText(getBaseContext(), efec, Toast.LENGTH_SHORT).show();

            insumo.setAdapter(result);
            insumo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    insumos = insumo.getSelectedItem().toString();
                    insumootm=insumos.replaceAll(" ","%20");
                    //Inconveniente=Inconv;
                    Obtreferencia obreferencia = new Obtreferencia();
                    obreferencia.cargarcontenido(getApplicationContext());
                    obreferencia.execute(referenciaJson);

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }
    class Obtreferencia extends AsyncTask<Spinner, Void, ArrayAdapter<String>> {
        Context contexto;
        private void cargarcontenido(Context contexto) {
            this.contexto = contexto;
        }
        @Override
        protected ArrayAdapter<String> doInBackground(Spinner... params) {
            referencia = params[0];
            try {
                URL url = new URL("http://"+IP+"/ACCESS/php/Obreferencias.php?Insumo="+insumootm);
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
                    resultJSON = respuestaJSON.getString("estado");

                    listareferencia.clear();
                    if (resultJSON.equals("1")) {
                        JSONArray arrayJson = respuestaJSON.getJSONArray("Referencia");
                        n=arrayJson.length();
                        listaPrecios=new String[n];
                        for (int i = 0; i < arrayJson.length(); i++) {
                            JSONObject objetoJson = arrayJson.getJSONObject(i);
                            refe = objetoJson.getString("Referencia");
                            Prec = objetoJson.getString("Precio");
                            listareferencia.add(refe);
                            listaPrecios[i]=Prec;
                        }
                    } else if (resultJSON.equals("2")) {
                        efec = "NO se encontraron Referencias";
                        listareferencia.add(refe);

                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, android.R.layout.simple_list_item_1, listareferencia);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, R.layout.spinner_item,listareferencia);
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

            //Toast.makeText(getBaseContext(),refe, Toast.LENGTH_SHORT).show();

            referencia.setAdapter(result);
            referencia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    referencias = referencia.getSelectedItem().toString();
                    int num=referencia.getSelectedItemPosition();
                    referenciasotm=referencias.replaceAll(" ","%20");
                    Precios = listaPrecios[num];
                    Preci.setText(Precios);


                    //Inconveniente=Inconv;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    public class EnviarInsumo extends AsyncTask<String, Void, String> {

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
                    String resultJSON = respuestaJSON.getString("estado");

                    if (resultJSON.equals("1")) {
                        JSONObject ObjetJsonresp = respuestaJSON.getJSONObject("equipo");
                        numresp = ObjetJsonresp.getString("numeroOTM");

                    } else if (resultJSON.equals("2")) {
                         numresp= "NO se encontraron Referencias";
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return numresp;
        }
        @Override
        protected void onPreExecute () {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute (String s){

            Toast.makeText(getBaseContext(), "Insumo Agregado", Toast.LENGTH_SHORT).show();
            Envio="echo";

            hiloCant = new ObtCantInsumo();
            String urlnum = "http://"+IP+"/ACCESS/php/Obcantinsumos.php?Insumos="+insumootm+"&Referencia="+referenciasotm;

            progressDialogCant = ProgressDialog.show(InsumosOTM.this,
                    "Espere un momneto", "Obteniendo cantidad de Insumos...");

            hiloCant.execute(urlnum);
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

    public class ObtCantInsumo extends AsyncTask<String, Void, String> {

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
                    String resultJSON = respuestaJSON.getString("estado");

                    if (resultJSON.equals("1")) {
                        JSONArray arrayJson = respuestaJSON.getJSONArray("insumo");
                        for (int i = 0; i < arrayJson.length(); i++) {
                            JSONObject objetoJson = arrayJson.getJSONObject(i);
                            cant = objetoJson.getString("Inventario");
                        }
                    } else if (resultJSON.equals("2")) {
                        cant = "NO hay Referencias";
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return cant;
        }
        @Override
        protected void onPreExecute () {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute (String s){

            progressDialogCant.dismiss();

            cantnum=Integer.parseInt(s);
            Cantidadinsumonum=Integer.parseInt(Cantidadinsumo);
            Newcant=cantnum-Cantidadinsumonum;

            hilonewcant = new Envianewcant();
            String urlnum = "http://"+IP+"/ACCESS/php/Editarcantinsumos.php?Insumos="+insumootm+"&Referencia="+referenciasotm+"&Newcant="+Newcant;
            hilonewcant.execute(urlnum);
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

    public class Envianewcant extends AsyncTask<String, Void, String> {

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

            Toast.makeText(getBaseContext(), "Valor editado", Toast.LENGTH_SHORT).show();
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
