package com.example.pica.copiaseguridad;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Xml;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class MainActivity extends AppCompatActivity {

    private Adaptador ad;
    private ArrayList<Contacto> contactos;
    private ListView lv;
    boolean sem = true;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            iniciar();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.desc: {
                ad.desc();
                Toast.makeText(this, R.string.label_desc, Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.asc: {
                ad.asc();
                Toast.makeText(this, R.string.label_asc, Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.add: {
                Intent i = new Intent(this, AddActivity.class);
                Bundle b = new Bundle();
                Contacto c = new Contacto();
                b.putSerializable("contacto", c);
                i.putExtras(b);
                startActivityForResult(i, 2);
                return true;
            }
            case R.id.sinc: {

                sp = getSharedPreferences("sincronizacion",this.MODE_PRIVATE);
                String sinc=sp.getString("tipo","");
                if(sinc.compareTo("manual")==0) {
                    try {
                        escribir();
                        Toast.makeText(this, R.string.label_sinc_manual, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(sinc.compareTo("automatica")==0) {
                    Toast.makeText(this, R.string.label_sinc_auto, Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            case R.id.act: {
                try {
                    leer();
                    System.out.println("contactos = " + contactos);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
                return true;
            }
            case R.id.tiposinc: {
                sp = getSharedPreferences("sincronizacion",this.MODE_PRIVATE);
                final SharedPreferences.Editor ed = sp.edit();
                final AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Sincronizacion");
                alert.setMessage("¿Que tipo de sincronizacion deseas tener?");

                DialogInterface.OnClickListener listenerauto = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        ed.putString("tipo", "automatica");
                        ed.commit();
                    }
                };
                DialogInterface.OnClickListener listenermanual = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        ed.putString("tipo", "manual");
                        ed.commit();
                    }
                };
                alert.setPositiveButton("Automatica", listenerauto);
                alert.setNegativeButton("Manual", listenermanual);
                alert.show();
                return true;
            }
            case R.id.infoSinc: {
                sp = getSharedPreferences("sincronizacion",this.MODE_PRIVATE);
                final SharedPreferences.Editor ed = sp.edit();
                final AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Datos de sincronizacion");
                alert.setMessage("Fecha de ultima sincronizacion: " + sp.getString("ultima", ""));

                alert.show();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Contacto contacto = (Contacto) data.getExtras().getSerializable("contacto");
                for (Contacto c : contactos) {
                    if (c.getId() == contacto.getId()) {
                        c.setNumeros(contacto.getNumeros());
                        c.setNombre(contacto.getNombre());
                    }
                }
            }
        }
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                Contacto contacto = (Contacto) data.getExtras().getSerializable("contacto");
                contactos.add(contacto);
            }
        }
        ad.notifyDataSetChanged();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int pos = menuInfo.position;
        View v = menuInfo.targetView;
        switch (item.getItemId()) {
            case R.id.menu_editar: {
                Intent i = new Intent(this, EditActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("contacto", contactos.get(pos));
                i.putExtras(b);
                startActivityForResult(i, 1);
                return true;
            }
            case R.id.menu_eliminar: {
                contactos.remove(ad.removeContact(contactos.get(pos).getId()));
                return true;
            }
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_contextual, menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sp = getSharedPreferences("sincronizacion",this.MODE_PRIVATE);
        final SharedPreferences.Editor ed = sp.edit();
        String sinc=sp.getString("tipo","");
        if(sinc.compareTo("automatica")==0) {
            try {
                escribir();
                ed.putString("ultima", new GregorianCalendar().getTime().toString());
                ed.commit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void iniciar() throws IOException, XmlPullParserException {
        sp = getSharedPreferences("sincronizacion",this.MODE_PRIVATE);
        final SharedPreferences.Editor ed = sp.edit();
        String sinc=sp.getString("tipo","");
        if(sinc.compareTo("")==0) {
            final AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Sincronizacion");
            alert.setMessage("¿Quieres sincronizar automaticamente?");

            DialogInterface.OnClickListener listenersi = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    ed.putString("tipo", "automatica");
                    ed.putString("ultima", "Nunca");
                    ed.commit();
                }
            };
            DialogInterface.OnClickListener listenerno = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    ed.putString("tipo", "manual");
                    ed.putString("ultima", "Nunca");
                    ed.commit();
                }
            };
            alert.setPositiveButton("si", listenersi);
            alert.setNegativeButton("no", listenerno);
            alert.show();
        }

        lv = (ListView) findViewById(R.id.lv_contactos);
        contactos = (ArrayList<Contacto>) getListaContactos(this);
        System.out.println(contactos);
        Collections.sort(contactos);
        for (Contacto c : contactos) {
            c.setNumeros(cleanRep(getListaTelefonos(this, c.getId())));
        }
        ad = new Adaptador(this, R.layout.elemento_lista, contactos);
        lv.setAdapter(ad);
        this.registerForContextMenu(lv);

        if(sinc.compareTo("automatica")==0 && sp.getString("ultima","").compareTo("Nunca")!=0){
            leer();
        }
    }

    public void mostrarNumeros(View v) {
        if (!search(v).isEmpty()) {
            TextView tv = (TextView) ((LinearLayout) ((LinearLayout) v.getParent()).getChildAt(1)).getChildAt(1);
            tv.setText("");
            if (sem) {
                for (String tlf : search(v).getNumeros()) {
                    tv.append(tlf + "\n");
                }
            } else {
                tv.append(search(v).getNumeros().get(0));
            }
            sem = !sem;
        }
    }

    private Contacto search(View v) {
        for (Contacto c : contactos) {
            if (c.getId() == (Long) v.getTag())
                return c;
        }
        return null;
    }

    private ArrayList<String> cleanRep(List<String> list) {
        ArrayList<String> clearedList = new ArrayList();
        for (String str : list) {
            if (!clearedList.contains(str))
                clearedList.add(str);
        }
        return clearedList;
    }

    public static List<Contacto> getListaContactos(Context contexto) {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String proyeccion[] = null;
        String seleccion = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = ? and " +
                ContactsContract.Contacts.HAS_PHONE_NUMBER + "= ?";
        String argumentos[] = new String[]{"1", "1"};
        String orden = ContactsContract.Contacts.DISPLAY_NAME + " collate localized asc";
        Cursor cursor = contexto.getContentResolver().query(uri, proyeccion, seleccion, argumentos, orden);
        int indiceId = cursor.getColumnIndex(ContactsContract.Contacts._ID);
        int indiceNombre = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        List<Contacto> lista = new ArrayList<>();
        Contacto contacto;
        while (cursor.moveToNext()) {
            contacto = new Contacto();
            contacto.setId(cursor.getLong(indiceId));
            contacto.setNombre(cursor.getString(indiceNombre));
            lista.add(contacto);
        }
        return lista;
    }

    public static List<String> getListaTelefonos(Context contexto, long id) {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String proyeccion[] = null;
        String seleccion = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
        String argumentos[] = new String[]{id + ""};
        String orden = ContactsContract.CommonDataKinds.Phone.NUMBER;
        Cursor cursor = contexto.getContentResolver().query(uri, proyeccion, seleccion, argumentos, orden);
        int indiceNumero = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        List<String> lista = new ArrayList<>();
        String numero;
        while (cursor.moveToNext()) {
            numero = cursor.getString(indiceNumero);
            lista.add(numero);
        }
        return lista;
    }

    public void escribir() throws IOException {
        FileOutputStream fos = new FileOutputStream(new File(getExternalFilesDir(null),"contactos.xml"));

        XmlSerializer doc = Xml.newSerializer();

        doc.setOutput(fos, "UTF-8");
        doc.startDocument(null, Boolean.valueOf(true));
        doc.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

        doc.startTag(null, "contactos");
        for (Contacto c : contactos) {
            doc.startTag(null, "contacto");
            doc.attribute(null, "id", Long.toString(c.getId()));
            doc.attribute(null, "nombre", c.getNombre());
            for (String num : c.getNumeros()) {
                doc.startTag(null, "numero");
                doc.text(num);
                doc.endTag(null, "numero");
            }
            doc.endTag(null, "contacto");
        }
        doc.endDocument();

        doc.flush();
        fos.close();
    }

    public void leer() throws IOException, XmlPullParserException {
        ArrayList<Contacto> contactosN = new ArrayList();
        XmlPullParser lector = Xml.newPullParser();
        lector.setInput(new FileInputStream(new File(getExternalFilesDir(null), "contactos.xml")), "utf-8");
        int evento = lector.getEventType();
        Contacto c=new Contacto();
        while (evento != XmlPullParser.END_DOCUMENT){
            if(evento == XmlPullParser.START_TAG){
                String etiqueta = lector.getName();
                if(etiqueta.compareTo("contacto")==0){
                    c=new Contacto();
                    c.setId(Long.parseLong(lector.getAttributeValue(null, "id")));
                    c.setNombre(lector.getAttributeValue(null, "nombre"));
                    System.out.println("ETIQUETA CONTACTO = " + c);
                }
                if(etiqueta.compareTo("numero")==0){
                    c.añadirNumero(lector.nextText());
                    System.out.println("ETIQUETA NUMERO = " + c);
                }
            }
            if(evento == XmlPullParser.END_TAG) {
                String etiqueta = lector.getName();
                if(etiqueta.compareTo("contacto")==0){
                    contactosN.add(c);
                }
            }
            evento = lector.next();
        }
        ad = new Adaptador(this, R.layout.elemento_lista, contactosN);
        lv.setAdapter(ad);
        System.out.println("contactosN = " + contactosN);
        contactos=contactosN;
    }
}
