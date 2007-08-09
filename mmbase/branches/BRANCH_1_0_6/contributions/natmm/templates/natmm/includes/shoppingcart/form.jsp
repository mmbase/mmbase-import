<%@include file="/taglibs.jsp" %>
<%@include file="../request_parameters.jsp" %>
<%@include file="vars.jsp" %>
<mm:cloud jspvar="cloud">
<%
String styleSheet = request.getParameter("rs");
PaginaHelper ph = new PaginaHelper(cloud);  
String scriptStr = "";
%>
<table width="100%" cellspacing="0" cellpadding="0">
<tr>
	<td width="15%"><img src="media/trans.gif" width="1" height="1" border="0" alt=""></td>
	<td style="width:65%;vertical-align:top;padding-top:11px;">
    <mm:list nodes="<%= paginaID %>" path="pagina,contentrel,artikel" constraints="contentrel.pos='3'"
      ><mm:field name="artikel.number" jspvar="artikel_number" vartype="String" write="false"
        ><jsp:include page="../artikel_1_column.jsp">
           <jsp:param name="o" value="<%= artikel_number %>" />
           <jsp:param name="r" value="<%= rubriekID %>" />
           <jsp:param name="rs" value="<%= styleSheet %>" />
         </jsp:include
       ></mm:field> 
    </mm:list>
    <% 
    // **************** total costs ****************************
    offsetID = (String) session.getAttribute("totalcosts"); 
    if(offsetID !=null) { 
    %><img src="media/trans.gif" width="1" height="11" border="0" alt=""><br/>
    <table width="100%" cellspacing="0" cellpadding="0">
      <tr><td class="maincolor" colspan="2"><img src="media/trans.gif" height="1" width="1" border="0" alt=""></td></tr>
      <tr>
        <td class="carteven"  style="text-align:left;">
            <span style="font-size:12px;font-weight:bold;"><bean:message bundle="LEOCMS" key="shoppingcart.total_costs" /></span>
        </td>
        <td class="carteven" style="font-size:12px;font-weight:bold;padding-right:5px;text-align:right;"><%
        if(!offsetID.equals("-1")) { 
          %>&euro;&nbsp;<%= nf.format(((double) Integer.parseInt(offsetID))/100) %><%
        } else {
          %>nog onbekend<% 
        } %></td>
      </tr>
      <tr><td class="maincolor" colspan="2"><img src="media/trans.gif" height="1" width="1" border="0" alt=""></td></tr>
    </table>
    <br/><br/>
    <%
    } 
    // **************** input fields ****************************
    String answerValue = answerValue = (String)  session.getAttribute("qg");  if(answerValue==null) { answerValue = ""; }
    %>
    <table width="100%" cellspacing="0" cellpadding="0">
      <form name="formulier" method="post" action="javascript:changeIt('<mm:url page="<%= 
          ph.createPaginaUrl("bestel",request.getContextPath()) + "?t=send&pst=" %>" />');">
      <tr>
        <td class="maincolor" style="width:40%;vertical-align:middle;background-color:#5D5D5D;">
        <input type="radio" name="gender" value="m" <% if(answerValue.equals("m")) { %>CHECKED<% } %>>Dhr
        <input type="radio" name="gender" value="f" <% if(answerValue.equals("f")) { %>CHECKED<% } %>>Mevr</td>
        <td style="width:60%;height:17px;"><img src="media/trans.gif" width="1" height="1" border="0" alt=""></td>
      </tr><%
      String [] fields = { "Naam", "Adres+huisnr.", "Postcode", "Woonplaats", "Telefoon", "E-mail adres" };
      for(int i=0; i<fields.length; i++) {
        answerValue = (String) session.getAttribute("q" + i); if(answerValue==null) { answerValue = ""; }
        %>
        <tr><td colspan="2"><img src="media/trans.gif" height="7" width="1" border="0" alt=""></td></tr>
        <tr>
          <td class="maincolor" style="width:40%;vertical-align:middle;padding-left:4px;background-color:#5D5D5D;">
            <%= fields[i] + (i!=4? "*": "" ) %>
          </td>
          <td class="maincolor" style="width:60%;vertical-align:middle;text-align:right;padding-right:1px;background-color:#5D5D5D;">
            <input type="text" name="q<%= i %>" style="width:100%;height:15px;border:0px;" value="<%= answerValue %>"></td>
        </tr><%  
        scriptStr += "var answer = escape(document.formulier.elements[\"q" + i + "\"].value);\n"
              + "href += \"|q" + i + "=\" + answer; \n";
      }
      // **************** obligatory fields text ******************
      %>
      <table width="100%" cellspacing="0" cellpadding="0">
        <tr>
           <td class="creditline" style="text-align:left;">
           <bean:message bundle="LEOCMS" key="shoppingcart.mandatory_fields" /></td>
        </tr>
      </table>
    <%
     // **************** send order ******************
      %>
      <table width="180" cellspacing="0" cellpadding="0" align="right">
        <tr>
        <td class="maincolor" style="vertical-align:middle;padding-left:4px;padding-right:2px;" width="100%">
          <nowrap><a href="javascript:changeIt('<mm:url page="<%= 
              ph.createPaginaUrl("bestel",request.getContextPath()) + "?t=send" %>" />');" class="klikpad">
                <b><bean:message bundle="LEOCMS" key="shoppingcart.send" /></b>
              </a></nowrap></td>
        <td class="maincolor" style="padding:2px;" width="100%">
          <a href="javascript:changeIt('<mm:url page="<%= ph.createPaginaUrl("bestel",request.getContextPath()) + "?t=send" %>" />');"><img src="media/shop/pijl_wit_op_oranje.gif" border="0" alt=""></a></td>
        </tr>
      </form>
    </table>
	</td>
	<td width="8"><img src="media/trans.gif" height="1" width="8" border="0" alt=""></td>
	<td width="180" style="padding:4px;padding-top:14px;vertical-align:top;">
    <% 
    shop_itemHref = ph.createPaginaUrl("bestel",request.getContextPath()) + "?t=view";
    %>
    <a href="javascript:changeIt('<mm:url page="<%= shop_itemHref %>" />');" class="maincolor_link">
      <bean:message bundle="LEOCMS" key="shoppingcart.backtoform" />
    </a> 
    <a href="javascript:changeIt('<mm:url page="<%= shop_itemHref %>" />');"> <img src="media/shop/back.gif" border="0" alt=""></a><br/>
    <img src="media/trans.gif" height="1" width="180" border="0" alt=""><br/>
  </td>
</tr>
</table>
<%-- ********************* create the javascript for posting the values *******************
--%>
<script language="javascript" type="text/javascript">
<%= "<!--" %>
function changeIt(url) {
var href = "&pst=";
var answer = document.formulier.gender;
for (var i=0; i < answer.length; i++){
	if (answer[i].checked) {
		var rad_val = answer[i].value;
		if(rad_val != '') {	href += "|qg=" + rad_val; }
	}
}
<%= scriptStr %>
if(url!=null) {
	document.location =  url + href; 
	return false;
} else {
	return href;
}
}
<%= "//-->" %>
</script>
</mm:cloud>
