<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-0.8" prefix="mm"%>
<%@page import="org.mmbase.bridge.*"%>
<html>
<head>
<title>Forum Demo: add a user to MMBase</title>
</head>
<body>
<mm:cloud>
<blockquote>
<%
  String invalid=null;

  String firstname="";
  String lastname="";
  String email="";
  String password="";
  String login="";
  String url="";
  String website="";

  try {
  firstname=request.getParameter("firstname").trim(); 
  lastname=request.getParameter("lastname").trim();
  email=request.getParameter("email").trim(); 
  password=request.getParameter("password");
  login=request.getParameter("login").trim();
  website=request.getParameter("website").trim();
  url=request.getParameter("url").trim();
  if (firstname.equals("")) {
    invalid="Voornaam niet ingevuld";
  }
  if (login.equals("")) {
    invalid="Login niet ingevuld";
  }
  if (password.equals("")) {
    invalid="Wachtwoord niet ingevuld";
  }
  } catch(Exception e) { invalid="Ongeoorloofde toegang"; }
  if (invalid!=null) {
%>i
<p>De door u ingevoerde gegevens zijn incorrecti (<%=invalid%>).</p>

<p><a href="people.jsp">Opnieuw invoeren</a></p>
<% } else {
    NodeManager people = cloud.getNodeManager("people");
    NodeManager chatter = cloud.getNodeManager("chatter");


    Node person=people.createNode();
    person.setValue("firstname", firstname);
    person.setValue("lastname", lastname);
    person.setValue("email", email);
    person.commit();

    Node chat=chatter.createNode();
    chat.setValue("username", login);
    chat.setValue("password", password);
    chat.commit();
     
    RelationManager relman=cloud.getRelationManager("chatter","people","related");
    Relation rel=relman.createRelation(chat,person);
    rel.commit();
%>
<p>Toegevoegd:</p>

<p><%=person.getValue("firstname")%> <%=person.getValue("lastname")%> </p>
<p>email : <%=person.getValue("email") %></p>

<p>Your login name is <%=chat.getValue("username")%></p>
<p><a href="people.jsp">Meer invoeren</a></p>
<% } %>
</blockquote>
</mm:cloud>
</body></html>