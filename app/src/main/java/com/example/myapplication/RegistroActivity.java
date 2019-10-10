package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.clases.Usuario;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {

    TextView nombre;
    EditText nombre_reg, direccion;
    Button btn_registrar;

    String UPLOAD_URL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        UPLOAD_URL = getString(R.string.url_conexion);

        nombre = findViewById(R.id.name);
        nombre_reg = findViewById(R.id.nombre_reg);
        direccion = findViewById(R.id.direccion);

        btn_registrar = findViewById(R.id.btn_registrar);

        String nombreUsuario = Usuario.getNombre();//obtener valor del nombre del usuario desde la clase USUARIO()
        nombre.setText(nombreUsuario);

        btn_registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nombre_reg.getText().toString().trim().isEmpty()){
                    nombre_reg.setError("Ingresar nombre");
                }else if(direccion.getText().toString().trim().isEmpty()){
                    direccion.setError("Ingresar direccion");
                }else{
                    AlertRegistrar();
                }
            }
        });
    }

    //Mostrar alerta de salida al presionar boton fisico Atras
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            salirRegistro();
        }
        // para las demas cosas, se reenvia el evento al listener habitual
        return super.onKeyDown(keyCode, event);
    }

    //Ventana para mostrar las opciones de salir de activity
    private void salirRegistro() {
        final CharSequence[] option = {"Cerrar sesion","Cancelar"};

        AlertDialog.Builder builder = new AlertDialog.Builder(RegistroActivity.this);
        builder.setTitle("Desea cerrar la sesion?");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (option[which].equals("Cerrar sesion")){
                    finish();
                }
                if (option[which].equals("Cancelar")){
                    dialog.dismiss();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //Ventana para mostrar las opciones de registro
    private void AlertRegistrar() {
        final CharSequence[] option = {"Aceptar","Cancelar"};

        AlertDialog.Builder builder = new AlertDialog.Builder(RegistroActivity.this);
        builder.setTitle("Los datos estan correctos?");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (option[which].equals("Aceptar")){
                    RegistrarPersona();
                }
                if (option[which].equals("Cancelar")){
                    dialog.dismiss();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //Envios usando volley
    public void RegistrarPersona() {
        final ProgressDialog loading = ProgressDialog.show(this, "Registrando persona", "Enviando datos, espere...");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL+"registrar_personas.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();

                        try {
                            JSONObject object=new JSONObject(response);
                            if (object.getBoolean("success")){

                                nombre_reg.setText("");
                                direccion.setText("");

                                Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
                                intent.putExtra("nombre", object.getString("nombre").trim());
                                intent.putExtra("id_carpeta", object.getString("id_carpeta").trim());
                                startActivity(intent);
                            }else {
                                Toast.makeText(getApplicationContext(),"Los datos no se pudieron registrar, intente nuevamente", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(RegistroActivity.this, "Error al contactar con el servidor, contacte al administrador", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                //Variables que se enviaran al servidor
                String nombre_registrar = nombre_reg.getText().toString().trim();
                String direccion_registrar = direccion.getText().toString().trim();

                Map<String,String> map = new HashMap<>();
                map.put("nombre_reg", nombre_registrar);
                map.put("direccion", direccion_registrar);

                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
