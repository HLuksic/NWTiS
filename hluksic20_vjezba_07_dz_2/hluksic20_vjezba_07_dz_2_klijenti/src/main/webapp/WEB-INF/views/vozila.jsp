<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>REST MVC - Izbornik za vozila</title>
    </head>
    <body>
    <h1>REST MVC - Izbornik za vozila</h1>
        <ul>
        	<li>
                <h2><a href="${pageContext.servletContext.contextPath}/mvc/">Početna stranica</a></h2>
            </li>
         	<li>
            	<h3>Pretraživanje praćenih vožnji u intervalu</h3>
                <form method="post" action="${pageContext.servletContext.contextPath}/mvc/vozila/pretrazivanjeVoznjiVrijeme">
                    <table>
                        <tr>
                            <td>Od vremena: </td>
                            <td><input name="odVremena"/></td>
                        </tr>
                        <tr>
                            <td>Do vremena: </td>
                            <td><input name="doVremena"/></td>
                        </tr>
                        <tr>
                            <td>&nbsp;</td>
                            <td><input type="submit" value=" Dohvati voznje "></td>
                        </tr>                        
                    </table>
                </form>
            </li>
         	<li>
            	<h3>Pretraživanje praćenih vožnji po vozilu i/ili u intervalu</h3>
                <form method="post" action="${pageContext.servletContext.contextPath}/mvc/vozila/pretrazivanjeVoznjiVoziloVrijeme">
                    <table>
                    	<tr>
                            <td>ID vozila: </td>
                            <td><input name="idVozila"/>
                                <input type="hidden" name="${mvc.csrf.name}" value="${mvc.csrf.token}"/>
                            </td>
                        </tr>
                        <tr>
                            <td>Od vremena: </td>
                            <td><input name="odVremena"/></td>
                        </tr>
                        <tr>
                            <td>Do vremena: </td>
                            <td><input name="doVremena"/></td>
                        </tr>
                        <tr>
                            <td>&nbsp;</td>
                            <td><input type="submit" value=" Dohvati voznje "></td>
                        </tr>                        
                    </table>
                </form>
            </li>
            <li>
            	<h3>Start</h3>
                <form method="post" action="${pageContext.servletContext.contextPath}/mvc/vozila/startVozilo">
                    <table>
                    	<tr>
                            <td>ID vozila: </td>
                            <td><input name="idVozila"/>
                                <input type="hidden" name="${mvc.csrf.name}" value="${mvc.csrf.token}"/>
                            </td>
                        </tr>
                        <tr>
                            <td>&nbsp;</td>
                            <td><input type="submit" value=" Start "></td>
                        </tr>                        
                    </table>
                </form>
                <%
			    String odg = (String) request.getAttribute("odg");
                if (odg != null) {
				%>
                <p><%= odg %></p>
                <%
                }
                %>
            </li>
            <li>
            	<h3>Stop</h3>
                <form method="post" action="${pageContext.servletContext.contextPath}/mvc/vozila/stopVozilo">
                    <table>
                    	<tr>
                            <td>ID vozila: </td>
                            <td><input name="idVozila"/>
                                <input type="hidden" name="${mvc.csrf.name}" value="${mvc.csrf.token}"/>
                            </td>
                        </tr>
                        <tr>
                            <td>&nbsp;</td>
                            <td><input type="submit" value=" Stop "></td>
                        </tr>                        
                    </table>
                </form>
                <%
                if (odg != null) {
				%>
                <p><%= odg %></p>
                <%
                }
                %>
            </li>
        </ul>
    </body>
</html>