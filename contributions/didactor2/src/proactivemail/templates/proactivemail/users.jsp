<!-- find all related people -->
        <mm:listnodescontainer type="proactivemailtemplates">
          <mm:constraint operator="LIKE" field="name" referid="templatename" />
          <mm:constraint operator="EQUAL" field="active" value="0" inverse="true"/>
          <mm:listnodes >
            <mm:relatednodescontainer type="providers">
              <mm:relatednodes>
                <mm:relatednodescontainer type="educations">
                    <mm:relatednodes>
                      <mm:relatednodescontainer type="classes">
                          <mm:relatednodes>
                            <mm:relatednodescontainer type="people">
                                <mm:relatednodes>
                                  <%@include file="getFields.jsp" %>
                                </mm:relatednodes>
                            </mm:relatednodescontainer>
                          </mm:relatednodes>
                      </mm:relatednodescontainer>
                      <mm:relatednodescontainer type="people">
                        <mm:relatednodes>
                           <%@include file="getFields.jsp" %>
                        </mm:relatednodes>
                      </mm:relatednodescontainer>
                    </mm:relatednodes>
                </mm:relatednodescontainer>
              </mm:relatednodes>
            </mm:relatednodescontainer>

            <mm:relatednodescontainer type="educations">
                <mm:relatednodes>
                  <mm:relatednodescontainer type="classes">
                      <mm:relatednodes>
                        <mm:relatednodescontainer type="people">
                          <mm:relatednodes>
                            <%@include file="getFields.jsp" %>
                          </mm:relatednodes>
                        </mm:relatednodescontainer>
                      </mm:relatednodes>
                  </mm:relatednodescontainer>
                  <mm:relatednodescontainer type="people">
                    <mm:relatednodes>
                      <%@include file="getFields.jsp" %>
                    </mm:relatednodes>
                  </mm:relatednodescontainer>
                </mm:relatednodes>
            </mm:relatednodescontainer>


            <mm:relatednodescontainer type="classes">
                <mm:relatednodes>
                  <mm:relatednodescontainer type="people">
                    <mm:relatednodes>
                      <%@include file="getFields.jsp" %>
                    </mm:relatednodes>
                  </mm:relatednodescontainer>
                </mm:relatednodes>
            </mm:relatednodescontainer>


            <mm:relatednodescontainer type="people">
              <mm:relatednodes>
                <%@include file="getFields.jsp" %>
              </mm:relatednodes>
            </mm:relatednodescontainer>
<!-- if role to send is set, test it with existing users -->
            <% 
              Iterator it = emailUsers.values().iterator();
              while ( it.hasNext() ) {
                Object[] o = (Object[])it.next();
                if ( o[4] != null && ((String)o[4]).trim().length() > 0 && o[3] != null && "admin".compareTo((String)o[3]) != 0 ) {
            %>
                  <mm:import id="userNumber" reset="true"><%=o[0]%></mm:import>
                  <mm:relatednodescontainer type="roles">
                    <mm:relatednodes>
                      <mm:first><mm:import id="roleTest" /></mm:first>
                      <mm:notpresent referid="hasRole">
                        <mm:import id="roleName" reset="true" jspvar="jsp_roleName"><mm:field name="name"/></mm:import>
                        <di:hasrole referid="userNumber" role="<%=jsp_roleName%>">
                          <mm:import id="hasRole"/>
                        </di:hasrole>
                      </mm:notpresent>
                    </mm:relatednodes>
                  </mm:relatednodescontainer>
                  <mm:present referid="roleTest">
                    <mm:notpresent referid="hasRole">
                       <%
                       it.remove();
                       %>                     
                    </mm:notpresent>
                  </mm:present>
                  <mm:remove referid="hasRole"/>
                  <mm:remove referid="roleTest"/>
            <% 
                } else {
                    it.remove();
                }
              }
            %>
            
            <mm:field name="frompart" jspvar="tmp_from" vartype="String">
              <%jsp_from = tmp_from;%>
            </mm:field>
            <mm:field name="body" jspvar="tmp_body" vartype="String">
              <%jsp_body = tmp_body;%>
            </mm:field>
            <mm:field name="subject" jspvar="tmp_subject" vartype="String">
              <%jsp_subject = tmp_subject;%>
            </mm:field>
          </mm:listnodes>
        </mm:listnodescontainer>
