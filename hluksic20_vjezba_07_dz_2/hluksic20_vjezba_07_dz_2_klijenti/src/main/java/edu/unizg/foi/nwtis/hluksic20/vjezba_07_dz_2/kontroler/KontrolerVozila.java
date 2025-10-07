package edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.kontroler;

import edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.model.RestKlijentVoznje;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.mvc.binding.BindingResult;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Controller
@Path("vozila")
@RequestScoped
public class KontrolerVozila {
  @Inject
  private Models model;

  @Inject
  private BindingResult bindingResult;

  @GET
  @View("vozila.jsp")
  public void pocetnaVozila() {}

  @POST
  @Path("pretrazivanjeVoznjiVrijeme")
  @View("voznjePregled.jsp")
  public void json_pi(@FormParam("odVremena") long odVremena,
      @FormParam("doVremena") long doVremena) {
    model.put("voznje", new RestKlijentVoznje("vozila").getVoznjeJSON_od_do(odVremena, doVremena));
  }

  @POST
  @Path("pretrazivanjeVoznjiVoziloVrijeme")
  @View("voznjePregled.jsp")
  public void json_pi(@FormParam("idVozila") String idVozila,
      @FormParam("odVremena") long odVremena, @FormParam("doVremena") long doVremena) {
    model.put("voznje",
        new RestKlijentVoznje("vozila").getVoznjeJSON_vozilo_od_do(idVozila, odVremena, doVremena));
  }

  @POST
  @Path("startVozilo")
  @View("vozila.jsp")
  public void json_start(@FormParam("idVozila") String idVozila) {
    if (new RestKlijentVoznje("vozila").getKomanda(idVozila, "start"))
      model.put("odg", "Startano");
    else
      model.put("odg", "Poslužitelj za vozila ne radi.");
  }

  @POST
  @Path("stopVozilo")
  @View("vozila.jsp")
  public void json_stop(@FormParam("idVozila") String idVozila) {
    if (new RestKlijentVoznje("vozila").getKomanda(idVozila, "stop"))
      model.put("odg", "Zaustavljeno");
    else
      model.put("odg", "Poslužitelj za vozila ne radi.");
  }
}
