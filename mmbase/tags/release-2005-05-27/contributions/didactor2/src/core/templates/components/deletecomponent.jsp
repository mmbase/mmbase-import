<%--
  This template deletes a relation between component and education.
--%>

<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>


<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">

   <%@include file="/shared/setImports.jsp" %>

   <mm:import externid="component"/>
   <mm:import externid="callerpage"/>
   <mm:import externid="action1"/>
   <mm:import externid="action2"/>
   <mm:import externid="components_show_cockpit"/>

   <mm:compare referid="components_show_cockpit" value="true">
      <mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
         <mm:param name="extraheader">
            <!-- TODO translate -->
            <title><fmt:message key="DELETECOMPONENT" /></title>
         </mm:param>
      </mm:treeinclude>
   </mm:compare>
   <mm:compare referid="components_show_cockpit" value="true" inverse="true">
      <html>
         <head>
            <link rel="stylesheet" type="text/css" href="<mm:treefile page="/css/base.css" objectlist="$includePath" referids="$referids" />" />
         </head>
         <body>
   </mm:compare>

   <!-- TODO some components may perhaps not be deleted; core... -->
   <!-- TODO in near future settings will be registered. Deleting must then be adjusted -->

   <%-- Check if the yes button is pressed --%>
   <mm:import id="action1text"><fmt:message key="YES" /></mm:import>
   <mm:compare referid="action1" referid2="action1text">
      <mm:node number="$component">
         <mm:listrelationscontainer type="educations">
            <mm:constraint field="educations.number" value="$education"/>

            <mm:listrelations>
               <mm:deletenode/>
            </mm:listrelations>

         </mm:listrelationscontainer>

         <mm:listrelationscontainer type="providers">
            <mm:constraint field="providers.number" value="$provider"/>

            <mm:listrelations>
               <mm:deletenode/>
            </mm:listrelations>

         </mm:listrelationscontainer>
      </mm:node>

      <mm:redirect referids="$referids,component" page="$callerpage"/>
   </mm:compare>


   <%-- Check if the no button is pressed --%>
   <mm:import id="action2text"><fmt:message key="NO" /></mm:import>
   <mm:compare referid="action2" referid2="action2text">
      <mm:redirect referids="$referids,component" page="$callerpage"/>
   </mm:compare>

   <div class="rows">

      <mm:compare referid="components_show_cockpit" value="true">
         <div class="navigationbar">
            <div class="titlebar"></div>
         </div>

         <div class="folders">
            <div class="folderHeader"></div>
            <div class="folderBody"></div>
         </div>

      <div class="mainContent">
      </mm:compare>

         <div class="contentHeader">
            <!-- TODO translate -->
            <fmt:message key="DELETECOMPONENT" />
         </div>
         <div class="contentSubHeader"></div>

         <div class="contentBody">
            <%-- Show the form --%>
            <form name="deletecomponentform" method="post" action="<mm:treefile page="/components/deletecomponent.jsp" objectlist="$includePath" referids="$referids"/>">
               <fmt:message key="DELETETHISCOMPONENTYESNO" />
               <table class="Font">
                  <mm:node referid="component">
                     <mm:fieldlist fields="name,classname">
                        <tr>
                           <td><mm:fieldinfo type="guiname"/></td>
                           <td><mm:fieldinfo type="value"/></td>
                        </tr>
                     </mm:fieldlist>
                  </mm:node>
               </table>

               <input type="hidden" name="component" value="<mm:write referid="component"/>"/>
               <input type="hidden" name="callerpage" value="<mm:write referid="callerpage"/>"/>
               <input class="formbutton" type="submit" name="action1" value="<fmt:message key="YES" />"/>
               <input class="formbutton" type="submit" name="action2" value="<fmt:message key="NO" />"/>
            </form>
         </div>
      <mm:compare referid="components_show_cockpit" value="true">
      </div>
      </mm:compare>
   </div>

   <mm:compare referid="components_show_cockpit" value="true">
      <mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
   </mm:compare>
   <mm:compare referid="components_show_cockpit" value="true" inverse="true">
         </body>
      </html
   </mm:compare>

</mm:cloud>
</mm:content>