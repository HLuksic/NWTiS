package edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.podaci;

public class Radar {
  private int id;
  private String adresaRadara;
  private int mreznaVrataRadara;
  private int maksBrzina;
  private int maksTrajanje;
  private int maksUdaljenost;
  private String adresaRegistracije;
  private int mreznaVrataRegistracije;
  private String adresaKazne;
  private int mreznaVrataKazne;
  private String postanskaAdresaRadara;
  private double gpsSirina;
  private double gpsDuzina;

  public Radar() {}

  public Radar(int id, String adresaRadara, int mreznaVrataRadara, int maksBrzina, int maksTrajanje,
      int maksUdaljenost, String adresaRegistracije, int mreznaVrataRegistracije,
      String adresaKazne, int mreznaVrataKazne, String postanskaAdresaRadara, double gpsSirina,
      double gpsDuzina) {
    super();
    this.id = id;
    this.adresaRadara = adresaRadara;
    this.mreznaVrataRadara = mreznaVrataRadara;
    this.maksBrzina = maksBrzina;
    this.maksTrajanje = maksTrajanje;
    this.maksUdaljenost = maksUdaljenost;
    this.adresaRegistracije = adresaRegistracije;
    this.mreznaVrataRegistracije = mreznaVrataRegistracije;
    this.adresaKazne = adresaKazne;
    this.mreznaVrataKazne = mreznaVrataKazne;
    this.postanskaAdresaRadara = postanskaAdresaRadara;
    this.gpsSirina = gpsSirina;
    this.gpsDuzina = gpsDuzina;
  }

  public int getId() {
    return id;
  }

  public String getAdresaRadara() {
    return adresaRadara;
  }

  public int getMreznaVrataRadara() {
    return mreznaVrataRadara;
  }

  public int getMaksBrzina() {
    return maksBrzina;
  }

  public int getMaksTrajanje() {
    return maksTrajanje;
  }

  public int getMaksUdaljenost() {
    return maksUdaljenost;
  }

  public String getAdresaRegistracije() {
    return adresaRegistracije;
  }

  public int getMreznaVrataRegistracije() {
    return mreznaVrataRegistracije;
  }

  public String getAdresaKazne() {
    return adresaKazne;
  }

  public int getMreznaVrataKazne() {
    return mreznaVrataKazne;
  }

  public String getPostanskaAdresaRadara() {
    return postanskaAdresaRadara;
  }

  public double getGpsSirina() {
    return gpsSirina;
  }

  public double getGpsDuzina() {
    return gpsDuzina;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setAdresaRadara(String adresaRadara) {
    this.adresaRadara = adresaRadara;
  }

  public void setMreznaVrataRadara(int mreznaVrataRadara) {
    this.mreznaVrataRadara = mreznaVrataRadara;
  }

  public void setMaksBrzina(int maksBrzina) {
    this.maksBrzina = maksBrzina;
  }

  public void setMaksTrajanje(int maksTrajanje) {
    this.maksTrajanje = maksTrajanje;
  }

  public void setMaksUdaljenost(int maksUdaljenost) {
    this.maksUdaljenost = maksUdaljenost;
  }

  public void setAdresaRegistracije(String adresaRegistracije) {
    this.adresaRegistracije = adresaRegistracije;
  }

  public void setMreznaVrataRegistracije(int mreznaVrataRegistracije) {
    this.mreznaVrataRegistracije = mreznaVrataRegistracije;
  }

  public void setAdresaKazne(String adresaKazne) {
    this.adresaKazne = adresaKazne;
  }

  public void setMreznaVrataKazne(int mreznaVrataKazne) {
    this.mreznaVrataKazne = mreznaVrataKazne;
  }

  public void setPostanskaAdresaRadara(String postanskaAdresaRadara) {
    this.postanskaAdresaRadara = postanskaAdresaRadara;
  }

  public void setGpsSirina(double gpsSirina) {
    this.gpsSirina = gpsSirina;
  }

  public void setGpsDuzina(double gpsDuzina) {
    this.gpsDuzina = gpsDuzina;
  }
}
