<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content expires="0">
<% response.setContentType("text/xml"); %>
<%@page import = "java.lang.Integer" %>
<%@page import = "java.lang.String" %>
<%@page import = "java.util.Random" %>
<%@page import = "java.util.HashMap" %>
<%@page import = "java.util.ArrayList" %>
<%@page import = "java.util.Collection" %>
<%@page import = "java.util.Iterator" %>
<mm:cloud method="logout">
<mm:import externid="username" required="true" jspvar="username"/>
<mm:import externid="password" required="true" jspvar="password"/>
<mm:import externid="templatename" required="true" jspvar="templatename"/>

<mm:import id="subject" jspvar="jsp_subject" vartype="String"/>
<mm:import id="body" jspvar="jsp_body" vartype="String"/>
<mm:import id="from" jspvar="jsp_from" vartype="String"/>

<!--
Collect all users which should be email using this template, XML output 
 -->

<% 
  HashMap emailUsers = new HashMap();
  ArrayList removeUsers = new ArrayList();
  long lastSent = System.currentTimeMillis()/1000;
%>

<%
    try{ 
%>
<!-- find last sent time -->
			<mm:cloud username="$username" password="$password" jspvar="cloud" method="pagelogon">
        <mm:listnodescontainer type="proactivemailbatches">
          <mm:constraint operator="LIKE" field="name" referid="templatename" />
          <mm:listnodes>
            <mm:last>
              <mm:import id="batchesFound" />
              <mm:import id="lastsent" jspvar="jsp_lastsent" vartype="Long"><mm:field name="end_time" /></mm:import>
              <% lastSent = jsp_lastsent.longValue(); %>
            </mm:last>
          </mm:listnodes>
        </mm:listnodescontainer>
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
                if ( o[4] != null && ((String)o[4]).trim().length() > 0 && "admin".compareTo((String)o[3]) != 0 ) {
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
        
<!-- make output, and do last filtering  -->        
        <template>            
          <from><%=jsp_from%></from>
          <subject><%=jsp_subject%></subject>
          <body><%=jsp_body%></body>
          <users>
              <% 
                Iterator it = emailUsers.values().iterator();
                while ( it.hasNext() ) {
                  Object[] o = (Object[])it.next(); 
              %>
                  <%@include file="doFiltering.jsp" %>
              <%    
                }
              %>
              <% 
                Iterator it1 = ((Collection)emailUsers.values()).iterator();
                while ( it1.hasNext() ) {
                  Object[] o = (Object[])it1.next();
              %>
                <user>
                  <firstname><%=(String)o[1]%></firstname>
                  <lastname><%=(String)o[2]%></lastname>
                  <username><%=(String)o[3]%></username>
                  <email><%=(String)o[4]%></email>
                  <lastactivity><%=(String)o[5]%></lastactivity>
                </user>            
              <% 
                }
              %>
          </users>
        </template>            
        
			</mm:cloud> 
<%
    } catch (Exception ex) {
%>
        <error>
          <% ex.toString(); %>
        </error>
<%
    }
%>        
</mm:cloud> 
</mm:content>