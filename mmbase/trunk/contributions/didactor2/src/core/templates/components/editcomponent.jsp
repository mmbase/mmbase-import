<%--
  This template adds a new component to a provider or education.
--%>

<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>

<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">

<%@include file="/shared/setImports.jsp" %>
<mm:import externid="component"/>
<mm:import externid="callerpage"/>
<mm:import externid="action1"/>
<mm:import externid="action2"/>
<mm:import externid="componentname"/>
<mm:import externid="components_show_cockpit"/>



   <mm:compare referid="components_show_cockpit" value="true">
      <mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
         <mm:param name="extraheader">
         <!-- TODO translate -->
         <title><fmt:message key="EDITCOMPONENT" /></title>
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



<mm:present referid="action1">
   <mm:compare referid="component" value="-1">
      <mm:listnodescontainer type="components">
         <mm:constraint field="name" referid="componentname"/>

         <mm:listnodes>
            <mm:remove referid="component"/>
            <mm:field id="component" name="number" write="false"/>
         </mm:listnodes>
      </mm:listnodescontainer>
   </mm:compare>

   <mm:listnodes type="providers" id="p">
      <mm:remove referid="isrelated"/>

      <mm:list nodes="$p" path="providers,settingrel,components" constraints="components.number=$component" max="1">
         <mm:import id="isrelated"><mm:field name="settingrel.number"/></mm:import>
      </mm:list>

      <mm:remove referid="levelnum"/>
      <mm:field name="number" id="levelnum"/>
      <mm:remove referid="level"/>
      <mm:import externid="level$levelnum"/>

      <mm:present referid="level$levelnum">
         <mm:notpresent referid="isrelated">
            <mm:createrelation role="settingrel" source="component" destination="p"/>
         </mm:notpresent>
      </mm:present>

      <mm:notpresent referid="level$levelnum">
         <mm:present referid="isrelated">
            <mm:deletenode number="$isrelated"/>
         </mm:present>
      </mm:notpresent>

      <mm:relatednodes type="educations" id="e">
         <mm:remove referid="isrelated"/>
         <mm:list nodes="$e" path="educations,settingrel,components" constraints="components.number=$component" max="1">
            <mm:import id="isrelated"><mm:field name="settingrel.number"/></mm:import>
         </mm:list>

         <mm:remove referid="levelnum"/>
         <mm:field name="number" id="levelnum"/>
         <mm:remove referid="level"/>
         <mm:import externid="level$levelnum"/>

         <mm:present referid="level$levelnum">
            <mm:notpresent referid="isrelated">
               <mm:createrelation role="settingrel" source="component" destination="e"/>
            </mm:notpresent>
         </mm:present>

         <mm:notpresent referid="level$levelnum">
            <mm:present referid="isrelated">
               <mm:deletenode number="$isrelated"/>
            </mm:present>
         </mm:notpresent>

      </mm:relatednodes>
      <mm:node number="$component" notfound="skip">
         <mm:field name="mayrelateclasses">
            <mm:compare value="1">
               <mm:list nodes="$p" path="providers,educations,classes" fields="classes.number" distinct="true">
                  <mm:remove referid="isrelated"/>
                  <mm:node element="classes" id="c">
                     <mm:related path="settingrel,components" constraints="components.number=$component" max="1">
                        <mm:import id="isrelated"><mm:field name="settingrel.number"/></mm:import>
                     </mm:related>
               
                     <mm:remove referid="levelnum"/>
                     <mm:field name="number" id="levelnum"/>
                     <mm:remove referid="level"/>
                     <mm:import externid="level$levelnum"/>

                     <mm:present referid="level$levelnum">
                        <mm:notpresent referid="isrelated">
                           <mm:createrelation role="settingrel" source="component" destination="c"/>
                        </mm:notpresent>
                     </mm:present>

                     <mm:notpresent referid="level$levelnum">
                        <mm:present referid="isrelated">
                           <mm:deletenode number="$isrelated"/>
                        </mm:present>
                     </mm:notpresent>
                  </mm:node>

               </mm:list>
            </mm:compare>
         </mm:field>
      </mm:node>
   </mm:listnodes>

  <mm:redirect referids="$referids,component" page="$callerpage"/>

</mm:present>


