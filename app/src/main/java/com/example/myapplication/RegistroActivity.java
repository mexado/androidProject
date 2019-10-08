package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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

    String UPLOAD_URL = "http://192.168.0.4/android/registrar_personas.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        nombre = findViewById(R.id.nombre);
        nombre_reg = findViewById(R.id.nombre_reg);
        direccion = findViewById(R.id.direccion);

        btn_registrar = findViewById(R.id.btn_registrar);

        String nombreUsuario = Usuario.getNombre();//obtener valor del nombre del usuario desde la clase USUARIO()
        nombre.setText(nombreUsuario);

        btn_registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nombre_reg.getText().toString().trim().isEmpty()){
                    nombre_reg.setError("Ingresar nombre de la persona");
                }else if(direccion.getText().toString().trim().isEmpty()){
                    direccion.setError("Ingresa la direccion");
                }else{
                    RegistrarPersona();
                }
            }
        });
    }

    //Envios usando volley
    public void RegistrarPersona() {
        final ProgressDialog loading = ProgressDialog.show(this, "Registrando persona", "Enviando los datos, espere...");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();

                        try {
                            JSONObject object=new JSONObject(response);

                            if (object.getBoolean("success")){

                                Toast.makeText(getApplicationContext(),"Datos insertados correctamente", Toast.LENGTH_LONG).show();

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

                Map<String,String> map = new HashMap<String,String>();
                map.put("nombre_reg", nombre_registrar);
                map.put("direccion", direccion_registrar);

                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}