package edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2;

import edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.podaci.Voznja;
import edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.podaci.VoznjaDAO;
import edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.pomocnici.MrezneOperacije;
import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("nwtis/v1/api/vozila")
public class PraceneVoznjeResurs extends SviResursi {
  private VoznjaDAO voznjaDAO = null;

  @PostConstruct
  private void pripremiKorisnikDAO() {
    System.out.println("Pokrećem REST: " + this.getClass().getName());
    try {
      var vezaBP = this.vezaBazaPodataka.getVezaBazaPodataka();
      this.voznjaDAO = new VoznjaDAO(vezaBP, "praceneVoznje");
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }
  }

  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public Response getJsonOdDo(@HeaderParam("Accept") String tipOdgovora,
      @QueryParam("od") long odVremena, @QueryParam("do") long doVremena) {
    return Response.status(Response.Status.OK)
        .entity(voznjaDAO.dohvatiVoznje(odVremena, doVremena).toArray()).build();
  }

  @Path("/vozilo/{id}")
  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public Response getJsonVozilo(@HeaderParam("Accept") String tipOdgovora, @PathParam("id") int id,
      @QueryParam("od") long odVremena, @QueryParam("do") long doVremena) {

    if (odVremena <= 0 || doVremena <= 0)
      return Response.status(Response.Status.OK).entity(voznjaDAO.dohvatiVoznjeVozila(id)).build();

    return Response.status(Response.Status.OK)
        .entity(voznjaDAO.dohvatiVoznjeVozilaOdDo(id, odVremena, doVremena)).build();
  }

  @POST
  @Produces({MediaType.APPLICATION_JSON})
  public Response postJsonDodajKaznu(@HeaderParam("Accept") String tipOdgovora, Voznja novaVoznja) {

    var odgovor = voznjaDAO.dodajVoznju(novaVoznja);
    if (odgovor) {
      return Response.status(Response.Status.OK).build();
    } else {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("Neuspješni upis voznje u bazu podataka.").build();
    }
  }

  @Path("vozilo/{id}/start")
  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public Response getJsonStart(@HeaderParam("Accept") String tipOdgovora, @PathParam("id") int id) {

    var odgovor = posaljiPoruku("VOZILO START " + id);

    if (!odgovor)
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("{\"odgovor\":\"Poslužitelj za vozila ne radi.\"}").build();

    return Response.status(Response.Status.OK).entity("{\"odgovor\":\"OK\"}").build();
  }

  @Path("vozilo/{id}/stop")
  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public Response getJsonStop(@HeaderParam("Accept") String tipOdgovora, @PathParam("id") int id) {

    var odgovor = posaljiPoruku("VOZILO STOP " + id);

    if (!odgovor)
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("{\"odgovor\":\"Poslužitelj za vozila ne radi.\"}").build();

    return Response.status(Response.Status.OK).entity("{\"odgovor\":\"OK\"}").build();
  }

  private boolean posaljiPoruku(String k) {
    var poruka = new StringBuilder();
    poruka.append(k).append("\n");

    var odgovor = MrezneOperacije.posaljiZahtjevPosluzitelju("localhost", 8001, poruka.toString());

    return odgovor != null;
  }
}
