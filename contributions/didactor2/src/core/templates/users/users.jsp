<%--
   This is the list of users with role student, as used in the big (main) cockpit.
   It lists all users from the classes of which the current user is a member.

   If the user is connected directly to education it shows all useres in all classes
--%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<%@page import ="java.util.Iterator" %>
<%@page import ="java.util.SortedSet" %>
<%@page import ="java.util.TreeSet" %>

<%@page import ="nl.didactor.component.users.PeopleComparator" %>


<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
   <%@include file="/shared/setImports.jsp" %>

   <mm:import externid="mode"/>

   <%
      SortedSet sortsetUsers = new TreeSet(new PeopleComparator());
   %>

   <mm:node referid="user">
      <mm:related path="classrel,educations">
         <mm:node element="educations">
            <mm:related path="classrel,people">
               <%@include file="add_person.jsp"%>
            </mm:related>
            <mm:related path="related,classes">
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

   <%
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
                     <img src="<mm:treefile write="true" page="/gfx/icon_offline.gif" objectlist="$includePath" />" width="6" height="12" border="0" alt="offline" />
                  </mm:compare>
                  <mm:compare referid="isonline" value="1">
                     <img src="<mm:treefile write="true" page="/gfx/icon_online.gif" objectlist="$includePath" />" width="6" height="12" border="0" alt="online" />
                  </mm:compare>
                  <mm:remove referid="isonline" />
                  <%= arrstr[2] %> <%= arrstr[1] %>
               </a>
               <br />
            </mm:node>
         <%
      }
   %>

</mm:cloud>
</mm:content>
