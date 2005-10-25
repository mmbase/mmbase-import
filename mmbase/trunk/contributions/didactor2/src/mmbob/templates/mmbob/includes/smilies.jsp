    <mm:present referid="thememanager">
    <mm:import id="role">smilies</mm:import>
    <mm:import id="id"><mm:write referid="themeid"/></mm:import>
    <mm:nodelistfunction set="thememanager" name="getThemeImageSets" referids="id,role">
    <mm:node>
    <mm:import id="imagesetid" reset="true"><mm:field name="id"/></mm:import>
    <mm:nodelistfunction set="thememanager" name="getThemeImages" referids="imagecontext,themeid,imagesetid" >
       <mm:node>
         <a href="javascript:insertsmiley('<mm:field name="id"/>')"><img class="smilies" src="<mm:field name="imagelocation"/>" /></a>
       </mm:node>
    </mm:nodelistfunction>
    </mm:node>
    </mm:nodelistfunction>
    </mm:present>
