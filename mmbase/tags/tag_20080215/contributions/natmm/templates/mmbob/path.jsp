<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:import externid="type" />
<mm:import externid="forumid" />
<mm:import externid="postareaid" />
<mm:import externid="postthreadid" />
<mm:import externid="posterid" />

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%">
<tr><td align="left">
<mm:compare value="index" referid="type">
	<mm:node number="$forumid">
	<a href="<mm:url page="index.jsp">
		<mm:param name="forumid" value="$forumid" />
		</mm:url>">
	<mm:field name="name" /></a>
	</mm:node>
</mm:compare>

<mm:compare value="postarea" referid="type">
	<mm:node number="$forumid">
	<a href="<mm:url page="index.jsp">
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
	</mm:node>
</mm:compare>


<mm:compare value="poster_index" referid="type">
	<mm:node number="$forumid">
	<a href="<mm:url page="index.jsp">
		<mm:param name="forumid" value="$forumid" />
		</mm:url>">
	<mm:field name="name" /></a>
	</mm:node> >

	<mm:node number="$posterid">
	<a href="<mm:url page="profile.jsp">
		<mm:param name="forumid" value="$forumid" />
		<mm:param name="postareaid" value="$postareaid" />
		<mm:param name="posterid" value="$posterid" />
		</mm:url>">
	<mm:field name="account" /></a>
	</mm:node>
</mm:compare>

<mm:compare value="onlineposters" referid="type">
	<mm:node number="$forumid">
	<a href="<mm:url page="index.jsp">
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
	<a href="<mm:url page="index.jsp">
		<mm:param name="forumid" value="$forumid" />
		</mm:url>">
	<mm:field name="name" /></a>
	</mm:node> >

	<a href="<mm:url page="allposters.jsp">
		<mm:param name="forumid" value="$forumid" />
		</mm:url>">all members</a>
</mm:compare>


<mm:compare value="privatemessages" referid="type">
	<mm:node number="$forumid">
	<a href="<mm:url page="index.jsp">
		<mm:param name="forumid" value="$forumid" />
		</mm:url>">
	<mm:field name="name" /></a>
	</mm:node> >

	<a href="<mm:url page="privatemessages.jsp">
		<mm:param name="forumid" value="$forumid" />
		</mm:url>">private messages</a>
</mm:compare>


<mm:compare value="onlineposters_poster" referid="type">
	<mm:node number="$forumid">
	<a href="<mm:url page="index.jsp">
		<mm:param name="forumid" value="$forumid" />
		</mm:url>">
	<mm:field name="name" /></a>
	</mm:node> >

	<a href="<mm:url page="onlineposters.jsp">
		<mm:param name="forumid" value="$forumid" />
		</mm:url>">members online </a>
	&gt;
	<mm:node number="$posterid">
	<a href="<mm:url page="profile.jsp">
		<mm:param name="forumid" value="$forumid" />
		<mm:param name="postareaid" value="$postareaid" />
		<mm:param name="posterid" value="$posterid" />
		<mm:param name="pathtype" value="onlineposters_poster" />
		</mm:url>">
	<mm:field name="account" /></a>
	</mm:node>
</mm:compare>


<mm:compare value="allposters_poster" referid="type">
	<mm:node number="$forumid">
	<a href="<mm:url page="index.jsp">
		<mm:param name="forumid" value="$forumid" />
		</mm:url>">
	<mm:field name="name" /></a>
	</mm:node> >

	<a href="<mm:url page="allposters.jsp">
		<mm:param name="forumid" value="$forumid" />
		<mm:param name="pathtype" value="allposters" />
		</mm:url>">all members</a>
	&gt;
	<mm:node number="$posterid">
	<a href="<mm:url page="profile.jsp">
		<mm:param name="forumid" value="$forumid" />
		<mm:param name="postareaid" value="$postareaid" />
		<mm:param name="posterid" value="$posterid" />
		<mm:param name="pathtype" value="onlineposters_poster" />
		</mm:url>">
	<mm:field name="account" /></a>
	</mm:node>
</mm:compare>

<mm:compare value="moderatorteam_poster" referid="type">
	<mm:node number="$forumid">
	<a href="<mm:url page="index.jsp">
		<mm:param name="forumid" value="$forumid" />
		</mm:url>">
	<mm:field name="name" /></a>
	</mm:node> >

	<a href="<mm:url page="moderatorteam.jsp">
		<mm:param name="forumid" value="$forumid" />
		</mm:url>">moderator team </a>
	&gt;
	<mm:node number="$posterid">
	<a href="<mm:url page="profile.jsp">
		<mm:param name="forumid" value="$forumid" />
		<mm:param name="postareaid" value="$postareaid" />
		<mm:param name="posterid" value="$posterid" />
		<mm:param name="pathtype" value="onlineposters_poster" />
		</mm:url>">
	<mm:field name="account" /></a>
	</mm:node>
</mm:compare>


<mm:compare value="moderatorteam" referid="type">
	<mm:node number="$forumid">
	<a href="<mm:url page="index.jsp">
		<mm:param name="forumid" value="$forumid" />
		</mm:url>">
	<mm:field name="name" /></a>
	</mm:node> >

	<a href="<mm:url page="moderatorteam.jsp">
		<mm:param name="forumid" value="$forumid" />
		</mm:url>">moderator team</a>
</mm:compare>


<mm:compare value="poster_thread" referid="type">
	<mm:node number="$forumid">
	<a href="<mm:url page="index.jsp">
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

	<mm:node number="$posterid">
	<a href="<mm:url page="profile.jsp">
		<mm:param name="forumid" value="$forumid" />
		<mm:param name="postareaid" value="$postareaid" />
		<mm:param name="postthreadid" value="$postthreadid" />
		<mm:param name="type" value="poster_thread" />
		<mm:param name="posterid" value="$posterid" />
		</mm:url>">
	<mm:field name="account" /></a>
	</mm:node>
</mm:compare>

<mm:compare value="postthread" referid="type">
	<mm:node number="$forumid">
	<a href="<mm:url page="index.jsp">
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
</mm:cloud>
</td>
</tr>
</table>
