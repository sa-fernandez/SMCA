package Modelo;

import Enum.TipoMedida;

public class Medida {

    TipoMedida tipo;
    double valor;

    public Medida() {
    }

    public Medida(TipoMedida tipo, double valor) {
        this.tipo = tipo;
        this.valor = valor;
    }

    public TipoMedida getTipo() {
        return tipo;
    }

    public void setTipo(TipoMedida tipo) {
        this.tipo = tipo;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "[ " + tipo.toString() + " ] = " + valor;
    }
}
