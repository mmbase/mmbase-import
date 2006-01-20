      <mm:import id="tmpreferids" reset="true">provider?</mm:import>
      <%
         SortedMap sortmapEducations = new TreeMap();
         ArrayList arliEducations = new ArrayList();
         String sUserID = null;
      %>
      <mm:node number="$user" jspvar="nodeUser">
         <%
            sUserID = "" + nodeUser.getNumber();

            for(Iterator it = educationPeopleConnector.relatedEducations("" + nodeUser.getNumber()).iterator(); it.hasNext(); )
            {
               String sEducationID = (String) it.next();
               %>
                  <mm:node number="<%= sEducationID %>" jspvar="nodeEducation">
                     <%
                        sortmapEducations.put((String) nodeEducation.getValue("name"), sEducationID);
                     %>
                  </mm:node>
               <%
            }
         %>
      </mm:node>

      <%
         for(Iterator it = sortmapEducations.keySet().iterator(); it.hasNext();)
         {
            String sEducationName = (String) it.next();
            String sEducationID = (String) sortmapEducations.get(sEducationName);
            String sConstraints;
            %>
               <mm:node number="<%= sEducationID %>">
                  <%
                     sConstraints = "people.number=" + sUserID;
                  %>
                  <%// the person has connected to the education directly %>
                  <mm:related path="classrel,people" constraints="<%= sConstraints %>">
                     <mm:node element="people">
                        <%
                           String[] arrstrTemp = new String[3];
                           arrstrTemp[0] = sEducationID;
                           arrstrTemp[1] = sEducationName;
                           arrstrTemp[2] = null;
                           arliEducations.add(arrstrTemp);
                        %>
                     </mm:node>
                  </mm:related>
                  <%// the person has connected to the education throw the class %>
                  <mm:related path="classrel,classes,classrel,people" constraints="<%= sConstraints %>">
                     <mm:node element="classes" jspvar="nodeClass">
                        <%
                           String[] arrstrTemp = new String[3];
                           arrstrTemp[0] = sEducationID;
                           arrstrTemp[1] = sEducationName;
                           arrstrTemp[2] = "" + nodeClass.getNumber();
                           arliEducations.add(arrstrTemp);
                        %>
                     </mm:node>
                  </mm:related>
               </mm:node>
            <%
         }


         for(Iterator it = arliEducations.iterator(); it.hasNext();)
         {
            String[] arrstrEducation = (String[]) it.next();
            %>
               <nobr>
                  <img src="<mm:treefile write="true" page="/gfx/icon_course_notdone.gif" objectlist="$includePath" />" width="13" height="11" border="0" alt="" />
                  <a href="<mm:treefile page="/education/index.jsp" objectlist="$includePath" referids="$tmpreferids">
                              <mm:param name="education"><%= arrstrEducation[0] %></mm:param>
                              <mm:param name="class"><%= arrstrEducation[2] %></mm:param>
                           </mm:treefile>" class="users">
                     <%@include file="show_education_name.jsp"%>
                  </a>
               </nobr>
               <br/>
            <%
         }
      %>
      <mm:remove referid="tmpreferids"/>
