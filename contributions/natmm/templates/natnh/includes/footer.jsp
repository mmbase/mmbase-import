				</td></tr>
        <tr><td><img src="media/spacer.gif" width="1" height="20"></td></tr>
		  <% String thisPage = ph.createPaginaUrl(paginaID,request.getContextPath()); %> 
        <tr><td><p><a href="<%= thisPage + (thisPage.indexOf("?")==-1 ? "?" : "&" ) %>?i=<%= imageId %>&a=<%= articleId %>&o=<%= offsetId %>#top">Terug naar boven</a></p>
                <p>
					 	<% 
							breadCrumClass = ""; 
                     showEndLeaf = false;
                  %>
						<%@include file="breadcrums.jsp" %>
					</p>
                <mm:present referid="ishomepage"
                ><mm:list nodes="<%= paginaID %>" path="pagina,posrel,images" constraints="posrel.pos=='2'"
                    ><mm:first><div align="right"></mm:first
                    ><mm:node element="images"
                        ><a href="<mm:relatednodes type="link"><mm:field name="url" /></mm:relatednodes
                            >" target="_blank"><img src="<mm:image />" border="0" class="logo"></a></mm:node
                    ><mm:last></div></mm:last
                ></mm:list
                ></mm:present>
        </td></tr>
        <tr><td><img src="media/spacer.gif" width="1" height="5"></td></tr>
        <tr><td class="black"><img src="media/spacer.gif" width="1" height="1"></td></tr>
        <tr><td><span class="creditline">Vereniging Natuurmonumenten <%= cal.get(Calendar.YEAR) 
            %> | <a class="creditline" href="mailto:informatieaanvraag@natuurmonumenten.nl?subject=Natuurherstel.nl Feedback">Vragen en opmerkingen</a></span></td></tr>
    </table>
    </td>
    <td width="73"><img src="media/spacer.gif" width="73" height="1"></td>
</tr>
</table>
<p>&nbsp;</p>
<%@include file="sitestatscript.jsp" 
%>

<script type="text/javascript">
   var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
   document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
   try{
   var pageTracker = _gat._getTracker("UA-5801611-2");
   pageTracker._trackPageview();
   } catch(err) {}
</script>

</body>
</html>