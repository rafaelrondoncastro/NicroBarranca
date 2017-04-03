package com.oneapp.Mantenimiento;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.text.DateFormat;

public class Ejecutaotm extends AppCompatActivity {

    Bundle extras;
    String IP,numotm,Nombre,Insumos,costomanobra,Nombre20,resultJSON,Codigo,sol;
    Button inInsumo,envotmejec;
    int conf;
    EditText Costoman,solu;
    ListView listaJson;
    Enviarotmejec hiloEnv;
    EnviaSI hiloSI;
    LinearLayout laycosto;
    TextView costo,txtsol;
    EnviarAverias hilo;
    ProgressDialog progressDialogEfect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ejecutaotm);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        inInsumo=(Button)findViewById(R.id.inInsumo);
        listaJson=(ListView)findViewById(R.id.cargalista);
        envotmejec=(Button)findViewById(R.id.enviarotmejecutada);
        Costoman=(EditText)findViewById(R.id.CostMano);
        laycosto=(LinearLayout)findViewById(R.id.laycosto);
        costo=(TextView)findViewById(R.id.txtcosto);
        solu=(EditText)findViewById(R.id.solucion);
        txtsol=(TextView)findViewById(R.id.txtsol);

        envotmejec.setVisibility(View.GONE);
        laycosto.setVisibility(View.GONE);
        costo.setVisibility(View.GONE);
        solu.setVisibility(View.GONE);
        txtsol.setVisibility(View.GONE);

        Intent intent=getIntent();
        extras = intent.getExtras();
        Nombre = extras.getString("Nombre");
        numotm = extras.getString("NumOtm");
        Insumos = extras.getString("Insumos");
        Codigo = extras.getString("Codigo");

        IP = extras.getString("IP");

        Nombre20=Nombre.replace(" ","%20");

        if (Insumos.equals("Lleno")){
            envotmejec.setVisibility(View.VISIBLE);
            inInsumo.setVisibility(View.GONE);
            laycosto.setVisibility(View.VISIBLE);
            costo.setVisibility(View.VISIBLE);
            solu.setVisibility(View.VISIBLE);
            txtsol.setVisibility(View.VISIBLE);
        }else{
            envotmejec.setVisibility(View.GONE);
            inInsumo.setVisibility(View.VISIBLE);
            laycosto.setVisibility(View.GONE);
            costo.setVisibility(View.GONE);
            solu.setVisibility(View.GONE);
            txtsol.setVisibility(View.GONE);
        }

        Tarea1 tarea1 = new Tarea1();
        tarea1.cargarcontenido(getApplicationContext());
        tarea1.execute(listaJson);

        inInsumo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ABinsumosotm = new Intent(Ejecutaotm.this, InsumosOTM.class);
                ABinsumosotm.putExtra("NumOtm", numotm);
                ABinsumosotm.putExtra("Nombre",Nombre);
                ABinsumosotm.putExtra("IP",IP);
                ABinsumosotm.putExtra("Codigo",Codigo);
                ABinsumosotm.putExtra("Origen","Corr");
                startActivity(ABinsumosotm);
            }
        });

        envotmejec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                costomanobra=Costoman.getText().toString();
                sol=solu.getText().toString();
                if(costomanobra.equals("")|| sol.equals("")){
                    Toast.makeText(getBaseContext(),"Ingrese el costo de mano y la sulucion al problema",Toast.LENGTH_SHORT).show();
                }else{
                    hiloEnv = new Enviarotmejec();
                    String urlnum = "http://"+IP+"/ACCESS/php/GuardarEjecOTM.php?NumeroOTM="+numotm+"&Nombre="+Nombre20+"&Costoman="+costomanobra;
                    hiloEnv.execute(urlnum);
                }
            }
        });
    }

    class Tarea1 extends AsyncTask<ListView, Void, ArrayAdapter<String>> {
        Context contexto;
        ListView list;
        ArrayList<String> listacontroles =new ArrayList<String>();

        public void cargarcontenido(Context contexto){
            this.contexto = contexto;
        }

        @Override
        protected ArrayAdapter<String> doInBackground(ListView... params) {
            list=params[0];
            String cli;
            URL url = null;

            try {
                url = new URL("http://"+IP+"/ACCESS/php/Obaveriasconnum.php?NumerOtm="+numotm);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //Abrir la conexión
                connection.setRequestProperty("User-Agent", "Mozilla/5.0" +
                        " (Linux; Android 1.5; es-ES) Ejemplo HTTP");
                //connection.setHeader("content-type", "application/json");
                int respuesta = connection.getResponseCode();
                conf=respuesta;
                StringBuilder result = new StringBuilder();
                if (respuesta == HttpURLConnection.HTTP_OK) {
                    InputStream in = new BufferedInputStream(connection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);        // Paso toda la entrada al StringBuilder
                    }

                    //Creamos unobjeto JSONObject para poder acceder a los atributos (campos) del objeto.
                    JSONObject respuestaJSON = new JSONObject(result.toString());   //Creo un JSONObject a partir del StringBuilder pasado a cadena
                    //Accedemos al vector de resultados
                    String resultJSON = respuestaJSON.getString("estado");   // results es el nombre del campo en el JSON

                    if (resultJSON.equals("1")) {
                        JSONArray arrayJson = respuestaJSON.getJSONArray("control");
                        for (int i = 0; i < arrayJson.length(); i++) {
                            JSONObject objetoJson = arrayJson.getJSONObject(i);
                            cli = objetoJson.getString("ControlesRealizados");
                            listacontroles.add(cli);
                        }
                    } else if (resultJSON.equals("2")) {
                        cli = "NO se realizaron controles";
                        listacontroles.add(cli);
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
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, android.R.layout.simple_list_item_1, listacontroles);

            return adapter;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayAdapter<String> result) {
            //Toast.makeText(getBaseContext(),numotm,Toast.LENGTH_SHORT).show();
            if (conf!=200){
                final AlertDialog.Builder alertaconexion = new AlertDialog.Builder(Ejecutaotm.this);
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

            list.setAdapter(result);
            result.notifyDataSetChanged();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    public class Enviarotmejec extends AsyncTask<String, Void, String> {

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

            //Toast.makeText(getBaseContext(), s, Toast.LENGTH_SHORT).show();
            String ejecutada = "Si";
            hiloSI = new EnviaSI();
            String urlnum = "http://"+IP+"/ACCESS/php/EditarSI.php?numOTM="+numotm+"&ejecutada="+ejecutada;
            hiloSI.execute(urlnum);
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

    public class EnviaSI extends AsyncTask<String, Void, String> {

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

            //Toast.makeText(getBaseContext(), s, Toast.LENGTH_SHORT).show();
            sol=sol.replace(" ","%20");
            hilo = new EnviarAverias();
            String cadena = "http://"+IP+"/ACCESS/php/GuardarAverias.php?NumOTM=" + numotm + "&EfectoPresentado=No%20Aplica&ModoEnQueSePresnto=No%20Aplica&Componente=No%20Aplica&Causa=Solucion&ControlesRealizados=" + sol ;

            progressDialogEfect = ProgressDialog.show(Ejecutaotm.this,
            "Espere un momneto", "Cargando efectos de falla...");

            hilo.execute(cadena);
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
            progressDialogEfect.dismiss();
            Intent ABActividades = new Intent(Ejecutaotm.this, Actividades.class);
            String nom = Nombre.replace("%20","");
            ABActividades.putExtra("Nombre",nom);
            ABActividades.putExtra("IP",IP);
            startActivity(ABActividades);
        }
    }
}
