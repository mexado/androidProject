package com.example.myapplication.clases;

public class Product_List {
    private String prNombre;
    private String prDireccion;
    private String prIdcarpeta;

    public Product_List(String prNombre, String prDireccion, String prIdcarpeta) {
        this.prNombre = prNombre;
        this.prDireccion = prDireccion;
        this.prIdcarpeta = prIdcarpeta;
    }

    public String getPrNombre() {
        return prNombre;
    }

    public String getPrDireccion() {
        return prDireccion;
    }

    public String getPrIdcarpeta() {
        return prIdcarpeta;
    }
}