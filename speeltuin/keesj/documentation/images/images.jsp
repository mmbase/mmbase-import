<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<%@page language="java" contentType="text/html;charset=utf-8" session="false"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:content type="text/html" language="en">

<mm:cloud>
<html>
<head>
</head>
</body>
<mm:node number="images_sample" notfound="skip">
  <mm:import id="imported">true</mm:import>
</mm:node>
<mm:notpresent referid="imported">
</mm:notpresent>
<mm:node number="images_sample">
images is a builder that can hold images and some meta-data. The images
builder can perform operations on images. To perform the operations the
images builder delegates the operation
to a class implementing the ImageConvertInterface. this interface has
only one method.<br>
<pre>byte[] convertImage(byte[] input,Vector comands);
</pre>
In the images builder configuration it is possible (using properties)
to choose what class to use to do the object transformations.<br>
<br>
There are two known implementations:<br>
<dl>
 <dt>org.mmbase.modules.builders.ConvertImageMagick</dt>
 <dd>This implementation uses the external program <a href="http://www.imagemagick.org/www/convert.html">convert</a> from <a href="http://www.imagemagick.org">ImageMagick</a> 
 </dd>

 <dt>org.mmbase.modules.builders.ConvertJAI</dt>
 <dd>This implementation uses the <a href="http://java.sun.com/products/java-media/jai/">Java Advanced Imageing API</a>.
This is a very simple implementation that does not provide as much
features as the ImageMagick implementation but is wel suited for demo's
and development</dd>
</dl>
<br>
images uses the icaches builder for storing the images after
transformation. That way transformations are only performed once. It is
possible to update image in that case the entries
in the icaches are also removed. Still often this is not a good choice
images on the web are oftend cached very aggesively by web-browser
http-proxy servers. Also it looks like MMBase sometimes fails to
invalidate it's memory cache for images so that only after a server
restart the image is visible. you can check if that is the case by
requresting an image from img.db with a size you never asked before. If
you see the new image than you have the "node in memory problem"
(restart required).
<br>
<br>
Requesting an image transformation is done by calling a java servlet named img.db<br>
this servlet accepts parameters separated by a plus sing "+". The first parameter is always
the object number of the image(s). what other parameters can be depends on the convertert interface.
<br>

Some basic transfromations are:<br>
<dl>
       <dt>s(width[,heigt])</dt>
       <dd>Resizes the image to fit in in a square of size with,height the image keeps it's acpect ratio</dd>
        <dt>f(format)</dt>
        <dd>set the output format of the image where format can be gif/jpg/png</dd>       

        <dt>r(angle)</dt>

        <dd>rotate an image where angle is and angle in degrees( like in 360)</dd>
</dl>If you use the ConvertImageMagick implementation the sky is the
limit. The bad thing is that
the content mamager can't tweak the image. it's up to the jsp pages to
create the image url so filters like wave, adding text, creating
buttons etc... are not that easy for a content manager.
The good thing is that they don't have to wory about it, they can
upload tif/bmp/ps etc... .
<br>
<br>
<style>
.pane {
        padding:20px;
	background-image:url(background.png); 
}
</style>

<table width="850">
<tr>
<td>
<mm:include page="image_template.jsp">
  <mm:param name="comment">The original image, By default the size of the image is the orginal image size</mm:param>
  <mm:param name="image"><mm:field name="number"/></mm:param>
</mm:include>

<mm:include page="image_template.jsp">
  <mm:param name="comment">By default, the width and height are maximum values. That is, the image is expanded or contracted to fit the width and height value while maintaining the aspect ratio of the image, in mmbase you can use the "s" template commando to set the size, the first parameter being the width and the second parameter begin the height</mm:param>
  <mm:param name="template">s(200x200)</mm:param>
  <mm:param name="image"><mm:field name="number"/></mm:param>
</mm:include>

<mm:include page="image_template.jsp">
  <mm:param name="comment">Append an exclamation point to the geometry to force the image size to exactly the size you specify. For example, if you specify 200x200! the image width is set to 200 pixels and height to 200.</mm:param>
  <mm:param name="template">s(200x200!)</mm:param>
  <mm:param name="image"><mm:field name="number"/></mm:param>
</mm:include>


<mm:include page="image_template.jsp">
  <mm:param name="comment">If only the width is specified, the width assumes the value and the height is chosen to maintain the aspect ratio of the image. </mm:param>
  <mm:param name="template">s(200)</mm:param>
  <mm:param name="image"><mm:field name="number"/></mm:param>
</mm:include>

<mm:include page="image_template.jsp">
  <mm:param name="comment">Similarly, if only the height is specified , the width is chosen to maintain the aspect ratio.</mm:param>
  <mm:param name="template">s(x100)</mm:param>
  <mm:param name="image"><mm:field name="number"/></mm:param>
</mm:include>


<mm:include page="image_template.jsp">
  <mm:param name="comment">To specify a percentage width or height instead, append %. The image size is multiplied by the width and height percentages to obtain the final image dimensions. To increase the size of an image, use a value greater than 100 (e.g. 125%). To decrease an image's size, use a percentage less than 100.</mm:param>
  <mm:param name="template">s(25%)</mm:param>
  <mm:param name="image"><mm:field name="number"/></mm:param>
</mm:include>

<mm:include page="image_template.jsp">
  <mm:param name="comment">Use &tl; to change the dimensions of the image only if its width or height exceeds the geometry specification.</mm:param>
  <mm:param name="template">-geometry(20x20)</mm:param>
  <mm:param name="image"><mm:field name="number"/></mm:param>
