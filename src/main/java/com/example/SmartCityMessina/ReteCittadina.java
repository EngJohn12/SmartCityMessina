package com.example.SmartCityMessina;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SISTEMA DECENTRALIZZATO: Questa classe NON è un controller che dà ordini.
 * Funge da "Registro" (Service Locator) a cui i dispositivi si connettono
 * per potersi trovare e comunicare tra loro autonomamente.
 */
public class ReteCittadina {
    
    //Polimorfismo parametrico:  generics
    // COLLEZIONI: Uso di Map (Dizionario chiave-valore) e List (Array dinamico)
    private Map<String, Dispositivo> mappaDispositivi;
    private List<Veicolo> flottaVeicoli;

    public ReteCittadina() {
        this.mappaDispositivi = new HashMap<>();
        this.flottaVeicoli = new ArrayList<>();
    }

    // Le entità si "connettono" alla rete
    public void registraDispositivo(Dispositivo d) {
        mappaDispositivi.put(d.getId(), d);
        System.out.println("[Rete Cittadina] Nodo connesso: " + d.getId());
    }

    public void registraVeicolo(Veicolo v) {
        flottaVeicoli.add(v);
        System.out.println("[Rete Cittadina] Veicolo connesso: " + v.getId());
    }

    // Metodo per permettere a un'entità di cercarne un'altra per comunicare
    public Dispositivo cercaDispositivo(String idDaCercare) throws DispositivoNonTrovatoException {
        if (!mappaDispositivi.containsKey(idDaCercare)) {
            throw new DispositivoNonTrovatoException("ERRORE: Il dispositivo " + idDaCercare + " non è raggiungibile sulla rete!");
        }
        return mappaDispositivi.get(idDaCercare);
    }
    
    // Broadcast: Invia un segnale di "tick" temporale, ma OGNI dispositivo 
    // decide in totale autonomia cosa fare (Polimorfismo By Inclusion/Subtyping).
    public void aggiornaTuttiIDispositivi() {
        System.out.println("\n--- SINCRONIZZAZIONE DI RETE ---");
        //(Polimorfismo By Inclusion/Subtyping).
        for (Dispositivo d : mappaDispositivi.values()) {
            d.eseguiAzione(); // Nessun ordine centrale: il dispositivo agisce da solo!
        }
    }
}
