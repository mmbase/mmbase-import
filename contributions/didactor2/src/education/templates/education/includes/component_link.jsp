   <mm:related path="rolerel,components">
      <mm:node element="components">
         <a href="<%= request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() %>/components/edit.jsp?component=component.<mm:field name="name"/>"><mm:field name="name"/></a>
      </mm:node>
   </mm:related>
