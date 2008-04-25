<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><mm:content type="text/html" postprocessor="reducespace">
<html>
  <head>

  </head>
  <body>
    <h1>Image tag </h1>
    <mm:cloud rank="basic user">
      Images:
    <mm:listnodes id="image" type="images" max="3" orderby="number" directions="down">
       nodeinfo: <mm:nodeinfo type="gui" /><br />
       field: <mm:field escape="none" name="gui()" /> <br />
       cache: <mm:field name="cache(s(100x100))" /> <br />      
       image tag: <a href="<mm:image />"><img <mm:image template="s(100x100)" mode="attributes" /> /></a><br />
       image tag 2:
       <mm:image template="s(200x200)">
         <img src="${_}" width="${dimension.width}" height="${dimension.height}" 
              alt="${mm:escape('text/html/attribute', image.title)}"
              />
       </mm:image>       
       <br />
       image tag 3: <mm:image template="s(150x150)" mode="img" /><br />

    </mm:listnodes>
    </mm:cloud>
  </body>
</html>
</mm:content>
<mm:log>end</mm:log>