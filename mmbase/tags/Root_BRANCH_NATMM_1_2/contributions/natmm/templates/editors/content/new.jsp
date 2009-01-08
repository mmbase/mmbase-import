<%@page import="nl.leocms.authorization.*,org.mmbase.bridge.*,nl.leocms.content.*, nl.leocms.util.ContentTypeHelper" %>
<%@include file="/taglibs.jsp" %>
<html>
<head>
<title>Maak nieuwe content aan</title>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
<script language="javascript" src="../../js/gen_utils.js">
</script>
</head>
<body>
<mm:cloud jspvar="cloud" rank="basic user">
<% String allowedTypes = request.getParameter("allowedTypes"); 
   String refreshFrame = request.getParameter("refreshFrame");      
   String refreshFrameJs = "";
   if (refreshFrame != null && !"".equals(refreshFrame)) {
      refreshFrameJs += "&refreshframe=";
      refreshFrameJs += refreshFrame;
   }
   else {
      refreshFrameJs = "&refreshframe=bottompane";
   }
   String returnUrl = request.getParameter("returnUrl");
   String rubriekNumber = request.getParameter("rubriekNumber");
   boolean predefinedRubriek = false;
   if (rubriekNumber!=null && !"".equals(rubriekNumber)) {
      predefinedRubriek = true;
   }
   %>
<h1>Nieuwe content</h1>
<form action='../WizardInitAction.eb'>
<input type="hidden" name="rubriek" <% if (predefinedRubriek) { %>value="<%=rubriekNumber%>"<% } %>/>
<input type="hidden" name="action" value='create'/>
<input type="hidden" name="returnurl" value="<%=returnUrl%>">
<table>
   <% boolean filterOnAllowedTypes = false;
      if (allowedTypes!=null && !"".equals(allowedTypes)) {
         if (ContentTypeHelper.isContentType(allowedTypes)) {
            filterOnAllowedTypes = true;      
         }
         else {
            throw new JspException("Unknown content type " + allowedTypes + " passed as allowedTypes parameter.");
         }
      } %>
    <tr><td class="fieldname">Type</td>
        <td><select name="contenttype">
         <% if (!filterOnAllowedTypes || filterOnAllowedTypes && (allowedTypes.indexOf(ContentTypeHelper.ARTIKEL) > -1)) {%>
            <option value='<%=ContentTypeHelper.ARTIKEL%>'>Artikel</option>
         <% } %>
         <% if (!filterOnAllowedTypes || filterOnAllowedTypes && (allowedTypes.indexOf(ContentTypeHelper.VGV) > -1)) {%>
            <option value='<%=ContentTypeHelper.VGV%>'>Veelgestelde vraag</option>
         <% } %>         
         <% if (!filterOnAllowedTypes || filterOnAllowedTypes && (allowedTypes.indexOf(ContentTypeHelper.PROVINCIES) > -1)) {%>
            <option value='<%=ContentTypeHelper.PROVINCIES%>'>Provincie</option>
         <% } %>
         <% if (!filterOnAllowedTypes || filterOnAllowedTypes && (allowedTypes.indexOf(ContentTypeHelper.ORGANISATIE) > -1)) {%>
            <option value='<%=ContentTypeHelper.ORGANISATIE%>'>Organisatie</option>
         <% } %>         
         <% if (!filterOnAllowedTypes || filterOnAllowedTypes && (allowedTypes.indexOf(ContentTypeHelper.PERSOON) > -1)) {%>
            <option value='<%=ContentTypeHelper.PERSOON%>'>Persoon</option>
         <% } %>         
         <% if (!filterOnAllowedTypes || filterOnAllowedTypes && (allowedTypes.indexOf(ContentTypeHelper.AFBEELDING) > -1)) {%>
            <option value='<%=ContentTypeHelper.AFBEELDING%>'>Afbeelding</option>
         <% } %>
         <% if (!filterOnAllowedTypes || filterOnAllowedTypes && (allowedTypes.indexOf(ContentTypeHelper.BIJLAGE) > -1)) {%>
            <option value='<%=ContentTypeHelper.BIJLAGE%>'>Bijlage</option>
         <% } %>       
         <% if (!filterOnAllowedTypes || filterOnAllowedTypes && (allowedTypes.indexOf(ContentTypeHelper.LINK) > -1)) {%>
            <option value='<%=ContentTypeHelper.LINK%>'>Link</option>
         <% } %>
         <% if (!filterOnAllowedTypes || filterOnAllowedTypes && (allowedTypes.indexOf(ContentTypeHelper.PANNO) > -1)) {%>
            <option value='<%=ContentTypeHelper.PANNO%>'>Pano</option>
         <% } %>
         <% if (!filterOnAllowedTypes || filterOnAllowedTypes && (allowedTypes.indexOf(ContentTypeHelper.SHORTY) > -1)) {%>       
            <option value='<%=ContentTypeHelper.SHORTY%>'>Shorty</option>
         <% } %>
         <% if (!filterOnAllowedTypes || filterOnAllowedTypes && (allowedTypes.indexOf(ContentTypeHelper.TEASER) > -1)) {%>       
            <option value='<%=ContentTypeHelper.TEASER%>'>Teaser</option>
         <% } %>
         <% if (!filterOnAllowedTypes || filterOnAllowedTypes && (allowedTypes.indexOf(ContentTypeHelper.PARAGRAAF) > -1)) {%>       
            <option value='<%=ContentTypeHelper.PARAGRAAF%>'>Paragraaf</option>
         <% } %>
            </select>
        </td>
    </tr>
    <tr>
    <td class="fieldname">Rubriek</td>
    <% if (!predefinedRubriek) { %>
    <td><input type='text' maxlength='30' name='rubrieknaam' disabled='true'><a href="../rubrieken/selector/rubriek_selector.jsp?refreshtarget=setRubriek&mode=js<%=refreshFrameJs%>" onclick="openPopupWindow('selector',350,500)" target='selector'><img src="../img/rubriek.gif" border='0'/></a>
</td>
   <% }
      else { %>
         <mm:node number="<%=rubriekNumber%>">
         <td><input type='text' maxlength='30' name='rubrieknaam' value='<mm:field name="naam" write="true"/>' disabled='true'></td>
         </mm:node>
   <% } %>
    </tr>
    <tr><td>&nbsp;</td><td><input type="submit" name="submitButton" value="Maak object" <% if (!predefinedRubriek) {%>disabled="true"<% } %>/></td></tr>
</table>
</form>
<script>
    function setRubriek(nummer,naam) {
        document.forms[0].rubrieknaam.value=naam;
        document.forms[0].rubriek.value=nummer;
        document.forms[0].submitButton.disabled = false;
    }
</script>
</mm:cloud>
</body>