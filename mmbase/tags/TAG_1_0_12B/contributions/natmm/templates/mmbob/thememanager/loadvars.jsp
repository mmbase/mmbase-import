<%-- check whether the thememanager has been installed --%>
<mm:import id="thememanager">true</mm:import>
<%-- use thememanager --%>
<mm:present referid="thememanager">
<%-- get the context of the thememanger, this is used to create urls --%>
<mm:import id="context"><mm:url page="/mmbase/thememanager"/></mm:import>
<%-- set the imagecontext, this can be used if images are stored somewhere else --%>
<mm:import id="imagecontext"><mm:write referid="context"/>/images</mm:import>
<mm:import externid="forumid" id="tmpid" />
<mm:present referid="tmpid">
<mm:import id="themeid">MMBob.<mm:write referid="tmpid" /></mm:import>
<mm:import id="tmptest"><mm:function set="thememanager" name="getStyleSheet" referids="context,themeid" /></mm:import> 
<mm:compare referid="tmptest" value="">
<mm:import id="themeid" reset="true">MMBob</mm:import>
</mm:compare>
</mm:present>
<mm:present referid="tmpid" inverse="true">
<mm:import id="themeid">MMBob</mm:import>
</mm:present>
<mm:import id="style_default"><mm:function set="thememanager" name="getStyleSheet" referids="context,themeid" /></mm:import> 

<mm:import id="imageid" reset="true">arrowright</mm:import>
<mm:import id="image_arrowright"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></mm:import>
<mm:import id="imageid" reset="true">arrowleft</mm:import>
<mm:import id="image_arrowleft"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></mm:import>
<mm:import id="imageid" reset="true">logo</mm:import>
<mm:import id="image_logo"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></mm:import>

<mm:import id="imageid" reset="true">privatemsg</mm:import>
<mm:import id="image_privatemsg"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></mm:import>
<mm:import id="imageid" reset="true">quotemsg</mm:import>
<mm:import id="image_quotemsg"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></mm:import>
<mm:import id="imageid" reset="true">editmsg</mm:import>
<mm:import id="image_editmsg"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></mm:import>
<mm:import id="imageid" reset="true">newmsg</mm:import>
<mm:import id="image_newmsg"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></mm:import>
<mm:import id="imageid" reset="true">newreply</mm:import>
<mm:import id="image_newreply"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></mm:import>
<mm:import id="imageid" reset="true">medit</mm:import>
<mm:import id="image_medit"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></mm:import>
<mm:import id="imageid" reset="true">mdelete</mm:import>
<mm:import id="image_mdelete"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></mm:import>

<mm:import id="imageid" reset="true">state_normal</mm:import>
<mm:import id="image_state_normal"><img align="absmiddle" src="<mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" />"></mm:import>
<mm:import id="imageid" reset="true">state_closed</mm:import>
<mm:import id="image_state_closed"><img align="absmiddle" src="<mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" />"></mm:import>
<mm:import id="imageid" reset="true">state_pinned</mm:import>
<mm:import id="image_state_pinned"><img align="absmiddle" src="<mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" />"></mm:import>
<mm:import id="imageid" reset="true">state_hot</mm:import>
<mm:import id="image_state_hot"><img align="absmiddle" src="<mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" />"></mm:import>
<mm:import id="imageid" reset="true">state_normalnew</mm:import>
<mm:import id="image_state_normalnew"><img align="absmiddle" src="<mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" />"></mm:import>
<mm:import id="image_state_new"><img align="absmiddle" src="<mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" />"></mm:import>
<mm:import id="imageid" reset="true">state_hotnew</mm:import>
<mm:import id="image_state_hotnew"><img align="absmiddle" src="<mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" />"></mm:import>
<mm:import id="imageid" reset="true">state_closed</mm:import>
<mm:import id="image_state_closedme"><img align="absmiddle" src="<mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" />"></mm:import>
<mm:import id="imageid" reset="true">state_pinned</mm:import>
<mm:import id="image_state_pinnedme"><img align="absmiddle" src="<mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" />"></mm:import>

<mm:import id="imageid" reset="true">state_normalme</mm:import>
<mm:import id="image_state_normalme"><img align="absmiddle" src="<mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" />"></mm:import>
<mm:import id="imageid" reset="true">state_normalnewme</mm:import>
<mm:import id="image_state_normalnewme"><img align="absmiddle" src="<mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" />"></mm:import>
<mm:import id="image_state_newme"><img align="absmiddle" src="<mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" />"></mm:import>
<mm:import id="imageid" reset="true">state_hotme</mm:import>
<mm:import id="image_state_hotme"><img align="absmiddle" src="<mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" />"></mm:import>
<mm:import id="imageid" reset="true">state_hotnewme</mm:import>
<mm:import id="image_state_hotnewme"><img align="absmiddle" src="<mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" />"></mm:import>



<mm:import id="imageid" reset="true">mood_normal</mm:import>
<mm:import id="image_mood_normal"><img src="<mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" />"></mm:import>
<mm:import id="imageid" reset="true">mood_mad</mm:import>
<mm:import id="image_mood_mad"><img src="<mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" />"></mm:import>
<mm:import id="imageid" reset="true">mood_happy</mm:import>
<mm:import id="image_mood_happy"><img src="<mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" />"></mm:import>
<mm:import id="imageid" reset="true">mood_question</mm:import>
<mm:import id="image_mood_question"><img src="<mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" />"></mm:import>
<mm:import id="imageid" reset="true">mood_warning</mm:import>
<mm:import id="image_mood_warning"><img src="<mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" />"></mm:import>
<mm:import id="imageid" reset="true">mood_joke</mm:import>
<mm:import id="image_mood_joke"><img src="<mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" />"></mm:import>
<mm:import id="imageid" reset="true">mood_sad</mm:import>
<mm:import id="image_mood_sad"><img src="<mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" />"></mm:import>
<mm:import id="imageid" reset="true">mood_idea</mm:import>
<mm:import id="image_mood_idea"><img src="<mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" />"></mm:import>
<mm:import id="imageid" reset="true">mood_suprised</mm:import>
<mm:import id="image_mood_suprised"><img src="<mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" />"></mm:import>

</mm:present>
<mm:present referid="thememanager" inverse="true">
<mm:import id="style_default"><link rel="stylesheet" type="text/css" href="css/mmbase-dev.css" /></mm:import>
<mm:import id="image_arrowright">images/arrow-right.gif</mm:import>
<mm:import id="image_arrowleft">images/arrow-left.gif</mm:import>
<mm:import id="image_logo"></mm:import>
</mm:present>
