<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<%@page import="java.net.URLEncoder"%>

<%-- 
This page generates javascript that will create an array of banners, this array will be added to a 
global array of carrousels. The banner is just some html code (a flash movie, an image or something else).
The script that displays the carrousel will rotate the banners by dynamically assiging each piece of html
code from the array to a designated div tag. Also parallel arrays containing the intervals and the 
banner positions are generated.
 --%>
<mm:cloud method="asis">
    <mm:import externid="elementId" required="true" from="request" />
    <mm:node number="${elementId}" notfound="skip">
        
        <mm:field name="title" write="false" jspvar="title" />
        <%-- width and height are recommended for flash movies otherwise flickering occurs when changing banners in the carrousel --%>
        <mm:field name="width" write="false" jspvar="width" />
        <mm:field name="height" write="false" jspvar="height" />
        <mm:field name="timeout" write="false" jspvar="interval" />
        
        <c:if test="${empty interval or interval le 0}">
            <c:set var="interval" value="5000"/>
        </c:if>

        <mm:relatednodes type="urls" role="posrel" searchdir="destination">
            <mm:first>
                <mm:field name="url" write="false" jspvar="url" />
            </mm:first>
        </mm:relatednodes>


		<mm:url page="/jsp/bannerRedirectNedstat.jsp" id="bannerRedirect" write="false" />
        <portlet:actionURL var="renderUrl">
            <portlet:param name="elementId" value="${elementId}" />
            <portlet:param name="redirect" value="${bannerRedirect}" />
        </portlet:actionURL>

        <mm:relatednodes type="attachments" role="posrel" searchdir="destination">
            <mm:first>
                <%-- the url to the flashmovie with the click url appended and encoded, we cannot use the attachment and url tags together --%>
                <c:set var="flashUrl">
                	<% String fullUrl = net.sf.mmapps.commons.util.HttpUtil.getServerDocRoot(request);
                		fullUrl += (String) pageContext.getAttribute("renderUrl"); %>
                    <mm:attachment />?clickTAG=<%=URLEncoder.encode(fullUrl)%>
                </c:set>
                    banners.push('<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" id="flashbanner" <c:if test="${width gt 0}">width="${width}"</c:if> <c:if test="${height gt 0}">height="${height}"</c:if> title="Reclame: ${title}">'+
                    '<param name="movie" value="${flashUrl}">'+
                    '<PARAM NAME="WMode" VALUE="opaque">'+ 
                    '<embed src="${flashUrl}" wmode="opaque" type="application/x-shockwave-flash" <c:if test="${width gt 0}">width="${width}"</c:if> <c:if test="${height gt 0}">height="${height}"</c:if> />'+
                    '<\/object>');
                    intervals.push(${interval});
                </mm:first>
        </mm:relatednodes>
        <mm:field name="height" write="false" jspvar="bannerheight" >
        <mm:relatednodes type="images" role="imagerel" searchdir="destination">
            <mm:first>
                    banners.push('<a href="${renderUrl}" target="_blank"> <img src="<mm:image />" <c:if test="${bannerheight != -1}">height="${bannerheight}"</c:if>  alt="<mm:field name="title" />" title="<mm:field name="title" />" /></a>');
                    intervals.push(${interval});
                </mm:first>
        </mm:relatednodes>
        </mm:field>
    </mm:node>
</mm:cloud>

