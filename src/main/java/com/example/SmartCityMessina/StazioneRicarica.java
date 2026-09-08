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
        
        // ==============================================================
        // = Intercetta il comando dinamico e isola la targa dell'auto  =
        // ==============================================================
        if (messaggio.startsWith("RICHIESTA_RICARICA_PER:")) {
            String targaAuto = messaggio.split(":")[1];
            System.out.println("   [Azione] Erogazione energia attivata. Ricarica del veicolo " + targaAuto + " in corso...");
        }
    }
}