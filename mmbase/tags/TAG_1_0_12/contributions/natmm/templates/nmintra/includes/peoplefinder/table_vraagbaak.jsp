<%@include file="/taglibs.jsp" %>
<mm:import externid="e" jspvar="employeeId"/>
<mm:import externid="tp" jspvar="thisPrograms"/>
<mm:import externid="it" jspvar="imageTemplate"/>
<mm:import externid="p" jspvar="paginaID"/>
<mm:import externid="ps" jspvar="postingStr"/>
<mm:import externid="tqs" jspvar="templateQueryString"/>
<mm:import externid="d" jspvar="departmentId"/>
<mm:import externid="pr" jspvar="programId"/>
<mm:import externid="f" jspvar="firstnameId"/>
<mm:import externid="l" jspvar="lastnameId"/>
<mm:import externid="rl" jspvar="sRubriekLayout"/>
<mm:import externid="sd" jspvar="specialDays"/>
<mm:cloud>
<% boolean fullDescription = true; %>
<table border="0" cellpadding="0" cellspacing="0"> 
<tr>
    <td><img src="media/spacer.gif" width="114" height="2"></td>
    <td><img src="media/spacer.gif" width="1" height="2"></td>
    <td rowspan="2" valign="top" style="padding-left:5px;">
    <mm:node number="<%= employeeId %>"
        ><table border="0" cellpadding="0" cellspacing="0">
            <tr><td colspan="2" style="padding-bottom:3px;">
            <mm:field name="prefix"><mm:isnotempty><mm:write/>&nbsp;</mm:isnotempty></mm:field>
            <mm:field name="gender"><mm:compare value="0">mw. </mm:compare></mm:field
                ><mm:field name="firstname" jspvar="firstname" vartype="String" write="true">
                    <mm:field name="initials" jspvar="initials" vartype="String" write="false">
                       <mm:isnotempty>
                       <% 
                       if(firstname!=null && firstname.length()>1
                          && initials!=null && initials.length()>1 
                          && firstname.substring(0,1).equals(initials.substring(0,1))){
                           initials = initials.substring(initials.indexOf(".")+1);
                       }
                       %><%= initials 
                       %></mm:isnotempty>
                    </mm:field>
                </mm:field>
                <mm:field name="suffix"><mm:isnotempty><mm:write /></mm:isnotempty></mm:field>
                <mm:field name="lastname" />
            </td></tr>
            <% if(fullDescription) { 
               %><mm:related path="readmore,programs"
               ><mm:field name="readmore.readmore"
                   ><mm:isnotempty>
                       <tr><td style="padding-bottom:3px;">
                        <mm:first>Functie in<br></mm:first><li><mm:field name="programs.title" />:&nbsp;</td><td style="padding-bottom:3px;vertical-align:bottom;"><mm:write /></td></tr>
                   </mm:isnotempty
               ></mm:field
               ></mm:related><% 
            } %>
            <tr><td style="padding-bottom:3px;">Telefoon:&nbsp;</td>
                <td style="padding-bottom:3px;style="padding-bottom:3px;vertical-align:bottom;""><mm:field name="companyphone" /></td></tr>
            <mm:field name="showinfo"
                 ><mm:compare value="0" inverse="true"
                 ><tr><td style="padding-bottom:3px;">06-nummer:&nbsp;</td>
                    <td style="padding-bottom:3px;vertical-align:bottom;"><mm:field name="cellularphone" /></td></tr>
                 </mm:compare
            ></mm:field>
            <tr><td style="padding-bottom:3px;">Fax:&nbsp;</td>
                <td style="padding-bottom:3px;vertical-align:bottom;"><mm:field name="fax" /></td></tr>
            <tr><td style="padding-bottom:3px;">Email:</td>
                <td style="padding-bottom:3px;vertical-align:bottom;"><mm:field name="email" jspvar="email" vartype="String" write="false"><%
                    int adPos = email.indexOf("@");
                    if(adPos>-1) { 
                        %><a href="mailto:<%= email %>"><%= email.substring(0,adPos+1) + "&shy;" + email.substring(adPos+1) %></a><%
                    } %></mm:field></td></tr>
        <%
        if(fullDescription) { 
           %><mm:related path="readmore,afdelingen" 
                   fields="afdelingen.naam,readmore.readmore">
               <tr><td style="padding-bottom:3px;">Regio, eenheid of afdeling:&nbsp;</td>
                   <td style="padding-bottom:3px;vertical-align:bottom;"><mm:field name="afdelingen.naam" /></td></tr>
               <mm:field name="readmore.readmore"><mm:isnotempty><tr><td style="padding-bottom:3px;">Functie:&nbsp;</td>
                   <td style="padding-bottom:3px;vertical-align:bottom;"><mm:write /></td></tr></mm:isnotempty></mm:field>
           </mm:related
           ><mm:field name="job"
               ><mm:isnotempty>
                   <tr><td style="padding-bottom:3px;">Functie (visitekaartje):&nbsp;</td><td style="padding-bottom:3px;vertical-align:bottom;"><mm:write /></td></tr>
               </mm:isnotempty
           ></mm:field
           ><mm:related path="readmore,locations" fields="locations.naam" distinct="true">
               <tr><td style="padding-bottom:3px;">Lokatie:&nbsp;</td><td style="padding-bottom:3px;vertical-align:bottom;"><mm:field name="locations.naam" /></td></tr>
           </mm:related
           >
           <%-- 
           <tr><td style="padding-bottom:3px;">Verjaardag:&nbsp;</td><td style="padding-bottom:3px;vertical-align:bottom;"><mm:field name="birthday" id="birthday" write="false"/><mm:time format="d MMM" referid="birthday" /></td></tr>
           <tr><td style="padding-bottom:3px;">In dienst per:&nbsp;</td><td style="padding-bottom:3px;vertical-align:bottom;"><mm:field name="enrolldate" id="enroll" write="false"/><mm:time format="dd MMM yyyy" referid="enroll" /></td></tr>
           --%>
           <tr><td style="padding-bottom:3px;"><%= specialDays %>:&nbsp;</td>
              <td style="padding-bottom:3px;vertical-align:bottom;"><mm:field name="omschrijving_fra" /></td>
           </tr>
             <tr><td style="padding-bottom:3px;">Werkzaamheden:&nbsp;</td>
              <td style="padding-bottom:3px;vertical-align:bottom;"><mm:field name="omschrijving_de" /></td>
           </tr>         
           <%
           if(!sRubriekLayout.equals("" + NMIntraConfig.SUBSITE1_LAYOUT)) { 
              %>
              <tr><td style="padding-bottom:3px;">En verder:&nbsp;</td><td style="padding-bottom:3px;vertical-align:bottom;"><mm:field name="omschrijving" /></td></tr>
              <%
            }
         } %>
        </table>
    </mm:node>
    </td>
