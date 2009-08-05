<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<%@ include file="../../../../thememanager/loadvars.jsp" %>
<mm:import externid="main" />
<mm:import externid="sub" />
<mm:import externid="name" />
<mm:import externid="package" />
        <tr><th>Change Display/Theme Settings</th>
         <td>
           <A HREF="<mm:url page="index.jsp" referids="main,sub,name,package">
            <mm:param name="mode" value="includeisettings" />
           </mm:url>">
           <IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
        </td>
      </tr>
</mm:cloud>
