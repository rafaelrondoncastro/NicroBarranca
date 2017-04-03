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

public class Equipos extends AppCompatActivity {

    Bundle extras;
    String Nombre,Origen,subStr,IP,cli;
    ListView listaJson;
    ObtnumOTM hiloNum;
    ObtnumI hiloNumI;
    int conf;
    ProgressDialog progressDialogEqui,progressDialogNumOtm,progressDialogNumInsp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipos);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        listaJson=(ListView)findViewById(R.id.Equipos);

        Intent intent=getIntent();
        extras = intent.getExtras();
        Nombre = extras.getString("Nombre");
        Origen = extras.getString("Origen");
        IP = extras.getString("IP");

        Cargarequipos cargaequipos = new Cargarequipos();
        cargaequipos.cargarcontenido(getApplicationContext());

        progressDialogEqui = ProgressDialog.show(this,
                "Espere un momneto", "Cargando equipos...");

        cargaequipos.execute(listaJson);
    }

    public void onBackPressed() {
        Intent ABactividades = new Intent(getApplicationContext(), Actividades.class);
        ABactividades.putExtra("Nombre",Nombre);
        ABactividades.putExtra("IP",IP);
        startActivity(ABactividades);
    }

    class Cargarequipos extends AsyncTask<ListView, Void, ArrayAdapter<String>> {
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

            try {
                URL url = new URL("http://"+IP+"/ACCESS/php/Obequiposex.php");
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
                        JSONArray arrayJson = respuestaJSON.getJSONArray("equipo");
                        for (int i = 0; i < arrayJson.length(); i++) {
                            JSONObject objetoJson = arrayJson.getJSONObject(i);
                            cli = objetoJson.getString("busqueda");
                            listaservicios.add(cli);
                        }
                    } else if (resultJSON.equals("2")) {
                        cli = "NO hay Equipos";
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
            progressDialogEqui.dismiss();

            if (conf!=200){
                final AlertDialog.Builder alertaconexion = new AlertDialog.Builder(Equipos.this);
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

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String name = (String) list.getItemAtPosition(position);
                        subStr = name.substring(0, 8);
                        if (Origen.equals("Ins")) {
                            hiloNumI = new ObtnumI();
                            String urlnum = "http://" + IP + "/ACCESS/php/ObnumI.php";

                            progressDialogNumInsp = ProgressDialog.show(Equipos.this,
                                    "Espere un momneto", "Obteniendo numero de Inspección...");

                            hiloNumI.execute(urlnum);

                        } else if (Origen.equals("Sol")) {
                            hiloNum = new ObtnumOTM();
                            String urlnum = "http://" + IP + "/ACCESS/php/ObnumOTM.php";

                            progressDialogNumOtm = ProgressDialog.show(Equipos.this,
                                    "Espere un momneto", "Obteniendo numero de OTM...");

                            hiloNum.execute(urlnum);

                        } else if (Origen.equals("Mant")){
                            Intent ABmantenimientos = new Intent(Equipos.this, Mantenimientos.class);
                            ABmantenimientos.putExtra("Origen", Origen);
                            ABmantenimientos.putExtra("Equipo", subStr);
                            ABmantenimientos.putExtra("Nombre", Nombre);
                            ABmantenimientos.putExtra("IP", IP);
                            startActivity(ABmantenimientos);
                        }
                    }
                });
            }else{
                Toast.makeText(getBaseContext(),"Compruebe que la base de datos se encuentra cerrada",Toast.LENGTH_LONG).show();
            }
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

            progressDialogNumOtm.dismiss();
            //Toast.makeText(getBaseContext(),"Bien hasta acá",Toast.LENGTH_LONG).show();

            int numotm=Integer.parseInt(s)+1;
            String numero=String.valueOf(numotm);
            //Toast.makeText(getBaseContext(), "inspección guardada exitosamente", Toast.LENGTH_SHORT).show();

            Intent ABOtm = new Intent(Equipos.this, Otm.class);
            ABOtm.putExtra("Origen",Origen);
            ABOtm.putExtra("Equipo", subStr);
            ABOtm.putExtra("Averias", "null");
            ABOtm.putExtra("Nombre", Nombre);
            ABOtm.putExtra("Efectfallo","");
            ABOtm.putExtra("NumOtm",numero);
            ABOtm.putExtra("IP",IP);
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

    public class ObtnumI extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            String NumeroI = "";

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
                    String resultJSON = respuestaJSON.getString("Numero");
                    NumeroI= resultJSON;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return NumeroI;
        }
        @Override
        protected void onPreExecute () {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute (String s){

            progressDialogNumInsp.dismiss();
            int numI=Integer.parseInt(s)+1;
            String NumI=String.valueOf(numI);

            //Toast.makeText(getBaseContext(), NumI, Toast.LENGTH_SHORT).show();

            Intent ABtareas = new Intent(Equipos.this, Tareas.class);
            ABtareas.putExtra("Origen", Origen);
            ABtareas.putExtra("Equipo", subStr);
            ABtareas.putExtra("Nombre", Nombre);
            ABtareas.putExtra("IP", IP);
            ABtareas.putExtra("NumI", NumI);
            startActivity(ABtareas);
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