</tr>
<tr>
    <td valign="top"><% boolean imageExists = false; 
        %><mm:list nodes="<%= employeeId %>" path="medewerkers,images" max="1"
            ><% imageTemplate = "+s(110)"; 
                %><a href="javascript:launchCenter('/nmintra/imageview.jsp?image=<mm:field name="images.number" 
                    />', 'popup_<mm:field name="images.number" 
                    />', 600, 800, ',scrollbars,resizable=yes')"><img src=<%@include file="../imagessource.jsp" 
                    %> alt="" border="0"></a>
                <% imageExists = true; 
        %></mm:list><%
        if(!imageExists) { 
            %><mm:list nodes="<%= paginaID %>" path="pagina,posrel,images" constraints="posrel.pos='2'"
                ><% imageTemplate = "+s(110)"; 
                    %><img src=<%@include file="../imagessource.jsp" %> alt="" border="0">
            </mm:list><%
        }%><br><%
        if(!postingStr.equals("|action=print")) {
            %><a target="_blank" href="smoelenboek_vraagbaak.jsp<%= 
                templateQueryString %>&department=<%= departmentId %>&program=<%= programId
                %>&firstname=<%= firstnameId %>&lastname=<%= lastnameId %>&employee=<%= employeeId %>&pst=|action=print">print</a><%
        } 
    %></td>
    <td class="black"><img src="media/spacer.gif" width="1" height="465"></td>
</tr>
</table>
</mm:cloud>