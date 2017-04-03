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
import android.widget.LinearLayout;
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

public class AveriasOTM extends AppCompatActivity {

    Bundle extras;
    TextView equipo;
    String resultJSON;
    String IP,FalloBus,cli,falloenv,numero,Equipo,Nombre,Averias="full",Fallo1,Fallo,modos,piezas,lispiezas,liscausas,liscontrol,
            modofalla,piezasfalla,causasfalla,controlfalla,efec,causasfallas,controlfallas,Llenado="";
    Spinner modo,pieza,causa,control,efecto;
    Spinner modoJson,piezaJson,causaJson,controlJson,efectoJson;
    Button regrasaaOTM,envaverias;
    //ProgressDialog progressDialogEfect;
    ArrayList<String> listamodo = new ArrayList<String>();
    ArrayList<String> listapieza = new ArrayList<String>();
    ArrayList<String> listacausa = new ArrayList<String>();
    ArrayList<String> listacontrol = new ArrayList<String>();
    ArrayList<String> listaefectos = new ArrayList<String>();
    EnviarAverias hilo;
    LinearLayout Layefecto;
    EditText txtefecto,txtmodo,txtpieza,txtcausa,txtcontrol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_averias_otm);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        equipo=(TextView)findViewById(R.id.prueba);
        regrasaaOTM=(Button)findViewById(R.id.regresaraOTM);
        envaverias=(Button) findViewById(R.id.envaverias);
        modoJson=(Spinner)findViewById(R.id.modo);
        piezaJson=(Spinner)findViewById(R.id.pieza);
        causaJson=(Spinner)findViewById(R.id.causa);
        controlJson=(Spinner)findViewById(R.id.control);
        efectoJson=(Spinner)findViewById(R.id.efecto);

        txtmodo=(EditText) findViewById(R.id.txtmodo);
        txtpieza=(EditText)findViewById(R.id.txtpieza);
        txtcausa=(EditText)findViewById(R.id.txtcausa);
        txtcontrol=(EditText)findViewById(R.id.txtcontrol);
        txtefecto=(EditText)findViewById(R.id.txtefecto);

        Layefecto=(LinearLayout)findViewById(R.id.Layefecto);

        txtefecto.setVisibility(View.GONE);
        txtmodo.setVisibility(View.GONE);
        txtpieza.setVisibility(View.GONE);
        txtcausa.setVisibility(View.GONE);
        txtcontrol.setVisibility(View.GONE);

        efectoJson.setVisibility(View.GONE);
        modoJson.setVisibility(View.GONE);
        piezaJson.setVisibility(View.GONE);
        causaJson.setVisibility(View.GONE);
        controlJson.setVisibility(View.GONE);

        Intent intent = getIntent();
        extras = intent.getExtras();
        Equipo = extras.getString("Equipo");
        Nombre = extras.getString("Nombre");
        Fallo1 = extras.getString("Fallo");
        numero = extras.getString("NumOTM");
        IP = extras.getString("IP");

        equipo.setText("Equipo analizado: "+Equipo);
        regrasaaOTM.setVisibility(View.INVISIBLE);

        if (Fallo1.equals("")){
            Layefecto.setVisibility(View.VISIBLE);

            Obtefecto obefec = new Obtefecto();
            obefec.cargarcontenido(getApplicationContext());
            obefec.execute(efectoJson);

        }else{
            Layefecto.setVisibility(View.GONE);
            falloenv=Fallo1.replaceAll(" ","%20");
            FalloBus=Fallo1;

            Obtmodo obmodo = new Obtmodo();
            obmodo.cargarcontenido(getApplicationContext());
            obmodo.execute(modoJson);
        }

        envaverias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getBaseContext(), falloenv, Toast.LENGTH_SHORT).show();
                if (Llenado.equals("Vacio")){
                    modofalla = txtmodo.getText().toString().replace(" ","%20");
                    piezasfalla = txtpieza.getText().toString().replace(" ","%20");
                    causasfallas = txtcausa.getText().toString().replace(" ","%20");
                    controlfallas = txtcontrol.getText().toString().replace(" ","%20");
                }
                if (falloenv==null){
                    falloenv = txtefecto.getText().toString().replace(" ","%20");
                }
                hilo = new EnviarAverias();
                String cadena = "http://"+IP+"/ACCESS/php/GuardarAverias.php?NumOTM=" + numero + "&EfectoPresentado=" + falloenv + "&ModoEnQueSePresnto=" + modofalla + "&Componente=" + piezasfalla + "&Causa=" + causasfallas + "&ControlesRealizados=" + controlfallas ;

                //progressDialogEfect = ProgressDialog.show(AveriasOTM.this,
                //"Espere un momneto", "Cargando efectos de falla...");

                hilo.execute(cadena);
            }
        });
    }

    class Obtefecto extends AsyncTask<Spinner, Void, ArrayAdapter<String>> {
        Context contexto;

        public void cargarcontenido(Context contexto) {
            this.contexto = contexto;
        }

        @Override
        protected ArrayAdapter<String> doInBackground(Spinner... params) {
            efecto = params[0];

            try {
                URL url = new URL("http://" + IP + "/ACCESS/php/Obefectofalla.php?Equipo=" + Equipo);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0" +
                        " (Linux; Android 1.5; es-ES) Ejemplo HTTP");

                int respuesta = connection.getResponseCode();
                StringBuilder result = new StringBuilder();

                if (respuesta == HttpURLConnection.HTTP_OK) {
                    InputStream in = new BufferedInputStream(connection.getInputStream());  // preparo la cadena de entrada
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));  // la introduzco en un BufferedReader
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);        // Paso toda la entrada al StringBuilder
                    }

                    JSONObject respuestaJSON = new JSONObject(result.toString());
                    resultJSON = respuestaJSON.getString("estado");   // results es el nombre del campo en el JSON

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
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, R.layout.spinner_item, listaefectos);

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

            //progressDialogEfect.dismiss();
            if (result.getCount() != 0) {

                txtefecto.setVisibility(View.GONE);
                txtmodo.setVisibility(View.GONE);
                txtpieza.setVisibility(View.GONE);
                txtcausa.setVisibility(View.GONE);
                txtcontrol.setVisibility(View.GONE);

                efectoJson.setVisibility(View.VISIBLE);
                modoJson.setVisibility(View.VISIBLE);
                piezaJson.setVisibility(View.VISIBLE);
                causaJson.setVisibility(View.VISIBLE);
                controlJson.setVisibility(View.VISIBLE);

                Llenado="Lleno";
                efecto.setAdapter(result);
                efecto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Fallo = efecto.getSelectedItem().toString();

                        falloenv = Fallo.replaceAll(" ", "%20");
                        FalloBus = falloenv;

                        Obtmodo obmodo = new Obtmodo();
                        obmodo.cargarcontenido(getApplicationContext());
                        obmodo.execute(modoJson);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

            } else {
                txtefecto.setVisibility(View.VISIBLE);
                txtmodo.setVisibility(View.VISIBLE);
                txtpieza.setVisibility(View.VISIBLE);
                txtcausa.setVisibility(View.VISIBLE);
                txtcontrol.setVisibility(View.VISIBLE);

                efectoJson.setVisibility(View.GONE);
                modoJson.setVisibility(View.GONE);
                piezaJson.setVisibility(View.GONE);
                causaJson.setVisibility(View.GONE);
                controlJson.setVisibility(View.GONE);

                Llenado="Vacio";
            }
        }
    }

    class Obtmodo extends AsyncTask<Spinner, Void, ArrayAdapter<String>> {
        Context contexto;
        private void cargarcontenido(Context contexto) {
            this.contexto = contexto;
        }
        @Override
        protected ArrayAdapter<String> doInBackground(Spinner... params) {
            modo = params[0];

            try {
                URL url = new URL("http://"+IP+"/ACCESS/php/Obmodofalla.php?Equipo="+Equipo+"&Efectos_de_Falla="+FalloBus);
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

                    listamodo.clear();
                    if (resultJSON.equals("1")) {
                        JSONArray arrayJson = respuestaJSON.getJSONArray("modo");
                        for (int i = 0; i < arrayJson.length(); i++) {
                            JSONObject objetoJson = arrayJson.getJSONObject(i);
                            efec = objetoJson.getString("Modos_de_Falla");
                            listamodo.add(efec);
                        }
                    } else if (resultJSON.equals("2")) {
                        efec = "NO hay modos de falla registrados";
                        listamodo.add(efec);

                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, android.R.layout.simple_list_item_1, listamodo);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, R.layout.spinner_item,listamodo);
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

            //Toast.makeText(getBaseContext(), FalloBus, Toast.LENGTH_LONG).show();

            if (result.getCount() != 0) {

                modoJson.setVisibility(View.VISIBLE);
                piezaJson.setVisibility(View.VISIBLE);
                causaJson.setVisibility(View.VISIBLE);
                controlJson.setVisibility(View.VISIBLE);

                modo.setAdapter(result);
                modo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        modos = modo.getSelectedItem().toString();
                        modofalla=modos.replaceAll(" ","%20");
                        //Inconveniente=Inconv;
                        Obtpieza obpieza = new Obtpieza();
                        obpieza.cargarcontenido(getApplicationContext());
                        obpieza.execute(piezaJson);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }else{

                txtmodo.setVisibility(View.VISIBLE);
                txtpieza.setVisibility(View.VISIBLE);
                txtcausa.setVisibility(View.VISIBLE);
                txtcontrol.setVisibility(View.VISIBLE);

                efectoJson.setVisibility(View.GONE);
                modoJson.setVisibility(View.GONE);
                piezaJson.setVisibility(View.GONE);
                causaJson.setVisibility(View.GONE);
                controlJson.setVisibility(View.GONE);

                Llenado="Vacio";
            }
        }
    }

    class Obtpieza extends AsyncTask<Spinner, Void, ArrayAdapter<String>> {
        Context contexto;
        private void cargarcontenido(Context contexto) {
            this.contexto = contexto;
        }
        @Override
        protected ArrayAdapter<String> doInBackground(Spinner... params) {
            pieza = params[0];

            try {
                URL url = new URL("http://"+IP+"/ACCESS/php/Obpiezafalla.php?Equipo="+Equipo+"&Efectos_de_Falla="+FalloBus+"&Modos_de_Falla="+modofalla);
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

                    listapieza.clear();
                    if (resultJSON.equals("1")) {
                        JSONArray arrayJson = respuestaJSON.getJSONArray("pieza");
                        for (int i = 0; i < arrayJson.length(); i++) {
                            JSONObject objetoJson = arrayJson.getJSONObject(i);
                            lispiezas = objetoJson.getString("Componentes");
                            listapieza.add(lispiezas);
                        }
                    } else if (resultJSON.equals("2")) {
                        lispiezas = "NO hay modos de falla registrados";
                        listapieza.add(lispiezas);

                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, android.R.layout.simple_list_item_1, listapieza);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, R.layout.spinner_item,listapieza);
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
            //Toast.makeText(getBaseContext(), modos , Toast.LENGTH_SHORT).show();

            pieza.setAdapter(result);
            pieza.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    piezas = pieza.getSelectedItem().toString();
                    piezasfalla=piezas.replaceAll(" ","%20");
                    //Inconveniente=Inconv;
                    Obtcausa obcausa = new Obtcausa();
                    obcausa.cargarcontenido(getApplicationContext());
                    obcausa.execute(causaJson);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    class Obtcausa extends AsyncTask<Spinner, Void, ArrayAdapter<String>> {
        Context contexto;
        private void cargarcontenido(Context contexto) {
            this.contexto = contexto;
        }
        @Override
        protected ArrayAdapter<String> doInBackground(Spinner... params) {
            causa = params[0];

            try {
                URL url = new URL("http://"+IP+"/ACCESS/php/Obcausa.php?Equipo="+Equipo+"&Efectos_de_Falla="+FalloBus+"&Modos_de_Falla="+modofalla+"&Componentes="+piezasfalla);
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

                    listacausa.clear();
                    if (resultJSON.equals("1")) {
                        JSONArray arrayJson = respuestaJSON.getJSONArray("causa");
                        for (int i = 0; i < arrayJson.length(); i++) {
                            JSONObject objetoJson = arrayJson.getJSONObject(i);
                            liscausas = objetoJson.getString("Causas_de_Falla");
                            listacausa.add(liscausas);
                        }
                    } else if (resultJSON.equals("2")) {
                        liscausas = "NO hay modos de falla registrados";
                        listacausa.add(liscausas);

                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, android.R.layout.simple_list_item_1, listacausa);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, R.layout.spinner_item,listacausa);
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
            //Toast.makeText(getBaseContext(), resultJSON , Toast.LENGTH_SHORT).show();

            causa.setAdapter(result);
            causa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    causasfalla = causa.getSelectedItem().toString();
                    causasfallas=causasfalla.replaceAll(" ","%20");
                    //Inconveniente=Inconv;
                    Obtcontrol obcontrol = new Obtcontrol();
                    obcontrol.cargarcontenido(getApplicationContext());
                    obcontrol.execute(controlJson);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    class Obtcontrol extends AsyncTask<Spinner, Void, ArrayAdapter<String>> {
        Context contexto;
        private void cargarcontenido(Context contexto) {
            this.contexto = contexto;
        }
        @Override
        protected ArrayAdapter<String> doInBackground(Spinner... params) {
            control = params[0];

            try {
                URL url = new URL("http://"+IP+"/ACCESS/php/Obcontrol.php?Equipo="+Equipo+"&Efectos_de_Falla="+FalloBus+"&Modos_de_Falla="+modofalla+"&Componentes="+piezasfalla+"&Causas="+causasfallas);
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

                    listacontrol.clear();
                    if (resultJSON.equals("1")) {
                        JSONArray arrayJson = respuestaJSON.getJSONArray("control");
                        for (int i = 0; i < arrayJson.length(); i++) {
                            JSONObject objetoJson = arrayJson.getJSONObject(i);
                            liscontrol = objetoJson.getString("Controles_a_Efectuar");
                            listacontrol.add(liscontrol);
                        }
                    } else if (resultJSON.equals("2")) {
                        liscontrol = "NO hay modos de falla registrados";
                        listacontrol.add(liscontrol);

                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, android.R.layout.simple_list_item_1, listacontrol);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, R.layout.spinner_item,listacontrol);
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

            control.setAdapter(result);
            control.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    controlfalla = control.getSelectedItem().toString();
                    controlfallas=controlfalla.replaceAll(" ","%20");
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
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
            Toast.makeText(getBaseContext(), "Registro Exitoso", Toast.LENGTH_SHORT).show();
            regrasaaOTM.setVisibility(View.VISIBLE);
            regrasaaOTM.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent ABotm = new Intent(getApplicationContext(), Otm.class);
                    ABotm.putExtra("Averias",Averias);
                    ABotm.putExtra("Equipo",Equipo);
                    ABotm.putExtra("Nombre",Nombre);
                    ABotm.putExtra("NumOtm",numero);
                    ABotm.putExtra("IP",IP);
                    startActivity(ABotm);
                }
            });
        }
    }

}
