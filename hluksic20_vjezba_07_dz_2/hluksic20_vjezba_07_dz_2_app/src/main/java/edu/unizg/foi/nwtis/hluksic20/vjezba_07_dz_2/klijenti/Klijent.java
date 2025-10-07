package edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.klijenti;

import java.net.UnknownHostException;
import edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.pomocnici.MrezneOperacije;
import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.konfiguracije.NeispravnaKonfiguracija;

/**
 * Klasa Klijent za dobivanje podataka o kaznama
 */
public class Klijent {

  private String adresaKazne;
  private int mreznaVrataKazne;

  public static void main(String[] args) throws Exception {
    if (args.length != 3 && args.length != 4) {
      System.out.println("Broj argumenata nije 3 ili 4.");
      return;
    }
    if (!isInt(args[1]) || !isInt(args[2]) || (args[3] != null && !isInt(args[3]))) {
      System.out.println("Nevaljani argumenti!");
      return;
    }
    Klijent k = new Klijent();
    k.preuzmiPostavke(args);
    k.posaljiUpit(args);
  }

  private static boolean isInt(String str) {
    try {
      Integer.parseInt(str);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  /**
   * Posalji upit posluzitelju kazni.
   *
   * @param args naredba naredbenog retka
   */
  private void posaljiUpit(String[] args) {
    StringBuilder sb = new StringBuilder();

    if (args.length == 4) {
      // VOZILO id vrijemeOd vrijemeDo
      sb.append("VOZILO ").append(args[1]).append(" ").append(args[2]).append(" ").append(args[3])
          .append("\n");
    } else {
      // STATISTIKA vrijemeOd vrijemeDo
      sb.append("STATISTIKA ").append(args[1]).append(" ").append(args[2]).append("\n");
    }

    MrezneOperacije.posaljiZahtjevPosluzitelju(this.adresaKazne, this.mreznaVrataKazne,
        sb.toString());
  }

  public void preuzmiPostavke(String[] args)
      throws NeispravnaKonfiguracija, NumberFormatException, UnknownHostException {
    Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(args[0]);

    this.adresaKazne = konfig.dajPostavku("adresaKazne");
    this.mreznaVrataKazne = Integer.valueOf(konfig.dajPostavku("mreznaVrataKazne"));
  }
}
