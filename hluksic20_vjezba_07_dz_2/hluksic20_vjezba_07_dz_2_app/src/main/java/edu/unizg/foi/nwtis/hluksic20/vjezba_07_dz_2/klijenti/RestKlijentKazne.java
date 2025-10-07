package edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.klijenti;

import edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.podaci.Kazna;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;

public class RestKlijentKazne {
  public RestKlijentKazne() {}
  
  public boolean postKaznaJSON(Kazna kazna) {
    return new RestKazne().postJSON(kazna);
  }
  
  static class RestKazne {

    private final WebTarget webTarget;
    private final Client client;
    private static final String BASE_URI = "http://localhost:9080/";

    public RestKazne() {
      client = ClientBuilder.newClient();
      webTarget = client.target(BASE_URI).path("nwtis/v1/api/kazne");
    }
    
    public boolean postJSON(Kazna kazna) {
      WebTarget resource = webTarget;
      if (kazna == null) {
        return false;
      }
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);

      var odgovor =
          request.post(Entity.entity(kazna, MediaType.APPLICATION_JSON), String.class).toString();
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
