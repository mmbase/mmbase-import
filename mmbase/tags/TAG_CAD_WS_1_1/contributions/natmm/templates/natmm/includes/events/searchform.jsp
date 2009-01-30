<%@include file="/taglibs.jsp" %>
<%@include file="../../includes/request_parameters.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="../../includes/calendar.jsp" %>
<%@include file="../../includes/time.jsp" %>
<%@include file="selecteddateandtype.jsp" %>
<%
String sRubriekLayout = request.getParameter("rl");
%>
<span class="colortitle">Selecteer een periode</span>
<form name="eventForm" action="events.jsp" method="post">
   <input type="hidden" name="p" value="<%=paginaID%>"/>
   <input type="hidden" name="offset" value="0"/>
   <input type="hidden" name="search" value="manual"/>
   <%
      
   %>
   <nobr>
      <select name="from_day">
         <% for(int f = 1; f < 32; f++) {
               %><option value="<%=f%>"<% if(iFromDay == f) { %> selected <% } %>><%=f%></option><%
            }
         %>
      </select>
      <select name="from_month">
         <% for(int f = 1; f < 13; f++) {
               %><option value="<%=f%>"<% if(iFromMonth == f) { %> selected <% } %>><%=months_lcase[f - 1]%></option><%
            }
         %>
      </select>
      <select name="from_year">
         <% for(int f = 2000; f < 2010; f++) { 
            %><option value="<%=f%>"<% if(iFromYear == f) { %> selected <% } %>><%=f%></option><%
            }
         %>
      </select>
   </nobr>
   <br/>
    <nobr>
      <select name="till_day">
         <% for(int f = 1; f < 32; f++) {
               %><option value="<%=f%>"<% if(iTillDay == f) { %> selected <% } %>><%=f%></option><%
            }
         %>
      </select>
      <select name="till_month">
         <% for(int f = 1; f < 13; f++) {
               %><option value="<%=f%>"<% if(iTillMonth == f) { %> selected <% } %>><%=months_lcase[f - 1]%></option><%
            }
         %>
      </select>
      <select name="till_year">
         <% for(int f = 2000; f < 2010; f++) { 
            %><option value="<%=f%>"<% if(iTillYear == f) { %> selected <% } %>><%=f%></option><%
            }
         %>
      </select>
   </nobr>
   <br/><br/>
   <!-- Show list of all activity types -->
   <span class="colortitle">Soort activiteit</span>
   <br/>

   <script>
      function setAllChechboxes()
      {
         var bCheckBoxState;
         if(document.all["activity_type_all"].checked) bCheckBoxState = true;
            else bCheckBoxState = false;

         var f = 0;
         while (document.all[f])
         {
            if(document.all[f].id.match("^activity_type_")) document.all[f].checked = bCheckBoxState;
            f++;
         }
      }
   </script>
   <input type="checkbox" id="activity_type_all" name="activity_type_all" onClick="setAllChechboxes()"/>&nbsp;Alle activiteiten of
   <br/>
   <mm:list nodes="<%= selectedEventTypes %>" path="evenement_type" constraints="evenement_type.isoninternet='1'">
      <mm:node element="evenement_type" jspvar="evenement_type">
      <input type="checkbox" id="activity_type_<mm:field name="number" />" name="activity_type_<mm:field name="number"/>"
      <%
         if((checkedActivityTypes.size() == 0) || (checkedActivityTypes.contains(evenement_type.getStringValue("number")))) %> checked <%
      %>
      />&nbsp;<span class="select"><mm:field name="naam" /></span>
      <br/>
      </mm:node>
   </mm:list>
   <br>
