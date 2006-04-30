<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@ page import = "java.util.Calendar,nl.mmatch.HtmlCleaner" %>
<%@include file="../../includes/calendar.jsp" %>
<%
boolean isIE = (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE")>-1);
String p = request.getParameter("p");
// *** warning on using two times the same question should be added ***
%>
<mm:cloud jspvar="cloud">
<form name="emailform" method="post" target="" action="javascript:postIt('');">
   <%
      int iNumberOfPools = 0;
      int iCounterOfPools = 0;
   %>
   <mm:list nodes="<%= p %>" path="pagina,posrel,formulier" orderby="posrel.pos" directions="UP">
      <mm:size jspvar="tmp" vartype="Integer">
         <% iNumberOfPools = tmp.intValue(); %>
      </mm:size><%
      iCounterOfPools++;
      %><mm:node element="formulier" jspvar="thisForm"><%
            String thisFormNumber = thisForm.getStringValue("number");
            %><span class="colortitle"><mm:field name="titel"/></span><br/>
              <mm:field name="omschrijving" jspvar="text" vartype="String" write="false">
                 <% if(text!=null&&!HtmlCleaner.cleanText(text,"<",">","").trim().equals("")) { %><mm:write /><% } %>
          	  </mm:field
              ><mm:related path="posrel,formulierveld" orderby="posrel.pos" directions="UP"
              ><mm:node element="formulierveld" jspvar="thisFormField"><%
              String formulierveld_number = thisFormField.getStringValue("number");
              String formulierveld_label = thisFormField.getStringValue("label");
              if(!thisFormField.getStringValue("label_fra").equals("")) {
                 formulierveld_label = thisFormField.getStringValue("label_fra");
              }
              formulierveld_label = formulierveld_label.substring(0,1).toUpperCase()+formulierveld_label.substring(1);
              String formulierveld_type = thisFormField.getStringValue("type");
              boolean isRequired = thisFormField.getStringValue("verplicht").equals("1");
              %>
              <mm:first><table cellspacing="0" cellpadding="0" border="0" style="width:354px;margin-bottom:10px;margin-top:3px;"></mm:first>
              <mm:first inverse="true">
                 <tr><td colspan="2" style="height:7px;"></td></tr>
              </mm:first>
              <tr><%
                    int iNumberOfItems = 0;
                    boolean bDefaultIsSet = false;
                 %><mm:related path="posrel,formulierveldantwoord">
                    <mm:size jspvar="size" vartype="Integer" write="false"><%
                       iNumberOfItems = size.intValue();
                    %></mm:size>
                 </mm:related>
                 <% 
                 // *** radio buttons with only 2 choices  ***
                 if((formulierveld_type.equals("4")) && (iNumberOfItems < 3)) {
                 %><td style="width:177px;">
                      <table cellspacing="0" cellpadding="0" border="0" width="100%">
                         <tr>
                         <mm:related path="posrel,formulierveldantwoord" orderby="posrel.pos" directions="UP">
                            <td class="maincolor" width="8px;"><input type="radio" name="q<%= thisFormNumber %>_<%= formulierveld_number%>" value="<mm:field name="formulierveldantwoord.waarde" />"
                               <%@include file="radio_checked.jsp" %>
                            ></td>
                            <td class="maincolor" style="padding:5px;line-height:0.80em;">
                              <mm:field name="formulierveldantwoord.waarde" jspvar="waarde" vartype="String" write="false">
                                    <%= (waarde!=null ? waarde.substring(0,1).toUpperCase()+waarde.substring(1) : "") %>
                              </mm:field></td>
                         </mm:related>
                         </tr>
                      </table>
                   </td>
                   <td style="width:177px;">&nbsp;</td><%
                 }
                 // *** radio buttons or checkboxes ****
                 if( (formulierveld_type.equals("4") && (iNumberOfItems > 2)) || formulierveld_type.equals("5")) {
                  %><td colspan="2" class="maincolor" style="padding:5px;line-height:0.80em;"><%=
                         formulierveld_label %>&nbsp;<% if(isRequired) { %>*&nbsp;<% } 
                     %></td>
                    </tr>
                    <mm:related path="posrel,formulierveldantwoord" orderby="posrel.pos" directions="UP">
                       <tr><td colspan="2" style="height:7px;"></td></tr>
                       <tr>
                       <td class="maincolor" style="width:177px;line-height:0.80em;text-align:right"><%
                            if(formulierveld_type.equals("4")) {
                                %><input type="radio" name="q<%= thisFormNumber %>_<%= formulierveld_number
                                   %>" value="<mm:field name="formulierveldantwoord.waarde" />"
                                   <%@include file="radio_checked.jsp" %>
                                ><%
                             } else if(formulierveld_type.equals("5")) {
                                %><input type="checkbox" name="q<%= thisFormNumber %>_<%= formulierveld_number %>_<mm:field name="formulierveldantwoord.number"/>" value="<mm:field name="formulierveldantwoord.waarde" />"
                                      <mm:field name="formulierveldantwoord.standaard" jspvar="defaultValue" vartype="Integer">
                                         <%
                                            if(defaultValue.intValue() != 0)
                                            {
                                               %>checked="checked"<%
                                               bDefaultIsSet = true;
                                            }
                                         %>
                                      </mm:field>
                                 ><%
                             }
                          %></td>
                       <td class="maincolor" style="width:177px;padding:5px;line-height:0.80em;">
                              <mm:field name="formulierveldantwoord.waarde" jspvar="waarde" vartype="String" write="false">
                                    <%= (waarde!=null ? waarde.substring(0,1).toUpperCase()+waarde.substring(1) : "") %>
                              </mm:field>
                       </td>
                       <mm:last inverse="true"></tr></mm:last>
                    </mm:related><%
                 }
                
                 // **** dropdown ****
                 if(formulierveld_type.equals("3")) {
                 %>
                    <td class="maincolor" style="width:177px;padding:5px;line-height:0.80em;"><%= formulierveld_label %>&nbsp;<% if(isRequired) { %>*&nbsp;<% } %></td>
                    <td style="width:177px;padding:0px;vertical-align:top;">
                        <select name="q<%= thisFormNumber %>_<%= formulierveld_number %>" style="width:178px;font-size:11px;">
                          <option>...
                          <mm:related path="posrel,formulierveldantwoord" orderby="posrel.pos" directions="UP">
                             <option value="<mm:field name="formulierveldantwoord.waarde" />"
                                <mm:field name="formulierveldantwoord.standaard" jspvar="defaultValue" vartype="Integer">
                                   <%
                                      if((!bDefaultIsSet) && (defaultValue.intValue() != 0))
                                      {
                                         %>selected="selected"<%
                                         bDefaultIsSet = true;
                                      }
                                   %>
                                </mm:field>
                                ><mm:field name="formulierveldantwoord.waarde" jspvar="waarde" vartype="String" write="false">
                                    <%= (waarde!=null ? waarde.substring(0,1).toUpperCase()+waarde.substring(1) : "") %>
                                </mm:field>
                             </option>
                          </mm:related>
                       </select></td>
                 <%
              } 

              // *** textline ***
              if(formulierveld_type.equals("2")) {
                 %>
                    <td class="maincolor" style="width:177px;padding:5px;line-height:0.85em;"><%=
                         formulierveld_label %>&nbsp;<% if(isRequired) { %>*&nbsp;<% } %></td>
                    <td class="maincolor" style="width:177px;padding:0px;padding-right:1px;vertical-align:top;<% if(!isIE) { %>padding-top:1px;<% } %>">
                       <input type="text" name="q<%= thisFormNumber %>_<%= formulierveld_number %>" style="width:100%;border:0;" onkeypress="return handleEnter(this, event)">
                    </td>
                 <%
              }
              
              // *** textarea ***
              if(formulierveld_type.equals("1")) {
                 %>
                    <td colspan="2" class="maincolor" style="padding:5px;line-height:0.80em;"><%=
                         formulierveld_label %>&nbsp;<% if(isRequired) { %>*&nbsp;<% } 
                     %></td>
                    </tr>
                    <tr>
                    <td colspan="2" class="maincolor" style="<% if(!isIE) { %>padding-right:2px;<% } %>"><textarea rows="4" name="q<%= thisFormNumber %>_<%= formulierveld_number %>" wrap="physical" style="width:100%;margin-left:1px;margin-right:1px;border:0;"></textarea></td>
                 <%
              }
  
              // *** date ***
              if(formulierveld_type.equals("6")) { // *** create input fields for day, month and year
                  %>
                  <td class="maincolor" style="width:177px;padding:5px;line-height:0.80em;">
                     <%= formulierveld_label %>&nbsp;<% if(isRequired) { %>*&nbsp;<% } %>
                  </td>
                  <td class="maincolor" style="width:177px;padding:0px;padding-right:1px;vertical-align:top;text-align:right;<% if(!isIE) { %>padding-top:1px;<% } %>"><%
                     %><input type="text" name="q<%= thisFormNumber %>_<%= formulierveld_number %>_day" style="width:43px;border:0;" onkeypress="return handleEnter(this, event)"><%
                     %><select name="q<%= thisFormNumber %>_<%= formulierveld_number %>_month" style="width:89px;font-size:9px;">
                       <option>...<%
                       for(int m = 0; m < 12; m++) { %><option value="<%= months_lcase[m] %>"><%= months_lcase[m] %><% }
                       %></select><%
                     %><input type="text" name="q<%= thisFormNumber %>_<%= formulierveld_number %>_year" style="width:43px;border:0;" onkeypress="return handleEnter(this, event)"><%
                  %></td>
                 <%
              }
              %>
              </tr>     
              <mm:last><%
                    if(iNumberOfPools == iCounterOfPools)
                    {
                       %>
                          <tr>
                             <td colspan="2">(*) Vul minimaal deze velden in i.v.m. een correcte afhandeling.</td>
                          </tr>
                          <tr>
                             <td colspan="2" style="height:8px;"></td>
                          </tr>
                          <tr>
                             <td style="width:177px;">
                             </td>
                             <td style="width:177px;">
                                <table border="0" cellpadding="0" cellspacing="0" class="maincolor" style="width:177px;">
                                   <tr>
                                      <td class="submitbutton" >&nbsp;<a href="javascript:postIt('');" class="submitbutton"><%= thisForm.getStringValue("emailonderwerp").toUpperCase() %></a>&nbsp;</td>
                                      <td class="submit_image" onClick="javascript:postIt('');"></td>
                                   </tr>
                                </table>
                             </td>
                          </tr>
                       <%
                    }
                 %>
                 </table>
              </mm:last>
            </mm:node>
          </mm:related>
          <mm:field name="omschrijving_de" jspvar="text" vartype="String" write="false">
            <% if(text!=null&&!HtmlCleaner.cleanText(text,"<",">","").trim().equals("")) { %><mm:write /><% } %>
   		 </mm:field>
      </mm:node>
   </mm:list>
</form>
</mm:cloud>