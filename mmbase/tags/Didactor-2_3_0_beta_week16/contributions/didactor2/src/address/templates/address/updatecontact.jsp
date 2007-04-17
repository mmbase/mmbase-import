<%--
  This template updates or shows a contact of a addressbook.
--%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>

<%-- expires is set so renaming a folder does not show the old name --%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">

<%@include file="/shared/setImports.jsp" %>

<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><di:translate key="address.updatecontact" /></title>
  </mm:param>
</mm:treeinclude>

<mm:import externid="formsubmitted">false</mm:import>

<mm:import externid="addressbook"/>
<mm:import externid="contact"/>
<mm:import externid="callerpage"/>
<mm:import externid="action1"/>
<mm:import externid="action2"/>

<mm:list nodes="$user" path="people,addressbooks,contacts" constraints="contacts.number=$contact" max="1">
    <mm:import id="editable">true</mm:import>
</mm:list>

<%-- get the values from the node the first time --%>
<mm:compare referid="formsubmitted" value="false">
  <mm:node number="$contact">
    <mm:field id="initials" name="initials" write="false"/>
    <mm:field id="firstname" name="firstname" write="false"/>
    <mm:field id="lastname" name="lastname" write="false"/>
    <mm:field id="email" name="email" write="false"/>
    <mm:field id="address" name="address" write="false"/>
    <mm:field id="zipcode" name="zipcode" write="false"/>
    <mm:field id="city" name="city" write="false"/>
    <mm:field id="telephone" name="telephone" write="false"/>
  </mm:node>
</mm:compare>

<%-- import of given input of the fields when the form is submitted --%>
<mm:compare referid="formsubmitted" value="true">
  <mm:import id="initials" externid="_initials"/>
  <mm:import id="firstname" externid="_firstname"/>
  <mm:import id="lastname" externid="_lastname"/>
  <mm:import id="email" externid="_email"/>
  <mm:import id="address" externid="_address"/>
  <mm:import id="zipcode" externid="_zipcode"/>
  <mm:import id="city" externid="_city"/>
  <mm:import id="telephone" externid="_telephone"/>
</mm:compare>

<%-- settings for generating form fields and buttons --%>
<% String text = ""; %>
<mm:node number="$contact">
  <mm:import id="nodetype"><mm:nodeinfo type="type"/></mm:import>
  <mm:compare referid="nodetype" value="people">
    <% text = "value"; %>
  </mm:compare>
  <mm:compare referid="nodetype" value="contacts">
    <% text = "input"; %>
  </mm:compare>
</mm:node>

<%-- addressbook value is not always provided --%>
<mm:isgreaterthan referid="addressbook" value="0">
  <mm:node number="$addressbook" id="myaddressbook"/>
</mm:isgreaterthan>



<mm:present referid="editable">

    <%-- Check if the update button is pressed --%>
    <mm:import id="action1text"><di:translate key="address.update" /></mm:import>
    <mm:compare referid="action1" referid2="action1text">

        <%-- check if a firstname is given --%>
        <mm:compare referid="firstname" value="">
            <mm:import id="error">1</mm:import>
        </mm:compare>

        <%-- check if a lastname is given --%>
        <mm:notpresent referid="error">
            <mm:compare referid="lastname" value="">
                <mm:import id="error">2</mm:import>
            </mm:compare>
        </mm:notpresent>

        <mm:notpresent referid="error">
            <mm:node number="$contact">
                <mm:fieldlist type="all" fields="initials,firstname,lastname,email,address,zipcode,city,telephone">
                    <mm:fieldinfo type="useinput" />
                </mm:fieldlist>
            </mm:node>

            <mm:redirect referids="$referids,addressbook" page="$callerpage"/>
        </mm:notpresent>

    </mm:compare>

</mm:present>

<%-- Check if the back button is pressed --%>
<mm:import id="action2text"><di:translate key="address.back" /></mm:import>
<mm:compare referid="action2" referid2="action2text">
  <mm:redirect referids="$referids,addressbook" page="$callerpage"/>
</mm:compare>


<div class="rows">

