package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.adapters.CustomListAdapter;
import com.example.myapplication.clases.Product_List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    SearchView search;
    ListView lv;

    String URL;

    ArrayList<Product_List>product;

    private JsonArrayRequest request;
    private RequestQueue requestQueue;
    private CustomListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        search = findViewById(R.id.buscar);
        lv = findViewById(R.id.listview);

        product=new ArrayList<>();

        URL = getString(R.string.url_conexion);

        //Hacer el envio de los datos a buscar, se hace condicional a partir de 2 letras solo hara el envio de datos
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (search.getQuery().length() >= 2){
                    lv.setVisibility(View.VISIBLE);
                    closeKeyboard();
                    Buscar();
                }else{
                    product.clear();
                    lv.setVisibility(View.GONE);
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                product.clear();
                lv.setVisibility(View.GONE);

                return true;
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent i = new Intent (SearchActivity.this, MainActivity.class);
                TextView paramId = view.findViewById(R.id.id_carpetaBD);
                TextView paramNombre = view.findViewById(R.id.nombreBD);

                String paramId_carpeta = paramId.getText().toString();
                String paramNombreP = paramNombre.getText().toString();

                i.putExtra("id_carpeta", paramId_carpeta);
                i.putExtra("nombre", paramNombreP);
                startActivity(i);
            }
        });

    }

    //Cerrar el teclado virtual al enviar datos con boton o teclado del celular
    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    //Se hace la consulta y envio de datos al Web service PHP para buscar al contribuyente
    private void Buscar() {
        final ProgressDialog loading = ProgressDialog.show(this, "Buscando persona", "Espere por favor...");

        //Reemplazar espacios en cadena enviada
        String cadenaUrl= search.getQuery().toString();
        String cadenaUrl2 = cadenaUrl.replace(" ","%20");

        request=new JsonArrayRequest(URL+ "search_user.php?nombre_persona="+cadenaUrl2, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject=null;
                product.clear();
                for (int i=0; i<response.length(); i++){
                    try {
                        jsonObject=response.getJSONObject(i);
                        Product_List productList=new Product_List(jsonObject.getString("nombre"), jsonObject.getString("direccion"), jsonObject.getString("id_carpeta"));
                        product.add(productList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                loading.dismiss();
                setupData(product);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                product.clear();
            }
        });
        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(request);

    }

    private void setupData(ArrayList<Product_List> product) {
        adapter=new CustomListAdapter(getApplicationContext(),R.layout.lista_contribuyentes_pagos,product);
        lv.setAdapter(adapter);
    }
}
