/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.kontroler;

import java.util.List;
import edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.model.RestKlijentKazne;
import edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.podaci.Kazna;
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

/**
 *
 * @author NWTiS
 */
@Controller
@Path("kazne")
@RequestScoped
public class KontrolerKazne {

  @Inject
  private Models model;

  @Inject
  private BindingResult bindingResult;
  
  @GET
  @View("kazne.jsp")
  public void pocetnaKazne() {}
  
  @GET
  @Path("provjeriPosluzitelja")
  @View("kazne.jsp")
  public void json_h() {
    RestKlijentKazne k = new RestKlijentKazne();
    if (k.head()) {
      model.put("stanjeKazne", "RADI");
    } else {
      model.put("stanjeKazne", "NE RADI");
    }
  }

  @GET
  @Path("dajSve")
  @View("kaznePregled.jsp")
  public void json() {
    RestKlijentKazne k = new RestKlijentKazne();
    List<Kazna> kazne = k.getKazneJSON();
    model.put("kazne", kazne);
  }

  @POST
  @Path("pretrazivanjeKazniVrijeme")
  @View("kaznePregled.jsp")
  public void json_pi(@FormParam("odVremena") long odVremena,
      @FormParam("doVremena") long doVremena) {
    RestKlijentKazne k = new RestKlijentKazne();
    List<Kazna> kazne = k.getKazneJSON_od_do(odVremena, doVremena);
    model.put("kazne", kazne);
  }

  @POST
  @Path("pretrazivanjeKazniRb")
  @View("kaznePregled.jsp")
  public void json_pi(@FormParam("redniBroj") String redniBroj) {
    RestKlijentKazne k = new RestKlijentKazne();
    Kazna kazna = k.getKaznaJSON_rb(redniBroj);
    model.put("kazna", kazna);
    model.put("redniBroj", redniBroj);
  }

  @POST
  @Path("pretrazivanjeKazniVozilo")
  @View("kaznePregled.jsp")
  public void json_pi2(@FormParam("idVozila") String idVozila) {
    RestKlijentKazne k = new RestKlijentKazne();
    List<Kazna> kazne = k.getKazneJSON_vozilo(idVozila);
    model.put("kazne", kazne);
  }

  @POST
  @Path("pretrazivanjeKazniVoziloVrijeme")
  @View("kaznePregled.jsp")
  public void json_pi3(@FormParam("idVozila") String idVozila,
      @FormParam("odVremena") long odVremena, @FormParam("doVremena") long doVremena) {
    RestKlijentKazne k = new RestKlijentKazne();
    List<Kazna> kazne = k.getKazneJSON_vozilo_od_do(idVozila, odVremena, doVremena);
    model.put("kazne", kazne);
  }
}
