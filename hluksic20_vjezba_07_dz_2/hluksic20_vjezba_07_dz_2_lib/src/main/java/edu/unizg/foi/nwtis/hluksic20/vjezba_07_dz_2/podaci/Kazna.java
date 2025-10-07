package edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.podaci;

public class Kazna {
  private int id;
  private long vrijemePocetak;
  private long vrijemeKraj;
  private double brzina;
  private double gpsSirina;
  private double gpsDuzina;
  private double gpsSirinaRadar;
  private double gpsDuzinaRadar;

  public Kazna() {}

  public Kazna(int id, long vrijemePocetak, long vrijemeKraj, double brzina, double gpsSirina,
      double gpsDuzina, double gpsSirinaRadar, double gpsDuzinaRadar) {
    super();
    this.id = id;
    this.vrijemePocetak = vrijemePocetak;
    this.vrijemeKraj = vrijemeKraj;
    this.brzina = brzina;
    this.gpsSirina = gpsSirina;
    this.gpsDuzina = gpsDuzina;
    this.gpsSirinaRadar = gpsSirinaRadar;
    this.gpsDuzinaRadar = gpsDuzinaRadar;
  }

  public int getId() {
    return id;
  }

  public long getVrijemePocetak() {
    return vrijemePocetak;
  }

  public long getVrijemeKraj() {
    return vrijemeKraj;
  }

  public double getBrzina() {
    return brzina;
  }

  public double getGpsSirina() {
    return gpsSirina;
  }

  public double getGpsDuzina() {
    return gpsDuzina;
  }

  public double getGpsSirinaRadar() {
    return gpsSirinaRadar;
  }

  public double getGpsDuzinaRadar() {
    return gpsDuzinaRadar;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setVrijemePocetak(long vrijemePocetak) {
    this.vrijemePocetak = vrijemePocetak;
  }

  public void setVrijemeKraj(long vrijemeKraj) {
    this.vrijemeKraj = vrijemeKraj;
  }

  public void setBrzina(double brzina) {
    this.brzina = brzina;
  }

  public void setGpsSirina(double gpsSirina) {
    this.gpsSirina = gpsSirina;
  }

  public void setGpsDuzina(double gpsDuzina) {
    this.gpsDuzina = gpsDuzina;
  }

  public void setGpsSirinaRadar(double gpsSirinaRadar) {
    this.gpsSirinaRadar = gpsSirinaRadar;
  }

  public void setGpsDuzinaRadar(double gpsDuzinaRadar) {
    this.gpsDuzinaRadar = gpsDuzinaRadar;
  }
}
