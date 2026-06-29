package com.example.SmartCityMessina;

import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList; 
import java.util.List;      
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

public class MappaGraficaPanelPiuMacchine extends JPanel {

    private BufferedImage iconaAmbulanza;
    private BufferedImage iconaTesla; 
    private BufferedImage iconaCarroAttrezzi; 
    //  ===================================
    //  = VARIABILI PER IL CARRO ATTREZZI =
    //  ===================================
    private boolean carroAttrezziInAzione = false;
    private int timerCarroAttrezzi = 0;
    private double crashX = -1;
    private double crashY = -1;

    // ================================================
    // = CLASSE INTERNA PER GESTIRE OGNI SINGOLA AUTO =
    // ================================================
    private class AutoGrafica {
        double[][] percorsoAssegnato;
        double progressoAttuale;
        double velocita;
        boolean isEmergenza;
        boolean isElettrica; 
        //  ===========================================
        //  = VARIABILI PER LE COLLISIONI E POSIZIONE =
        //  ===========================================
        boolean inIncidente = false;
        double currentX = -1;
        double currentY = -1;
        
        public AutoGrafica(double[][] percorso, double velocitaIniziale, double offsetPartenza) {
            this(percorso, velocitaIniziale, offsetPartenza, false, false); 
        }

        public AutoGrafica(double[][] percorso, double velocitaIniziale, double offsetPartenza, boolean isEmergenza, boolean isElettrica) {
            this.percorsoAssegnato = percorso;
            this.progressoAttuale = offsetPartenza;
            this.velocita = velocitaIniziale;
            this.isEmergenza = isEmergenza;
            this.isElettrica = isElettrica;
        }
    }

    private Image sfondo, iconaAuto, iconaSensore;
    
    // ============
    // = PERCORSI =
    // ============
    private final double[][] percorsoGaribaldiNordToSud =   { 
        {0.48, -0.10}, {0.48, 0.0}, {0.47, 0.38}, {0.45, 1.10} 
    };
    
    private final double[][] percorsoGaribaldiSudToNordDiritto = { 
        {0.53, 1.10}, {0.53, 0.85}, {0.53, 0.70}, {0.54, 0.45}, {0.56, -0.10} 
    };
     
    private final double[][] percorsoGaribaldiSudToEstGiraDestra = { 
        {0.552, 1.10}, {0.553, 0.85}, {0.553, 0.70}, {0.565, 0.65}, 
        {0.58, 0.635}, {0.60, 0.625}, {0.62, 0.625}, {0.78, 0.90}, {0.76, 1.35} 
    };
    
    private final double[][] percorsoGaribaldiSudToNordDirittoGiallo = { 
        {0.552, 1.10}, {0.553, 0.85}, {0.555, 0.70}, {0.56, 0.45}, {0.58, -0.10} 
    };
    
    private final double[][] percorsoEstToOvestVittorioEmanueleDirittoDestra = { 
        {0.00, 0.335}, {0.32, 0.54}, {0.40, 0.60}, {0.55, 0.65}, 
        {0.62, 0.62}, {0.78, 0.90}, {0.76, 1.35} 
    };
    
    private final double[][] percorsoBoccettaOvestToSudGiraDestra = { 
        {0.00, 0.38}, {0.32, 0.59}, {0.40, 0.64}, {0.44, 0.68}, 
        {0.44, 0.85}, {0.435, 1.10} 
    };

    private final double[][] percorsoAmbulanzaParallelo = { 
        {0.80, 1.10}, {0.81, 0.85}, {0.817, 0.70}, {0.825, 0.45}, {0.84, -0.10} 
    };

    private List<AutoGrafica> flottaAuto = new ArrayList<>();
    //  ====================
    //  = SEMAFORI GRAFICI =
    //  ====================
    
