package com.example.SmartCityMessina;

public class ParcheggioPalacultura extends Dispositivo {

    private final int postiTotali = 30; 
    private int postiOccupati;
    private GestoreLog logger = new GestoreLog(); 
    private boolean scontiAttivi = false;
    
    public ParcheggioPalacultura(String id, String posizione) {
        super(id, posizione);
        this.postiOccupati = 0; 
    }

    public boolean isCompleto() {
        return postiOccupati >= postiTotali;
    }
    // ===========================================
    // = NUOVO: Logica di controllo all'ingresso =
    // ===========================================
    
    public void tentaParcheggio(Auto auto) {
        System.out.println(">>> " + auto.getId() + " (" + auto.getModello() + ") richiede l'accesso agli stalli di ricarica...");
        
        if (auto.getTipoMotore() == 1) { // 1 = Termico
            String messaggioLog = "🚨 [ALLARME] ACCESSO NEGATO! Veicolo termico (" + auto.getId() + ") rilevato agli stalli EV del Palacultura. Segnalazione per RIMOZIONE CARRO ATTREZZI!";
            System.out.println("   " + messaggioLog);
            
            // =========================================================
            // = SALVATAGGIO DELL'INFRAZIONE SUL FILE DI LOG (.txt)    =
            // =========================================================
            try (java.io.FileWriter fw = new java.io.FileWriter("log_citta.txt", true);
                 java.io.PrintWriter out = new java.io.PrintWriter(fw)) {
                String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                out.println("[" + timestamp + "] " + messaggioLog);
            } catch (java.io.IOException ex) {
                System.out.println("Errore scrittura log_citta.txt: " + ex.getMessage());
            }
            
        } else if (auto.getTipoMotore() == 0) { // 0 = Elettrico
            if (!isCompleto()) {
                postiOccupati++;
                System.out.println("   ⚡ [ACCESSO CONSENTITO] Veicolo elettrico parcheggiato e in ricarica. Posti: " + getPostiLiberi() + "/" + postiTotali);
            } else {
                System.out.println("   ❌ [PARCHEGGIO PIENO] Attendi che si liberi una colonnina.");
            }
        }
    }

    public void esceAuto() {
        if (postiOccupati > 0) {
            postiOccupati--;
            System.out.println("[Parcheggio " + getId() + "] Un'auto elettrica ha terminato la ricarica ed è uscita. Posti liberi: " + getPostiLiberi());
        }
    }

    public int getPostiLiberi() {
        return postiTotali - postiOccupati;
    }

    @Override
    public void eseguiAzione() {
        System.out.println("[Stato Parcheggio] " + getId() + " - Posti ricarica liberi: " + getPostiLiberi());
    }


        
       @Override
    public void riceviMessaggio(Comunicazione mittente, String messaggio) {
        // NUOVO: Gestione dell'allarme smog
        if (messaggio.equals("ALLARME_INQUINAMENTO")) {
            String msgAlert = "Allarme Smog ricevuto dalla rete! Attivazione sconti per ricarica EV per disincentivare il traffico termico.";
            String outputCompleto = "[Parcheggio " + getId() + "] " + msgAlert;
            
            // 1. Stampa a terminale (con icona per risaltarlo)
            System.out.println("\n🚨 " + outputCompleto + "\n");
            
            // 2. Scrive su file log_citta.txt tramite il GestoreLog
            logger.scriviLog(outputCompleto);
            
            this.scontiAttivi = true;
        }
    


        
        // 3. Gestione di messaggi sconosciuti PER DEBUG
        else {
            // ========================================================================================
            // =               Per evitare il "flooding" la println viene usata come debug            =
            // ======================================================================================== 
            
            // System.out.println("[Parcheggio " + getId() + "] Messaggio ignorato: " + messaggio); 
            
        }
    }
}