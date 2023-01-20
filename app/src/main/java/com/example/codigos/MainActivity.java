package com.example.codigos;



import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private TextView etCodeClaros,etDescripcion, etPrecio, etIp, etTalla;
    private EditText etCodigo;
    Button idScan;
    private RequestQueue rq;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etCodigo = findViewById(R.id.etCodigo);
        etTalla = findViewById(R.id.etTalla);
        etCodeClaros = findViewById(R.id.etCodeClaros);
        etDescripcion = findViewById(R.id.etDescripcion);
        etPrecio = findViewById(R.id.etPrecio);
        etIp = findViewById(R.id.etIp);
        idScan = findViewById(R.id.idScan);
        rq = Volley.newRequestQueue(this);

        idScan.setOnClickListener(view -> {

            IntentIntegrator integrador = new IntentIntegrator(MainActivity.this);
            integrador.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
            integrador.setPrompt("Lector de barras RodrigoApp");
            integrador.setCameraId(0);
            integrador.setOrientationLocked(false);
            integrador.setBeepEnabled(false);
            integrador.setCaptureActivity(CaptureActivityPortraint.class);
            integrador.setBarcodeImageEnabled(true);
            integrador.initiateScan();
        });
        etCodigo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (etCodigo.getText().toString().isEmpty()){

                }else{
                    buscar();
                }
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "lectura cancelada", Toast.LENGTH_LONG).show();
            } else {
                etCodigo.setText(result.getContents());
                buscar();
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void buscar() {
        String url = "http://" + etIp.getText().toString() + "/android/buscar_producto.php?codigo=" + etCodigo.getText().toString();
        JsonArrayRequest requerimiento = new JsonArrayRequest(Request.Method.GET,
                url,
                null,
                response -> {
                    if (response.length() == 1) {
                        try {
                            JSONObject objeto = new JSONObject(response.get(0).toString());
                            etCodeClaros.setText(objeto.getString("code"));
                            etDescripcion.setText(objeto.getString("descripcion"));
                            etPrecio.setText(objeto.getString("precio"));
                            etTalla.setText(objeto.getString("talla"));
                            etCodigo.setText("");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "no existe codigo articulo", Toast.LENGTH_LONG).show();
                        etCodeClaros.setText("");
                        etDescripcion.setText("");
                        etPrecio.setText("");
                        etTalla.setText("");
                    }
                },
                error -> Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show()
        );
        rq.add(requerimiento);
    }

    public void limpiar(View v){
        etCodigo.setText("");
        etCodeClaros.setText("");
        etDescripcion.setText("");
        etPrecio.setText("");
        etTalla.setText("");
    }
}