<mm:node element="paragraaf"><% 

// ************* inner table to prevent clustering of images  **********************
// see the types/images_position at the editwizards
// <option id="1">rechts</option>
// <option id="2">links</option>
// <option id="3">rechts klein</option>
// <option id="4">links klein</option>
// <option id="5">rechts medium</option>
// <option id="6">links medium</option>
// <option id="7">rechts groot</option>
// <option id="8">links groot</option>

%><table cellpadding="0" cellspacing="0" width="100%">
    <tr>
        <td>
        <p><mm:related path="posrel,images" orderby="images.title" constraints="posrel.pos!='9'" 
            ><mm:first
               ><mm:field name="posrel.pos" jspvar="dummy" vartype="Integer" write="false"><%
                  int posrel_pos = dummy.intValue();  
                  if((2<posrel_pos)&&(posrel_pos<5)) { imageTemplate = "+s(80)"; }
                  if((4<posrel_pos)&&(posrel_pos<7)) { imageTemplate = "+s(180)"; }
                  if(6<posrel_pos) { imageTemplate = "+s(400)"; }
                %></mm:field>
                <table border=0 cellpadding="0" cellspacing="0" width="80" <%@include file="../includes/imagesposition.jsp" %>>
            </mm:first><%
    
             // ************** inner table with image **********************************************
             // ** give table small width otherwise description can push the table to large width **
             %><tr><td><div align="center"><img src="<mm:node element="images"><mm:image template="<%= imageTemplate %>" /></mm:node
                           >" alt="<mm:field name="images.title" />" border="0"></div></td></tr>
                 <mm:field name="images.description" 
                 ><mm:isnotempty
                     ><tr><td><div align="center"><mm:write /><div></td></tr>
                     <tr><td class="black"><img src="media/spacer.gif" width="1" height="1"></td></tr>
                     <tr><td><img src="media/spacer.gif" width="1" height="5"></td></tr></mm:isnotempty
                 ></mm:field
             ><mm:last></table></mm:last>
        </mm:related
        ><mm:field name="titel_zichtbaar"
			   ><mm:compare value="0" inverse="true"
			      ><div class="pageheader"><mm:field name="titel" 
		   	/></div></mm:compare
			></mm:field
		  ><mm:field name="tekst"><mm:isnotempty><span class="black"><mm:write /></span></mm:isnotempty></mm:field>
        <%@include file="../includes/attachment.jsp" %>
        <mm:related path="posrel,link" orderby="posrel.pos,link.titel" directions="UP,UP"
            ><br/><a target="_blank" href="<mm:field name="link.url" />"  title="<mm:field name="alt_tekst" />" ><mm:field name="link.titel" /></a>
        </mm:related
        ><br>
        </p>
        </td>
    </tr>
</table>
</mm:node>