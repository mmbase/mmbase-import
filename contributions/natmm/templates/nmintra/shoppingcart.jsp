<%@include file="/taglibs.jsp" %>
<mm:cloud logon="admin" pwd="<%= (String) com.finalist.mmbase.util.CloudFactory.getAdminUserCredentials().get("password") %>" method="pagelogon" jspvar="cloud">
<%@include file="includes/templateheader.jsp" %>
<%@include file="includes/calendar.jsp" %>
<%@include file="includes/cacheparams.jsp" %>
<%@include file="includes/header.jsp" %>
<%@include file="includes/shoppingcart/update.jsp" %>
<td colspan="2"><%@include file="includes/pagetitle.jsp" %></td>
</tr>
<tr>
  <td colspan="2" class="transperant" style="text-align:right;">
    <div class="<%= infopageClass %>" id="infopage">
      <%
      
      boolean debug = false;
      String debugEmail = "hangyi@xs4all.nl";
      
      TreeMap shop_items = (TreeMap) session.getAttribute("shop_items"); 
      if(shop_items==null) {
         shop_items = new TreeMap();
         try { session.setAttribute("shop_items",shop_items);
         } catch(Exception e) { } 
      }
      TreeMap shop_itemsIterator = (TreeMap) shop_items.clone();
      
      if(actionId.equals("proceed")) { 
         %><%@include file="includes/shoppingcart/form.jsp" 
         %><%@include file="includes/shoppingcart/script.jsp" %><%
      } else if(actionId.equals("send")&&shop_items.size()>0) {
         %><%@include file="includes/shoppingcart/result.jsp" %><%
      } else {
         %><jsp:include page="includes/shoppingcart/table.jsp" /><%
      } 
      %>
    </div>
  </td>
<%@include file="includes/footer.jsp" %>
</mm:cloud>
