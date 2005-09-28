<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<%@page import = "java.util.*" %>

<%
   String sNode = request.getParameter("node");    // node for which metadata is editted

   String [] MONTHS = { "januari", "februari", "maart", "april", "mei", "juni", "juli", "augustus", "september", "oktober", "november", "december" };
   String EMPTY_VALUE = "...";
   String sNodeValue = "";
   String sNodeUrlString ="";

   HashSet hsetMetastandards = new HashSet();
   HashSet hsetCurMetastandards = new HashSet();

   HashSet hsetRelatedNodes = new HashSet();
   SortedSet hsetLangCodes = new TreeSet();
   HashSet hsetAssignedVals = new HashSet();
   HashSet hsetVocabularis = new HashSet();        // set of all selected metavocabularies

   HashSet hsetClassesOfTypeUrl = new HashSet();
   hsetClassesOfTypeUrl.add("audiotapes");
   hsetClassesOfTypeUrl.add("videotapes");

   HashSet hsetClassesOfTypeBin = new HashSet();
   hsetClassesOfTypeBin.add("attachments");
   hsetClassesOfTypeBin.add("pdfs");

   int CONSTRAINT_FORBIDDEN = 3;
%>

<style type="text/css">
   .bottom_link{
      color:18248C;
      font-family:arial;
      font-size:20px;
      font-weight:normal;
      text-decoration:none;
   }

   body, .body{
      font-family:arial;
      font-size:13px;
   }
</style>

