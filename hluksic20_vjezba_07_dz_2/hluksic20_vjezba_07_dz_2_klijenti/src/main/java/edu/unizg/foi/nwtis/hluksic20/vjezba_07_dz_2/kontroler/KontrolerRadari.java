package edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.kontroler;

import edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.model.RestKlijentRadari;
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
import jakarta.ws.rs.QueryParam;

@Controller
@Path("radari")
@RequestScoped
public class KontrolerRadari {
  
  @Inject
  private Models model;

  @Inject
  private BindingResult bindingResult;
  
  @GET
  @View("radari.jsp")
  public void pocetnaRadari() {}

  @GET
  @Path("ispisRadara")
  @View("radariPregled.jsp")
  public void json() {
    model.put("radari", new RestKlijentRadari().getRadariJSON());
  }
  
  @POST
  @Path("pretraziId")
  @View("radariPregled.jsp")
  public void jsonId(@FormParam("id") String id) {
    model.put("radari", new RestKlijentRadari().getRadarJSON_id(id));
  }
  
  @POST
  @Path("provjeriId")
  @View("radari.jsp")
  public void jsonProvjeriId(@FormParam("id") String id) {
    var k = new RestKlijentRadari();
    if (k.getProvjeriJSON_id(id))
      model.put("stanjeRadar", "Radar je u kolekciji.");
    else
      model.put("stanjeRadar", "Radar nije u kolekciji.");
  }
  
  @GET
  @Path("brisiSve")
  @View("radari.jsp")
  public void brisi() {
    new RestKlijentRadari().deleteJson();
  }
  
  @GET
  @Path("brisiId")
  @View("radariPregled.jsp")
  public void brisiId(@QueryParam("id") String id) {
    new RestKlijentRadari().deleteJson_id(id);
    model.put("radari", new RestKlijentRadari().getRadariJSON());
  }
  
  @GET
  @Path("reset")
  @View("radari.jsp")
  public void reset() {
    model.put("resetOdg", new RestKlijentRadari().getResetJson());
  }
}
