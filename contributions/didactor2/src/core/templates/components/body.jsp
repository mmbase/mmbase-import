
  <div class="contentSubHeader">
     <a href="<mm:treefile page="/components/editcomponent.jsp" objectlist="$includePath" referids="$referids">
                 <mm:param name="component">-1</mm:param>
                 <mm:param name="callerpage"><%= sReturnURL %></mm:param>
                 <mm:param name="components_show_cockpit"><mm:write referid="components_show_cockpit"/></mm:param>
             </mm:treefile>"><fmt:message key="NEWCOMPONENT" /></a>
  </div>

  <% int maxpage = 0; 
     int of = 0;
  %>
  <mm:import externid="of" jspvar="dummy" vartype="Integer">0</mm:import>
  <% of = dummy.intValue(); %>
  <mm:listnodes type="components">
     <mm:import id="maxpage" jspvar="dummy" vartype="Integer"><mm:size/></mm:import>
     <% maxpage = (dummy.intValue()-1) / 10; %>
  </mm:listnodes>
  <div class="contentBody">
     <% if (maxpage > 0) {
           if (of == 0) { 
              %>&lt;<% 
           } else { 
              %><a href="<mm:write referid="link_to_main"/>&of=<%= of-1 %>">&lt;</a><% 
           } 
           for (int i=0; i<=maxpage; i++) { 
              if (of==i) { 
                 %> <%= i+1 %><% 
              } else { 
                 %> <a href="<mm:write referid="link_to_main"/>&of=<%= i %>"><%= i+1 %></a><% 
              }
           }
           if (of == maxpage) {
              %> &gt;<% 
           } else { 
              %> <a href="<mm:write referid="link_to_main"/>&of=<%= of+1 %>">&gt;</a><% 
           } 
        }
     %>

     <table class="body">
        <tr class="listcanvas">
           <td>
              <table class="listcontent">
                 <tr class="listheader">
                    <th>Level</th>
                    <th><fmt:message key="COMPONENT" /></th>
                    <th><fmt:message key="CLASSNAME" /></th>
                 </tr>
                 <mm:listnodes type="components" orderby="name" offset="<%= "" + of*10 %>" max="10">
                    <tr>
                       <mm:remove referid="link"/>
                       <mm:import id="link"><a href="<mm:treefile page="/components/editcomponent.jsp" objectlist="$includePath" referids="$referids">
                                                        <mm:param name="component"><mm:field name="number"/></mm:param>
                                                        <mm:param name="callerpage"><%= sReturnURL %></mm:param>
                                                        <mm:param name="components_show_cockpit"><mm:write referid="components_show_cockpit"/></mm:param>
                                                     </mm:treefile>">
                       </mm:import>

                       <td class="field">
                          <mm:relatednodes type="providers" max="1">
                             <mm:write referid="link" escape="none"/><mm:nodeinfo type="type"/></a>
                          </mm:relatednodes>

                          <mm:relatednodes type="educations" max="1">
                             <mm:write referid="link" escape="none"/><mm:nodeinfo type="type"/></a>
                          </mm:relatednodes>

                          <mm:relatednodes type="classes" max="1">
                             <mm:write referid="link" escape="none"/><mm:nodeinfo type="type"/></a>
                          </mm:relatednodes>
                       </td>

                       <td class="field"><mm:write referid="link" escape="none"/><mm:field name="name"/></a></td>
                       <td class="field"><mm:write referid="link" escape="none"/><mm:field name="classname"/></a></td>
                    </tr>
                 </mm:listnodes>
              </table>
           </td>
        </tr>
     </table>
  </div>
