package com.example.SmartCityMessina;

import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

public class MappaGraficaPanel extends JPanel {

    private Image sfondo, iconaAuto, iconaSensore;
    
    // =========================================================
    // =       PERCORSI (Calibrati con i punti di rottura)     =
    // =========================================================
    private final double[][] percorsoGaribaldiNordToSud = {
        {0.48, -0.10}, 
        {0.48, 0},  
        {0.465, 0.38}, 
        {0.445, 1.10} 
    };

    private final double[][] percorsoGaribaldiSudToNordDiritto = {
        {0.53, 1.10},  // P0: Partenza (fuori schermo in basso)
        {0.53, 0.85},  // P1: Risalita verso l'incrocio
        {0.53, 0.70},  // P2: Linea di Stop (esattamente al semaforo)
        {0.54, 0.45},  // P3: Oltre l'incrocio 
        {0.56, -0.10}  // P4: Uscita (fuori schermo in alto)
    };
    
    private final double[][] percorsoEstToOvestVittorioEmanueleDirittoDestra = {
        {0.00, 0.335}, // P0: Partenza a sinistra
        {0.32, 0.54},  // P1: Metà rettilineo
        {0.40, 0.60},  // P2: Linea di Stop (Primo Semaforo)
        {0.55, 0.65},  // P3: Oltre l'incrocio
        {0.62, 0.62},  // P4: Avvicinamento
        {0.78, 0.90},  // P5: Linea di Stop (Secondo Semaforo)
        {0.76, 1.35}   // P6: Uscita
    };
    
    // ==================================================================================================================
    // =              NUOVO PERCORSO: Arriva da Boccetta (sinistra) e gira a destra (scende in Garibaldi)               =
    // ==================================================================================================================
    private final double[][] percorsoBoccettaOvestToSudGiraDestra = {
        {0.00, 0.38}, // P0: Partenza a sinistra
        {0.32, 0.59},  // P1: Metà rettilineo
        {0.40, 0.64},  // P2: Linea di Stop (Primo Semaforo)
        {0.44, 0.68},  // P3: L'auto entra nell'incrocio e inizia a curvare a destra
        {0.44, 0.85},  // P4: Allineamento con la corsia a scendere di Via Garibaldi
        {0.435, 1.10}   // P5: Uscita (fuori schermo in basso)
    };

    // =========================================================
    // =            VARIABILI DI STATO DELL'AUTO               =
    // =========================================================
    // Attiviamo il nuovo percorso per testare la curva a destra!
    private double[][] percorsoAttivo = percorsoEstToOvestVittorioEmanueleDirittoDestra;
    
    private double progressoAuto = 0.0; 
    
    // =========================================================
    // =                I NOSTRI OGGETTI SEMAFORO              =
    // =========================================================
    private SemaforoGrafico semaforoGaribaldiNordToSud = new SemaforoGrafico(0.475, 0.34);
    private SemaforoGrafico semaforoGaribaldiSudToNordGiraADestraBoccetta = new SemaforoGrafico(0.57, 0.70);
    private SemaforoGrafico semaforoGaribaldiSudToNordDirittoBoccetta = new SemaforoGrafico(0.495, 0.70);
    private SemaforoGrafico semaforoBoccettaEstToOvestGaribaldiDirittoDestra = new SemaforoGrafico(0.41 , 0.70);
    private SemaforoGrafico semaforoBoccettaEstToOvestGaribaldiDirittoSinistra = new SemaforoGrafico(0.41 , 0.475);
    private SemaforoGrafico semaforoEstToOvestVittorioEmanueleDirittoSinistra = new SemaforoGrafico(0.755 , 0.20);
    private SemaforoGrafico semaforoEstToOvestVittorioEmanueleDirittoDestra = new SemaforoGrafico(0.755 , 0.66);
    private SemaforoGrafico semaforoVittorioEmanueleSudToNord = new SemaforoGrafico(0.855 , 0.55);
    
    // =========================================================
    // =               LOGICA DELL'INCROCIO                    =
    // =========================================================
    private String coloreGaribaldi = "VERDE";
    private String coloreBoccetta = "ROSSO";
    
