<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>
<mm:content postprocessor="reducespace">
<mm:cloud jspvar="cloud">
  <mm:import externid="node" required="true"/>
  <%@include file="/shared/setImports.jsp" %>
  <link rel="stylesheet" type="text/css" href="<mm:treefile page="/css/base.css" objectlist="$includePath" referids="$referids" />" />
  <mm:import jspvar="faqLink"><%=request.getRequestURL()%>?node=<mm:write referid="node"/></mm:import>
  <mm:node number="$node" notfound="skipbody">
    <h1><mm:field name="name"/></h1><br/>
    <table width="100%">
      <mm:relatednodes type="faqitems"> 
        <mm:import jspvar="itemNumber"><mm:field name="number"/></mm:import>
        <tr>
          <td bgcolor="#f8e0c5" style="padding: 1px; padding-left: 5px; cursor: pointer; cursor: hand;"
                 onclick="document.location.href='<%=faqLink%>#q<%=itemNumber%>'">
             <table cellspacing="0">
               <tr>
                 <td valign="center">
                   <img src="<mm:treefile write='true' page='/gfx/icon_arrow_tab_closed.gif' objectlist='$includePath' referids='$referids'/>">
                 </td>
                 <td style="padding-left: 7px;" class="plaintext">
                   <mm:field name="question"/> 
                 </td>
               </tr>
             </table>
           </td>
         </tr>
       </mm:relatednodes>
    </table>  
    <mm:relatednodes type="faqitems"> 
      <mm:import jspvar="itemNumber"><mm:field name="number"/></mm:import>
      <p>
        <a name="q<%=itemNumber%>"></a>
        <table width="100%">  
          <tr>
            <td bgcolor="#f8e0c5" style="padding: 1px; padding-left: 5px;">
              <table cellspacing="0">
                <tr>
                  <td>
                    <img src="<mm:treefile write='true' page='/gfx/icon_arrow_tab_closed.gif' objectlist='$includePath' referids='$referids'/>">
                  </td>
                  <td style="padding-left: 7px;"  class="plaintext">
                    <b><mm:field name="question"/></b>
                  </td>
                </tr>
              </table>
            </td>
          </tr>
          <tr>
            <td style="padding: 10px" bgcolor="#f8eee3"  class="plaintext">
              <mm:field name="answer" escape="none"/>
            </td>
          </tr>
        </table> 
      </p>
    </mm:relatednodes>  
  </mm:node>
</mm:cloud>
</mm:content>