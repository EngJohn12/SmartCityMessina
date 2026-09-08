
package com.example.SmartCityMessina;
/**
 * EREDITARIETÀ: L'Auto "è un" Veicolo (Subtyping).
 */

public class Auto extends Veicolo {
    
    
    // ===================================================
    // =       INFORMATION HIDING (Regola dei 3 step)    =
    // ===================================================

    // 1. Private/Public | 2. Class (Static) | 3. Constant (final)
    public static final int MOTORE_ELETTRICO = 0;
    public static final int MOTORE_TERMICO = 1;
    private static final int ACCELERAZIONE_STANDARD = 5;

    // ------ Variabile di Istanza ------
    // 1.Private | 2.Instance | 3. Constant (final)
    private final String modello;

    // 1. Private | 2. Instance | 3. Constant 
    private final int tipoMotore; // 0 = Elettrico, 1 = Termico

    public Auto(String targa, String destinazione, String modello, int tipoMotore) {
        super(targa, destinazione);
        this.modello = modello;
        this.tipoMotore = tipoMotore;
    }

    public int getTipoMotore() {
        return tipoMotore;
    }

    public String getModello() {
        return modello;
    }

    @Override
        public void muovi() {
            setVelocitaAttuale(getVelocitaAttuale() + ACCELERAZIONE_STANDARD);
        String tipo = (tipoMotore == 0) ? "Elettrica" : "Termica";
        System.out.println("[Auto " + tipo + " " + getId() + "] in movimento verso " + getDestinazione() + " a " 
        + getVelocitaAttuale() + " km/h.");
    }

    @Override
    public void riceviMessaggio(Comunicazione mittente, String messaggio) {
        System.out.println("-> " + getId() + " riceve da " + mittente.getId() + ": " + messaggio);
    }
}