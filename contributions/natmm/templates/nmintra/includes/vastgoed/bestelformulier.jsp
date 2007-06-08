<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@include file="/taglibs.jsp" %>
<mm:cloud>

<html>
   <head>
   </head>
   <body>
      
      <h3>Mijn bestelling:</h3>
      
      <html:form action="/nmintra/includes/vastgoed/BestelAction" method="GET">
         
         Naam:<html:text property="naam"/><br/>
         E-mail:<html:text property="email"/><br/>
         Eendheid:
         <html:select property="eendheid">
            <html:option value="test a">test a</html:option>
            <html:option value="test b">test b</html:option>
         </html:select><br/>
         
         Alternatief bezorgadres:<html:textarea property="bezorgadres"></html:textarea><br/>
         
         <br/>
         
         <html:link 
            page="/nmintra/includes/vastgoed/KaartenInitAction.eb">
            purchase maps
         </html:link>
         <br/><br/>
         
         
         <table border="1">
            <tr>
               <td>kaartsoort</td>
               <td>natuurgebied,nenheid,regio,co�rdinaten etc.</td>
               <td>schaal of formaat</td>
               <td>aantal</td>
               <td>gerold of gevouwen</td>
               <td></td>
               <td></td>
            </tr>
            
            <logic:iterate id="item" name="vastgoed_shoppingbasket" type="nl.leocms.vastgoed.KaartenForm" scope="session" 
                           indexId="i" property="items">
               
               
               <tr>
                  <td>
                  <%
                   	String[] kartNodes = item.getSel_Kaart();
                   	for (int iNodes = 0; iNodes < kartNodes.length; iNodes++) {
                   		String nodeNumber = kartNodes[iNodes];	
                  %>
                  <mm:node number="<%=nodeNumber%>">
                  	<mm:field name="naam"/>
                  </mm:node>
                  <% 			
                  	 if (iNodes != (kartNodes.length - 1)) {
                  	 	out.print(", ");
                  	 }
                   }
                   %>
                  </td>
                  
                  <td><%= item.getKaartType()%></td>
                  <td><%= item.getSchaalOfFormaat()%></td>
                  <td><%= item.getAantal()%></td>
                  <td><%= item.getGevouwenOfOpgerold()%></td>
                  <td>
                     
                     <html:link 
                        page="/nmintra/includes/vastgoed/KaartenInitAction.eb" 
                        paramId="number" paramName="i">
                        update
                     </html:link>
                     
                  </td>
                  <td>
                     
                     <html:link 
                        page="/nmintra/includes/vastgoed/BestelAction.eb" 
                        paramId="delete" paramName="i">
                        verwijderen
                     </html:link>
                     
                  </td>
               </tr>
               
            </logic:iterate>
            
         </table>
         
         <br/>
         <input type="submit" name="send" value="verzenden"/>
         
      </html:form>
      
   </body>
</html>

</mm:cloud>
