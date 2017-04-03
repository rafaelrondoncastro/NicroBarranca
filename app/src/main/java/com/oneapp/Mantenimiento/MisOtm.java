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

public class MisOtm extends AppCompatActivity {

    Bundle extras;
    String Nombre,cli,resultJSON,Nombre20,IP;
    ListView listaJson;
    ProgressBar cargalista;
    int conf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_otm);

        listaJson=(ListView)findViewById(R.id.misotm);
        cargalista=(ProgressBar)findViewById(R.id.cargamisotm);

        cargalista.setVisibility(View.VISIBLE);
        Intent intent=getIntent();
        extras = intent.getExtras();
        Nombre = extras.getString("Nombre");
        IP = extras.getString("IP");
        Nombre20 = Nombre.replace(" ","%20");

        //Toast.makeText(getBaseContext(),Nombre,Toast.LENGTH_SHORT).show();

        Tarea1 tarea1 = new Tarea1();
        tarea1.cargarcontenido(getApplicationContext());
        tarea1.execute(listaJson);
    }
    public void onBackPressed() {
        Intent ABactividades = new Intent(getApplicationContext(), Actividades.class);
        ABactividades.putExtra("Nombre",Nombre);
        ABactividades.putExtra("IP",IP);
        startActivity(ABactividades);
    }

    class Tarea1 extends AsyncTask<ListView, Void, ArrayAdapter<String>> {
        Context contexto;
        ListView list;
        ArrayList<String> listaservicios =new ArrayList<String>();

        public void cargarcontenido(Context contexto){
            this.contexto = contexto;
        }

        @Override
        protected ArrayAdapter<String> doInBackground(ListView... params) {
            list=params[0];

            try {
                URL url = new URL("http://"+IP+"/ACCESS/php/Obmisordenes.php?Nombre="+Nombre20);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //Abrir la conexión
                connection.setRequestProperty("User-Agent", "Mozilla/5.0" +
                        " (Linux; Android 1.5; es-ES) Ejemplo HTTP");
                //connection.setHeader("content-type", "application/json");

                int respuesta = connection.getResponseCode();
                StringBuilder result = new StringBuilder();
                conf=respuesta;
                if (respuesta == HttpURLConnection.HTTP_OK) {


                    InputStream in = new BufferedInputStream(connection.getInputStream());  // preparo la cadena de entrada

                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));  // la introduzco en un BufferedReader

                    // El siguiente proceso lo hago porque el JSONOBject necesita un String y tengo
                    // que tranformar el BufferedReader a String. Esto lo hago a traves de un
                    // StringBuilder.

                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);        // Paso toda la entrada al StringBuilder
                    }

                    //Creamos un objeto JSONObject para poder acceder a los atributos (campos) del objeto.
                    JSONObject respuestaJSON = new JSONObject(result.toString());
                    resultJSON = respuestaJSON.getString("estado");

                    if (resultJSON.equals("1")) {
                        JSONArray arrayJson = respuestaJSON.getJSONArray("Solicitud");
                        for (int i = 0; i < arrayJson.length(); i++) {
                            JSONObject objetoJson = arrayJson.getJSONObject(i);
                            cli = objetoJson.getString("Resumen");
                            listaservicios.add(cli);
                        }
                    } else if (resultJSON.equals("2")) {
                        cli = "NO Tiene Solicitudes para OTM";
                        listaservicios.add(cli);
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
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, android.R.layout.simple_list_item_1, listaservicios);

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
                final AlertDialog.Builder alertaconexion = new AlertDialog.Builder(MisOtm.this);
                alertaconexion.setTitle("Importante");
                alertaconexion.setMessage("No se encuentra conectado a la red de la base de datos, que desea hacer?" );
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

            cargalista.setVisibility(View.GONE);
            list.setAdapter(result);
            result.notifyDataSetChanged();

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String name= (String) list.getItemAtPosition(position);
                    String numotm=name.substring(0, 4);
                    String Codigo=name.substring(5,13);

                    Intent ABejecutaotm = new Intent(MisOtm.this, Ejecutaotm.class);
                    ABejecutaotm.putExtra("NumOtm",numotm);
                    ABejecutaotm.putExtra("Nombre", Nombre);
                    ABejecutaotm.putExtra("Insumos","vacio");
                    ABejecutaotm.putExtra("Codigo",Codigo);
                    ABejecutaotm.putExtra("IP",IP);
                    startActivity(ABejecutaotm);
                }
            });
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            cargalista.getProgress();
        }

    }
}
