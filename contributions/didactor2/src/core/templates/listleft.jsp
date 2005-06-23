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
                  <mm:relatednodes type="people" constraints="<%= sConstraints %>">
                     <%
                        String[] arrstrTemp = new String[3];
                        arrstrTemp[0] = sEducationID;
                        arrstrTemp[1] = sEducationName;
                        arrstrTemp[2] = null;
                        arliEducations.add(arrstrTemp);
                     %>
                  </mm:relatednodes>

                  <%// the person has connected to the education throw the class %>
                  <mm:related path="classes,people" constraints="<%= sConstraints %>">
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
               <a href="<mm:treefile page="/education/index.jsp" objectlist="$includePath" referids="$referids">
                           <mm:param name="education"><%= arrstrEducation[0] %></mm:param>
                           <mm:param name="class"><%= arrstrEducation[2] %></mm:param>
                        </mm:treefile>" class="users" />
                  <%= arrstrEducation[1] %>
                  <%
                     if(arrstrEducation[2] != null)
                     {
                        %>
                           <mm:node number="<%= arrstrEducation[2] %>" jspvar="nodeClass">
                              (<%
                                 String sClassName = (String) nodeClass.getValue("name");
                                 if(sClassName.length() > 7)
                                 {
                                    out.println(sClassName.substring(0, 7));
                                    %>...<%
                                 }
                                 else
                                 {
                                    out.println(sClassName);
                                 }
                              %>)
                           </mm:node>
                        <%
                     }
                  %>
               </a>
               </nobr>
               <br />
            <%
         }
      %>
