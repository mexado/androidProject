package com.example.myapplication.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;
import com.example.myapplication.clases.Product_List;

import java.util.ArrayList;

public class CustomListAdapter extends ArrayAdapter<Product_List> {
    ArrayList<Product_List> productList;
    Context context;
    int resource;
    public CustomListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Product_List> productList) {
        super(context, resource, productList);
        this.productList=productList;
        this.context=context;
        this.resource=resource;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView==null){
            LayoutInflater layoutInflater=(LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView=layoutInflater.inflate(R.layout.lista_contribuyentes_pagos,null,true);
        }
        Product_List pr=getItem(position);
        TextView prnombre = convertView.findViewById(R.id.nombreBD);
        TextView prdireccion = convertView.findViewById(R.id.direccionBD);
        TextView prcarpeta = convertView.findViewById(R.id.id_carpetaBD);

        prnombre.setText(pr.getPrNombre());
        prdireccion.setText(pr.getPrDireccion());
        prcarpeta.setText(pr.getPrIdcarpeta());

        return convertView;
    }
}