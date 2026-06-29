
package com.example.SmartCityMessina;



/**
 * EREDITARIETÀ: Il Semaforo "è un" Dispositivo.
 */
public class Semaforo extends Dispositivo {

    private String colore;

    public Semaforo(String id, String posizione) {
        super(id, posizione);
        this.colore = "VERDE"; // Di default parte col verde
    }

    @Override
    public void eseguiAzione() {
        System.out.println("[Semaforo " + getId() + " in " + getPosizione() + "] Luce attuale: " + colore);
    }

    // POLIMORFISMO: Il semaforo reagisce ai messaggi in modo diverso dall'auto
    @Override
    public void riceviMessaggio(Comunicazione mittente, String messaggio) {
        System.out.println("-> Semaforo " + getId() + " riceve da " + mittente.getId() + ": " + messaggio);
        
        if (messaggio.equals("ALLARME_INQUINAMENTO")) {
            System.out.println("   [Azione] Il semaforo diventa ROSSO per bloccare le auto e abbassare lo smog!");
            this.colore = "ROSSO";
        }
    }
    
    public String getColore() {
        return colore;
    }
    
    public void setColore(String colore) {
        this.colore = colore;
    }
}