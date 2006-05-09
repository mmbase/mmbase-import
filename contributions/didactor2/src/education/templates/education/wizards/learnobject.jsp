<%@ page import = "java.util.HashSet" %>
<%@ page import = "org.mmbase.bridge.*" %>

<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>


<mm:content postprocessor="reducespace">
<mm:import externid="wizardjsp" required="true" jspvar="wizardjsp" />
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp"%>

<%
 Node nodeUser = nl.didactor.security.Authentication.getCurrentUserNode(cloud);
 String imageName = "";
 String sAltText = "";
 String startnode = request.getParameterValues("startnode")[0];
 String zeroPath="";



 int [] offset = new int[10];
 for(int d=0; d<offset.length; d++) offset[d]= 0;

 String [] lastLearnObject = new String[10];
 for(int d=0; d<lastLearnObject.length; d++) lastLearnObject[d]= "";
 lastLearnObject[0] = startnode;

 int depth = 1;
 boolean subLearnobjectFound = false;
 boolean[] branches = {true, true, true, true, true, true, true, true, true, true};
 
 %>
    <mm:import reset="true" id="the_last_element">true</mm:import>
    <mm:node id="sn" number="<%= startnode %>">
       <mm:relatednodes type="learnobjects" searchdir="destination" max="1">
          <mm:import reset="true" id="the_last_element">false</mm:import>                    
       </mm:relatednodes>
       <mm:import reset="true" id="the_last_parent"><%= request.getParameter("the_last_parent")%></mm:import>
      <mm:field name="name" jspvar="startnodename" vartype="String" write="false">
        <% 
        zeroPath=startnodename;       
        %>
        </mm:field>      
       <% session.setAttribute("path",zeroPath); %>
       <%@include file="newfromtree.jsp" %>      
    </mm:node>   
<%
String currentPath="";
branches = new boolean[10];
String[] currentPathN = new String[10];
 
 while( (depth > 0 || subLearnobjectFound) && depth < 10)
 {
     subLearnobjectFound = false;
     %>
     
        <mm:list nodes="<%= lastLearnObject[depth-1] %>" path="learnobjects1,posrel,learnobjects2" searchdir="destination" orderby="posrel.pos" directions="UP" max="1" offset="<%= ""+ offset[depth] %>">
        <mm:import id="ow1" reset="true" jspvar="ow1">
        <mm:field  name="learnobjects1.name" jspvar="learnobjects1_name" vartype="String"/>     
        </mm:import>
                
       <mm:import id="ow2" reset="true" jspvar="ow2">
       <mm:field  name="learnobjects2.otype" jspvar="learnobjects2_otype" vartype="String"/>     
       </mm:import>      
       <mm:import id="ow3" reset="true" jspvar="ow3"></mm:import>      
       <mm:import id="owtrim" reset="true"><%=ow2.trim()%></mm:import>       
       <mm:compare referid="owtrim" value="202" >      
       <mm:import id="ow3" reset="true" jspvar="ow3">
       <mm:field  name="learnobjects2.name" jspvar="learnobjects2_name" vartype="String"/>     
       </mm:import>
		<% 		
			currentPathN[depth]=" > "+ow3.trim();			
		%>	
		</mm:compare>
                      
           <%
           currentPath="";
           for(int j=1;j<depth;j++){
        	   currentPath=currentPath+currentPathN[j];
           }  		 
		   session.setAttribute("path",zeroPath+currentPath);      
           %>
                     
           <mm:field  name="learnobjects2.number" jspvar="learnobjects2_number" vartype="String" write="false">            
              <%
                 if(offset[depth]==0)
                 {
                    %>
                       <mm:node number="component.pdf" notfound="skip">
                           <mm:relatednodes type="providers" constraints="providers.number=$provider">
                               <mm:import id="pdfurl" reset="true"><mm:treefile write="true" page="/pdf/pdfchooser.jsp" objectlist="$includePath" referids="$referids" /></mm:import>
                           </mm:relatednodes>
                       </mm:node>
                    <% 
                 }
                 subLearnobjectFound= true;
                 offset[depth]++;
              %>
              <mm:import reset="true" id="the_last_leaf_in_this_level" >true</mm:import>
              <%
                 branches[depth - 1] = false;
              %>
              <mm:list nodes="<%= lastLearnObject[depth-1] %>" path="learnobjects1,posrel,learnobjects2" searchdir="destination" orderby="posrel.pos" directions="UP" max="1" offset="<%= ""+ offset[depth] %>">
                 <mm:import reset="true" id="the_last_leaf_in_this_level">false</mm:import>
                 <%
                    branches[depth - 1] = true;
                 %>
       
              </mm:list>                          
              <%@include file="showlearnobject.jsp" %>
              <%
                 lastLearnObject[depth] = learnobjects2_number;
                 depth ++;
              %>
           </mm:field>         		
        </mm:list>
     <%
     if(!subLearnobjectFound)
     { // go one layer back
        %>
           <mm:node number="<%= lastLearnObject[depth-1] %>">
              <mm:import id="objecttype" reset="true"><mm:nodeinfo type="type" /></mm:import>
              <mm:compare referid="objecttype" valueset="learnblocks">
                 <%
                    if(depth > 1)
                    {// We haven't to close the last level. There is </div> in code.jsp                   	
                       %>
                          </div>                         
                       <%                       
                    }                                  	               
                 %>                 
              </mm:compare>             
           </mm:node>
        <%
        offset[depth]=0;
        depth--;
     }    
 }
%>
</mm:cloud>
</mm:content>
<% session.removeAttribute("path");%>