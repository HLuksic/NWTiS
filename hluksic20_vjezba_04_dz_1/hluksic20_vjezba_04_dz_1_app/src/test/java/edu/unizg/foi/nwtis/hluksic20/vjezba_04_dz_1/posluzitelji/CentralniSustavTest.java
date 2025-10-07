package edu.unizg.foi.nwtis.hluksic20.vjezba_04_dz_1.posluzitelji;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.konfiguracije.NeispravnaKonfiguracija;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CentralniSustavTest {
  private CentralniSustav centralniSustav;

  @BeforeAll
  static void setUpBeforeClass() throws Exception {}

  @AfterAll
  static void tearDownAfterClass() throws Exception {}

  @BeforeEach
  void setUp() throws Exception {
    centralniSustav = new CentralniSustav();
  }

  @AfterEach
  void tearDown() throws Exception {
    centralniSustav = null;
  }

  @Test
  @Order(3)
  void testMain() {
    var status = false;
    var mreznaVrataRadara = 8002;
    var mreznaVrataVozila = 8003;
    this.centralniSustav.mreznaVrataRadara = mreznaVrataRadara;
    this.centralniSustav.mreznaVrataVozila = mreznaVrataVozila;
    try {
      InetSocketAddress isa =
          new InetSocketAddress("localhost", this.centralniSustav.mreznaVrataRadara);
      Socket s = new Socket();
      s.connect(isa, 70);
      s.close();
    } catch (Exception e) {
      status = true;
    }
    assertTrue(status);
  }

  @Test
  @Order(2)
  void testPokreniPosluzitelje() throws IOException {
    var serverSocket = new ServerSocket(0);
    Thread serverThread = new Thread(() -> {
      try {
        centralniSustav.pokreniPosluzitelje();
      } catch (Exception e) {
        fail("Exception thrown: " + e.getMessage());
      }
    });
    serverThread.start();

    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    assertTrue(serverSocket.isBound());
    serverSocket.close();
  }

  @Test
  @Order(1)
  void testPreuzmiPostavke()
      throws NeispravnaKonfiguracija, NumberFormatException, UnknownHostException {
    var nazivDatoteke = "CentralniSustav.txt";
    try {
      Konfiguracija k = KonfiguracijaApstraktna.preuzmiKreirajKonfiguraciju(nazivDatoteke);
      k.spremiPostavku("mreznaVrataRadara", "8002");
      k.spremiPostavku("mreznaVrataVozila", "8002");
      k.spremiPostavku("mreznaVrataNadzora", "8002");
      k.spremiPostavku("maksVozila", "8002");
      k.spremiKonfiguraciju();
      String[] argumenti = {nazivDatoteke};
      this.centralniSustav.preuzmiPostavke(argumenti);
      assertEquals(Integer.valueOf(k.dajPostavku("mreznaVrataRadara")).intValue(),
          this.centralniSustav.mreznaVrataRadara);
      assertEquals(Integer.valueOf(k.dajPostavku("mreznaVrataVozila")).intValue(),
          this.centralniSustav.mreznaVrataRadara);
      assertEquals(Integer.valueOf(k.dajPostavku("mreznaVrataNadzora")).intValue(),
          this.centralniSustav.mreznaVrataRadara);
      assertEquals(Integer.valueOf(k.dajPostavku("maksVozila")).intValue(),
          this.centralniSustav.mreznaVrataRadara);
    } finally {
      this.obrisiDatoteku(nazivDatoteke);
    }
  }

  private boolean obrisiDatoteku(String nazivDatoteke) {
    File f = new File(nazivDatoteke);

    if (!f.exists()) {
      return true;
    } else if (f.exists() && f.isFile()) {
      f.delete();
      if (!f.exists()) {
        return true;
      }
    }
    return false;
  }

}