    // private SemaforoGragico = new SemaforoGrafico(x,y) [Posizione]
    private SemaforoGrafico semaforoGaribaldiNordToSud = new SemaforoGrafico(0.475, 0.34);
    private SemaforoGrafico semaforoGaribaldiSudToNordGiraADestraBoccetta = new SemaforoGrafico(0.61, 0.70);
    private SemaforoGrafico semaforoGaribaldiSudToNordDirittoBoccetta = new SemaforoGrafico(0.495, 0.70);
    private SemaforoGrafico semaforoBoccettaEstToOvestGaribaldiDirittoDestra = new SemaforoGrafico(0.41 , 0.70);
    private SemaforoGrafico semaforoBoccettaEstToOvestGaribaldiDirittoSinistra = new SemaforoGrafico(0.41 , 0.475);
    private SemaforoGrafico semaforoEstToOvestVittorioEmanueleDirittoSinistra = new SemaforoGrafico(0.755 , 0.20);
    private SemaforoGrafico semaforoEstToOvestVittorioEmanueleDirittoDestra = new SemaforoGrafico(0.755 , 0.66);
    private SemaforoGrafico semaforoVittorioEmanueleSudToNord = new SemaforoGrafico(0.855 , 0.55);
    
    private Semaforo backendSemGaribaldi;
    private Semaforo backendSemBoccetta;
    private SensoreAmbientale sensoreSmogBackEnd;
    
    private int contatoreTempo = 0;
    private Timer timer;
    private JButton btnPlayPause;
    //  =================================
    //  = VARIABILE PER IL SENSORE SMOG = 
    //  =================================
    private int livelloSmogGrafico = 40;

