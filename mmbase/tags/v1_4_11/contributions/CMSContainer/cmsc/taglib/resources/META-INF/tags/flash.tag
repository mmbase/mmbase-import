<%@ attribute name="id" required="true" %>
<%@ attribute name="movie" required="true" %>
<%@ attribute name="width" required="true" %>
<%@ attribute name="height" required="true" %>
<%@ attribute name="majorversion" required="false" %>
<%@ attribute name="build" required="false" %>
<%@ attribute name="wmode" required="false" %>
<%@ attribute name="flashvars" required="false" %>
<%@ attribute name="base" required="false" %>
<%@ attribute name="quality" required="false" %>
<%@ attribute name="scale" required="false" %>
<%@ attribute name="align" required="false" %>
<%@ attribute name="salign" required="false" %>
<%@ attribute name="bgcolor" required="false" %>
<%@ attribute name="play" required="false" %>
<%@ attribute name="loop" required="false" %>
<%@ attribute name="menu" required="false" %>
<%@ attribute name="swliveconnect" required="false" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"
%><%@taglib uri="http://finalist.com/cmsc" prefix="cmsc" %>
<c:if test="${empty majorversion}"><c:set var="majorversion">6</c:set></c:if>
<c:if test="${empty build}"><c:set var="build">40</c:set></c:if>
<c:if test="${empty wmode}"><c:set var="wmode">transparent</c:set></c:if>
<div id="${id}" class="flashcontent">
	<jsp:doBody/>
</div>
<script type="text/javascript">
//<![CDATA[
  var ${id} =  { movie: "${movie}", width: "${width}", height: "${height}",
				<c:if test="${not empty flashvars}">flashvars: "${flashvars}",</c:if>   
				<c:if test="${not empty base}">base: "${base}",</c:if>
				<c:if test="${not empty quality}">quality: "${quality}",</c:if>
				<c:if test="${not empty scale}">scale: "${scale}",</c:if>
				<c:if test="${not empty align}">align: "${align}",</c:if>
				<c:if test="${not empty salign}">salign: "${salign}",</c:if>
				<c:if test="${not empty bgcolor}">bgcolor: "${bgcolor}",</c:if>
				<c:if test="${not empty play}">play: "${play}",</c:if>
				<c:if test="${not empty loop}">loop: "${loop}",</c:if>
				<c:if test="${not empty menu}">menu: "${menu}",</c:if>
				<c:if test="${not empty swliveconnect}">swliveconnect: "${swliveconnect}",</c:if>
                 majorversion: "${majorversion}", build: "${build}",= wmode: "${wmode}"};
  UFO.create( ${id}, "${id}" );
//]]>
</script>