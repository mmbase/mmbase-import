<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<%@page import = "java.util.*" %>
<%@page import = "org.mmbase.bridge.*" %>
<%@page import = "org.mmbase.bridge.util.*" %>

<%@page import = "nl.didactor.metadata.util.MetaDataHelper" %>
<%@page import = "nl.didactor.component.metadata.constraints.*" %>

<%
    String sNode = request.getParameter("number");
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


<script>
   function switchMetaVocabularyTree(checkbox){
      f = 0;
      while(checkbox.form[f] != null){
         if(checkbox.form[f].getAttribute("checkbox_id", false) != null)
         {
            if(checkbox.checked)
            {
               if(checkbox.form[f].getAttribute("checkbox_id", false).match("^" + checkbox.getAttribute("checkbox_id", false) + "_\\d*$"))
               {
                  checkbox.form[f].disabled = !checkbox.checked;
               }
            }
            else
            {
               if(checkbox.form[f].getAttribute("checkbox_id", false).match("^" + checkbox.getAttribute("checkbox_id", false) + "_\\d*"))
               {
                  checkbox.form[f].disabled = !checkbox.checked;
                  checkbox.form[f].checked = false;
               }
            }
         }
         f++;
      }
   }

   function switchMetaVocabularyTreeVisibility(item){
      var imgControlImage = document.getElementById('img_layer_controller_' + item);
      var sVisibility;
      var sControlMask;

      var sCurrentState = "closed";
      var i = 0
      while(document.all[i] != null)
      {
         if (document.all[i].id.match("^checkbox_layer_" + item + "_\\d*$"))
         {
            if (document.all[i].style.display != "none")
            {
               sCurrentState = "opened";
            }
            break;
         }
         i++;
      }


      if(sCurrentState == 'opened')
      {
         imgControlImage.src = "gfx/show.gif"

         i = 0
         while(document.all[i] != null)
         {
            if (document.all[i].id.match("^checkbox_layer_" + item + "_\\d*"))
            {
               document.all[i].style.display = "none";
            }
            if (document.all[i].id.match("^img_layer_controller_" + item + "_\\d*"))
            {
               document.all[i].src = imgControlImage.src;
            }
            i++;
         }
      }
      else
      {
         imgControlImage.src = "gfx/hide.gif"

         i = 0
         while(document.all[i] != null)
         {
            if (document.all[i].id.match("^checkbox_layer_" + item + "_\\d*$"))
            {
               document.all[i].style.display = "";
            }
            i++;
         }
      }
   }


