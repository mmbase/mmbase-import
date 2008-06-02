<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/edit/form" %>
<%@ taglib prefix="util" tagdir="/WEB-INF/tags/edit/util" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="mm" uri="http://www.mmbase.org/mmbase-taglib-1.0"  %>

<form:wizard title="Statische teksten in de site" wizardfile="tekst" >

    <edit:path name="Menu" url="../index.jsp"/>
    <edit:path name="Statische teksten" url="../site/teksten.jsp"/>
    <edit:path  node="${nodenr}"  />

    <form:container nodetype="freetext">
        <form:showfield fieldname="sleutel" field="title"/>
        <form:richtextfield field="body" size="medium"/>
    </form:container>
</form:wizard>



