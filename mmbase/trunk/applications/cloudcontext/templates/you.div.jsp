 <div id="you">
   <p><%=m.getString("you")%>: <%=cloud.getUser().getIdentifier()%></p>
   <p><%=m.getString("your_rank")%>: <%=cloud.getUser().getRank()%></p>
   <p><a href="<mm:url page="login.jsp?logout=" />"><%=m.getString("logout")%></a></p>
 </div>
