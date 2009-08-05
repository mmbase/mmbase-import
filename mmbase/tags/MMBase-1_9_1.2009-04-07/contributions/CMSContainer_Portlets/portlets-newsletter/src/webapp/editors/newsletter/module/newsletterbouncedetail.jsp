<%@include file="globals.jsp"
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<mm:cloud jspvar="cloud" rank="basic user" method='http'>
   <mm:import externid="objectnumber" vartype="Integer" required="true"/>
   <c:set var="showitemTitle"><fmt:message key="newsletter.bounce.title" /></c:set>
<cmscedit:head title="${showitemTitle}" titleMode="plain"/>
<body>
	<c:set var="sideblockTitle"><fmt:message key="newsletter.bounce.title" /></c:set>
	<cmscedit:sideblock title="${sideblockTitle}" titleMode="plain"
		 titleClass="side_block_green" titleStyle="width: 100%">         
            <table class="listcontent">
              
               <tr>
                  <td><fmt:message key="newsletter.bounce.number" /></td>
                  <td> <c:out value="${bounce.id}"/></td>
               </tr>
               <tr>
                  <td><fmt:message key="newsletter.bounce.subscriber" /></td>
                  <td> <c:out value="${bounce.userName}"/></td>
               </tr>
               <tr>
                  <td><fmt:message key="newsletter.bounce.newsletter" /></td>
                  <td> <c:out value="${bounce.newsLetterTitle}"/></td>
               </tr>
               <tr>
                  <td><fmt:message key="newsletter.bounce.bouncedate" /></td>
                  <td> <c:out value="${bounce.bounceDate}"/></td>
               </tr>
               <tr>
                  <td><fmt:message key="newsletter.bounce.bouncecontent" /></td>
                  <td> <c:out value="${bounce.bounceContent}"/></td>
               </tr>
            </table>
           <br />
			<ul class="shortcuts">
               <li class="close">
	               <a href="#" onClick="window.close()"><fmt:message key="newsletter.bounce.window.close" /></a>
				</li>
			</ul>
	</cmscedit:sideblock>            
   </body>
</mm:cloud>
</html:html>
</mm:content>