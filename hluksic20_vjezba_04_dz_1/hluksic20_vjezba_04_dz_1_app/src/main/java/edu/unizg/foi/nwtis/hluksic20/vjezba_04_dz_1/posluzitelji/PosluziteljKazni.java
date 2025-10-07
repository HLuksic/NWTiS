package edu.unizg.foi.nwtis.hluksic20.vjezba_04_dz_1.posluzitelji;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import edu.unizg.foi.nwtis.hluksic20.vjezba_04_dz_1.podaci.PodaciKazne;
import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.konfiguracije.NeispravnaKonfiguracija;

/**
 * Sinkroni jednodretveni server koji prima podatke o prekrsajima brzine, sprema kazne i vraca
 * statistiku.
 */
public class PosluziteljKazni {

  private static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
  int mreznaVrata;
  private Pattern predlozakKazna = Pattern.compile(
      "^VOZILO (?<id>\\d+) (?<vrijemePocetak>\\d+) (?<vrijemeKraj>\\d+) (?<brzina>-?\\d+(?:[.]\\d+)?) (?<gpsSirina>\\d+[.]\\d+) (?<gpsDuzina>\\d+[.]\\d+) (?<gpsSirinaRadar>\\d+[.]\\d+) (?<gpsDuzinaRadar>\\d+[.]\\d+)$");
  private Pattern predlozakVozilo =
      Pattern.compile("^VOZILO (?<id>\\d+) (?<vrijemeOd>\\d+) (?<vrijemeDo>\\d+)$");
  private Pattern predlozakStatistika =
      Pattern.compile("^STATISTIKA (?<vrijemeOd>\\d+) (?<vrijemeDo>\\d+)$");
  private Matcher poklapanjeKazna;
  private Matcher poklapanjeVozilo;
  private Matcher poklapanjeStatistika;
  private volatile Queue<PodaciKazne> sveKazne = new ConcurrentLinkedQueue<>();

  public static void main(String[] args) {
    if (args.length != 1) {
      System.out.println("Broj argumenata nije 1.");
      return;
    }

    PosluziteljKazni posluziteljKazni = new PosluziteljKazni();

    try {
      posluziteljKazni.preuzmiPostavke(args);
      posluziteljKazni.pokreniPosluzitelja();
    } catch (NeispravnaKonfiguracija | NumberFormatException | UnknownHostException e) {
      System.out.println(e.getMessage());
      return;
    }
  }

