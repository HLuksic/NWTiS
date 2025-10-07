<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>REST MVC - Izbornik za simulacije</title>
    </head>
    <body>
    <h1>REST MVC - Izbornik za simulacije</h1>
        <ul>
        	<li>
                <h2><a href="${pageContext.servletContext.contextPath}/mvc/">Početna stranica</a></h2>
            </li>
         	<li>
            	<h3>Pretraživanje vožnji u intervalu</h3>
                <form method="post" action="${pageContext.servletContext.contextPath}/mvc/simulacije/pretrazivanjeVoznjiVrijeme">
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
            	<h3>Pretraživanje vožnji po vozilu i/ili u intervalu</h3>
                <form method="post" action="${pageContext.servletContext.contextPath}/mvc/simulacije/pretrazivanjeVoznjiVoziloVrijeme">
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
        </ul>
    </body>
</html>