 <div id="you">
   <p><%=getPrompt(m,"you")%>: <%=cloud.getUser().getIdentifier()%></p>
   <p><%=getPrompt(m,"your_rank")%>: <%=cloud.getUser().getRank()%></p>
   <p><a href="<mm:url page="login.jsp?logout=" />"><%=getPrompt(m,"logout")%></a></p>
 </div>
