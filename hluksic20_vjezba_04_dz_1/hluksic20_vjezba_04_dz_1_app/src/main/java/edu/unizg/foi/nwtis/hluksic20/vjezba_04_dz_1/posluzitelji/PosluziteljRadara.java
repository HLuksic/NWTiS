package edu.unizg.foi.nwtis.hluksic20.vjezba_04_dz_1.posluzitelji;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.ThreadFactory;
import edu.unizg.foi.nwtis.hluksic20.vjezba_04_dz_1.podaci.BrzoVozilo;
import edu.unizg.foi.nwtis.hluksic20.vjezba_04_dz_1.podaci.PodaciRadara;
import edu.unizg.foi.nwtis.hluksic20.vjezba_04_dz_1.pomocnici.MrezneOperacije;
import edu.unizg.foi.nwtis.hluksic20.vjezba_04_dz_1.posluzitelji.radnici.RadnikZaRadare;
import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.konfiguracije.NeispravnaKonfiguracija;

/**
 * Sinkroni visedretveni server za brisanje i dodavanje radara.
 */
public class PosluziteljRadara {

  private ThreadFactory tvornicaDretvi = Thread.ofVirtual().factory();
  private PodaciRadara podaciRadara;
  public HashMap<Integer, BrzoVozilo> pracenaVozila = new HashMap<Integer, BrzoVozilo>();

  public static void main(String[] args) {
    if (args.length != 1 && args.length != 3) {
      System.out.println("Neispravan broj argumenata (mora biti 1 ili 3).");
      return;
    }

    PosluziteljRadara posluziteljRadara = new PosluziteljRadara();

    try {
      posluziteljRadara.preuzmiPostavke(args);

      // registriraj i pokreni radar ako je registracija uspjesna
      if (args.length == 1) {
        if (posluziteljRadara.registrirajPosluzitelja() == true) {
          posluziteljRadara.pokreniPosluzitelja();
        }
        return;
      }
      // brisanje radara
      MrezneOperacije.posaljiZahtjevPosluzitelju(
          posluziteljRadara.podaciRadara.adresaRegistracije(),
          posluziteljRadara.podaciRadara.mreznaVrataRegistracije(),
          new StringBuilder().append("RADAR").append(" ").append(args[1]).append(" ")
              .append(args[2]).append("\n").toString());
    } catch (NeispravnaKonfiguracija | NumberFormatException | UnknownHostException e) {
      System.out.println(e.getMessage());
      return;
    }
  }

  /**
   * Pokreni mrezna vrata posluzitelja i obradi zahtjeve pomocu dretvi RadnikZaRadare
   */
  public void pokreniPosluzitelja() {
    boolean kraj = false;

    try (ServerSocket mreznaUticnicaPosluzitelja =
        new ServerSocket(this.podaciRadara.mreznaVrataRadara())) {

      while (!kraj) {
        var mreznaUticnica = mreznaUticnicaPosluzitelja.accept();
        var rr = new RadnikZaRadare(mreznaUticnica, podaciRadara, this);
        var dretva = this.tvornicaDretvi.newThread(rr);
        dretva.start();
      }
    } catch (NumberFormatException | IOException e) {
      e.printStackTrace();
    }
  }

  public void preuzmiPostavke(String[] args)
      throws NeispravnaKonfiguracija, NumberFormatException, UnknownHostException {
    Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(args[0]);
    this.podaciRadara = new PodaciRadara(Integer.valueOf(konfig.dajPostavku("id")),
        InetAddress.getLocalHost().getHostName(),
        Integer.valueOf(konfig.dajPostavku("mreznaVrataRadara")),
        Integer.valueOf(konfig.dajPostavku("maksBrzina")),
        Integer.valueOf(konfig.dajPostavku("maksTrajanje")),
        Integer.valueOf(konfig.dajPostavku("maksUdaljenost")),
        String.valueOf(konfig.dajPostavku("adresaRegistracije")),
        Integer.valueOf(konfig.dajPostavku("mreznaVrataRegistracije")),
        String.valueOf(konfig.dajPostavku("adresaKazne")),
        Integer.valueOf(konfig.dajPostavku("mreznaVrataKazne")),
        String.valueOf(konfig.dajPostavku("postanskaAdresaRadara")),
        Double.valueOf(konfig.dajPostavku("gpsSirina")),
        Double.valueOf(konfig.dajPostavku("gpsDuzina")));
  }

  /**
   * Registriraj posluzitelja slanjem naredbe na posluzitelj za registraciju radara.
   *
   * @return true, ako je uspjesno
   */
  public boolean registrirajPosluzitelja() {
    var naredba = new StringBuilder();

    naredba.append("RADAR").append(" ").append(this.podaciRadara.id()).append(" ")
        .append(this.podaciRadara.adresaRadara()).append(" ")
        .append(this.podaciRadara.mreznaVrataRadara()).append(" ")
        .append(this.podaciRadara.gpsSirina()).append(" ").append(this.podaciRadara.gpsDuzina())
        .append(" ").append(this.podaciRadara.maksUdaljenost());

    var odgovor = MrezneOperacije.posaljiZahtjevPosluzitelju(this.podaciRadara.adresaRegistracije(),
        this.podaciRadara.mreznaVrataRegistracije(), naredba.toString());
    // System.out.println(odgovor);
    if (odgovor.contains("OK")) {
      return true;
    }
    return false;
  }
}
