<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@include file="/taglibs.jsp" %>

<html>
<head>
<title>bestelformulier plotopdrachten</title>

<SCRIPT LANGUAGE="JavaScript">
<!--
arr_NatGeb = new Array
(
	new Array
	(
		new Array("Harger- en Pettemerpolder", 1),
		new Array("Loterijlanden", 2),
		new Array("Nijenburg", 3),
		new Array("Weidse Polder", 4),
		new Array("etc.......", 5)
	),
	new Array
	(
		new Array("Kadelanden", 1),
		new Array("Nieuwkoopse plassen", 2),
		new Array("etc.......", 3)		
	),
	new Array
	(
		new Array("Beekbergerwoud", 1),
		new Array("Hoeve Delle", 2),
		new Array("Loenense Hooilanden", 3),
		new Array("etc.......", 4)
	),
	new Array
	(
		new Array("Ankeveense plassen", 1),
		new Array("Loosdrechtse plassen", 2),
		new Array("Tienhovense plassen", 3),
		new Array("etc.......", 4)
	),
	new Array
	(
		new Array("Chaamse Beek", 1),
		new Array("Markdal", 2),
		new Array("Oosterheide", 3),
		new Array("etc.......", 4)
	),
		
	new Array
	(
		new Array("Genhoes", 1),
		new Array("Geuldal", 2),
		new Array("Gulpdal", 3),
		new Array("Sint-Pietersberg", 4),
		new Array("etc.......", 5)
	)
);


arr_Areaal = new Array
(
	new Array
	(
		new Array("Noordenveld", 1),
		new Array("Waddengebied", 2),
		new Array("Zuid-Drenthe", 3),
		new Array("de Wieden", 4),
		new Array("Salland", 5),
		new Array("Twente", 6),
		new Array("etc.....", 7)
	),
	new Array
	(
		new Array("Groningen/Friesland/Drenthe", 1),
		new Array("Overijssel en Flevoland", 2),
		new Array("Gelderland", 3),
		new Array("Noord-Holland en Utrecht", 4),
		new Array("Zuid-Holland en Zeeland", 5),
		new Array("Noord-Brabant en Limburg", 6)
	),
	new Array
	(
		new Array("Groningen", 1),
		new Array("Friesland", 2),
		new Array("Drenthe", 3),
		new Array("Overijssel", 4),
		new Array("Flevoland", 5),
		new Array("Gelderland", 6),
		new Array("Utrecht", 7),
		new Array("Noord-Holland", 8),
		new Array("Zuid-Holland", 9),
		new Array("Zeeland", 10),
		new Array("Noord-Brabant", 11)
	)

);


function jsc_VulSelectUitArray(selectCtrl, itemArray)
{
	var i, j;
	// leeg de tweede lijst
	for (i = selectCtrl.options.length; i >= 0; i--)
		{
			selectCtrl.options[i] = null; 
		}
		
	if (itemArray != null)
	{
		j = 0;
	}
	else
	{	
		j = 0;}
		if (itemArray != null)
		{
		// nieuwe items toevoegen
		for (i = 0; i < itemArray.length; i++)
		{
			selectCtrl.options[j] = new Option(itemArray[i][0]);
			if (itemArray[i][1] != null)
				{
					selectCtrl.options[j].value = itemArray[i][1]; 
				}
			j++;
		}
	// eerste item selecteren voor tweede lijst, is nu uitgeschakeld
	//selectCtrl.options[0].selected = true;
	}
}

function jsc_GeefInfo(id_DIV)
//DIV zichtbaar -> maak onzichtbaar, DIV onzichtbaar -> maak zichtbaar
{
	if (id_DIV.style.display=="none"){id_DIV.style.display=""}
	else{id_DIV.style.display="none"}
}

function small_window(NaamPagina) {
var newWindow;
var props = 'scrollBars=no,resizable=no,toolbar=no,status=0,minimize=no,statusbar=0,menubar=no,directories=no,width=screen.availWidth,height=screen.availHeight, top='+(20)+',left='+(20);

newWindow = window.open(NaamPagina, "Add_from_Src_to_Dest", props);
newWindow.focus();
}


