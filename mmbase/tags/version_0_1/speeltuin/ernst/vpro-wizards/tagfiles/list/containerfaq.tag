<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="container" required="true" %>
<%@ attribute name="type" required="true" %>
<%@ attribute name="name" required="true" %>


<div class="highlighted">
    <div class="header">
        ${name}
    </div>
    <table cellspacing="0" cellpadding="0" border="0">
        <mm:cloud>
            <mm:list nodes="${container}" path="object,posrel,${type}" fields="posrel.pos" orderby="posrel.pos">
                <c:set var="_nodenr"><mm:field name="${type}.number" /></c:set>
                <tbody>
                    <tr>
                        <td class="move">
                            <%--arrow down--%>
                            <mm:last inverse="true">
                                <a style="text-decoration:none" href="/mmbase/vpro-wizards/system/changeposrelnew.jsp?container=${container}&node=${_nodenr}&direction=up" class="movedown" onclick="return checkSearch(this);">
                                    <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/arrow_down_green.png" class="icon" border="0" title="Sortering aanpassen"/>
                                </a>
                            </mm:last>
                            <%--arrow up--%>
                            <mm:first inverse="true">
                                <a style="text-decoration:none" href="/mmbase/vpro-wizards/system/changeposrelnew.jsp?container=${container}&node=${_nodenr}&direction=down" class="moveup" onclick="return checkSearch(this);">
                                    <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/arrow_up_green.png" class="icon" border="0"title="Sortering aanpassen"/>
                                </a>
                            </mm:first>
                        </td>
                        <td class="icons">
                            <mm:node element="${type}">
                                <mm:relatednodes type="answers" max="1"><c:set var="_answer" ><mm:field name="number"/></c:set></mm:relatednodes>
                            </mm:node>
                            <a href="${wizardfile}.jsp?nodenr=${_answer}" class="edit">
                                <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/edit_green.png" class="icon" border="0" title="Aanpassen" />
                            </a>
                            <a href="/mmbase/vpro-wizards/system/deletenode.jsp?nodenr=${_nodenr}">
                                <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/delete_green.png" class="icon" border="0" title="Verwijder"/>
                            </a>
                        </td>
                        <td><mm:field name="${type}.gui()"/></td>
                    </tr>
                <tbody>
            </mm:list>
        </mm:cloud>
    </table>
</div>