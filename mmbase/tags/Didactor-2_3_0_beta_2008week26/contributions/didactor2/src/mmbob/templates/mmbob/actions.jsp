<%@ page contentType="text/html; charset=utf-8" language="java" 
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" 
%><%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" 
%><mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<mm:import externid="action" />
<mm:import externid="forumid" />
<mm:import externid="folderaction" />

<!-- login part -->
<%@ include file="getposterid.jsp" %>
<!-- end login part -->

<mm:import id="adminmode">false</mm:import>
<mm:import externid="admincheck" />
<mm:present referid="admincheck">
	  <mm:nodefunction set="mmbob" name="getForumInfo" referids="forumid,posterid">
		<mm:remove referid="adminmode" />
  		<mm:import id="adminmode"><mm:field name="isadministrator" /></mm:import>
	  </mm:nodefunction>
</mm:present>

<mm:log>action ${action} admin: ${adminmode}</mm:log>

<mm:compare value="postreply" referid="action">
	<mm:import externid="postareaid" />
	<mm:import externid="postthreadid" />
	<mm:import externid="poster" />
	<mm:import externid="subject" />
	<mm:import externid="body" />
	<mm:nodefunction set="mmbob" name="postReply" referids="forumid,postareaid,postthreadid,poster,subject,body">
    <mm:field name="error">
      <mm:compare value="none" inverse="true">
        <mm:write />
      </mm:compare>
    </mm:field>
	</mm:nodefunction>

  <mm:list nodes="$postthreadid" path="postthreads,postings" orderby="postings.number" directions="DOWN" max="1">
    <mm:import id="postingid"><mm:field name="postings.number"/></mm:import>
  </mm:list>
  <%@ include file="addfile.jsp" %>
</mm:compare>

<mm:compare value="newpost" referid="action">
	<mm:import externid="postareaid" />
	<mm:import externid="poster" />
	<mm:import externid="subject" />
	<mm:import externid="body" />
  <mm:nodefunction set="mmbob" name="newPost" referids="forumid,postareaid,poster,subject,body">
    <mm:import id="postthreadid"><mm:field name="postthreadid"/></mm:import>
    <mm:field name="error">
      <mm:compare value="none" inverse="true">
        <mm:write />
      </mm:compare>
    </mm:field>
	</mm:nodefunction>

  <mm:list nodes="$postthreadid" path="postthreads,postings" orderby="postings.number" max="1">
    <mm:import id="postingid"><mm:field name="postings.number"/></mm:import>
  </mm:list>
  <jsp:directive.include file="addfile.jsp" />
</mm:compare>


<mm:compare value="newfolder" referid="action">
	<mm:import externid="newfolder" />
	<mm:nodefunction set="mmbob" name="newFolder" referids="forumid,posterid,newfolder">
	</mm:nodefunction>
</mm:compare>


<mm:compare value="removefolder" referid="action">
	<mm:import externid="foldername" />
	<mm:booleanfunction set="mmbob" name="removeFolder" referids="forumid,posterid,foldername">
	</mm:booleanfunction>
</mm:compare>


<mm:compare value="newprivatemessage" referid="action">
	<mm:import externid="poster" />
	<mm:import externid="to" />
	<mm:import externid="subject" />
	<mm:import externid="body" />
	<mm:nodefunction set="mmbob" name="newPrivateMessage" referids="forumid,to,poster,subject,body">
	</mm:nodefunction>
</mm:compare>


<mm:compare value="editpost" referid="action">
	<mm:import externid="postareaid" />
	<mm:import externid="postthreadid" />
	<mm:import externid="postingid" />
	<mm:import externid="subject" />
	<mm:import externid="body" />
	<mm:booleanfunction set="mmbob" name="editPost" referids="forumid,postareaid,postthreadid,postingid,posterid,subject,body">
	</mm:booleanfunction>
	
        <%@ include file="addfile.jsp" %>
</mm:compare>


