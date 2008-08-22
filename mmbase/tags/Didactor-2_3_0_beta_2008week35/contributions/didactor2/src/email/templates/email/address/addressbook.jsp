<%--
This message is used in an iframe in write.jsp, in which users can select other users on the same course to send message to
--%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp"%>

<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><di:translate key="email.addressbook" /></title>
  </mm:param>
</mm:treeinclude>

  <script>
    function useThese() {
      var to = '';
      var cc = '';
      var form = document.forms['addressbookForm'];
      for(var count = 0; count < form.length; count++) {
        if(form[count].checked == true) {
          var name = form[count].name;
          var value = form[count].value;
          if(name.indexOf('to') == 0) {
            if(to.length != 0) to += ',';
            to += value;
          }
          if(name.indexOf('cc') == 0) {
            if(cc.length != 0) cc += ',';
            cc += value;
          }
        }
      }
      parent.setTo(to);
      parent.setCc(cc);
      parent.showAddressbook();
    }
  </script>

  <html>
    <head>
      <title>Didactor</title>
    </head>
    <body style="overflow: hidden; background-color: #FFFFFF">

		<di:table maxitems="30">
			<di:row>
				<di:headercell sortfield="to"><di:translate key="email.to" /></di:headercell>
				<di:headercell sortfield="cc"><di:translate key="email.cc" /></di:headercell>
				<di:headercell sortfield="name"><di:translate key="email.name" /></di:headercell>
				<di:headercell sortfield="email"><di:translate key="email.email" /></di:headercell>
			</di:row>
		</di:table>
		
		
      <table width="100%" height="100%">
        <form name="addressbookForm">
          <tr>
            <td>
              <div style="border:1px solid black; overflow: auto; height:210px; width:100%;">
                <table width="100%" class="listTable">
                  <tr>
                    <td class="listHeader">aan</td>
                    <td class="listHeader">cc</td>
                    <td class="listHeader">naam</td>
                    <td class="listHeader">email</td>
                  </tr>
                  <mm:import id="emaildomain" escape="trimmer"><mm:treeinclude write="true" page="/email/init/emaildomain.jsp" objectlist="$includePath" referids="$referids" /></mm:import>
                  <mm:listnodes type="administrators" orderby="firstname,lastname">
                    <mm:first>
                      <tr>
                        <td colspan=2 class="listItem"></td>
                        <td colspan=2 class="listItem"><font class="specialFont"><di:translate key="email.administrators" /></font></td>
                      </tr>
                    </mm:first>
                    <tr>
                      <td class="listItem"><input type="checkbox" name="to<mm:field name="number"/>" value="<mm:field name="username"/><mm:write referid="emaildomain" />"></td>
                      <td class="listItem"><input type="checkbox" name="cc<mm:field name="number"/>" value="<mm:field name="username"/><mm:write referid="emaildomain" />"></td>
                      <td class="listItem" width="50%"><mm:field name="firstname"/> <mm:field name="lastname"/></td>
                      <td class="listItem" width="50%"><mm:field name="username"/><mm:write referid="emaildomain" /></td>
                    </tr>
                  </mm:listnodes>

                  <mm:node referid="course">
                    <mm:related path="courserel,teachers" orderby="teachers.firstname,teachers.lastname">
                      <mm:node element="teachers">
                      <mm:first>
                        <tr>
                          <td colspan=2 class="listItem"></td>
                          <td colspan=2 class="listItem"><font class="specialFont"><di:translate key="email.teachers" /></font></td>
                        </tr>
                      </mm:first>
                      <tr>
                        <td class="listItem"><input type="checkbox" name="to<mm:field name="number"/>" value="<mm:field name="username"/><mm:write referid="emaildomain" />"></td>
                        <td class="listItem"><input type="checkbox" name="cc<mm:field name="number"/>" value="<mm:field name="username"/><mm:write referid="emaildomain" />"></td>
                        <td class="listItem" width="50%"><mm:field name="firstname"/> <mm:field name="lastname"/></td>
                        <td class="listItem" width="50%"><mm:field name="username"/><mm:write referid="emaildomain" /></td>
                      </tr>
                      </mm:node>
                    </mm:related>  

                    <mm:related path="courserel,students" orderby="students.firstname,students.lastname">
                      <mm:node element="students">
                      <mm:first>
                        <tr>
                          <td colspan=2 class="listItem"></td>
                          <td colspan=2 class="listItem"><font class="specialFont"><di:translate key="email.students" /></font></td>
                        </tr>
                      </mm:first>
                      <tr>
                        <td class="listItem"><input type="checkbox" name="to<mm:field name="number"/>" value="<mm:field name="username"/><mm:write referid="emaildomain" />"></td>
                        <td class="listItem"><input type="checkbox" name="cc<mm:field name="number"/>" value="<mm:field name="username"/><mm:write referid="emaildomain" />"></td>
                        <td class="listItem" width="50%"><mm:field name="firstname"/> <mm:field name="lastname"/></td>
                        <td class="listItem" width="50%"><mm:field name="username"/><mm:write referid="emaildomain" /></td>
                      </tr>
                      </mm:node>
                    </mm:related>
                  </mm:node>
                </table>
              </div>
            </td>
          </tr>
          <tr>
            <td height=100%>
              <table>
                <tr>
                  <td>
                    <mm:treeinclude write="true" page="/email/button/default.jsp" objectlist="$includePath"  referids="$referids">
                      <mm:param name="caption"><di:translate key="email.usethese" /></mm:param>
                      <mm:param name="onclick">javascript:useThese()</mm:param>
                    </mm:treeinclude>
                  </td>
                  <td>
                    <mm:treeinclude write="true" page="/email/button/default.jsp" objectlist="$includePath"  referids="$referids">
                      <mm:param name="caption"><di:translate key="email.hide" /></mm:param>
                      <mm:param name="onclick">javascript:parent.showAddressbook()</mm:param>
                    </mm:treeinclude>
                  </td>
                </tr>
              </table>
            </td>
          </tr>
        </form>
      </table>
    </body>
  </html>
  <mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />  
</mm:cloud>
</mm:content>