  /**
   * Pokreni sinkroni server.
   */
  public void pokreniPosluzitelja() {
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
   * Obradi zahtjeve za upis kanzne i statistiku.
   *
   * @param zahtjev dobiveni zahtjev
   * @return odgovor na zahtjev
   */
  public String obradaZahtjeva(String zahtjev) {
    if (zahtjev == null)
      return "ERROR 40 Neispravna sintaksa komande.";

    String odgovor = null;

    this.poklapanjeKazna = this.predlozakKazna.matcher(zahtjev);
    this.poklapanjeVozilo = this.predlozakVozilo.matcher(zahtjev);
    this.poklapanjeStatistika = this.predlozakStatistika.matcher(zahtjev);
    // System.out.println(zahtjev);
    try {
      if (this.predlozakKazna.matcher(zahtjev).matches()) {
        odgovor = obradaZahtjevaKazna(zahtjev);
      } else if (this.predlozakVozilo.matcher(zahtjev).matches()) {
        odgovor = obradaZahtjevaVozilo(zahtjev);
      } else if (this.predlozakStatistika.matcher(zahtjev).matches()) {
        odgovor = obradaZahtjevaStatistika(zahtjev);
      } else {
        odgovor = "ERROR 40 Neispravna sintaksa komande.";
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      return "ERROR 49 Nepoznata greška";
    }
    return odgovor;
  }

  /**
   * Obrada zahtjeva za ukupnu statistiku.
   *
   * @param zahtjev dobiveni zahtjev
   * @return vraca ukupan broj kazni za svaki ID vozila u vremenskom rasponu
   */
  private String obradaZahtjevaStatistika(String zahtjev) {
    this.poklapanjeStatistika.matches();

    var vrijemeOd = Long.valueOf(this.poklapanjeStatistika.group("vrijemeOd"));
    var vrijemeDo = Long.valueOf(this.poklapanjeStatistika.group("vrijemeDo"));

    var kazne = this.sveKazne.stream()
        .filter(k -> k.vrijemePocetak() >= vrijemeOd && k.vrijemeKraj() <= vrijemeDo)
        .toArray(PodaciKazne[]::new);

    if (kazne.length == 0) {
      return "ERROR 41 Nema kazni u ovom vremenu.";
    }

    var ids = this.sveKazne.stream().map(k -> k.id()).distinct().toArray(Integer[]::new);
    var odgovor = new StringBuilder("OK ");

    for (int id : ids) {
      var ukupnoKazni =
          this.sveKazne.stream().filter(k -> k.id() == id).toArray(PodaciKazne[]::new).length;
      odgovor.append(id).append(" ").append(ukupnoKazni).append("; ");
    }

    System.out.println(odgovor.toString());
    return odgovor.toString();
  }

  /**
   * Obrada zahtjeva za prikaz kazni za određeni ID vozila.
   *
   * @param zahtjev dobiveni zahtjev
   * @return vraca najkasniju kaznu za odredeni ID vozila u vremenskom rasponu
   */
  private String obradaZahtjevaVozilo(String zahtjev) {
    this.poklapanjeVozilo.matches();

    var vrijemeOd = Long.valueOf(this.poklapanjeVozilo.group("vrijemeOd"));
    var vrijemeDo = Long.valueOf(this.poklapanjeVozilo.group("vrijemeDo"));

    var zadnjaKazna = this.sveKazne.stream()
        .filter(k -> k.id() == Integer.valueOf(this.poklapanjeVozilo.group("id"))
            && k.vrijemePocetak() >= vrijemeOd && k.vrijemeKraj() <= vrijemeDo)
        .reduce((a, b) -> b).orElse(null);

    if (zadnjaKazna == null) {
      return "ERROR 41 Nema kazni u ovom vremenu.";
    }

    StringBuilder sb = new StringBuilder();
    sb.append("OK ").append(sdf.format(zadnjaKazna.vrijemeKraj())).append(" ")
        .append(zadnjaKazna.brzina()).append(" ").append(zadnjaKazna.gpsSirinaRadar()).append(" ")
        .append(zadnjaKazna.gpsDuzinaRadar());

    System.out.println(sb.toString());
    return sb.toString();
  }

  /**
   * Obrada zahtjeva za upisom kazne
   *
   * @param zahtjev dobiveni zahtjev
   * @return OK i ispis podataka o kazni
   */
  public String obradaZahtjevaKazna(String zahtjev) {
    this.poklapanjeKazna.matches();

    var kazna = new PodaciKazne(Integer.valueOf(this.poklapanjeKazna.group("id")),
        Long.valueOf(this.poklapanjeKazna.group("vrijemePocetak")),
        Long.valueOf(this.poklapanjeKazna.group("vrijemeKraj")),
        Double.valueOf(this.poklapanjeKazna.group("brzina")),
        Double.valueOf(this.poklapanjeKazna.group("gpsSirina")),
        Double.valueOf(this.poklapanjeKazna.group("gpsDuzina")),
        Double.valueOf(this.poklapanjeKazna.group("gpsSirinaRadar")),
        Double.valueOf(this.poklapanjeKazna.group("gpsDuzinaRadar")));

    this.sveKazne.add(kazna);

    System.out.println("Id: " + kazna.id() + " Vrijeme od: " + sdf.format(kazna.vrijemePocetak())
        + "  Vrijeme do: " + sdf.format(kazna.vrijemeKraj()) + " Brzina: " + kazna.brzina()
        + " GPS: " + kazna.gpsSirina() + ", " + kazna.gpsDuzina());

    return "OK";
  }

  public void preuzmiPostavke(String[] args)
      throws NeispravnaKonfiguracija, NumberFormatException, UnknownHostException {
    Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(args[0]);

    this.mreznaVrata = Integer.valueOf(konfig.dajPostavku("mreznaVrataKazne"));
  }
}
