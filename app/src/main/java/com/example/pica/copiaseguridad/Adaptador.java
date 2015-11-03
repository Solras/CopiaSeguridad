package com.example.pica.copiaseguridad;

import android.content.Context;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Adaptador extends ArrayAdapter<Contacto> {

    private Context ctx;
    private int res;
    private LayoutInflater lInflator;
    private ArrayList<Contacto> values;

    static class ViewHolder {
        TextView tv1, tv2;
        ImageView iv1, iv2;
    }

    public Adaptador(Context context, int resource, ArrayList<Contacto> objects) {
        super(context, resource, objects);
        this.ctx = context;
        this.res = resource;
        this.lInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.values = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = new ViewHolder();

        if (convertView == null) {
            convertView = lInflator.inflate(res, null);
            TextView tv = (TextView) convertView.findViewById(R.id.tv_l);
            vh.tv1 = tv;
            tv = (TextView) convertView.findViewById(R.id.tv_m);
            vh.tv2 = tv;
            ImageView iv = (ImageView) convertView.findViewById(R.id.iv_foto);
            vh.iv1 = iv;
            iv = (ImageView) convertView.findViewById(R.id.iv_mas);
            vh.iv2 = iv;
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        if (values.get(position).getNumeros().size() <= 1) {
            vh.iv2.setImageResource(R.drawable.no);
        } else {
            vh.iv2.setImageResource(R.drawable.si);
        }
        vh.iv2.setTag(values.get(position).getId());
        vh.tv1.setText(values.get(position).getNombre());
        if (!values.get(position).isEmpty())
            vh.tv2.setText(values.get(position).getNumeros().get(0));

        return convertView;
    }

    public void desc() {
        Collections.reverse(values);
        this.notifyDataSetChanged();
    }

    public void asc() {
        Collections.sort(values);
        this.notifyDataSetChanged();
    }

    public Contacto removeContact(long id) {
        for (Contacto c : values) {
            if (c.getId() == id) {
                values.remove(c);
                this.notifyDataSetChanged();
                return c;
            }
        }
        return null;
    }

}
