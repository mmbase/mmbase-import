<%@ include file="inc.jsp" %>
<mm:import externid="channel" />
<mm:cloud>
<% // code to determine offset... since we don't have a tag that does this.
    // can be done now, but not yet...
   int pagesize=30;
   String spage=request.getParameter("page");
   int thispage=0;
   if (spage!=null) thispage=Integer.parseInt(spage);
   if (thispage<0) thispage=0;
   int count=0;
%>
<body class="basic">
<mm:node referid="channel">
<table summary="forum threads" width="93%" cellspacing="1" cellpadding="3" border="0">
<tr align="left">
  <th class="header" colspan="4"><mm:field name="title" /></th>
</tr>

<h2><mm:field name="title" /></h2>
<mm:field name="number" id="number" write="false" />
<mmcommunity:testchannel condition="readonly" reverse="true">
<p>[ <a href="<mm:url page="createnewthread.jsp" referids="channel" />" >Start a new Thread</a> ]</p>
</mmcommunity:testchannel>
<mmcommunity:tree thread="$number" max='<%="" + pagesize%>' maxdepth="5" offset='<%="" + thispage*pagesize%>' id="thread">
  <mm:field name="listhead" />
     <li><a href="<mm:url page="message.jsp" referids="channel,thread" />"><mm:field name="html(subject)" /></a> (<mm:field name="replycount" />)
     <mmcommunity:getinfo key="name">
     <mm:isnotempty>
     <em>by <mm:write /> on
        <mm:field name="day(timestampsec)" />
        <mm:field name="month(timestampsec)" />
        <mm:field name="year(timestampsec)" /></em>
     </mm:isnotempty>
     </mmcommunity:getinfo></li>
     <% count++; %>
  <mm:field name="listtail" />
</mmcommunity:tree>
&nbsp;
<% if (thispage>0) { %>
[ <a href="<mm:url page="forum.jsp" referids="channel" ><mm:param name="page"><%=thispage-1%></mm:param></mm:url>" >Previous Page</a> ]
<% }
   if (count==pagesize) {
%>
[ <a href="<mm:url page="forum.jsp"referids="channel" ><mm:param name="page"><%=thispage+1%></mm:param></mm:url>" >Next Page</a> ]
<% } %>
</mm:node>
</mm:cloud>
</body></html>
