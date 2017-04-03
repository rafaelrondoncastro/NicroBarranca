package com.oneapp.Mantenimiento;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

public class auditoria extends AppCompatActivity {

    ListView listaJson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auditoria);

        listaJson = (ListView)findViewById(R.id.listatablas);

        Tarea1 tarea1 = new Tarea1();
        tarea1.cargarcontenido(getApplicationContext());
        tarea1.execute(listaJson);
    }

    class Tarea1 extends AsyncTask<ListView, Void, ArrayAdapter<String>> {
        Context contexto;
        ListView list;
        InputStream is;
        ArrayList<String> listaservicios =new ArrayList<String>();

        public void cargarcontenido(Context contexto){
            this.contexto = contexto;
        }

        @Override
        protected ArrayAdapter<String> doInBackground(ListView... params) {
            list=params[0];
            String cli;
            URL url = null;

            try {
                url = new URL("http://192.168.1.52/access/Consultar.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //Abrir la conexión
                connection.setRequestProperty("User-Agent", "Mozilla/5.0" +
                        " (Linux; Android 1.5; es-ES) Ejemplo HTTP");
                //connection.setHeader("content-type", "application/json");

                int respuesta = connection.getResponseCode();
                StringBuilder result = new StringBuilder();

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
                    JSONObject respuestaJSON = new JSONObject(result.toString());   //Creo un JSONObject a partir del StringBuilder pasado a cadena
                    //Accedemos al vector de resultados
                    // results es el nombre del campo en el JSON

                        JSONArray arrayJson = respuestaJSON.getJSONArray("");
                        for (int i = 0; i < arrayJson.length(); i++) {
                            JSONObject objetoJson = arrayJson.getJSONObject(i);
                            cli = objetoJson.getString("Preguntas") + "\n";
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
            ArrayAdapter<String> adaptador = new ArrayAdapter<String>(contexto, android.R.layout.simple_list_item_1, listaservicios);
            return adaptador;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayAdapter<String> result) {
            list.setAdapter(result);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String name=list.getItemAtPosition(position).toString();
                    Toast.makeText(getBaseContext(),name,Toast.LENGTH_SHORT).show();
                    //Intent ABServespecific = new Intent(auditoria.this, ServEspesificacion.class);
                    //ABServespecific.putExtra("Service",name);
                    //startActivity(ABServespecific);
                }
            });
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }
}
