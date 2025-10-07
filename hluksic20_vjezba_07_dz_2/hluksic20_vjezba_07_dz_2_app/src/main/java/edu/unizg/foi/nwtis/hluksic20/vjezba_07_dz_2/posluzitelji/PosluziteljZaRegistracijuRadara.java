package edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.posluzitelji;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.podaci.PodaciRadara;
import edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.pomocnici.MrezneOperacije;

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
  private Pattern predlozakDajSve = Pattern.compile("^RADAR SVI$");
  private Pattern predlozakReset = Pattern.compile("^RADAR RESET$");
  private Pattern predlozakProvjeriId = Pattern.compile("^RADAR (?<id>\\d+)$");
  private Matcher poklapanjeRegistracijaRadara;
  private Matcher poklapanjeBrisanjaJednog;
  private Matcher poklapanjeProvjeriId;
  private Matcher poklapanjeReset;

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
    this.poklapanjeProvjeriId = this.predlozakProvjeriId.matcher(zahtjev);
    this.poklapanjeReset = this.predlozakReset.matcher(zahtjev);
    String odgovor = null;

    try {
      if (this.predlozakRegistracijaRadara.matcher(zahtjev).matches()) {
        odgovor = obradaZahtjevaRegistracijeRadara(zahtjev);
      } else if (this.predlozakBrisanjaJednog.matcher(zahtjev).matches()) {
        odgovor = obradaZahtjevaObrisiJednog(zahtjev);
      } else if (this.predlozakBrisanjaSvih.matcher(zahtjev).matches()) {
        odgovor = obradaZahtjevaObrisiSve();
      } else if (this.predlozakDajSve.matcher(zahtjev).matches()) {
        odgovor = obradaZahtjevaDajSve();
      } else if (this.predlozakProvjeriId.matcher(zahtjev).matches()) {
        odgovor = obradaZahtjevaProvjeriId(zahtjev);
      } else if (this.predlozakReset.matcher(zahtjev).matches()) {
        odgovor = obradaZahtjevaReset();
      } else {
        odgovor = "ERROR 10 Neispravna sintaksa komande.";
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      return "ERROR 19 Nepoznata greška";
    }

    return odgovor;
  }
  
  private String obradaZahtjevaReset() {
    int m = 0;
    
    for (var r : this.centralniSustav.sviRadari.values()) {
      var odgovor = MrezneOperacije.posaljiZahtjevPosluzitelju(r.adresaRadara(), r.mreznaVrataRadara(), "RADAR " + r.id());
      if (odgovor == null) {
        m++;
        this.centralniSustav.sviRadari.remove(r.id());
      }
    }
    
    return "OK " + this.centralniSustav.sviRadari.size() + " " + m;
  }

  private String obradaZahtjevaProvjeriId(String zahtjev) {
    this.poklapanjeProvjeriId.matches();
    var id = Integer.valueOf(this.poklapanjeProvjeriId.group("id"));

    if (!this.centralniSustav.sviRadari.containsKey(id))
      return "ERROR 12 Traženi radar ne postoji!";

    return "OK";
  }

  private String obradaZahtjevaDajSve() {

    var odgovor = "OK {";
    
    for (var radar : this.centralniSustav.sviRadari.values()) {
      odgovor += "[" + radar.id() + " " + radar.adresaRadara() + " " + radar.mreznaVrataRadara() + " " + radar.gpsSirina() + " " + radar.gpsDuzina() + " " + radar.maksBrzina() + "], ";
    }
    odgovor += "}";
    //System.out.println(odgovor);
    return odgovor;
  }

  /**
   * Obrada zahtjeva za brisanje svih radara.
   *
   * @param zahtjev dobiveni zahtjev
   * @return OK
   */
  private String obradaZahtjevaObrisiSve() {
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
