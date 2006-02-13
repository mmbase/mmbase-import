<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<%@page import = "java.util.Date" %>
<%@page import = "java.util.HashSet" %>
<%@page import = "org.mmbase.bridge.*" %>
<%@page import = "org.mmbase.bridge.util.*" %>

<%@page import = "nl.didactor.metadata.util.MetaDataHelper" %>

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

<mm:content postprocessor="reducespace">
    <mm:cloud method="delegate" jspvar="cloud">
        <%@include file="/shared/setImports.jsp" %>


        <%
        MetaDataHelper mdh = new MetaDataHelper();
        NodeList nlLangCodes = mdh.getLangCodes(cloud);              // *** Get languages list
        NodeList nlRelatedNodes = mdh.getRelatedMetaData(cloud,sNode); // *** Get all related metadata to this node
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
                        sMetastandartNodes = mdh.getActiveMetastandards(cloud, null, null);
                    }

                    //System.out.println("result=" + mdh.getActiveMetastandards(cloud, null, null));

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
                                  <%= mdh.getAliasForObject(cloud, sMetaStandartID, sUserID) %>
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
                              String sMinValues = thisMetadefinition.getStringValue("minvalues");
                              String sMaxValues = thisMetadefinition.getStringValue("maxvalues");
                              String sMetaDefinitionID = thisMetadefinition.getStringValue("number");

                              Node metadataNode = mdh.getMetadataNode(cloud,sNode,sMetaDefinitionID,request.getParameter("set_defaults") != null);
                              nlRelatedNodes.add(metadataNode);
                          %>
                          <a name="m<mm:field name="number"/>"/>
                              <%-- Taking the synonym if there is any --%>
                              <font style="font-family:arial; font-size:13px; font-weight:bold">
                                  <mm:write referid="user" jspvar="sUserID" vartype="String">
                                      <%= mdh.getAliasForObject(cloud, sMetaDefinitionID, sUserID) %>
                                  </mm:write>
                              </font>
                          <br/>


                          <mm:field name="description"><mm:isnotempty><mm:write /><br/></mm:isnotempty></mm:field>
                          <%
                              if(!mdh.hasValidMetadata(cloud,sNode,sMetaDefinitionID)) {
                                  %>
                                  <br/>
                                  <font style="color:#FF0000;font-size:90%"><di:translate key="<%= "metadata." + mdh.getReason(cloud,sMetaDefinitionID) %>" />
                                  <%
                                  if(mdh.getReason(cloud,sMetaDefinitionID).equals("number_of_vocabularies_should_match_min_max")) {
                                      %>
                                      = [ <mm:field name="minvalues"/>, <mm:field name="maxvalues"/> ]
                                      <%
                                  }
                                  %>.</font>
                                  <%
                              }
                          %>
                          <br/>
                          <%
                              if(sDefType.equals("" + mdh.VOCABULARY_TYPE))
                              {
                                  %><%@include file="metaedit_form_vocabulary.jsp" %><%
                              }
                              if(sDefType.equals("" + mdh.DATE_TYPE))
                              {
                                  %><%@include file="metaedit_form_date.jsp" %><%
                              }
                              if(sDefType.equals("" + mdh.LANGSTRING_TYPE))
                              {
                                  %><%@include file="metaedit_form_langstring.jsp" %><%
                              }
                              if(sDefType.equals("" + mdh.DURATION_TYPE))
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
