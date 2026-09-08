package com.example.SmartCityMessina;


// ===========================================================================
// =    GESTIONE I/O: Classe responsabile della scrittura su file di testo.  =
// ===========================================================================
public class LogFallitoException extends Exception {
    
       public LogFallitoException(String messaggio){
        super(messaggio);
    }
}
