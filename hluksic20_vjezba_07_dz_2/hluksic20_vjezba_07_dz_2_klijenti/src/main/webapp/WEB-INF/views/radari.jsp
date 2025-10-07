<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>REST MVC - Izbornik za radare</title>
    </head>
    <body>
    <h1>REST MVC - Izbornik za radare</h1>
        <ul>
        	<li>
                <h2><a href="${pageContext.servletContext.contextPath}/mvc/">Početna stranica</a></h2>
            </li>
         	<li>
            	<h3><a href="${pageContext.servletContext.contextPath}/mvc/radari/ispisRadara">Ispis svih radara</a></h3>
            </li>
            <li>
            	<h3><a href="${pageContext.servletContext.contextPath}/mvc/radari/reset">Resetiraj sve radare</a></h3>
            	<%
			    String resetOdg = (String) request.getAttribute("resetOdg");
                if (resetOdg != null) {
				%>
                <p><%= resetOdg %></p>
                <%
                }
                %>
            </li>
            <li>
            	<h3>Pretraživanje radara po ID-u</h3>
                <form method="post" action="${pageContext.servletContext.contextPath}/mvc/radari/pretraziId">
                    <table>
                        <tr>
                            <td>ID: </td>
                            <td><input name="id"/>
                                <input type="hidden" name="${mvc.csrf.name}" value="${mvc.csrf.token}"/>
                            </td>
                        </tr>
                        <tr>
                            <td>&nbsp;</td>
                            <td><input type="submit" value=" Dohvati "></td>
                        </tr>                        
                    </table>
                </form>
            </li>
            <li>
            	<h3>Provjeri radar po ID-u</h3>
                <form method="post" action="${pageContext.servletContext.contextPath}/mvc/radari/provjeriId">
                    <table>
                        <tr>
                            <td>ID: </td>
                            <td><input name="id"/>
                                <input type="hidden" name="${mvc.csrf.name}" value="${mvc.csrf.token}"/>
                            </td>
                        </tr>
                        <tr>
                            <td>&nbsp;</td>
                            <td><input type="submit" value=" Provjeri "></td>
                        </tr>
                        <tr>
                            <%
						    String stanjeRadar = (String) request.getAttribute("stanjeRadar");
			                if (stanjeRadar != null) {
							%>
			                <p><%= stanjeRadar %></p>
			                <%
			                }
			                %>
                        </tr>                  
                    </table>
                </form>
            </li>
            <li>
            	<h3><a href="${pageContext.servletContext.contextPath}/mvc/radari/brisiSve">Briši sve radare</a></h3>
            </li>
        </ul>
    </body>
</html>