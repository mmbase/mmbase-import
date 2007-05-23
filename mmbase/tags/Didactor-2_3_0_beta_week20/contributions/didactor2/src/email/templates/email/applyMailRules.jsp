<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" 
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di"
 %><mm:cloud method="delegate">
<jsp:directive.include file="/shared/setImports.jsp" />

<mm:node number="$user">
  <mm:relatednodescontainer type="mailboxes">
    <mm:constraint field="type" value="0" />
    <mm:maxnumber value="1" />      
    <mm:relatednodes id="mailbox">
      <mm:listrelationscontainer role="subjectmailrule">
        <mm:constraint field="mailboxes.type" value="0" inverse="true" />
        <mm:listrelations>          
          <mm:field write="false" id="rule" name="rule"/>
          <mm:relatednode id="destinationbox" />

          <mm:listrelationscontainer node="mailbox" role="related" type="emails">
            <mm:constraint field="emails.type" value="2" />
            <mm:constraint field="emails.subject" operator="LIKE" value="%$rule%" />
            <mm:listrelations>
              <mm:relatednode id="mail" />
              <mm:deletenode  deleterelations="false"/>
            </mm:listrelations>
            <mm:createrelation role="related" source="destinationbox" destination="mail"/>
          </mm:listrelationscontainer>
          
        </mm:listrelations>
      </mm:listrelationscontainer>

      <mm:listrelationscontainer role="sendermailrule" type="mailboxes" id="mailbox2">
        <mm:constraint field="mailboxes.type" value="0" inverse="true" />
        <mm:listrelations>
          <mm:field write="false" id="rule" name="rule"/>
          <mm:relatednodes id="destinationbox" />

          <mm:listrelationscontainer node="mailbox" role="related" type="emails">
            <mm:constraint field="emails.type" value="2" />
            <mm:constraint field="emails.from" operator="LIKE" value="%$rule%" />
            <mm:listrelations>
              <mm:relatednode id="mail" />
              <mm:deletenode deleterelations="false"/>
            </mm:listrelations>
            <mm:createrelation role="related" source="destinationbox" destination="mail"/>
          </mm:listrelationscontainer>

        </mm:listrelations>
      </mm:listrelationscontainer>

    </mm:relatednodes>
  </mm:relatednodescontainer>
</mm:node>
  
</mm:cloud>
