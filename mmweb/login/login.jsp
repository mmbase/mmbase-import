<%@page import="java.util.*,javax.servlet.http.*,org.mmbase.bridge.*"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<%
// what a hackery.
{
// are we logged in with cookies?
Cookie cookie[] = request.getCookies();
String userid = null;
if(cookie != null) {
for(int i=0; i<cookie.length; i++) {
  if(cookie[i].getName().equals("ca")) userid = cookie[i].getValue();
}
}
boolean loginCookie = userid != null;

// are we logged in on the cloud?
String id = null;
Object o = session.getAttribute("cloud_mmbase");
Cloud cloud = (Cloud) o;
if(o != null) {
  org.mmbase.security.UserContext u = cloud.getUser();
  if(u != null && u.isValid()) {
    id = u.getIdentifier();
  }
}
boolean loginCloud = id != null;

Object nodeNumber = session.getAttribute("user_node_number");
boolean loginSession = nodeNumber != null;

if(loginCloud && !loginCookie) {
  // login cookie
  // find user by id, set cookie ca to userid and cw to password
  NodeList list = cloud.getNodeManager("users").getList("account = '"+id+"'", null, null);
  if(list != null && list.size() == 1) {
    Node node = list.getNode(0);
    String password = node.getStringValue("password");
    response.addCookie(new Cookie("ca", id));
    response.addCookie(new Cookie("cw", password));
    loginCookie = true;
  }
}

if(loginCookie && !loginSession) {
  // loginSession
  String firstname = null;
  String lastname = null;
  String number = null;
%>
<mm:cloud>
<% String constraints = "account='" + userid + "'"; %>
<mm:listnodes type="users" constraints="<%=constraints%>">
<mm:node>
<mm:field name="firstname" jspvar="fname" vartype="String" write="false">
<% firstname = fname; %>
</mm:field>
<mm:field name="lastname" jspvar="lname" vartype="String" write="false">
<% lastname = lname; %>
</mm:field>
<mm:field name="number" jspvar="nb" vartype="String" write="false">
<% number = nb; %>
</mm:field>
</mm:node>
</mm:listnodes>
</mm:cloud>
<%
    if(firstname != null && lastname != null) {
      session.setAttribute("user_node_name", firstname + " " + lastname);
      session.setAttribute("user_node_number", new Integer(number));
    }
  }
}
%>
