<%    String baseUrl = getServletContext().getInitParameter("internalUrl");

    if (baseUrl == null) {
        throw new ServletException("Please set 'internalUrl' in the web.xml!");
    }
%>

<mm:compare referid="imagelayout" value="0">
     <table width="100%">
        <tr>

   <mm:related path="sizerel,images" orderby="sizerel.pos">
   <mm:import id="imwidth" reset="true"><mm:field name="sizerel.width"/></mm:import>
   <mm:import id="imheight" reset="true"><mm:field name="sizerel.height"/></mm:import>
   <mm:import id="imnum" reset="true"><mm:field name="images.number"/></mm:import>
   <mm:node number="$imnum">
  
      <td valign="top" width="100%">
      <table>
        <tr><td>
         <mm:field name="showtitle">
        <mm:compare value="1">
          <h3> <mm:field name="title"/></h3>
        </mm:compare>
        </mm:field>
       <mm:isgreaterthan referid="imwidth" value="0">
          <mm:isgreaterthan referid="imheight" value="0">
            <mm:import id="template" reset="true">s(<mm:write referid="imwidth"/>x<mm:write referid="imheight"/>)</mm:import>
            <mm:import jspvar="imageUrl" reset="true"><mm:image template="$template"/></mm:import>
            <% imageUrl = baseUrl + imageUrl.substring(imageUrl.indexOf("/img.db")); %>
            <img src="<%= imageUrl %>" border="0"/>
 
          </mm:isgreaterthan>
       </mm:isgreaterthan>
       <mm:islessthan referid="imwidth" value="1">
          <mm:import jspvar="imageUrl" reset="true"><mm:image/></mm:import>
          <% imageUrl = baseUrl + imageUrl.substring(imageUrl.indexOf("/img.db")); %>
          <img src="<%= imageUrl %>" border="0"/>
 
      </mm:islessthan>

      
        </td></tr>
        <tr><td>
        <mm:field name="description"/>
        </td></tr>
     </table>
     </td>
    </mm:node>
    </mm:related>

       </tr>
     </table>
 
    </mm:compare>
    
    <mm:compare referid="imagelayout" value="1">
      <table width="100%">
  <mm:related path="sizerel,images" orderby="sizerel.pos">

   <mm:import id="imwidth" reset="true"><mm:field name="sizerel.width"/></mm:import>
   <mm:import id="imheight" reset="true"><mm:field name="sizerel.height"/></mm:import>
   <mm:import id="imnum" reset="true"><mm:field name="images.number"/></mm:import>
   <mm:node number="$imnum">
        <tr>
       <td valign="top" width="100%">
          <mm:field name="showtitle">
        <mm:compare value="1">
          <h3> <mm:field name="title"/></h3>
        </mm:compare>
        </mm:field>
 
       <mm:isgreaterthan referid="imwidth" value="0">
       <mm:isgreaterthan referid="imheight" value="0">
       <mm:import id="template" reset="true">s(<mm:write referid="imwidth"/>x<mm:write referid="imheight"/>)</mm:import>
       <mm:import jspvar="imageUrl" reset="true"><mm:image template="$template"/></mm:import>
            <% imageUrl = baseUrl + imageUrl.substring(imageUrl.indexOf("/img.db")); %>
            <img src="<%= imageUrl %>" border="0"/>
 
      </mm:isgreaterthan>
      </mm:isgreaterthan>
      <mm:islessthan referid="imwidth" value="1">
       <mm:import jspvar="imageUrl" reset="true"><mm:image/></mm:import>
            <% imageUrl = baseUrl + imageUrl.substring(imageUrl.indexOf("/img.db")); %>
            <img src="<%= imageUrl %>" border="0"/>
 
      </mm:islessthan>
             
       
        </td></tr>
        <tr><td width="100%">
        
      <mm:field name="description"/>
        </td></tr>
     </mm:node>
    </mm:related>
   </table>
    </mm:compare>

