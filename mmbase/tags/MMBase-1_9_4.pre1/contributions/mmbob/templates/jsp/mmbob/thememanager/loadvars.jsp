<%-- this is a @ include file--%>

<%-- check whether the thememanager has been installed --%>
<mm:import id="thememanager">true</mm:import>

<%-- use thememanager --%>
<mm:present referid="thememanager">

    <%-- get the context of the thememanger, this is used to create urls --%>
    <mm:import id="context">${pageContext.request.contextPath}/mmbase/thememanager</mm:import>

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
    <mm:import  id="themename"></mm:import>
    <mm:nodelistfunction set="thememanager" name="getAssignedList">
       <mm:import reset="true" id="tid"><mm:field name="id"/></mm:import>
       <mm:import reset="true" id="themeDir"><mm:field name="theme"/></mm:import>
       <mm:compare referid="themeid" referid2="tid">
          <mm:import reset="true" id="themename"><mm:field name="theme"/></mm:import>
       </mm:compare>
    </mm:nodelistfunction>

    <%--  global stuff--%>
    <mm:import id="theme_html"><mm:write referid="context"/>/html/<mm:write referid="themename"/></mm:import>
    <mm:import id="theme_images"><mm:write referid="context"/>/images/<mm:write referid="themename"/>/default</mm:import>
    <mm:import id="theme_header"><mm:write referid="theme_html"/>/header.jsp</mm:import>
    <mm:import id="theme_footer"><mm:write referid="theme_html"/>/footer.jsp</mm:import>

    <mm:import id="style_default"><mm:function set="thememanager" name="getStyleSheet" referids="context,themeid" /></mm:import>

    <mm:import id="imageid" reset="true">arrowright</mm:import>
    <mm:import id="image_arrowright"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></mm:import>
    <mm:import id="imageid" reset="true">arrowleft</mm:import>
    <mm:import id="image_arrowleft"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></mm:import>
    <mm:import id="imageid" reset="true">logo</mm:import>
    <mm:import id="image_logo"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></mm:import>

    <%--  message icons--%>
    <mm:import id="imageid" reset="true">reportmsg</mm:import>
    <mm:import id="image_reportmsg"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></mm:import>
    <mm:import id="imageid" reset="true">privatemsg</mm:import>
    <mm:import id="image_privatemsg"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></mm:import>
    <mm:import id="imageid" reset="true">quotemsg</mm:import>
    <mm:import id="image_quotemsg"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></mm:import>
    <mm:import id="imageid" reset="true">editmsg</mm:import>
    <mm:import id="image_editmsg"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></mm:import>
    <mm:import id="imageid" reset="true">newmsg</mm:import>
    <mm:import id="image_newmsg"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></mm:import>
    <mm:import id="imageid" reset="true">deletemsg</mm:import>
    <mm:import id="image_deletemsg"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></mm:import>
    <mm:import id="imageid" reset="true">newreply</mm:import>
    <mm:import id="image_newreply"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></mm:import>
    <mm:import id="imageid" reset="true">medit</mm:import>
    <mm:import id="image_medit"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></mm:import>
    <mm:import id="imageid" reset="true">mdelete</mm:import>
    <mm:import id="image_mdelete"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></mm:import>

    <%--state icons--%>
    <mm:import id="imageid" reset="true">state_normal</mm:import>
    <c:set var="t"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></c:set>
    <mm:import id="image_state_normal"><img align="absmiddle" src="${t}"></mm:import>

    <mm:import id="imageid" reset="true">state_closed</mm:import>
    <c:set var="t"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></c:set>
    <mm:import id="image_state_closed"><img align="absmiddle" src="${t}"></mm:import>

    <mm:import id="imageid" reset="true">state_pinned</mm:import>
    <c:set var="t"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid"/></c:set>
    <mm:import id="image_state_pinned"><img align="absmiddle" src="${t}"></mm:import>

    <mm:import id="imageid" reset="true">state_hot</mm:import>
    <c:set var="t"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></c:set>
    <mm:import id="image_state_hot"><img align="absmiddle" src="${t}"></mm:import>

    <mm:import id="imageid" reset="true">state_normalnew</mm:import>
    <c:set var="t"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></c:set>
    <mm:import id="image_state_normalnew"><img align="absmiddle" src="${t}"></mm:import>
    <mm:import id="image_state_new"><img align="absmiddle" src="${t}"></mm:import>

    <mm:import id="imageid" reset="true">state_hotnew</mm:import>
    <c:set var="t"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></c:set>
    <mm:import id="image_state_hotnew"><img align="absmiddle" src="${t}"></mm:import>

    <mm:import id="imageid" reset="true">state_closed</mm:import>
    <c:set var="t"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></c:set>
    <mm:import id="image_state_closedme"><img align="absmiddle" src="${t}"></mm:import>

    <mm:import id="imageid" reset="true">state_pinned</mm:import>
    <c:set var="t"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></c:set>
    <mm:import id="image_state_pinnedme"><img align="absmiddle" src="${t}"></mm:import>

    <mm:import id="imageid" reset="true">state_pinnedclosed</mm:import>
    <c:set var="t"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></c:set>
    <mm:import id="image_state_pinnedclosed"><img align="absmiddle" src="${t}"></mm:import>

    <mm:import id="imageid" reset="true">state_pinnedclosed</mm:import>
    <c:set var="t"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></c:set>
    <mm:import id="image_state_pinnedclosedme"><img align="absmiddle" src="${t}"></mm:import>



    <mm:import id="imageid" reset="true">state_normalme</mm:import>
    <c:set var="t"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></c:set>
    <mm:import id="image_state_normalme"><img align="absmiddle" src="${t}"></mm:import>

    <mm:import id="imageid" reset="true">state_normalnewme</mm:import>
    <c:set var="t"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></c:set>
    <mm:import id="image_state_normalnewme"><img align="absmiddle" src="${t}"></mm:import>
    <mm:import id="image_state_newme"><img align="absmiddle" src="${t}"></mm:import>

    <mm:import id="imageid" reset="true">state_hotme</mm:import>
    <c:set var="t"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></c:set>
    <mm:import id="image_state_hotme"><img align="absmiddle" src="${t}"></mm:import>

    <mm:import id="imageid" reset="true">state_hotnewme</mm:import>
    <c:set var="t"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></c:set>
    <mm:import id="image_state_hotnewme"><img align="absmiddle" src="${t}"></mm:import>


    <%--  mood icons--%>
    <mm:import id="imageid" reset="true">mood_normal</mm:import>
    <c:set var="t"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></c:set>
    <mm:import id="image_mood_normal"><img src="${t}"></mm:import>

    <mm:import id="imageid" reset="true">mood_mad</mm:import>
    <c:set var="t"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></c:set>
    <mm:import id="image_mood_mad"><img src="${t}"></mm:import>


    <mm:import id="imageid" reset="true">mood_happy</mm:import>
    <c:set var="t"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></c:set>
    <mm:import id="image_mood_happy"><img src="${t}"></mm:import>

    <mm:import id="imageid" reset="true">mood_question</mm:import>
    <c:set var="t"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></c:set>
    <mm:import id="image_mood_question"><img src="${t}"></mm:import>

    <mm:import id="imageid" reset="true">mood_warning</mm:import>
    <c:set var="t"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></c:set>
    <mm:import id="image_mood_warning"><img src="${t}"></mm:import>

    <mm:import id="imageid" reset="true">mood_joke</mm:import>
    <c:set var="t"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></c:set>
    <mm:import id="image_mood_joke"><img src="${t}"></mm:import>

    <mm:import id="imageid" reset="true">mood_sad</mm:import>
    <c:set var="t"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></c:set>
    <mm:import id="image_mood_sad"><img src="${t}"></mm:import>

    <mm:import id="imageid" reset="true">mood_idea</mm:import>
    <c:set var="t"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></c:set>
    <mm:import id="image_mood_idea"><img src="${t}"></mm:import>

    <mm:import id="imageid" reset="true">mood_suprised</mm:import>
    <c:set var="t"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></c:set>
    <mm:import id="image_mood_suprised"><img src="${t}"></mm:import>

    <mm:import id="imageid" reset="true">guest</mm:import>
    <c:set var="t"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></c:set>
    <mm:import id="image_guest"><img src="${t}"></mm:import>


</mm:present>

<mm:present referid="thememanager" inverse="true">
    <mm:import id="style_default"><link rel="stylesheet" type="text/css" href="css/mmbase-dev.css" /></mm:import>
    <mm:import id="image_arrowright">images/arrow-right.gif</mm:import>
    <mm:import id="image_arrowleft">images/arrow-left.gif</mm:import>
    <mm:import id="image_logo"></mm:import>
</mm:present>
