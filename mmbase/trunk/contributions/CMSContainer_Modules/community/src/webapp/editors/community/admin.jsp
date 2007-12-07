<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="../globals.jsp" %>

<%@page import="com.finalist.cmsc.services.community.CommunityServiceMysqlImpl"%>
<jsp:useBean id = "community" class="com.finalist.cmsc.services.community.CommunityServiceMysqlImpl" scope="request" />
<jsp:useBean id = "newsletter" class="com.finalist.cmsc.services.community.NewsletterCommunicationServiceMysqlImpl" scope="request" />

<fmt:setBundle basename="community" scope="request" />

<h3><fmt:message key="view.adduser" /></h3>
<p><fmt:message key="view.adduserr" /></p>

<mm:cloud jspvar="cloud" rank="basic user" loginpage="../login.jsp">  
<form name="form" method="post"
			action="addUser.jsp" value="addUser"/>
<table>
				<tr>
					<td>
						<fmt:message key="mod.username" />
					</td>
					<td>
						<input type="text" id="userText" 

name="userText" class="userText" value="${userText}" />
					</td>
				</tr>
				<tr>
					<td>
                    	<fmt:message key="mod.password" />
                	</td>
                	<td>
                    	<input type="text" id="passText" name="passText" 

class="passText" value="${passText}" />
                	</td>
                </tr>
				<tr>
                	<td>
                    	<fmt:message key="mod.firstname" />
                	</td>
                	<td>
                    	<input type="text" id="firstname" name="firstname" 

class="firstname" value="${firstname}" />
                	</td>
                </tr>
				<tr>
                	<td>
                    	<fmt:message key="mod.lastname" />
                	</td>
                	<td>
                    	<input type="text" id="lastname" name="lastname" 

class="lastname" value="${lastname}" />
                	</td>
                </tr>
				<tr>
                	<td>
                    	<fmt:message key="mod.emailaddress" />
                	</td>
                	<td>
                    	<input type="text" id="emailadres" name="emailadres" 

class="emailadres" value="${emailadres}" />
                	</td>
                <tr>
                	<td>
                	   <input type="submit" name="addUser" value="<fmt:message 

key="mod.add" />"/>
                	</td>
            	</tr>
            	<tr>
            	</tr>
            	<tr>
            	</tr>
            	<tr>
            	</tr>
            	<tr>
            	</tr>
            	<tr>
            	</tr>
            	<tr>
            	</tr>
            	<tr>
            	</tr>
            	<tr>
            	</tr>
            	<tr>
            	</tr>
            	<tr>
            	</tr>
            	<tr>
            	</tr>
            	<tr>
            	</tr>
            	<tr>
            	</tr>
            	<tr>
            	</tr>
            	<tr>
            	</tr>
            	<tr>
            	   <td>
            	      Username:<br>
            	      Firstname:<br>
            	      Lastname:<br>
            	      Email adres:
            	   </td>
            	   <c:set var="users" value="<%=community.getAllUsers()%>"/>
            	   <c:forEach var="user" items="${users}">
            	      <td>
            	      	 ${user.userId}<br>
            	      	 ${user.name}<br>
            	      	 ${user.lastname}<br>
            	      	 ${user.emailadress}
            	      </td><td></td><td></td><td></td><td></td><td></td>
            	      <td></td><td></td><td></td><td></td><td></td>
            	   </c:forEach>
            	</tr>
            	<tr>
            	</tr>
            	<tr>
            	</tr>
            	<tr>
            	</tr>
            	<tr>
            	</tr>
            	<tr>
            	</tr>
            	<tr>
            	</tr>
            	<tr>
            	</tr>
            	<tr>
            	</tr>
            	<tr>
            	</tr>
            	<tr>
            	</tr>
            	<tr>
            	</tr>
            	<tr>
            	</tr>
            	<tr>
            	</tr>
            	<tr>
            	</tr>
            	<tr>
            	</tr>
            	<tr>
            	   <td>
            	      Username:<br>
            	      Newsletter key:<br>
            	      Newsletter value:
            	   </td>
            	   <c:set var="users" value="<%=newsletter.getAllNewsPrefs()%>"/>
            	   <c:forEach var="user" items="${users}">
            	      <td>
            	      	 ${user.userId}<br>
            	      	 ${user.newsletterKey}<br>
            	      	 ${user.newsletterValue}
            	      </td><td></td><td></td><td></td><td></td><td></td>
            	      <td></td><td></td><td></td><td></td><td></td>
            	   </c:forEach>
            	</tr>
			</table>
</form>
</mm:cloud>