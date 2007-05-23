<%--
  This template shows a item from an agenda.
--%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%-- expires is set so renaming a folder does not show the old name --%>
<mm:content postprocessor="reducespace" expires="0">

<mm:cloud method="delegate" jspvar="cloud">

<mm:import externid="callerpage">/agenda/index.jsp</mm:import>

<%@include file="/shared/setImports.jsp" %>
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
  <link rel="stylesheet" type="text/css" href="<mm:treefile page="/agenda/css/calendar.css" objectlist="$includePath" referids="$referids"/>" />
    <title><di:translate key="agenda.appointment" /></title>
  </mm:param>
</mm:treeinclude>

<mm:import externid="currentitem" required="true" />
<mm:import externid="year"        required="true" />
<mm:import externid="day"         required="true" />
<mm:import externid="month"       required="true" />

<mm:import externid="back" />

<mm:time id="date" time="$year-$month-$day" write="false" />

<mm:import externid="status"/>


<mm:node number="$currentitem" id="mycurrentitem"/>


<%-- Check if the back button is pressed --%>
<mm:present referid="back">
<mm:redirect referids="$referids,currentitem,year,day,month" page="$callerpage"/>
</mm:present>




<div class="rows">

<div class="navigationbar">
  <div class="titlebar">
  </div>
</div>

<div class="folders">
  <div class="folderHeader">
  </div>
  <div class="folderBody">
  </div>
</div>

