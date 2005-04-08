<mm:node number="<%= learnobjects2_number %>">
   <%@include file="whichimage.jsp"%>
   <mm:import id="objecttype" reset="true"><mm:nodeinfo type="type" /></mm:import>

   <mm:compare referid="objecttype" valueset="couplingquestions,dropquestions,hotspotquestions,mcquestions,openquestions,rankingquestions,valuequestions" inverse="true">
      lbTree<%= lastLearnObject[depth-1] %>z.addItem(
          "<mm:field name="name"><mm:isempty><mm:field name="title"/></mm:isempty><mm:isnotempty><mm:write/></mm:isnotempty></mm:field><mm:present referid="pdfurl"><mm:compare referid="objecttype" value="pages"></a> <a href='<mm:write referid="pdfurl"/>&number=<mm:field name="number"/>' target='text'><img src='gfx/icpdf.gif' border='0' alt='(PDF)'/></mm:compare><mm:compare referid="objecttype" value="learnblocks"></a> <a href='<mm:write referid="pdfurl"/>&number=<mm:field name="number"/>' target='text'><img src='gfx/icpdf.gif' border='0' alt='(PDF)'/></mm:compare></mm:present></a> <a href='metaedit.jsp?number=<mm:field name="number"/>' target='text'><img id='img_<mm:field name="number"/>' src='<%= imageName %>' border='0' alt='<%= sAltText %>'>",
          "<mm:write referid="wizardjsp"/>?wizard=<mm:write referid="objecttype" />&objectnumber=<mm:field name="number" />&origin=<mm:field name="number" />",
          null,
          "bewerk object",
          "<mm:treefile write="true" page="/education/wizards/gfx/edit_learnobject.gif" objectlist="" />");

      <mm:compare referid="objecttype" valueset="learnblocks">
         <%
            treeName = "tree" + learnobjects2_number + "z";
         %>
         var  <%= treeName %> = new MTMenu();
               <%@include file="newfromtree.jsp" %>
               lbTree<%= lastLearnObject[depth-1] %>z.makeLastSubmenu(<%= treeName %>, true);
      </mm:compare>
   </mm:compare>

</mm:node>