<div class="navigationbar">
  <div class="titlebar">

    <mm:present referid="myaddressbook">
      <mm:node referid="myaddressbook">
  	    <mm:field name="name"/><br/>
	  </mm:node>
	</mm:present>

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
    <di:translate key="address.updatecontact" />
  </div>

  <div class="contentBodywit">

    <%-- Show the form --%>
    <form name="updatecontact" class="formInput" method="post" action="<mm:treefile page="/address/updatecontact.jsp" objectlist="$includePath" referids="$referids"/>">

      <input type="hidden" name="formsubmitted" value="true"/>

      <mm:present referid="editable">
      <table class="font">
        <tr><td><di:translate key="address.initials" /></td><td><input type="text" name="_initials" size="80" value="<mm:write referid="initials"/>"/></td></tr>
        <tr><td><di:translate key="address.firstname" /></td><td><input type="text" name="_firstname" size="80" value="<mm:write referid="firstname"/>"/></td></tr>
        <tr><td><di:translate key="address.lastname" /></td><td><input type="text" name="_lastname" size="80" value="<mm:write referid="lastname"/>"/></td></tr>
        <tr><td><di:translate key="address.email" /></td><td><input type="text" name="_email" size="80" value="<mm:write referid="email"/>"/></td></tr>
        <tr><td><di:translate key="address.address" /></td><td><input type="text" name="_address" size="80" value="<mm:write referid="address"/>"/></td></tr>
        <tr><td><di:translate key="address.zipcode" /></td><td><input type="text" name="_zipcode" size="80" value="<mm:write referid="zipcode"/>"/></td></tr>
        <tr><td><di:translate key="address.city" /></td><td><input type="text" name="_city" size="80" value="<mm:write referid="city"/>"/></td></tr>
        <tr><td><di:translate key="address.telephone" /></td><td><input type="text" name="_telephone" size="80" value="<mm:write referid="telephone"/>"/></td></tr>
      </table>
      </mm:present>
      <mm:notpresent referid="editable">
      <table class="font">
        <tr><td><di:translate key="address.initials" /></td><td><mm:write referid="initials"/></td></tr>
        <tr><td><di:translate key="address.firstname" /></td><td><mm:write referid="firstname"/></td></tr>
        <tr><td><di:translate key="address.lastname" /></td><td><mm:write referid="lastname"/></td></tr>
        <tr><td><di:translate key="address.email" /></td><td><mm:write referid="email"/></td></tr>
        <tr><td><di:translate key="address.address" /></td><td><mm:write referid="address"/></td></tr>
        <tr><td><di:translate key="address.zipcode" /></td><td><mm:write referid="zipcode"/></td></tr>
        <tr><td><di:translate key="address.city" /></td><td><mm:write referid="city"/></td></tr>
        <tr><td><di:translate key="address.telephone" /></td><td><mm:write referid="telephone"/></td></tr>
      </table>
 
      </mm:notpresent>
      <br />

      <input type="hidden" name="callerpage" value="<mm:write referid="callerpage"/>"/>
      <input type="hidden" name="addressbook" value="<mm:write referid="addressbook"/>"/>
      <input type="hidden" name="contact" value="<mm:write referid="contact"/>"/>
      <% if ( text.equals( "input" ) ) { %>
          <mm:present referid="editable">
                <input class="formbutton" type="submit" name="action1" value="<di:translate key="address.update" />"/>
        </mm:present>
      <% } %>
      <input class="formbutton" type="submit" name="action2" value="<di:translate key="address.back" />"/>

      <mm:present referid="error">
	    <p/>
	    <mm:compare referid="error" value="1">
  	      <h1><di:translate key="address.firstnamenotempty" /></h1>
	    </mm:compare>
	    <mm:compare referid="error" value="2">
  	      <h1><di:translate key="address.lastnamenotempty" /></h1>
	    </mm:compare>
	  </mm:present>

    </form>

    <mm:node number="$provider">
      <mm:relatedcontainer path="settingrel,components">
        <mm:constraint field="components.name" value="portfolio"/>
        <mm:related>
          <table class="font">
            <tr><td><a href="<mm:treefile page="/portfolio/index.jsp" objectlist="$includePath" referids="$referids,contact"></mm:treefile>">portfolio</a></td></tr>
          </table>
        </mm:related>
      </mm:relatedcontainer>
    </mm:node>

  </div>
</div>
</div>

<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />

</mm:cloud>
</mm:content>
