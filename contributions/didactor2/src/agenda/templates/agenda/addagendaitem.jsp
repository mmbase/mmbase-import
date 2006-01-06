<%--
  This template adds a item in an agenda.
--%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%-- expires is set so renaming a folder does not show the old name --%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">

<%@ page import="java.text.SimpleDateFormat,
                 java.text.ParseException,
                 java.util.Date,
                 java.util.Calendar"%>

<mm:import externid="callerpage"/>
<mm:import externid="typeof"/>

<%@include file="/shared/setImports.jsp" %>
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title>
      <mm:compare referid="typeof" value="1">
        <di:translate key="agenda.addpersonalagendaitem" />
      </mm:compare>
      <mm:compare referid="typeof" value="2">
        <di:translate key="agenda.addclassagendaitem" />
      </mm:compare>
      <mm:compare referid="typeof" value="3">
        <di:translate key="agenda.addworkgroupagendaitem" />
      </mm:compare>
      <mm:compare referid="typeof" value="4">
        <di:translate key="agenda.addinvitation" />
      </mm:compare>


    </title>
  </mm:param>
</mm:treeinclude>


<mm:import externid="action1"/>
<mm:import externid="action2"/>
<mm:import externid="year" jspvar="year" vartype="Integer"/>
<mm:import externid="month" jspvar="month" vartype="Integer"/>
<mm:import externid="day" jspvar="day" vartype="Integer"/>
<mm:import externid="classname"/>
<mm:import externid="workgroupname"/>

<%-- Adding items in the personal scope --%>
<mm:compare referid="typeof" value="1">
  <mm:node number="$user">
    <mm:relatednodes type="agendas" max="1">
      <mm:field name="number" id="currentagenda" write="false"/>
    </mm:relatednodes>
  </mm:node>
</mm:compare>

<%-- Adding items in the class scope --%>
<mm:compare referid="typeof" value="2">
  <mm:node number="$user">
    <mm:relatednodescontainer type="classes">

      <%-- if a class is chosen get the correct class for saving the data --%>
      <mm:isnotempty referid="classname">
        <mm:constraint field="name" referid="classname"/>
      </mm:isnotempty>

      <mm:relatednodes id="myclasses">
        <mm:relatednodes type="agendas">
          <mm:first>
            <mm:field name="number" id="currentagenda" write="false"/>
          </mm:first>
        </mm:relatednodes>
      </mm:relatednodes>

    </mm:relatednodescontainer>
  </mm:node>
</mm:compare>

<%-- Adding items in the workgroup scope --%>
<mm:compare referid="typeof" value="3">
  <mm:node number="$user">
    <mm:relatednodescontainer type="workgroups">

      <%-- if a workgroup is chosen get the correct workgroup for saving the data --%>
      <mm:isnotempty referid="workgroupname">
        <mm:constraint field="name" referid="workgroupname"/>
      </mm:isnotempty>

      <mm:relatednodes id="myworkgroups">
        <mm:relatednodes type="agendas">
          <mm:first>
            <mm:field name="number" id="currentagenda" write="false"/>
          </mm:first>
        </mm:relatednodes>
      </mm:relatednodes>

    </mm:relatednodescontainer>
  </mm:node>
</mm:compare>

<%-- Create invitation --%>
<mm:compare referid="typeof" value="4">
  <mm:node number="$user">
    <mm:relatednodes type="agendas" max="1">
      <mm:field name="number" id="currentagenda" write="false"/>
    </mm:relatednodes>
  </mm:node>
</mm:compare>



<mm:node number="$currentagenda" id="mycurrentagenda"/>


