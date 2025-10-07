package edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.posluzitelji;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.podaci.PodaciRadara;
import edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.podaci.RedPodaciVozila;
import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.konfiguracije.NeispravnaKonfiguracija;

/**
 * Centralni sustav koji pokrece posluzitelje za registraciju radara i vozila, sadrzi podatke o svim
 * radarima i vozilima
 */
public class CentralniSustav {

  private static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
  int mreznaVrataRadara;
  int mreznaVrataVozila;
  private int mreznaVrataNadzora;
  private int maksVozila;
  private ThreadFactory tvornicaDretvi = Thread.ofVirtual().factory();

  public ConcurrentHashMap<Integer, PodaciRadara> sviRadari =
      new ConcurrentHashMap<Integer, PodaciRadara>();
  public ConcurrentHashMap<Integer, RedPodaciVozila> svaVozila =
      new ConcurrentHashMap<Integer, RedPodaciVozila>();

  public static void main(String[] args) {
    if (args.length != 1) {
      System.out.println("Broj argumenata nije 1!");
      return;
    }

    CentralniSustav centralniSustav = new CentralniSustav();

    try {
      centralniSustav.preuzmiPostavke(args);
    } catch (NeispravnaKonfiguracija | NumberFormatException | UnknownHostException e) {
      System.out.println(e.getMessage());
      return;
    }

    centralniSustav.pokreniPosluzitelje();
  }

  /**
   * Pokreni posluzitelje kao dretve.
   */
  public void pokreniPosluzitelje() {
    var dretvaPRR = this.tvornicaDretvi
        .newThread(new PosluziteljZaRegistracijuRadara(this.mreznaVrataRadara, this));
    dretvaPRR.start();

    var dretvaPV =
        this.tvornicaDretvi.newThread(new PosluziteljZaVozila(this.mreznaVrataVozila, this));
    dretvaPV.start();

    try {
      dretvaPRR.join();
      dretvaPV.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void preuzmiPostavke(String[] args)
      throws NeispravnaKonfiguracija, NumberFormatException, UnknownHostException {
    Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(args[0]);

    this.mreznaVrataRadara = Integer.valueOf(konfig.dajPostavku("mreznaVrataRadara"));
    this.mreznaVrataVozila = Integer.valueOf(konfig.dajPostavku("mreznaVrataVozila"));
    this.mreznaVrataNadzora = Integer.valueOf(konfig.dajPostavku("mreznaVrataNadzora"));
    this.maksVozila = Integer.valueOf(konfig.dajPostavku("maksVozila"));
  }

}