<% 
if(!sRubriekLayout.equals("" + NatMMConfig.DEMO_LAYOUT)) {
   // ** Show list of provinces
   if(selectedNatuurgebieden.equals("")) { // ** don't show the provincie dropdown if a natuurgebied is selected
      %><span class="colortitle">Kies een provincie</span>
      <br/>
      <select name="prov" style="width:100%" onchange="javascript:setOptionsN(document.eventForm.prov.options[document.eventForm.prov.selectedIndex].value);">
         <option value="-1"<% 
              if(provID.equals("-1")) { %> selected <% }
            %>>Alle provincies</option>
   
         <mm:listnodes type="provincies" orderby="naam">
            <mm:node jspvar="provincie">
               <option value="<mm:field name="number"/>"
               <%
                  if(provincie.getStringValue("number").equals(provID)) %> selected <%
               %>><mm:field name="naam"/></option>
            </mm:node>
         </mm:listnodes>
      </select>
      <br/>
      <%
   }
   // ** Show list of natuurgebieden
   if(selectedNatuurgebieden.equals("")||selectedNatuurgebieden.indexOf(",")!=-1) {
      // don't show list if exactly one natuurgebied is selected
      // if provID is set it means the not natuurgebieden are selected on this page, use provID determine selectedNatuurgebieden 
      if(!provID.equals("-1")) {
         %><mm:list nodes="<%= provID %>" path="provincies,pos4rel,natuurgebieden" fields="natuurgebieden.number">
            <mm:first inverse="true"><% selectedNatuurgebieden += ","; %></mm:first>
            <mm:field name="natuurgebieden.number" jspvar="et_number" vartype="String" write="false">
               <% selectedNatuurgebieden += et_number; %>
            </mm:field>
         </mm:list>
         <% 
      } %>
      <span class="colortitle">Kies een natuurgebied</span>
      <br/>
      <select name="n" style="width:100%"  onchange="javascript:setSelectedProv(document.eventForm.n.options[document.eventForm.n.selectedIndex].value);">
         <option value="-1"<% 
              if(natuurgebiedID.equals("-1")) { %> selected <% }
            %>>Alle natuurgebieden</option>
         <mm:list nodes="<%= selectedNatuurgebieden %>" path="natuurgebieden" orderby="natuurgebieden.naam">
            <mm:node element="natuurgebieden" jspvar="natuurgebied">
               <option value="<mm:field name="number"/>"
               <%
                  if(natuurgebied.getStringValue("number").equals(natuurgebiedID)) %> selected <%
               %>><mm:field name="naam"/></option>
            </mm:node>
         </mm:list>
      </select>
      <br/>
      <%
   } 
 } %>
   &nbsp;<div align="right"><input type="submit" value="ZOEK IN AGENDA" class="submit_image" style="width:175px;" /></div>
   <br/>
   <br/>
</form>
<script type="text/javascript"> 
<!-- 
function setOptionsN(chosen) 
{ 
   var selbox = document.eventForm.n; 
   selbox.options.length = 0;
<mm:listnodes type="provincies">
   if (chosen == "<mm:field name="number" />") {
      selbox.options[selbox.options.length] = new Option("Alle gebieden in <mm:field name="naam" />","-1");
   <mm:related path="pos4rel,natuurgebieden" orderby="natuurgebieden.naam">
      selbox.options[selbox.options.length] = new Option("<mm:field name="natuurgebieden.naam" />","<mm:field name="natuurgebieden.number" />"); 
   </mm:related>
   }
</mm:listnodes>
   
   if (chosen == "-1") { 
      selbox.options[selbox.options.length] = new Option("Alle natuurgebieden","-1");
<mm:listnodes type="natuurgebieden" orderby="naam">
      selbox.options[selbox.options.length] = new Option("<mm:field name="naam" />","<mm:field name="number" />"); 
</mm:listnodes>
   }
} 
function setSelectedProv(chosen) 
{ 
   var selbox = document.eventForm.prov; 
<mm:listnodes type="natuurgebieden">
   if (chosen == "<mm:field name="number" />") {
   <mm:related path="pos4rel,provincies">
      selbox.value = '<mm:field name="provincies.number" />'; 
   </mm:related>
   }
</mm:listnodes>
   
} 
--> 
</script>
</mm:cloud>