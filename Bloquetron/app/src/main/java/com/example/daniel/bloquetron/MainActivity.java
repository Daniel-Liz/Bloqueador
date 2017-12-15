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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private String FILENAME = "contatos_file";
    private static final int REQUEST_PERMISSION = 10;
    private LayoutInflater inflater;
    private EditText numero;
    private ArrayAdapter<String> mArrayAdapter;
    private List<String> mList;
    private ListView numerosListView;
    private AlertDialog dialog;
    private BloqueadorDeChamadas bloqueadorDeChamadas;
    private BloqueadorDeSms bloqueadorDeSms;
    private FileOutputStream fos = null;
    private int posicao;


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
        bloqueadorDeSms = new BloqueadorDeSms();
        read();
        editarNumero();



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.CALL_PHONE}, REQUEST_PERMISSION);
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
        try {
            fos = openFileOutput(FILENAME, MODE_APPEND);
            fos.write(("0"+Mask.unmask(numero.getText().toString())+";").getBytes());
            Log.e(TAG, "0"+Mask.unmask(numero.getText().toString()));
            fos.close();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        mArrayAdapter.notifyDataSetChanged();
        bloqueadorDeChamadas.setMList(mList);
        bloqueadorDeSms.setMList(mList);
        dialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            fos = openFileOutput(FILENAME, MODE_PRIVATE);
            for (String n : mList) {
                fos.write((n+";").getBytes());
                Log.e(TAG, n);
            }
            fos.close();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void read(){
        try{
            FileInputStream fin = openFileInput(FILENAME);
            int c;
            String temp="";
            while( (c = fin.read()) != -1){
                if(c==';') {
                    mList.add(temp);
                    mArrayAdapter.notifyDataSetChanged();
                    bloqueadorDeChamadas.setMList(mList);
                    bloqueadorDeSms.setMList(mList);
                    Log.e(TAG, temp);
                    temp = "";
                }else {
                    temp = temp + Character.toString((char) c);
                }

            }
            fin.close();

        }catch(Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    private void editarNumero(){
        numerosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                posicao = position;
                final String num = mArrayAdapter.getItem(position);
                Log.e(TAG, num);

                AlertDialog.Builder dialog1 = new AlertDialog.Builder(MainActivity.this);
                View edicaoNumero = inflater.inflate(R.layout.edicao_numero, null);
                dialog1.setView(edicaoNumero);
                dialog = dialog1.create();
                dialog.show();
                numero = (EditText) dialog.findViewById(R.id.edicaoNumeroEditText);
                numero.addTextChangedListener(Mask.insert("(##)#####-####",numero));
                numero.setText(num.substring(1));
            }
        });
    }

    public void excluirItem(View v){
        mList.remove(posicao);
        mArrayAdapter.notifyDataSetChanged();
        dialog.dismiss();
        try {
            fos = openFileOutput(FILENAME, MODE_PRIVATE);
            for (String n : mList) {
                fos.write((n+";").getBytes());
                Log.e(TAG, n);
            }
            fos.close();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void confirmarItem(View v){
        mList.set(posicao, "0"+Mask.unmask(numero.getText().toString()));
        mArrayAdapter.notifyDataSetChanged();
        dialog.dismiss();
        try {
            fos = openFileOutput(FILENAME, MODE_PRIVATE);
            for (String n : mList) {
                fos.write((n+";").getBytes());
                Log.e(TAG, n);
            }
            fos.close();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
