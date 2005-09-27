<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<%@page import = "java.util.Date" %>
<%@page import = "java.util.HashSet" %>
<%@page import = "java.util.ArrayList" %>
<%@page import = "java.util.Enumeration" %>
<%@page import = "java.util.Iterator" %>
<%@page import = "java.text.SimpleDateFormat" %>

<html>
   <head>
   <script>
   function toggleDiv(thisDiv)
   {  var style2 = null;
      if (document.getElementById)
      {
         // this is the way the standards work
         style2 = document.getElementById(thisDiv).style;
      }
      else if (document.all)
      {
         // this is the way old msie versions work
         style2 = document.all[thisDiv].style;
      }
      else if (document.layers)
      {
         // this is the way nn4 works
         style2 = document.layers[thisDiv].style;
      }
      if(style2.display=="block" ) {
         style2.display="none";
      } else {
         style2.display="block";
      }
   }
   </script>
   <head>

   <body style="padding-left:10px">

      <%
         String sNode = request.getParameter("number");
         String EMPTY_VALUE = "...";


         String sRequest_Submitted = request.getParameter("submitted");
         String sRequest_DoCloseMetaeditor = request.getParameter("close");

         if (sRequest_Submitted == null)
         {//Empty form
            if (request.getParameter("set_defaults") != null)
            {


               %>
                  <jsp:include page="metaedit_form.jsp">
                     <jsp:param name="node" value="<%= sNode %>" />
                     <jsp:param name="set_defaults" value="true" />
                  </jsp:include>
               <%
            }
            else
            {



               %>
                  <jsp:include page="metaedit_form.jsp">
                     <jsp:param name="node" value="<%= sNode %>" />
                  </jsp:include>
               <%
            }
         }
         else
         {
            //Submit has been pressed
            //------------------------------- Check form -------------------------------
            HashSet hsetPassedNodes = new HashSet();
            HashSet hsetHaveToBeNotEmpty = new HashSet();
            HashSet hsetAssignedMetadefinitions = new HashSet();
            HashSet hsetAssignedVocabularies = new HashSet();

            Enumeration enumParamNames;
            ArrayList arlistParVals = new ArrayList();
            ArrayList arliConstraintErrors = new ArrayList();
            ArrayList arliRelVocVals = new ArrayList();
            ArrayList arliRelVocNumbers = new ArrayList();

            boolean bFillOk = true;
            boolean bSizeOk = true;
            boolean bConstraintOk = true;

            %>
               <mm:content postprocessor="reducespace">
                  <mm:cloud>
                   <%@include file="metaedit_standard_init.jsp" %>
                   <%

                      if ((!sRequest_Submitted.equals("add")) && (!sRequest_Submitted.equals("remove")) &&
                          (request.getParameter("set_defaults") == null)) {
                         %>
                         <%@include file="metaedit_processparams.jsp" %>
                         <%
                      } // end of if .....

                      //If we set only defaults values, always redirect
                      if((request.getParameter("set_defaults") != null) && (!sRequest_Submitted.equals("add")) && (!sRequest_Submitted.equals("remove"))){
                            String sParList = "";

                                 enumParamNames = request.getParameterNames();

                                 while(enumParamNames.hasMoreElements()){
                                      String sParameter = (String) enumParamNames.nextElement();
                                      String[] arrstrParameters = request.getParameterValues(sParameter);

                                      if(sParameter.charAt(0) == 'm'){
                                                for(int i=0; i < arrstrParameters.length; i++){
                                                     sParList += "&" + sParameter + "=" + arrstrParameters[i] ;
                                                }

                                       }// end of if(sParameter.charAt(0) == 'm')

                                 } // end of while

                                 %>

                                 <%@include file="metaedit_header.jsp" %>
                                 <br/>
                                 Standaard metadata waarden zijn opgeslagen.
                                 <script>
                                    window.setInterval("document.location.href='metaedit.jsp?number=<%= sNode %>&random=<%= (new Date()).getTime()%>&set_defaults=true<%=sParList%>;'", 3000);
                                 </script>
                                 <br/><br/>
                                 <a href="javascript:history.go(-1)"><font style="color:red; font-weight:bold; text-decoration:none">Terug naar het metadata formulier</font></a>

                      <%
                      } // end of if((request.getParameter("set_defaults") != null)

                      //---------------- Process parameters and store values ---------------
                      enumParamNames = request.getParameterNames();
                      while(enumParamNames.hasMoreElements()){
                         //Go throw all parameters from http-request
                         String sParameter = (String) enumParamNames.nextElement();

                         if((sParameter.equals("add")) && (sRequest_Submitted.equals("remove"))){
                            //we have got "remove lang string" command
                         }

                         if(sParameter.charAt(0) == 'm'){
                            String sMetadataDefinitionID = sParameter.substring(1);
                            //It creates a new or gets exist metadata
                            %>
                                <jsp:include page="metaedit_metaget.jsp" flush="true">
                                   <jsp:param name="node" value="<%= sNode %>" />
                                   <jsp:param name="metadata_definition" value="<%= sMetadataDefinitionID%>" />
                                </jsp:include>
                            <%
                            String sMetadataID = (String) session.getAttribute("metadata_id");

                            //Add this node to the "passed" list
                            //We shouldn't erase values from it in future
                            hsetPassedNodes.add(sMetadataID);

                            String sMetadataDefinitionType = "";
                            boolean bIsRelated = false;
                            //Type of metadata
                            %>
                            <%@include file="metaedit_definition_process.jsp" %>
                            <%
                         } // end of if(sParameter.charAt(0) == 'm')
                      } // end of  while(enumParamNames.hasMoreElements())


                      if(sRequest_Submitted.equals("add")){
                        //we have got "add lang string" command
                        //It creates a new or gets exist metadata

                      %>
                         <jsp:include page="metaedit_metaget.jsp" flush="true">
                             <jsp:param name="node" value="<%= sNode %>" />
                             <jsp:param name="metadata_definition" value="<%= request.getParameter("add") %>" />
                         </jsp:include>
                      <%
                         String sMetadataID = (String) session.getAttribute("metadata_id");

                         //add a new field
                      %>
                      <mm:remove referid="lang_id" />
                      <mm:remove referid="metadata_id" />
                      <mm:createnode type="metalangstring" id="lang_id"/>

                         <mm:node number="<%= sMetadataID %>" id="metadata_id">
                            <mm:createrelation source="metadata_id" destination="lang_id" role="posrel">
                               <mm:setfield name="pos"><%= 1000000 %></mm:setfield>
                            </mm:createrelation>
                         </mm:node>
                      <%
                      } //end of if(sRequest_Submitted.equals("add"))
                      %>
                      <mm:node number="<%= sNode %>">
                         <mm:relatednodes type="metadata">
                            <mm:field name="number" jspvar="sID" vartype="String">

                                <mm:relatednodes type="metavocabulary">
                                   <mm:field name="number" jspvar="sNum" vartype="String">
                                      <mm:listrelations type="metadata" role="posrel">
                                         <mm:field name="snumber" jspvar="sSource" vartype="String">
                                            <%
                                            if(!hsetAssignedVocabularies.contains(sNum) &&
                                               sSource.equals(sID)&&
                                               sRequest_Submitted != null){
                                            %>
                                            <mm:deletenode />
                                            <%
                                            } // end of if(!hsetAssignedVocabularies.contains(sNum) &&
                                            %>
                                         </mm:field>
                                      </mm:listrelations>
                                   </mm:field>
                                </mm:relatednodes>
                            <%
                            if(!hsetPassedNodes.contains(sID)){
                               // ------------ Remove old values ---------------
                               %>
                               <mm:relatednodes type="metadate">
                                   <mm:deletenode deleterelations="true"/>
                               </mm:relatednodes>
                               <mm:relatednodes type="metalangstring">
                                    <mm:deletenode deleterelations="true"/>
                               </mm:relatednodes>
                            <%
                            } // end of if(!hsetPassedNodes.contains(sID)){
                            %>
                            </mm:field>
                         </mm:relatednodes>
                      </mm:node>
                      <%

                      if((sRequest_Submitted.equals("add")) || (sRequest_Submitted.equals("remove"))){
                         String sParList = "";
                         enumParamNames = request.getParameterNames();
                         while(enumParamNames.hasMoreElements()){
                            String sParameter = (String) enumParamNames.nextElement();
                            String[] arrstrParameters = request.getParameterValues(sParameter);
                            if(sParameter.charAt(0) == 'm'){
                               for(int i=0; i < arrstrParameters.length; i++){
                                  sParList += "&" + sParameter + "=" + arrstrParameters[i] ;
                               } // end of for
                            } // end of if
                         }// end of while
                         %>
                         <jsp:include page="metaedit_form.jsp?node=<%= sNode %><%= sParList %>" flush="true" />
                     <%
                     }  // end of if((sRequest_Submitted.equals("add")) ||

                     // We have to update metadate.value field (it is handled by metadata builder) %>
                     <mm:node number="<%= sNode %>">
                        <mm:relatednodes type="metadata">
                           <mm:setfield name="value">-</mm:setfield>
                        </mm:relatednodes>
                     </mm:node>
                  </mm:cloud>
               </mm:content>
            <%
         } // end of Submit has been pressed
         %>
   </body>
</html>

