<%@ page contentType="text/html; charset=utf-8" language="java" 
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<mm:import externid="type" />
<mm:import externid="forumid" />
<mm:import externid="postareaid" />
<mm:import externid="postthreadid" />
<mm:import externid="posterid" />

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%">
<tr><td align="left">
<mm:compare value="index" referid="type">
	<mm:node number="$forumid">
	<a href="<mm:url page="start.jsp">
		<mm:param name="forumid" value="$forumid" />
		</mm:url>">
	<mm:field name="name" /></a>
	</mm:node>
</mm:compare>

<mm:compare value="postarea" referid="type">
	<mm:node number="$forumid">
	<a href="<mm:url page="start.jsp">
		<mm:param name="forumid" value="$forumid" />
		</mm:url>">
	<mm:field name="name" /></a>
	</mm:node> >
	<mm:node number="$postareaid">
	<a href="<mm:url page="postarea.jsp">
		<mm:param name="forumid" value="$forumid" />
		<mm:param name="postareaid" value="$postareaid" />
		</mm:url>">
    <mm:field name="name" />
</a>
	</mm:node>
</mm:compare>


<mm:compare value="poster_index" referid="type">
	<mm:node number="$forumid">
	<a href="<mm:url page="start.jsp">
		<mm:param name="forumid" value="$forumid" />
		</mm:url>">
    <mm:field name="name" /></a>
	</mm:node> >

	<mm:list nodes="$posterid" path="posters,people">
	<a href="<mm:treefile page="/portfolio/index.jsp" objectlist="$includePath" referids="$referids">
                    <mm:param name="contact"><mm:field name="people.number"/></mm:param>
                 </mm:treefile>" target="_top">
           <mm:field name="posters.account" /></a>
	</mm:list>
</mm:compare>

<mm:compare value="onlineposters" referid="type">
	<mm:node number="$forumid">
	<a href="<mm:url page="start.jsp">
		<mm:param name="forumid" value="$forumid" />
		</mm:url>">
    <mm:field name="name" /></a>
	</mm:node> >

	<a href="<mm:url page="onlineposters.jsp">
		<mm:param name="forumid" value="$forumid" />
		</mm:url>">members online </a>
</mm:compare>

<mm:compare value="allposters" referid="type">
	<mm:node number="$forumid">
	<a href="<mm:url page="start.jsp">
		<mm:param name="forumid" value="$forumid" />
		</mm:url>">
    <mm:field name="name" />/</a>
	</mm:node> >

	<a href="<mm:url page="allposters.jsp">
		<mm:param name="forumid" value="$forumid" />
		</mm:url>"><di:translate key="mmbob.allmembers" /></a>
</mm:compare>


<mm:compare value="privatemessages" referid="type">
	<mm:node number="$forumid">
	<a href="<mm:url page="start.jsp">
		<mm:param name="forumid" value="$forumid" />
		</mm:url>">
    <mm:field name="name" /></a>
	</mm:node> >

	<a href="<mm:url page="privatemessages.jsp">
		<mm:param name="forumid" value="$forumid" />
		</mm:url>"><di:translate key="mmbob.pm" /></a>
</mm:compare>


<mm:compare value="onlineposters_poster" referid="type">
	<mm:node number="$forumid">
	<a href="<mm:url page="start.jsp">
		<mm:param name="forumid" value="$forumid" />
		</mm:url>">
	<mm:field name="name" /></a>
	</mm:node> >

	<a href="<mm:url page="onlineposters.jsp">
		<mm:param name="forumid" value="$forumid" />
		</mm:url>"><di:translate key="mmbob.membersonline" /></a>
	&gt;
	<mm:list nodes="$posterid" path="posters,people">
	<a href="<mm:treefile page="/portfolio/index.jsp" objectlist="$includePath" referids="$referids">
                    <mm:param name="contact"><mm:field name="people.number"/></mm:param>
                 </mm:treefile>" target="_top">
           <mm:field name="posters.account" /></a>
	</mm:list>
</mm:compare>


