
package com.example.SmartCityMessina;
/**
 * EREDITARIETÀ: L'Auto "è un" Veicolo (Subtyping).
 */

public class Auto extends Veicolo {
    
    private String modello;
    // 0 = Elettrico, 1 = Termico
    private int tipoMotore; 

    public Auto(String targa, String destinazione, String modello, int tipoMotore) {
        super(targa, destinazione);
        this.modello = modello;
        this.tipoMotore = tipoMotore;
    }

    public int getTipoMotore() {
        return tipoMotore;
    }

    public String getModello() {
        return modello;
    }

    @Override
    public void muovi() {
        setVelocitaAttuale(getVelocitaAttuale() + 5);
        String tipo = (tipoMotore == 0) ? "Elettrica" : "Termica";
        System.out.println("[Auto " + tipo + " " + getId() + "] in movimento verso " + getDestinazione() + " a " + getVelocitaAttuale() + " km/h.");
    }

    @Override
    public void riceviMessaggio(Comunicazione mittente, String messaggio) {
        System.out.println("-> " + getId() + " riceve da " + mittente.getId() + ": " + messaggio);
    }
}