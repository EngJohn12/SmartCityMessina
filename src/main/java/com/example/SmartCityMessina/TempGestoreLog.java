/* package com.example.SmartCityMessina;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// ===========================================================================
// =    GESTIONE I/O: Classe responsabile della scrittura su file di testo.  =
// ===========================================================================
public class TempGestoreLog {
    
    // =========================================================
    // = INFORMATION HIDING (Regola dei 3 step)                =
    // =========================================================
    
    // 1. Private | 2. Class (static) | 3. Constant (final)
    // Valore condiviso e immutabile
    private static final String NOME_FILE = "log_citta.txt";

    public void scriviLog(String messaggio) {
        
        int tentativi = 0;
        boolean scritto = false;
        
        // ====================================================================================================
        // =        I/O SU FILE con "Try-with-resources": chiude il file in automatico alla fine!             =
        // = Il parametro 'true' in FileWriter serve ad "appendere" il testo senza cancellare quello vecchio. =
        // ====================================================================================================
       while(tentativi <3 && !scritto) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(NOME_FILE, true))) {
            writer.write(messaggio);
            writer.newLine(); 
            scritto = true;
        } catch (IOException e) {
            tentativi++;
            if (tentativi >= 3) {
            System.out.println("ERRORE CRITICO DI I/O: Impossibile scrivere sul file di log.");
            System.out.println("Dettagli: " + e.getMessage());
        }
    }
}

    }
}

*/