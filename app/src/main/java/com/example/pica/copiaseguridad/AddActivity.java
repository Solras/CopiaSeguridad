package com.example.pica.copiaseguridad;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AddActivity extends AppCompatActivity {

    Contacto c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_activity);
        c = (Contacto) this.getIntent().getExtras().getSerializable("contacto");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void saveNew(View v){
        ArrayList<String> numeros=new ArrayList();
        View vertical = findViewById(R.id.add);
        TextView etNombre = (TextView) findViewById(R.id.etNombre);
        int children = ((LinearLayout) vertical).getChildCount();
        for(int i=2; i<children-1; i++){
            EditText et = (EditText) ((LinearLayout) vertical).getChildAt(i);
            numeros.add(et.getText().toString());
        }
        c.setNombre(etNombre.getText().toString());
        c.setNumeros(numeros);
        this.getIntent().putExtra("contacto", c);
        setResult(RESULT_OK, this.getIntent());
        Toast.makeText(this, R.string.label_guardado, Toast.LENGTH_SHORT).show();
        finish();
    }

    public void addEditText(View v){
        LinearLayout vertical = (LinearLayout) findViewById(R.id.add);
        EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_PHONE);
        et.setHint(R.string.numero);
        et.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        vertical.addView(et, vertical.getChildCount() - 1);
    }
    public void dontSaveChanges(View v){
        finish();
    }
}








