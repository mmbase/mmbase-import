<%@include file="/taglibs.jsp" %>
<%@page import="java.util.*,java.net.*,java.io.*,java.awt.*,org.mmbase.bridge.*,java.text.*" %>

<mm:cloud name="mmbase" method="http" rank="basic user" jspvar="cloud">
<mm:import externid="actie" jspvar="actie" id="actie"></mm:import>
<mm:import externid="tX" jspvar="tX">0</mm:import>
<mm:import externid="tY" jspvar="tY">0</mm:import>
<mm:import externid="bX" jspvar="bX">0</mm:import>
<mm:import externid="bY" jspvar="bY">0</mm:import>
<mm:import externid="vwidth" jspvar="vwidth">0</mm:import>
<mm:import externid="vheight" jspvar="vheight">0</mm:import>
<html>
<head>
<title>Editors: crop image</title>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
<%	
Date dd = new Date();
long dateTime = dd.getTime();
dateTime = dateTime / 1000;
String imgID = request.getParameter("img");	if (imgID == null || imgID.equals("")) { imgID = ""; }

%>
<mm:notpresent referid="actie">
   <mm:node number="<%= imgID %>">
   
     <script>
       var moz = ((document.all)? false : true);
       var ie = ((document.all)? true : false);
       var isCropped = false;
       var verhouding = 0.7; // parseInt(document.cropit.scale[document.cropit.scale.selectedIndex].value);
      
      
      function setCropSize(s){
       verhouding = s;
       ImageBox.createBox("image");
      }
      
       function ImageBox(imgId) {      
         var origX, origY;
         var imgPosition, imgDimension;
         var dragDiv, overLayer;
       
         
         function init() {
           var img = document.getElementById(imgId);        
           imgPosition = ElementUtil.getElementPosition(img);
           imgDimension = ElementUtil.getElementDimension(img);        
   
           // I put this div over the image to remove drag behaviour 
           // on image in mozilla.
           if(moz) {
             overLayer = document.createElement("div");
             document.body.appendChild(overLayer);
             overLayer.style.position = "absolute";
             overLayer.style.left = imgPosition.left;
             overLayer.style.top = imgPosition.top;
             overLayer.style.width = imgDimension.width;
             overLayer.style.height = imgDimension.height;
           }
           
           dragDiv = document.createElement("div");
           document.body.appendChild(dragDiv);                        
           dragDiv.style.visibility = "hidden";
           dragDiv.style.position = "absolute";
           dragDiv.style.border = "2px solid black";
           dragDiv.style.width = "0px";
           dragDiv.style.height = "0px";
           dragDiv.style.zIndex = 3;
           dragDiv.style.left = "20px";
           dragDiv.style.top = "20px";
           dragDiv.style.overflow = "hidden";      
                   
           if(ie) {
             // Removes default drag behaviour on image
             ElementUtil.addEventListener(img, "drag", function() {return false;});
             // Adds my "drag" behaviour to the image
             ElementUtil.addEventListener(img, "mousedown", mouseDown);
           }
           if(moz) {
             ElementUtil.addEventListener(overLayer, "mousedown",mouseDown);      
           }        
         }   
   
         function mouseDown(evt) {
           if(!evt) {
             evt = event;
           }
           dragDiv.style.visibility = "visible";
           dragDiv.style.left = evt.clientX;
           dragDiv.style.top = evt.clientY;
   	 	  dragDiv.style.width = "2px";
           dragDiv.style.height = "2px";
           origX = evt.clientX;
           origY = evt.clientY;
           ElementUtil.addEventListener(document, "mousemove", mouseMove);
           ElementUtil.addEventListener(document, "mouseup", mouseUp); 
           evt.cancelBubble = true;
           return false;     
         }
   
         function mouseMove(evt) {
           if(!evt) {
             evt = event;
           }                       
           if(evt.clientX < imgPosition.left) {
             dragDiv.style.left = imgPosition.left;
             dragDiv.style.width = origX - imgPosition.left;        
           }
           else if(evt.clientX > (imgPosition.left + imgDimension.width)) {
   
             dragDiv.style.left = origX;
             if(ie) {            
               dragDiv.style.width = imgDimension.width - (origX - imgPosition.left);
             }
             else if(moz) {
               dragDiv.style.width = imgDimension.width - (origX - imgPosition.left) - 2;
             }
           }
           else {
             var newWidth = evt.clientX - origX;
             var newLeft = -1;
             if(newWidth < 0) {
               if(evt.clientX > imgPosition.left) {
                 newLeft = evt.clientX;
               }
               newWidth = origX - evt.clientX;
             }
             if(newLeft != -1) {
               dragDiv.style.left = newLeft;
             }
             dragDiv.style.width = newWidth;
           }        
           
           if(evt.clientY < imgPosition.top) {
             dragDiv.style.top = imgPosition.top;
             dragDiv.style.height = origY - imgPosition.top;        
           }
           else if(evt.clientY > (imgPosition.top + imgDimension.height)) {
   
             dragDiv.style.top = origY;
             if(ie) {
               dragDiv.style.height = imgDimension.height - (origY - imgPosition.top);
             }
             else if(moz) {
               dragDiv.style.height = imgDimension.height - (origY - imgPosition.top) - 2;
             }
           }
           else {
      		  var newHeight = 0;
      		  if ( verhouding == '0') {
               	 newHeight = evt.clientY - origY;
         		} else {
         		 	newHeight = (evt.clientX - origX ) * verhouding; 
         		}  
      		
              var newTop = -1;
              if(newHeight < 0) {
               if(evt.clientY > imgPosition.top) {
                 newTop = evt.clientY;
               }
               newHeight = origY - evt.clientY;
             }
             if(newTop != -1) {
               dragDiv.style.top = newTop;
             }
             dragDiv.style.height = newHeight;
           }
         }      
         
         function mouseUp(evt) {        
           ElementUtil.removeEventListener(document, "mousemove", mouseMove);  
           ElementUtil.removeEventListener(document, "mouseup", mouseUp);
         } 
         
         init();
               
         this.getX = function() {
           return (parseInt(dragDiv.style.left) - parseInt(imgPosition.left));
         }
         
         this.getY = function() {
           return (parseInt(dragDiv.style.top) - parseInt(imgPosition.top));
         }
         
         this.getWidth = function() {
           return parseInt(dragDiv.style.width);
         }
         
         this.getHeight = function() {
           return parseInt(dragDiv.style.height);
         }    
         
         this.toggleCrop = function() {
           var img = document.getElementById(imgId);
           var str = "";
           if(isCropped) {
             str = "rect(auto auto auto auto)";
         //    isCropped = false;
           }
           else {
             var top = this.getY();
             var left = this.getX();
             var bottom = this.getY() + this.getHeight();
             var right = this.getX() + this.getWidth();
             if(moz) {
               bottom = bottom + 2;
               right = right + 2;
             }
   		  
             str = "rect(" + top + "px, " +   right + "px, " +   bottom + "px, " + left + "px)";  
            // isCropped = true;
           
   		}
   		img.style.clip = str;
   		document.cropit.tX.value=this.getX();
   		document.cropit.tY.value=this.getY();
   		document.cropit.bX.value=this.getX()+this.getWidth();
   		document.cropit.bY.value=this.getY()+this.getHeight();	
   		document.cropit.vwidth.value=this.getWidth();
   		document.cropit.vheight.value=this.getHeight();
         }  
         
         this.toString = function() {
   	  	
   		
           var str = "Left: " + this.getX() + "px\n" +
           "Top: " + this.getY() + "px\n" +
           "Width: " + this.getWidth() + "px\n" + 
           "Height: " + this.getHeight() + "px";
           return str;     
         }
       }
       ImageBox.boxes = new Array();
         ImageBox.createBox = function(imgId) {
         ImageBox.boxes[imgId] = new ImageBox(imgId);
       }
       ImageBox.getBox = function(imgId) {
         return ImageBox.boxes[imgId];
       }
       
       var ElementUtil = new Object();
       ElementUtil.getElementPosition = function(elt){
         var position = new Object();    
         if(elt.style.position == "absolute") {
           position.left = parseInt(elt.style.left);
           position.top = parseInt(elt.style.top);
         }
         else {
           position.left = ElementUtil.calcPosition(elt, "Left");      
           position.top = ElementUtil.calcPosition(elt, "Top");      
         }  
         return position;
       }
       
       ElementUtil.calcPosition = function(elt, dir){
           var tmpElt = elt;
           var pos = parseInt(tmpElt["offset" + dir]);
           while(tmpElt.tagName != "BODY") {
               tmpElt = tmpElt.offsetParent;
               pos += parseInt(tmpElt["offset" + dir]);
           }
           return pos;
       }
       
       ElementUtil.getElementDimension = function(elt) {
         var dim = new Object();
         dim.width = elt.offsetWidth;
         dim.height = elt.offsetHeight;
         return dim;
       }    
       
       ElementUtil.addEventListener = function(o, type, handler) {
         if(ie) {
           o.attachEvent("on" + type, handler);
         }
         else if(moz) {
           o.addEventListener(type, handler, false);
         }
       }
   
       ElementUtil.removeEventListener = function(o, type, handler) {
         if(ie) {
           o.detachEvent("on" + type, handler);
         }
         else if(moz) {
           o.removeEventListener(type, handler, false);
         }
       }
       
       window.onload = function() {
        ImageBox.createBox("image");
   	  window.focus();
    //    ImageBox.createBox("image3");
       }
       
       function cropMe() {
         var box = ImageBox.getBox("image");
   	  isCropped = false;
   	  box.toggleCrop();
   	//  alert(box);	
       }  
   	function resetMe() {
         var box = ImageBox.getBox("image");
   	  isCropped = true;
   	  box.toggleCrop();
         }
       </script>  
   </head>
   <body  style="overflow:auto;">
   <img style="position:absolute;left:0px;top:40px" id="image" src="<mm:image />" alt="<mm:field name="alttext" />" border="0">
   <form action="image_crop.jsp" method="post" name="cropit" id="cropit">
      <input type="hidden" name="tX" value="0">
      <input type="hidden" name="tY" value="0">
      <input type="hidden" name="bX" value="0">
      <input type="hidden" name="bY" value="0">
      <input type="hidden" name="vwidth" value="0">
      <input type="hidden" name="vheight" value="0">
      <input type="hidden" name="actie" value="make">
      <input type="hidden" name="img" value="<%= imgID %>">
      
      Verhouding: <select name="cropsize" id="cropsize" size="1" onchange="setCropSize(this.value)" style="width:250px;" >
         <option value="0" SELECTED>vrij</option>
         <option value="1">vierkant</option>
         <option value="0.2110">homepage pano (744:157)</option>
         <option value="0.1008">naardermeer pano (744:75)</option>
         <option value="0.1854">overige panos(744:138)</option>
         <option value="0.5765">weblog foto (170:98)</option>
      </select>
      <input type="button" value="uitsnijden" onclick="cropMe()" style="width:100px;" />
      <input type="button" value="reset" onclick="resetMe()" style="width:100px;" />
      <input type="submit" value="opslaan" style="width:100px;" />
   </form>
   <div style="position:absolute;left:5px;bottom:5px;height:100px;width:100%;overflow:auto;z-index:100">
   <ol>
      <li>Selecteer een verhouding. De verhouding werkt alleen, zolang de selectie zich binnen de afbeelding bevindt.</li>
      <li>Als de gewenste uitsnede is geselecteerd, klik op "uitsnijden".</li>
      <li>Als uitsnede niet naar wens is, klik op "reset".</li>
      <li>Als de uitsnede naar wens is, klik op "opslaan".</li>
   </ol>
   </div>
   </mm:node>