<%-- Check if the create button is pressed --%>
<mm:present referid="action1">
  <mm:import id="action1text"><di:translate key="agenda.create" /></mm:import>
  <mm:compare referid="action1" referid2="action1text">

    <mm:import externid="startHours" jspvar="startHours" vartype="Integer"/>
    <mm:import externid="startMinutes" jspvar="startMinutes" vartype="Integer"/>
    <mm:import externid="stopHours" jspvar="stopHours" vartype="Integer"/>
    <mm:import externid="stopMinutes" jspvar="stopMinutes" vartype="Integer"/>

    <mm:import id="title" externid="_title"/>
    <mm:compare referid="title" value="">
      <mm:import id="error">2</mm:import>
    </mm:compare>

    <mm:notpresent referid="error">
      <mm:import id="body" externid="_body"/>
      <mm:compare referid="body" value="">
        <mm:import id="error">3</mm:import>
      </mm:compare>
    </mm:notpresent>

    <mm:notpresent referid="error">
      <%
        if ( startHours.intValue() * 60 + startMinutes.intValue() >= stopHours.intValue() * 60 + stopMinutes.intValue() ) {
      %>
        <mm:import id="error">1</mm:import>
      <%
        }
      %>
    </mm:notpresent>

    <mm:import externid="recipient"/>

    <mm:notpresent referid="error">
      <mm:node referid="mycurrentagenda">

        <mm:createnode type="items" id="myitems">

          <mm:fieldlist type="all" fields="title,body,repeatinterval,repeatuntil">
  		    <mm:fieldinfo type="useinput" />
	      </mm:fieldlist>
	</mm:createnode>
	
	<mm:import jspvar="repeatInterval" externid="_repeatinterval" vartype="Integer"/>
	<mm:node referid="myitems">
	  <mm:import jspvar="repeatUntil" vartype="Integer"><mm:field name="repeatuntil"/></mm:import>
            <%
		int interval = 0;
		int until = 0;
		if (repeatInterval != null) { 
		    interval = repeatInterval.intValue();
		    until = repeatUntil.intValue() + 24 * 60 * 60;
		}
		Calendar calendarStart = Calendar.getInstance();
		calendarStart.set( year.intValue(), month.intValue()-1, day.intValue(), startHours.intValue(), startMinutes.intValue(), 0);
		Calendar calendarStop = Calendar.getInstance();  
		calendarStop.set( year.intValue(), month.intValue()-1, day.intValue(), stopHours.intValue(), stopMinutes.intValue(), 0);
		 
		do {	    
		    %>
			<mm:createrelation role="eventrel" source="mycurrentagenda" destination="myitems">

			<mm:setfield name="start"><%=calendarStart.getTime().getTime()/1000%></mm:setfield>
			<mm:setfield name="stop"><%=calendarStop.getTime().getTime()/1000%></mm:setfield>

			</mm:createrelation>
		    <%
		    calendarStart.add(Calendar.DATE,interval);
		    calendarStop.add(Calendar.DATE,interval);
		} while (interval > 0 && calendarStart.getTime().getTime()/1000 <= until);
	%>
    
	</mm:node>
	      



	<mm:createrelation role="invitationrel" source="myitems" destination="user">
	    <mm:setfield name="status">1</mm:setfield>
	</mm:createrelation>

	<%-- link to recipient, if we have one --%>
	<mm:present referid="recipient">
	    <mm:node number="$recipient" id="myrecipient">
	    <mm:createrelation role="invitationrel" source="myitems" destination="myrecipient">
		<mm:setfield name="status">0</mm:setfield> <%-- pending  ack / decline --%>
	    </mm:createrelation>
            <mm:relatednodes type="agendas">
	       <mm:first>
	          <mm:field name="number" id="recipientagenda" write="false"/>
		  <mm:createrelation role="related" source="recipientagenda" destination="myitems"/>
	       </mm:first>
	    </mm:relatednodes>
 	  </mm:node>
	</mm:present>
	
      </mm:node>

      <mm:redirect referids="$referids,currentagenda,typeof,year,day,month" page="$callerpage"/>
    </mm:notpresent>

  </mm:compare>
</mm:present>


<%-- Check if the back button is pressed --%>
<mm:present referid="action2">
  <mm:import id="action2text"><di:translate key="agenda.back" /></mm:import>
  <mm:compare referid="action2" referid2="action2text">
    <mm:redirect referids="$referids,currentagenda,typeof,year,day,month" page="$callerpage"/>
  </mm:compare>
