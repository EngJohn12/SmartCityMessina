package com.example.SmartCityMessina;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// ===========================================================================
// =    GESTIONE I/O: Classe responsabile della scrittura su file di testo.  =
// ===========================================================================
public class GestoreLog {
    // ===================================================
    // = NOME DEL FILE CHE VIENE CREATO "log_citta.txt"  =
    // ===================================================
    
    private final String nomeFile = "log_citta.txt";

    public void scriviLog(String messaggio) {
        // ==============================================================
        // =  Otteniamo l'ora esatta per rendere il log più preciso     =
        // ==============================================================
        
        String oraCorrente = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String rigaDaScrivere = "[" + oraCorrente + "] " + messaggio;
        // ====================================================================================================
        // =        I/O SU FILE con "Try-with-resources": chiude il file in automatico alla fine!             =
        // = Il parametro 'true' in FileWriter serve ad "appendere" il testo senza cancellare quello vecchio. =
        // ====================================================================================================
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeFile, true))) {
            
            writer.write(rigaDaScrivere);
            writer.newLine(); // Va a capo per il prossimo log
            
        } catch (IOException e) {
            // =============================================
            // = Gestione dell'eccezione specifica per I/O =
            // =============================================
            System.out.println("ERRORE CRITICO DI I/O: Impossibile scrivere sul file di log.");
            System.out.println("Dettagli: " + e.getMessage());
        }
    }
}