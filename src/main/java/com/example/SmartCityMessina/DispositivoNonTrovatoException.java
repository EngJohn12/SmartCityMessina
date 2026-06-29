package com.example.SmartCityMessina;



// =======================================================================
// = GESTIONE ECCEZIONI: Un'eccezione personalizzata (Checked Exception) =
// =======================================================================

public class DispositivoNonTrovatoException extends Exception {
    
    public DispositivoNonTrovatoException(String messaggio) {
        super(messaggio); // Passa il messaggio alla superclasse Exception
    }
}