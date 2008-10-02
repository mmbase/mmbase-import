<%@ page isELIgnored="false" %>
<% // warning on using two times the same question should be added
boolean timeFieldIsOpen = false;
String buttonText = "verstuur je bericht";
%>

<table cellpadding="0" cellspacing="0" align="left">
<tr><td>

<table cellpadding="0" cellspacing="0" align="left">
	<tr>
		<td><img src="media/spacer.gif" width="10" height="1"></td>
		<td colspan="3"><img src="media/spacer.gif" width="400" height="1">
         <div align="right" style="letter-spacing:1px;"><a href="javascript:history.go(-1);">terug</a></div>
         <%@include file="../relatedteaser.jsp" %>
      </td>
	</tr>
</table>

</td></tr>
<tr><td><img src="media/spacer.gif" width="10" height="1"></td><td colspan="3">

</td></tr>
<tr><td colspan=3><br/></td></tr>
<tr><td>


   <mm:list nodes="<%= paginaID %>" id="forms" path="pagina,posrel,formulier" fields="formulier.number" orderby="posrel.pos" directions="UP" searchdir="destination">
      <mm:size id="formulierCount" write="false"/>
      <mm:isgreaterthan referid="formulierCount" value="1">
         <mm:first>
            </td></tr>
            <tr><td colspan="3">Selecteer het formulier</td></tr>
            <tr><td colspan=3>
            <select id="selectform" onchange="showForm(this)">
         </mm:first>
         <mm:field name="formulier.titel" jspvar="titel" vartype="String" write="false"/>
         <mm:field name="formulier.number" jspvar="number" vartype="String" write="false"/>
         <option value="${number}">${titel}</option>
      </mm:isgreaterthan>
   </mm:list>
   <mm:isgreaterthan referid="formulierCount" value="1">
      </select><br/><br/>
      </td></tr>
      <tr><td>
   </mm:isgreaterthan>

<mm:list referid="forms">

<mm:field name="formulier.number" jspvar="number" vartype="String" write="false"/>
<script type="text/javascript">
   forms.push("form${number}");
</script>

