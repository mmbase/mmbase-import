<% 

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

%>

<mm:related path="posrel,images"
   constraints="posrel.pos='9'" orderby="images.title"
   ><br/><div align="center"><img src="<mm:node element="images"><mm:image template="s(535)" /></mm:node
      >" alt="<mm:field name="images.title" />" border="0" ></div><br/>
</mm:related>


<mm:related path="posrel,images" orderby="images.title" constraints="posrel.pos!='9'" 
    ><mm:first
       ><mm:field name="posrel.pos" jspvar="dummy" vartype="Integer" write="false"><%
          int posrel_pos = dummy.intValue();  
          if((2<posrel_pos)&&(posrel_pos<5)) { imageTemplate = "+s(80)"; }
          if((4<posrel_pos)&&(posrel_pos<7)) { imageTemplate = "+s(180)"; }
          if(6<posrel_pos) { imageTemplate = "+s(400)"; }
        %></mm:field>
        <table border=0 cellpadding="0" cellspacing="0" style="width:80;margin-bottom:5px;" <%@include file="../includes/imagesposition.jsp" %>>
    </mm:first><%

     // ************** inner table with image **********************************************
     // ** give table small width otherwise description can push the table to large width **
     %><tr><td><div align="center"><img src="<mm:node element="images"><mm:image template="<%= imageTemplate %>" /></mm:node
                   >" alt="<mm:field name="images.title" />" border="0"></div></td></tr>
         <mm:last></table></mm:last>
</mm:related>
