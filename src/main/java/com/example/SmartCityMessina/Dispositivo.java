
package com.example.SmartCityMessina;

/** ====================================================================== 
 *  = CLASSE ASTRATTA: Rappresenta un'infrastruttura fissa della città.  =
 *  =      Implementa l'interfaccia Comunicazione.                       =
    ======================================================================   */
public abstract class Dispositivo implements Comunicazione {
    // ======================================================================
    // = INCAPSULAMENTO: Variabili private, protette dall'accesso esterno.  =
    // ======================================================================
    private String id;
    private String posizione; // Es. "Viale Boccetta"
    private boolean attivo;

    // COSTRUTTORE
    public Dispositivo(String id, String posizione) {
        this.id = id;
        this.posizione = posizione;
        this.attivo = true; // Di default il dispositivo è acceso
    }

    // GETTER e SETTER per leggere/modificare lo stato in modo sicuro
    @Override
    public String getId() {
        return id;
    }

    public String getPosizione() {
        return posizione;
    }

    public boolean isAttivo() {
        return attivo;
    }

    public void setAttivo(boolean attivo) {
        this.attivo = attivo;
    }
    // ========================================================================================
    // = ASTRAZIONE: Un metodo che ogni dispositivo specifico dovrà implementare a modo suo.  =
    // = Un semaforo farà una cosa, una stazione di ricarica ne farà un'altra.                =
    // ========================================================================================
    public abstract void eseguiAzione();
    
    // ========================================================================================
    // =       Implementazione di base dell'invio messaggi (Ereditata da Comunicazione)       =
    // ========================================================================================
    @Override
    public void inviaMessaggio(Comunicazione destinatario, String messaggio) {
        if (this.attivo) {
            System.out.println("[LOG] " + this.id + " invia a " + destinatario.getId() + ": " + messaggio);
            destinatario.riceviMessaggio(this, messaggio);
        }
    }
}