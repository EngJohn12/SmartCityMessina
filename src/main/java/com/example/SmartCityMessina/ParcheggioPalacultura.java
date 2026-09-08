package com.example.SmartCityMessina;

public class ParcheggioPalacultura extends Dispositivo {

    private final static int postiTotali = 30; 
    private int postiOccupati;
    private GestoreLog logger = new GestoreLog(); 
    private boolean scontiAttivi = false;
    
    // =======================================================
    // = Iniezione della dipendenza per comunicare sulla rete =
    // =======================================================
    private ReteCittadina rete;
    
    /*public ParcheggioPalacultura(String id, String posizione, GestoreLog loggerEsterno) {
        super(id, posizione);
        this.postiOccupati = 0;
        this.logger = loggerEsterno;
    }
*/
    public ParcheggioPalacultura(String id, String posizione, ReteCittadina rete) {
        super(id, posizione);
        this.postiOccupati = 0; 
        this.rete = rete;
    }

    public boolean isCompleto() {
        return postiOccupati >= postiTotali;
    }
    
    public void tentaParcheggio(Auto auto) {
        System.out.println(">>> " + auto.getId() + " (" + auto.getModello() + ") richiede l'accesso agli stalli di ricarica...");
        
        if (auto.getTipoMotore() == 1) { 
            String messaggioLog = "🚨 [ALLARME] ACCESSO NEGATO! Veicolo termico (" + auto.getId() + ") rilevato agli stalli EV del Palacultura. Segnalazione per RIMOZIONE CARRO ATTREZZI!";
            System.out.println("   " + messaggioLog);
            
            try (java.io.FileWriter fw = new java.io.FileWriter("log_citta.txt", true);
                 java.io.PrintWriter out = new java.io.PrintWriter(fw)) {
                String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                out.println("[" + timestamp + "] " + messaggioLog);
            } catch (java.io.IOException ex) {
                System.out.println("Errore scrittura log_citta.txt: " + ex.getMessage());
            }
            
        } else if (auto.getTipoMotore() == 0) { 
            if (!isCompleto()) {
                postiOccupati++;
                System.out.println("   ⚡ [ACCESSO CONSENTITO] Veicolo elettrico " + auto.getId() + " parcheggiato. Posti: " + getPostiLiberi() + "/" + postiTotali);
                
                // =========================================================================
                // = INTEGRAZIONE STAZIONE RICARICA: Il Parcheggio delega la ricarica      =
                // =========================================================================
                try {
                    Dispositivo colonnina = rete.cercaDispositivo("STAZ-Ric-01"); 
                    this.inviaMessaggio(colonnina, "RICHIESTA_RICARICA_PER:" + auto.getId());
                } catch (DispositivoNonTrovatoException e) {
                    System.out.println("   [ERRORE DI RETE] Impossibile contattare la stazione di ricarica.");
                }
                
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
        if (messaggio.equals("ALLARME_INQUINAMENTO")) {
            String msgAlert = "Allarme Smog ricevuto dalla rete! Attivazione sconti per ricarica EV per disincentivare il traffico termico.";
            String outputCompleto = "[Parcheggio " + getId() + "] " + msgAlert;
            
            System.out.println("\n🚨 " + outputCompleto + "\n");
            logger.scriviLog(outputCompleto);
            this.scontiAttivi = true;
        }
    }
}