    public MappaGraficaPanelPiuMacchine(ReteCittadina rete) {
        
        try {
            this.backendSemGaribaldi = (Semaforo) rete.cercaDispositivo("SEM-Garibaldi-N-S");
            this.backendSemBoccetta = (Semaforo) rete.cercaDispositivo("SEM-Boccetta-E-O-Dx");
            this.sensoreSmogBackEnd = (SensoreAmbientale) rete.cercaDispositivo("SENS-01 ");
        } catch (DispositivoNonTrovatoException e) {
            System.out.println("Attenzione: Semafori backend non trovati per la sincronizzazione grafica.");
        }

        try {
            this.sfondo = ImageIO.read(new File("immagini/mappa_sfondo.jpeg"));
            this.iconaAuto = ImageIO.read(new File("immagini/icona_macchina.png"));
            this.iconaSensore = ImageIO.read(new File("immagini/icona_sensore.png"));
            this.iconaAmbulanza = ImageIO.read(new File("immagini/ambulanza.png"));
            this.iconaTesla = ImageIO.read(new File("immagini/tesla.png"));
            this.iconaCarroAttrezzi = ImageIO.read(new File("immagini/carro_attrezzi.png"));
        } catch (IOException e) { e.printStackTrace(); }

        double velocitaStandard = 0.0030;

        flottaAuto.add(new AutoGrafica(percorsoGaribaldiNordToSud, velocitaStandard, 0.0));
        flottaAuto.add(new AutoGrafica(percorsoEstToOvestVittorioEmanueleDirittoDestra, velocitaStandard, 0.0));
        flottaAuto.add(new AutoGrafica(percorsoBoccettaOvestToSudGiraDestra, velocitaStandard, 0.4));
        flottaAuto.add(new AutoGrafica(percorsoGaribaldiSudToEstGiraDestra, 0.0029, 0.8, false, false));
        flottaAuto.add(new AutoGrafica(percorsoGaribaldiSudToNordDiritto, velocitaStandard, 0.1, false, true));
        flottaAuto.add(new AutoGrafica(percorsoEstToOvestVittorioEmanueleDirittoDestra, velocitaStandard, 3.0, false, true));
        flottaAuto.add(new AutoGrafica(percorsoAmbulanzaParallelo, 0.0050, 0.0, true, false));
        
        btnPlayPause = new JButton("Pausa"); 
        btnPlayPause.addActionListener(e -> {
            if (timer.isRunning()) { timer.stop(); btnPlayPause.setText("Riprendi");
            } else { timer.start(); btnPlayPause.setText("Pausa"); }
        });
        this.add(btnPlayPause);

        JButton btnParcheggio = new JButton("Telecamere Parcheggio");
        btnParcheggio.addActionListener(e -> {
            javax.swing.JFrame framePark = new javax.swing.JFrame("Smart Parking - Palacultura");
            framePark.setSize(800, 400); 
            framePark.setLocationRelativeTo(null);
            
            //il pannello viene salvato in una variabile pannelloPark
            ParcheggioPalaculturaPanel pannelloPark = new ParcheggioPalaculturaPanel();
            framePark.add(pannelloPark);
         
         // Alla chiusura della finestra vi è eliminazione della memoria
            framePark.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
            
            // 3. Aggiungiamo addWindowListener per il click sulla X rossa
            framePark.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    // Fermiamo fisicamente la simulazione!
                    pannelloPark.fermaSimulazione();
                    System.out.println("📺 [SISTEMA] Telecamere Parcheggio disattivate. Simulazione sospesa.");
                }
        });
            framePark.setVisible(true);
        });
        this.add(btnParcheggio);
        
        
        JButton btnCarroAttrezzi = new JButton("Chiama Carro Attrezzi");
        btnCarroAttrezzi.addActionListener(e -> {
            boolean incidentePresente = false;
            for (AutoGrafica a : flottaAuto) {
                if (a.inIncidente) {
                    incidentePresente = true;
                    crashX = a.currentX;
                    crashY = a.currentY;
                    break;
                }
            }
            if (incidentePresente && !carroAttrezziInAzione) {
                carroAttrezziInAzione = true;
                timerCarroAttrezzi = 60; 
                System.out.println("🚜 [EMERGENZA] Carro attrezzi in arrivo sul luogo dell'incidente!");
            } else if (!incidentePresente) {
                System.out.println("ℹ️ Nessun incidente rilevato sulla mappa.");
            }
        });
        this.add(btnCarroAttrezzi);

        timer = new Timer(30, e -> aggiornaAnimazione());
        timer.start();
    }

    private void aggiornaAnimazione() {
        contatoreTempo++;
        
        // =============================
        // = LOGICA SENSORE AMBIENTALE =
        // =============================
        int vecchioSmog = livelloSmogGrafico;

        if (Math.random() > 0.85) { 
        livelloSmogGrafico += (Math.random() > 0.5) ? 5 : -5;
        if (livelloSmogGrafico < 10) livelloSmogGrafico = 10;
        if (livelloSmogGrafico > 95) livelloSmogGrafico = 95;
        
        // 🚨 NUOVO: Sincronizziamo il backend con la simulazione grafica!
        if (sensoreSmogBackEnd != null) {
            sensoreSmogBackEnd.aggiornaLivelloSmog(livelloSmogGrafico);
        }
    }

        if (vecchioSmog != livelloSmogGrafico) {
            if (livelloSmogGrafico >= 75) {
                System.out.println("🚨 [ALLARME SENSORE] Smog critico: " + livelloSmogGrafico + " AQI! Richiesto blocco traffico.");
            }
        }

        // ===================================
        // =  GESTIONE SEMAFORI E MOVIMENTO  =
        // ===================================
        int tempoAttuale = contatoreTempo % 280;
        String nuovoColoreG = "VERDE";
        String nuovoColoreB = "ROSSO";
        //  =======================
        //  = Override ambientale =
        //  =======================
        
        if (livelloSmogGrafico >= 75) {
            nuovoColoreG = "ROSSO";
            nuovoColoreB = "ROSSO";
        } else {
            // =================
            // = Ciclo normale =
            // =================
            if (tempoAttuale < 100) { nuovoColoreG = "VERDE"; nuovoColoreB = "ROSSO"; } 
            else if (tempoAttuale < 140) { nuovoColoreG = "GIALLO"; nuovoColoreB = "ROSSO"; } 
            else if (tempoAttuale < 240) { nuovoColoreG = "ROSSO"; nuovoColoreB = "VERDE"; } 
            else { nuovoColoreG = "ROSSO"; nuovoColoreB = "GIALLO"; }
        }

        if (backendSemGaribaldi != null && backendSemBoccetta != null) {
            backendSemGaribaldi.setColore(nuovoColoreG);
            backendSemBoccetta.setColore(nuovoColoreB);
        }
        String coloreGaribaldiAttuale = backendSemGaribaldi != null ? backendSemGaribaldi.getColore() : "VERDE";
        String coloreBoccettaAttuale = backendSemBoccetta != null ? backendSemBoccetta.getColore() : "ROSSO";

        for (AutoGrafica auto : flottaAuto) {
            boolean deveFermarsi = false;
            double p = auto.progressoAttuale;
            
            // =========================================================
            // CONTROLLO SEMAFORI CON AGGANCIO CALAMITA (Anti-Overshoot)
            // =========================================================
            if (!auto.isEmergenza) {
                if (auto.percorsoAssegnato == percorsoGaribaldiSudToNordDiritto || 
                    auto.percorsoAssegnato == percorsoGaribaldiSudToEstGiraDestra || 
                    auto.percorsoAssegnato == percorsoGaribaldiSudToNordDirittoGiallo || 
                    auto.percorsoAssegnato == percorsoGaribaldiNordToSud) {
                    
                    if (coloreGaribaldiAttuale.equals("ROSSO") && p >= 1.70 && p < 1.95) {
                        auto.progressoAttuale = 1.8; // Calamita sulla linea
                        deveFermarsi = true;
                    }
                } else if (auto.percorsoAssegnato == percorsoEstToOvestVittorioEmanueleDirittoDestra) {
                    if (coloreBoccettaAttuale.equals("ROSSO")) {
                        if (p >= 1.70 && p < 1.95) {
                            auto.progressoAttuale = 1.8; // Primo semaforo
                            deveFermarsi = true;
                        } else if (p >= 4.70 && p < 4.95) {
                            auto.progressoAttuale = 4.8; // Secondo semaforo
                            deveFermarsi = true;
                        }
                    }
                } else if (auto.percorsoAssegnato == percorsoBoccettaOvestToSudGiraDestra) {
                    if (coloreBoccettaAttuale.equals("ROSSO") && p >= 1.70 && p < 1.95) {
                        auto.progressoAttuale = 1.8; // Calamita sulla linea
                        deveFermarsi = true;
                    }
                }
            }

            // =========================================================
            // SISTEMA RADAR UNIVERSALE: ANTI-TAMPONAMENTO INTELLIGENTE
            // =========================================================
            if (auto.currentX != -1 && !deveFermarsi) {
                int idx1 = (int) auto.progressoAttuale;
                if (idx1 >= auto.percorsoAssegnato.length - 1) idx1 = auto.percorsoAssegnato.length - 2;
                double dirX1 = auto.percorsoAssegnato[idx1 + 1][0] - auto.percorsoAssegnato[idx1][0];
                double dirY1 = auto.percorsoAssegnato[idx1 + 1][1] - auto.percorsoAssegnato[idx1][1];
                double mag1 = Math.max(0.001, Math.sqrt(dirX1*dirX1 + dirY1*dirY1));
                double nx1 = dirX1 / mag1; 
                double ny1 = dirY1 / mag1;

                for (AutoGrafica altraAuto : flottaAuto) {
                    if (auto != altraAuto && !altraAuto.inIncidente && altraAuto.currentX != -1) {
                        
                        double diffX = altraAuto.currentX - auto.currentX;
                        double diffY = altraAuto.currentY - auto.currentY;
                        double distanza = Math.sqrt((diffX * diffX) + (diffY * diffY));
                        // ===================================================================
                        // = Soglia a 0.06 per evitare fisicamente qualunque contatto visivo =
                        // ===================================================================
                        if (distanza < 0.06) { 
                            int idx2 = (int) altraAuto.progressoAttuale;
                            if (idx2 >= altraAuto.percorsoAssegnato.length - 1) idx2 = altraAuto.percorsoAssegnato.length - 2;
                            double dirX2 = altraAuto.percorsoAssegnato[idx2 + 1][0] - altraAuto.percorsoAssegnato[idx2][0];
                            double dirY2 = altraAuto.percorsoAssegnato[idx2 + 1][1] - altraAuto.percorsoAssegnato[idx2][1];
                            double mag2 = Math.max(0.001, Math.sqrt(dirX2*dirX2 + dirY2*dirY2));
                            double nx2 = dirX2 / mag2; 
                            double ny2 = dirY2 / mag2;

                            double allineamento = (nx1 * nx2) + (ny1 * ny2);
                            // ==================================================================================
                            // =  Controllo angolare: frenano solo se marciano allineate nella stessa direzione =
                            // ==================================================================================
                            if (allineamento > 0.75) {
                                double dotPosizione = (diffX * nx1) + (diffY * ny1);
                                if (dotPosizione > 0) { 
                                    deveFermarsi = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            // ===================================================
            // = Movimento dell'auto se libera e non incidentata =
            // ===================================================
            if (!deveFermarsi && !auto.inIncidente) {
                int i = (int) auto.progressoAttuale;
                if (i >= auto.percorsoAssegnato.length - 1) { i = auto.percorsoAssegnato.length - 2; }
                double dx = auto.percorsoAssegnato[i + 1][0] - auto.percorsoAssegnato[i][0];
                double dy = auto.percorsoAssegnato[i + 1][1] - auto.percorsoAssegnato[i][1];
                auto.progressoAttuale += auto.velocita / Math.sqrt((dx * dx) + (dy * dy));
                
                if (auto.progressoAttuale >= auto.percorsoAssegnato.length - 1) {
                    auto.progressoAttuale = 0.0; 
                    if (auto.percorsoAssegnato == percorsoGaribaldiSudToEstGiraDestra || 
                        auto.percorsoAssegnato == percorsoGaribaldiSudToNordDirittoGiallo) {
                        if (Math.random() > 0.5) {
                            auto.percorsoAssegnato = percorsoGaribaldiSudToNordDirittoGiallo;
                        } else {
                            auto.percorsoAssegnato = percorsoGaribaldiSudToEstGiraDestra;
                        }
                    }
                }
            }
            // ====================================================
            // = Calcolo coordinate esatte correnti per rendering =
            // ====================================================
            
            int index = (int) auto.progressoAttuale;
            double percentuale = auto.progressoAttuale - index; 
            if (index >= auto.percorsoAssegnato.length - 1) { index = auto.percorsoAssegnato.length - 2; percentuale = 1.0; }
            double cx1 = auto.percorsoAssegnato[index][0], cy1 = auto.percorsoAssegnato[index][1];
            double cx2 = auto.percorsoAssegnato[index + 1][0], cy2 = auto.percorsoAssegnato[index + 1][1];
            
            auto.currentX = cx1 + (cx2 - cx1) * percentuale;
            auto.currentY = cy1 + (cy2 - cy1) * percentuale;
        }

        // ==================================================================
        // = COLLISION DETECTION (Crash Mirato Garibaldi-Vittorio Emanuele) =
        // ==================================================================
        for (int i = 0; i < flottaAuto.size(); i++) {
            for (int j = i + 1; j < flottaAuto.size(); j++) {
                AutoGrafica a1 = flottaAuto.get(i);
                AutoGrafica a2 = flottaAuto.get(j);

                boolean coinvolgeGaribaldi = (a1.percorsoAssegnato == percorsoGaribaldiNordToSud || a2.percorsoAssegnato == percorsoGaribaldiNordToSud);
                boolean coinvolgeVittorioEmanuele = (a1.percorsoAssegnato == percorsoEstToOvestVittorioEmanueleDirittoDestra || a2.percorsoAssegnato == percorsoEstToOvestVittorioEmanueleDirittoDestra);

                if (coinvolgeGaribaldi && coinvolgeVittorioEmanuele) {
                    if (!a1.inIncidente && !a2.inIncidente && a1.currentX != -1 && a2.currentX != -1) {
                        double dx = a1.currentX - a2.currentX;
                        double dy = a1.currentY - a2.currentY;
                        double distanza = Math.sqrt((dx * dx) + (dy * dy));

                        if (distanza < 0.025) {
                            a1.inIncidente = true;
                            a2.inIncidente = true;
                            System.out.println("💥 [ALLARME] Rilevato INCIDENTE all'incrocio Garibaldi-Vittorio Emanuele! Veicoli bloccati.");
                        }
                    }
                }
            }
        }
        
        // ===========================
        // = GESTIONE CARRO ATTREZZI =
        // ===========================
        if (carroAttrezziInAzione) {
            timerCarroAttrezzi--;
            if (timerCarroAttrezzi <= 0) {
                carroAttrezziInAzione = false;
                for (AutoGrafica a : flottaAuto) {
                    if (a.inIncidente) {
                        a.inIncidente = false;
                        a.progressoAttuale = 0.0; 
                    }
                }
                System.out.println(" [EMERGENZA RIENTRATA] Strada sgombrata. Il traffico riprende regolarmente.");
            }
        }

        repaint();       
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(); int h = getHeight();
        
        // ======================
        // = SFONDO E TRACCIATI =
        // ======================
        if (sfondo != null) g2d.drawImage(sfondo, 0, 0, w, h, this);

        g2d.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10f, new float[]{15f, 10f}, 0f));

        g2d.setColor(new Color(0, 150, 255, 100)); 
        double[][][] percorsiNormali = { 
            percorsoGaribaldiNordToSud, 
            percorsoGaribaldiSudToNordDiritto, 
            percorsoEstToOvestVittorioEmanueleDirittoDestra, 
            percorsoBoccettaOvestToSudGiraDestra 
        };
        for (double[][] percorso : percorsiNormali) {
            for (int j = 0; j < percorso.length - 1; j++) {
                g2d.drawLine((int)(percorso[j][0]*w), (int)(percorso[j][1]*h), (int)(percorso[j+1][0]*w), (int)(percorso[j+1][1]*h));
            }
        }

        g2d.setColor(new Color(255, 230, 0, 180)); 
        for (int j = 0; j < percorsoGaribaldiSudToEstGiraDestra.length - 1; j++) {
            g2d.drawLine((int)(percorsoGaribaldiSudToEstGiraDestra[j][0]*w), (int)(percorsoGaribaldiSudToEstGiraDestra[j][1]*h), 
                         (int)(percorsoGaribaldiSudToEstGiraDestra[j+1][0]*w), (int)(percorsoGaribaldiSudToEstGiraDestra[j+1][1]*h));
        }
        for (int j = 0; j < percorsoGaribaldiSudToNordDirittoGiallo.length - 1; j++) {
            g2d.drawLine((int)(percorsoGaribaldiSudToNordDirittoGiallo[j][0]*w), (int)(percorsoGaribaldiSudToNordDirittoGiallo[j][1]*h), 
                         (int)(percorsoGaribaldiSudToNordDirittoGiallo[j+1][0]*w), (int)(percorsoGaribaldiSudToNordDirittoGiallo[j+1][1]*h));
        }

        g2d.setColor(Color.MAGENTA); 
        for (int j = 0; j < percorsoGaribaldiSudToEstGiraDestra.length; j++) {
            int xPunto = (int) (percorsoGaribaldiSudToEstGiraDestra[j][0] * w);
            int yPunto = (int) (percorsoGaribaldiSudToEstGiraDestra[j][1] * h);
            g2d.fillOval(xPunto - 6, yPunto - 6, 12, 12);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.drawString("P" + j, xPunto + 10, yPunto - 5);
            g2d.setColor(Color.MAGENTA); 
        }

        g2d.setColor(new Color(255, 50, 50, 150));
        for (int j = 0; j < percorsoAmbulanzaParallelo.length - 1; j++) {
            g2d.drawLine((int)(percorsoAmbulanzaParallelo[j][0]*w), (int)(percorsoAmbulanzaParallelo[j][1]*h), 
                         (int)(percorsoAmbulanzaParallelo[j+1][0]*w), (int)(percorsoAmbulanzaParallelo[j+1][1]*h));
        }

        g2d.setStroke(new BasicStroke(1f));
        // ============
        // = SEMAFORI =
        // ============
        String coloreG = backendSemGaribaldi != null ? backendSemGaribaldi.getColore() : "VERDE";
        String coloreB = backendSemBoccetta != null ? backendSemBoccetta.getColore() : "ROSSO";

        semaforoGaribaldiNordToSud.disegna(g2d, w, h, coloreG);
        semaforoGaribaldiSudToNordGiraADestraBoccetta.disegna(g2d, w, h, coloreG);
        semaforoGaribaldiSudToNordDirittoBoccetta.disegna(g2d, w, h, coloreG);
        semaforoVittorioEmanueleSudToNord.disegna(g2d, w, h, coloreG);
        semaforoBoccettaEstToOvestGaribaldiDirittoDestra.disegna(g2d, w, h, coloreB);
        semaforoBoccettaEstToOvestGaribaldiDirittoSinistra.disegna(g2d, w, h, coloreB);
        semaforoEstToOvestVittorioEmanueleDirittoSinistra.disegna(g2d, w, h, coloreB);
        semaforoEstToOvestVittorioEmanueleDirittoDestra.disegna(g2d, w, h, coloreB);
        
        // =====================
        // = CRUSCOTTO SENSORE =
        // =====================
        int xCruscotto = (int)(w * 0.58);
        int yCruscotto = (int)(h * 0.22);
        int larghezzaCruscotto = 220;
        int altezzaCruscotto = 70;

        g2d.setColor(new Color(30, 30, 30, 200)); 
        g2d.fillRoundRect(xCruscotto, yCruscotto, larghezzaCruscotto, altezzaCruscotto, 15, 15);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawRoundRect(xCruscotto, yCruscotto, larghezzaCruscotto, altezzaCruscotto, 15, 15);

        int xSensore = xCruscotto + 10;
        int ySensore = yCruscotto + 12;
        g2d.drawImage(iconaSensore, xSensore, ySensore, 45, 45, this);
        
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        if (livelloSmogGrafico >= 75) {
            g2d.setColor(new Color(255, 80, 80)); 
            g2d.drawString("SMOG CRITICO: " + livelloSmogGrafico + " AQI", xSensore + 55, ySensore + 18);
            g2d.drawString("BLOCCO TRAFFICO!", xSensore + 55, ySensore + 38);
        } else {
            g2d.setColor(new Color(80, 255, 80)); 
            g2d.drawString("Smog OK: " + livelloSmogGrafico + " AQI", xSensore + 55, ySensore + 28);
        }
        g2d.setStroke(new BasicStroke(1f));
        
        // ================================
        // = DISEGNO MACCHINE E INCIDENTI =
        // ================================
        if (iconaAuto != null) {
            for (AutoGrafica auto : flottaAuto) {
                int i = (int) auto.progressoAttuale;
                double perc = auto.progressoAttuale - i; 
                if (i >= auto.percorsoAssegnato.length - 1) { i = auto.percorsoAssegnato.length - 2; perc = 1.0; }
                double x1 = auto.percorsoAssegnato[i][0], y1 = auto.percorsoAssegnato[i][1];
                double x2 = auto.percorsoAssegnato[i + 1][0], y2 = auto.percorsoAssegnato[i + 1][1];
                double x = x1 + (x2 - x1) * perc, y = y1 + (y2 - y1) * perc;
                double angolo = Math.atan2((y2 - y1) * h, (x2 - x1) * w);

                g2d.translate(x * w, y * h); 
                g2d.rotate(angolo + Math.PI/2); 
                
                if (auto.inIncidente) {
                    g2d.drawImage(iconaAuto, -20, -20, 40, 40, this); 
                    g2d.setColor(new Color(255, 0, 0, 200));
                    g2d.fillOval(-15, -15, 30, 30);
                    g2d.setColor(Color.YELLOW);
                    g2d.setFont(new Font("Arial", Font.BOLD, 10));
                    g2d.drawString("CRASH", -18, 4);
                }
                else if (auto.isEmergenza && iconaAmbulanza != null) {
                    g2d.drawImage(iconaAmbulanza, -20, -20, 40, 40, this);
                    if (contatoreTempo % 10 < 5) g2d.setColor(Color.BLUE); else g2d.setColor(Color.WHITE);
                    g2d.fillOval(-5, -5, 10, 10); 
                } else if (auto.isElettrica && iconaTesla != null) {
                    g2d.drawImage(iconaTesla, -20, -20, 40, 40, this); 
                } else {
                    g2d.drawImage(iconaAuto, -20, -20, 40, 40, this); 
                }

                g2d.rotate(-(angolo + Math.PI/2)); 
                g2d.translate(-(x * w), -(y * h));
            }
        }
        //  ==========================
        //  = DISEGNO CARRO ATTREZZI =
        //  ==========================
        if (carroAttrezziInAzione) {
            int xDraw = (int)(crashX * w);
            int yDraw = (int)(crashY * h);
            
            g2d.setColor(contatoreTempo % 10 < 5 ? Color.ORANGE : Color.YELLOW);
            g2d.fillOval(xDraw - 10, yDraw - 40, 20, 20);

            if (iconaCarroAttrezzi != null) {
                g2d.drawImage(iconaCarroAttrezzi, xDraw - 25, yDraw - 25, 50, 50, this);
            } else {
                g2d.setColor(Color.ORANGE);
                g2d.fillRect(xDraw - 20, yDraw - 20, 40, 40);
                g2d.setColor(Color.BLACK);
                g2d.drawString("SOCCORSO", xDraw - 20, yDraw);
            }
            
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.drawString("RIMOZIONE VEICOLI...", xDraw - 50, yDraw + 35);
        }
    }
}
