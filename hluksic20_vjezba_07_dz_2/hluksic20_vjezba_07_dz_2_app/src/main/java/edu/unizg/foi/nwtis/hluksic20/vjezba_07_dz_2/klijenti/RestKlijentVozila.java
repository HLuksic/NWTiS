package edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.klijenti;

import edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.podaci.Voznja;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;

public class RestKlijentVozila {
  public RestKlijentVozila() {}
  
  public boolean postVoznjaJSON(Voznja voznja) {
    return new RestVoznje().postJSON(voznja);
  }
  
  static class RestVoznje {

    private final WebTarget webTarget;
    private final Client client;
    private static final String BASE_URI = "http://localhost:9080/";

    public RestVoznje() {
      client = ClientBuilder.newClient();
      webTarget = client.target(BASE_URI).path("nwtis/v1/api/vozila");
    }
    
    public boolean postJSON(Voznja voznja) {
      WebTarget resource = webTarget;
      if (voznja == null) {
        return false;
      }
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);

      var odgovor =
          request.post(Entity.entity(voznja, MediaType.APPLICATION_JSON), String.class).toString();
      if (odgovor.trim().length() > 0) {
        return false;
      }

      return true;
    }
    
    public void close() {
      client.close();
    }
  }
}
