package edu.unizg.foi.nwtis.hluksic20.vjezba_04_dz_1.posluzitelji;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import edu.unizg.foi.nwtis.hluksic20.vjezba_04_dz_1.podaci.PodaciRadara;

/**
 * Jednodretveni sinkroni server za registraciju radara u Centralnom sustavu.
 */
public class PosluziteljZaRegistracijuRadara implements Runnable {

  private int mreznaVrata;
  private CentralniSustav centralniSustav;
  private Pattern predlozakRegistracijaRadara =
      Pattern.compile("^RADAR (?<id>\\d+) (?<adresa>.*) (?<mreznaVrata>\\d+) "
          + "(?<gpsSirina>\\d+[.]\\d+) (?<gpsDuzina>\\d+[.]\\d+) (?<maksUdaljenost>-?\\d*)$");
  private Pattern predlozakBrisanjaJednog = Pattern.compile("^RADAR OBRIŠI (?<id>\\d+)$");
  private Pattern predlozakBrisanjaSvih = Pattern.compile("^RADAR OBRIŠI SVE$");
  private Matcher poklapanjeRegistracijaRadara;
  private Matcher poklapanjeBrisanjaJednog;

  public PosluziteljZaRegistracijuRadara(int mreznaVrata, CentralniSustav centralniSustav) {
    super();
    this.mreznaVrata = mreznaVrata;
    this.centralniSustav = centralniSustav;
  }

  @Override
  public void run() {
    boolean kraj = false;

    try (ServerSocket mreznaUticnicaPosluzitelja = new ServerSocket(this.mreznaVrata)) {
      while (!kraj) {
        var mreznaUticnica = mreznaUticnicaPosluzitelja.accept();
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
      }
    } catch (NumberFormatException | IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Obrada zahtjeva za registraciju, brisanje jednog i brisanje svih radara.
   *
   * @param zahtjev dobiveni zahtjev
   * @return odgovor
   */
  public String obradaZahtjeva(String zahtjev) {
    if (zahtjev == null)
      return "ERROR 10 Neispravna sintaksa komande.";

    this.poklapanjeRegistracijaRadara = this.predlozakRegistracijaRadara.matcher(zahtjev);
    this.poklapanjeBrisanjaJednog = this.predlozakBrisanjaJednog.matcher(zahtjev);
    String odgovor = null;

    try {
      if (this.predlozakRegistracijaRadara.matcher(zahtjev).matches()) {
        odgovor = obradaZahtjevaRegistracijeRadara(zahtjev);
      } else if (this.predlozakBrisanjaJednog.matcher(zahtjev).matches()) {
        odgovor = obradaZahtjevaObrisiJednog(zahtjev);
      } else if (this.predlozakBrisanjaSvih.matcher(zahtjev).matches()) {
        odgovor = obradaZahtjevaObrisiSve(zahtjev);
      } else {
        odgovor = "ERROR 10 Neispravna sintaksa komande.";
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      return "ERROR 19 Nepoznata greška";
    }

    return odgovor;
  }

  /**
   * Obrada zahtjeva za brisanje svih radara.
   *
   * @param zahtjev dobiveni zahtjev
   * @return OK
   */
  private String obradaZahtjevaObrisiSve(String zahtjev) {
    this.centralniSustav.sviRadari.clear();
    return "OK";
  }

  /**
   * Obrada zahtjeva za brisanje radara po ID.
   *
   * @param zahtjev dobiveni zahtjev
   * @return OK
   */
  private String obradaZahtjevaObrisiJednog(String zahtjev) {
    this.poklapanjeBrisanjaJednog.matches();
    var id = Integer.valueOf(this.poklapanjeBrisanjaJednog.group("id"));

    if (this.centralniSustav.sviRadari.remove(id) == null)
      return "ERROR 12 Traženi radar ne postoji!";

    return "OK";
  }

  /**
   * Obrada zahtjeva registracije radara.
   *
   * @param zahtjev dobiveni zahtjev
   * @return OK ili ERROR ako radar postoji
   */
  private String obradaZahtjevaRegistracijeRadara(String zahtjev) {
    this.poklapanjeRegistracijaRadara.matches();
    var radar = new PodaciRadara(Integer.valueOf(this.poklapanjeRegistracijaRadara.group("id")),
        this.poklapanjeRegistracijaRadara.group("adresa"),
        Integer.valueOf(this.poklapanjeRegistracijaRadara.group("mreznaVrata")), -1, -1,
        Integer.valueOf(this.poklapanjeRegistracijaRadara.group("maksUdaljenost")), null, -1, null,
        -1, null, Double.valueOf(this.poklapanjeRegistracijaRadara.group("gpsSirina")),
        Double.valueOf(this.poklapanjeRegistracijaRadara.group("gpsDuzina")));

    if (this.centralniSustav.sviRadari.put(radar.id(), radar) != null)
      return "ERROR 11 Radar već postoji!";

    return "OK";
  }
}
