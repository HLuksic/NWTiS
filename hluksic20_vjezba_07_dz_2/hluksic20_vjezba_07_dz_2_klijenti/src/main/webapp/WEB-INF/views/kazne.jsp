<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>REST MVC - Izbornik za kazne</title>
    </head>
    <body>
    <h1>REST MVC - Izbornik za kazne</h1>
        <ul>
         	<li>
                <h2><a href="${pageContext.servletContext.contextPath}/mvc/">Početna stranica</a></h2>
            </li>
            <li>
                <h3><a href="${pageContext.servletContext.contextPath}/mvc/kazne/dajSve">Ispis svih kazni</a></h3>
            </li>
            <li>
            	<h3><a href="${pageContext.servletContext.contextPath}/mvc/kazne/provjeriPosluzitelja">Provjeri poslužitelj kazni</a></h3>
                <%
			    String stanjeKazne = (String) request.getAttribute("stanjeKazne");
                if (stanjeKazne != null) {
				%>
                <p><%= stanjeKazne %></p>
                <%
                }
                %>
            </li>
            <li>
            	<h3>Pretraživanje kazni u intervalu</h3>
                <form method="post" action="${pageContext.servletContext.contextPath}/mvc/kazne/pretrazivanjeKazniVrijeme">
                    <table>
                        <tr>
                            <td>Od vremena: </td>
                            <td><input name="odVremena"/>
                                <input type="hidden" name="${mvc.csrf.name}" value="${mvc.csrf.token}"/>
                            </td>
                        </tr>
                        <tr>
                            <td>Do vremena: </td>
                            <td><input name="doVremena"/>
                        </tr>
                        <tr>
                            <td>&nbsp;</td>
                            <td><input type="submit" value=" Dohvati kazne "></td>
                        </tr>                        
                    </table>
                </form>
            </li>
            <li>
            	<h3>Pretraživanje kazni po rednom broju</h3>
                <form method="post" action="${pageContext.servletContext.contextPath}/mvc/kazne/pretrazivanjeKazniRb">
                    <table>
                        <tr>
                            <td>Redni broj: </td>
                            <td><input name="redniBroj"/>
                                <input type="hidden" name="${mvc.csrf.name}" value="${mvc.csrf.token}"/>
                            </td>
                        </tr>
                        <tr>
                            <td>&nbsp;</td>
                            <td><input type="submit" value=" Dohvati kaznu "></td>
                        </tr>                        
                    </table>
                </form>
            </li>
            <li>
            	<h3>Pretraživanje kazni po vozilu</h3>
            	<form method="post" action="${pageContext.servletContext.contextPath}/mvc/kazne/pretrazivanjeKazniVozilo">
                    <table>
                        <tr>
                            <td>ID vozila: </td>
                            <td><input name="idVozila"/>
                                <input type="hidden" name="${mvc.csrf.name}" value="${mvc.csrf.token}"/>
                            </td>
                        </tr>
                        <tr>
                            <td>&nbsp;</td>
                            <td><input type="submit" value=" Dohvati kazne "></td>
                        </tr>                        
                    </table>
                </form>
            </li>
            <li>
            	<h3>Pretraživanje kazni po vozilu u intervalu</h3>
            	<form method="post" action="${pageContext.servletContext.contextPath}/mvc/kazne/pretrazivanjeKazniVoziloVrijeme">
                    <table>
                        <tr>
                            <td>ID vozila: </td>
                            <td><input name="idVozila"/>
                                <input type="hidden" name="${mvc.csrf.name}" value="${mvc.csrf.token}"/>
                            </td>
                        </tr>
                        <tr>
                            <td>Od vremena: </td>
                            <td><input name="odVremena"/>
                                <input type="hidden" name="${mvc.csrf.name}" value="${mvc.csrf.token}"/>
                            </td>
                        </tr>
                        <tr>
                            <td>Do vremena: </td>
                            <td><input name="doVremena"/></td>
                        </tr>
                        <tr>
                            <td>&nbsp;</td>
                            <td><input type="submit" value=" Dohvati kazne "></td>
                        </tr>                        
                    </table>
                </form>
            </li>
        </ul>          
    
    </body>
</html>