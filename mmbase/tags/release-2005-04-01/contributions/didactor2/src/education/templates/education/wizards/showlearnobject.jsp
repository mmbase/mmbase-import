<mm:node number="<%= learnobjects2_number %>">
   <%@include file="whichimage.jsp"%>
   <mm:import id="objecttype" reset="true"><mm:nodeinfo type="type" /></mm:import>
   
   <mm:import id="mark_error" reset="true"></mm:import>
   <mm:compare referid="objecttype" value="tests">
       <mm:remove referid="questionamount" />
       <mm:field name="questionamount" id="questionamount">
           <mm:isgreaterthan value="0">
               <mm:countrelations type="questions">
                   <mm:islessthan value="$questionamount">
                       <mm:import id="mark_error" reset="true">Er zijn minder vragen ingevoerd dan er gesteld moeten worden.</mm:import>
                   </mm:islessthan>
               </mm:countrelations>
           </mm:isgreaterthan>
           <mm:remove referid="requiredscore" />
           <mm:field name="requiredscore" id="requiredscore">
             <mm:countrelations type="questions">
                 <mm:islessthan value="$requiredscore">
                     <mm:import id="mark_error" reset="true">Er zijn minder vragen ingevoerd dan er goed beantwoord moeten worden.</mm:import>
                 </mm:islessthan>
             </mm:countrelations>
             <mm:isgreaterthan referid="questionamount" value="0">
                 <mm:islessthan referid="questionamount" value="$requiredscore">
                   <mm:import id="mark_error" reset="true">Er worden minder vragen gesteld dan er goed beantwoord moeten worden.</mm:import>
                 </mm:islessthan>
             </mm:isgreaterthan>
           </mm:field>
       </mm:field>
   </mm:compare>
   <mm:compare referid="objecttype" value="mcquestions">
       <mm:import id="mark_error" reset="true">Een multiple-choice vraag moet minstens 1 goed antwoord hebben</mm:import>
       <mm:relatednodes type="mcanswers" constraints="mcanswers.correct > '0'" max="1">
           <mm:import id="mark_error" reset="true"></mm:import>
       </mm:relatednodes>
   </mm:compare>
   
   lbTree<%= lastLearnObject[depth-1] %>z.addItem(
       "<mm:field name="name"><mm:isempty><mm:field name="title"/></mm:isempty><mm:isnotempty><mm:write/></mm:isnotempty></mm:field><mm:present referid="pdfurl"><mm:compare referid="objecttype" value="pages"></a> <a href='<mm:write referid="pdfurl"/>&number=<mm:field name="number"/>' target='text'><img src='gfx/icpdf.gif' border='0' alt='(PDF)'/></mm:compare><mm:compare referid="objecttype" value="learnblocks"></a> <a href='<mm:write referid="pdfurl"/>&number=<mm:field name="number"/>' target='text'><img src='gfx/icpdf.gif' border='0' alt='(PDF)'/></mm:compare></mm:present></a> <a href='metaedit.jsp?number=<mm:field name="number"/>' target='text'><img id='img_<mm:field name="number"/>' src='<%= imageName %>' border='0' alt='<%= sAltText %>'> <mm:isnotempty referid="mark_error"></a> <a style='color: red; font-weight: bold' href='javascript:alert(&quot;<mm:write referid="mark_error"/>&quot;);'>!</mm:isnotempty>",
       "<mm:write referid="wizardjsp"/>?wizard=<mm:write referid="objecttype" />&objectnumber=<mm:field name="number" />&origin=<mm:field name="number" />",
       null,
       "bewerk object",
       "<mm:treefile write="true" page="/education/wizards/gfx/edit_learnobject.gif" objectlist="" />");
</mm:node>
