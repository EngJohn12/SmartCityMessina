package com.example.SmartCityMessina;

import java.util.Random;

public class SensoreAmbientale extends Dispositivo {

    private int livelloSmog;
    private ReteCittadina rete; 
    private boolean sogliaSuperataPrecedente = false; 
    
    
     

    public SensoreAmbientale(String id, String posizione,ReteCittadina rete) {
        super(id, posizione);
        this.rete = rete;
        this.livelloSmog = 0;
    }




    @Override
    public void eseguiAzione() {
        // =================================================================
        // = Simula la lettura dei dati nell'aria (es. sul Viale Boccetta) =
        // =================================================================
               
        Random rand = new Random();
        this.livelloSmog = rand.nextInt(100); 
        System.out.println("[Sensore " + getId() + " in " + getPosizione() + "] Smog rilevato: " + livelloSmog + "/100 ");
    }
        public void aggiornaLivelloSmog(int nuovoLivello) {
        this.livelloSmog = nuovoLivello;
        
        boolean sogliaSuperataOra = this.livelloSmog > 80;
        
        // =================================================================
        // = LOGICA DI INTERAZIONE: Se soglia superata, invia allarme      =
        // =================================================================
        if (sogliaSuperataOra && !sogliaSuperataPrecedente) {
            System.out.println("🚨 [SENSORE " + getId() + "] SOGLIA CRITICA SUPERATA! Invio allarme al Parcheggio...");
            try {
                // Cerca il parcheggio nella rete e gli invia il messaggio
                Dispositivo parcheggio = rete.cercaDispositivo("PARK-Pala ");
                this.inviaMessaggio(parcheggio, "ALLARME_INQUINAMENTO");
            } catch (DispositivoNonTrovatoException e) {
                System.out.println("Errore invio allarme: " + e.getMessage());
            }
     }
        
        // Aggiorna lo stato precedente per il prossimo tick
        sogliaSuperataPrecedente = sogliaSuperataOra;
    }

    @Override
    public void riceviMessaggio(Comunicazione mittente, String messaggio) {
        // =====================================================================
        // = Un sensore solitamente non riceve comandi, ma li registra e basta =
        // =====================================================================
        System.out.println("-> Sensore " + getId() + " registra nel log: " + messaggio);
    }

    // =====================================
    // = Metodo specifico di questa classe =
    // =====================================
    public boolean isAriaInquinata() {
        return livelloSmog > 80;
    }
}