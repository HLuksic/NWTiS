package edu.unizg.foi.nwtis.hluksic20.vjezba_04_dz_1.posluzitelji.radnici;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import edu.unizg.foi.nwtis.hluksic20.vjezba_04_dz_1.podaci.PodaciRadara;
import edu.unizg.foi.nwtis.hluksic20.vjezba_04_dz_1.pomocnici.GpsUdaljenostBrzina;
import edu.unizg.foi.nwtis.hluksic20.vjezba_04_dz_1.pomocnici.MrezneOperacije;
import edu.unizg.foi.nwtis.hluksic20.vjezba_04_dz_1.posluzitelji.PosluziteljZaVozila;

/**
 * Klasa obraduje podatke o vozilima i salje ih odgovarajucem radaru ako je vozilo u njegovom
 * dometu.
 */
public class RadnikZaVozila implements Runnable {

  AsynchronousSocketChannel clientChannel;
  private PosluziteljZaVozila posluziteljZaVozila;
  private Pattern predlozakVozila = Pattern.compile(
      "^VOZILO (?<id>\\d+) (?<broj>\\d+) (?<vrijeme>\\d+) (?<brzina>-?\\d+(?:[.]\\d+)?) (?<snaga>-?\\d+(?:[.]\\d+)?) (?<struja>-?\\d+(?:[.]\\d+)?) (?<visina>-?\\d+(?:[.]\\d+)?) (?<gpsBrzina>-?\\d+(?:[.]\\d+)?) (?<tempVozila>-?\\d+) (?<postotakBaterija>\\d+) (?<naponBaterija>\\d+(?:[.]\\d+)?) (?<kapacitetBaterija>\\d+) (?<tempBaterija>-?\\d+) (?<preostaloKm>\\d+(?:[.]\\d+)?) (?<ukupnoKm>\\d+(?:[.]\\d+)?) (?<gpsSirina>\\d+[.]\\d+) (?<gpsDuzina>\\d+[.]\\d+)$");
  private Matcher poklapanjeVozila;

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

    try {
      return obradaZahtjevaVozila(zahtjev);
    } catch (Exception e) {
      e.printStackTrace();
      return "ERROR 29 Nepoznata greska.";
    }
  }

  /**
   * Obrada zahtjeva s podacima vozila.
   *
   * @param zahtjev dobiveni zahtjev
   * @return status
   */
  public String obradaZahtjevaVozila(String zahtjev) {
    this.poklapanjeVozila = this.predlozakVozila.matcher(zahtjev);
    // System.out.println(zahtjev);
    if (!poklapanjeVozila.matches()) {
      return "ERROR 20 Neispravna sintaksa komande.";
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

    // salji podatke posluzitelju radara
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