    private int contatoreTempo = 0;
    private Timer timer;
    private JButton btnPlayPause;

    public MappaGraficaPanel() {
        try {
            this.sfondo = ImageIO.read(new File("immagini/mappa_sfondo.jpeg"));
            this.iconaAuto = ImageIO.read(new File("immagini/icona_macchina.png"));
            this.iconaSensore = ImageIO.read(new File("immagini/icona_sensore.png"));
        } catch (IOException e) { 
            e.printStackTrace(); 
        }

        btnPlayPause = new JButton("Pausa"); 
        btnPlayPause.addActionListener(e -> {
            if (timer.isRunning()) {
                timer.stop();
                btnPlayPause.setText("Riprendi");
            } else {
                timer.start();
                btnPlayPause.setText("Pausa");
            }
        });
        this.add(btnPlayPause);

        timer = new Timer(30, e -> aggiornaAnimazione());
        timer.start();
    }

    private void aggiornaAnimazione() {
        contatoreTempo++;
        
        int tempoAttuale = contatoreTempo % 280;

        if (tempoAttuale < 100) {
            coloreGaribaldi = "VERDE"; coloreBoccetta = "ROSSO";
        } else if (tempoAttuale < 140) {
            coloreGaribaldi = "GIALLO"; coloreBoccetta = "ROSSO";
        } else if (tempoAttuale < 240) {
            coloreGaribaldi = "ROSSO"; coloreBoccetta = "VERDE";
        } else {
            coloreGaribaldi = "ROSSO"; coloreBoccetta = "GIALLO";
        }

        // =========================================================
        // =                LOGICA SEMAFORO UNIVERSALE             =
        // =========================================================
        boolean deveFermarsi = false;
        
        if (percorsoAttivo == percorsoGaribaldiSudToNordDiritto || percorsoAttivo == percorsoGaribaldiNordToSud) {
            deveFermarsi = coloreGaribaldi.equals("ROSSO") && progressoAuto > 1.8 && progressoAuto < 1.95;
        } else if (percorsoAttivo == percorsoEstToOvestVittorioEmanueleDirittoDestra) {
            boolean fermoAlPrimo = progressoAuto > 1.8 && progressoAuto < 1.95;
            boolean fermoAlSecondo = progressoAuto > 4.8 && progressoAuto < 4.95; 
            deveFermarsi = coloreBoccetta.equals("ROSSO") && (fermoAlPrimo || fermoAlSecondo);
        } else if (percorsoAttivo == percorsoBoccettaOvestToSudGiraDestra) {
            // Se gira a destra, c'è solo un semaforo prima della curva
            deveFermarsi = coloreBoccetta.equals("ROSSO") && progressoAuto > 1.8 && progressoAuto < 1.95;
        }
        // =========================================================
        // =    IL MOTORE DELL'AUTO A VELOCITÀ COSTANTE (VERA!)    =
        // =========================================================
        if (!deveFermarsi) {
            int indiceSegmento = (int) progressoAuto;
            if (indiceSegmento >= percorsoAttivo.length - 1) {
                indiceSegmento = percorsoAttivo.length - 2;
            }

            double x1 = percorsoAttivo[indiceSegmento][0];
            double y1 = percorsoAttivo[indiceSegmento][1];
            double x2 = percorsoAttivo[indiceSegmento + 1][0];
            double y2 = percorsoAttivo[indiceSegmento + 1][1];

            double distanzaX = x2 - x1;
            double distanzaY = y2 - y1;
            double lunghezzaSegmento = Math.sqrt((distanzaX * distanzaX) + (distanzaY * distanzaY));

            double velocitaReale = 0.003; 

            double incremento = velocitaReale / lunghezzaSegmento;
            progressoAuto += incremento;
            
            if (progressoAuto >= percorsoAttivo.length - 1) {
                progressoAuto = 0.0; 
            }
        }
        
        repaint();       
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        
        // =========================================================
        // =                  Disegna Sfondo                       =
        // =========================================================
        if (sfondo != null) g2d.drawImage(sfondo, 0, 0, w, h, this);

        // =========================================================
        // =  Disegna il percorso attivo (Linea Blu tratteggiata)  =
        // =========================================================
        g2d.setColor(new Color(0, 150, 255, 150)); 
        g2d.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10f, new float[]{15f, 10f}, 0f));

        for (int j = 0; j < percorsoAttivo.length - 1; j++) {
            int x1 = (int) (percorsoAttivo[j][0] * w);
            int y1 = (int) (percorsoAttivo[j][1] * h);
            int x2 = (int) (percorsoAttivo[j+1][0] * w);
            int y2 = (int) (percorsoAttivo[j+1][1] * h);
            g2d.drawLine(x1, y1, x2, y2);
        }

        g2d.setStroke(new BasicStroke(1f));

        // ===========================================================
        // =   VISUAL DEBUG: Triangolini Rossi sui punti di rottura  =
        // ===========================================================
        g2d.setColor(Color.RED); 
        for (int i = 0; i < percorsoAttivo.length; i++) {
            int px = (int) (percorsoAttivo[i][0] * w);
            int py = (int) (percorsoAttivo[i][1] * h);
            int dim = 12; 
            int[] xPoints = {px, px - dim/2, px + dim/2};
            int[] yPoints = {py - dim/2, py + dim/2, py + dim/2};
            g2d.fillPolygon(xPoints, yPoints, 3);
            g2d.setColor(Color.BLACK);
            g2d.drawString("P" + i, px + 10, py);
            g2d.setColor(Color.RED); 
        }
        
        // =========================================================
        // =          Disegna i Semafori (Asse Nord-Sud)           =
        // =========================================================
        semaforoGaribaldiNordToSud.disegna(g2d, w, h, coloreGaribaldi);
        semaforoGaribaldiSudToNordGiraADestraBoccetta.disegna(g2d, w, h, coloreGaribaldi);
        semaforoGaribaldiSudToNordDirittoBoccetta.disegna(g2d, w, h, coloreGaribaldi);
        semaforoVittorioEmanueleSudToNord.disegna(g2d, w, h, coloreGaribaldi);
        
        // =========================================================
        // =        Disegna i Semafori (Asse Est-Ovest)            =
        // =========================================================
        semaforoBoccettaEstToOvestGaribaldiDirittoDestra.disegna(g2d, w, h, coloreBoccetta);
        semaforoBoccettaEstToOvestGaribaldiDirittoSinistra.disegna(g2d, w, h, coloreBoccetta);
        semaforoEstToOvestVittorioEmanueleDirittoSinistra.disegna(g2d, w, h, coloreBoccetta);
        semaforoEstToOvestVittorioEmanueleDirittoDestra.disegna(g2d, w, h, coloreBoccetta);
        
        // =========================================================
        // =                      Disegna il Sensore               =
        // =========================================================
        g2d.drawImage(iconaSensore, (int)(w*0.61), (int)(h*0.26), 45, 45, this);
        
        // =========================================================
        // =     MOTORE DI MOVIMENTO UNIVERSALE DELL'AUTO          =
        // =========================================================
        if (iconaAuto != null) {
            int indiceSegmento = (int) progressoAuto;
            double percentuale = progressoAuto - indiceSegmento; 
            
            if (indiceSegmento >= percorsoAttivo.length - 1) {
                indiceSegmento = percorsoAttivo.length - 2;
                percentuale = 1.0;
            }

            double xStart = percorsoAttivo[indiceSegmento][0];
            double yStart = percorsoAttivo[indiceSegmento][1];
            double xEnd = percorsoAttivo[indiceSegmento + 1][0];
            double yEnd = percorsoAttivo[indiceSegmento + 1][1];

            double x = xStart + (xEnd - xStart) * percentuale;
            double y = yStart + (yEnd - yStart) * percentuale;

            double dx = xEnd - xStart;
            double dy = yEnd - yStart;
            double angolo = Math.atan2(dy * h, dx * w);

            g2d.translate(x * w, y * h);
            g2d.rotate(angolo + Math.PI/2); 
            g2d.drawImage(iconaAuto, -25, -25, 50, 50, this);
            g2d.rotate(-(angolo + Math.PI/2));
            g2d.translate(-(x * w), -(y * h));
        }
    }
}