<%-- Check if the back button is pressed --%>
<mm:import id="action2text"><fmt:message key="BACK" /></mm:import>
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
         <fmt:message key="EDITCOMPONENT" />
      </div>

      <%@include file="/education/wizards/roles_defs.jsp" %>
      <mm:import id="editcontextname" reset="true">componenten</mm:import>
      <%@include file="/education/wizards/roles_chk.jsp" %>
      <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RWD">
         <div class="contentSubHeader">
            <mm:compare referid="component" value="-1" inverse="true">
               <!-- TODO translate -->
               <a href="<mm:treefile page="/components/deletecomponent.jsp" objectlist="$includePath" referids="$referids">
                           <mm:param name="component"><mm:write referid="component"/></mm:param>
                           <mm:param name="callerpage"><mm:write referid="callerpage"/></mm:param>
                           <mm:param name="components_show_cockpit"><mm:write referid="components_show_cockpit"/></mm:param>
                        </mm:treefile>">delete component</a>
            </mm:compare>
         </div>
      </mm:islessthan>
   
      <div class="contentBody">
         <%-- Show the form --%>
         <form name="editcomponentform" method="post" action="<mm:treefile page="/components/editcomponent.jsp" objectlist="$includePath" referids="$referids"/>">

            <table class="Font">
               <!-- TODO show only components not yet in education or provider -->
               <mm:compare referid="component" value="-1">
                  <mm:listnodes type="components" orderby="name">
                     <mm:first><tr><td><select name="componentname"></mm:first>
                     <option><mm:field name="name"/></option>
                     <mm:last></select></td><td/></tr></mm:last>
                  </mm:listnodes>
               </mm:compare>

               <mm:compare referid="component" value="-1" inverse="true">
                  <mm:node number="$component" notfound="skip">
                     <mm:fieldlist nodetype="components" fields="name,classname">
                        <tr>
                           <td><mm:fieldinfo type="guiname"/></td>
                           <td><mm:fieldinfo type="value"/></td>
                        </tr>
                     </mm:fieldlist>
                  </mm:node>
               </mm:compare>

               <!-- TODO check if the right level is given for a specific component -->
            </table>

            <table class="Font">
               <mm:listnodes type="providers" orderby="name" id="p">
                  <tr>
                     <td>
                        <input type="checkbox" name="level<mm:field name="number"/>" value="on" <mm:list nodes="$p" path="providers,settingrel,components" constraints="components.number=${component}" max="1">checked="checked"</mm:list>/>
                     </td>
                     <td colspan="2"><mm:field name="name"/></td>
                  </tr>
                  <mm:relatednodes type="educations" orderby="name" id="e">
                     <tr>
                        <td></td>
                        <td>
                           <input type="checkbox" name="level<mm:field name="number"/>" value="on" <mm:list nodes="$e" path="educations,settingrel,components" constraints="components.number=${component}" max="1">checked="checked"</mm:list>/>
                        </td>
                        <td><mm:field name="name"/></td>
                     </tr>
                  </mm:relatednodes>
                  <mm:node number="$component" notfound="skip">
                     <mm:field name="mayrelateclasses">
                        <mm:compare value="1">
                           <mm:list nodes="$p" path="providers,educations,classes" orderby="classes.name" fields="classes.number" distinct="true">
                              <mm:node element="classes" id="c">
                                 <tr>
                                    <td></td>
                                    <td>
                                       <input type="checkbox" name="level<mm:field name="number"/>" value="on" <mm:list nodes="$c" path="classes,settingrel,components" constraints="components.number=${component}" max="1">checked="checked"</mm:list>/>
                                    </td>
                                    <td><mm:field name="name"/></td>
                                 </tr>
                              </mm:node>
                           </mm:list>
                        </mm:compare>
                     </mm:field>
                  </mm:node>
               </mm:listnodes>
            </table>

            <input type="hidden" name="component" value="<mm:write referid="component"/>"/>
            <input type="hidden" name="callerpage" value="<mm:write referid="callerpage"/>"/>

            <mm:compare referid="component" value="-1">
               <input class="formbutton" type="submit" name="action1" value="<fmt:message key="CREATE" />"/>
            </mm:compare>

            <mm:compare referid="component" value="-1" inverse="true">
               <input class="formbutton" type="submit" name="action1" value="<fmt:message key="UPDATE" />"/>
            </mm:compare>

            <input class="formbutton" type="submit" name="action2" value="<fmt:message key="BACK" />"/>
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
   </html>
</mm:compare>

</mm:cloud>
</mm:content>