</mm:include>

<mm:include page="image_template.jsp">
  <mm:param name="comment">resize the image so that the fits in a  200x200 pixel box and rotate it 30 degrees</mm:param>
  <mm:param name="template">s(200x200)+r(30)</mm:param>
  <mm:param name="image"><mm:field name="number"/></mm:param>
</mm:include>

<mm:include page="image_template.jsp">
  <mm:param name="comment">resize the image so that the fits in a  200x200 pixel box and rotate it 30 degrees, also save the image in png format(Portable Network Graphics). Png supports transparancy so the ugly border is now gone.</mm:param>
  <mm:param name="template">s(200x200)+r(30)+t(white)+f(png)</mm:param>
  <mm:param name="image"><mm:field name="number"/></mm:param>
</mm:include>

<mm:include page="image_template.jsp">
  <mm:param name="comment">Same as previous but now we
  first rotate and only after resize. the image has now exactely 200
  pixels width. The order of the transformation does matter! But keep in
  mind first rotating a huge image and only later resize it is more work
  for the transformer</mm:param>
  <mm:param name="template">r(30)+s(200x200)+t(white)+f(png)</mm:param>
  <mm:param name="image"><mm:field name="number"/></mm:param>
</mm:include>

<mm:include page="image_template.jsp">
  <mm:param name="comment">It is possible to only take a part of an image using the part command. Let's take a look a my girl friend</mm:param>
  <mm:param name="template">part(140x0x200x100)</mm:param>
  <mm:param name="image"><mm:field name="number"/></mm:param>
</mm:include>

<mm:include page="image_template.jsp">
  <mm:param name="comment">a now I will blow her up</mm:param>
  <mm:param name="template">part(140x0x200x100)+s(200x200)</mm:param>
  <mm:param name="image"><mm:field name="number"/></mm:param>
</mm:include>

<mm:include page="image_template.jsp">
  <mm:param name="comment">We can also create borders</mm:param>
  <mm:param name="template">border(10x10)</mm:param>
  <mm:param name="image"><mm:field name="number"/></mm:param>
</mm:include>

<mm:include page="image_template.jsp">
  <mm:param name="comment">We can also create borders and give them a color</mm:param>
  <mm:param name="template">bordercolor(13370)+border(10x10)</mm:param>
  <mm:param name="image"><mm:field name="number"/></mm:param>
</mm:include>

<mm:include page="image_template.jsp">
  <mm:param name="comment">Remember that te order does matter. here are two borders</mm:param>
  <mm:param name="template">bordercolor(13370)+border(10x10)+bordercolor(ff0000)+border(10x10)</mm:param>
  <mm:param name="image"><mm:field name="number"/></mm:param>
</mm:include>

<mm:include page="image_template.jsp">
  <mm:param name="comment">flipx and flipy can be used to flip an image</mm:param>
  <mm:param name="template">flipx+flipy</mm:param>
  <mm:param name="image"><mm:field name="number"/></mm:param>
</mm:include>

<mm:include page="image_template.jsp">
  <mm:param name="comment">and even drawing</mm:param>
  <mm:param name="template">draw(line 0,0 20,20)</mm:param>
  <mm:param name="image"><mm:field name="number"/></mm:param>
</mm:include>
<br/>
<b>ImageMagick</b> provides very powerfull commands and most can be used inside mmbase (with some hacks).
The convert module accecpts any image magick parameter but there are some conventions to rember
first of all paramters are given between braces.<br/><br/>

and image magick command like<br/>
-pointsize 16 -channel RGBA -fill "rgb(255,106,00)" -stroke "rgb(255,106,00)" -strokewidth 8 -draw "text 20,20 'Scores'"  -stroke white -strokewidth 1 -fill white -draw "text 20,20 'Scores'"<br/>  will become<br/>
scale(200x26!)+pointsize(16)+channel(RGBA)+fill(ffae00)+stroke(ffae00)+strokewidth(8)+draw(text 20,20 'Scores')+stroke(white)+stro
kewidth(1)+fill(white)+draw(text 20,20 'Scores')
<br/>
<br/>

<mm:include page="image_template.jsp">
  <mm:param name="comment">a nice button</mm:param>
  <mm:param name="template">scale(100x26!)+pointsize(16)+channel(RGBA)+fill(ffae00)+stroke(ffae00)+strokewidth(8)+draw(text 20,20 'Scores')+stroke(white)+strokewidth(1)+fill(white)+draw(text 20,20 'Scores')</mm:param>
  <mm:param name="image"><mm:field name="number"/></mm:param>
</mm:include>
</tr>
</td>
</table>
<br>


<h1>Images</h1>

<pre>
When used in combination with ImageMagic
        if (a.equals("s")) return "geometry";
        if (a.equals("r")) return "rotate";
        if (a.equals("c")) return "colors";
        if (a.equals("t")) return "transparent";
        if (a.equals("i")) return "interlace";
        if (a.equals("q")) return "quality";
        if (a.equals("mono")) return "monochrome";
        if (a.equals("highcontrast")) return "contrast";
        if (a.equals("flipx")) return "flop";
        if (a.equals("flipy")) return "flip";
        // I don't think that this makes any sense, I dia is not dianegative,
        // can be diapositive as well... But well, we are backwards compatible.
        if (a.equals("dia")) return "negate";

	</pre>
  </mm:node>
</mm:cloud>
</body>
</html>
</mm:content>
