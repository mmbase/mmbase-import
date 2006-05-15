
            <mm:field id="classrelNumber" name="classrel.number" jspvar="lastClassRel" write="false"/>
            <mm:node referid="classrelNumber">
               <mm:write referid="oldLastActivity" jspvar="oldLastActivity" vartype="Integer">
                  <mm:field name="onlinetime" jspvar="onlinetime" vartype="Integer" write="false">
                     <mm:import id="newOnlineTime" jspvar="newOnlineTime" vartype="Integer"><%=onlinetime.intValue()+Math.min(120,(System.currentTimeMillis()/1000-oldLastActivity.intValue()))%></mm:import>
                     <%
                        Object oldEduObject = session.getAttribute("educationId");
                        String oldEducationId = null;
                        String educationId = request.getParameter("education") + "-" + username + "-" + session.getId();

                        session.setAttribute("educationId", educationId);
                        if (oldEduObject != null)
                        {
                           oldEducationId = oldEduObject.toString();
                        }
                        if (!educationId.equals(oldEducationId))
                        {
                           %>
                              <mm:field name="logincount" jspvar="logincount" vartype="Integer" write="false">
                                 <mm:setfield name="logincount"><%=logincount.intValue()+1%></mm:setfield>
                              </mm:field>
                           <%
                           session.setAttribute( educationId, new Long( System.currentTimeMillis() ) );
                           Object oldEd = session.getAttribute( oldEducationId );
                           if( oldEd != null )
                           {
                             long startReading = ((Long)oldEd).longValue();
                             long duration = System.currentTimeMillis() - startReading;
                             String edId = oldEducationId.substring( 0, oldEducationId.indexOf( "-" ) );
                          %>
                             <rep:event eventtype="<%= nl.didactor.reports.data.EventType.READING_EDUCATION + "" %>" educationId="<%= edId %>" eventvalue="<%= duration + "" %>" note="read education" />
                          <%
                             session.removeAttribute( oldEducationId );
													 }
                        }
                     %>
                  </mm:field>
               </mm:write>
               <mm:setfield name="onlinetime"><mm:write referid="newOnlineTime"/></mm:setfield>
            </mm:node>