</script>
<mm:content postprocessor="reducespace">
    <mm:cloud method="delegate" jspvar="cloud" username="admin" password="admin2k">
        <%@include file="/shared/setImports.jsp" %>


        <%
           Node nodeUser = nl.didactor.security.Authentication.getCurrentUserNode(cloud);
           Node nodeObject = cloud.getNode(sNode);
           NodeList nlLangCodes = MetaDataHelper.getLangCodes(cloud);              // *** Get languages list
           NodeList nlRelatedNodes = MetaDataHelper.getRelatedMetaData(cloud,sNode); // *** Get all related metadata to this node
           MetaDataHelper.fillAutoValues(cloud.getNode(sNode), getServletContext(), nodeUser);
        %>
        <form name="meta_form">

            <div style="overflow-y:scroll; width:100%; height:90%; position:absolute">
                <input type="hidden" name="number" value="<%= sNode %>"/>
                <input type="hidden" name="submitted" value="submit"/>
                <input type="hidden" name="add" value=""/>
                <input type="hidden" name="close" value="yes"/>
                <%
                    if(request.getParameter("set_defaults") != null)
                    {//If we are setting default values, we should repeat "set_default" parameter
                        %>
                            <input type="hidden" name="set_defaults" value="true"/>
                        <%
                    }
                %>

                <%@include file="metaedit_header.jsp" %>
                <font style="font-size:5px">&nbsp;<br/></font>

                <%
                    //if we are working with default values, we have to show only one metastandart
                    //So, we have to use constraints
                    String sMetastandartNodes;
                    if(request.getParameter("set_defaults") != null) {
                        sMetastandartNodes = sNode;
                    } else {
                        sMetastandartNodes = MetaDataHelper.getCachedActiveMetastandards(cloud, application, null, nodeUser);
                    }

                %>
                <mm:list nodes="<%= sMetastandartNodes %>" path="metastandard" orderby="metastandard.name">
                   <mm:node element="metastandard">
                      <mm:first inverse="true">
                          <br/><br/>
                      </mm:first>


                      <font style="font-family:arial; font-size:20px; font-weight:normal">
                          <%-- Taking the synonym if there is any --%>
                          <mm:field name="number" jspvar="sMetaStandartID" vartype="String">
                              <mm:write referid="user" jspvar="sUserID" vartype="String">
                                  <%= MetaDataHelper.getAliasForObject(cloud, sMetaStandartID, sUserID) %>
                              </mm:write>
                          </mm:field>
                      </font>
                      <br/>


                      <mm:field name="description"><mm:isnotempty><mm:write /><br/></mm:isnotempty></mm:field>
                      <hr style="width:99%; height:1px; color:#CCCCCC">
                      <mm:related path="posrel,metadefinition" orderby="posrel.pos,metadefinition.name">
                          <mm:node element="metadefinition" jspvar="thisMetadefinition">
                          <%
                              String sDefType = thisMetadefinition.getStringValue("type");
                              String sMaxValues = thisMetadefinition.getStringValue("maxvalues");
                              String sMetaDefinitionID = thisMetadefinition.getStringValue("number");

                              Node metadataNode = MetaDataHelper.getMetadataNode(cloud,sNode,sMetaDefinitionID,request.getParameter("set_defaults") != null);
                              nlRelatedNodes.add(metadataNode);
                          %>
                          <a name="m<mm:field name="number"/>"/>
                              <%-- Taking the synonym if there is any --%>
                              <font style="font-family:arial; font-size:13px; font-weight:bold">
                                  <mm:write referid="user" jspvar="sUserID" vartype="String">
                                      <%= MetaDataHelper.getAliasForObject(cloud, sMetaDefinitionID, sUserID) %>
                                  </mm:write>
                              </font>
                          <br/>


                          <mm:field name="description"><mm:isnotempty><mm:write /><br/></mm:isnotempty></mm:field>
                          <%
                              ArrayList arliErrors = MetaDataHelper.hasTheMetaDefinitionValidMetadata(thisMetadefinition, cloud.getNode(sNode), application, nodeUser);
                              for(Iterator it = arliErrors.iterator(); it.hasNext();){
                                %>
                                  <br/>
                                  <font style="color:#FF0000;font-size:90%"><%= ((nl.didactor.component.metadata.constraints.Error) it.next()).getErrorReport() %>.</font>
                                <%
                              }
                          %>
                          <br/>
                          <%
                              if(sDefType.equals("" + MetaDataHelper.VOCABULARY_TYPE))
                              {
                                  %><%@include file="metaedit_form_vocabulary.jsp" %><%
                              }
                              if(sDefType.equals("" + MetaDataHelper.DATE_TYPE))
                              {
                                  %><%@include file="metaedit_form_date.jsp" %><%
                              }
                              if(sDefType.equals("" + MetaDataHelper.LANGSTRING_TYPE))
                              {
                                  %><%@include file="metaedit_form_langstring.jsp" %><%
                              }
                              if(sDefType.equals("" + MetaDataHelper.DURATION_TYPE))
                              {
                                  %><%@include file="metaedit_form_duration.jsp" %><%
                              }
                          %>
                          <mm:last inverse="true">
                              <hr style="width:99%; height:1px; color:#CCCCCC">
                          </mm:last>
                      </mm:node>
                      </mm:related>
                      <mm:last inverse="true">
                          <hr style="width:99%; height:1px; color:#CCCCCC">
                      </mm:last>
                   </mm:node>
                </mm:list>

                <style type="text/css">
                    .special_buttons {
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
                                if(session.getAttribute("show_metadata_in_list") == null)
                                {
                                    %><input type="reset" name="command" value="<di:translate key="metadata.cancel" />" class="special_buttons" style="width:90px" onClick="document.location.href='menu.jsp'"/><%
                                }
                                else
                                {
                                    %><input type="reset" name="command" value="<di:translate key="metadata.cancel" />" class="special_buttons" style="width:90px" onClick="document.location.href='<%= session.getAttribute("metalist_url") %>'"/><%
                                }
                            %>
                        </td>
                        <td>-</td>
                        <td><input type="submit" name="command" value="<di:translate key="metadata.save_and_close" />" class="special_buttons" style="width:200px" /></td>
                        <td>-</td>
                        <td><input type="submit" name="command" value="<di:translate key="metadata.save" />" class="special_buttons" style="width:80px" onClick="close.value='no'"/></td>
                    </tr>
                </table>
            </div>
        </form>
    </mm:cloud>
</mm:content>