<form name="form${number}" id="form${number}" method="post" action="<%= javax.servlet.http.HttpUtils.getRequestURL(request) %>" <mm:first inverse="true">style='display:none'</mm:first>>
<mm:node element="formulier" jspvar="form">
   <table cellpadding="0" cellspacing="0" align="left">
     
      <tr>
        <td><img src="media/spacer.gif" width="10" height="1"></td>
        <td colspan="3">
          <div class="pageheader"><mm:field name="titel" /></div><br/>
          <mm:field name="omschrijving" jspvar="text" vartype="String" write="false">
            <% if(text!=null&&!HtmlCleaner.cleanText(text,"<",">","").trim().equals("")) { %><mm:write /><br/><br/><% } %>
          </mm:field>
        </td>
      </tr>
      <% 
      String formulier_number = form.getStringValue("number"); 
      %>
      <mm:related path="posrel,formulierveld" fields="formulierveld.number" orderby="posrel.pos" directions="UP" searchdir="destination">
        <mm:node element="formulierveld" jspvar="thisField"><% 
          
          String questions_type = thisField.getStringValue("type");
          String questions_number = thisField.getStringValue("number");
          boolean isRequired = "1".equals(thisField.getStringValue("verplicht"));
          if(!"".equals(thisField.getStringValue("emailonderwerp"))) {
            buttonText = thisField.getStringValue("emailonderwerp");
          }
          // *** radio buttons or checkboxes  
          if(questions_type.equals("4")||questions_type.equals("5")) {
            %>
            <tr>
              <td><img src="media/spacer.gif" width="10" height="1"></td>
              <td colspan="3">
                <mm:field name="label" />
                <% if(isRequired) { %> (*) <% } %>
              </td>
            </tr>
            <mm:related path="posrel,formulierveldantwoord" fields="formulierveldantwoord.waarde,formulierveldantwoord.number"
              orderby="posrel.pos" directions="UP">
              <tr>
                <td><img src="media/spacer.gif" width="10" height="1"></td>
                <td>
                  <% if(questions_type.equals("4")) { %>
                    <input type="radio" name="q<%= questions_number %>" value="<mm:field name="formulierveldantwoord.waarde" />" style="background-color:#FFFFFF;">
                  <% } else if(questions_type.equals("5")) { %>
                    <input type="checkbox" name="q<%= questions_number %>_<mm:field name="formulierveldantwoord.number" />" value="<mm:field name="formulierveldantwoord.waarde" />" style="background-color:#FFFFFF;">
                  <% } %>	
                </td>
                <td colspan="2">
                  <mm:field name="formulierveldantwoord.waarde" />
                </td>
              </tr>
            </mm:related>
            <% 
          }
          // *** dropdown
          if(questions_type.equals("3")) {
            
            String questions_title = thisField.getStringValue("label");
            
            // do something special for question sequences with day, month, year in their title
            if(questions_title.equals("Dag")||questions_title.equals("Maand")||questions_title.equals("Jaar")||questions_title.equals("U")||questions_title.equals("M")) { 
              if(!timeFieldIsOpen) {
                %>
                <tr>	
                  <td><img src="media/spacer.gif" width="10" height="1"></td>
                  <td colspan=3>
                    <table><tr>
                  <%
                  timeFieldIsOpen = true;
              } %>
                <td><%= questions_title %><% if(isRequired) { %> (*) <% } %><br>
                  <select name="q<%= questions_number %>">
                  <option>...
                  <mm:related path="posrel,formulierveldantwoord" fields="formulierveldantwoord.waarde"
                    orderby="posrel.pos" directions="UP">
                    <option value="<mm:field name="formulierveldantwoord.waarde" />"><mm:field name="formulierveldantwoord.waarde" />
                  </mm:related>
                </select>
                </td>
                <% 
            } else { 
              if(timeFieldIsOpen) {%>
                        </tr></table>
                    </td></tr>
                    <%
                    timeFieldIsOpen = false;
              } %>
              <tr>	
                <td><img src="media/spacer.gif" width="10" height="1"></td>
                <td colspan=3>
                <mm:field name="formulierveld.label" />
                <% if(isRequired) { %> (*) <% } %>
              </td></tr>
              <tr>	
                <td><img src="media/spacer.gif" width="10" height="1"></td>
                <td colspan="3">
                <select name="q<%= questions_number %>">
                <option>...
                <mm:related path="posrel,formulierveldantwoord" fields="formulierveldantwoord.waarde" orderby="posrel.pos" directions="UP">
                  <option value="<mm:field name="formulierveldantwoord.waarde" />"><mm:field name="formulierveldantwoord.waarde" />
                </mm:related>
                </select>
              </td></tr>
              <% 
            } 
          } 
          // *** textarea and textline
          if(questions_type.equals("1")||questions_type.equals("2")) {
            %>
            <tr>
              <td><img src="media/spacer.gif" width="10" height="1"></td>
              <td colspan=3>
              <mm:field name="label" />
              <% if(isRequired) { %> (*) <% } %>
            </td></tr>
            <tr>
              <td><img src="media/spacer.gif" width="10" height="1"></td>
              <td colspan="3">
                <% if(questions_type.equals("1")) { %>
                  <textarea rows="3" cols="52" name="q<%= questions_number %>" wrap="physical"></textarea>
                <% } else { %>
                  <input type="text" name="q<%= questions_number %>" size="50" value="">
                <% } %>
              </td>
            </tr>
            <% 
          } %>
        </mm:node>
	    </mm:related>
      <%
      if(timeFieldIsOpen) {
          %>
              </tr></table>
          </td></tr>
          <%
          timeFieldIsOpen = false;
      } %>
      <tr>
        <td><img src="media/spacer.gif" width="10" height="1"></td>
        <td colspan="3"><img src="media/spacer.gif" width="1" height="10"></td>
      </tr>
      <tr>
        <td><img src="media/spacer.gif" width="10" height="1"></td>
        <td colspan="3"><div align="right">
        <a href="javascript:postIt(${number});"><%= buttonText %></a><img src="media/spacer.gif" width="10" height="1"></div>
        </td>
      </tr>

   </table>

</td></tr>
<tr><td>
    </mm:node>
   </form>
   </mm:list>



<script type="text/javascript">
   showForm(document.getElementById("selectform"));
</script>
 
 
<table cellpadding="0" cellspacing="0" align="left">
	<tr>
		<td><img src="media/spacer.gif" width="10" height="1"></td>
		<td colspan="3"><img src="media/spacer.gif" width="1" height="10"></td>
	</tr>
	<tr>
		<td><img src="media/spacer.gif" width="10" height="1"></td>
		<td colspan="3">
		(*) vul minimaal deze velden in i.v.m. een correcte afhandeling.
		</td>
	</tr>
	<tr>
		<td><img src="media/spacer.gif" width="10" height="1"></td>
		<td colspan="3"><img src="media/spacer.gif" width="1" height="10"></td>
	</tr>
</table>

</td></tr>
</table>
