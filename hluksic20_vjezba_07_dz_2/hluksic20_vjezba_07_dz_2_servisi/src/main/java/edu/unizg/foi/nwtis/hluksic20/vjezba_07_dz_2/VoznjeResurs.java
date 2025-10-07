package edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2;

import edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.podaci.Voznja;
import edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.podaci.VoznjaDAO;
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

@Path("nwtis/v1/api/simulacije")
public class VoznjeResurs extends SviResursi {
  private VoznjaDAO voznjaDAO = null;

  @PostConstruct
  private void pripremiKorisnikDAO() {
    System.out.println("Pokrećem REST: " + this.getClass().getName());
    try {
      var vezaBP = this.vezaBazaPodataka.getVezaBazaPodataka();
      this.voznjaDAO = new VoznjaDAO(vezaBP);
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
}
