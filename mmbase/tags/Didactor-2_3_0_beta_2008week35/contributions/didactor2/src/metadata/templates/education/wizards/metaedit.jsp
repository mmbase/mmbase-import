<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@page contentType="text/html;charset=UTF-8"
%><%@page import = "java.util.*" %>
<%@page import = "java.text.SimpleDateFormat" %>

<%@page import = "org.mmbase.bridge.*" %>
<%@page import = "nl.didactor.metadata.util.MetaDataHelper" %>

<%@page import = "nl.didactor.component.metadata.constraints.Constraint" %>
<%@page import = "nl.didactor.component.metadata.constraints.Error" %>

<html>
   <head>
     <title>Metadata</title>
   </head>
   <body style="padding-left:10px">
   <%
      // MetaDataHelper.log(request,"metaedit.jsp");

      String sNode = request.getParameter("number");
      String sRequest_Submitted = request.getParameter("submitted");
      String sRequest_DoCloseMetaeditor = request.getParameter("close");
      String sRequest_SetDefaults = request.getParameter("set_defaults");

      if (sRequest_Submitted == null) {
         //Empty form
         if (sRequest_SetDefaults != null) {
            %>
               <jsp:include page="metaedit_form.jsp">
                  <jsp:param name="number" value="<%= sNode %>" />
                  <jsp:param name="set_defaults" value="true" />
               </jsp:include>
            <%
         }
         else {
            %>
               <jsp:include page="metaedit_form.jsp">
                  <jsp:param name="number" value="<%= sNode %>" />
               </jsp:include>
            <%
         }
      }
      else {
         //Submit has been pressed

         //Remove cached constraints
         application.removeAttribute(MetaDataHelper.APPLICATION_CONSTRAINTS_INDEX_KEY);

         //------------------------------- Save form -------------------------------
         %>
	 
	 <mm:cloud method="delegate" jspvar="cloud">
	   <%@include file="/shared/setImports.jsp" %>

               <%
                  Node nodeUser = nl.didactor.security.Authentication.getCurrentUserNode(cloud);
                  Node nodeObject = cloud.getNode(sNode);

                  //All parameters from the form
                  Enumeration enumParamNames = request.getParameterNames();

                  HashSet nlPassedNodes = new HashSet();


                  //---------------- Process parameters and store values ---------------
                  if(sRequest_Submitted.equals("add")) {

                     // We have got "add lang string" command, it creates a new metalangstring

                     Node mNode = MetaDataHelper.getMetadataNode(cloud,sNode,request.getParameter("add"),false);
                     String sMetadataID = mNode.getStringValue("number");

                     %>
                     <mm:remove referid="lang_id" />
                     <mm:remove referid="metadata_id" />
                     <mm:createnode type="metalangstring" id="lang_id"/>
                     <mm:node number="<%= sMetadataID %>" id="metadata_id" />
                     <mm:createrelation source="metadata_id" destination="lang_id" role="posrel">
                        <mm:setfield name="pos">-1</mm:setfield>
                     </mm:createrelation>
                     <%

                  }
                  else {

                     enumParamNames = request.getParameterNames();
                     while(enumParamNames.hasMoreElements()) {
                        // Parse all parameters from http-request
                        String sParameter = (String) enumParamNames.nextElement();
                        String[] arrstrParameters = request.getParameterValues(sParameter);

                        if(sParameter.charAt(0) == 'm') {

                           String sMetadefID = sParameter.substring(1);
                           Node metadataNode = MetaDataHelper.getMetadataNode(cloud,sNode,sMetadefID,false);

                           // Add this node to the "passed" list, we shouldn't erase values from it in future
                           nlPassedNodes.add(metadataNode);

                           int skipParameter = -1;
                           if(sRequest_Submitted.equals("remove")){

                              // if we have got "remove" command,
                              // we should skip processing of the langstring defined by the add parameter
                              String[] sTarget = request.getParameter("add").split("\\,");
                              if (sMetadefID.equals(sTarget[0])) {
                                 try {
                                    skipParameter = Integer.parseInt(sTarget[1]);
                                 } catch (Exception e ){

                                 }
                              }
                           }
                           MetaDataHelper.setMetadataNode(cloud,arrstrParameters,metadataNode,sMetadefID,skipParameter);
                        }
                     }

                     // ------------ Remove nodes which are not passed by this form ---------------

                     %>
                     <mm:node number="<%= sNode %>">
                        <mm:relatednodes type="metadata" jspvar="metadataNode">
                           <%
                           if(!nlPassedNodes.contains(metadataNode)) {
                              %>
                              <mm:related path="posrel,metavocabulary">
                                 <mm:deletenode element="posrel" />
                              </mm:related>
                              <mm:relatednodes type="metadate">
                                 <mm:deletenode deleterelations="true"/>
                              </mm:relatednodes>
                              <mm:relatednodes type="metalangstring">
                                 <mm:deletenode deleterelations="true"/>
                              </mm:relatednodes>
                              <%
                           }
                           %>
                        </mm:relatednodes>
                     </mm:node>
                     <%
                  }



                  //------------------------------- Check form -------------------------------

                  //List of metadefinitions that must be checked
                  HashMap hashmapConstraints = MetaDataHelper.getCachedConstraints(nodeObject, application);



                  if( !sRequest_Submitted.equals("add") && !sRequest_Submitted.equals("remove")
                     && sRequest_SetDefaults == null) {
                     //modes "add" & "remove" don't call the form checker, they only store values


                     //Global list of errors
                     ArrayList arliErrors = new ArrayList();


                     //Go through all constraints
                     for(Iterator it = hashmapConstraints.keySet().iterator(); it.hasNext();){
                         Node nodeMetaDefinition = (Node) it.next();

/*
                         System.out.println(nodeMetaDefinition.getNumber() + "-------------" + ((Constraint) hashmapConstraints.get(nodeMetaDefinition)).getType());
                         if(((Constraint) hashmapConstraints.get(nodeMetaDefinition)).getConstraintsChain() != null){
                            System.out.println(nodeMetaDefinition.getNumber() + "-size--------" + ((Constraint) hashmapConstraints.get(nodeMetaDefinition)).getConstraintsChain().size());
                         }
                         else{
                            System.out.println(nodeMetaDefinition.getNumber() + "-size--------null");
                         }
*/

                         String[] arrstrParameters = request.getParameterValues("m" + nodeMetaDefinition.getNumber());
                         if(arrstrParameters == null){
                             arrstrParameters = new String[0];
                         }

                         ArrayList arliThisStepErrors = MetaDataHelper.hasValidMetadata(nodeMetaDefinition, (Constraint) hashmapConstraints.get(nodeMetaDefinition), arrstrParameters);

                         if(arliThisStepErrors.size() != 0){
                             //add these errors to the global list
                             arliErrors.addAll(arliThisStepErrors);
                         }
                     }
                     %>
                     <%@include file="metaedit_header.jsp" %>
                     <br/>
                     <%

                     ArrayList arliGroupConstraintsErrors = MetaDataHelper.checkGroupConstraints(nodeObject, application, "nl");


                     //Header, if error
                     //arliErrors contains all errors
                     //System.out.println("errors=" + arliErrors.size());
                     if((arliErrors.size() > 0) || (arliGroupConstraintsErrors.size() > 0))
                     {
                        %>
                        <style type="text/css">
                           body{
                              font-family:arial;
                              font-size:12px;
                           }
                        </style>

                        <font style="color:red; font-size:11px; font-weight:bold; text-decoration:none; letter-spacing:1px;"><font style="font-size:15px">O</font>ONTBREKENDE VERPLICHTE VELDEN!</font>
                        <br/>
                        <%

                        for(Iterator it = arliErrors.iterator(); it.hasNext();){
                            Error error = (Error) it.next();
                            %> <%= error.getErrorReport() %> <br/> <%
                        }

                        for(Iterator it = arliGroupConstraintsErrors.iterator(); it.hasNext();){
                            String sError = (String) it.next();
                            %> <%= sError %> <br/> <%
                        }
                        %>
                           <a href="metaedit.jsp?number=<%= sNode %>&random=<%= (new Date()).getTime() %>"><font style="color:red; font-weight:bold; text-decoration:none"><di:translate key="metadata.back_to_metaeditform" /></font></a>
                           <script>
                              try
                              {
                                 top.document.getElementById('img_<%= sNode %>').src = 'gfx/metaerror.gif';
                              }
                              catch(err)
                              {
                              }
                           </script>
                        <%
                     }
                     else{

                        if(session.getAttribute("show_metadata_in_list") == null) {
                           //We use metaeditor from content_metadata or not?
                           %>
                           <di:translate key="metadata.metadata_is_saved" />.
                           <script>
                              try
                              {
                                 top.document.getElementById('img_<%= sNode %>').src = 'gfx/metavalid.gif';
                              }
                              catch(err)
                              {
                              }
                              window.setInterval("document.location.href='metaedit.jsp?number=<%= sNode %>&random=<%= (new Date()).getTime()%>;'", 3000);
                           </script>
                           <br/><br/>
                           <a href="metaedit.jsp?number=<%= sNode %>&random=<%= (new Date()).getTime() %>"><font style="color:red; font-weight:bold; text-decoration:none"><di:translate key="metadata.back_to_metaeditform" /></font></a>
                           <%

                        } else {

                           if ((sRequest_DoCloseMetaeditor != null) && (sRequest_DoCloseMetaeditor.equals("yes"))) {

                              //User has selected "SAVE&CLOSE"
                              response.sendRedirect((String) session.getAttribute("metalist_url"));

                           } else {

                              response.sendRedirect("metaedit.jsp?number=" + sNode + "&random=" + (new Date()).getTime());
                           }
                        }
                     }
                  }

                  //If we set only defaults values, always redirect
                  if((sRequest_SetDefaults != null)
                        && (!sRequest_Submitted.equals("add"))
                        && (!sRequest_Submitted.equals("remove"))) {
                        %>
                        <%@include file="metaedit_header.jsp" %>
                        <br/>
                        <di:translate key="metadata.default_metadatavalues_are_saved" />.
                        <script>
                           window.setInterval("document.location.href='metaedit.jsp?number=<%= sNode %>&random=<%= (new Date()).getTime()%>&set_defaults=true;'", 3000);
                        </script>
                        <br/><br/>
                        <a href="metaedit.jsp?number=<%= sNode %>&random=<%= (new Date()).getTime() %>"><font style="color:red; font-weight:bold; text-decoration:none"><di:translate key="metadata.back_to_metaeditform" /></font></a>
                        <%
                  }
                  if((sRequest_Submitted.equals("add")) || (sRequest_Submitted.equals("remove"))) {
                  %>
                     <jsp:include page="metaedit_form.jsp" flush="true">
                        <jsp:param name="number" value="<%= sNode %>" />
                     </jsp:include>
                  <%
                  }
                  // We have to update metadate.value field (it is handled by metadata builder)
               %>
               <mm:node number="<%= sNode %>">
                  <mm:relatednodes type="metadata">
                     <mm:setfield name="value">-</mm:setfield>
                  </mm:relatednodes>
               </mm:node>
            </mm:cloud>
         <%
      }
   %>
   </body>
</html>