function jsc_optie0()
{
document.forms[0].sel_Kaart.length=0;
document.forms[0].sel_Kaart[0] =new Option("deze lijst afh. van bovenstaande selectie", "0", true, false);
document.forms[0].sel_Kaart[1] =new Option("Aankoopgebiedenkaart", "Aankoopgebiedenkaart", true, false);
document.forms[0].sel_Kaart[2] =new Option("Natuurtype Huidig", "Natuurtype Huidig", true, false);
document.forms[0].sel_Kaart[3] =new Option("Natuurtype Gewenst", "Natuurtype Gewenst", true, false);
document.forms[0].sel_Kaart[4] =new Option("Kadastrale kaart", "Kadastrale kaart", true, false);
document.forms[0].sel_Kaart[5] =new Option("Topografie kleur", "Topografie kleur", true, false);
document.forms[0].sel_Kaart[6] =new Option("Vak en Afdelingen kaart", "Vak en Afdelingen kaart", true, false);
document.forms[0].sel_Kaart[7] =new Option("etc......", "0", true, false);
}

function jsc_optie1()
{
document.forms[0].sel_Kaart.length=0;
document.forms[0].sel_Kaart[0] =new Option("deze lijst afh. van bovenstaande selectie", "0", true, false);
document.forms[0].sel_Kaart[1] =new Option("Aankoopgebiedenkaart", "0", true, false);
document.forms[0].sel_Kaart[2] =new Option("Top 250 met EHS", "0", true, false);
document.forms[0].sel_Kaart[3] =new Option("Natuurgebieden kaart", "0", true, false);
document.forms[0].sel_Kaart[4] =new Option("Invloedsferenkaart", "0", true, false);
document.forms[0].sel_Kaart[5] =new Option("etc......", "0", true, false);
}

function jsc_optie2()
{
document.forms[0].sel_Kaart.length=0;
document.forms[0].sel_Kaart[0] =new Option("deze lijst afh. van bovenstaande selectie", "0", true, false);
document.forms[0].sel_Kaart[1] =new Option("Top 250 met EHS", "0", true, false);
document.forms[0].sel_Kaart[2] =new Option("Natuurgebieden kaart", "0", true, false);
document.forms[0].sel_Kaart[3] =new Option("Invloedsferenkaart", "0", true, false);
document.forms[0].sel_Kaart[4] =new Option("etc......", "0", true, false);
}

function jsc_optie3()
{
document.forms[0].sel_Kaart.length=0;
document.forms[0].sel_Kaart[0] =new Option("deze lijst afh. van bovenstaande selectie", "0", true, false);
document.forms[0].sel_Kaart[1] =new Option("Aankoopgebiedenkaart", "0", true, false);
document.forms[0].sel_Kaart[2] =new Option("Natuurtype Huidig", "0", true, false);
document.forms[0].sel_Kaart[3] =new Option("Natuurtype Gewenst", "0", true, false);
document.forms[0].sel_Kaart[4] =new Option("etc......", "0", true, false);
}

-->
</script>

<style type="text/css">
<!--

DIV.Pagina
{
	position: absolute;
	z-index:3;
}

DIV.Info
{
	position: relative;
	Color: #CC0033;
	cursor: hand;
	width: 29px;
	height: 24px;
	background-image: url(../../media/vastgoed/Info.png);
}

DIV.Schermuitleg 
{
	position: relative;
	height: auto;
	width: 470px;
	margin-top: .6em;
	margin-right: 3em;
	margin-left: 0;
	margin-bottom: .6em;
	padding-top: .75em;
	padding-right: 6px;
	padding-left: .75em;
	padding-bottom: .75em;
}


-->
</style>
</head>

<body onload="jsc_optie0();">
<html:form action="/nmintra/includes/vastgoed/KaartenAction" method="GET">

	<table>
		<tr>
			<td width="450">
				Selecteer het(de) gebied(en) of geef de coördinaten:
			</td>
			<td align="right">	
				<a href="#nowhere">
					<img src="../../media/vastgoed/Info.png" width="29" height="24" border=0 
						alt="Uitleg over dit invoer deel van het
