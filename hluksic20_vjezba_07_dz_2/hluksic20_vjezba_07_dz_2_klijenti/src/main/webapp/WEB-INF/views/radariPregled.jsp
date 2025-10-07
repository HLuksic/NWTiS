<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, java.util.Date, java.text.SimpleDateFormat, edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.podaci.Radar" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>REST MVC - Pregled radara</title>
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
        <h1>REST MVC - Pregled radara</h1>
       <ul>
       		<li>
                <h2><a href="${pageContext.servletContext.contextPath}/mvc/">Početna stranica</a></h2>
            </li>
            <li>
                <h2><a href="${pageContext.servletContext.contextPath}/mvc/radari">Izbornik za radare</a></h2>
           	</li>
        </ul>
        <br/>
        <table>
        <tr><th>ID<th>Adresa</th><th>Port</th><th>GPS širina</th><th>GPS dužina</th><th>Domet</th><th></th></tr>
		<%
		List<Radar> radari = (List<Radar>) request.getAttribute("radari");
		
		for(Radar r: radari) {
		  %>
	       <tr><td class="desno"><%= r.getId() %></td><td><%= r.getAdresaRadara() %></td><td><%= r.getMreznaVrataRadara() %></td><td><%= r.getGpsSirina() %></td><td><%= r.getGpsDuzina() %></td><td><%= r.getMaksUdaljenost() %></td><td><a href="${pageContext.servletContext.contextPath}/mvc/radari/brisiId?id=<%= r.getId() %>">Obriši</a></td></tr>	  
		  <%
		}
		%>	
        </table>	        
    </body>
</html>
