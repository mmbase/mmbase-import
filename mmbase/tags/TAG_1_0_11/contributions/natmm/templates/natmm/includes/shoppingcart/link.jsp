<%@include file="/taglibs.jsp" %>
<%@include file="../request_parameters.jsp" %>
<mm:cloud jspvar="cloud">
  <% 
  String actionId = request.getParameter("t"); if (actionId==null) {actionId=""; }
  String totalitemsId = request.getParameter("ti"); if (totalitemsId==null) {totalitemsId=""; }
  
  PaginaHelper ph = new PaginaHelper(cloud);
  String extendedHref = ph.createPaginaUrl("bestel",request.getContextPath()) + "?t=view";
  %>
  <table width="180" cellspacing="0" cellpadding="0">
    <tr>
      <td width="180">
      <table width="180" cellspacing="0" cellpadding="0">
        <tr>
          <td class="maincolor" width="0%" style="padding-left:2px;"><%
            %><a href="<%@include file="../shop/extendedhref.jsp" %>"><img src="media/shop/w_wagentje.gif" border="0" alt=""></a></td>
          <td class="maincolor" width="100%" style="vertical-align:middle;padding-right:2px;padding-left:2px;"><%
             %><nowrap>
                <a href="<%@include file="../shop/extendedhref.jsp"	%>" class="klikpad">
                <b>
                  <%= totalitemsId %> 
                  <% 
                  if(totalitemsId.equals("1")) {
                    %><bean:message bundle="LEOCMS" key="shoppingcart.item" /><%
                    } else {
                    %><bean:message bundle="LEOCMS" key="shoppingcart.items" /><%
                  }
                  %>
                </b>
                </a>
             </nowrap></td>
          <td class="maincolor" style="padding:2px;" width="0%"><a href="<%@include file="../shop/extendedhref.jsp" 
            %>"><img src="media/shop/pijl_wit_op_oranje.gif" border="0" alt=""></a></td>
        </tr>
      </table>
      </td>
    </tr>
    <tr>
      <td width="180" colspan="2"><img src="media/trans.gif" width="180" height="1" border="0" alt=""></td>
    </tr>
    <tr>
      <td class="subtitlebar" width="180" colspan="2"><bean:message bundle="LEOCMS" key="shoppingcart.inthecart" /></td>
    </tr>
  </table>
</mm:cloud>
