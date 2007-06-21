<%--
   This is the list of users with role student, as used in the big (main) cockpit.
   It lists all users from the classes of which the current user is a member.

   If the user is connected directly to education it shows all useres in all classes
--%><%@page buffer="200kb"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di"
%><%@page import ="java.util.*"
%><%@page import ="nl.didactor.component.users.PeopleComparator"
%><mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate">
  <mm:log>USERSa</mm:log>
  <jsp:directive.include file="/shared/setImports.jsp" />
  <mm:log>USERSb</mm:log>
  <mm:import id="tmpreferids" reset="true">provider?</mm:import>
  
  <mm:import externid="mode"/>
  <jsp:scriptlet>
    // SortedSet sortsetUsers = new TreeSet(new PeopleComparator());
    SortedSet sortsetEducations = new TreeSet(new PeopleComparator());
  </jsp:scriptlet>

  <mm:list path="people,classrel,educations" constraints="people.number=$user">
    <mm:node element="educations" jspvar="nodeEducation">
      <jsp:scriptlet>
        String[] arrstrEducation = new String[3];
        arrstrEducation[0] = "" + nodeEducation.getNumber();
        arrstrEducation[1] = (String) nodeEducation.getValue("name");
        arrstrEducation[2] = "";
        sortsetEducations.add(arrstrEducation);
      </jsp:scriptlet>
    </mm:node>
  </mm:list>
  <mm:list path="people,classrel,classes,classrel,educations" constraints="people.number=$user">
    <mm:node element="educations" jspvar="nodeEducation">
      <jsp:scriptlet>
        String[] arrstrEducation = new String[3];
        arrstrEducation[0] = "" + nodeEducation.getNumber();
        arrstrEducation[1] = (String) nodeEducation.getValue("name");
        arrstrEducation[2] = "";
        sortsetEducations.add(arrstrEducation);
      </jsp:scriptlet>
    </mm:node>
  </mm:list>
  
  <% for(Iterator it = sortsetEducations.iterator(); it.hasNext();) {
        String[] arrstrEducation = (String[]) it.next();
        %>
        <mm:node number="<%= arrstrEducation[0] %>">
         <%// Do check: is anybody online for this education %>
         <mm:import id="show_this_item" reset="true">false</mm:import>
         <mm:related path="classrel,people">
            <jsp:directive.include file="online_check.jsp" />
         </mm:related>
         <mm:related path="classrel,classes,classrel,people">
           <jsp:directive.include file="online_check.jsp" />
         </mm:related>
         <mm:compare referid="show_this_item" value="true">
            <di:translate key="core.users_educationheader" /><b><mm:field name="name"/></b>
            <br/>
            <mm:related path="classrel,people">
              <jsp:directive.include file="add_person.jsp" />
            </mm:related>

            <mm:related path="classrel,classes" orderby="classes.name">
               <mm:node element="classes" jspvar="nodeClass">

                  <mm:import id="show_this_item" reset="true">false</mm:import>
                  <mm:related path="classrel,people">
                     <jsp:directive.include file="online_check.jsp" />
                  </mm:related>
                  <mm:compare referid="show_this_item" value="true">
                     <di:translate key="core.users_classheader" /><b><a href="<mm:treefile page="/index.jsp" objectlist="$includePath" referids="$tmpreferids">
                     <mm:param name="class"><mm:field name="number"/></mm:param>
                     </mm:treefile>"><mm:field name="name"/></a></b>
                     <br/>
                     <mm:related path="classrel,people">
                       <jsp:directive.include file="add_person.jsp" />
                     </mm:related>
                  </mm:compare>
               </mm:node>
            </mm:related>
         </mm:compare>
      </mm:node>
    <% } %>
    <mm:remove referid="tmpreferids"/>
  </mm:cloud>
</mm:content>
