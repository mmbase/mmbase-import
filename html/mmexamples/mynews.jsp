<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud name="mmbase">
<html>
<head>
   <title>MMExamples - MyNews</title>
   <link rel="stylesheet" type="text/css" href="../share/style.css" />
</head>

<body>
<br />
<table width="90%" align="center">
<tr>
	<td><img src="../../mmadmin/jsp/images/trans.gif" width="50" height="1"></td>
	<th  colspan="3">Description of MyNews</th>
</tr>
<tr>
		<td><img src="../../mmadmin/jsp/images/trans.gif" width="50" height="1"></td>
		<td colspan="3" valign="top">
			<p>
      MyNews is an easy example that show how to create a small
      magazine with news articles.
      </p>
      <p>
			This page will give information about how a magazine like this MyNews magazine is structured.
On your filesystem in the directory /mmexamples/jsp/mynews you will find the templates that show the actual layout of the MyNews magazine.	
			</p>
		</td>
</tr>
<tr>
	<td>
  &nbsp;
	</td>
</tr>


<tr>
	<td><img src="../../mmadmin/jsp/images/trans.gif" width="50" height="1"></td>
	<th colspan="3">Location of MyNews</th>
</tr>
<tr>
		<td><img src="../../mmadmin/jsp/images/trans.gif" width="50" height="1"></td>
		<td colspan="3" >
			<br />
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
              This url will show the MyNew magazine: <a href="<mm:write referid="url" />" target="community"><mm:write referid="url" /></a>
            </mm:present>

			<br /><br /> 
		</td>
</tr>
<tr>
	<td>&nbsp;
	</td>
</TR>

<tr>
	<td><img src="../../mmadmin/jsp/images/trans.gif" width="50" height="1"></td>
	<th>Cloud Design</th>
	<th>Picture</th>
</tr>
<tr>	
		<td><img src="../../mmadmin/jsp/images/trans.gif" width="50" height="1"></td>
		<td valign="top">
	Click on the image to get a good view of the MyNews Cloud Design.<BR>
	The MyNews application consists of 7 builders (objects of a certain type), namely: jumpers, mags, images, news, mmevent, urls, people. In this example we won't use the images and mmevent builders. A magazine is a collection of articles. The line between mags and news indicates that a magazine can have relations with news articles. A news article can have relations with images, urls, mmevents and people. A jumper enables you to use a short url that will be expanded (by the server) to another url. e.g. http://yourhost/jspmynews is a jumper and will be expanded to http://yourhost/mmexamples/jsp/mynews/index.jsp.
		</td>
		<td colspan="2">
		<a href="../share/images/mynews_cloud.jpg" target="img">
		<img src="../share/images/mynews_cloud.jpg" width="220">
		</a>
		</td>
</tr>

<tr>
	<td>
	<br>
	</td>
</tr>


<tr>
	<td><img src="../../mmadmin/jsp/images/trans.gif" width="50" height="1"></td>
	<th>Manual</th>
	<th>Picture</th>
</tr>
<tr>	
		<td><img src="../../mmadmin/images/trans.gif" width="50" height="1"></td>
		<td valign="top">
			With the object cloud design described above you can create webpages like the one you see on the right (By clicking the MyNews url you can see the MyNews magazine in real action). The MyNews magazine consists of a couple of news items. The Title and the Introducation of the newsitems are visualized in this picture. After selecting a news item the complete article will be showed.
		</td>
		<td>
		<a href="../share/images/mynews_manual.jpg" target="img">
		<img src="../share/images/mynews_manual.jpg" width="220">
		</a>
		</td>
</tr>
</table>


</body>
</html>
</mm:cloud>





