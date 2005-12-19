<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>
<mm:content postprocessor="reducespace">
<mm:cloud jspvar="cloud">
  <mm:import externid="node" required="true"/>
   <mm:import externid="node2" required="true"/>
  

  
  <%@include file="/shared/setImports.jsp" %>
  <link rel="stylesheet" type="text/css" href="<mm:treefile page="/portalpages/css/base.css" objectlist="$includePath" referids="$referids" />" />
  <mm:import jspvar="helpLink"><%=request.getRequestURL()%>?node=<mm:write referid="node"/></mm:import>
  <mm:node number="$node" notfound="skipbody">
    <h1><mm:field name="name"/></h1><br/>
    <table width="100%">
      <mm:relatednodes type="helpnodes"> 
    	<mm:remove referid="notgeneral"/>
    	<mm:relatednodes type="educations">
    	  <mm:import id="notgeneral" reset="true">true</mm:import>
    	</mm:relatednodes>
    	<mm:relatednodes type="roles">
    	  <mm:import id="notgeneral" reset="true">true</mm:import>
    	</mm:relatednodes>    
    	<mm:notpresent referid="notgeneral">     
          <mm:import jspvar="nodeNumber"><mm:field name="number"/></mm:import>
          <tr>
            <td bgcolor="#f8e0c5" style="padding: 1px; padding-left: 5px; cursor: pointer; cursor: hand;" onclick="document.location.href='<%=helpLink%>#h<%=nodeNumber%>'">
              <table cellspacing="0">
                <tr>
                  <td valign="center">
                    <img src="<mm:treefile write='true' page='/gfx/icon_arrow_tab_closed.gif' objectlist='$includePath' referids='$referids'/>">
                  </td>
                  <td style="padding-left: 7px;" class="plaintext">
                    <mm:field name="name"/> 
                  </td>
                </tr>
              </table>
            </td>
          </tr>
        </mm:notpresent>   
      </mm:relatednodes>
      <mm:relatednodes type="simplecontents">    
  	    <mm:import jspvar="contentNumber"><mm:field name="number"/></mm:import>
        <tr>
    	  <td bgcolor="#f8e0c5" style="padding: 1px; padding-left: 5px; cursor: pointer; cursor: hand;" onclick="document.location.href='<%=helpLink%>#h<%=contentNumber%>'">
    	    <table cellspacing="0">
    	      <tr>
    	        <td valign="center">
    	          <img src="<mm:treefile write='true' page='/gfx/icon_arrow_tab_closed.gif' objectlist='$includePath' referids='$referids'/>">
    	        </td>
    	        <td style="padding-left: 7px;" class="plaintext">
    	          <mm:field name="title"/> 
    	        </td>
    	      </tr>
    	    </table>
    	  </td>
        </tr>  
      </mm:relatednodes>     
    </table>
    <mm:relatednodes type="helpnodes"> 
      <mm:remove referid="notgeneral"/>
      <mm:relatednodes type="educations">
        <mm:import id="notgeneral" reset="true">true</mm:import>
      </mm:relatednodes>
      <mm:relatednodes type="roles">
        <mm:import id="notgeneral" reset="true">true</mm:import>
  	  </mm:relatednodes>    
  	  <mm:notpresent referid="notgeneral">   
  	    <mm:import jspvar="nodeNumber"><mm:field name="number"/></mm:import>
  	    <p>
  	      <a name="h<%=nodeNumber%>"></a>
  	      <table width="100%">  
  	        <tr>
  		      <td bgcolor="#f8e0c5" style="padding: 1px; padding-left: 5px;">
  		        <table cellspacing="0">
  			      <tr>
  			        <td>
  			          <img src="<mm:treefile write='true' page='/gfx/icon_arrow_tab_closed.gif' objectlist='$includePath' referids='$referids'/>">
  			        </td>
  			        <td style="padding-left: 7px;"  class="plaintext">
  			          <b><mm:field name="name"/></b>
  			        </td>
  			      </tr>
  			      <tr>
  			        <td colspan="2">
  			          <table cellspacing="0">
  				        <mm:relatednodes type="simplecontents">    
  					      <mm:import jspvar="contentNumber"><mm:field name="number"/></mm:import>
  					      <tr>
  					        <td bgcolor="#f8e0c5" style="padding: 1px; padding-left: 5px; cursor: pointer; cursor: hand;" onclick="document.location.href='<%=helpLink%>#h<%=contentNumber%>'">
  			                  <table cellspacing="0">
  						        <tr>
  							      <td valign="center">
  							        <img src="<mm:treefile write='true' page='/gfx/icon_arrow_tab_closed.gif' objectlist='$includePath' referids='$referids'/>">
  							      </td>
  							      <td style="padding-left: 7px;" class="plaintext">
  							        <mm:field name="title"/> 
  							      </td>
  							    </tr>
  						      </table>
    						</td>
  					      </tr>  
  					    </mm:relatednodes>  
  			          </table>  			                 
  			        </td>
  			      </tr>
  			    </table>
  		      </td>
  	        </tr>
  		    <tr>
  		      <td style="padding: 10px" bgcolor="#f8eee3"  class="plaintext">
  		        <mm:relatednodes type="simplecontents">  
  			      <mm:import jspvar="contentNumber"><mm:field name="number"/></mm:import>
  			      <p>
  			        <a name="h<%=contentNumber%>"></a>
  			        <table width="100%">  
  			          <tr>
  				        <td bgcolor="#f8e0c5" style="padding: 1px; padding-left: 5px;">
  				          <table cellspacing="0">
  				            <tr>
  				              <td>
  				                <img src="<mm:treefile write='true' page='/gfx/icon_arrow_tab_closed.gif' objectlist='$includePath' referids='$referids'/>">
  				              </td>
  		                      <td style="padding-left: 7px;"  class="plaintext">
  						        <b><mm:field name="title"/></b>
  						      </td>
  					        </tr>
  					      </table>
  				        </td>
  			          </tr>
  			          <tr>
  			            <td style="padding: 10px" bgcolor="#f8eee3" class="plaintext">
  			              <mm:field name="abstract" escape="none"/>
  			            </td>
  			          </tr> 
  			          <tr>
  			            <td style="padding: 10px" bgcolor="#f8eee3" class="plaintext">
  			              <mm:field name="body" escape="none"/>
  			            </td>			           			         
  			          </tr>
  			        </table> 
  			      </p>  
  	            </mm:relatednodes> 			             
  	          </td>
  	        </tr>
  	      </table> 
        </p>
      </mm:notpresent>    
    </mm:relatednodes>
    <mm:relatednodes type="simplecontents">  
  	  <mm:import jspvar="contentNumber"><mm:field name="number"/></mm:import>
      <p>
  	    <a name="h<%=contentNumber%>"></a>
  	    <table width="100%">  
  	  	  <tr>
  		    <td bgcolor="#f8e0c5" style="padding: 1px; padding-left: 5px;">
  		      <table cellspacing="0">
  		        <tr>
  		          <td>
  		             <img src="<mm:treefile write='true' page='/gfx/icon_arrow_tab_closed.gif' objectlist='$includePath' referids='$referids'/>">
  		          </td>
  		          <td style="padding-left: 7px;"  class="plaintext">
  		            <b><mm:field name="title"/></b>
  		          </td>
  		        </tr>
  		      </table>
  		    </td>
  		  </tr>
  		  <tr>
  		    <td style="padding: 10px" bgcolor="#f8eee3"  class="plaintext">
  		      <mm:field name="abstract" escape="none"/>
  		    </td>
  		  </tr> 
  		  <tr>
  		    <td style="padding: 10px" bgcolor="#f8eee3"  class="plaintext">
  		      <mm:field name="body" escape="none"/>
  		    </td>			           			         
  		  </tr>
  	    </table> 
      </p>  
    </mm:relatednodes>      
  </mm:node>

  
  <mm:node number="$node2" notfound="skipbody">
  
   <mm:import jspvar="contentNumber"><mm:field name="number"/></mm:import>
      <p>
        <a name="h<%=contentNumber%>"></a>
        <table width="100%">  
          <tr>
          <td bgcolor="#f8e0c5" style="padding: 1px; padding-left: 5px;">
            <table cellspacing="0">
              <tr>
                <td>
                   <img src="<mm:treefile write='true' page='/gfx/icon_arrow_tab_closed.gif' objectlist='$includePath' referids='$referids'/>">
                </td>
                <td style="padding-left: 7px;"  class="plaintext">
                  <b><mm:field name="title"/></b>
                </td>
              </tr>
            </table>
          </td>
        </tr>
        <tr>
          <td style="padding: 10px" bgcolor="#f8eee3"  class="plaintext">
            <mm:field name="abstract" escape="none"/>
          </td>
        </tr> 
        <tr>
          <td style="padding: 10px" bgcolor="#f8eee3"  class="plaintext">
            <mm:field name="body" escape="none"/>
          </td>                              
        </tr>
        </table> 
      </p>  
  
  </mm:node>
  
  
 
  
  
</mm:cloud>
</mm:content>