<mm:content postprocessor="reducespace">
   <mm:cloud>

     <%@include file="me_form_assignedvals_vocabularis.jsp"%>
     <%@include file="me_form_langcodes.jsp"%>

      <%
      // Get all related metadata to this node

      %>
      <mm:node number="<%= sNode %>">
         <mm:relatednodes type="metadata">
            <mm:field name="number" jspvar="sID" vartype="String">
               <%
               hsetRelatedNodes.add(sID);
               %>

               <mm:list nodes="<%=sID%>" path="metadata,posrel,metavocabulary"
                       searchdir="destination" fields="metavocabulary.number,metavocabulary.value" >
                  <mm:field name="metavocabulary.number" jspvar="sMetaVocNum" vartype="String">
                     <mm:field name="metavocabulary.value" jspvar="sMetaVocVal" vartype="String" >
                        <%
                           hsetVocabularis.add(sMetaVocNum);
                        %>
                     </mm:field>
                  </mm:field>
               </mm:list>
            </mm:field>
         </mm:relatednodes>
         <% // set sNodeValue and SNodeUrlString for automatic generation of metadata %>
         <mm:nodeinfo type="type" jspvar="sNodeType" vartype="String" >

             <%

             if(hsetClassesOfTypeUrl.contains(sNodeType)){
             %>
                <mm:field name="url" jspvar="sObjectUrl" vartype="String">
                   <%
                   sNodeUrlString = sObjectUrl;
                   %>
                </mm:field>
             <%
             }
             else if (hsetClassesOfTypeBin.contains(sNodeType)){
                %>
                <mm:field name="size" jspvar="sObjectSize" vartype="String">
                   <% sNodeValue =  sObjectSize; %>
                </mm:field>
             <%
             }
             %>
         </mm:nodeinfo>
      </mm:node>

      <form name="meta_form">
         <div style="overflow-y:scroll; width:100%; height:90%; position:absolute">
            <input type="hidden" name="node" value="<%= sNode %>"/>
            <input type="hidden" name="number" value="<%= sNode %>"/>
            <input type="hidden" name="submitted" value="submit"/>
            <input type="hidden" name="add" value=""/>
            <input type="hidden" name="close" value="yes"/>
            <%
            if(request.getParameter("set_defaults") != null){
               //If we are setting default values, we should repeat "set_default" parameter
               %>
                  <input type="hidden" name="set_defaults" value="true"/>
               <%
            }
            %>

            <%@include file="metaedit_header.jsp" %>
            <font style="font-size:5px">&nbsp;<br/></font>

            <%
            // If we are working with default values, we have to show only one metastandart
            // So, we have to use constraints

            String sMetastandartConstraints = "";
            if(request.getParameter("set_defaults") != null) sMetastandartConstraints = "number=" + sNode;

            %>
            <mm:listnodes type="metastandard" orderby="name" constraints="<%= sMetastandartConstraints %>" directions="DOWN" max="1">
               <mm:field name="number" jspvar="sNumber" vartype="String" >
                  <% // Let's go to this metastandard tree in the direct order ...
                     // Tree root number ...
                  hsetMetastandards.add(sNumber);
                  %>
               </mm:field>
             </mm:listnodes>

             <%
             for(Iterator msta = hsetMetastandards.iterator(); msta.hasNext();){
                String sRootMetastandard = (String) msta.next();
                String sCurMetastandard = sRootMetastandard;
                String sPrefix = "m";
                hsetCurMetastandards.add(sRootMetastandard);
                %>
                <mm:node number="<%=sRootMetastandard%>" >
                   <font style="font-family:arial; font-size:20px; font-weight:normal">
                      <mm:field name="name"/>
                   </font>
                   <br/>
                   <mm:field name="description"/>
                   <hr style="width:99%; height:1px; color:#CCCCCC">
                </mm:node>

                <%
                while(hsetCurMetastandards.size()>0){
                   if(!sRootMetastandard.equals(sCurMetastandard)){
                      sPrefix = "n";
                   }
                   else{
                      sPrefix = "m";
                   }
                   %>
                   <mm:list nodes="<%=sCurMetastandard%>" path="metastandard,posrel,metadefinition" fields="metadefinition.number"
                       orderby="posrel.pos" directions="UP">
                      <mm:node element="metadefinition" jspvar="thisMetaDef">

                         <% boolean bCheckRelations = true; %>
                         <%@include file="me_form_checkrelations.jsp"%>
                         <%

                         if(bCheckRelations){
                         %>
                            <a name="<%=sPrefix%><mm:field name="number"/>">
                            <font style="font-family:arial; font-size:13px; font-weight:bold"><mm:field name="name"/></font>
                            <br/>
                            <mm:field name="description"/>
                            <br/>
                            <br/>
                            <%
                            String sDefType = thisMetaDef.getStringValue("type");
                            String sMinValues = thisMetaDef.getStringValue("minvalues");
                            String sMaxValues = thisMetaDef.getStringValue("maxvalues");
                            String sMetaDefinitionID = thisMetaDef.getStringValue("number");
                            String sMetaHandler = thisMetaDef.getStringValue("handler");

                            if(request.getParameter("set_defaults") != null){
                               // We have to insert default values here, so we have to create metadata
                               %>
                               <jsp:include page="metaedit_metaget.jsp" flush="true">
                                  <jsp:param name="node" value="<%= sNode %>" />
                                  <jsp:param name="metadata_definition" value="<%= sMetaDefinitionID %>" />
                                  <jsp:param name="set_defaults" value="true" />
                               </jsp:include>
                            <%
                            }
                            else{
                            %>
                               <jsp:include page="metaedit_metaget.jsp" flush="true">
                                  <jsp:param name="node" value="<%= sNode %>" />
                                  <jsp:param name="metadata_definition" value="<%= sMetaDefinitionID %>" />
                               </jsp:include>
                            <%
                            }

                            hsetRelatedNodes.add(session.getAttribute("metadata_id"));

                            if(sDefType.equals("1")) { // vocabulary
                            %>
                               <%@include file="me_form_vocabularyfield.jsp"%><%
                            } else if(sDefType.equals("2")) { // date
                               %><%@include file="me_form_datefield.jsp"%><%
                            } else if(sDefType.equals("3")) { // langstring
                               %><%@include file="me_form_langstringfield.jsp"%><%
                            } else if(sDefType.equals("4")) { // duration
                               %><%@include file="me_form_durationfield.jsp"%><%
                            } else if(sDefType.equals("5")) { // autocomplete value
                               %><%@include file="me_form_autocompletefield.jsp"%><%
                            }
                            %>
                            <hr style="width:99%; height:1px; color:#CCCCCC">
                            <%
                         } // if(bCheckRelations)
                         %>
                      </mm:node> <%// metadefinitions %>
                   </mm:list>
                   <%
                   // remove current metastandard ... from hash
                   hsetCurMetastandards.remove(sCurMetastandard);

                   // add child metastandards to the hash ...
                   %>
                   <mm:list nodes="<%=sCurMetastandard%>" path="metastandard1,posrel,metastandard2"
                            fields="metastandard2.number" searchdir="destination" >
                      <mm:field name="metastandard2.number" jspvar="sNumber" vartype="String">
                         <%
                         hsetCurMetastandards.add(sNumber);
                         %>
                      </mm:field>
                   </mm:list>
                   <%
                   Iterator it = hsetCurMetastandards.iterator();
                   if(it.hasNext())
                      sCurMetastandard = (String)it.next();

                }// end of while
                %>
                <br/><br/>
                <hr style="width:99%; height:1px; color:#CCCCCC">
             <%
             } // end of for(Iterator msta = hsetMetastandards.iterator(); msta.hasNext();)
             %>

             <style type="text/css">
                .special_buttons
                {
                   background-color:transparent;
                   color: #18248C;
                   font-size: 20px;
                   font-weight: normal;
                   font-family:arial;
                   border: none;
                   vertical-align: middle;
                }
             </style>
         </div>
         <div align="center" style="position:absolute; bottom:0px">
            <hr style="width:100%; height:3px; color:#000000">
            <table border="0" cellpadding="0" cellspacing="0" width="0" style="height:0px">
               <tr>
                  <td align="right">
                     <%
                     if(session.getAttribute("show_metadata_in_list") == null){
                        %><input type="reset" name="command" value="Annuleren" class="special_buttons" style="width:90px" onClick="document.location.href='menu.jsp'"/><%
                     }
                     else{
                        %><input type="reset" name="command" value="Annuleren" class="special_buttons" style="width:90px" onClick="document.location.href='<%= session.getAttribute("metalist_url") %>'"/><%
                     }
                     %>
                  </td>
                  <td>-</td>
                  <td><input type="submit" name="command" value="Opslaan en beëindigen" class="special_buttons" style="width:200px" /></td>
                  <td>-</td>
                  <td><input type="submit" name="command" value="Opslaan" class="special_buttons" style="width:80px" onClick="close.value='no'"/></td>
               </tr>
            </table>
         </div>
      </form>
   </mm:cloud>
</mm:content>

