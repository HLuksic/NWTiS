package edu.unizg.foi.nwtis.hluksic20.vjezba_04_dz_1.klijenti;

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
import java.util.concurrent.Future;
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
    Future<Void> result = clientChannel.connect(serverAddr);
    result.get();

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
    String linija = null;
    String prethodnaLinija = null;
    int redak = 0;

    while ((linija = bf.readLine()) != null) {
      if (redak == 0) {
        redak++;
        continue;
      }

      if (prethodnaLinija != null) {
        String[] podaciPrethodna = prethodnaLinija.split(",");
        String[] podaci = linija.split(",");
        long razlika = Long.parseLong(podaci[0]) - Long.parseLong(podaciPrethodna[0]);
        Thread.sleep(razlika * (this.trajanjeSek / 1000));
      }

      // System.out.println(redak);
      prethodnaLinija = linija;
      linija = linija.replace(",", " ");
      StringBuilder sb = new StringBuilder().append("VOZILO").append(" ").append(this.id)
          .append(" ").append(redak).append(" ");
      sb.append(linija).append("\n");
      // System.out.println("naredba: " + sb.toString());
      ByteBuffer buffer = ByteBuffer.wrap(sb.toString().getBytes());
      Future<Integer> writeBuff = clientChannel.write(buffer);
      writeBuff.get();
      buffer.flip();
      Future<Integer> readval = clientChannel.read(buffer);
      // System.out.println("Odgovor od PosluziteljVozila: " + new String(buffer.array()).trim());
      readval.get();
      buffer.clear();
      Thread.sleep(this.trajanjePauze);
      redak++;
    }
    bf.close();
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
