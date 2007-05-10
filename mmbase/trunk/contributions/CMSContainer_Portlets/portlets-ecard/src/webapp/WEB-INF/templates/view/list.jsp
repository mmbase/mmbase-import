<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
<mm:content type="text/html" encoding="UTF-8">
<mm:cloud method="asis">
     display the images
    <mm:node number="${galleryId}" notfound="skip">
       	<h2><mm:field name="title"/> </h2>  
                     
           <mm:relatednodescontainer type="images" role="imagerel" >
           		<mm:sortorder field="imagerel.number" direction="down" />
                construct the url 
                                
               <mm:relatednodes offset="${offset}" max="${elementsPerPage}" orderby="imagerel.order">
                  	<br>
                  	<a href="#"> 
                  	<img src="<mm:image template="s(175)" />" 
                  	alt="<mm:field name="description" escape="none"/>"/></a><br>
                  	<mm:field name="title" escape="none"/><br>
               </mm:relatednodes>	
          </mm:relatednodescontainer>                                        
            
    </mm:node> 
</mm:cloud>
</mm:content>