<div class="mainContent">

  <div class="contentHeader">
    <mm:time referid="date" format=":FULL" />
  </div>

  <div class="contentSubHeader">
    <mm:list nodes="$user" path="people,invitationrel,items" constraints="invitationrel.status=1 AND items.number=$currentitem" max="1">
      <mm:first>
    <a href="<mm:treefile page="/agenda/deleteagendaitem.jsp" objectlist="$includePath" referids="$referids,year,month,day">
               <mm:param name="ids"><mm:write referid="currentitem"/></mm:param>
               <mm:param name="callerpage"><mm:write referid="callerpage"/></mm:param>
             </mm:treefile>">
      <img src="<mm:treefile page="/agenda/gfx/afspraak verwijderen.gif" objectlist="$includePath" />" border="0" title="<di:translate key="agenda.deleteagendaitem" />" alt="<di:translate key="agenda.deleteagendaitem" />"/></a>
      </mm:first>
    </mm:list>
  </div>

  <div class="contentBodywit">
    <%-- Show the form --%>
    <form name="showagendaitem" method="post" action="<mm:treefile page="/agenda/showagendaitem.jsp" objectlist="$includePath" referids="$referids"/>">
    <input type="hidden" name="callerpage" value="<mm:write referid="callerpage"/>">
      <mm:node referid="mycurrentitem">
	<!-- mm:field name="name" -->

	<br/>

        <table class="Font">
          <mm:fieldlist nodetype="items" fields="title,body">
            <tr>
              <th><mm:fieldinfo type="guiname"/></th>
              <td><mm:fieldinfo type="value"/></td>
            </tr>
            
          </mm:fieldlist>
        
        
          <mm:listrelations role="eventrel">
            
            <mm:fieldlist fields="start,stop">            
              <tr>
                <th><mm:fieldinfo type="guiname"/></th>
                <td><mm:fieldinfo type="guivalue" /></td>
              </tr>
            </mm:fieldlist>
          </mm:listrelations>
          
          <tr>
            <mm:fieldlist nodetype="items" fields="repeatinterval">
              <tr>
                <th><mm:fieldinfo type="guiname"/></th>
                <td>
                  <mm:fieldinfo type="guivalue" />
                </td>
              </tr>
            </mm:fieldlist>
          </tr>
          <mm:fieldlist nodetype="items" fields="repeatuntil">
            <tr>
              <th><mm:fieldinfo type="guiname"/></th>
              <td><mm:fieldinfo type="value" options="date" /></td>
            </tr>
          </mm:fieldlist>
          
          <%-- this is an invitation to $user --%>
          <mm:list nodes="$currentitem" path="items,invitationrel,people" constraints="invitationrel.status=1 AND people.number!=$user" max="1">
            <mm:first>
              <mm:import reset="true" id="okbutton">1</mm:import>
              <mm:import id="sendername"><mm:field name="people.firstname"/> <mm:field name="people.lastname"/></mm:import>
            </mm:first>
          </mm:list>
 
      <mm:list nodes="$currentitem" path="items,invitationrel,people" constraints="invitationrel.status!=1 AND people.number=$user" max="1">
	    <tr>
		<th><di:translate key="agenda.sender" /></th>
		<td><mm:write referid="sendername"/></td>
	    </tr>
	    <tr>
		<th><di:translate key="agenda.invitationstatus" /></th>
		<td>
		    <%-- Check for update --%>
		    <mm:import id="mystatus" externid="status"/>
		    <mm:present referid="mystatus">
			<mm:import id="invrelnum"><mm:field name="invitationrel.number"/></mm:import>
			<mm:node number="$invrelnum">
			    <mm:setfield name="status"><mm:write referid="mystatus"/></mm:setfield>
			</mm:node>

			<mm:redirect referids="$referids,currentitem,year,day,month" page="$callerpage"/>

		    </mm:present>
		
		    <mm:import id="status" reset="true"><mm:field name="invitationrel.status"/></mm:import>

		    <select name="status">
			<option value="2" <mm:compare referid="status" value="2">selected</mm:compare>><di:translate key="agenda.accepted" /></option>
			<option value="3" <mm:compare referid="status" value="3">selected</mm:compare>><di:translate key="agenda.declined" /></option>
			<option value="0" <mm:islessthan referid="status" value="2">selected</mm:islessthan>><di:translate key="agenda.pending" /></option>
		    </select>
		    </td>
		</tr>
      </mm:list>
	    
        </table>

      </mm:node>
    
      
      <%-- this is an invitation by $user --%>
      <mm:list nodes="$currentitem" path="items,invitationrel,people" constraints="invitationrel.status!=1 AND people.number!=$user" max="1">
	<mm:first>
          <table class="Font"> 
	    <tr>
		<th><di:translate key="agenda.recipients" /></th>
		<th><di:translate key="agenda.invitationstatus" /></th>
	    </tr>
	</mm:first>
	    <tr>
		<td>
		    <mm:field name="people.firstname"/> <mm:field name="people.lastname"/>
		</td>
		<td>
		    <mm:import id="status" reset="true"><mm:field name="invitationrel.status"/></mm:import>


		    <mm:compare referid="status" value="2">
			<di:translate key="agenda.accepted" />
		    </mm:compare>
		    <mm:compare referid="status" value="3">
			<di:translate key="agenda.declined" />
		    </mm:compare>
		    <mm:islessthan referid="status" value="2">
			<di:translate key="agenda.pending" />
		    </mm:islessthan>
		    </td>
		</tr>
	</mm:list>
	    </table>



            
      <input type="hidden" name="callerpage" value="<mm:write referid="callerpage"/>"/>
      <input type="hidden" name="currentitem" value="<mm:write referid="currentitem"/>"/>
      <input type="hidden" name="year" value="<mm:write referid="year"/>"/>
      <input type="hidden" name="day" value="<mm:write referid="day"/>"/>
      <input type="hidden" name="month" value="<mm:write referid="month"/>"/>
      <input class="formbutton" type="submit" name="back" value="<di:translate key="agenda.back" />"/>
      <mm:present referid="okbutton">
      <input class="formbutton" type="submit" name="update" value="<di:translate key="agenda.ok" />" />
      </mm:present>
      

    </form>

  </div>
  </div>
</div>


<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />

</mm:cloud>
</mm:content>