formulier.">

				</a>
			</td>
		</tr> 
	</table>	


	<table width ="500"  bgcolor="#CCCC00" border="0" cellspacing="0">
		<tr>
			<td width="20">
				<html:radio property="rad_Gebied" value="natuurgebied" onclick="jsc_optie0();"/>
			</td>
			<td width="220">natuurgebied(en):</td>

			<td>&nbsp;</td>
		</tr>
		<tr>
			<td rowspan="2" height="110">&nbsp;</td>
			<td></td>
			<td rowspan="2" width="249" valign="top">
				<select NAME="sel_NatGeb" style="width:100%;" size="6" Multiple>
            	</select>
			</td>

		</tr>
		<tr>
			<td height="70" valign="top">
				<html:select style="width:100%;" property="sel_Beheereenheden" onclick="jsc_VulSelectUitArray(this.form.sel_NatGeb, arr_NatGeb[this.selectedIndex]);">
					<html:option value="Kennemerland">Kennemerland</html:option>
					<html:option value="Nieuwkoop">Nieuwkoop</html:option>
					<html:option value="Oost-Veluwe">Oost-Veluwe</html:option>
					<html:option value="Vechtplassen">Vechtplassen</html:option>
					<html:option value="West-Brabant">West-Brabant</html:option>
					<html:option value="Zuid-Limburg">Zuid-Limburg</html:option>
				</html:select>
			</td>
		</tr>
		<tr height="1" bgcolor="#FFFFFF">

			<td></td>
			<td></td>
			<td></td>
		</tr>
	</table>
	
	
	<table width ="500"  bgcolor="#CCCC99" border="0" cellspacing="0">
		<tr>
			<td width="20">
				<html:radio property="rad_Gebied"value="regio" onclick="jsc_optie1();"/>

			</td>
			<td width="220">Eenheid / Regio / Provincie:</td>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td rowspan="2" height="110">&nbsp;</td>
			<td></td>
			<td rowspan="2" width="249" valign="top">

				<select NAME="sel_Areaal" style="width:100%;" size="6">
            	</select>
			</td>
		</tr>
		<tr>
			<td height="70" valign="top">
				<html:select style="width:100%;" property="sel_gebieden" onclick="jsc_VulSelectUitArray(this.form.sel_Areaal, arr_Areaal[this.selectedIndex]);">
					<html:option value="Eenheid">Eenheid</html:option>
					<html:option value="Regio">Regio</html:option>
					<html:option value="Provincie">Provincie</html:option>
				</html:select>
			</td>
		</tr>
		<tr height="1" bgcolor="#FFFFFF">
			<td></td>
			<td></td>

			<td></td>
		</tr>
	</table>


	<table width ="500"  bgcolor="#CCCC00" border="0" cellspacing="0">
		<tr>
			<td width="20" height="20" valign="top">
				<html:radio property="rad_Gebied" value="nederland" onclick="jsc_optie2();"/>
			</td>

			<td width="220" valign="top">Nederland:</td>
		</tr>
		<tr height="1" bgcolor="#FFFFFF">
			<td></td>
			<td></td>
			<td></td>
		</tr>
	</table>

	<table width="500" bgcolor="#CCCC99" border="0" cellspacing="0">	
		<tr>
			<td width="20">
				<html:radio property="rad_Gebied" value="coordinaten" onclick="jsc_optie3();"/>
			</td>
			<td colspan="4">coördinaten:&nbsp;</td>
			<td>&nbsp;</td>
		</tr>

		<tr>
			<td>&nbsp;</td>
			<td width="150" align="right">linksonder X:&nbsp;</td>
			<td width="50">
              <html:text style="width:100%;" property="linksX" size="7"/>
			</td>
			<td width="50" align="right">Y:&nbsp;</td>
			<td width="50">
				<html:text style="width:100%;" property="linksY" size="7"/>
			</td>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td align="right">rechtsboven X:&nbsp;</td>
			<td>
             <html:text style="width:100%;" property="rechtsX" size="7"/>
			</td>
			<td align="right">Y:&nbsp;</td>
			<td>
			 <html:text style="width:100%;" property="rechtsY" size="7"/>    
			</td>
			<td>&nbsp;</td>
		</tr>
	</table>

	<br>

	
	<table>
		<tr>
			<td width="450">
				Selecteer de gewenste kaart(en):
			</td>
			<td align="right">	
				<a href="#nowhere">
					<img src="../../media/vastgoed/Info.png" width="29" height="24" border=0 
						alt="Uitleg over dit invoer deel van het
formulier.">

				</a>
			</td>
		</tr> 
	</table>		
	

	<table width="500"  bgcolor="#CCCC00" border="0" cellspacing="0">
		<tr>
			<td width="96" align="left">kaarten:&nbsp;<br>klik hier voor vergroting en informatie</td>
			<td width="139">
				<img style="cursor:pointer" src="../../media/vastgoed/Nicolao%20Visscher.jpg" width="132" height="107" border="0" alt="Klik hier voor vergroting en meer gegevens van deze kaart" onClick="javascript:small_window('kaart_popup.jsp');">

			</td>
			<td width="249">
				<html:select style="width:100%;" property="sel_Kaart" size="6" multiple="multiple">
				</html:select>
			</td>
		</tr>
	</table>
	<br>
	
	
	<table>

		<tr>
			<td width="450">
				Geef de schaal of het formaat  en het aantal:
			</td>
			<td align="right">	
				<a href="#nowhere">
					<img src="../../media/vastgoed/Info.png" width="29" height="24" border=0 
						alt="Uitleg over dit invoer deel van het
