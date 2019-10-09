package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.adapters.RvAdapter;
import com.example.myapplication.clases.DataModel;
import com.example.myapplication.clases.Usuario;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText usuario, password;
    Button btn_entrar;

    String URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usuario = findViewById(R.id.usuario);
        password = findViewById(R.id.password);

        URL = getString(R.string.url_conexion);

        btn_entrar = findViewById(R.id.btn_entrar);

        btn_entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usuario.getText().toString().trim().isEmpty()){
                    usuario.setError("Ingresa el usuario");
                }else if(password.getText().toString().trim().isEmpty()){
                    password.setError("Ingresa la contraseña");
                }else{
                    IniciarSesion();
                }
            }
        });
    }

    //Envios usando volley
    public void IniciarSesion() {
        final ProgressDialog loading = ProgressDialog.show(this, "Iniciar sesion", "Validando datos, espere...");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL+"iniciar_sesion.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        Usuario usuarioData = new Usuario();

                        try {
                            JSONObject object=new JSONObject(response);

                            if (object.getBoolean("success")){
                                usuarioData.setUsuario(object.getString("usuario").trim());
                                usuarioData.setNombre(object.getString("nombre").trim());

                                usuario.setText("");
                                password.setText("");

                                Intent intencion = new Intent(LoginActivity.this, RegistroActivity.class);
                                startActivity(intencion);
                            }else {
                                Toast.makeText(getApplicationContext(),"Datos incorrectos", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(LoginActivity.this, "Error al contactar con el servidor, contacte al administrador", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                String user = usuario.getText().toString().trim();
                String pass = password.getText().toString().trim();

                Map<String,String> map = new HashMap<String,String>();
                map.put("usuario", user);
                map.put("password", pass);

                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
