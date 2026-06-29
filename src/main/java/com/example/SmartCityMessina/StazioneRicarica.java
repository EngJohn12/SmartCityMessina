package com.example.SmartCityMessina;

public class StazioneRicarica extends Dispositivo {

    public StazioneRicarica(String id, String posizione) {
        super(id, posizione);
    }

    @Override
    public void eseguiAzione() {
        System.out.println("[Stazione " + getId() + " in " + getPosizione() + "] Pronta all'uso. Erogazione energia attiva.");
    }

    @Override
    public void riceviMessaggio(Comunicazione mittente, String messaggio) {
        System.out.println("-> Stazione Ricarica riceve da " + mittente.getId() + ": " + messaggio);
        if (messaggio.equals("RICHIESTA_RICARICA")) {
            System.out.println("   [Azione] Ricarica del veicolo " + mittente.getId() + " iniziata...");
        }
    }
}