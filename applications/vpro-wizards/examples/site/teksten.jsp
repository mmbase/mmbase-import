<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/vpro-wizards" %>
<%@ taglib prefix="list" tagdir="/WEB-INF/tags/vpro-wizards/list" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/vpro-wizards/form" %>
<%@ taglib prefix="util" tagdir="/WEB-INF/tags/vpro-wizards/util" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%--the secion node for plaats van herinnering--%>
<list:wizard  title="Plaats van Herinnering" >
    <edit:path name="Menu" url="../index.jsp"/>
    <edit:path name="Statische teksten"/>

    <util:info title="Statische teksten editen voor Plaats van Herinnering">
        <p>Hier kun je de statische teksten van de 'Plaats van herinnering' site aanpassen. Je kunt op tekst zoeken of op sleutel. Om er achter te komen welke sleutel een bepaalde tekst heeft, voeg dan aan de url ?showkeys=true toe in de website zelf.</p>
    </util:info>


    <list:search nodetype="freetext" wizardfile="tekst" >
    <list:searchfields fields="title,body" defaultmaxage="365"/>
        <list:parentsearchlist parentnodenr="33717335" relationrole="editabletextrel">
            <list:searchrow  delete="false" fields="title,body"   />
        </list:parentsearchlist>
    </list:search>
</list:wizard>

