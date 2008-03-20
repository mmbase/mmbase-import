<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<mm:content postprocessor="reducespace" escaper="none">
<mm:cloud jspvar="cloud">
<mm:import externid="node" required="true"/>
<%@include file="/shared/setImports.jsp" %>
<link rel="stylesheet" type="text/css" href="<mm:treefile page="/portalpages/css/base.css" objectlist="$includePath" referids="$referids" />" />
  <mm:node number="$node">
  <table>
	<tr>
	  <td>
	    <h1 style="color:#B85602;"><mm:field name="title" /></h1> <!-- WTF, style in the jsp? -->
	  </td>
	</tr>
	<tr>
	  <td>
	    <mm:field name="abstract" />
	  </td>
	</tr>
  </table>
  <mm:field name="impos">
    <mm:compare value="1">
      <mm:field name="body" />
      <table>
        <tr>
          <td>
            <mm:relatednodes type="images">
              <h3><mm:field name="title" /></h3>
              <img src="<mm:image />" width="200" border="0" /><br/>
              <mm:field name="description" />
            </mm:relatednodes>
          </td>
        </tr>
      </table>
    </mm:compare>
  </mm:field>
  <mm:field name="impos">
    <mm:compare value="0">
      <table>
        <tr>
         <mm:relatednodes type="images">
           <h3><mm:field name="title" /></h3>
           <img src="<mm:image />" width="200" border="0" />
           <mm:field name="description" />
         </mm:relatednodes>
        </tr>
        <tr>
 	      <td>
 	        <mm:field name="body" />
 	      </td>
 	    </tr>
      </table>
    </mm:compare>
  </mm:field>
  <mm:field name="impos">
    <mm:compare value="2">
      <table>
        <tr>
  	      <td>
            <mm:relatednodes type="images">
              <table>
                <tr> <h3><mm:field name="title"/></h3></tr>
                <tr align="right"> <mm:field name="description" />  </tr>
                <tr> <td><img src="<mm:image />" width="200" border="0"/><br/></td></tr>
              </table>
            </mm:relatednodes>
          </td>
          <td>
            <mm:field name="body" />
          </td>
        </tr>
      </table>
    </mm:compare>
  </mm:field>
  </mm:node>
</mm:cloud>
</mm:content>

