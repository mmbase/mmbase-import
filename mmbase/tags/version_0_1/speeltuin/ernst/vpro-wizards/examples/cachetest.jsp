<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="pvh" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/vpro-wizards" %>

<mm:cloud >
<html>
    <head>
        <title>Plaats van Herinnering: cache flush pagina</title>
    </head>
    <body >

    <%
        pageContext.setAttribute("servletContext", pageContext.getServletContext(), PageContext.REQUEST_SCOPE);
    %>

    <c:if test="${not empty param.group}">
        <c:set var="group">${param.group}</c:set>
        <c:if test="${not empty param.number}">
            <c:set var="group">${group}_${param.number}</c:set>
        </c:if>
        <pvh:flushGroup group="${group}"/>
        <c:if test="${not empty param.back}">
            <c:redirect url="index.jsp"/>
        </c:if>
        <p style="color: red">De volgende cache groep is geflushed: ${feedback}</p>
    </c:if>

        <h2>Plaats van herinnering oscache manage page</h2>
        <p>op deze pagina kun je de os cache groepen flushen die door de 'plaats van herinnerning' wordt
        gebruikt de groepen zijn:</p>
        <ul>
            <li><b>plaatsvanherinnering:</b> Alle pagina's met uitzondering van het formulier 'herinnering uploaden',
            de 'nieuwste herinnering' op de 'plaats' pagina en de herinneringen pagina. Deze groep moet worden geflushed wanneer de redactie een plaats heeft toegevoegd, of Een dynamische tekst op de pagina heeft aangepast</li>
            <li><b>plaatsvanherinnering_quick:</b> De 'nieuwste herinnering' op de 'plaats' pagina, en de xml tempate voor het flash filmpje op de homepage. Wordt automatisch geflushed wanneer er een nieuwe herinnering online komt (of wanneer de redactie de herinnering offline zet: moet nog worden geimplementeerd in de beheer omgeving)</li>
            <li><b>herinneringen_[plaatsnummer]:</b> De herinneringen pagina's. Iedere pagina krijgt zijn eigen groep, zodat wanneer er een herinnering wordt toegevoegd niet de 'herinneringen' pagina's van alle plaatsen worden geflushed. Wordt automatisch geflushed wanneer er een nieuwe herinnering online komt (of wanneer de redactie de herinnering offline zet: moet nog worden geimplementeerd in de beheer omgeving)</li>
        </ul>
        <h3>Flush</h3>

        <p>flush de groep: plaatsvanherinnering</p>
        <form method="post">
            <input type="hidden" name="group" value="plaatsvanherinnering" />
            <input type="submit" value="flush" />
        </form>
        <hr/>

        <p>flush de groep: plaatsvanherinnering_quick</p>
        <form method="post">
            <input type="hidden" name="group" value="plaatsvanherinnering_quick" />
            <input type="submit" value="flush" />
        </form>
        <hr/>

        <p>flush de een van de groepen voor de herinneringen pagina's</p>
        <form method="post">
            <input type="hidden" name="group" value="herinneringen" />
                <select name="number">
                    <mm:listnodes type="memorylocation">
                        <mm:first>
                            <option value="">-- selecteer een plaats --</option>
                        </mm:first>
                        <c:set var="number"><mm:field name="number" /></c:set>
                        <option value="${number}"><mm:field name="title" /></option>
                    </mm:listnodes>
                </select>
            <input type="submit" value="flush" />
        </form>

    </body>
</html>
</mm:cloud>
