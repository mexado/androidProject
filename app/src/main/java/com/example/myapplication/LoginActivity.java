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

    private String URLstring = "http://192.168.0.4/android/documentos_lista.php?id_carpeta=1AdolfoMejiaRodriguez";
    ArrayList<DataModel> dataModelArrayList;
    private RvAdapter rvAdapter;
    private RecyclerView recyclerView;

    String UPLOAD_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        recyclerView = findViewById(R.id.recycler);

        fetchingJSON();

        usuario = findViewById(R.id.usuario);
        password = findViewById(R.id.password);

        UPLOAD_URL = getString(R.string.url_conexion);

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

    private void fetchingJSON() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLstring,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);

                                dataModelArrayList = new ArrayList<>();
                                JSONArray dataArray  = obj.getJSONArray("data");

                                for (int i = 0; i < dataArray.length(); i++) {

                                    DataModel playerModel = new DataModel();
                                    JSONObject dataobj = dataArray.getJSONObject(i);

                                    playerModel.setName_documento(dataobj.getString("nombre_documento"));

                                    dataModelArrayList.add(playerModel);
                                }

                                setupRecycler();



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurrs
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        requestQueue.add(stringRequest);


    }

    private void setupRecycler(){

        rvAdapter = new RvAdapter(this,dataModelArrayList);
        recyclerView.setAdapter(rvAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

    }


    //Envios usando volley
    public void IniciarSesion() {
        final ProgressDialog loading = ProgressDialog.show(this, "Iniciar sesion", "Validando datos, espere...");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL+"iniciar_sesion.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        Usuario usuario = new Usuario();

                        try {
                            JSONObject object=new JSONObject(response);

                            if (object.getBoolean("success")){
                                usuario.setUsuario(object.getString("usuario").trim());
                                usuario.setNombre(object.getString("nombre").trim());

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
