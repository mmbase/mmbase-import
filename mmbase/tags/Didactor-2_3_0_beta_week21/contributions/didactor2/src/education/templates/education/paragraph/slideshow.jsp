<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<%@page language="java" contentType="text/html; charset=utf-8"%>


<mm:cloud>
<mm:import externid="i">-1</mm:import>
<mm:node number="$i">
<head>
   <title><mm:field name="title" /></title>
   <meta http-equiv="imagetoolbar" content="no">
</head>
<body>
   <table width="100%" border="0" cellspacing="0" cellpadding="0">
   <tr>
      <td><img src="../media/spacer.gif" width="10" height="10"></td>
   </tr>
   <tr>
      <td>
         <table class="default" width="600" border="0" align="center" cellpadding="0" cellspacing="0">
            <tr>
               <td colspan="2">
                  <a href="#" onClick="window.close()" title="Click on image to close window"><center><img src="<mm:image template="s(500x500)" />" border="0"></center></a>
               </td>
            </tr>
            <tr>
               <td colspan="2"><img src="../media/spacer.gif" width="10" height="10" border="0"></td>
            </tr>
            <tr valign="top">
               <td style="padding-bottom:10px;">
                  <center>
                     <mm:field name="showtitle">
                        <mm:compare value="1">
                           <b><mm:field name="title"/></b><br/>
                        </mm:compare>
                     </mm:field>
                     <mm:field name="description" />
                  </center>
               </td>
            </tr>
            <tr>
               <td align="right"><center><a href="javascript:window.close();">close this window</a></center></td>
            </tr>
         </table>
      </td>
   </tr>
</table>
</body>
</html>
</mm:node>
</mm:cloud>
