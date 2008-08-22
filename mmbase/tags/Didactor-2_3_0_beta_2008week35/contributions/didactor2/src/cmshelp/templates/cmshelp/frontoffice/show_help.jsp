<?xml version="1.0" encoding="UTF-8" ?>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud jspvar="cloud">
  <mm:import externid="node" required="true"/>
  <mm:import externid="node2"/>
  <%@include file="/shared/setImports.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
	<title>Help</title>
  <link rel="stylesheet" type="text/css" href="<mm:treefile page="/cmshelp/css/cmshelp.css" objectlist="$includePath" referids="$referids" />" />
</head>
<body>
  <mm:import jspvar="helpLink"><%=request.getRequestURL()%>?node=<mm:write referid="node"/></mm:import>
  
  <mm:node number="$node" notfound="skipbody">
    <h1><mm:field name="name"/></h1><br/>
    
  	<mm:node number="$node" notfound="skipbody">
  		<mm:treeinclude page="/education/paragraph/paragraph_anonymous.jsp" objectlist="$includePath" referids="$referids">
  			<mm:param name="node_id"><mm:write referid="node"/></mm:param>
  			<mm:param name="path_segment">../</mm:param>
  		</mm:treeinclude>
  	</mm:node>
      
    
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
                        	      <td valign="middle">
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
  			          <!-- added -->
  			          <tr>
			  		    <td style="padding: 10px" bgcolor="#f8eee3"  class="plaintext">
			  		      <mm:field name="impos">
						    <mm:compare value="1">	
						      <table>
						        <tr>
						          <td>
						            <mm:relatednodes type="images">
						              <b><mm:field name="title" /></b>
						              <img src="<mm:image />" width="200" border="0" /><br/>
						              <mm:field name="description" /> 
						            </mm:relatednodes>
						          </td>
						        </tr>
						      </table>
						    </mm:compare>
						  </mm:field>
						  <mm:field name="impos">
						    <mm:compare value="0">
						      <table>
						        <tr>
						         <mm:relatednodes type="images">
						           <b><mm:field name="title" /></b><br/>
						           <img src="<mm:image />" width="200" border="0" />
						           <mm:field name="description" /> 
						         </mm:relatednodes>
						        </tr>		        
						      </table>	  
						    </mm:compare>
						  </mm:field>
						  <mm:field name="impos">
						    <mm:compare value="2">
						      <table> 
						        <tr >	
						  	      <td>   
						            <mm:relatednodes type="images">
						              <table>
						                <tr align="left"><td><b><mm:field name="title"/></b></td></tr>
						                <tr align="left"><td> <mm:field name="description" /> </td> </tr>
						                <tr> <td><img src="<mm:image />" width="200" border="0"/><br/></td></tr>
						              </table>
						              <hr/>
						            </mm:relatednodes>
						          </td>             
						        </tr>
						      </table>
						    </mm:compare>
						  </mm:field>
			  		    </td>			           			         
			  		  </tr>
              
              

      <!--  /added -->
  <!-- cms changes start -->
           <!-- related videotapes -->
           
          
           <mm:relatednodes path="videotapes">
                 <mm:import id="existVideoTape" reset="true">true</mm:import>
                 <mm:field name="url" jspvar="url" vartype="string">
                   <% 
                    String type = url.substring(url.lastIndexOf(".")+1).toLowerCase();
                    if (type.equals("swf")) { 
                   %>
                     <jsp:include page="presentationspace/flashplayer.jsp" flush="true">
                       <jsp:param name="url" value="<%=url%>"/>
                     </jsp:include>
                   <%} else if (type.equals("rm")) {%>
   
                     <jsp:include page="presentationspace/realplayer.jsp" flush="true">
                       <jsp:param name="url" value="<%=url%>"/>
                     </jsp:include>
   
                   <%} else if (type.equals("mov")) {%>   
                     <jsp:include page="presentationspace/quicktimeplayer.jsp" flush="true">
                       <jsp:param name="url" value="<%=url%>"/>
                     </jsp:include>
   
                   <%} else if (type.equals("mpg") || type.equals("mpeg") || type.equals("avi") || type.equals("wmv")) {%>
                     <jsp:include page="presentationspace/mediaplayer.jsp" flush="true">
                       <jsp:param name="url" value="<%=url%>"/>
                     </jsp:include> 
    
                   <%} else {%> 
                     <tr> 
                       <td bgcolor="#f8eee3"  class="plaintext">
                          There is no valid media file for presentation.
                       </td> 
                     </tr>  
                   <%}%>
 
                 </mm:field>
             </mm:relatednodes>

         <!-- /related videotapes --> 
          
          <!-- related audiotapes -->
          <tr>
          <td bgcolor="#f8eee3"  class="plaintext"> 
      <mm:relatednodes type="audiotapes">       
          <b><mm:field name="title"/></b>       
        <p>         
          <i><mm:field name="subtitle"/></i>        
        </p>        
        <i><mm:field name="intro" escape="p"/></i>        
        <p>       
        <mm:field name="body" escape="inline"/><br>
        <mm:field name="url" jspvar="url3" vartype="string">
                   <% 
                    String type = url3.substring(url3.lastIndexOf(".")+1).toLowerCase();
                    if (type.equals("mp3")) { 
                   %>
              
                     <jsp:include page="presentationspace/audiomediaplayer.jsp" flush="true">
                       <jsp:param name="url" value="<%=url3%>"/>
                     </jsp:include> 
<%-- 
          <EMBED
            src="<mm:field name="url" />"
            width="100%"
            height="45"
            autostart="0"
            type="application/x-mplayer2">
            <NOEMBED><A HREF="<%=config.getServletContext().getRealPath("/")+request.getParameter("url")%>">Play with Windows Media Player.</A></NOEMBED> 
          </EMBED>
--%>                                
                   <%} %>
        </mm:field>
          
        </p>
      </mm:relatednodes>
      </td>
           </tr>
          <!-- /related audiotapes --> 
          
          <!-- related attachments -->
          <tr>
          <td bgcolor="#f8eee3"  class="plaintext">
          <mm:relatednodes path="attachments">
             <mm:import id="attachName" reset="true" jspvar="fname"><mm:field name="filename" /></mm:import>
             <mm:import id="attachNumber" reset="true"><mm:field name="number" /></mm:import>              
             <% 
              String type2 = fname.substring(fname.lastIndexOf(".")+1).toLowerCase();
              if (type2.equals("swf")) {  
                  
              %>
                
                <jsp:include page="presentationspace/flashplayer.jsp" flush="true">
                  <jsp:param name="url" value="<%=fname%>"/>
                </jsp:include>
              <%}
              else{%>
                <a href="<% out.println(request.getContextPath());%>/attachment.db?<mm:field name="number" />"><mm:field name="filename" /></a>
              <% } %>
           <br/>            
           </mm:relatednodes>
           </td>
           </tr>
          <!-- /related attachments -->
          
          <!-- cms changes end -->

              
              
              
  			        </table> 
  			      </p>  
 
  	            </mm:relatednodes> 			             
  	          </td>
  	        </tr>
  	        <%-- Added by Nix for paragraphs --%>
  	        <mm:relatednodes type="paragraphs">
  	        <tr>
  	          <td colspan="2">
  	            <%-- TODO: detect if "showtitle" was on and display paragraph accordingly --%>
  	            <h2><mm:field name="title" /></h2>
  	            <p><mm:field name="body" /></p>
  	          </td>
  	        </tr>
  	        </mm:relatednodes>
  	      </table> 
        </p>
      </mm:notpresent>    
    </mm:relatednodes>
    <mm:relatednodes type="simplecontents">  
  	  <mm:import jspvar="contentNumber2"><mm:field name="number"/></mm:import>
      <p>
  	    <a name="h<%=contentNumber2%>"></a>
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
  		  
  		  <!--  added -->
  		  <tr>
  		    <td style="padding: 10px" bgcolor="#f8eee3"  class="plaintext">
  		      <mm:field name="impos">
			    <mm:compare value="1">	
			      <table>
			        <tr>
			          <td>
			            <mm:relatednodes type="images">
			              <b><mm:field name="title" /></b>
			              <img src="<mm:image />" width="200" border="0" /><br/>
			              <mm:field name="description" /> 
			            </mm:relatednodes>
			          </td>
			        </tr>
			      </table>
			    </mm:compare>
			  </mm:field>
			  <mm:field name="impos">
			    <mm:compare value="0">
			      <table>
			        <tr>
			         <mm:relatednodes type="images">
			           <b><mm:field name="title" /></b><br/>
			           <img src="<mm:image />" width="200" border="0" />
			           <mm:field name="description" /> 
			         </mm:relatednodes>
			        </tr>		        
			      </table>	  
			    </mm:compare>
			  </mm:field>
			  <mm:field name="impos">
			    <mm:compare value="2">
			      <table> 
			        <tr >	
			  	      <td>   
			            <mm:relatednodes type="images">
			              <table>
			                <tr align="left"><td><b><mm:field name="title"/></b></td></tr>
			                <tr align="left"><td> <mm:field name="description" /> </td> </tr>
			                <tr> <td><img src="<mm:image />" width="200" border="0"/><br/></td></tr>
			              </table>
			              <hr/>
			            </mm:relatednodes>
			          </td>             
			        </tr>
			      </table>
			    </mm:compare>
			  </mm:field>
  		    </td>			           			         
  		  </tr>
  		  <!--  /added -->
  <!-- cms changes start -->
           <!-- related videotapes -->
           
          
	         <mm:relatednodes path="videotapes">
                 <mm:import id="existVideoTape" reset="true">true</mm:import>
                 <mm:field name="url" jspvar="url" vartype="string">
                   <%	
                  	String type = url.substring(url.lastIndexOf(".")+1).toLowerCase();
                  	if (type.equals("swf")) {	
                   %>
                     <jsp:include page="presentationspace/flashplayer.jsp" flush="true">
                       <jsp:param name="url" value="<%=url%>"/>
                     </jsp:include>
                   <%} else if (type.equals("rm")) {%>
   
                     <jsp:include page="presentationspace/realplayer.jsp" flush="true">
                       <jsp:param name="url" value="<%=url%>"/>
                     </jsp:include>
   
                   <%} else if (type.equals("mov")) {%>   
                     <jsp:include page="presentationspace/quicktimeplayer.jsp" flush="true">
                       <jsp:param name="url" value="<%=url%>"/>
                     </jsp:include>
   
                   <%} else if (type.equals("mpg") || type.equals("mpeg") || type.equals("avi") || type.equals("wmv")) {%>
                     <jsp:include page="presentationspace/mediaplayer.jsp" flush="true">
                       <jsp:param name="url" value="<%=url%>"/>
                     </jsp:include>	
   	
                   <%} else {%> 
                     <tr> 
                       <td bgcolor="#f8eee3"  class="plaintext">
                          There is no valid media file for presentation.
                       </td> 
                     </tr>  
                   <%}%>
 
                 </mm:field>
             </mm:relatednodes>

         <!-- /related videotapes --> 
          
          <!-- related audiotapes -->
          <tr>
          <td bgcolor="#f8eee3"  class="plaintext"> 
			<mm:relatednodes type="audiotapes">				
			    <b><mm:field name="title"/></b>				
			  <p>					
			    <i><mm:field name="subtitle"/></i>				
			  </p>				
			  <i><mm:field name="intro" escape="p"/></i>				
			  <p>				
			  <mm:field name="body" escape="inline"/><br>
			  <mm:field name="url" jspvar="url3" vartype="string">
                   <%	
                  	String type = url3.substring(url3.lastIndexOf(".")+1).toLowerCase();
                  	if (type.equals("mp3")) {	
                   %>
              
                     <jsp:include page="presentationspace/audiomediaplayer.jsp" flush="true">
                       <jsp:param name="url" value="<%=url3%>"/>
                     </jsp:include> 
<%-- 
          <EMBED
					  src="<mm:field name="url" />"
					  width="100%"
					  height="45"
					  autostart="0"
					  type="application/x-mplayer2">
					  <NOEMBED><A HREF="<%=config.getServletContext().getRealPath("/")+request.getParameter("url")%>">Play with Windows Media Player.</A></NOEMBED> 
					</EMBED>
--%>                     				    
                   <%} %>
			  </mm:field>
			    
			  </p>
			</mm:relatednodes>
			</td>
           </tr>
          <!-- /related audiotapes --> 
          
          <!-- related attachments -->
          <tr>
          <td bgcolor="#f8eee3"  class="plaintext">
          <mm:relatednodes path="attachments">
             <mm:import id="attachName" reset="true" jspvar="fname"><mm:field name="filename" /></mm:import>
             <mm:import id="attachNumber" reset="true"><mm:field name="number" /></mm:import>              
             <%	
             	String type2 = fname.substring(fname.lastIndexOf(".")+1).toLowerCase();
             	if (type2.equals("swf")) {	
              %>
                <jsp:include page="presentationspace/flashplayer.jsp" flush="true">
                  <jsp:param name="url" value="<%=fname%>"/>
                </jsp:include>
              <%}
              else{%>
                <a href="<% out.println(request.getContextPath());%>/attachment.db?<mm:field name="number" />"><mm:field name="filename" /></a>
              <% } %>
           <br/>            
           </mm:relatednodes>
           </td>
           </tr>
          <!-- /related attachments -->
          
          <!-- cms changes end -->
	  
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
  
</body>
</html>
</mm:cloud>
</mm:content>
