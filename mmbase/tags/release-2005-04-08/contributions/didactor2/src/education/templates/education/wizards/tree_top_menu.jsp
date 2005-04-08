<%@ page import = "java.util.ArrayList" %>
<%@ page import = "java.util.Iterator" %>

<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<mm:cloud jspvar="cloud" method="asis">
   <%@include file="/shared/setImports.jsp" %>

   <%
      if(request.getParameter("mode") != null)
      {
         session.setAttribute("education_topmenu_mode", request.getParameter("mode"));
      }

      if(request.getParameter("course") != null)
      {
         session.setAttribute("education_topmenu_course", request.getParameter("course"));
      }
   %>

   <html>
   <head>
      <link rel="stylesheet" type="text/css" href='<mm:treefile page="/css/base.css" objectlist="$includePath" referids="$referids" />' />

      <style type="text/css">
         .education_top_menu_selected
         {
            background:#888586;
         }
         .education_top_menu_nonselected
         {
            background:#DEDEDE;
         }
      </style>

   </head>

   <mm:import id="education_top_menu"><%= session.getAttribute("education_topmenu_mode") %></mm:import>
   <%
      ArrayList arliEducations = new ArrayList();
   %>
   <mm:node number="$user">
      <% //We go throw all educations for CURRENT USER%>
      <mm:node number="$user">
         <mm:related path="classrel,classes">
            <mm:node element="classes">
               <mm:related path="related,educations" distinct="true" fields="educations.number">
                  <mm:node element="educations">
                       <mm:field name="number" jspvar="sID" vartype="String" write="false">
                          <%
                             arliEducations.add(sID);
                          %>
                       </mm:field>
                  </mm:node>
               </mm:related>
            </mm:node>
         </mm:related>
      </mm:node>
   </mm:node>

   <fmt:bundle basename="nl.didactor.component.education.EducationMessageBundle">

      <body onLoad="try{top.frames['menu'].location.reload();  top.frames['code'].location.reload()} catch(err){};">
         <table border="0" cellpadding="0" cellspacing="0" class="titlefield2" style="width:965px; background:#DEDEDE;">
            <form>
               <input type="hidden" name="mode" value="educations"/>
               <tr>
                  <td style="border-right: #000000 1px solid" <mm:compare referid="education_top_menu" value="components">     class="education_top_menu_selected" </mm:compare>><a href="?mode=components"     style="font-weight:bold;"><fmt:message key="educationMenuComponents"/></a></td>
                  <td style="border-right: #000000 1px solid" <mm:compare referid="education_top_menu" value="roles">          class="education_top_menu_selected" </mm:compare>><a href="?mode=roles"          style="font-weight:bold;"><fmt:message key="educationMenuRoles"/></a></td>
                  <td style="border-right: #000000 1px solid" <mm:compare referid="education_top_menu" value="competence">     class="education_top_menu_selected" </mm:compare>><a href="?mode=competence"     style="font-weight:bold;"><fmt:message key="educationMenuCompetence"/></a></td>
                  <td style="border-right: #000000 1px solid" <mm:compare referid="education_top_menu" value="metadata">       class="education_top_menu_selected" </mm:compare>><a href="?mode=metadata"       style="font-weight:bold;"><fmt:message key="educationMenuMetadata"/></a></td>
                  <td style="border-right: #000000 1px solid" <mm:compare referid="education_top_menu" value="filemanagement"> class="education_top_menu_selected" </mm:compare>><a href="?mode=filemanagement" style="font-weight:bold;"><fmt:message key="educationMenuFilemanagement"/></a></td>
                  <td style="border-right: #000000 1px solid" <mm:compare referid="education_top_menu" value="tests"> class="education_top_menu_selected" </mm:compare>><a href="?mode=tests" style="font-weight:bold;"><fmt:message key="educationMenuTests"/></a></td>
                  <td style="padding:1px;padding-left:6px;"   <mm:compare referid="education_top_menu" value="educations">     class="education_top_menu_selected" </mm:compare>>
                     <%
                        if(arliEducations.size() < 2)
                        {
                           %>
                              <a href="?mode=educations"     style="font-weight:bold;"><fmt:message key="educationMenuEducations"/></a>
                           <%
                        }
                        else
                        {
                           %>
                              <table border="0" cellspacing="0" class="titlefield2" style="padding:2px;">
                                 <tr>
                                    <td style="padding-right:6px;">
                                       <fmt:message key="educationMenuEducations"/>
                                    </td>
                                    <td>
                                         <select name="course" class="titlefield2">
                                             <%
                                                for(Iterator it = arliEducations.iterator(); it.hasNext();)
                                                {
                                                   String sEducationID = (String) it.next();
                                                   %>
                                                      <option <% if((session.getAttribute("education_topmenu_course") != null) && (session.getAttribute("education_topmenu_course").equals(sEducationID))) out.print(" selected=\"selected\" "); %> onMouseOver="ActiveUrl(educationTopMenu)" value="<%=sEducationID%>"><mm:node number="<%=sEducationID%>"><mm:field name="name"/></mm:node></option>
                                                   <%
                                                }
                                             %>
                                          </select>
                                    </td>
                                    <td>
                                      <input type="image" src="<mm:treefile page="/education/wizards/gfx/ga.gif" objectlist="$includePath" referids="$referids" />" border="0" align="bottom"/>
                                    </td>
                                 </tr>
                              </table>
                           <%
                        }
                     %>
                  </td>
                  <td width="100%">&nbsp;</td>
               </tr>
            </form>
         </table>
      </body>
   </fmt:bundle>
</mm:cloud>