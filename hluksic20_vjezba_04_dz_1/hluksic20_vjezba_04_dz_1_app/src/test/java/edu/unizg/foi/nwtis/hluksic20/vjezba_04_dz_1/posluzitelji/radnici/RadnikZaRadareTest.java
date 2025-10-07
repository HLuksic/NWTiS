package edu.unizg.foi.nwtis.hluksic20.vjezba_04_dz_1.posluzitelji.radnici;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import edu.unizg.foi.nwtis.hluksic20.vjezba_04_dz_1.podaci.PodaciRadara;
import edu.unizg.foi.nwtis.hluksic20.vjezba_04_dz_1.posluzitelji.PosluziteljRadara;

class RadnikZaRadareTest {
  private RadnikZaRadare radnikZaRadare;
  private static PodaciRadara podaciRadara;
  private static PosluziteljRadara posluziteljRadara;

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
    podaciRadara = new PodaciRadara(0, null, 0, 0, 0, 0, null, 0, null, 0, null, 0, 0);
    posluziteljRadara = new PosluziteljRadara();
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {}

  @BeforeEach
  void setUp() throws Exception {
    radnikZaRadare = new RadnikZaRadare(null, podaciRadara, posluziteljRadara);
  }

  @AfterEach
  void tearDown() throws Exception {
    radnikZaRadare = null;
  }

  @Test
  void testObradaZahtjeva_Null() {
    RadnikZaRadare radnik = new RadnikZaRadare(null, null, null);
    String result = radnik.obradaZahtjeva(null);
    assertEquals("ERROR 30 Neispravna sintaksa komande.", result);
  }

  @Test
  void testObradaZahtjeva_Krivo() {
    RadnikZaRadare radnik = new RadnikZaRadare(null, null, null);
    String result = radnik.obradaZahtjeva("krivo");
    assertEquals("ERROR 30 Neispravna sintaksa komande.", result);
  }
}