</mm:notpresent>
<mm:present referid="actie">
   <mm:compare referid="actie" value="make">
      <%
      String toTitle = "";
      String imgParams="part("+tX+","+tY+","+bX+","+bY+")";
      String cacheParams= "cache("+imgParams+")";
      %>
      <mm:node number="<%= imgID %>" jspvar="orgImage">
      <mm:field name="<%=cacheParams%>" jspvar="icacheID" vartype="String" write="false">
      </head>
      <body style="overflow:auto;">
      <img src="<mm:image template="<%=imgParams%>" />" alt="" border="0">
      <%
		String tempdir = System.getProperty("java.io.tmpdir");
		if ( !(tempdir.endsWith("/") || tempdir.endsWith("\\")) ) {
			tempdir += tempdir + System.getProperty("file.separator");
		}
      String toFile = tempdir+imgID+"crop.jpg";
      String my_imgUrl = "http://"+request.getServerName()+":"+request.getServerPort()+"/mmbase/images?"+icacheID; 
      %><!-- <%= my_imgUrl %> --><%
      URL url = new URL(my_imgUrl);
            try {
              URLConnection con = url.openConnection();
              con.connect();
              int length = con.getContentLength();
              int block = 4096;
              int count = 0;
              FileOutputStream fos = new FileOutputStream(toFile);
              InputStream is = con.getInputStream();
              byte[] buff = new byte[block];
              int read = 0;
      	 // out.println("Downloading... ");
              while((read = is.read(buff, 0, block)) != -1) {
                byte[] bytes;
                if(read != buff.length) {
                  bytes = new byte[read];
                  System.arraycopy(buff, 0, bytes, 0, read);
                } else bytes = buff;
                fos.write(bytes);
                count += read;
            // out.println("... " + count);
              }
              fos.flush();
              fos.close();
            } catch(Exception e) {
              out.println("Error creating file " + url);
            }
      	File f = new File(toFile);
      	int fsize = (int)f.length();
      	byte[] thedata = new byte[fsize];
      	try {
      		// out.println(f);
      		FileInputStream instream = new FileInputStream(f);
      		instream.read(thedata);
      		NodeManager imageItemManager = cloud.getNodeManager("images");
      		Node imgNode = imageItemManager.createNode();
            toTitle = orgImage.getStringValue("titel") +" (uitsnede: "+vwidth+"x"+vheight+")";
      		imgNode.setValue("titel",toTitle);
      		imgNode.setValue("handle",thedata);
      		imgNode.setValue("creatiedatum",String.valueOf(dateTime));
      		imgNode.setValue("omschrijving",orgImage.getStringValue("omschrijving"));
      		imgNode.setValue("metatags",orgImage.getStringValue("metatags"));
      		imgNode.setValue("bron",orgImage.getStringValue("bron"));
      		imgNode.setValue("alt_tekst",orgImage.getStringValue("alt_tekst"));
      		imgNode.setValue("titel_fra",orgImage.getStringValue("titel_fra"));
      		imgNode.commit();
            instream.close();
      	} catch (Exception e) {
      		out.println("Exception: " + e);
      	}
      %>
      </mm:field>
      </mm:node>
      </head>
      
      <body>
      
      <br/><br/>
      Nieuwe afbeelding is opgeslagen met titel: <%= toTitle %>
      <br/><br/>
      <a href="/mmbase/edit/wizard/jsp/list.jsp?wizard=config/images/images&nodepath=images&fields=handle,title,metatags,titel_fra,alt_tekst,bron,datumlaatstewijziging&orderby=number&directions=down&pagelength=25&maxpagecount=50&search=yes&searchfields=title,metatags,titel_fra,alt_tekst,bron&maxsize=1048576">terug naar overzicht</a>
   </mm:compare>
</mm:present>
</div>
</body>

</html>
</mm:cloud>