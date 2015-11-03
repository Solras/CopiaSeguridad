package com.example.pica.copiaseguridad;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class EditActivity extends AppCompatActivity {

    Contacto c;
    EditText et;
    View vertical;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_activity);
        iniciar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    public void iniciar(){
        et = (EditText) findViewById(R.id.et_nombre);
        Intent i= getIntent();
        Bundle b= i.getExtras();
        c = (Contacto) b.getSerializable("contacto");
        System.out.println(c);
        et.setText(c.getNombre());
        colocarNumeros(c);
    }

    private void colocarNumeros(Contacto c){
        vertical = findViewById(R.id.edit);
        int cont=0;
        for(String str : c.getNumeros()) {

            EditText et = new EditText(this);
            et.setId(cont);
            et.setText(str);
            et.setInputType(InputType.TYPE_CLASS_PHONE);
            et.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

            ((LinearLayout) vertical).addView(et);
            cont++;
        }
    }

    public void saveChanges(View v){
        int children=((LinearLayout) vertical).getChildCount();
        for(int i=2; i<children; i++){
            EditText et = (EditText) ((LinearLayout) vertical).getChildAt(i);
            c.getNumeros().set(i - 2, et.getText().toString());
        }
        c.setNombre(et.getText().toString());
        System.out.println(c);
        this.getIntent().putExtra("contacto", c);
        setResult(RESULT_OK, this.getIntent());
        Toast.makeText(this, R.string.label_guardado, Toast.LENGTH_SHORT).show();
        finish();
    }

    public void dontSaveChanges(View v){
        finish();
    }
}