formulier.">
				</a>
			</td>
		</tr> 
	</table>	

	
	<table width="500" border="0" cellspacing="0">

		<tr bgcolor="#CCCC99">
	  		<td width="20">
        		<html:radio property="rad_Schaal" value="schaal"/>
      		</td>
			<td width="100" align="right">schaal:&nbsp;</td>
      		<td width="100">
				<html:select style="width:100%;" property="schaal">
					<html:option value="1:500">1:500</html:option>
					<html:option value="1:1000">1:1000</html:option>
					<html:option  value="1:5000">1:5000</html:option>
				</html:select>
	  		</td>
      		<td width="200">&nbsp;</td>
      		<td>&nbsp;</td>
		</tr>
		<tr height="1" bgcolor="#FFFFFF">

			<td></td>
			<td></td>
			<td></td>
			<td></td>
			<td></td>
		</tr>
    	<tr bgcolor="#CCCC00">
			<td><html:radio property="rad_Schaal" value="formaat"/></td>
			<td align="right">formaat:&nbsp;</td>

      		<td>
				<html:select style="width:100%;" property="formaat">
					<html:option value="A4">A4</html:option>
					<html:option value="A3">A3</html:option>
					<html:option value="A2">A2</html:option>
					<html:option value="A1">A1</html:option>
					<html:option value="A0">A0</html:option>

      			</html:select>
			</td>
			<td align="right">aantal:&nbsp;</td>
      		<td><html:text property="aantal" size="4"/></td>
    	</tr>
		<tr height="10" bgcolor="#FFFFFF">
			<td></td>
			<td></td>

			<td></td>
			<td></td>
			<td></td>
		</tr>
        <tr bgcolor="#CCCC99">
            <td>
				<html:radio property="rad_Gevouwen" value="gevouwen"/>
			</td>
			<td align="left">&nbsp;gevouwen&nbsp;</td>

			<td></td>
			<td></td>
			<td></td>
        </tr>
		<tr height="1" bgcolor="#FFFFFF">
			<td></td>
			<td></td>
			<td></td>
			<td></td>

			<td></td>
		</tr>
        <tr bgcolor="#CCCC00">
            <td><html:radio property="rad_Gevouwen" value="opgerold"/></td>
			<td align="left">&nbsp;opgerold&nbsp;</td>
			<td></td>
			<td></td>
			<td></td>

        </tr>
  </table>
	
	<br>

	<table>
		<tr>
			<td width="450">
				Opmerkingen:
			</td>
			<td align="right">	
				<a href="#nowhere">

					<img src="../../media/vastgoed/Info.png" align="right" width="29" height="24" border=0 
						alt="Uitleg over dit invoer deel van het
formulier.">
				</a>
			</td>
		</tr> 
	</table>	

	<table width="500" bgcolor="#CCCC66" border="0" cellspacing="0">
		<tr height="5">
			<td width="5"></td>
			<td width="440"></td>
			<td width="50"></td>

			<td width="5"></td>
		</tr>
		<tr>
			<td></td>
			<td colspan="2">
        		<textarea style="width:486px;" name="textfield" rows="5"></textarea>
      		</td>
			<td></td>

 		</tr>
 		<tr height="5">
			<td></td>
			<td></td>
			<td></td>
			<td></td>
		</tr>
		<tr height="50">
	  		<td></td>

			<td>toevoegen aan mijn bestelling:&nbsp;</td>
			<td align="right">
			<input type="image" src="../../media/vastgoed/wwagen.jpg" name="send"/>
			</td>
			<td></td>
		</tr>
		<tr height="5">
			<td></td>
			<td>
		
		<html:link 
        page="/nmintra/includes/vastgoed/KaartenAction.eb?shopping_cart">
        terug...
</html:link>

		</td>
			<td></td>

			<td></td>
		</tr>
	</table>
 
<input type="hidden" name="number" value="<%=request.getParameter("number")%>"/>
 
</html:form>

</body>
</html>
