<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="util" tagdir="/WEB-INF/tags/vpro-wizards/util" %>
<%@ taglib prefix="mm" uri="http://www.mmbase.org/mmbase-taglib-1.0"  %>
<%@ attribute name="nodetype" required="true" %>
<%@ attribute name="multipart" type="java.lang.Boolean" description="don't set this if you use a fielfield in the body. In that case it's the default" %>


<%--
    This is the wrapper for the main form. it sets the type of the html form (multipart?) and
    the nodetype, which can be used by other tags (fields)

    It also sets the defaults for action and modifier, and sets the id and nodetype
    on the action for the creation of this node.
--%>

<%-- set the default fields --%>
<c:set var="action" scope="request">Node</c:set>
<c:set var="authorized" value="false" scope="request"/>
<c:choose>
    <c:when test="${empty nodenr}">
        <c:set var="modifier" scope="request">create</c:set>
        <mm:maycreate type="${nodetype}"><c:set var="authorized" value="true" scope="request"/></mm:maycreate>
    </c:when>
    <c:otherwise>
        <c:set var="modifier" scope="request">update</c:set>
            <mm:maywrite number="${nodenr}"><c:set var="authorized" value="true" scope="request"/></mm:maywrite>
    </c:otherwise>
</c:choose>


<c:set var="nodetype" scope="request">${nodetype}</c:set>
<div class="objectfields">

    <%--do the body, and catch an exception if there is one. --%>
    <c:set var="body">
        <util:try setMessageAs="m" setExceptionAs="ex" stacktrace="false">
            <jsp:attribute name="catchit">
                <div class="error">Er gaat iets fout bij het afdrukken van een veld. reden: ${m}</div>
            </jsp:attribute>
            <jsp:body>
                <jsp:doBody/>
            </jsp:body>
        </util:try>
    </c:set>
    <c:set var="enctype"><c:if test="${multipart == true || not empty requestScope.filefield}">enctype="multipart/form-data"</c:if></c:set>
    <c:remove var="requestScope.filefield" />

    <c:choose>
        <c:when test="${authorized == 'true'}">
		    <form method="post" ${enctype} action="${pageContext.request.contextPath}/wizard/post" id="formcontainer">
		
		        <%--set the default fields for this action in the form--%>
		        <c:if test="${modifier == 'create'}">
		            <input type="hidden" name="actions[${modifier}${action}][].id" value="new"/>
		            <input type="hidden" name="actions[${modifier}${action}][].nodeType" value="${nodetype}"/>
		            <input type="hidden" name="actions[${modifier}${action}][].nodeAsParam" value="nodenr"/>
		        </c:if>
		        <%--if --%>
		        <c:if test="${modifier == 'update'}">
		            <input type="hidden" name="actions[${modifier}${action}][${nodenr}].nodenr" value="${nodenr}"/>
		        </c:if>
		
		        <c:out value="${body}" escapeXml="false"/>
		
		        <c:set var="isedit" value="${requestScope.___editablefields}" />
		            <div class="formButtons" id="formButtons">
		                <util:flushname/>
		                <c:if test="${not empty flushname}">
		                    <input type="hidden" name="flushname" value="${flushname}" />
		                </c:if>
		                <%--
		                    do we need this?
		                    <input type="hidden" name="nodenr" value="${nodenr}" />
		                --%>
		                <%--
		                    when there are no editable fields, don't show these.
		                    we don't omit the whole button infrastructure, becouse perhaps the
		                    editor adds it's own buttons (like 'preview' or so)
		                    TODO a more generic approach to adding buttons to the forms. perhaps wrap it in a tag on it's own.
		                --%>
		                <c:if test="${not empty isedit && isedit > 0}">
		                    <input id="save" class="save" type="submit" value="bewaar" />
		                    <input id="annuleren" onclick="document.location = document.location;" class="cancel" type="reset" value="annuleer" />
		                </c:if>
		            </div>
		            <c:if test="${not empty isedit && isedit > 0}">
		                <script type="text/javascript">
		                    // Standaard save & annuleerknop uit
		                    var save = document.getElementById('save');
		                    var annuleren = document.getElementById('annuleren');
		                    var enableButtons = document.getElementById('enableButtons');
		                    enableButtons.style.display = 'block';
		                    enableButtons.style.left = getleft(save)+'px';
		                    enableButtons.style.top = gettop(save)+'px';
		                    enableButtons.style.height = save.offsetHeight+'px';
		                    enableButtons.style.width = annuleren.offsetLeft - save.offsetLeft + annuleren.offsetWidth + 'px';
		                </script>
		            </c:if>
		    </form>
	    </c:when>
	    <c:otherwise>
	       <h3>Verboden</h3>
	       <p>Je hebt geen rechten om
	       <c:choose>
	           <c:when test="${modifier == 'create'}">een object van type ${nodetype} aan te maken.</c:when>
	           <c:otherwise>dit object te bewerken.</c:otherwise>
           </c:choose>
	    </c:otherwise>
    </c:choose>		    
</div>