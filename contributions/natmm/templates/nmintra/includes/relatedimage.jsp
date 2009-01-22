<%@page isELIgnored="false"
%><%@page import="org.mmbase.util.images.*"
%><%
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
<c:set var="right" value="false"/>
<mm:related path="posrel,images" orderby="images.title" constraints="posrel.pos!='9'" searchdir="destination">
   <mm:field name="posrel.pos" jspvar="dummy" vartype="Integer" write="false">
   <mm:node element="images" jspvar="image">
      
    <%
       boolean right = false;
       int posrel_pos = dummy.intValue();
       Dimension d = (Dimension) image.getFunctionValue("dimension", null).get();
       int width = d.getWidth();
       int height = d.getHeight();
       pageContext.setAttribute("width", width);
       pageContext.setAttribute("height", height);
       
       if(posrel_pos == 1 ||posrel_pos == 3 || posrel_pos == 5 || posrel_pos == 7){
          right = true;
       }
       if((2<posrel_pos)&&(posrel_pos<5)) { 
          imageTemplate = "+s(120)(>)";
          if (width > 120) width = 120;
       } else if((4<posrel_pos)&&(posrel_pos<7)) {
          if (width > 180) width = 180;
          imageTemplate = "+s(180)(>)"; 
       } else if(6<posrel_pos) { 
          if (width > 400) width = 400;
          imageTemplate = "+s(400)(>)"; 
       } else { //Catch for positions 1 and 2
          imageTemplate = "+s(180)(>)";
          if (width > 180) width = 180;
       }
       
       if(right){ 
   %><div style="float:right;margin:0 0 5px 5px; width: <%=width%>px;"><%
   } else {
   %><div style="float:left;margin:0 5px 5px 0; width: <%=width%>px;"><%
   } %>
   
   <c:set var="resizedImageSource"><mm:image template="<%=imageTemplate%>"/></c:set>
   <c:set var="showpopup"><mm:field name="reageer" vartype="String"/></c:set>
   <c:set var="title"><mm:field name="images.title" /></c:set>
   </mm:node>

   <c:if test="${showpopup eq 1}">
      <mm:node element="images" jspvar="image">
         <c:set var="bigSource"><mm:image template="s(600x450)(>)" /></c:set>
      </mm:node>
      <a href="javascript:void(0)" onClick="window.open('${bigSource}', 'PopUp', 'toolbar=no,location=no,directories=no,status=no,menubar=no,resizable=no,scrollbars=no,width=620,height=470')" >
   </c:if>

   <img src="${resizedImageSource}" alt="${title}" border="0">
   
   <c:if test="${not empty showpopup}"></a></c:if>
   
   </mm:field>
   <c:if test="${empty imageNoDescription}">
      <mm:field name="images.description">
         <mm:isnotempty><div style="text-align:center; margin: 5px 0pt 0pt;"><mm:write /></div></mm:isnotempty>
      </mm:field>
   </c:if>
   <c:remove var="imageNoDescription"/>
   </div>
</mm:related>