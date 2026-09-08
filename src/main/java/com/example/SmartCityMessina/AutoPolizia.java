package com.example.SmartCityMessina;

public class AutoPolizia extends Auto implements VeicoloPrioritario {
    
    private boolean inInseguimento;

    public AutoPolizia(String targa, String destinazione, String modello, int tipoMotore, boolean isInseguimento) {
        super(targa, destinazione, modello , tipoMotore);
        setInseguimento(inInseguimento);
    }
    
    
public boolean isInInseguimento() {
        return inInseguimento;
    }

    public void setInseguimento(boolean stato) {
        this.inInseguimento = stato;
    }
    
    @Override
    public void richiediEmergenza() {
        if (inInseguimento) {
            System.out.println("🚓 [POLIZIA] Sirene spiegate! Inseguimento in corso, blocco traffico imminente!");
        } else {
            System.out.println("🚓 [POLIZIA] Pattugliamento di routine in corso.");
        }
    }
    
    @Override
    public void riceviMessaggio(Comunicazione mittente, String messaggio) {
        System.out.println("-> Centrale di Polizia riceve da " + mittente.getId() + ": " + messaggio);
        
        if (messaggio.equals("ROSSO") && inInseguimento) {
            System.out.println("   [Azione] La volante ignora il semaforo rosso per inseguimento!");
        } else if (messaggio.equals("ROSSO")) {
            System.out.println("   [Azione] Volante in pattugliamento, si ferma al semaforo.");
            setVelocitaAttuale(0);
        }
    }
}
    
    
    

