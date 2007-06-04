            <%
               for(int f = 1; f < depth; f++)
               {
                  if(branches[f - 1])
                  {
                     %>
                        <td><img src="gfx/tree_vertline.gif" border="0" align="center" valign="middle"/></td>
                     <%
                  }
                  else
                  {
                     %>
                        <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
                     <%
                  }
               }
            %>
