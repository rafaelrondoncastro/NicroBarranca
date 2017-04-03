package com.oneapp.Mantenimiento;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class ingreso extends AppCompatActivity {

    String usuarios,passwords,regreso,IP,Nombre,resultJSON;
    int valor;
    EditText txtpas,txtUsu,txtip;
    Button Ingreso;
    Obtenerminombre hiloconexionminombre;
    SQLiteDatabase db;
    Bundle extras;
    ProgressDialog progressDialogNom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingreso);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Ingreso = (Button) findViewById(R.id.ingresar);
        txtpas = (EditText) findViewById(R.id.Pass);
        txtUsu = (EditText) findViewById(R.id.Usu);
        txtip = (EditText)findViewById(R.id.IP);

        Intent intent = getIntent();
        extras = intent.getExtras();

        resultJSON="";
        regreso="false";
        valor=0;

        if (extras!=null){
            regreso = extras.getString("regreso");
            IP = extras.getString("IP");
            txtip.setText(IP);
        }

        BDusuario usuario =
                new BDusuario(this, "BDusuario", null, 1);

        db = usuario.getWritableDatabase();

        String[] id={"1"};
        final Cursor c = db.rawQuery("SELECT * FROM usu WHERE id=?",id);

        if (regreso.equals("true")){
            valor=1;
        }else{
            if (c.moveToFirst()){
                do{
                    usuarios = c.getString(1);
                    passwords = c.getString(2);
                    Nombre = c.getString(3);
                }while (c.moveToNext());

                txtpas.setText(passwords);
                txtUsu.setText(usuarios);
                Intent ABopc = new Intent(ingreso.this, Actividades.class);
                ABopc.putExtra("Nombre", Nombre);
                ABopc.putExtra("IP", "null");
                startActivity(ABopc);

            }else{
                Toast.makeText(getApplicationContext(), "Ingrese un usuario y un password", Toast.LENGTH_LONG).show();
            }
        }

        Ingreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (c.moveToFirst()){
                    valor=1;
                }
                String txtUsuario=txtUsu.getText().toString();
                String txtPassword=txtpas.getText().toString();
                if (txtUsu.getText().toString().equals("") || txtpas.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Ingrese un usuario y un password", Toast.LENGTH_LONG).show();
                } else {
                    IP=txtip.getText().toString();

                    hiloconexionminombre = new Obtenerminombre();
                    String urlminombre = "http://"+IP+"/access/php/Valida.php?usu="+txtUsuario+"&pas="+txtPassword;

                    progressDialogNom = ProgressDialog.show(ingreso.this,
                            "Espere un momento", "Obteniendo validación...");

                    hiloconexionminombre.execute(urlminombre);
                }
                }
        });
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder alertasalida = new AlertDialog.Builder(ingreso.this);
        alertasalida.setTitle("Confirmar");
        alertasalida.setMessage("¿Desea salir de la aplicación?");
        alertasalida.setIcon(R.drawable.logo);
        alertasalida.setCancelable(false);

        alertasalida.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface alertasalida, int which) {
                alertasalida.cancel();
            }
        });
        alertasalida.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface alertasalida, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        alertasalida.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface alertasalida, int which) {

            }
        });
        alertasalida.create();
        alertasalida.show();
    }

    public class Obtenerminombre extends AsyncTask<String, Void, String> {

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
                    resultJSON = respuestaJSON.getString("Nombres");
                }

            } catch (MalformedURLException e) {
                final AlertDialog.Builder alertaconexion = new AlertDialog.Builder(ingreso.this);
                alertaconexion.setTitle("Importante");
                alertaconexion.setMessage("No se encuentra conectado a la red de la base de datos, ¿que desea hacer?" );
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
            return resultJSON;
        }

        @Override
        protected void onPreExecute () {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute (String s){
            progressDialogNom.dismiss();
            if (s.equals("")) {
                    Toast.makeText(getApplicationContext(), "Usuario o contraseña erradas", Toast.LENGTH_LONG).show();
                } else {
                    int id=1;
                    if (valor==1){
                        db.execSQL("UPDATE usu SET usuario='"+ txtUsu.getText().toString()+"',password='"+txtpas.getText().toString()+"',nombre='"+s+"' WHERE id=1");
                        //Toast.makeText(getBaseContext(), "Base de Datos Actualizada", Toast.LENGTH_SHORT).show();
                    }else{
                        db.execSQL("INSERT INTO usu (id,usuario,password,nombre)" + "VALUES(" + id + ", '" + txtUsu.getText().toString()+"', '"+txtpas.getText().toString()+"', '"+s+"')");
                        //Toast.makeText(getBaseContext(), "Usuario se encuentra en la base de datos", Toast.LENGTH_SHORT).show();
                    }

                    Intent ABopc = new Intent(ingreso.this, Actividades.class);
                    ABopc.putExtra("Nombre", s);
                    ABopc.putExtra("IP", IP);
                    startActivity(ABopc);
                }

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
