
package com.example.SmartCityMessina;

public interface Comunicazione {
    // ========================================================
    // = Metodo per ricevere un messaggio da un'altra entità  =
    // ========================================================
    void riceviMessaggio(Comunicazione mittente, String messaggio);
    // =========================================================
    // = Metodo per inviare un messaggio a un'entità specifica =
    // =========================================================
    void inviaMessaggio(Comunicazione destinatario, String messaggio);
    // =========================================================
    // =       Utile per identificare chi sta parlando         =
    // =========================================================
    String getId(); 
}