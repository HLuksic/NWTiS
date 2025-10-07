<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>REST MVC - Početna stranica</title>
    </head>
    <body>
        <h1>REST MVC - Početna stranica</h1>
        
        <fieldset>
 		<legend>Kazne</legend>
        <ul>
            <li><h2><a href="${pageContext.servletContext.contextPath}/mvc/kazne">Izbornik za kazne</a></h2></li>
        </ul>
        </fieldset>
        
        <fieldset>
 		<legend>Radari</legend>
 		<ul>
            <li><h2><a href="${pageContext.servletContext.contextPath}/mvc/radari">Izbornik za radare</a></h2></li>
 		</ul>
 		</fieldset>
 		
 		<fieldset>
 		<legend>Vozila</legend>
 		<ul>
            <li><h2><a href="${pageContext.servletContext.contextPath}/mvc/vozila">Izbornik za vozila</a></h2></li>
 		</ul>
 		</fieldset>
 		
 		<fieldset>
 		<legend>Simulacije</legend>
 		<ul>
            <li><h2><a href="${pageContext.servletContext.contextPath}/mvc/simulacije">Izbornik za simulacije</a></h2></li>
 		</ul>
 		</fieldset>
    </body>
</html>
