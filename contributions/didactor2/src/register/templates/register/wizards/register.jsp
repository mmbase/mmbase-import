<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">

<%@include file="/shared/setImports.jsp" %>
<%@include file="/education/wizards/roles_defs.jsp" %>

<mm:import externid="educationid" />
<mm:import externid="person" />
<mm:import externid="chosenclass" />
<mm:import externid="chosenworkgroup" />

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>AANMELDINGEN</title>
    <link rel="stylesheet" href="<mm:treefile page="/css/base.css" objectlist="$includePath" />" />
    <link rel="stylesheet" href="<mm:treefile page="/register/css/register.css" objectlist="$includePath" />" />
    <style>
      body {
        padding: 30px;
      }
    </style>
  </head>
  <body>
    <div class="content">
      <mm:import id="editcontextname" reset="true">opleidingen</mm:import>
      <%@include file="/education/wizards/roles_chk.jsp" %>
      <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
        <mm:isnotempty referid="chosenclass">
        <mm:compare referid="chosenworkgroup" value="-">
          <script>
            alert("Kies een werkgroep!");
          </script>
        </mm:compare>
        <mm:compare referid="chosenworkgroup" value="-" inverse="true">
	  <mm:node id="n_student" number="$person" />
	  <mm:node id="n_education" number="$educationid" />
	  <mm:node id="n_class" number="$chosenclass" />
          <mm:node id="n_workgroup" number="$chosenworkgroup" />
          <mm:node referid="n_student">
            <mm:related path="related,educations" fields="related.number,educations.number">
              <mm:import id="ed"><mm:field name="educations.number" /></mm:import>
              <mm:import id="rel"><mm:field name="related.number" /></mm:import>
              <mm:compare referid="ed" referid2="educationid">
                <mm:deletenode number="$rel" />
              </mm:compare>
              <mm:remove referid="rel" />
              <mm:remove referid="ed" />
            </mm:related>
          </mm:node>
	  <mm:remove referid="n_student" />
	  <mm:remove referid="n_education" />
	  <mm:remove referid="n_class" />
	  <mm:remove referid="n_workgroup" />
	  <mm:remove referid="person" />
	  <mm:import id="person" />
	  <mm:remove referid="chosenworkgroup" />
	  <mm:remove referid="chosenclass" />
        </mm:compare>
        </mm:isnotempty>

        <mm:isnotempty referid="person">
	  <mm:node number="$person">
	    Voornaam:   <mm:field name="firstname" /><br />
	    Achternaam: <mm:field name="lastname" /><br />
	    Email adres: <mm:field name="email" /><br />
	    <hr />
	    <mm:node number="$educationid">
	      <mm:related path="classes,mmevents" fields="classes.number,classes.name,mmevents.start,mmevents.stop" orderby="mmevents.start">
	        <mm:first>
		  Koppel deze student aan klas:<br />
		  <table class="registerTable">
		    <tr>
		      <th>Klas naam</th><th>Begin</th><th>Werkgroep</th>
		    </tr>
		</mm:first>
		<tr>
		  <form method="post">
		    <input type="hidden" name="educationid" value="<mm:write referid="educationid" />" />
		    <input type="hidden" name="person" value="<mm:write referid="person" />" />
		    <input type="hidden" name="chosenclass" value="<mm:field name="classes.number" />" />
		    <td><mm:field name="classes.name" /></td>
		    <td><mm:field name="gui(mmevents.start)" /></td>
		    <td>
		      <select name="chosenworkgroup">
		        <option value="-">Selecteer een werkgroep</option>
			<mm:node element="classes">
			  <mm:related path="workgroups,people,roles" constraints="roles.name='teacher'" fields="workgroups.name,workgroups.number">
			    <option value="<mm:field name="workgroups.number" />"><mm:field name="workgroups.name" /></option>
			  </mm:related>
			</mm:node>
		      </select>
		      <input type="submit" value="kies" />
		    </td>
		  </form>
		</tr>
		<mm:last>
		  </table>
		</mm:last>
	      </mm:related>
	    </mm:node>
	  </mm:node>
	</mm:isnotempty>
	<mm:isempty referid="person">
          <mm:node number="$educationid">
	    Kies aanmelding om te verwerken: <br />
	    <hr />
	    <mm:relatednodes type="people" role="related">
	      <mm:field name="number" id="person" write="false" />
	      <a href="<mm:treefile page="/register/wizards/register.jsp" objectlist="$includePath" referids="$referids,educationid,person" />"><mm:field name="number" /> <mm:field name="firstname" /> <mm:field name="lastname" /></a> <br />
	      <mm:remove referid="person" />
	    </mm:relatednodes>
	  </mm:node>
	</mm:isempty>  
      </mm:islessthan>
    </div>
  </body>
</html>
</mm:cloud>
</mm:content>
