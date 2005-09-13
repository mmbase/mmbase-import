<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>

<di:hasrole role="systemadministrator">

   Deleting forums...
   <mm:listnodes type="forums">
      <mm:deletenode deleterelations="true"/>
   </mm:listnodes>
   done. <br>
   Deleting forumthreads...
   <mm:listnodes type="forumthreads">
      <mm:deletenode deleterelations="true"/>
   </mm:listnodes>
   done. <br>
   Deleting forummessages...
   <mm:listnodes type="forummessages">
      <mm:deletenode deleterelations="true"/>
   </mm:listnodes>
   done. <br>

   <mm:listnodes type="components" constraints="name='forum'">
      <mm:deletenode deleterelations="true"/>
   </mm:listnodes>

   <mm:listnodes type="typerel" constraints="dnumber='forumthreads'">
      <mm:deletenode deleterelations="true"/>
   </mm:listnodes>
   <mm:lisnodes type="typerel" constraints="snumber='forumthreads'">
      <mm:deletenode deleterelations="true"/>
   </mm:listnodes>

</di:hasrole>

</mm:cloud>
</mm:content>