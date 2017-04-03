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
import android.widget.EdgeEffect;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Tareas extends AppCompatActivity {

    Bundle extras;
    String Nombre,Origen,Equipo,NumI,IP,cli;
    ListView listaJson;
    int conf;
    ProgressDialog progressDialogTarea;
    Button siguiente;
    TextView titulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tareas);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent intent=getIntent();
        extras = intent.getExtras();
        Nombre = extras.getString("Nombre");
        Equipo = extras.getString("Equipo");
        Origen = extras.getString("Origen");
        NumI = extras.getString("NumI");
        IP = extras.getString("IP");

        listaJson=(ListView)findViewById(R.id.Tareas);
        siguiente=(Button)findViewById(R.id.siguiente);
        titulo = (TextView)findViewById(R.id.titulo);

        String Texto=("Equipo: "+Equipo);
        titulo.setText(Texto);

        Cargartareas cargaequipos = new Cargartareas();
        cargaequipos.cargarcontenido(getApplicationContext());

        progressDialogTarea = ProgressDialog.show(this,
                "Espere un momneto", "Cargando Tareas...");

        cargaequipos.execute(listaJson);
    }

    class Cargartareas extends AsyncTask<ListView, Void, ArrayAdapter<String>> {
        Context contexto;
        ListView list;
        InputStream is;
        ArrayList<String> listatareas =new ArrayList<String>();

        public void cargarcontenido(Context contexto){
            this.contexto = contexto;
        }

        @Override
        protected ArrayAdapter<String> doInBackground(ListView... params) {
            list=params[0];

            try {
                URL url = new URL("http://"+IP+"/ACCESS/php/Obtareasdiarias.php?Equipo="+Equipo);
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
                    String resultJSON = respuestaJSON.getString("estado");

                    if (resultJSON.equals("1")) {
                        JSONArray arrayJson = respuestaJSON.getJSONArray("tarea");
                        for (int i = 0; i < arrayJson.length(); i++) {
                            JSONObject objetoJson = arrayJson.getJSONObject(i);
                            cli = objetoJson.getString("Tarea");
                            listatareas.add(cli);
                        }
                    } else if (resultJSON.equals("2")) {
                        cli = "NO hay Equipos";
                        listatareas.add(cli);
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
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, android.R.layout.simple_list_item_1, listatareas);

            return adapter;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayAdapter<String> result) {
            progressDialogTarea.dismiss();

            if (conf!=200){
                final AlertDialog.Builder alertaconexion = new AlertDialog.Builder(Tareas.this);
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
                        finish();
                    }
                });
                alertaconexion.setNegativeButton("Intentar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface alertasalida, int which) {

                    }
                });
                alertaconexion.create();
                alertaconexion.show();
            }

            if (result.getCount()!=0) {
                list.setAdapter(result);
                result.notifyDataSetChanged();
            }else{
                Toast.makeText(getBaseContext(),"No se encontraron tareas para este equipo",Toast.LENGTH_LONG).show();
            }

            siguiente.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent ABinspeccion = new Intent(Tareas.this, Inspeccion.class);
                    ABinspeccion.putExtra("Origen", Origen);
                    ABinspeccion.putExtra("Equipo", Equipo);
                    ABinspeccion.putExtra("Nombre", Nombre);
                    ABinspeccion.putExtra("IP", IP);
                    ABinspeccion.putExtra("NumI", NumI);
                    startActivity(ABinspeccion);
                }
            });

        }
        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

}
