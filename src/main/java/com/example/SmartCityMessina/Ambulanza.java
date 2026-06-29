package com.example.SmartCityMessina;

// ============================================================
// =   EREDITARIETÀ: L'Ambulanza "è un" Veicolo.              =
// =   POLIMORFISMO: Reagisce diversamente ai semafori rossi. =
// ============================================================
public class Ambulanza extends Veicolo {

    private boolean inEmergenza;

    public Ambulanza(String targa, String destinazione, boolean inEmergenza) {
        super(targa, destinazione);
        this.inEmergenza = inEmergenza; // Se true, passa col rosso!
    }

    public boolean isInEmergenza() {
        return inEmergenza;
    }

    public void setEmergenza(boolean stato) {
        this.inEmergenza = stato;
    }

    @Override
    public void muovi() {
        // ================================================================
        // = L'ambulanza accelera molto di più rispetto a un'auto normale =
        // ================================================================
        
        int accelerazione = inEmergenza ? 25 : 10;
        setVelocitaAttuale(getVelocitaAttuale() + accelerazione);
        System.out.println("[Ambulanza " + getId() + "] Sirene: " + (inEmergenza ? "ACCESE" : "SPENTE") + " - Velocità: " + getVelocitaAttuale() + " km/h");
    }

    @Override
    public void riceviMessaggio(Comunicazione mittente, String messaggio) {
        System.out.println("-> Radio " + getId() + " riceve da " + mittente.getId() + ": " + messaggio);
        
        if (messaggio.equals("ROSSO")) {
            if (inEmergenza) {
                System.out.println("   [Azione] EMERGENZA! L'ambulanza accende i lampeggianti, ignora il rosso e passa!");
            // ===========================
            // = Non azzera la velocità! = 
            // ===========================    
            } else {
                System.out.println("   [Azione] Nessuna emergenza. L'ambulanza frena e si ferma al semaforo come tutti.");
                setVelocitaAttuale(0);
            }
        }
    }
}