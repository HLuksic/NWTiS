package edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.podaci.Radar;
import edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.pomocnici.MrezneOperacije;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("nwtis/v1/api/radari")
public class RadariResurs extends SviResursi {
  private final int port = 8000;

  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public Response getJson(@HeaderParam("Accept") String tipOdgovora) throws UnknownHostException {

    var odgovor = MrezneOperacije
        .posaljiZahtjevPosluzitelju(InetAddress.getLocalHost().getHostAddress(), port, "RADAR SVI");

    if (odgovor == null)
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("{\"odgovor\":\"Poslužitelj za registraciju radara ne radi.\"}").build();

    if (odgovor.length() == 5) // OK {}, nema radara
      return Response.status(Response.Status.OK).entity(new ArrayList<>()).build();

    List<Radar> radari = dajRadare(odgovor);

    return Response.status(Response.Status.OK).entity(radari).build();
  }

  @Path("reset")
  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public Response resetJson(@HeaderParam("Accept") String tipOdgovora) throws UnknownHostException {

    var odgovor = MrezneOperacije.posaljiZahtjevPosluzitelju(
        InetAddress.getLocalHost().getHostAddress(), port, "RADAR RESET");

    if (odgovor == null)
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("{\"odgovor\":\"Poslužitelj za registraciju radara ne radi.\"}").build();

    return Response.status(Response.Status.OK).entity(odgovor).build();
  }

  @Path("{id}")
  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public Response getJsonId(@HeaderParam("Accept") String tipOdgovora, @PathParam("id") int id)
      throws UnknownHostException {

    var odgovor = MrezneOperacije
        .posaljiZahtjevPosluzitelju(InetAddress.getLocalHost().getHostAddress(), port, "RADAR SVI");

    if (odgovor == null)
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("{\"odgovor\":\"Poslužitelj za registraciju radara ne radi.\"}").build();

    if (odgovor.length() == 5) // OK {}
      return Response.status(Response.Status.OK).entity(new ArrayList<>()).build();

    List<Radar> radari = dajRadare(odgovor);

    for (Radar radar : radari) {
      if (radar.getId() == id) {
        var a = new ArrayList<Radar>();
        a.add(radar);
        return Response.status(Response.Status.OK).entity(a).build();
      }
    }
    return Response.status(Response.Status.OK).entity(new ArrayList<>()).build();
  }

  @Path("{id}/provjeri")
  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public Response provjeriJsonId(@HeaderParam("Accept") String tipOdgovora, @PathParam("id") int id)
      throws UnknownHostException {

    var odgovor = MrezneOperacije.posaljiZahtjevPosluzitelju(
        InetAddress.getLocalHost().getHostAddress(), port, "RADAR " + id);

    if (odgovor == null)
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("{\"odgovor\":\"Poslužitelj za registraciju radara ne radi.\"}").build();

    if (odgovor.contains("ERROR"))
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("{\"odgovor\":\"Radar nije u kolekciji.\"}").build();

    return Response.status(Response.Status.OK).entity("{\"odgovor\":\"OK\"}").build();
  }

  @DELETE
  @Produces({MediaType.APPLICATION_JSON})
  public Response deleteJson(@HeaderParam("Accept") String tipOdgovora)
      throws UnknownHostException {
    var odgovor = "";
    odgovor = MrezneOperacije.posaljiZahtjevPosluzitelju(
        InetAddress.getLocalHost().getHostAddress(), port, "RADAR OBRIŠI SVE");

    if (odgovor == null)
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("{\"odgovor\":\"Poslužitelj za registraciju radara ne radi.\"}").build();

    if (odgovor.contains("ERROR"))
      return Response.status(Response.Status.OK)
          .entity("{\"odgovor\":\"Radar nije aktivan ili ne postoji.\"}").build();

    return Response.status(Response.Status.OK).entity("{\"odgovor\":\"OK\"}").build();
  }

  @Path("{id}")
  @DELETE
  @Produces({MediaType.APPLICATION_JSON})
  public Response deleteJsonId(@HeaderParam("Accept") String tipOdgovora, @PathParam("id") int id)
      throws UnknownHostException {
    var odgovor = "";
    odgovor = MrezneOperacije.posaljiZahtjevPosluzitelju(
        InetAddress.getLocalHost().getHostAddress(), port, "RADAR OBRIŠI " + id);

    if (odgovor == null)
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("{\"odgovor\":\"Poslužitelj za registraciju radara ne radi.\"}").build();

    if (odgovor.contains("ERROR"))
      return Response.status(Response.Status.OK)
          .entity("{\"odgovor\":\"Radar nije aktivan ili ne postoji.\"}").build();

    return Response.status(Response.Status.OK).entity("{\"odgovor\":\"OK\"}").build();
  }

  private List<Radar> dajRadare(String odgovor) {
    List<Radar> radari = new ArrayList<>();

    Pattern pattern = Pattern.compile("\\[(.*?)\\]");
    Matcher matcher = pattern.matcher(odgovor);

    while (matcher.find()) {
      String group = matcher.group(1);
      String[] attributes = group.split("\\s+");

      // 1 localhost 8010 46.29950 16.33001 100
      radari.add(new Radar(Integer.parseInt(attributes[0]), attributes[1],
          Integer.parseInt(attributes[2]), -1, -1, Integer.parseInt(attributes[5]), null, -1, null,
          -1, null, Double.parseDouble(attributes[3]), Double.parseDouble(attributes[4])));
    }
    return radari;
  }
}
