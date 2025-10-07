<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, java.util.Date, java.text.SimpleDateFormat, edu.unizg.foi.nwtis.hluksic20.vjezba_07_dz_2.podaci.Voznja" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>REST MVC - Pregled vožnji</title>
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
        <h1>REST MVC - Pregled vožnji</h1>
       <ul>
            <li>
                <h2><a href="${pageContext.servletContext.contextPath}/mvc/">Početna stranica</a></h2>
            </li>
            <li>
                <h2><a href="${pageContext.servletContext.contextPath}/mvc/vozila">Izbornik za vozila</a></h2>
            </li>
            <li>
                <h2><a href="${pageContext.servletContext.contextPath}/mvc/simulacije">Izbornik za simulacije</a></h2>
            </li>
        </ul>
        <br/>       
        <table>
       	<tr><th>ID<th>Broj</th><th>Vrijeme</th><th>Brzina</th><th>GPS širina</th><th>GPS dužina</th></tr>
		<%
		int i=0;
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
		List<Voznja> voznje = (List<Voznja>) request.getAttribute("voznje");
		
		for(Voznja v: voznje) {
		  Date vrijeme = new Date(v.getVrijeme());
		  
		%>
       	<tr><td class="desno"><%= v.getId() %></td><td><%= v.getBroj() %></td><td><%=  sdf.format(vrijeme) %></td><td><%= v.getBrzina() %></td><td><%= v.getGpsSirina() %></td><td><%= v.getGpsDuzina() %></td></tr>	  
	  	<%
		}
		%>	
        </table>	        
    </body>
</html>
