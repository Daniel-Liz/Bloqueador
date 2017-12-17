package com.example.daniel.bloquetron;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
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
    private FileOutputStream fos = null;
    private int posicao;
    private String telAux;

    private static final int CONTACTS_INTENT = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inflater = LayoutInflater.from(this);
        numerosListView = (ListView) findViewById(R.id.listaDeNumeros);
        mList = new ArrayList<String>();
        mArrayAdapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1, mList);
        numerosListView.setAdapter(mArrayAdapter);
        read();
        editarNumero();



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.CALL_PHONE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.READ_CONTACTS}, REQUEST_PERMISSION);
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
        salvarContato("0"+Mask.unmask(numero.getText().toString()));
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
                telAux = mArrayAdapter.getItem(position);
                Log.e(TAG, telAux);

                AlertDialog.Builder dialog1 = new AlertDialog.Builder(MainActivity.this);
                View edicaoNumero = inflater.inflate(R.layout.edicao_numero, null);
                dialog1.setView(edicaoNumero);
                dialog = dialog1.create();
                dialog.show();
                numero = (EditText) dialog.findViewById(R.id.edicaoNumeroEditText);
                numero.addTextChangedListener(Mask.insert("(##)#####-####",numero));
                numero.setText(telAux.substring(1));
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
        String tel = "0"+Mask.unmask(numero.getText().toString());
        if(!telAux.equals(tel)) {
            if(!mList.contains(tel)) {
                mList.set(posicao, tel);
                mArrayAdapter.notifyDataSetChanged();
                try {
                    fos = openFileOutput(FILENAME, MODE_PRIVATE);
                    for (String n : mList) {
                        fos.write((n + ";").getBytes());
                        Log.e(TAG, n);
                    }
                    fos.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }else Toast.makeText(MainActivity.this,"Este número já se encontra na lista!",Toast.LENGTH_LONG).show();
        }
        dialog.dismiss();
    }

    public void adicionarContatoLista(View v){
        Intent intent1 = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent1, CONTACTS_INTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CONTACTS_INTENT && resultCode == RESULT_OK ){
            Uri uri = data.getData();

            Cursor cursor = getContentResolver().query(uri, null, null, null, null);

            int idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            cursor.moveToNext();
            String id = cursor.getString(idIndex);

            Cursor telefoneCursor = getContentResolver()
                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);

            int telIndex = telefoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            telefoneCursor.moveToNext();
            String telefone = telefoneCursor.getString(telIndex);
            if(telefone.contains("+55")) padronizaTelefone(telefone.substring(4));
            else padronizaTelefone(telefone);

            telefoneCursor.close();

            cursor.close();

        }

    }

    public void padronizaTelefone(String telefone){
        telefone = telefone.replaceAll(" ","").replaceAll("[-]","");
        if(telefone.length()<=9) telefone = "035"+telefone;
        else if(telefone.charAt(0)!='0') telefone = "0"+telefone;

        salvarContato(telefone);
    }

    public void salvarContato(String telefone){
        if(!mList.contains(telefone)) {
            mList.add(telefone);
            try {
                fos = openFileOutput(FILENAME, MODE_APPEND);
                fos.write((telefone + ";").getBytes());
                fos.close();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }

            mArrayAdapter.notifyDataSetChanged();
        }else Toast.makeText(MainActivity.this,"Este número já se encontra na lista!",Toast.LENGTH_LONG).show();
    }

}
