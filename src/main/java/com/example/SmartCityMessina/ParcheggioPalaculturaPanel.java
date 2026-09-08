package com.example.SmartCityMessina;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class ParcheggioPalaculturaPanel extends JPanel {

    private Image iconaTesla, iconaPanda;
    private boolean[] stalliOccupati;
    private int[] tipoAutoParcheggiata; 
    private Timer timerSimulazione;
    private Random random;

    public ParcheggioPalaculturaPanel() {
        this.stalliOccupati = new boolean[12];
        this.tipoAutoParcheggiata = new int[12];
        for (int i = 0; i < 12; i++) tipoAutoParcheggiata[i] = -1; 
        this.random = new Random();

        try {
            this.iconaTesla = ImageIO.read(new File("C:\\Users\\johnd\\Downloads\\SmartCityMessina-main\\SmartCityMessina-main\\src\\immagini\\tesla.png"));
            this.iconaPanda = ImageIO.read(new File("C:\\Users\\johnd\\Downloads\\SmartCityMessina-main\\SmartCityMessina-main\\src\\immagini\\icona_macchina.png"));
        } catch (IOException e) {
            System.out.println("Errore caricamento immagini auto nel parcheggio.");
        }

        JButton btnAllarme = new JButton("🚨 Forza Ingresso Auto Termica in EV");
        btnAllarme.setBackground(new Color(255, 100, 100));
        btnAllarme.setForeground(Color.BLACK);
        btnAllarme.addActionListener(e -> {
            
            stalliOccupati[0] = true;
            tipoAutoParcheggiata[0] = 1; // 1 = Auto Termica
            repaint(); 
            
            Timer ritardoPopup = new Timer(100, evt -> {
                
                String messaggioLog = "🚨 [FORCED ALARM] Rilevata auto TERMICA nello stallo EV n.1!";
                System.out.println(messaggioLog);
                
                try (java.io.FileWriter fw = new java.io.FileWriter("log_citta.txt", true);
                     java.io.PrintWriter out = new java.io.PrintWriter(fw)) {
                    String timestamp = java.time.LocalDateTime.now()
                                       .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    out.println("[" + timestamp + "] " + messaggioLog);
                } catch (java.io.IOException ex) {
                    System.out.println("Errore log: " + ex.getMessage());
                }

                // FORZIAMO LA STAMPA NEL TERMINALE PRIMA CHE IL POPUP BLOCCHI TUTTO
                stampaStatoTerminale();
                
                JOptionPane.showMessageDialog(this, 
                    "🚨 ALLARME VIOLAZIONE!\nVeicolo termico rilevato nelle colonnine EV.\nSegnalazione inviata: CARRO ATTREZZI IN ARRIVO!", 
                    "Smart Security System", 
                    JOptionPane.ERROR_MESSAGE);
                
                System.out.println("🚜 [INTERVENTO CONCLUSO] L'auto parcheggiata abusivamente viene trainata dallo stallo n° 1 dal carro attrezzi.");
                stalliOccupati[0] = false;
                tipoAutoParcheggiata[0] = -1;
                
                // FORZIAMO LA STAMPA NEL TERMINALE DOPO LA RIMOZIONE
                stampaStatoTerminale();
                repaint(); 
            });
            ritardoPopup.setRepeats(false);
            ritardoPopup.start();
        });
        this.add(btnAllarme);

        timerSimulazione = new Timer(2000, e -> simulaMovimento());
        timerSimulazione.start();
    }

    private void simulaMovimento() {
        int stalloCasuale = random.nextInt(12);
        
        if (stalliOccupati[stalloCasuale]) {
            stalliOccupati[stalloCasuale] = false;
            tipoAutoParcheggiata[stalloCasuale] = -1;
        } else {
            stalliOccupati[stalloCasuale] = true;
            int autoInArrivo = random.nextInt(2); 
            tipoAutoParcheggiata[stalloCasuale] = autoInArrivo;
            
            if (stalloCasuale < 6 && autoInArrivo == 1) {
                String messaggioLog = "🚨 [CONTROLLO SENSORI] Rilevata auto TERMICA nello stallo EV n." + (stalloCasuale + 1) + "!";
                System.out.println(messaggioLog);
                
                try (java.io.FileWriter fw = new java.io.FileWriter("log_citta.txt", true);
                     java.io.PrintWriter out = new java.io.PrintWriter(fw)) {
                    String timestamp = java.time.LocalDateTime.now()
                                       .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    out.println("[" + timestamp + "] " + messaggioLog);
                } catch (java.io.IOException ex) {}
                
                final int stalloDaSvuotare = stalloCasuale;
                Timer timerRimozione = new Timer(3000, evt -> {
                    System.out.println("🚜 [INTERVENTO CONCLUSO] L'auto abusiva nello stallo n° " + (stalloDaSvuotare + 1) + " è stata trainata.");
                    stalliOccupati[stalloDaSvuotare] = false;
                    tipoAutoParcheggiata[stalloDaSvuotare] = -1;
                    
                    // Aggiorna il terminale anche quando l'auto sparisce in automatico
                    stampaStatoTerminale();
                    repaint();
                });
                timerRimozione.setRepeats(false);
                timerRimozione.start();
            }
        }
        
        // Stampa lo stato per il normale ciclo di simulazione
        stampaStatoTerminale();
        repaint();
    }

    // =========================================================
    // = NUOVO METODO: STAMPA LO STATO NEL TERMINALE NETBEANS  =
    // =========================================================
    private void stampaStatoTerminale() {
        System.out.println("\n--- AGGIORNAMENTO STATO PARCHEGGIO PALACULTURA ---");
        for (int i = 0; i < 12; i++) {
            String tipoStallo = (i < 6) ? "⚡ EV " : "🔥 ICE";
            String stato = "🟩 VUOTO";
            
            if (stalliOccupati[i]) {
                stato = (tipoAutoParcheggiata[i] == 0) ? "🚗 Tesla (Elettrica)" : "🚙 Toyota (Termica)";
            }
            System.out.printf("Stallo %02d [%s]: %s\n", (i + 1), tipoStallo, stato);
        }
        System.out.println("--------------------------------------------------");
        System.out.flush(); 
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int larghezza = getWidth();
        int altezza = getHeight();
        
        g2d.setColor(new Color(50, 50, 55));
        g2d.fillRect(0, 0, larghezza, altezza);

        int larghezzaStallo = larghezza / 7; 
        int altezzaStallo = altezza / 3;     
        int margineX = (larghezza - (larghezzaStallo * 6)) / 2; 
        int margineYTop = 48; 
        int margineYBot = altezza - altezzaStallo - 20;

        int postiLiberiEV = 0;
        int postiLiberiICE = 0;
        
        for (int i = 0; i < 12; i++) {
            int x = margineX + (i % 6) * larghezzaStallo;
            int y = (i < 6) ? margineYTop : margineYBot;
            int larghezzaRett = larghezzaStallo - 10;
            boolean isEV = (i < 6);

            if (isEV) {
                g2d.setColor(new Color(200, 200, 200, 50)); 
                g2d.fillRect(x, y, larghezzaRett, altezzaStallo); 
                
                g2d.setStroke(new BasicStroke(4f));
                g2d.setColor(new Color(0, 200, 100, 180));
                g2d.drawRect(x, y, larghezzaRett, altezzaStallo);
                g2d.setFont(new Font("Arial", Font.BOLD, 18));
                g2d.drawString("⚡ EV " + (i + 1), x + 8, y + 25);
            } else {
                g2d.setStroke(new BasicStroke(4f));
                g2d.setColor(new Color(200, 200, 200));
                g2d.drawRect(x, y, larghezzaRett, altezzaStallo);
                g2d.setFont(new Font("Arial", Font.BOLD, 18));
                g2d.drawString("🔥 ICE " + (i + 1), x + 8, y + 25);
                g2d.drawString("  ICE ", x + larghezzaRett / 2 - 25, y + altezzaStallo / 2);
            }

            if (stalliOccupati[i]) {
                Image img = (tipoAutoParcheggiata[i] == 0) ? iconaTesla : iconaPanda;
                if (img != null) {
                    if (isEV) g2d.drawImage(img, x+5, y+30, larghezzaRett-10, altezzaStallo-35, this);
                    else {
                        g2d.translate(x+larghezzaRett/2, y+altezzaStallo/2); g2d.rotate(Math.PI);
                        g2d.drawImage(img, -(larghezzaRett-10)/2, -(altezzaStallo-35)/2, larghezzaRett-10, altezzaStallo-35, this);
                        g2d.rotate(-Math.PI); g2d.translate(-(x+larghezzaRett/2), -(y+altezzaStallo/2));
                    }
                }
                if (isEV && tipoAutoParcheggiata[i] == 1) {
                    g2d.setColor(Color.RED); g2d.setStroke(new BasicStroke(6f));
                    g2d.drawLine(x+10, y+30, x+larghezzaRett-10, y+altezzaStallo-10);
                    g2d.drawLine(x+larghezzaRett-10, y+30, x+10, y+altezzaStallo-10);
                    g2d.setColor(System.currentTimeMillis() % 1000 < 500 ? Color.RED : Color.YELLOW);
                } else g2d.setColor(isEV ? new Color(0, 150, 255) : Color.RED);
            } else {
                if (isEV) postiLiberiEV++; else postiLiberiICE++;
                g2d.setColor(Color.GREEN);
            }
            g2d.fillOval(x + larghezzaRett / 2 - 7, y - 10, 15, 15);
        }

        String testo = " PALA CULTURA - HUB ⚡ EV: " + postiLiberiEV + " LIBERI / 🔥 ICE: " + postiLiberiICE + " LIBERI";
        Font fontDisplay = new Font("Monospaced", Font.BOLD, 22);
        g2d.setFont(fontDisplay);
        FontMetrics fm = g2d.getFontMetrics(fontDisplay);

        int larghezzaTesto = fm.stringWidth(testo);
        int larghezzaRett = larghezzaTesto + 40; 
        int altezzaRett = 50;

        int xRett = (larghezza - larghezzaRett) / 2;
        int fineRigaEV = margineYTop + altezzaStallo;
        int inizioRigaICE = margineYBot;
        int yRett = fineRigaEV + (inizioRigaICE - fineRigaEV - altezzaRett) / 2;

        g2d.setColor(Color.BLACK);
        g2d.fillRoundRect(xRett, yRett, larghezzaRett, altezzaRett, 15, 15);
        
        int xTesto = xRett + (larghezzaRett - larghezzaTesto) / 2;
        int yTesto = yRett + ((altezzaRett - fm.getHeight()) / 2) + fm.getAscent();
        
        g2d.setColor(new Color(20, 250, 150));
        g2d.drawString(testo, xTesto, yTesto);
    }
    public void fermaSimulazione() {
        if (timerSimulazione != null) {
            timerSimulazione.stop();
        }
    }
}