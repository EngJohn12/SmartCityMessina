package com.example.SmartCityMessina;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class App {

    public static void main(String[] args) {
        System.out.println("=== AVVIO SISTEMA CENTRALE SMART CITY MESSINA ===\n");

        ReteCittadina rete = new ReteCittadina(); // Il nostro "Registro" decentralizzato
        GestoreLog logger = new GestoreLog();

        logger.scriviLog("\n\n--- NUOVA SESSIONE SIMULATORE AVVIATA ---");
        // =================================
        // =      CREAZIONE SEMAFORI       =
        // =================================
        
        Semaforo semaforoBoccetta = new Semaforo("SEM-Boccetta", "Incrocio Boccetta-Garibaldi");
        Semaforo semaforoGaribaldiNordToSud = new Semaforo("SEM-Garibaldi-N-S", "Via Garibaldi Nord");
        Semaforo semaforoGaribaldiSudToNordGiraADestraBoccetta = new Semaforo("SEM-Garibaldi-S-N-Dx", "Via Garibaldi Sud (Corsia Dx)");
        Semaforo semaforoGaribaldiSudToNordDirittoBoccetta = new Semaforo("SEM-Garibaldi-S-N-Dir", "Via Garibaldi Sud (Corsia Centrale)");
        Semaforo semaforoBoccettaEstToOvestGaribaldiDirittoDestra = new Semaforo("SEM-Boccetta-E-O-Dx", "Viale Boccetta (Corsia Dx)");
        Semaforo semaforoBoccettaEstToOvestGaribaldiDirittoSinistra = new Semaforo("SEM-Boccetta-E-O-Sin", "Viale Boccetta (Corsia Sinistra)");
        Semaforo semaforoEstToOvestVittorioEmanueleDirittoSinistra = new Semaforo("SEM-VEmanuele-E-O-Sin", "Corso V. Emanuele (Sinistra)");
        Semaforo semaforoEstToOvestVittorioEmanueleDirittoDestra = new Semaforo("SEM-VEmanuele-E-O-Dx", "Corso V. Emanuele (Destra)");
        Semaforo semaforoVittorioEmanueleSudToNord = new Semaforo("SEM-VEmanuele-S-N", "Corso V. Emanuele Sud");
        
        // ============================================================================
        // = CREAZIONE ALTRI DISPOSITIVI (Sensore, Parcheggio, Stazione di Ricarica)  =
        // ============================================================================
        SensoreAmbientale sensoreSmog = new SensoreAmbientale("SENS-01 ", "Viale Boccetta ", rete);
        ParcheggioPalacultura parkPala = new ParcheggioPalacultura("PARK-Pala ", "Piano -2 ");
        StazioneRicarica colonnina = new StazioneRicarica("STAZ-Ric-01", "Piazza Unione Europea");
        
        
        
        
        // ===================
        // = CREAZIONE AUTO  =
        // ===================
        Auto panda = new Auto("ME98765", "Imbarcaderi", "Fiat Panda", 1); // TERMICA
        Auto tesla = new Auto("ME12345", "Duomo", "Tesla ", 0);    // ELETTRICA
        Ambulanza croceRossa = new Ambulanza("CRI-118", "Ospedale Papardo", true); 

        System.out.println("\n--- TEST PARCHEGGIO PALACULTURA ---");
        
        // =========================
        // =  PARCHEGGIARE TESLA   =
        // =========================
        parkPala.tentaParcheggio(tesla);
        
        // ================================
        // =  PARCHEGGIARE AUTO/ALLARME   =
        // ================================
        parkPala.tentaParcheggio(panda);
        System.out.println("--- REGISTRAZIONE NELLA RETE ---");
        
        // ============================================
        // =  REGIASTRAZIONE DISPOSITIVI NELLA RETE   =
        // ============================================
        
        rete.registraDispositivo(semaforoBoccetta);
        rete.registraDispositivo(semaforoGaribaldiNordToSud);
        rete.registraDispositivo(semaforoGaribaldiSudToNordGiraADestraBoccetta);
        rete.registraDispositivo(semaforoGaribaldiSudToNordDirittoBoccetta);
        rete.registraDispositivo(semaforoBoccettaEstToOvestGaribaldiDirittoDestra);
        rete.registraDispositivo(semaforoBoccettaEstToOvestGaribaldiDirittoSinistra);
        rete.registraDispositivo(semaforoEstToOvestVittorioEmanueleDirittoSinistra);
        rete.registraDispositivo(semaforoEstToOvestVittorioEmanueleDirittoDestra);
        rete.registraDispositivo(semaforoVittorioEmanueleSudToNord);
        
        rete.registraDispositivo(sensoreSmog);
        rete.registraDispositivo(parkPala);
        rete.registraDispositivo(colonnina);
        
        rete.registraVeicolo(panda);
        rete.registraVeicolo(croceRossa);
        
        logger.scriviLog("Rete avviata con successo. Tutti i nodi sono connessi.");

        System.out.println("\n--- SINCRONIZZAZIONE DI RETE ---");
        rete.aggiornaTuttiIDispositivi();

        System.out.println("\n--- TEST RICERCA CON LOG ---");
        try {
            Dispositivo trovato = rete.cercaDispositivo("SEM-Boccetta");
            System.out.println("-> SUCCESSO: Trovato! Si trova in: " + trovato.getPosizione());
            logger.scriviLog("Ricerca riuscita: Trovato " + trovato.getId());
        } catch (DispositivoNonTrovatoException e) {
            System.out.println(e.getMessage());
        }

        try {
            Dispositivo fantasma = rete.cercaDispositivo("SEM-Fasullo");
            System.out.println("-> SUCCESSO: Trovato in: " + fantasma.getPosizione());
        } catch (DispositivoNonTrovatoException e) {
            System.out.println("-> ECCEZIONE CATTURATA: " + e.getMessage());
            logger.scriviLog("ERRORE RILEVATO: " + e.getMessage());
        }

        logger.scriviLog("--- FASE DI TEST TERMINATA ---\n");
        System.out.println("\n=== SALVATAGGIO SU FILE COMPLETATO ===");
        
        // ==============================================================
        // = AVVIO INTERFACCIA GRAFICA (GUI) COLLEGATA AL BACKEND (MVC) =
        // ==============================================================
        System.out.println("=== APERTURA CRUSCOTTO GRAFICO IN CORSO... ===");
        
        SwingUtilities.invokeLater(() -> {
            
            // =============================================================
            // =     PASSAGGIO LA RETE AL METODO CHE CREA LA FINESTRA      =
            // =============================================================
            creaEMostraGUI(rete);
        });
    }
    
    // =============================================================
    // =   Il metodo ora richiede la ReteCittadina come parametro  =
    // =============================================================
    private static void creaEMostraGUI(ReteCittadina rete) {
        JFrame finestra = new JFrame("Cruscotto Smart City - Messina");
        finestra.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        finestra.setSize(900, 600);
        finestra.setLocationRelativeTo(null);
        // ==============================================================================
        // = PASSAGGIO RETE AL COSTRUTTORE DEL PANNELLO, PER PRENDERE I SERMAFORI VERI  =
        // ==============================================================================
        MappaGraficaPanelPiuMacchine pannelloMappa = new MappaGraficaPanelPiuMacchine(rete);
      //   MappaGraficaPanel pannelloMappa = new MappaGraficaPanel();
        finestra.add(pannelloMappa);
        
        finestra.setVisible(true);
    }
}