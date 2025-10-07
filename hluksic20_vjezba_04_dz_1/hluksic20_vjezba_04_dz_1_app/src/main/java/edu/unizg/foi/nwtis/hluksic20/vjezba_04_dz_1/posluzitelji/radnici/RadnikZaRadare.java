package edu.unizg.foi.nwtis.hluksic20.vjezba_04_dz_1.posluzitelji.radnici;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Pattern;
import edu.unizg.foi.nwtis.hluksic20.vjezba_04_dz_1.podaci.BrzoVozilo;
import edu.unizg.foi.nwtis.hluksic20.vjezba_04_dz_1.podaci.PodaciRadara;
import edu.unizg.foi.nwtis.hluksic20.vjezba_04_dz_1.pomocnici.MrezneOperacije;
import edu.unizg.foi.nwtis.hluksic20.vjezba_04_dz_1.posluzitelji.PosluziteljRadara;

/**
 * Klasa obraduje podatke o brzini vozila koja se nalaze u dometu radara i salje podatke za kazne
 * posluzitelju kazni.
 */
public class RadnikZaRadare implements Runnable {

  private Socket mreznaUticnica;
  private PodaciRadara podaciRadara;
  private PosluziteljRadara posluziteljRadara;
  private Pattern predlozakBrzine = Pattern.compile(
      "^VOZILO (?<id>\\d+) (?<vrijeme>\\d+) (?<brzina>-?\\d+(?:[.]\\d+)?) (?<gpsSirina>\\d+[.]\\d+) (?<gpsDuzina>\\d+[.]\\d+)$");

  public RadnikZaRadare(Socket mreznaUticnica, PodaciRadara podaciRadara,
      PosluziteljRadara posluziteljRadara) {
    super();
    this.mreznaUticnica = mreznaUticnica;
    this.podaciRadara = podaciRadara;
    this.posluziteljRadara = posluziteljRadara;
  }

  @Override
  public void run() {
    try {
      BufferedReader citac =
          new BufferedReader(new InputStreamReader(mreznaUticnica.getInputStream(), "utf8"));
      OutputStream out = mreznaUticnica.getOutputStream();
      PrintWriter pisac = new PrintWriter(new OutputStreamWriter(out, "utf8"), true);
      var redak = citac.readLine();

      mreznaUticnica.shutdownInput();
      pisac.println(obradaZahtjeva(redak));

      pisac.flush();
      mreznaUticnica.shutdownOutput();
      mreznaUticnica.close();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Obrada zahtjeva s podacima brzine i pozicije.
   *
   * @param zahtjev dobiveni zahtjev
   * @return OK, ERROR 30 za neispravnu sintaksu, ERROR 31 ako je posluzitelj kazni neaktivan, ERROR
   *         39 za nepoznatu gresku
   */
  public String obradaZahtjeva(String zahtjev) {
    if (zahtjev == null) {
      return "ERROR 30 Neispravna sintaksa komande.";
    }

    return obradaZahtjevaBrzine(zahtjev);
  }

  /**
   * Pomocu moguceg prethodnog i trenutnog zapisa o brzini vozila obradi zahtjev.
   *
   * @param zahtjev dobiveni zahtjev
   * @return status
   */
  private String obradaZahtjevaBrzine(String zahtjev) {
    var poklapanje = this.predlozakBrzine.matcher(zahtjev);

    if (!poklapanje.matches()) {
      return "ERROR 30 Neispravna sintaksa komande.";
    }

    BrzoVozilo vozilo = new BrzoVozilo(Integer.parseInt(poklapanje.group("id")), -1,
        Long.parseLong(poklapanje.group("vrijeme")), Double.parseDouble(poklapanje.group("brzina")),
        Double.parseDouble(poklapanje.group("gpsSirina")),
        Double.parseDouble(poklapanje.group("gpsDuzina")),
        this.podaciRadara.maksBrzina() < Double.parseDouble(poklapanje.group("brzina")) ? true
            : false);

    BrzoVozilo stariZapis = this.posluziteljRadara.pracenaVozila.get(vozilo.id());
    // System.out.println(zahtjev);
    try {
      return obradiDogadaj(vozilo, stariZapis);
    } catch (Exception e) {
      e.printStackTrace();
      return "ERROR 39 Nepoznata greska.";
    }
  }

  /**
   * Obradi moguce dogadaje vozila, u slucaju prekrsaja salji podatke posluzitelju kazni.
   *
   * @param vozilo trenutni podaci o vozilu
   * @param stariZapis prethodni podaci o vozilu (nepostojeci ili pocetak brze voznje)
   * @return status
   */
  private String obradiDogadaj(BrzoVozilo vozilo, BrzoVozilo stariZapis) {
    // Prvi zapis/pocetak/kraj brze voznje
    if (stariZapis == null || vozilo.status() != stariZapis.status()) {
      this.posluziteljRadara.pracenaVozila.put(vozilo.id(), vozilo);
      // System.out.println("Prvi zapis/pocetak/kraj: " + vozilo.brzina());
      return "OK";
    }

    // Kontinuirana brza voznja
    if (vozilo.status() && stariZapis.status()) {
      var trajanje = vozilo.vrijeme() - stariZapis.vrijeme();
      // System.out.println(trajanje / 1000.0);

      if (trajanje > this.podaciRadara.maksTrajanje() * 2000) {
        this.posluziteljRadara.pracenaVozila.put(vozilo.id(), vozilo.postaviStatus(false));
        // System.out.println("Neocekivano: " + vozilo.id());
        return "OK";
      }

      if (trajanje > this.podaciRadara.maksTrajanje() * 1000) {
        this.posluziteljRadara.pracenaVozila.put(vozilo.id(), vozilo.postaviStatus(false));
        // System.out.println("Kazna: " + vozilo.id());
        StringBuilder sb = new StringBuilder();
        sb.append("VOZILO ").append(vozilo.id()).append(" ").append(stariZapis.vrijeme())
            .append(" ").append(vozilo.vrijeme()).append(" ").append(vozilo.brzina()).append(" ")
            .append(vozilo.gpsSirina()).append(" ").append(vozilo.gpsDuzina()).append(" ")
            .append(this.podaciRadara.gpsSirina()).append(" ").append(this.podaciRadara.gpsDuzina())
            .append("\n");

        if (MrezneOperacije.posaljiZahtjevPosluzitelju(this.podaciRadara.adresaKazne(),
            this.podaciRadara.mreznaVrataKazne(), sb.toString()) == null) {
          return "ERROR 31 Poslužitelj kazni neaktivan.";
        }
      }
    }
    return "OK";
  }
}
