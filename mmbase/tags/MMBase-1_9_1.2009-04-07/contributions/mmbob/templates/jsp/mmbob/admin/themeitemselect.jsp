<%@ include file="../jspbase.jsp" %>
<mm:cloud>
<mm:import externid="forumid" />
<mm:import externid="sub" />
<mm:import externid="themename" />
<mm:import id="tmpid" externid="sename" />
<mm:import id="sname"><mm:write referid="themename" />/<mm:write referid="tmpid" /></mm:import>
<mm:function set="thememanager" name="getCSSType" referids="sname">
<mm:import id="oldvalue"><mm:function set="thememanager" name="getCSSValue" referids="sname" /></mm:import>
<mm:compare value="default">
<form action="<mm:url page="changeforum.jsp" referids="forumid,sub" />" method="post" />
<input name="sname" value="<mm:write referid="sname" />" type="hidden" />
<input name="svalue" value="<mm:function set="thememanager" name="getCSSValue" referids="sname" />" size="25">
<input type="hidden" name="admincheck" value="true">
<input type="hidden" name="action" value="changethemedefault">
<input type="submit" value="Save">
</form>
</mm:compare>
<mm:compare value="fontsize">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%">
<tr><td>
<form action="<mm:url page="changeforum.jsp" referids="forumid,sub" />" method="post" />
<input name="sname" value="<mm:write referid="sname" />" type="hidden" />
<select name="svalue">
<option value="8px" <mm:compare referid="oldvalue" value="8px">selected<mm:import id="found">true</mm:import></mm:compare>>8px
<option value="9px" <mm:compare referid="oldvalue" value="9px">selected<mm:import id="found">true</mm:import></mm:compare>>9px
<option value="10px" <mm:compare referid="oldvalue" value="10px">selected<mm:import id="found">true</mm:import></mm:compare>>10px
<option value="11px" <mm:compare referid="oldvalue" value="11px">selected<mm:import id="found">true</mm:import></mm:compare>>11px
<option value="12px" <mm:compare referid="oldvalue" value="12px">selected<mm:import id="found">true</mm:import></mm:compare>>12px
<option value="13px" <mm:compare referid="oldvalue" value="13px">selected<mm:import id="found">true</mm:import></mm:compare>>13px
<option value="14px" <mm:compare referid="oldvalue" value="14px">selected<mm:import id="found">true</mm:import></mm:compare>>14px
<option value="15px" <mm:compare referid="oldvalue" value="15px">selected<mm:import id="found">true</mm:import></mm:compare>>15px
<option value="16px" <mm:compare referid="oldvalue" value="16px">selected<mm:import id="found">true</mm:import></mm:compare>>16px
<option value="17px" <mm:compare referid="oldvalue" value="17px">selected<mm:import id="found">true</mm:import></mm:compare>>17px
<option value="18px" <mm:compare referid="oldvalue" value="18px">selected<mm:import id="found">true</mm:import></mm:compare>>18px
<option value="20px" <mm:compare referid="oldvalue" value="20px">selected<mm:import id="found">true</mm:import></mm:compare>>20px
<option value="22px" <mm:compare referid="oldvalue" value="22px">selected<mm:import id="found">true</mm:import></mm:compare>>22px
<option value="24px" <mm:compare referid="oldvalue" value="24px">selected<mm:import id="found">true</mm:import></mm:compare>>24px
<option value="26px" <mm:compare referid="oldvalue" value="26px">selected<mm:import id="found">true</mm:import></mm:compare>>26px
<option value="28px" <mm:compare referid="oldvalue" value="28px">selected<mm:import id="found">true</mm:import></mm:compare>>28px
<option value="30px" <mm:compare referid="oldvalue" value="30px">selected<mm:import id="found">true</mm:import></mm:compare>>30px
<option value="32px" <mm:compare referid="oldvalue" value="32px">selected<mm:import id="found">true</mm:import></mm:compare>>32px
</select>
<input type="hidden" name="admincheck" value="true">
<input type="hidden" name="action" value="changethemefontsize">
<input type="submit" value="Save">
</form>
</td>
<td>
<form action="<mm:url page="changeforum.jsp" referids="forumid,sub" />" method="post" />
<input name="sname" value="<mm:write referid="sname" />" type="hidden" />
<input name="svalue" size="4" value="<mm:write referid="oldvalue" />" />
<input type="hidden" name="admincheck" value="true">
<input type="hidden" name="action" value="changethemecolor">
<input type="submit" value="Save">
</form>
</td>
</tr>
</table>
</mm:compare>
<mm:compare value="color">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%">
<tr><td>
<form action="<mm:url page="changeforum.jsp" referids="forumid,sub" />" method="post" />
<input name="sname" value="<mm:write referid="sname" />" type="hidden" />
<select name="svalue">
<option value="red" <mm:compare referid="oldvalue" value="red">selected<mm:import id="found">true</mm:import></mm:compare>>red
<option value="green" <mm:compare referid="oldvalue" value="green">selected<mm:import id="found">true</mm:import></mm:compare>>green
<option value="blue" <mm:compare referid="oldvalue" value="blue">selected<mm:import id="found">true</mm:import></mm:compare>>blue
<option value="yellow" <mm:compare referid="oldvalue" value="yellow">selected<mm:import id="found">true</mm:import></mm:compare>>yellow
<option value="white" <mm:compare referid="oldvalue" value="white">selected<mm:import id="found">true</mm:import></mm:compare>>white
<option value="black" <mm:compare referid="oldvalue" value="black">selected<mm:import id="found">true</mm:import></mm:compare>>black
</select>
<input type="hidden" name="admincheck" value="true">
<input type="hidden" name="action" value="changethemecolor">
<input type="submit" value="Save">
</form>
</td>
<td>
<form action="<mm:url page="changeforum.jsp" referids="forumid,sub" />" method="post" />
<input name="sname" value="<mm:write referid="sname" />" type="hidden" />
<input name="svalue" size="8" value="<mm:write referid="oldvalue" />" />
<input type="hidden" name="admincheck" value="true">
<input type="hidden" name="action" value="changethemecolor">
<input type="submit" value="Save">
</form>
</td>
</tr>
</table>
</mm:compare>

