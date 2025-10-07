package edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.posluzitelji.radnici;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.klijenti.RestKlijentVozila;
import edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.podaci.PodaciRadara;
import edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.podaci.RedPodaciVozila;
import edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.podaci.Voznja;
import edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.pomocnici.GpsUdaljenostBrzina;
import edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.pomocnici.MrezneOperacije;
import edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.posluzitelji.PosluziteljZaVozila;

/**
 * Klasa obraduje podatke o vozilima i salje ih odgovarajucem radaru ako je vozilo u njegovom
 * dometu.
 */
public class RadnikZaVozila implements Runnable {

  AsynchronousSocketChannel clientChannel;
  private PosluziteljZaVozila posluziteljZaVozila;
  private Pattern predlozakVozila = Pattern.compile(
      "^VOZILO (?<id>\\d+) (?<broj>\\d+) (?<vrijeme>\\d+) (?<brzina>-?\\d+(?:[.]\\d+)?) (?<snaga>-?\\d+(?:[.]\\d+)?) (?<struja>-?\\d+(?:[.]\\d+)?) (?<visina>-?\\d+(?:[.]\\d+)?) (?<gpsBrzina>-?\\d+(?:[.]\\d+)?) (?<tempVozila>-?\\d+) (?<postotakBaterija>\\d+) (?<naponBaterija>\\d+(?:[.]\\d+)?) (?<kapacitetBaterija>\\d+) (?<tempBaterija>-?\\d+) (?<preostaloKm>\\d+(?:[.]\\d+)?) (?<ukupnoKm>\\d+(?:[.]\\d+)?) (?<gpsSirina>\\d+[.]\\d+) (?<gpsDuzina>\\d+[.]\\d+)$");
  private Pattern predlozakStart = Pattern.compile("^VOZILO START (?<id>\\d+)$");
  private Pattern predlozakStop = Pattern.compile("^VOZILO STOP (?<id>\\d+)$");
  private Matcher poklapanjeVozila;
  private Matcher poklapanjeStart;
  private Matcher poklapanjeStop;

  public RadnikZaVozila(AsynchronousSocketChannel clientChannel,
      PosluziteljZaVozila posluziteljZaVozila) {
    super();
    this.clientChannel = clientChannel;
    this.posluziteljZaVozila = posluziteljZaVozila;
  }

