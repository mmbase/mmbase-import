<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>

This page shows the music on this site:<br/><br/>

<mm:cloud>
	<mm:listnodes id="fragment" type="pools">
  		<mm:field name="name" />:<br/>
		<mm:related path="audiofragments" fields="audiofragments.title,audiofragments.number">
			<LI><a href="playmusic.jsp?mediafragment=<mm:field name="audiofragments.number" />"><mm:field name="audiofragments.title" /></a>
		<mm:field name="audiofragments.brrrr" />
		</mm:related>
		
	</mm:listnodes>
</mm:cloud>
