<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, java.util.Date, java.text.SimpleDateFormat, edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.podaci.Kazna" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>REST MVC - Pregled kazni</title>
        <style type="text/css">
table, th, td {
  border: 1px solid;
}       
th {
	text-align: center;
	font-weight: bold;
} 
.desno {
	text-align: right;
}
        </style>
    </head>
    <body>
        <h1>REST MVC - Pregled kazni</h1>
       <ul>
            <li>
                <h2><a href="${pageContext.servletContext.contextPath}/mvc/">Početna stranica</a></h2>
            </li>
            <li>
                <h2><a href="${pageContext.servletContext.contextPath}/mvc/kazne">Izbornik za kazne</a></h2>
            </li>
        </ul>
        <br/>       
        <table>
       	<tr><th>R.br.<th>Vozilo</th><th>Vrijeme</th><th>Brzina</th><th>GPS širina</th><th>GPS dužina</th></tr>
		<%
		int i=0;
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
		List<Kazna> kazne = (List<Kazna>) request.getAttribute("kazne");
		Kazna kazna = (Kazna) request.getAttribute("kazna");
		String redniBroj = (String) request.getAttribute("redniBroj");
		
		if (kazna != null) {
		  kazne = new java.util.ArrayList<Kazna>();
		  kazne.add(kazna);
		  i = Integer.parseInt(redniBroj) - 1;
		}
		
		for(Kazna k: kazne) {
		  i++;
		  Date vrijeme = new Date(k.getVrijemePocetak());
		  
		%>
       	<tr><td class="desno"><%= i %></td><td><%= k.getId() %></td><td><%=  sdf.format(vrijeme) %></td><td><%= k.getBrzina() %></td><td><%= k.getGpsSirinaRadar() %></td><td><%= k.getGpsDuzinaRadar() %></td></tr>	  
	  	<%
		}
		%>	
        </table>	        
    </body>
</html>
