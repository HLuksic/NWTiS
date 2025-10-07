package edu.unizg.foi.nwtis.hluksic20.vjezba_04_dz_1.posluzitelji;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import edu.unizg.foi.nwtis.hluksic20.vjezba_04_dz_1.posluzitelji.radnici.RadnikZaVozila;

/**
 * Asinkroni visedretveni posluzitelj za primanje telemetrije simulatora vozila.
 */
public class PosluziteljZaVozila implements Runnable {

  private int mreznaVrata;
  public CentralniSustav centralniSustav;
  static Charset cs = Charset.forName("UTF-8");
  static ExecutorService executor;
  volatile AtomicInteger brojDretvi = new AtomicInteger(0);
  volatile List<Future<Integer>> odgovori = new ArrayList<Future<Integer>>();

  public PosluziteljZaVozila(int mreznaVrata, CentralniSustav centralniSustav) {
    super();
    this.mreznaVrata = mreznaVrata;
    this.centralniSustav = centralniSustav;
  }

  /**
   * Run metoda pomocu Future instancira serverski kanal
   */
  @Override
  public void run() {
    AsynchronousServerSocketChannel server = null;

    try {
      server = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(mreznaVrata));
    } catch (IOException e) {
      e.printStackTrace();
    }

    executor = Executors.newVirtualThreadPerTaskExecutor();

    try {
      while (true) {
        Future<AsynchronousSocketChannel> acceptChannel = server.accept();
        AsynchronousSocketChannel clientChannel = acceptChannel.get();
        odgovori.add(executor.submit(() -> cekajObradiKlijenta(clientChannel)));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      server.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Cekaj i obradi zahtjeve pomocu dretvi RadnikZaVozila.
   *
   * @param clientChannel kanal klijenta (SimulatorVozila)
   * @return broj aktivnih dretvi
   */
  Integer cekajObradiKlijenta(AsynchronousSocketChannel clientChannel) {
    int broj = brojDretvi.incrementAndGet();
    // System.out.println("Kreće: " + broj);
    try {
      var obrada = new RadnikZaVozila(clientChannel, this);
      var t = Thread.startVirtualThread(obrada);
      t.join();
    } catch (Exception e) {
      e.printStackTrace();
    }
    brojDretvi.decrementAndGet();
    return broj;
  }
}
