<p class="foot">
<mm:import id="logoutpage"><mm:compare 
     referid="config.method" value="loginpage" inverse="true">logout.jsp</mm:compare><mm:compare
     referid="config.method" value="loginpage">login.jsp?referrer=search_node.jsp</mm:compare></mm:import>
<a href="<mm:write referid="logoutpage" />"><%=m.getString("foot.logout")%></a> - <a href="<mm:url page="search_node.jsp" />"><%=m.getString("foot.home")%></a></p>
<p class="foot2">
<a href="<mm:url page="config.jsp" />"><%=m.getString("foot.configure")%></a> | <a href="<mm:url page="about.jsp" />"><%=m.getString("foot.about")%></a>
</p>
</body>
</html>
