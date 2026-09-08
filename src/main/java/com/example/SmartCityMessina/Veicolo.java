
package com.example.SmartCityMessina;

// =====================================================================================
// =       CLASSE ASTRATTA: Rappresenta un mezzo in movimento.                        =
// = Anch'esso implementa Comunicazione per parlare con i Dispositivi (es. Semafori). =
// ====================================================================================
public abstract class Veicolo implements Comunicazione {
    // ===================
    // =  INCAPSULAMENTO = 
    // ===================
    private String targa;
    private static int velocitaAttuale;
    private String destinazione;
    // ===================
    // =   COSTRUTTORE   =
    // ===================
    public Veicolo(String targa, String destinazione) {
        this.targa = targa;
        this.destinazione = destinazione;
        this.velocitaAttuale = 0;
    }

    @Override
    public String getId() {
        return "Veicolo-" + targa;
    }

    public int getVelocitaAttuale() {
        return velocitaAttuale;
    }

    protected void setVelocitaAttuale(int velocitaAttuale) {
        this.velocitaAttuale = velocitaAttuale;
    }

    public String getDestinazione() {
        return destinazione;
    }
    // =================================================================================
    // = ASTRAZIONE: Ogni veicolo si muoverà in modo diverso (es. il tram sui binari)  =
    // =================================================================================
    public abstract void muovi();
    
    // ============================================
    // Implementazione base per inviare messaggi  =
    // ============================================
    @Override
    public void inviaMessaggio(Comunicazione destinatario, String messaggio) {
        System.out.println("[LOG] " + this.getId() + " invia a " + destinatario.getId() + ": " + messaggio);
        destinatario.riceviMessaggio(this, messaggio);
    }
}

