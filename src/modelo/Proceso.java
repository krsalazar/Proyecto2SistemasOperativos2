package modelo;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Proceso {
    private final SimpleStringProperty nombre;
    private final SimpleIntegerProperty tamano;
    private final SimpleIntegerProperty llegada;
    private final SimpleIntegerProperty duracion;

    public Proceso(String nombre, int tamano, int llegada, int duracion) {
        this.nombre = new SimpleStringProperty(nombre);
        this.tamano = new SimpleIntegerProperty(tamano);
        this.llegada = new SimpleIntegerProperty(llegada);
        this.duracion = new SimpleIntegerProperty(duracion);
    }

    public String getNombre() {
        return nombre.get();
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    public SimpleStringProperty nombreProperty() {
        return nombre;
    }

    public int getTamano() {
        return tamano.get();
    }

    public void setTamano(int tamano) {
        this.tamano.set(tamano);
    }

    public SimpleIntegerProperty tamanoProperty() {
        return tamano;
    }

    public int getLlegada() {
        return llegada.get();
    }

    public void setLlegada(int llegada) {
        this.llegada.set(llegada);
    }

    public SimpleIntegerProperty llegadaProperty() {
        return llegada;
    }

    public int getDuracion() {
        return duracion.get();
    }

    public void setDuracion(int duracion) {
        this.duracion.set(duracion);
    }

    public SimpleIntegerProperty duracionProperty() {
        return duracion;
    }
}
