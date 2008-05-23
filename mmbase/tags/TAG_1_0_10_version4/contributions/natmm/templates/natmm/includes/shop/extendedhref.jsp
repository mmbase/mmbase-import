<% if(javax.servlet.http.HttpUtils.getRequestURL(request)!=null
      && javax.servlet.http.HttpUtils.getRequestURL(request).indexOf("shoppingcart")>-1
      && !actionId.equals("send")) { 
    %>javascript:changeIt('<mm:url page="<%= extendedHref %>" />');<%
} else {
    %><mm:url page="<%= extendedHref %>" /><%
} %>
