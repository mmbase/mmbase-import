<%@page import="com.finalist.tree.*,nl.leocms.pagina.*,nl.leocms.authorization.*, nl.leocms.rubrieken.*,nl.leocms.rubrieken.RubriekTreeModel" %>
<%@include file="/taglibs.jsp" %>
<html>
<head>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
<title>Rubrieken-selector</title>
<script language="Javascript1.2">
   /// selector js functions
   var debug = false;

   function setDebug(b) {
      debug = b;
   }

  /** */
   function refreshOpenerAndClose(refreshpage, rubrieknumber) {
      newLocation = refreshpage + '?rubrieknumber='+rubrieknumber;
      callStr = "opener.top.location='"+newLocation+"'";
      if (debug) {
         alert(callStr);
      }
      eval(callStr);
      window.close();
   }

   /** */
   function refreshOpenerFrameAndClose(refreshpage, refreshframe, rubrieknumber) {
      newLocation = refreshpage + '?rubrieknumber='+rubrieknumber;
      callStr = "opener.top." + refreshframe + ".location='"+newLocation+"'";
      if (debug) {
         alert(callStr);
      }
      eval(callStr);
      window.close();
   }

   /** */
   function callJsFunctionInFrame(functionname, frame, rubrieknumber, rubriekname) {
      callStr = "opener.top." + frame + "." + functionname + "('" + rubrieknumber + "', '" + rubriekname + "')";
      if (debug) {
         alert(callStr);
      }
      eval(callStr);
      window.close();
   }

   /** */
   function callJsFunction(functionname, rubrieknumber, rubriekname) {
      callStr = "opener.top." + functionname + "(" + rubrieknumber + ", '" + rubriekname + "')";
      if (debug) {
         alert(callStr);
      }
      eval(callStr);
      window.close();
   }
</script>
</head>
<body style="overflow: auto">
<mm:cloud jspvar="cloud" rank="basic user" method='http'>

<%
   String target = request.getParameter("refreshtarget");
   String targetFrame= request.getParameter("refreshframe");
   if (target==null || "".equals(target)) {
       target = "#";
   }
   String modus = request.getParameter("mode");
   boolean jsModus = "js".equals(modus);
    //
    RubriekTreeModel model = new RubriekTreeModel (cloud);
    HTMLTree t=new HTMLTree(model);
    String account=cloud.getUser().getIdentifier();
    AuthorizationHelper helper= new AuthorizationHelper(cloud);
    java.util.Map roles=helper.getRolesForUser( helper.getUserNode(account));
    t.setCellRenderer(new RubriekSelectorRenderer( model, roles, target, targetFrame, jsModus) );
    t.setExpandAll(true);
    t.setImgBaseUrl("../../img/");
    t.render( out );
%>
</mm:cloud>
</body>
