<%@page contentType="text/html" session="false" %>
<%@taglib uri='/WEB-INF/cewolf.tld' prefix='cewolf' %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@ taglib uri="http://www.opensymphony.com/oscache" prefix="cache" %>

<mm:cloud name="mmbase" sessionname="poll">

<mm:import externid="timeframe" from="parameters" jspvar="timeframe">year</mm:import>
<mm:import externid="refresh" from="parameters" jspvar="refresh">false</mm:import>
<cache:cache refresh="<%= refresh %>" time="86400">

<HTML>
<BODY>
<H3>Overzicht - Aantallen node's per tabel, timeframe = <%= timeframe %><br>

<mm:list path="typedef" fields="typedef.number,typedef.name" orderby="typedef.number">
<mm:first><table></mm:first>
<tr><td>

<mm:field name="typedef.number" jspvar="builder" vartype="String">

<jsp:useBean id="pageViews" class="nl.vpro.mmbase.applications.nodegraph.NodeDatasetProducer"/>

    <mm:field name="typedef.name" jspvar="name" vartype="String">
<cewolf:chart 
    id="line" 
    title="<%= name %>" 
    type="stackedhorizontalbar" 
    xaxislabel="nodes" 
    yaxislabel="aantal">
    <cewolf:data>
        <cewolf:producer id="pageViews">
            <cewolf:param name="builder" value='<%= builder %>'/>
            <cewolf:param name="timeframe" value='<%= timeframe %>'/>
        </cewolf:producer>
    </cewolf:data>
</cewolf:chart>
</mm:field>
<p>
<cewolf:img chartid="line" renderer="cewolf" width="1800" height="480"/>
</mm:field>
</td></tr>
<mm:last></table></mm:last>
</mm:list>
<p>

</BODY>
</HTML>

</cache:cache>
</mm:cloud>
