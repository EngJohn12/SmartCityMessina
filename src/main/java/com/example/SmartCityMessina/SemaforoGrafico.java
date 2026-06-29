package com.example.SmartCityMessina;

import java.awt.Color;
import java.awt.Graphics2D;

public class SemaforoGrafico {
    
    // Usiamo le percentuali così rimane "incollato" alla mappa anche se allarghi la finestra!
    private double percX;
    private double percY;

    // Costruttore: decidiamo le coordinate quando creiamo l'oggetto
    public SemaforoGrafico(double percX, double percY) {
        this.percX = percX;
        this.percY = percY;
    }

    // Un metodo comodo se in futuro vorrai spostarlo durante l'animazione
    public void setPosizione(double percX, double percY) {
        this.percX = percX;
        this.percY = percY;
    }

    // Il metodo che fa il lavoro sporco: disegna il semaforo!
    public void disegna(Graphics2D g2d, int larghezzaFinestra, int altezzaFinestra, String statoLuce) {
        // Calcola i pixel reali in base alla grandezza attuale della finestra
        int x = (int) (larghezzaFinestra * percX);
        int y = (int) (altezzaFinestra * percY);

        // 1. Scocca nera
        g2d.setColor(new Color(40, 40, 40)); 
        g2d.fillRoundRect(x, y, 20, 56, 10, 10);
        
        // 2. Bordo bianco
        g2d.setColor(Color.WHITE);
        g2d.drawRoundRect(x, y, 20, 56, 10, 10);

        // 3. Luce Rossa
        g2d.setColor(statoLuce.equals("ROSSO") ? Color.RED : new Color(60, 0, 0));
        g2d.fillOval(x + 4, y + 4, 12, 12);

        // 4. Luce Gialla
        g2d.setColor(statoLuce.equals("GIALLO") ? Color.YELLOW : new Color(60, 60, 0));
        g2d.fillOval(x + 4, y + 22, 12, 12);

        // 5. Luce Verde
        g2d.setColor(statoLuce.equals("VERDE") ? Color.GREEN : new Color(0, 60, 0));
        g2d.fillOval(x + 4, y + 40, 12, 12);
    }
}