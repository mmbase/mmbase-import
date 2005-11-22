<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">

<mm:import externid="question" required="true"/>

<%@include file="/shared/setImports.jsp" %>

<mm:node number="$question" jspvar="flash">
    <% 
        int layout = flash.getIntValue("layout");
        int width = 770;
        int height= 440;
        if (layout >= 0) {
            if (layout >= 2) {
                width = 385;
                height = 220;
                %><table class="Font"><tr><td><%
            }
            if (layout == 2 || layout == 0) {
                %><mm:field name="text" escape="none"/><%
                if (layout == 2) {
                    %></td><td><% 
                }
            }
        }
    %>


    <mm:import id="temp" jspvar="temp"><mm:field name="text" escape="none"/></mm:import>
	 <mm:field name="showtitle">
	    <mm:compare value="1">
	      <h2><mm:field name="title"/></h2>
	    </mm:compare>
	  </mm:field>


	<p/>

<mm:field name="flashOrText">
<mm:compare value="1">
          <mm:relatednodes type="attachments">
              <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,47,0" width="760" height="440" id="flashpage">
              <param name="movie" value="<mm:attachment/>">
              <param name="quality" value="high">
              <embed src="<mm:attachment/>" quality="high" pluginspage="http://www.macromedia.com/shockwave/download/index.cgi?P1_Prod_Version=ShockwaveFlash" type="application/x-shockwave-flash" width="<%= width %>" height="<%= height %>" name="flashpage" swLiveConnect="true">
              </embed> 			  
          </object>
  </mm:relatednodes>
</mm:compare>
</mm:field>



<mm:field name="flashOrText">
<mm:compare value="0">

    <mm:field name="impos">
    <mm:compare value="1">	
        <mm:field name="text" escape="none"/>
	       <div class="images">

            <mm:relatednodes type="images">
              <mm:field name="showtitle">
                <mm:compare value="1">
                  <h3><mm:field name="title"/></h3>
                </mm:compare>
              </mm:field>

              <img src="<mm:image />" width="200" border="0" /><br/>

              <mm:field name="description" escape="none"/> 
            </mm:relatednodes>

          </div>
    </mm:compare>
    </mm:field>

    <mm:field name="impos">
    <mm:compare value="0">	
 
	   <div class="images">

            <mm:relatednodes type="images">
              <mm:field name="showtitle">
                <mm:compare value="1">
                  <h3><mm:field name="title"/></h3>
                </mm:compare>
              </mm:field>

              <img src="<mm:image />" width="200" border="0" /><br/>

              <mm:field name="description" escape="none"/> 
            </mm:relatednodes>

          </div>
          <mm:field name="text" escape="none"/>
    </mm:compare>
    </mm:field>



    <mm:field name="impos">
    <mm:compare value="2">	
	   <div class="images">
            <mm:relatednodes type="images">
              <mm:field name="showtitle">
                <mm:compare value="1">
                  <h3><mm:field name="title"/></h3>
                </mm:compare>
              </mm:field>

            <table>
             <tr>
              
              
              <td><mm:write referid="temp"/></td>
              <td><img src="<mm:image />" width="200" border="0" align="right" /><br/></td>
            </tr>
            </table>
              <mm:field name="description" escape="none"/> 
              <br/>

            </mm:relatednodes>
          
      </div>
    </mm:compare>
    </mm:field>

</mm:compare>
</mm:field>

    <p>
    
    
	<mm:field name="textFirst" escape="none"/> 
		
	<input  type="text" size="15"  dtmaxlength="15" name="<mm:write referid="question"/>" /> 

	<mm:field name="textSecond" escape="none"/> 
   
	<p/>
  
  
 

  

    


</mm:node>

</mm:cloud>
</mm:content>