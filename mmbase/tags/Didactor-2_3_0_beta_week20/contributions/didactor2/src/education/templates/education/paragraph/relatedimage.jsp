<% // see the types/images_position at the editwizards
      // <option id="1">rechts (oorspronkelijk formaat)</option>
      // <option id="2">links (oorspronkelijk formaat)</option>
      // <option id="3">rechts klein</option>
      // <option id="4">links klein</option>
      // <option id="5">rechts medium</option>
      // <option id="6">links medium</option>
      // <option id="7">volle breedte</option>
%>
<mm:related path="pos2rel,images"  max="1">
   <mm:field name="pos2rel.pos1" jspvar="posrel_pos" vartype="Integer" write="false">
      <mm:field name="pos2rel.pos2" jspvar="showPopup" vartype="String" write="false">
         <%
            int image_position = 3;
            try
            {
               image_position = posrel_pos.intValue();
            }
            catch (Exception e)
            {
            }

            String imageTemplate = "";
            if(image_position == 7)
            {
               // **** large image 100% width ****
               %>
                  <img src="<%@include file="/education/paragraph/imagessource.jsp" %>" title="" alt="" border="0" width="100%">
               <%
            }
            else
            {
               // *** medium or small image ****
               if((2 < image_position) && (image_position < 5))
               {
                  imageTemplate = "+s(60)";
               }
               if((4 < image_position) && (image_position<7))
               {
                  imageTemplate = "+s(180)";
               }
               boolean rightAlign = false;
               if((image_position % 2) == 1)
               {
                  rightAlign = true;
               }

               %>
                  <table border="0" cellspacing="0" cellpadding="0" style="width:1%;"
               <%
               if(image_position != 8)
               {
                  if(rightAlign)
                  {
                     %> align="right" <%
                  }
                  else
                  {
                     %> align="left" <%
                  }
               }
               %>>
                  <tr>
                     <td style="padding:0px;padding-bottom:10px;
                        <%
                           if(rightAlign)
                           {
                              %>padding-left:10px;<%
                           }
                           else
                           {
                              %>padding-right:10px;<%
                           }
                        %>"><%
                        if(showPopup.equals("1"))
                        {
                           %><a href="#" onClick="javascript:launchCenter('../includes/slideshow.jsp?p=$page&i=<mm:field name="images.number" />', 'center', 610, 550);setTimeout('newwin.focus();',250);"><%
                        }
                        %>
                           <img src="<%@include file="imagessource.jsp" %>" title="" alt="" border="0">
                        <%
                        if(showPopup.equals("1"))
                        {
                           %></a><%
                        }
                     %>
                     </td>
                  </tr>
               </table>
            <%
            }
         %>
      </mm:field>
   </mm:field>
</mm:related>