<mm:compare value="editpostthread" referid="action">
	<mm:import externid="postareaid" />
	<mm:import externid="postthreadid" />
	<mm:import externid="mood" />
	<mm:import externid="state" />
	<mm:import externid="ttype" id="type" />
	<mm:booleanfunction set="mmbob" name="editPostThread" referids="forumid,postareaid,postthreadid,posterid,mood,state,type">
	</mm:booleanfunction>
</mm:compare>

<mm:compare value="editposter" referid="action">
	<mm:import id="firstname" externid="newfirstname" />
	<mm:import id="lastname" externid="newlastname" />
	<mm:import id="email" externid="newemail" />
	<mm:import id="gender" externid="newgender" />
	<mm:import id="location" externid="newlocation" />
	<mm:booleanfunction set="mmbob" name="editPoster" referids="forumid,posterid,firstname,lastname,email,gender,location">
	</mm:booleanfunction>
</mm:compare>



<mm:compare value="true" referid="adminmode">

<mm:compare value="newpostarea" referid="action">
	<mm:import externid="name" />
  <mm:import externid="description" />
	<mm:nodefunction set="mmbob" name="newPostArea" referids="forumid,name,description,posterid@activeid">
    <mm:field name="feedback">
      <mm:isnotempty>
        <mm:write />
      </mm:isnotempty>
    </mm:field>
    <mm:log><mm:field name="feedback" />. Created <mm:field name="newpostareadid" /> </mm:log>
	</mm:nodefunction>
</mm:compare>

<mm:compare value="changepostarea" referid="action">
	<mm:import externid="name" />
	<mm:import externid="description" />
	<mm:import externid="postareaid" />
	<mm:booleanfunction set="mmbob" name="changePostArea" referids="forumid,postareaid,name,description,posterid@activeid">
	</mm:booleanfunction>
</mm:compare>

<mm:compare value="changeforum" referid="action">
	<mm:import externid="name" />
	<mm:import externid="language" reset="true" />
	<mm:import externid="description" />
	<mm:booleanfunction set="mmbob" name="changeForum" referids="forumid,name,language@newlang,description,posterid@activeid" >
	</mm:booleanfunction>
</mm:compare>

<mm:compare value="newmoderator" referid="action">
	<mm:import externid="newmoderator" />
	<mm:import externid="postareaid" />
	<mm:booleanfunction set="mmbob" name="newModerator" referids="forumid,postareaid,posterid,newmoderator">
	</mm:booleanfunction>
</mm:compare>

<mm:compare value="removemoderator" referid="action">
	<mm:import externid="remmoderator" />
	<mm:import externid="postareaid" />
	<mm:booleanfunction set="mmbob" name="removeModerator" referids="forumid,postareaid,posterid,remmoderator">
	</mm:booleanfunction>
</mm:compare>


<mm:compare value="removepostarea" referid="action">
	<mm:import externid="postareaid" />
	<mm:booleanfunction set="mmbob" name="removePostArea" referids="forumid,postareaid">
	</mm:booleanfunction>
</mm:compare>


<mm:compare value="removepostthread" referid="action">
	<mm:import externid="postareaid" />
	<mm:import externid="postthreadid" />
	<mm:booleanfunction set="mmbob" name="removePostThread" referids="forumid,postareaid,postthreadid">
	</mm:booleanfunction>
</mm:compare>

<mm:compare value="removepost" referid="action">
	<mm:import externid="postareaid" />
	<mm:import externid="postthreadid" />
	<mm:import externid="postingid" />
	<mm:booleanfunction set="mmbob" name="removePost" referids="forumid,postareaid,postthreadid,postingid,posterid">
	</mm:booleanfunction>
</mm:compare>
</mm:compare>

<mm:compare value="newforum" referid="action">
	<mm:import externid="name" />
	<mm:import externid="description" />
	<mm:import externid="language" />
	<mm:import externid="account" />
	<mm:import externid="password" />
	<mm:nodefunction set="mmbob" name="newForum" referids="name,language,description,account,password">
	</mm:nodefunction>
</mm:compare>


<mm:compare value="removeforum" referid="action">
	<mm:import externid="remforum" />
	<mm:booleanfunction set="mmbob" name="removeForum" referids="remforum">
	</mm:booleanfunction>
</mm:compare>

</mm:cloud>
