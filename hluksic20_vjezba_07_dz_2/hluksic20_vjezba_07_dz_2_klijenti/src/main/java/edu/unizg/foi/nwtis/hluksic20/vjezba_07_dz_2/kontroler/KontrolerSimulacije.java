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
@Path("simulacije")
@RequestScoped
public class KontrolerSimulacije {
  
  @Inject
  private Models model;
  
  @Inject
  private BindingResult bindingResult;
  
  @GET
  @View("simulacije.jsp")
  public void pocetnaSimulacije() {}
  
  @POST
  @Path("pretrazivanjeVoznjiVrijeme")
  @View("voznjePregled.jsp")
  public void json_pi(@FormParam("odVremena") long odVremena,
      @FormParam("doVremena") long doVremena) {
    model.put("voznje", new RestKlijentVoznje("simulacije").getVoznjeJSON_od_do(odVremena, doVremena));
  }
  
  @POST
  @Path("pretrazivanjeVoznjiVoziloVrijeme")
  @View("voznjePregled.jsp")
  public void json_pi(@FormParam("idVozila") String idVozila, @FormParam("odVremena") long odVremena,
      @FormParam("doVremena") long doVremena) {
    model.put("voznje", new RestKlijentVoznje("simulacije").getVoznjeJSON_vozilo_od_do(idVozila, odVremena, doVremena));
  }
}
