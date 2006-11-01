<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp"%>

<mm:node number="$user">
  <mm:relatednodes type="mailboxes" constraints="mailboxes.m_type=0" max="1">
    <mm:field write="false" id="inbox" name="number"/>
    
    <mm:list nodes="$inbox" path="mailboxes1,subjectmailrule,mailboxes2" constraints="mailboxes1.m_type=0 AND mailboxes2.m_type != 0">
	<mm:field write="false" id="rule" name="subjectmailrule.rule"/>
	<mm:field write="false" id="destinationbox" name="mailboxes2.number"/>
	<mm:list nodes="$inbox" path="mailboxes,related,emails" constraints="emails.m_type=2 AND emails.subject LIKE '%$rule%'">
	    <mm:field write="false" id="relation" name="related.number"/>
	    <mm:field write="false" id="mail" name="emails.number"/>
	    <mm:deletenode number="$relation" deleterelations="false"/>
	    <mm:createrelation role="related" source="destinationbox" destination="mail"/>
	    <mm:remove referid="mail"/>
	    <mm:remove referid="relation"/>
	    </mm:list>
	    <mm:remove referid="destinationbox"/>
	    <mm:remove referid="rule"/>

    </mm:list>

    <mm:list nodes="$inbox" path="mailboxes1,sendermailrule,mailboxes2" constraints="mailboxes1.m_type=0 AND mailboxes2.m_type != 0">
	<mm:field write="false" id="rule" name="sendermailrule.rule"/>
	<mm:field write="false" id="destinationbox" name="mailboxes2.number"/>
	<mm:list nodes="$inbox" path="mailboxes,related,emails" constraints="emails.m_type=2 AND emails.m_from LIKE '%$rule%'">
	    <mm:field write="false" id="relation" name="related.number"/>
	    <mm:field write="false" id="mail" name="emails.number"/>
	    <mm:deletenode number="$relation" deleterelations="false"/>
	    <mm:createrelation role="related" source="destinationbox" destination="mail"/>
	    <mm:remove referid="mail"/>
	    <mm:remove referid="relation"/>
	    </mm:list>
	    <mm:remove referid="destinationbox"/>
	    <mm:remove referid="rule"/>
	    
    </mm:list>


	
  </mm:relatednodes>
</mm:node>
  
</mm:cloud>
