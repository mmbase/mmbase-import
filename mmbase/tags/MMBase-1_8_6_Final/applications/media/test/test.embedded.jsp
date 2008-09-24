<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:import externid="source" required="true" />
<mm:import externid="fragment" required="true" />

    <title></title>
  </head>
  
  <body>
  <mm:cloud>
 <mm:node number="$fragment">
    <h1>Embedded smil</h1>
    <table>
    <tr valign="top"><td>
    <embed 
      src="<mm:url referids="source,fragment" page="test.ram.jsp" />"
      width="400" 
      height="400"   
      type="audio/x-pn-realaudio-plugin"
      nojava="false" 
      controls="ImageWindow" 
      console="Clip1" 
      autostart="true" 
      nologo="true"
      nolabels="true"
      name="embeddedplayer"></embed>
   </td>
   <td align="top">
     <h2> Edit demo for: <mm:field name="title" /></h2>
     <form name="form">
       <input type="button" value="pause" onClick="document.embeddedplayer.DoPause();" /><br />
       <input type="button" value="start" onClick="document.embeddedplayer.DoPlay();" /><br />
       start: <input type="text" value="" name="start" /><input type="button" value="get" onClick="document.forms['form'].start.value = document.embeddedplayer.GetPosition();" /><br />
       end: <input type="text" value="" name="end" /><input type="button" value="get" onClick="document.forms['form'].end.value = document.embeddedplayer.GetPosition();" /><br /><br />

       source: <input type="text" value="" name="source" /><input type="button" value="get" onClick="document.forms['form'].source.value = document.embeddedplayer.GetSource();" />
       <input type="button" value="set" onClick="document.embeddedplayer.SetSource(document.forms['form'].source.value);" /><br /><br />

   
    <input type="button" value="set title" onClick="document.embeddedplayer.SetTitle('<mm:field name="title" />');" /><br /><br />
     </form>
   </td>
   </tr>
   </table>

   </mm:node>
    </mm:cloud>
    <hr />
    Also nice, embedded.
    <br />
    <a href="media.jsp">back</a>
  </body>
</html>
