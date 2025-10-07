package edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.podaci.Radar;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class RestKlijentRadari {

  public RestKlijentRadari() {}

  public List<Radar> getRadariJSON() {
    return new RestRadari().getJSON();
  }

  public List<Radar> getRadarJSON_id(String id) {
    return new RestRadari().getJSON_id(id);
  }

  public boolean getProvjeriJSON_id(String id) {
    return new RestRadari().getProvjeriJSON_id(id);
  }

  public String getResetJson() {
    return new RestRadari().getResetJSON();
  }

  public String deleteJson() {
    return new RestRadari().deleteJson();
  }

  public String deleteJson_id(String id) {
    return new RestRadari().deleteJson_id(id);
  }

  static class RestRadari {

    private final WebTarget webTarget;

    private final Client client;

    private static final String BASE_URI = "http://localhost:9080/";

    public RestRadari() {
      client = ClientBuilder.newClient();
      webTarget = client.target(BASE_URI).path("nwtis/v1/api/radari");
    }

    public List<Radar> getJSON_id(String id) {
      var resource = webTarget.path(java.text.MessageFormat.format("{0}", new Object[] {id}));
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      Response restOdgovor = resource.request().get();
      List<Radar> radari = new ArrayList<Radar>();

      if (restOdgovor.getStatus() == 200) {
        // Logger.getLogger(RestKlijentRadari.class.getName()).log(Level.SEVERE, odgovor, odgovor);
        radari.addAll(Arrays.asList(
            JsonbBuilder.create().fromJson(restOdgovor.readEntity(String.class), Radar[].class)));
      }

      return radari;
    }

    public List<Radar> getJSON() throws ClientErrorException {
      Invocation.Builder request = webTarget.request(MediaType.APPLICATION_JSON);
      Response restOdgovor = webTarget.request().get();
      List<Radar> radari = new ArrayList<Radar>();

      if (restOdgovor.getStatus() == 200) {
        radari.addAll(Arrays.asList(
            JsonbBuilder.create().fromJson(restOdgovor.readEntity(String.class), Radar[].class)));
      }

      return radari;
    }

    public boolean getProvjeriJSON_id(String id) throws ClientErrorException {
      var resource =
          webTarget.path(java.text.MessageFormat.format("{0}/provjeri", new Object[] {id}));
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);

      return resource.request().get().getStatus() == 200;
    }

    public String getResetJSON() throws ClientErrorException {
      var resource = webTarget.path("/reset");
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      Response restOdgovor = resource.request().get();

      if (restOdgovor.getStatus() == 200) {
        return restOdgovor.readEntity(String.class);
      }

      return "Poslužitelj za registraciju radara ne radi!";
    }

    public String deleteJson() {
      Invocation.Builder request = webTarget.request(MediaType.APPLICATION_JSON);
      Response restOdgovor = webTarget.request().delete();

      if (restOdgovor.getStatus() == 200) {
        return restOdgovor.readEntity(String.class);
      }

      return null;
    }

    public String deleteJson_id(String id) {
      var resource = webTarget.path(java.text.MessageFormat.format("{0}", new Object[] {id}));
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      Response restOdgovor = resource.request().delete();
      // Logger.getLogger(RestKlijentRadari.class.getName()).log(Level.INFO, "webtarget: " +
      // webTarget);

      if (restOdgovor.getStatus() == 200) {
        return restOdgovor.readEntity(String.class);
      }

      return null;
    }
  }
}