<mm:compare value="font">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%">
<tr><td>
<form action="<mm:url page="changeforum.jsp" referids="forumid,sub" />" method="post" />
<input name="sname" value="<mm:write referid="sname" />" type="hidden" />
<select name="svalue">
<option value="Verdana, Arial, Helvetica, sans-serif" <mm:compare referid="oldvalue" value="Verdana, Arial, Helvetica, sans-serif">selected<mm:import id="found">true</mm:import></mm:compare>>Veranda
<option value="Techno, Arial, Helvetica, sans-serif" <mm:compare referid="oldvalue" value="Techno, Arial, Helvetica, sans-serif">selected<mm:import id="found">true</mm:import></mm:compare>>Techno
<option value="Arial, Helvetica, sans-serif" <mm:compare referid="oldvalue" value="Arial, Helvetica, sans-serif">selected<mm:import id="found">true</mm:import></mm:compare>>Arial
<option value="Helvetica, Arial, sans-serif" <mm:compare referid="oldvalue" value="Helvetica, Arial, sans-serif">selected<mm:import id="found">true</mm:import></mm:compare>>Helvetica
</select>
<input type="hidden" name="admincheck" value="true">
<input type="hidden" name="action" value="changethemefont">
<input type="submit" value="Save">
</form>
</td>
<td>
<form action="<mm:url page="changeforum.jsp" referids="forumid,sub" />" method="post" />
<input name="sname" value="<mm:write referid="sname" />" type="hidden" />
<input name="svalue" size="20" value="<mm:write referid="oldvalue" />" />
<input type="hidden" name="admincheck" value="true">
<input type="hidden" name="action" value="changethemecolor">
<input type="submit" value="Save">
</form>
</td>
</tr>
</table>
</mm:compare>

</mm:function>
</mm:cloud>
