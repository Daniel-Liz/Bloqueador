package com.example.daniel.bloquetron;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_PERMISSION = 10;
    private LayoutInflater inflater;
    private EditText numero;
    private ArrayAdapter<String> mArrayAdapter;
    private List<String> mList;
    private ListView numerosListView;
    private AlertDialog dialog;
    private BloqueadorDeChamadas bloqueadorDeChamadas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inflater = LayoutInflater.from(this);
        numerosListView = (ListView) findViewById(R.id.listaDeNumeros);
        mList = new ArrayList<String>();
        mArrayAdapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1, mList);
        numerosListView.setAdapter(mArrayAdapter);

        bloqueadorDeChamadas = new BloqueadorDeChamadas();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.CALL_PHONE}, REQUEST_PERMISSION);
        }
    }

    public void abrirDialogo(View v){

        AlertDialog.Builder dialog1 = new AlertDialog.Builder(MainActivity.this);
        View novoNumero = inflater.inflate(R.layout.novo_numero, null);
        dialog1.setView(novoNumero);
        dialog = dialog1.create();
        dialog.show();
        numero = (EditText) dialog.findViewById(R.id.editTextNumero);

        numero.addTextChangedListener(Mask.insert("(##)#####-####",numero));


    }

    public void adicionarNumero(View v){
        mList.add("0"+Mask.unmask(numero.getText().toString()));
        mArrayAdapter.notifyDataSetChanged();
        bloqueadorDeChamadas.setMList(mList);
        dialog.dismiss();
    }

}
