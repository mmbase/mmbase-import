<%--
   This is the list of users with role student, as used in the big (main) cockpit.
   It lists all users from the classes of which the current user is a member.

   If the user is connected directly to education it shows all useres in all classes
--%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<%@page import ="java.util.Iterator" %>
<%@page import ="java.util.HashSet" %>
<%@page import ="java.util.SortedSet" %>
<%@page import ="java.util.TreeSet" %>

<%@page import ="nl.didactor.component.users.PeopleComparator" %>


<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">
   <%@include file="/shared/setImports.jsp" %>
   <mm:import id="tmpreferids" reset="true">provider?</mm:import> 

   <mm:import externid="mode"/>

   <%
//      SortedSet sortsetUsers = new TreeSet(new PeopleComparator());
      SortedSet sortsetEducations = new TreeSet(new PeopleComparator());
   %>

   <mm:list path="people,classrel,educations" constraints="people.number=$user">
      <mm:node element="educations" jspvar="nodeEducation">
         <%
            String[] arrstrEducation = new String[3];
            arrstrEducation[0] = "" + nodeEducation.getNumber();
            arrstrEducation[1] = (String) nodeEducation.getValue("name");
            arrstrEducation[2] = "";
            sortsetEducations.add(arrstrEducation);
         %>
      </mm:node>
   </mm:list>
   <mm:list path="people,classrel,classes,classrel,educations" constraints="people.number=$user">
      <mm:node element="educations" jspvar="nodeEducation">
         <%
            String[] arrstrEducation = new String[3];
            arrstrEducation[0] = "" + nodeEducation.getNumber();
            arrstrEducation[1] = (String) nodeEducation.getValue("name");
            arrstrEducation[2] = "";
            sortsetEducations.add(arrstrEducation);
         %>
      </mm:node>
   </mm:list>

   <%
      for(Iterator it = sortsetEducations.iterator(); it.hasNext();)
      {
         String[] arrstrEducation = (String[]) it.next();
         %>
      <mm:node number="<%= arrstrEducation[0] %>">
         <%// Do check: is anybody online for this education %>
         <mm:import id="show_this_item" reset="true">false</mm:import>
         <mm:related path="classrel,people">
            <%@include file="online_check.jsp"%>
         </mm:related>
         <mm:related path="classrel,classes,classrel,people">
            <%@include file="online_check.jsp"%>
         </mm:related>
         <mm:compare referid="show_this_item" value="true">
            <di:translate key="core.users_educationheader" /><b><mm:field name="name"/></b>
            <br/>
            <mm:related path="classrel,people">
               <%@include file="add_person.jsp"%>
            </mm:related>

            <mm:related path="classrel,classes" orderby="classes.name">
               <mm:node element="classes" jspvar="nodeClass">

                  <mm:import id="show_this_item" reset="true">false</mm:import>
                  <mm:related path="classrel,people">
                     <%@include file="online_check.jsp"%>
                  </mm:related>
                  <mm:compare referid="show_this_item" value="true">
                     <di:translate key="core.users_classheader" /><b><a href="<mm:treefile page="/index.jsp" objectlist="$includePath" referids="$tmpreferids">
                                          <mm:param name="class"><mm:field name="number"/></mm:param>
                                       </mm:treefile>"><mm:field name="name"/></a></b>
                     <br/>
                     <mm:related path="classrel,people">
                        <%@include file="add_person.jsp"%>
                     </mm:related>
                  </mm:compare>
               </mm:node>
            </mm:related>
         </mm:compare>
      </mm:node>
         <%
      }
   %>

<%--
   <mm:list path="people,classrel,classes" constraints="people.number=$user" orderby="classes.name">
      <mm:node element="classes" jspvar="nodeClass">
         <%
            if(!hsetClasses.contains("" + nodeClass.getNumber()))
            {
               %>
                  <mm:import id="show_this_item" reset="true">false</mm:import>

                  <mm:compare referid="show_this_item" value="true">
                     class:<b><mm:field name="name"/></b>
                     <br/>
                     <mm:related path="classrel,people">
                        <%@include file="add_person.jsp"%>
                     </mm:related>
                  </mm:compare>
               <%
            }
         %>
      </mm:node>
   </mm:list>
--%>
<%--
   <mm:node referid="user">
      <mm:related path="classrel,educations">
         <mm:node element="educations">
            <mm:related path="classrel,people">
               <%@include file="add_person.jsp"%>
            </mm:related>
            <mm:related path="classrel,classes">
               <mm:node element="classes">
                  <mm:related path="classrel,people">
                     <%@include file="add_person.jsp"%>
                  </mm:related>
               </mm:node>
            </mm:related>
         </mm:node>
      </mm:related>
      <mm:related path="classrel,classes">
         <mm:node element="classes">
            <mm:related path="classrel,people">
               <%@include file="add_person.jsp"%>
            </mm:related>
         </mm:node>
      </mm:related>
   </mm:node>
--%>

   <%
/*
      for(Iterator it = sortsetUsers.iterator(); it.hasNext(); )
      {
         String[] arrstr = (String[]) it.next();
         %>
            <mm:node number="<%= arrstr[0] %>">
               <a href="<mm:treefile page="/portfolio/index.jsp" objectlist="$includePath" referids="$referids">
                           <mm:param name="contact"><%= arrstr[0] %></mm:param>
                        </mm:treefile>" class="users">
                        <%-- Online/offline status is retrieved using the nl.didactor.builders.PeopleBuilder class  --%>
                  <mm:field name="isonline" id="isonline" write="false" />
                  <mm:compare referid="isonline" value="0">
                     <img src="<mm:treefile write="true" page="/gfx/icon_offline.gif" objectlist="$includePath" />" width="6" height="12" border="0" title="offline" alt="offline" />
                  </mm:compare>
                  <mm:compare referid="isonline" value="1">
                     <img src="<mm:treefile write="true" page="/gfx/icon_online.gif" objectlist="$includePath" />" width="6" height="12" border="0" title="online" alt="online" />
                  </mm:compare>
                  <mm:remove referid="isonline" />
                  <%= arrstr[2] %> <%= arrstr[1] %>
               </a>
               <br />
            </mm:node>
         <%
      }
*/
   %>
   <mm:remove referid="tmpreferids"/>
</mm:cloud>
</mm:content>
