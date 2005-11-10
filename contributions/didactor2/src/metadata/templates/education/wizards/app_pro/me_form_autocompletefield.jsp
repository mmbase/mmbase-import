<%
String sNValue = "";
String sStyle = "width:150px";
%>
<table border="0" cellpadding="0" cellspacing="0">
   <tr>
   <%
   if(!sNodeValue.equals("")){
      sNValue = sNodeValue;
   }
   else if (!sNodeUrlString.equals("")){
      // Autocomplete this field
      Class cl;
      Object []prm = new Object[1];
      Class []clm  = new Class[1];
      try{
         clm[0] = Class.forName("java.lang.String");
         prm[0] = sNodeUrlString;
         cl = Class.forName("nl.didactor.component.education.utils.handlers."+ sMetaHandler);
         sNValue = cl.getMethod("getData", clm).invoke(cl, prm).toString();
         sStyle = cl.getMethod("getStyle", null).invoke(cl, null).toString();
      }catch(Exception ex1){
         System.out.println(ex1.toString());
         ex1.printStackTrace();
      } // end of catch
   } // end of if(!sNodeUrlString.equals(""))
   %>
      <td>
         <input name="<%=sPrefix%><%= sMetaDefinitionID %>" type="text" value="<%=sNValue%>" readonly="readonly" style="<%=sStyle%>"/>
      </td>
   </tr>
</table>