  /**
   * Run metoda prima asinkroni Future zahtjev kao i kod PosluziteljZaVozila.
   */
  @Override
  public void run() {
    try {
      while (true) {
        if (clientChannel != null && clientChannel.isOpen()) {
          var buffer = ByteBuffer.allocate(2048);
          Future<Integer> readBuff = clientChannel.read(buffer);
          readBuff.get();
          var naredba = new String(buffer.array()).trim();

          String odgovor = obradaZahtjeva(naredba);
          Future<Integer> writeBuff = clientChannel.write(ByteBuffer.wrap(odgovor.getBytes()));

          try {
            writeBuff.get();
          } catch (ExecutionException e) {
            if (e.getCause() instanceof IOException) {
              break;
            } else {
              throw e;
            }
          }

          buffer.clear();
        } else {
          break;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        this.clientChannel.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Obrada zahtjeva.
   *
   * @param zahtjev dobiveni zahtjev
   * @return OK, ERROR 20 za neispravnu sintaksu, ERROR 29 za nepoznatu gresku
   */
  public String obradaZahtjeva(String zahtjev) {
    if (zahtjev == null)
      return "ERROR 20 Neispravna sintaksa komande.";

    String[] zahtjevi = new String[] {zahtjev};
    String odgovor = null;

    zahtjevi = zahtjev.split("\n");

    for (var z : zahtjevi) {
      // System.out.println("rr: " + z);
      try {
        if (this.predlozakVozila.matcher(z).matches())
          odgovor = obradaZahtjevaVozila(z);
        else if (this.predlozakStart.matcher(z).matches())
          odgovor = obradaZahtjevaStart(z);
        else if (this.predlozakStop.matcher(z).matches())
          odgovor = obradaZahtjevaStop(z);
      } catch (Exception e) {
        e.printStackTrace();
        return "ERROR 29 Nepoznata greska.";
      }
    }

    return odgovor;
  }

  private String obradaZahtjevaStart(String zahtjev) {
    this.poklapanjeStart = this.predlozakStart.matcher(zahtjev);

    if (!poklapanjeStart.matches())
      return "ERROR 20 Neispravna sintaksa komande.";

    int id = Integer.parseInt(poklapanjeStart.group("id"));
    this.posluziteljZaVozila.centralniSustav.svaVozila.putIfAbsent(id, new RedPodaciVozila(id));
    return "OK";
  }

  private String obradaZahtjevaStop(String zahtjev) {
    this.poklapanjeStop = this.predlozakStop.matcher(zahtjev);

    if (!poklapanjeStop.matches())
      return "ERROR 20 Neispravna sintaksa komande.";

    int id = Integer.parseInt(poklapanjeStop.group("id"));
    this.posluziteljZaVozila.centralniSustav.svaVozila.remove(id);
    return "OK";
  }

  /**
   * Obrada zahtjeva s podacima vozila.
   *
   * @param zahtjev dobiveni zahtjev
   * @return status
   */
  private String obradaZahtjevaVozila(String zahtjev) {
    this.poklapanjeVozila = this.predlozakVozila.matcher(zahtjev);

    if (!poklapanjeVozila.matches()) {
      return "ERROR 20 Neispravna sintaksa komande.";
    }

    // provjerava je li u kolekciji, ako da, salji POST na servis za pracenje, ako uspjesno ili
    // nije u kolekciji, provjeri je li u dosegu radara, ako da, salji PosluziteljuRadara
    if (this.posluziteljZaVozila.centralniSustav.svaVozila
        .containsKey(Integer.parseInt(poklapanjeVozila.group("id")))) {
      Voznja v = new Voznja(Integer.parseInt(poklapanjeVozila.group("id")),
          Integer.parseInt(poklapanjeVozila.group("broj")),
          Long.parseLong(poklapanjeVozila.group("vrijeme")),
          Double.parseDouble(poklapanjeVozila.group("brzina")),
          Double.parseDouble(poklapanjeVozila.group("snaga")),
          Double.parseDouble(poklapanjeVozila.group("struja")),
          Double.parseDouble(poklapanjeVozila.group("visina")),
          Double.parseDouble(poklapanjeVozila.group("gpsBrzina")),
          Integer.parseInt(poklapanjeVozila.group("tempVozila")),
          Integer.parseInt(poklapanjeVozila.group("postotakBaterija")),
          Double.parseDouble(poklapanjeVozila.group("naponBaterija")),
          Integer.parseInt(poklapanjeVozila.group("kapacitetBaterija")),
          Integer.parseInt(poklapanjeVozila.group("tempBaterija")),
          Double.parseDouble(poklapanjeVozila.group("preostaloKm")),
          Double.parseDouble(poklapanjeVozila.group("ukupnoKm")),
          Double.parseDouble(poklapanjeVozila.group("gpsSirina")),
          Double.parseDouble(poklapanjeVozila.group("gpsDuzina")));

      if (!new RestKlijentVozila().postVoznjaJSON(v))
        return "ERROR 21 POST operacija neuspješna.";
    }

    // provjeri je li u dosegu radara
    var radar = jeUnutarDosega(Double.parseDouble(poklapanjeVozila.group("gpsSirina")),
        Double.parseDouble(poklapanjeVozila.group("gpsDuzina")));

    if (radar == null) {
      return "OK";
    }

    StringBuilder sb = new StringBuilder();
    sb.append("VOZILO ").append(poklapanjeVozila.group("id")).append(" ")
        .append(poklapanjeVozila.group("vrijeme")).append(" ")
        .append(poklapanjeVozila.group("brzina")).append(" ")
        .append(poklapanjeVozila.group("gpsSirina")).append(" ")
        .append(poklapanjeVozila.group("gpsDuzina")).append("\n");

    return MrezneOperacije.posaljiZahtjevPosluzitelju(radar.adresaRadara(),
        radar.mreznaVrataRadara(), sb.toString());
  }

  /**
   * Provjerava nalazi li se vozilo u dometu radara.
   *
   * @param gpsSirinaVozilo GPS sirina vozila
   * @param gpsDuzinaVozilo GPS duzina vozila
   * @return podaci radara ukoliko je u dometu, inace null
   */
  private PodaciRadara jeUnutarDosega(double gpsSirinaVozilo, double gpsDuzinaVozilo) {
    for (var radar : this.posluziteljZaVozila.centralniSustav.sviRadari.entrySet()) {
      var gpsSirinaRadar = radar.getValue().gpsSirina();
      var gpsDuzinaRadar = radar.getValue().gpsDuzina();
      var domet = radar.getValue().maksUdaljenost();

      if (GpsUdaljenostBrzina.udaljenostKm(gpsSirinaRadar, gpsDuzinaRadar, gpsSirinaVozilo,
          gpsDuzinaVozilo) * 1000 <= domet) {
        return radar.getValue();
      }
    }
    return null;
  }
}
