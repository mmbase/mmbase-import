<%@ page import = "java.util.HashSet" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:content postprocessor="reducespace">
<mm:import externid="wizardjsp" required="true" jspvar="wizardjsp" />
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp"%>
<%
 String imageName = "";
 String sAltText = "";
 String startnode = request.getParameterValues("startnode")[0];
 String parenttree = request.getParameterValues("parenttree")[0];

 int [] offset = new int[10];
 for(int d=0; d<offset.length; d++) offset[d]= 0;

 String [] lastLearnObject = new String[10];
 for(int d=0; d<lastLearnObject.length; d++) lastLearnObject[d]= "";
 lastLearnObject[0] = startnode;

 int depth = 1;
 boolean subLearnobjectFound = false;

 String treeName = "lbTree" + startnode + "z";
 %>
    <mm:node number="<%= startnode %>">
       var  <%= treeName %> = new MTMenu();
       <%@include file="newfromtree.jsp" %>
    </mm:node>
    edutree0.makeLastSubmenu(<%= treeName %>, true);
 <%

 while((depth>0||subLearnobjectFound)&&depth<10) {
     subLearnobjectFound = false;
     %><mm:list nodes="<%= lastLearnObject[depth-1] %>" path="learnobjects1,posrel,learnobjects2"
         searchdir="destination" orderby="posrel.pos" directions="UP" max="1" offset="<%= ""+ offset[depth] %>">
         <mm:field name="learnobjects2.number" jspvar="learnobjects2_number" vartype="String" write="false">
            <%
            treeName = "lbTree" + lastLearnObject[depth-1] + "z";
            if(offset[depth]==0) { %>
               var  <%= treeName %> = new MTMenu();
               <mm:node number="component.pdf" notfound="skip">
                   <mm:relatednodes type="providers" constraints="providers.number=$provider">
                       <mm:import id="pdfurl" reset="true"><mm:treefile write="true" page="/pdf/pdfchooser.jsp" objectlist="$includePath" referids="$referids" /></mm:import>
                   </mm:relatednodes>
               </mm:node>
               <mm:node element="learnobjects1">
                  <%@include file="newfromtree.jsp" %>
               </mm:node>
            <% } %>
            <%@include file="showlearnobject.jsp" %>
            <%
               subLearnobjectFound= true;
               offset[depth]++;
               lastLearnObject[depth] = learnobjects2_number;
               depth ++;
            %>
         </mm:field>
     </mm:list><%
     if(!subLearnobjectFound) { // go one layer back
         if(offset[depth]!=0) {
            if(depth>1) { %>
               lbTree<%= lastLearnObject[depth-2] %>z.makeLastSubmenu(lbTree<%= lastLearnObject[depth-1] %>z, true);
            <% } else { %>
               edutree0.makeLastSubmenu(lbTree<%= startnode %>z, true);
            <% }
         }
         offset[depth]=0;
         depth--;
     }
 }
%>
</mm:cloud>
</mm:content>
