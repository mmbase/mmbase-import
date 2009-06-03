<%@ tag body-content="empty"  %>
<%@ taglib prefix="mm" uri="http://www.mmbase.org/mmbase-taglib-1.0"  %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/vpro-wizards/form" %>

<%@ attribute name="dateFormat" %>
<%@ attribute name="timeFormat" %>
<%@ attribute name="edit" type="java.lang.Boolean" description="when false the date/time is only displayed. When true the create attribute is ignored. Default is true" %>
<%@ attribute name="create" type="java.lang.Boolean" description="when true a formfield added for new nodes. when edit is false this will be a hidden field. Default is true" %>

<%@ attribute name="type" description="can be [date|time|datetime]. default: datetime. Edit only date or time"  %>
<%-- defaultvalue: use 'now' to insert the current time for new nodes--%>

<c:if test="${empty edit}"><c:set var="edit" value="true" /></c:if>
<c:if test="${empty create}"><c:set var="create" value="true" /></c:if>
<c:if test="${empty type || ( type != 'date' && type != 'time' && type != 'datetime')}"><c:set var="type" value="datetime" /></c:if>
<%--
<c:if test="${empty dateFormat}"><c:set var="dateFormat" value="d-M-yyyy" /></c:if>
--%>
<%@ include file="fieldinit.tagf" %>

<c:set var="_action" value="actions[${modifier}${action}][${actionnr}].dateFields[${field}]" />

<c:if test="${empty timeFormat}"><c:set var="timeFormat" value="HH:mm" /></c:if>
<c:if test="${empty dateFormat}"><c:set var="dateFormat" value="dd-MM-yyyy" /></c:if>


<%-- when fieldvalue is  = 'now' (from defaultvalue)--%>
<c:if test="${fieldvalue == 'now'}">
    <c:set var="fieldvalue" ><mm:time time="now"/></c:set>
</c:if>

<%-- parse the date into a date and time string--%>
<c:if test="${not empty fieldvalue}">
    <mm:import id="fieldvalue" reset="true">${fieldvalue}</mm:import>
    <c:set var="_date" ><mm:write referid="fieldvalue"><mm:time format="${dateFormat}"/></mm:write></c:set>
    <c:set var="_time" ><mm:write referid="fieldvalue"><mm:time format="${timeFormat}"/></mm:write></c:set>
</c:if>

<div class="inputBlock">
    <div class="mmevent">
        <div class="fieldName">${fieldname}</div>
        <div class="fieldValue">
            <c:choose>
                <c:when test="${edit == true}">
                    <c:if test="${fn:contains(type, 'date')}">
                        <input type="text" onchange="disableRelated();" name="${_action}.date" id="date_${field}" value="${_date}" size="${fn:length(dateFormat)}" id="field_${nodetype}_${field}">
                        <input type="hidden" name="${_action}.dateFormat" value="${dateFormat}" />
                    </c:if>
                    <c:if test="${fn:contains(type, 'time')}">
                        <input type="text" onchange="disablebRelated();" name="${_action}.time" id="time_${field}" value="${_time}" size="${fn:length(timeFormat)}" id="field_${nodetype}_${field}">
                        <input type="hidden" name="${_action}.timeFormat" value="${timeFormat}" />
                    </c:if>
                </c:when>
                <c:otherwise>

                    <%--show the date / time without a form field--%>
                    <c:choose>
                        <c:when test="${type == 'datetime'}">
                            <mm:time time="${fieldvalue}" format="${dateFormat} ${timeFormat}"/>
                        </c:when>
                        <c:when test="${type == 'date'}">
                            <mm:time time="${fieldvalue}" format="${dateFormat}"/>
                        </c:when>
                        <c:when test="${type == 'time'}">
                            <mm:time time="${fieldvalue}" format="${timeFormat}"/>
                        </c:when>
                    </c:choose>

                    <%--when create is true and this is a new node: do the hidden input fields--%>
                    <c:choose>
                        <c:when test="${create == true && fn:startsWith(_action, 'create')}">
                            <c:if test="${fn:contains(type, 'date')}">
                                <input type="hidden" name="${_action}.date" id="date_${field}" value="${_date}">
                                <input type="hidden" name="${_action}.dateFormat" value="${dateFormat}" />
                            </c:if>
                            <c:if test="${fn:contains(type, 'time')}">
                                <input type="hidden" name="${_action}.time" value="${_time}">
                                <input type="hidden" name="${_action}.timeFormat" value="${timeFormat}" />
                            </c:if>                        
                        </c:when>
                        <c:otherwise>
                            <%-- when edit is false, and create is false or _action is not of type create, this field is read only --%>
                            <form:noedit/>
                        </c:otherwise>
                    </c:choose>

                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<c:if test="${edit == true}">
<!--  we have to add the calendar(s) and time picker(s) after the document has completely loaded, or ie(6) will crash horribly!!-->
    <script language="javascript">
            if(window.addEventListener){
                window.addEventListener("load",
                    function(){
                        calendar.create("dmy");
                        calendar.attach("date_${field}");
                        calendar.onclick = function(){disableRelated()}
                        clock.create(15);
                        clock.attach("time_${field}");
                        clock.onclick = function(){disableRelated()}
                    },
                false);
            }else if(window.attachEvent){
                window.attachEvent('onload',
                    function (){
                        calendar.create("dmy");
                        calendar.attach("date_${field}");
                        calendar.onclick = function(){disableRelated()}
                        clock.create(15);
                        clock.attach("time_${field}");
                        clock.onclick = function(){disableRelated()}
                    });
            }
            function www(){ alert('hi windows');}
    </script>
</c:if>
