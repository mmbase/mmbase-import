<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">
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
   Deleting forummessages that aren't linked to anything anymore...
   <mm:listnodes type="forummessages">
      <mm:countrelations>
         <mm:isgreaterthan value="0" inverse="true">
            <mm:deletenode deleterelations="true"/>
         </mm:isgreaterthan>
      </mm:countrelations>
   </mm:listnodes>
   done. <br>

   <mm:listnodes type="components" constraints="name='forum'">
      <mm:deletenode deleterelations="true"/>
   </mm:listnodes>

   <mm:listnodes type="typerel" constraints="dnumber='forumthreads'">
      <mm:deletenode deleterelations="true"/>
   </mm:listnodes>
   <mm:listnodes type="typerel" constraints="snumber='forumthreads'">
      <mm:deletenode deleterelations="true"/>
   </mm:listnodes>

</di:hasrole>

</mm:cloud>
</mm:content>
