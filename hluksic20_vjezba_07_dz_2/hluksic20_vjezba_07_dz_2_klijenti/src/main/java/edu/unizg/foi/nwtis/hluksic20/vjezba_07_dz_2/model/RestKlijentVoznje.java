package edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.podaci.Voznja;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class RestKlijentVoznje {
  private String endpoint;

  public RestKlijentVoznje(String endpoint) {
    this.endpoint = endpoint;
  }

  public List<Voznja> getVoznjeJSON_od_do(long odVremena, long doVremena) {
    return new RestVoznje(endpoint).getJSON_od_do(odVremena, doVremena);
  }

  public List<Voznja> getVoznjeJSON_vozilo_od_do(String id, long odVremena, long doVremena) {
    return new RestVoznje(endpoint).getJSON_vozilo_od_do(id, odVremena, doVremena);
  }

  public boolean postVoznjaJSON(Voznja voznja) {
    return new RestVoznje(endpoint).postJSON(voznja);
  }

  public boolean getKomanda(String id, String k) {
    return new RestVoznje(endpoint).getKomanda(id, k);
  }

  static class RestVoznje {

    private final WebTarget webTarget;
    private final Client client;
    private static final String BASE_URI = "http://localhost:9080/";

    public RestVoznje(String endpoint) {
      client = ClientBuilder.newClient();
      webTarget = client.target(BASE_URI).path("nwtis/v1/api/" + endpoint);
    }


    public List<Voznja> getJSON_od_do(long odVremena, long doVremena) {
      WebTarget resource = webTarget;
      List<Voznja> voznje = new ArrayList<Voznja>();

      resource = resource.queryParam("od", odVremena);
      resource = resource.queryParam("do", doVremena);
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      Response restOdgovor = resource.request().get();

      if (restOdgovor.getStatus() == 200)
        voznje.addAll(Arrays.asList(
            JsonbBuilder.create().fromJson(restOdgovor.readEntity(String.class), Voznja[].class)));

      return voznje;
    }

    public List<Voznja> getJSON_vozilo_od_do(String id, long odVremena, long doVremena) {
      WebTarget resource = webTarget;
      List<Voznja> voznje = new ArrayList<Voznja>();

      resource = resource.path(java.text.MessageFormat.format("vozilo/{0}", new Object[] {id}));
      resource = resource.queryParam("od", odVremena);
      resource = resource.queryParam("do", doVremena);
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      Response restOdgovor = resource.request().get();

      if (restOdgovor.getStatus() == 200)
        voznje.addAll(Arrays.asList(
            JsonbBuilder.create().fromJson(restOdgovor.readEntity(String.class), Voznja[].class)));

      return voznje;
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
        return true;
      }

      return false;
    }

    public boolean getKomanda(String id, String k) {
      if (k == null) {
        return false;
      }

      var resource =
          webTarget.path(java.text.MessageFormat.format("vozilo/{0}/" + k, new Object[] {id}));
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);

      return resource.request().get().getStatus() == 200;
    }

    public void close() {
      client.close();
    }
  }
}
