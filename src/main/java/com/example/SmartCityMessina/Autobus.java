package com.example.SmartCityMessina;

public class Autobus extends Veicolo {

// Private | Class | Constant
    private static final int ACCELERAZIONE = 3; // m/s²

// Private | Instance | Final
    private final int numeroLinea;


    public Autobus (String targa, String destinazione, int numeroLinea){
      super(targa, destinazione);
      this.numeroLinea = numeroLinea;
    }

    public int getNumeroLinea() {
        return numeroLinea;
    }

    @Override
    public void muovi() {

        setVelocitaAttuale(getVelocitaAttuale() + ACCELERAZIONE);
        
        // Implementation for muovi method
    }

    @Override
    public void riceviMessaggio(Comunicazione comunicazione, String messaggio) {
        // Implementation for riceviMessaggio method
    }
}



