<%--
  This template edit a agenda item.
--%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%-- expires is set so renaming a folder does not show the old name --%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">

<%@ page import="java.text.SimpleDateFormat,
                 java.text.ParseException,
                 java.util.Date,
                 java.util.Calendar"%>

<mm:import externid="callerpage">/agenda/index.jsp</mm:import>

<mm:import externid="currentitem"/>
<mm:import externid="typeof"/>

<%@include file="/shared/setImports.jsp" %>
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title>
      <di:translate key="agenda.editagendaitem" />
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

<%-- Finding agenda --%>
<mm:node number="$currentitem">
  <mm:relatednodes type="agendas" max="1">
    <mm:field name="number" id="currentagenda" write="false"/>
  </mm:relatednodes>
</mm:node>

<mm:node number="$currentagenda" id="mycurrentagenda"/>


<%-- Check if the save button is pressed --%>
<mm:present referid="action1">
  <mm:import id="action1text"><di:translate key="agenda.save" /></mm:import>
  <mm:compare referid="action1" referid2="action1text">

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
      <mm:node number="$currentitem" id="myitems">
        <mm:fieldlist type="all" fields="title,body">
          <mm:fieldinfo type="useinput" />
        </mm:fieldlist>
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
      <img src="<mm:treefile page="/agenda/gfx/icon_agenda_item_person.gif" objectlist="$includePath" referids="$referids"/>" border="0" alt="<di:translate key="agenda.editagendaitem" />"/>
    </mm:compare>
    <mm:compare referid="typeof" value="2">
      <img src="<mm:treefile page="/agenda/gfx/icon_agenda_item_class.gif" objectlist="$includePath" referids="$referids"/>" border="0" alt="<di:translate key="agenda.editagendaitem" />"/>
    </mm:compare>
    <mm:compare referid="typeof" value="3">
      <img src="<mm:treefile page="/agenda/gfx/icon_agenda_item_workgroup.gif" objectlist="$includePath" referids="$referids"/>" border="0" alt="<di:translate key="agenda.editagendaitem" />"/>
    </mm:compare>
    <di:translate key="agenda.editagendaitem" />
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
    <form name="editagendaitem" method="post" action="<mm:treefile page="/agenda/editagendaitem.jsp" objectlist="$includePath" referids="$referids,year,day,month"/>">

      <table class="Font">

        <mm:compare referid="typeof" value="1">
          <tr>
            <td/>
            <td><di:translate key="agenda.personal_agenda_of" /> <mm:node number="$user"><mm:field name="firstname"/> <mm:field name="suffix"/> <mm:field name="lastname"/></mm:node></td>
          </tr>
        </mm:compare>

        <mm:compare referid="typeof" value="2">
          <mm:node number="$currentitem">
            <mm:relatednodes type="classes">
              <tr>
                <td/>
                <td><di:translate key="agenda.agenda_of" />  <di:translate key="agenda.class" /> <mm:field name="name"/></td>
              </tr>
            </mm:relatednodes>
          </mm:node>
        </mm:compare>

        <mm:compare referid="typeof" value="3">
          <mm:node number="$currentitem">
            <mm:relatednodes type="workgroups">
              <tr>
                <td/>
                <td><di:translate key="agenda.agenda_of" />  <di:translate key="agenda.workgroup" /> <mm:field name="name"/></td>
              </tr>
            </mm:relatednodes>
          </mm:node>
        </mm:compare>

        <mm:compare referid="typeof" value="4">
          <mm:node number="$currentitem">
            <mm:related path="related,agendas,people">
              <tr>
                <td/>
                <td><di:translate key="agenda.recipient" />: <mm:field name="people.firstname"/> <mm:field name="people.suffix"/> <mm:field name="people.lastname"/></td>
              </tr>
            </mm:related>
          </mm:node>
        </mm:compare>

        <mm:node number="$currentitem">
          <mm:fieldlist fields="title,body">
            <tr>
              <td><mm:fieldinfo type="guiname"/></td>
              <td><mm:fieldinfo type="input"/></td>
            </tr>
          </mm:fieldlist>
        </mm:node>

      </table>

      <input type="hidden" name="callerpage" value="<mm:write referid="callerpage"/>"/>
      <input type="hidden" name="currentagenda" value="<mm:write referid="currentagenda"/>"/>
      <input type="hidden" name="currentitem" value="<mm:write referid="currentitem"/>"/>
      <input type="hidden" name="typeof" value="<mm:write referid="typeof"/>"/>

      <input class="formbutton" type="submit" name="action1" value="<di:translate key="agenda.save" />" />
      <input class="formbutton" type="submit" name="action2" value="<di:translate key="agenda.back" />" />

      <mm:present referid="error">
        <p/>
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