<mm:compare value="allposters_poster" referid="type">
	<mm:node number="$forumid">
	<a href="<mm:url page="start.jsp">
		<mm:param name="forumid" value="$forumid" />
		</mm:url>">
	<mm:field name="name" /></a>
	</mm:node> >

	<a href="<mm:url page="allposters.jsp">
		<mm:param name="forumid" value="$forumid" />
		<mm:param name="pathtype" value="allposters" />
		</mm:url>"><di:translate key="mmbob.allmembers" /></a>
	&gt;
	<mm:list nodes="$posterid" path="posters,people">
	<a href="<mm:treefile page="/portfolio/index.jsp" objectlist="$includePath" referids="$referids">
                    <mm:param name="contact"><mm:field name="people.number"/></mm:param>
                 </mm:treefile>" target="_top">
           <mm:field name="posters.account" /></a>
	</mm:list>
</mm:compare>

<mm:compare value="moderatorteam_poster" referid="type">
	<mm:node number="$forumid">
	<a href="<mm:url page="start.jsp">
		<mm:param name="forumid" value="$forumid" />
		</mm:url>">
	<mm:field name="name" /></a>
	</mm:node> >

	<a href="<mm:url page="moderatorteam.jsp">
		<mm:param name="forumid" value="$forumid" />
		</mm:url>"><di:translate key="mmbob.moderatorteam" /></a>
	&gt;
	<mm:list nodes="$posterid" path="posters,people">
	<a href="<mm:treefile page="/portfolio/index.jsp" objectlist="$includePath" referids="$referids">
                    <mm:param name="contact"><mm:field name="people.number"/></mm:param>
                 </mm:treefile>" target="_top">
           <mm:field name="posters.account" /></a>
	</mm:list>
</mm:compare>


<mm:compare value="moderatorteam" referid="type">
	<mm:node number="$forumid">
	<a href="<mm:url page="start.jsp">
		<mm:param name="forumid" value="$forumid" />
		</mm:url>">
	<mm:field name="name" /></a>
	</mm:node> >

	<a href="<mm:url page="moderatorteam.jsp">
		<mm:param name="forumid" value="$forumid" />
		</mm:url>"><di:translate key="mmbob.moderatorteam" /></a>
</mm:compare>


<mm:compare value="poster_thread" referid="type">
	<mm:node number="$forumid">
	<a href="<mm:url page="start.jsp">
		<mm:param name="forumid" value="$forumid" />
		</mm:url>">
	<mm:field name="name" /></a>
	</mm:node> >
	<mm:node number="$postareaid">
	<a href="<mm:url page="postarea.jsp">
		<mm:param name="forumid" value="$forumid" />
		<mm:param name="postareaid" value="$postareaid" />
		</mm:url>">
	<mm:field name="name" /></a>
	</mm:node> >

	<mm:node number="$postthreadid">
	<a href="<mm:url page="thread.jsp">
		<mm:param name="forumid" value="$forumid" />
		<mm:param name="postareaid" value="$postareaid" />
		<mm:param name="postthreadid" value="$postthreadid" />
		</mm:url>">
	<mm:field name="subject" /></a>
	</mm:node> >

	<mm:list nodes="$posterid" path="posters,people">
	<a href="<mm:treefile page="/portfolio/index.jsp" objectlist="$includePath" referids="$referids">
                    <mm:param name="contact"><mm:field name="people.number"/></mm:param>
                 </mm:treefile>" target="_top">
           <mm:field name="posters.account" /></a>
	</mm:list>
</mm:compare>

<mm:compare value="postthread" referid="type">
	<mm:node number="$forumid">
	<a href="<mm:url page="start.jsp">
		<mm:param name="forumid" value="$forumid" />
		</mm:url>">
	<mm:field name="name" /></a>
	</mm:node> >
	<mm:node number="$postareaid">
	<a href="<mm:url page="postarea.jsp">
		<mm:param name="forumid" value="$forumid" />
		<mm:param name="postareaid" value="$postareaid" />
		</mm:url>">
	<mm:field name="name" /></a>
	</mm:node> >
	<mm:node number="$postthreadid">
	<a href="<mm:url page="thread.jsp">
		<mm:param name="forumid" value="$forumid" />
		<mm:param name="postareaid" value="$postareaid" />
		<mm:param name="postthreadid" value="$postthreadid" />
		</mm:url>">
	<mm:field name="subject" /></a>
	</mm:node>
</mm:compare>
</td>
</tr>
</table>
</mm:cloud>
