<%
  String userList=""; 
  String roleList="";
  java.util.Vector users = new java.util.Vector();
  java.util.Vector roles = new java.util.Vector();
%>
  <mm:node number="$user" notfound="skipbody">
    <%//user is related directly to education %>
    <mm:relatednodescontainer type="educations">
      <mm:constraint operator="equal" field="number" referid="education"/> 
      <mm:relatednodes>
        <mm:remove referid="sessionexists"/>
        <mm:relatednodes type="virtualclassroomsessions">
          <mm:import id="sessionexists" reset="true"/>
        </mm:relatednodes>
        <mm:present referid="sessionexists">
          <%//here find all users that are related to session directly through education %>
          <mm:relatednodescontainer type="people">
            <mm:constraint operator="in" field="username" value="$username,admin" inverse="true"/>
            <mm:relatednodes>
              <mm:import jspvar="userName"><mm:field name="username"/></mm:import>
              <%
                if (!users.contains(userName)){
                  users.add(userName);
                }
              %>  	
            </mm:relatednodes> 
          </mm:relatednodescontainer>
          <%//here find all users that are related to session through class %>
          <mm:relatednodes type="classes">
            <mm:relatednodescontainer type="people">
              <mm:constraint operator="in" field="username" value="$username,admin" inverse="true"/>
              <mm:relatednodes>
                <mm:import jspvar="userName"><mm:field name="username"/></mm:import>
                <%
                  if (!users.contains(userName)){
                    users.add(userName);
                  }
                %>  
              </mm:relatednodes> 
            </mm:relatednodescontainer> 
          </mm:relatednodes>
        </mm:present>  
      </mm:relatednodes>  
    </mm:relatednodescontainer>
    <mm:notpresent referid="sessionexists">
      <%//user is related to class %>
      <mm:relatednodes type="classes">
        <mm:remove referid="sessionexists"/>
        <mm:relatednodescontainer type="educations">
          <mm:constraint operator="equal" field="number" referid="education"/> 
          <mm:relatednodes>
            <mm:relatednodes type="virtualclassroomsessions">
              <mm:import id="sessionexists" reset="true"/>
            </mm:relatednodes>
          </mm:relatednodes>  
        </mm:relatednodescontainer>
        <mm:present referid="sessionexists">
          <%//users related through class %>
          <mm:relatednodescontainer type="people">
            <mm:constraint operator="in" field="username" value="$username,admin" inverse="true"/>
            <mm:relatednodes>        
              <mm:import jspvar="userName"><mm:field name="username"/></mm:import>
              <%
                if (!users.contains(userName)){
                  users.add(userName);
                }
              %>  
            </mm:relatednodes> 
          </mm:relatednodescontainer>
          <%//users related through education %>
          <mm:relatednodescontainer type="educations">
            <mm:constraint operator="equal" field="number" referid="education"/> 
            <mm:relatednodes>
              <mm:relatednodescontainer type="people">
                <mm:constraint operator="in" field="username" value="$username,admin" inverse="true"/>
                <mm:relatednodes>
                  <mm:import jspvar="userName"><mm:field name="username"/></mm:import>
                  <%
                    if (!users.contains(userName)){
                      users.add(userName);
                    }
                  %>  
                </mm:relatednodes> 
              </mm:relatednodescontainer> 
            </mm:relatednodes>  
          </mm:relatednodescontainer>          
        </mm:present>  
      </mm:relatednodes>    
    </mm:notpresent>  
  </mm:node>
<%
  for (int i=0; i<users.size(); i++){
	String tmpUsername = (String) users.get(i);
    %>
      <mm:listnodescontainer type="people">
        <mm:constraint operator="equal" field="username" value="<%=tmpUsername%>"/>
        <mm:listnodes>
          <mm:remove referid="expert"/>
          <mm:relatednodes type="roles">
            <mm:import id="rolename" reset="true"><mm:field name="name"/></mm:import>
            <mm:compare referid="rolename" value="student" inverse="true">
              <mm:import id="expert" reset="true"/>
            </mm:compare>
          </mm:relatednodes>
        </mm:listnodes>         
      </mm:listnodescontainer>   
      <mm:present referid="expert">
        <%roles.add("expert");%>
      </mm:present>
      <mm:present referid="expert" inverse="true">
        <%roles.add("student");%>
      </mm:present>        
    <%
  } 
  if (users.size()==roles.size()){
    for (int i=0; i<users.size()-1; i++){
      userList += (String) users.get(i) + ",";
      roleList += (String) roles.get(i) + ",";
    }
    if(users.size()>0){
      userList += users.lastElement();
      roleList += roles.lastElement();
    }
  }  
%>  
