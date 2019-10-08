package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.clases.Usuario;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button btn_selected, btn_subir;
    ImageView imageView;
    EditText document_text;
    Bitmap bitmap;

    private int camara = 1, galeria  = 2;
    public static final int PERMISSION_CODE = 111;

    Uri image_uri;

    String UPLOAD_URL = "http://192.168.0.4/android/upload.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_selected = findViewById(R.id.btn_selected);
        btn_subir = findViewById(R.id.btn_subir);
        imageView = findViewById(R.id.imagen);

        document_text = findViewById(R.id.document_text);

        if (!checkPermission()){
            requestPermission();
        }

        btn_selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (document_text.getText().toString().trim().isEmpty()){
                    document_text.setError("Ingresar el nombre del documento");
                }else {
                    OpenImages();
                }
            }
        });

        btn_subir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag_imagen = String.valueOf(imageView.getTag());

                if (document_text.getText().toString().trim().isEmpty()){
                    document_text.setError("Ingresar el nombre del documento");
                }else if(tag_imagen.equals("no_foto")){
                    Toast.makeText(MainActivity.this, "Selecciona una foto", Toast.LENGTH_LONG).show();
                }else{
                    uploadImage();
                }
            }
        });
    }

    //Solicitar permisos para guardar la imagen tomada de la camara, buscar en galeria
    public boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.CAMERA);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission(){
        ActivityCompat.requestPermissions(MainActivity.this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_CODE :
                if (grantResults.length > 0){
                    boolean storage  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameras = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (storage && cameras){
                        Toast.makeText(MainActivity.this, "Permisos aceptados", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MainActivity.this, "Permisos rechazados", Toast.LENGTH_SHORT).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                                showMsg("Debe permitir el acceso a los permisos.", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},PERMISSION_CODE);
                                        }
                                    }
                                });
                                return;
                            }
                        }
                    }
                }
        }

    }

    private void showMsg(String s, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(s)
                .setPositiveButton("OK", listener)
                .setNegativeButton("Cancelar", null)
                .create()
                .show();
    }
    //FINAL DE SOLICITUD DE PERMISOS

    //Ventana para mostrar las opciones si con camara o galeria
    private void OpenImages() {
        final CharSequence[] option = {"Camara","Galeria"};

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Selecciona una opcion");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (option[which].equals("Camara")){
                    CameraIntent();
                }
                if (option[which].equals("Galeria")){
                    GaleriaIntent();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void GaleriaIntent() {
        Intent g = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(g, galeria);
    }

    private void CameraIntent() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"New picture");
        values.put(MediaStore.Images.Media.DESCRIPTION,"from camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent, camara);
    }


    public String getStringImagen(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            if (requestCode == galeria) {
                Uri filePath = data.getData();
                try {
                    //Cómo obtener el mapa de bits de la Galería
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    //Configuración del mapa de bits en ImageView
                    imageView.setTag("si_foto");
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (requestCode == camara) {
                Uri filePath = image_uri;
                try {
                    //Cómo obtener el mapa de bits de la Galería
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    //Configuración del mapa de bits en ImageView
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //Enviar usando volley
    public void uploadImage() {
        final ProgressDialog loading = ProgressDialog.show(this, "Subiendo...", "Espere por favor");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();

                        document_text.setText("");
                        imageView.setImageBitmap(null);
                        imageView.setTag("no_foto");
                        imageView.setImageResource(R.drawable.no_imagen);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(MainActivity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //ASIGNANDO LOS PARAMETROS A VARIABLES Y CONVIRTIENDO A STRING

                Usuario usuario = new Usuario();                //CLASE QUE CONTIENE DATOS DEL USUARIO
                String valor_usuario = usuario.getUsuario();    //ASIGNAR DATO DE USUARIO CON SESION ABIERTA

                Intent intent = getIntent();                                              //OBTENER VARIABLES DE ACTIVITY ANTERIOR
                String valor_id = intent.getStringExtra("id_carpeta");              //OBTENER ID DE LA CARPETA CREADA EN ACTIVITY ANTERIOR
                String valor_nombre_persona = intent.getStringExtra("nombre");      //OBTENER EL NOMBRE DE LA PERSONA REGISTRADA EN ACTIVITY ANTERIOR

                String imagen = getStringImagen(bitmap);                                  //DATOS DE IMAGEN ELEGIDA
                String nombre = document_text.getText().toString().trim();                //NOMBRE ASIGNADO A LA IMAGEN

                //PARAMETROS A ENVIAR
                Map<String,String> map = new HashMap<String,String>();
                map.put("foto", imagen);                                //archivo imagen
                map.put("nombre", nombre);                              //nombre de la foto
                map.put("id_carpeta", valor_id);                        //nombre id de la carpeta
                map.put("nombre_persona", valor_nombre_persona);        //nompre de la persona registrada
                map.put("usuario", valor_usuario);                      //usuario que hace registro

                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
