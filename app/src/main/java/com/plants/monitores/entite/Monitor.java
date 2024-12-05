package com.plants.monitores.entite;

public class Monitor {
    private int id;
    private float temperature;
    private float humidity;
    private float solidhumidity;
    private String date;
    private boolean lightStatus;
    private boolean pampStatus;
    private boolean humidityStatus;
    private boolean ventStatus;
    public Monitor() {
    }
    public Monitor(int id, float temperature, float humidity, float solidhumidity, String date, boolean lightStatus, boolean pampStatus, boolean humidityStatus, boolean ventStatus) {
        this.id = id;
        this.temperature = temperature;
        this.humidity = humidity;
        this.solidhumidity = solidhumidity;
        this.date = date;
        this.lightStatus = lightStatus;
        this.pampStatus = pampStatus;
        this.humidityStatus = humidityStatus;
        this.ventStatus = ventStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public float getSolidhumidity() {
        return solidhumidity;
    }

    public void setSolidhumidity(float solidhumidity) {
        this.solidhumidity = solidhumidity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isLightStatus() {
        return lightStatus;
    }

    public void setLightStatus(boolean lightStatus) {
        this.lightStatus = lightStatus;
    }

    public boolean isPampStatus() {
        return pampStatus;
    }

    public void setPampStatus(boolean pampStatus) {
        this.pampStatus = pampStatus;
    }

    public boolean isHumidityStatus() {
        return humidityStatus;
    }

    public void setHumidityStatus(boolean humidityStatus) {
        this.humidityStatus = humidityStatus;
    }

    public boolean isVentStatus() {
        return ventStatus;
    }

    public void setVentStatus(boolean ventStatus) {
        this.ventStatus = ventStatus;
    }
}
