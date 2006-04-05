<mm:related path="parent,rubriek2" fields="rubriek2.number"><%
    
    // *** create popup for micro-sites ***
    %><mm:field name="rubriek2.number" jspvar="rubriek2_number" vartype="String" write="false"
    ><mm:node number="<%= rubriek2_number %>"
        ><mm:aliaslist
            ><mm:notpresent referid="alias"
                >"javascript:launchCenter('index.jsp', 'popup_<mm:write/>', 800, 600, ',scrollbars,resizable=yes');setTimeout('newwin.focus();',250)"</mm:notpresent
            ><mm:import id="alias"
        /></mm:aliaslist
        ><mm:notpresent referid="alias"
            >"javascript:launchCenter('index.jsp','popup_<%= rubriek2_number %>', 800, 600, ',scrollbars,resizable=yes');setTimeout('newwin.focus();',250)"</mm:notpresent
        ><mm:remove referid="alias"
    /></mm:node
    ></mm:field
    ><mm:import id="popup" 
/></mm:related
><mm:notpresent referid="popup"
   ><mm:field name="number" jspvar="referer_number" vartype="String" write="false"
      ><mm:related path="discountrel,pagina" fields="pagina.number" max="1"
         >index.jsp.jsp?p=<mm:field name="pagina.number" /><mm:import id="popup" 
      /></mm:related
   ></mm:field
></mm:notpresent
><mm:notpresent referid="popup"
    >"index.jsp?p=<mm:aliaslist
                    ><mm:notpresent referid="alias"
                        ><mm:write 
                        /><mm:import id="alias" 
                    /></mm:notpresent
                ></mm:aliaslist
                ><mm:notpresent referid="alias"><mm:field name="number" /></mm:notpresent
                ><mm:remove referid="alias" 
            />" target="_top"</mm:notpresent
><mm:remove referid="popup" 
/><mm:remove referid="externalwebsite" />
