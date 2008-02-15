<%
PaginaHelper ph = new PaginaHelper(cloud);
String menu_path = "menu1,posrel,menu";
String orderby="posrel.pos";
String path = "posrel,editwizards";
String userConstraint = "";
%>
<mm:notpresent referid="startnodes">
   <% // if there are no startnodes, this include is used from pagina_all.jsp %>
   <mm:import id="startnodes"></mm:import>
   <%
   menu_path = "menu"; 
   orderby="";
   path = "posrel,editwizards,gebruikt,users";
   %>
   <mm:node number="$thisuser" jspvar="thisUser">
      <% userConstraint = "users.number='" + thisUser.getStringValue("number") + "'"; %>
   </mm:node>
</mm:notpresent>
<%--
<%= menu_path %><br/>
<%= userConstraint %><br/>
<%= path %><br/>
--%>
<p>
<mm:import id="noeditwizard" />
<mm:list nodes="$startnodes" path="<%= menu_path %>" orderby="<%= orderby %>" directions="UP" searchdir="destination">
   <mm:remove referid="isvisible" />
   <mm:compare referid="startnodes" value="" inverse="true"><mm:import id="isvisible" /></mm:compare>
   <mm:node element="menu">
      <mm:remove referid="creatierubriek" />
      <mm:relatednodes type="rubriek">
         <mm:field name="number" id="creatierubriek" write="false" /> 
      </mm:relatednodes>
      <mm:notpresent referid="isvisible">
         <mm:related path="gebruikt,users" constraints="<%= userConstraint %>">
            <mm:import id="isvisible" />
         </mm:related>
      </mm:notpresent>
      <mm:notpresent referid="isvisible">
         <mm:related path="posrel,editwizards,gebruikt,users" max="1" constraints="<%= userConstraint %>">
            <mm:import id="isvisible" />
         </mm:related>
      </mm:notpresent>
      <mm:present referid="isvisible">
         <mm:field name="naam" />
         <table cellpadding="1" cellspacing="0" style="font-size:12px;">
            <tr>
            <td style="padding-right:5px;">
               <mm:related path="<%= path %>" constraints="<%= userConstraint %>" orderby="posrel.pos" directions="UP">
                  <mm:remove referid="noeditwizard" />
                  <mm:node element="editwizards">
                     <mm:field name="type" id="type">
                        <mm:compare referid="type" value="jsp" inverse="true">
                           <a target="workpane" href="<mm:url referids="referrer" page="${jsps}list.jsp">
                                 <mm:param name="wizard"><mm:field name="wizard"/></mm:param>
                                 <mm:param name="nodepath"><mm:field name="nodepath"/></mm:param>
                                 <mm:param name="fields"><mm:field name="fields"/></mm:param>
                                 <mm:param name="pagelength"><mm:field name="pagelength"/></mm:param>
                                 <mm:param name="maxpagecount"><mm:field name="maxpagecount"/></mm:param>
                                 <mm:param name="orderby"><mm:field name="orderby"/></mm:param>
                                 <mm:param name="directions"><mm:field name="directions"/></mm:param>
                                 <mm:param name="maxsize"><%= ph.getMaxSize() %></mm:param>
                                 <mm:param name="search">yes</mm:param>
                                 <mm:param name="origin"><mm:field name="origin"/></mm:param>
                                 <mm:param name="startnodes"><mm:field name="startnodes"/></mm:param>
                                 <mm:field name="constraints">
                                    <mm:isnotempty>
                                       <mm:param name="constraints"><mm:write /></mm:param>
                                    </mm:isnotempty>
                                 </mm:field>
                                 <mm:present referid="creatierubriek">
                                    <mm:param name="creatierubriek"><mm:write referid="creatierubriek" /></mm:param>
                                 </mm:present>
                              </mm:url>"
                              title='<mm:field name="description"/>'>
                        </mm:compare>
                        <mm:compare referid="type" value="jsp">
                           <mm:field name="wizard" jspvar="wizard" vartype="String" write="false">
                           <a target="workpane" href="<mm:url referids="referrer" page="<%= wizard %>" />"
                               title='<mm:field name="description"/>'>
                           </mm:field>
                        </mm:compare>
                     </mm:field>
                     <mm:field name="name"/>
                     </a><br/>
                  </mm:node>
               </mm:related>
               <br/>
               </td>
            </tr>
         </table>
      </mm:present>
   </mm:node>
</mm:list>
</p>
<mm:notpresent referid="noeditwizard">
   <script type="text/javascript">
   <!--
   function showEditorsButton()
   {
      if(document.layers)     //NN4+
      {
       document.layers['buttondiv'].visibility = "show";
      }
      else if(document.getElementById)   // gecko(NN6) + IE 5+
      {
        var obj = document.getElementById('buttondiv');
        obj.style.visibility = "visible";
      }
      else if(document.all)   // IE 4
      {
        document.all['buttondiv'].style.visibility = "visible";
      }
   }
   //-->
   </script>
</mm:notpresent>
<mm:present referid="noeditwizard">
   <script type="text/javascript">
   <!--
   function showEditorsButton()
   {
   }
   //-->
   </script>
</mm:present>