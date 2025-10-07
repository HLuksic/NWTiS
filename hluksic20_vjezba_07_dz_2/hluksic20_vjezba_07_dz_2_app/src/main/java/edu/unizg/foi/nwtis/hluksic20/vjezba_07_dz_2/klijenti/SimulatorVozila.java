package edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.klijenti;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.konfiguracije.NeispravnaKonfiguracija;

/**
 * Klasa SimulatorVozila cita telemetriju vozila i salje ju posluzitelju za vozila.
 */
public class SimulatorVozila {

  static Charset cs = Charset.forName("UTF-8");
  private String adresaVozila;
  private int mreznaVrataVozila;
  private int trajanjeSek;
  private int trajanjePauze;
  private int id;

  public static void main(String[] args) throws Exception {
    if (args.length != 3) {
      System.out.println("Broj argumenata nije 3.");
      return;
    }
    SimulatorVozila s = new SimulatorVozila();
    s.id = Integer.parseInt(args[2]);
    s.preuzmiPostavke(args);
    s.pokreniKlijenta(args);
  }

  /**
   * Pokreni asinkronog klijenta pomocu Future.
   *
   * @param args argumenti naredbenog retka
   * @throws Exception za neuspjelu konekciju
   */
  private void pokreniKlijenta(String[] args) throws Exception {
    AsynchronousSocketChannel clientChannel = AsynchronousSocketChannel.open();
    SocketAddress serverAddr = new InetSocketAddress(adresaVozila, mreznaVrataVozila);
    clientChannel.connect(serverAddr).get();
    citajPodatke(args, clientChannel);
  }

  /**
   * Metoda koja cita podatke iz datoteke i salje ih asinkrono posluzitelju preko kanala.
   *
   * @param args naredbe naredbenog retka
   * @param clientChannel mrezni kanal ovog objekta
   * @throws IOException greska povezanosti
   * @throws InterruptedException greska povezanosti
   * @throws ExecutionException greska povezanosti
   */
  private void citajPodatke(String[] args, AsynchronousSocketChannel clientChannel)
      throws IOException, InterruptedException, ExecutionException {

    var bf = Files.newBufferedReader(Path.of(args[1]), cs);
    String linija = bf.readLine();
    long prethodnoVrijeme = 0;
    int redak = 1;

    while ((linija = bf.readLine()) != null) {
      long trenutnoVrijeme = Long.parseLong(linija.split(",")[0]);

      if (prethodnoVrijeme != 0)
        Thread.sleep((trenutnoVrijeme - prethodnoVrijeme) * (this.trajanjeSek / 1000));
      // System.out.println(redak);

      prethodnoVrijeme = trenutnoVrijeme;
      linija = linija.replace(",", " ");

      StringBuilder sb = new StringBuilder().append("VOZILO").append(" ").append(this.id)
          .append(" ").append(redak).append(" ").append(linija).append("\n");

      // System.out.println("s: " + sb.toString());

      ByteBuffer buffer = ByteBuffer.wrap(sb.toString().getBytes());
      clientChannel.write(buffer).get();
      buffer.clear();
      Thread.sleep(this.trajanjePauze);
      redak++;
    }
    bf.close();
    clientChannel.close();
  }

  public void preuzmiPostavke(String[] args)
      throws NeispravnaKonfiguracija, NumberFormatException, UnknownHostException {
    Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(args[0]);

    this.adresaVozila = konfig.dajPostavku("adresaVozila");
    this.mreznaVrataVozila = Integer.valueOf(konfig.dajPostavku("mreznaVrataVozila"));
    this.trajanjeSek = Integer.valueOf(konfig.dajPostavku("trajanjeSek"));
    this.trajanjePauze = Integer.valueOf(konfig.dajPostavku("trajanjePauze"));
  }
}