</mm:present>

<div class="rows">

<div class="navigationbar">
  <div class="titlebar">
    <mm:compare referid="typeof" value="1">
      <img src="<mm:treefile page="/agenda/gfx/icon_agenda_item_person.gif" objectlist="$includePath" referids="$referids"/>" border="0" title="<di:translate key="agenda.addpersonalagendaitem" />" alt="<di:translate key="agenda.addpersonalagendaitem" />"/>
      <di:translate key="agenda.addpersonalagendaitem" />
    </mm:compare>
    <mm:compare referid="typeof" value="2">
      <img src="<mm:treefile page="/agenda/gfx/icon_agenda_item_class.gif" objectlist="$includePath" referids="$referids"/>" border="0" title="<di:translate key="agenda.addclassagendaitem" />" alt="<di:translate key="agenda.addclassagendaitem" />"/>
      <di:translate key="agenda.addclassagendaitem" />
    </mm:compare>
    <mm:compare referid="typeof" value="3">
	  <img src="<mm:treefile page="/agenda/gfx/icon_agenda_item_workgroup.gif" objectlist="$includePath" referids="$referids"/>" border="0" title="<di:translate key="agenda.addworkgroupagendaitem" />" alt="<di:translate key="agenda.addworkgroupagendaitem" />"/>
	  <di:translate key="agenda.addworkgroupagendaitem" />
    </mm:compare>
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
    <mm:write referid="day"/>/<mm:write referid="month"/>/<mm:write referid="year"/>
  </div>

  <div class="contentBodywit">

    <%-- Show the form --%>
    <form name="addagendaitem" method="post" action="<mm:treefile page="/agenda/addagendaitem.jsp" objectlist="$includePath" referids="$referids"/>">

      <table class="Font">

        <%-- Only valid for making items in the personal scope --%>
        <mm:notpresent referid="myclasses">
          <mm:notpresent referid="myworkgroups">
            <mm:node referid="mycurrentagenda">
              <tr>
              <td/><td><mm:field name="name"/></td>
              </tr>
            </mm:node>
          </mm:notpresent>
        </mm:notpresent>

        <%-- Only valid for making items in the class scope --%>
        <mm:present referid="myclasses">
          <tr>
          <mm:listnodes referid="myclasses">
  	        <mm:first><td><di:translate key="agenda.class" /></td><td><select name="classname"></mm:first>
	        <option><mm:field name="name"/></option>
	        <mm:last></select></td></mm:last>
	      </mm:listnodes>
	      </tr>
	    </mm:present>

        <%-- Only valid for making items in the workgroup scope --%>
        <mm:present referid="myworkgroups">
          <tr>
          <mm:listnodes referid="myworkgroups">
		<mm:first><td><di:translate key="agenda.workgroup" /></td><td><select name="workgroupname"></mm:first>
	        <option><mm:field name="name"/></option>
	        <mm:last></select></td></mm:last>
	      </mm:listnodes>
	      </tr>
	    </mm:present>

	<%-- create personal invitation --%>
	<mm:compare referid="typeof" value="4">
	<tr>
	<td><di:translate key="agenda.recipient" /></td>
	<td>
	<select name="recipient">
	<mm:list nodes="$user" path="people1,classes,people2" constraints="people2.number != people1.number" fields="people2.number" distinct="true" orderby="people2.lastname,people2.firstname">
	    <option value="<mm:field name="people2.number"/>"><mm:field name="people2.firstname"/> <mm:field name="people2.lastname"/></option>
	</mm:list>
	</select>
	</td>
	</tr>
	</mm:compare>

	    
   	    <mm:fieldlist nodetype="items" fields="title,body">
 	      <tr>
	      <td><mm:fieldinfo type="guiname"/></td>
	      <td><mm:fieldinfo type="input"/></td>
	      </tr>
	    </mm:fieldlist>

	<tr>
	  <td><di:translate key="agenda.date" /></td>
	  <td><select name="day">
	      <% for (int c = 1; c <=31; c++) { %>
		  <option value="<%= c %>" <%= c == day.intValue() ? "selected" : "" %> ><%= c %></option>
	      <% } %>
	      </select>
	      <select name="month">
	      <% for (int c = 1; c <= 12; c++) { %>
		  <option value="<%= c %>" <%= c == month.intValue() ? "selected" : "" %> ><%= c %></option>
	      <% } %>
	      </select>
	      <select name="year">
	      <% for (int c = 2000; c <= 2050; c++) { %>
		  <option value="<%= c %>" <%= c == year.intValue() ? "selected" : "" %> ><%= c %></option>
	       <% } %>
	      </select>
	  </td>
       </tr>


        <tr>
	  <td><di:translate key="agenda.starttime" /></td>
          <td>
            <select name="startHours">
	          <%for(int count = 1; count <= 23; count++) {%>
	            <option value="<%=count%>" <%=(count == 9)?("selected"):("")%>><%=count%></option>
	          <%}%>
            </select>
            <select name="startMinutes">
	          <%for(int count = 0; count < 60; count+=5) {%>
	            <option value="<%=count%>"><%=(count < 10)?"0":""%><%=count%></option>
	          <%}%>
            </select>
          </td>
        </tr>

        <tr>
	  <td><di:translate key="agenda.endtime" /></td>
          <td>
            <select name="stopHours">
	          <%for(int count = 1; count <= 23; count++) {%>
	            <option value="<%=count%>" <%=(count == 9)?("selected"):("")%>><%=count%></option>
	          <%}%>
            </select>
            <select name="stopMinutes">
	          <%for(int count = 0; count < 60; count+=5) {%>
	            <option value="<%=count%>"><%=(count < 10)?"0":""%><%=count%></option>
	          <%}%>
            </select>
          </td>
        </tr>

   	    <mm:fieldlist nodetype="items" fields="repeatinterval">
	    <tr>
	      <td><mm:fieldinfo type="guiname"/></td>
	      <td>
	        <input type="radio" name="_repeatinterval" checked value="0">geen</input>
	        <input type="radio" name="_repeatinterval" value="1">dagelijks</input>
	        <input type="radio" name="_repeatinterval" value="7">wekelijks</input>
	      </td>
	    </tr>
	    </mm:fieldlist>

   	    <mm:fieldlist nodetype="items" fields="repeatuntil">
 	      <tr>
	      <td><mm:fieldinfo type="guiname"/></td>
	      <td><mm:fieldinfo type="input" options="date"/></td>
	      </tr>
	    </mm:fieldlist>


      </table>

      <input type="hidden" name="callerpage" value="<mm:write referid="callerpage"/>"/>
      <input type="hidden" name="currentagenda" value="<mm:write referid="currentagenda"/>"/>
      <input type="hidden" name="typeof" value="<mm:write referid="typeof"/>"/>
<%--      <input type="hidden" name="year" value="<mm:write referid="year"/>"/>
      <input type="hidden" name="day" value="<mm:write referid="day"/>"/>
      <input type="hidden" name="month" value="<mm:write referid="month"/>"/>--%>
		  
      <input class="formbutton" type="submit" name="action1" value="<di:translate key="agenda.create" />" />
      <input class="formbutton" type="submit" name="action2" value="<di:translate key="agenda.back" />" />

      <mm:present referid="error">
        <p/>
        <mm:compare referid="error" value="1">
	<h1><di:translate key="agenda.starttimelessthanendtime" /></h1>
        </mm:compare>
        <mm:compare referid="error" value="2">
	<h1><di:translate key="agenda.titlenotempty" /></h1>
        </mm:compare>
        <mm:compare referid="error" value="3">
	<h1><di:translate key="agenda.descriptionnotempty" /></h1>
        </mm:compare>
      </mm:present>

    </form>

  </div>
</div>
</div>

<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />

</mm:cloud>
</mm:content>

