<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud name="mmbase">
<HTML>
<HEAD>
   <TITLE>MMExamples - MyNews</TITLE>
</HEAD>

<BODY TEXT="#42BDAD" BGCOLOR="#00425B" LINK="#FFFFFF" ALINK="#555555" VLINK="#FFFFF">
<BR>
<TABLE width="90%" cellspacing=1 cellpadding=3 border=0>
<TR>
	<TD><IMG SRC="../../mmadmin/jsp/images/trans.gif" WIDTH="50" HEIGHT="1"></TD>
	<TD BGCOLOR="#44BDAD" COLSPAN=3>
	<FONT COLOR="#000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Description of MyNews</B>
	</TD>
</TR>
<TR>
		<TD><IMG SRC="../../mmadmin/jsp/images/trans.gif" WIDTH="50" HEIGHT="1"></TD>
		<TD BGCOLOR="#00425A" COLSPAN=3 VALIGN="top">
			<BR>
			MyNews is an easy example that show how to create a small magazine with news articles.<BR>
			This page will give information about how a magazine like this MyNews magazine is structured.
On your filesystem in the directory /mmexamples/jsp/mynews you will find the templates that show the actual layout of the MyNews magazine.	
			<BR>
		</TD>
</TR>
<TR>
	<TD>
	<BR>
	</TD>
</TR>


<TR>
	<TD><IMG SRC="../../mmadmin/jsp/images/trans.gif" WIDTH="50" HEIGHT="1"></TD>
	<TD BGCOLOR="#44BDAD" COLSPAN=3>
	<FONT COLOR="#000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Location of MyNews</B>
	</TD>
</TR>
<TR>
		<TD><IMG SRC="../../mmadmin/jsp/images/trans.gif" WIDTH="50" HEIGHT="1"></TD>
		<TD BGCOLOR="#00425A" COLSPAN=3 >
			<BR>

            <mm:list path="versions" fields="name,type" constraints="versions.name='MyNews' AND versions.type='application'">
              <mm:first>
                <mm:import id="mynewsIsPresent">true</mm:import>   
              </mm:first>
            </mm:list>
            <mm:notpresent referid="mynewsIsPresent">
              MyNews application NOT installed please install before using it.<BR>
You can install the MyNews application by going to ADMIN -> APPLICATIONS
            </mm:notpresent>
            <mm:present referid="mynewsIsPresent">
                    
              <mm:url id="url" page="/mmexamples/jsp/mynews/index.jsp" write="false" />
              This url will show the MyNew magazint: <A HREF="<mm:write referid="url" />" TARGET="community"><mm:write referid="url" /></A>
            </mm:present>

			<BR><BR>
		</TD>
</TR>
<TR>
	<TD>
	<BR>
	</TD>
</TR>

<TR>
	<TD><IMG SRC="../../mmadmin/jsp/images/trans.gif" WIDTH="50" HEIGHT="1"></TD>
	<TD BGCOLOR="#44BDAD">
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Cloud Design</B>
	</TD>
	<TD BGCOLOR="#44BDAD" COLSPAN=2>
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Picture</B>
	</TD>
</TR>
<TR>	
		<TD><IMG SRC="../../mmadmin/jsp/images/trans.gif" WIDTH="50" HEIGHT="1"></TD>
		<TD BGCOLOR="#00425A" VALIGN="top">
	Click on the image to get a good view of the MyNews Cloud Design.<BR>
	The MyNews application consists of 7 builders (objects of a certain type), namely: jumpers, mags, images, news, mmevent, urls, people. In this example we won't use the images and mmevent builders. A magazine is a collection of articles. The line between mags and news indicates that a magazine can have relations with news articles. A news article can have relations with images, urls, mmevents and people. A jumper enables you to use a short url that will be expanded (by the server) to another url. e.g. http://yourhost/jspmynews is a jumper and will be expanded to http://yourhost/mmexamples/jsp/mynews/index.jsp.
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN=2>
		<A HREF="../share/images/mynews_cloud.jpg" TARGET="img">
		<IMG SRC="../share/images/mynews_cloud.jpg" WIDTH="220">
		</A>
		</TD>
</TR>

<TR>
	<TD>
	<BR>
	</TD>
</TR>


<TR>
	<TD><IMG SRC="../../mmadmin/jsp/images/trans.gif" WIDTH="50" HEIGHT="1"></TD>
	<TD BGCOLOR="#44BDAD">
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Manual</B>
	</TD>
	<TD BGCOLOR="#44BDAD" COLSPAN=1>
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Picture</B>
	</TD>
</TR>
<TR>	
		<TD><IMG SRC="../../mmadmin/images/trans.gif" WIDTH="50" HEIGHT="1"></TD>
		<TD BGCOLOR="#00425A" VALIGN="top">
			With the object cloud design described above you can create webpages like the one you see on the right (By clicking the MyNews url you can see the MyNews magazine in real action). The MyNews magazine consists of a couple of news items. The Title and the Introducation of the newsitems are visualized in this picture. After selecting a news item the complete article will be showed.
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN=1>
		<A HREF="../share/images/mynews_manual.jpg" TARGET="img">
		<IMG SRC="../share/images/mynews_manual.jpg" WIDTH="220">
		</A>
		</TD>
</TR>

</FORM>

</TABLE>


</BODY>
</HTML>
</mm:cloud>





