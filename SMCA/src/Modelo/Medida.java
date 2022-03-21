package Modelo;

import Enum.TipoMedida;

public class Medida {

    TipoMedida tipo;
    int valor;

    public Medida() {
    }

    public Medida(TipoMedida tipo, int valor) {
        this.tipo = tipo;
        this.valor = valor;
    }

    public TipoMedida getTipo() {
        return tipo;
    }

    public void setTipo(TipoMedida tipo) {
        this.tipo = tipo;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "[ " + tipo.toString() + " ] = " + valor;
    }
}
