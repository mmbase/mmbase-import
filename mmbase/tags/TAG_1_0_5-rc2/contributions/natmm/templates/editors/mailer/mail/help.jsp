<%@include file="includes/abonnee/top.jsp"%>
<mm:content type="text/html" expires="0">
<mm:cloud name="mmbase">

<% //The name of this page. Used in the sidebar include make an menu-item look active %>
<mm:import id="active">help</mm:import>

<div id="container">

<% // get header %>
<%@include file="includes/abonnee/banner.jsp"%>
<% // get menu %>
<mm:include referids="active" page="includes/abonnee/sidebar.jsp"/>

<%@include file="includes/abonnee/helptext.jsp"%>

<% // get footer %>
<%@include file="includes/abonnee/footer.jsp"%>

</div>
</body>
</html>
</mm:cloud>
</mm:content>
