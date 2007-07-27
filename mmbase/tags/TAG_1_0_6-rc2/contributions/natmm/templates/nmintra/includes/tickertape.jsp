<mm:node number="<%= teaser_number %>" notfound="skipbody"
><mm:related path="readmore,link" fields="link.url"
   ><mm:field name="link.url" jspvar="exturls_url" vartype="String" write="false"><%
      url = exturls_url;
   %></mm:field
></mm:related><%
if(url.equals("")) { 
   %><mm:related path="readmore,pagina" fields="pagina.number"
      ><mm:field name="pagina.number" jspvar="page_number" vartype="String" write="false"><%
         url = ph.createPaginaUrl(page_number,request.getContextPath());
      %></mm:field
   ></mm:related><%
} %><table border="0" cellpadding="0" cellspacing="0">
<tr><td rowspan="4"><img src="media/spacer.gif" width="20" height="1"></td>
	<td rowspan="4" background="media/dots.gif"><img src="media/spacer.gif" width="1" height="1"></td>
	<td background="media/dots.gif"><img src="media/spacer.gif" width="218" height="1"></td>
	<td rowspan="4" background="media/dots.gif"><img src="media/spacer.gif" width="1" height="1"></td>
</tr>
<tr><td class="white"><img src="media/spacer.gif" width="218" height="5"></td></tr>
<tr><td class="white"><script language="JavaScript1.2">

		/*
		Cross browser Marquee script- Dynamic Drive (www.dynamicdrive.com)
		For full source code, 100's more DHTML scripts, and Terms Of Use, visit http://www.dynamicdrive.com
		Credit MUST stay intact
		*/

		//Specify the marquee's width (in pixels)
		var marqueewidth="218px"
		//Specify the marquee's height
		var marqueeheight="20px"
		//Specify the marquee's marquee speed (larger is faster 1-10)
		var marqueespeed=2
		//Pause marquee onMousever (0=no. 1=yes)?
		var pauseit=1

		//Specify the marquee's content (don't delete <nobr> tag)
		//Keep all content on ONE line, and backslash any single quotations (ie: that\'s great):

		var marqueecontent='<nobr><% 
			if(!url.equals("")) {%><a href="<%= url %>" target="_top"><% } 
				%><span class="pageheader"><mm:field name="titel" /></span><%
			if(!url.equals("")) {%></a><% } %></nobr>'

		////NO NEED TO EDIT BELOW THIS LINE////////////
		marqueespeed=(document.all)? marqueespeed : Math.max(1, marqueespeed-1) //slow speed down by 1 for NS
		var copyspeed=marqueespeed
		var pausespeed=(pauseit==0)? copyspeed: 0
		var iedom=document.all||document.getElementById
		if (iedom)
		document.write('<span id="temp" style="visibility:hidden;position:absolute;top:-100px;left:-9000px">'+marqueecontent+'</span>')
		var actualwidth=''
		var cross_marquee, ns_marquee

		function populate(){
		   if (iedom){
			  cross_marquee=document.getElementById? document.getElementById("iemarquee") : document.all.iemarquee
			  cross_marquee.style.left=parseInt(marqueewidth)+8+"px"
			  cross_marquee.innerHTML=marqueecontent
			  actualwidth=document.all? temp.offsetWidth : document.getElementById("temp").offsetWidth
		   }
		   else if (document.layers){
			  ns_marquee=document.ns_marquee.document.ns_marquee2
			  ns_marquee.left=parseInt(marqueewidth)+8
			  ns_marquee.document.write(marqueecontent)
			  ns_marquee.document.close()
			  actualwidth=ns_marquee.document.width
		   }
		   lefttime=setInterval("scrollmarquee()",20)
		}

		

		function scrollmarquee(){
		   if (iedom){
			  if (parseInt(cross_marquee.style.left)>(actualwidth*(-1)+8))
			  cross_marquee.style.left=parseInt(cross_marquee.style.left)-copyspeed+"px"
		   else
			  cross_marquee.style.left=parseInt(marqueewidth)+8+"px"

		   }
		   else if (document.layers){
			  if (ns_marquee.left>(actualwidth*(-1)+8))
				 ns_marquee.left-=copyspeed
			  else
				 ns_marquee.left=parseInt(marqueewidth)+8
			  }
		   }

		   if (iedom||document.layers){
			  with (document){
				 document.write('<table border="0" cellspacing="0" cellpadding="0"><td>')
				 if (iedom){
				 write('<div style="position:relative;width:'+marqueewidth+';height:'+marqueeheight+';overflow:hidden">')
				 write('<div style="position:absolute;width:'+marqueewidth+';height:'+marqueeheight+'" onMouseover="copyspeed=pausespeed" onMouseout="copyspeed=marqueespeed">')
				 write('<div id="iemarquee" style="position:absolute;left:0px;top:0px"></div>')
				 write('</div></div>')
			  }
			  else if (document.layers){
				 write('<ilayer width='+marqueewidth+' height='+marqueeheight+' name="ns_marquee">')
				 write('<layer name="ns_marquee2" left=0 top=0 onMouseover="copyspeed=pausespeed" onMouseout="copyspeed=marqueespeed"></layer>')
				 write('</ilayer>')
			  }
			  document.write('</td></table>')
		   }
		}
</script>
</td></tr>
<tr><td background="media/dots.gif"><img src="media/spacer.gif" width="218" height="1"></td></tr>
</table></mm:node>