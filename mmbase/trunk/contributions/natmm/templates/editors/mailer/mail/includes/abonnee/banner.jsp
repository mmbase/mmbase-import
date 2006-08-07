<%@include file="../defaulttext.jsp"%>

<div id="banner">
   <h1><mm:write referid="bannertitle" escape="none"/></h1>
   <div id="secnav">
      <ul>
         <li><a href="<mm:write referid="bannerurl" escape="none"/>" class="link"><mm:write referid="bannerurltext" escape="none"/></a></li>

         <mm:present referid="login">
            <li><a href="over.jsp?login" class="about"><mm:write referid="bannerovertext" escape="none"/></a></li>
         </mm:present>


         <mm:notpresent referid="login">
            <%-- <li><a href="over.jsp" class="about"><mm:write referid="bannerovertext" escape="none"/></a></li> --%>
            <li><a href="index.jsp?action=logout" class="logout"><mm:write referid="bannerlogouttext" escape="none"/></a></li>
            <%-- <li><a href="help.jsp" class="help"><mm:write referid="bannerhelptext" escape="none"/></a></li> --%>
         </mm:notpresent>
      </ul>
   </div>
</div>








