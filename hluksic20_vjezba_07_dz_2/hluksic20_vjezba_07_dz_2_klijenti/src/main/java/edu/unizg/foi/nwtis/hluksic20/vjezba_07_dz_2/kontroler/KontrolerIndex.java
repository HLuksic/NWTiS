package edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.kontroler;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.mvc.binding.BindingResult;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Controller
@Path("/")
@RequestScoped
public class KontrolerIndex {
  
  @Inject
  private Models model;

  @Inject
  private BindingResult bindingResult;
  
  @GET
  public String index() {
      return "index.jsp";
  }
  
  @GET
  @Path("kazne")
  @View("kazne.jsp")
  public void kaznePocetna() {}
  
  @GET
  @Path("radari")
  @View("radari.jsp")
  public void radariPocetna() {}
}
