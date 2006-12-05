<%@page import="java.io.*,java.util.*,org.mmbase.bridge.*" %>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<mm:cloud method="http" rank="basic user" jspvar="cloud">
<mm:log jspvar="log">
<html>
   <head>
   <LINK rel="stylesheet" type="text/css" href="/editors/css/editorstyle.css">
   <title>Natuurmonumenten</title>
   <style>
     table { width: 100%; }
     td { border: solid #000000 1px; padding: 3px; height: auto; vertical-align: top; } 
   </style>
   </head>
   <body style="width:100%;padding:5px;">
   Changes made in this update:<br/>
   1. Added field evenement.adres_verplicht:<br/>  
   ALTER TABLE `v1_evenement` ADD `adres_verplicht` VARCHAR(40);<br/>
   UPDATE `v1_evenement` SET `adres_verplicht` = '0';<br/>
   2. importing images media/images/ngb/XX.gif, where XX is equal provincies.afkorting to the object cloud and relates them to the corresponding province.<br/>
   3. migrating the editwizard<br/>
   <mm:node number="89579">
      <mm:setfield name="nodepath">pagina,contentrel,provincies,pos4rel,natuurgebieden</mm:setfield>
      <mm:setfield name="fields">natuurgebieden.naam,natuurgebieden.bron,pos4rel.pos1,pos4rel.pos2</mm:setfield>
      <mm:setfield name="m_distinct">0</mm:setfield>
   </mm:node>
	4. Migrating all province-posrel-natuurgebieden relations to province-pos4rel-natuurgebieden relations.<br/>
   Processing...<br/>
   <% 
     // String mediaDir = "C:/data/natmm/webapps/ROOT/natmm/media/images/ngb/";
     String mediaDir = "/export/www/natuurmm/jakarta-tomcat/webapps/ROOT/media/images/ngb/";
     NodeList nlProvincies =  cloud.getNodeManager("provincies").getList(null,null,null);
	  int i = 0;
	  while(i<nlProvincies.size()) {
		  Node nProvincie = nlProvincies.getNode(i);
		  String sAfkorting = nProvincie.getStringValue("afkorting");
		  try {
   			File f = new File(mediaDir + sAfkorting.toLowerCase() + ".gif");
   			int fsize = (int)f.length();
   			byte[] thedata = new byte[fsize];
   			FileInputStream instream = new FileInputStream(f);
   			instream.read(thedata);
   	      NodeManager imageItemManager = cloud.getNodeManager("images");
   			Node imgNode = imageItemManager.createNode();
           	imgNode.setValue("handle",thedata);
   	  	   imgNode.commit();
   			Relation thisRelation = nProvincie.createRelation(imgNode, cloud.getRelationManager("posrel"));
   	      thisRelation.commit();
   			instream.close();
   		} catch (Exception e) {
       		out.println("Exception: " + e );
   		} 
 			i++;
   	}
		TreeMap dots = new TreeMap();
      dots.put("Drenthe:01","70:148");
      dots.put("Drenthe:02","162:56");
      dots.put("Drenthe:03","34:158");
      dots.put("Drenthe:04","25:143");
      dots.put("Drenthe:05","190:54");
      dots.put("Drenthe:06","47:167");
      dots.put("Drenthe:07","160:30");
      dots.put("Drenthe:08","190:41");
      dots.put("Drenthe:09","66:130");
      dots.put("Drenthe:10","19:164");
      dots.put("Drenthe:11","111:176");
      dots.put("Drenthe:12","164:43");
      dots.put("Drenthe:13","47:144");
      dots.put("Drenthe:14","88:163");
      dots.put("Drenthe:15","83:176");
      dots.put("Drenthe:16","44:93");
      dots.put("Drenthe:17","142:30");
      dots.put("Drenthe:18","205:66");
      dots.put("Drenthe:19","203:47");
      dots.put("Drenthe:20","177:47");
      dots.put("Drenthe:21","238:90");
      dots.put("Flevoland:01","113:156");
      dots.put("Flevoland:02","100:167");
      dots.put("Flevoland:03","126:147");
      dots.put("Flevoland:04","198:75");
      dots.put("Flevoland:05","181:65");
      dots.put("Flevoland:06","-241:-169");
      dots.put("Friesland:01","205:27");
      dots.put("Friesland:02","85:115");
      dots.put("Friesland:03","98:107");
      dots.put("Friesland:04","224:138");
      dots.put("Friesland:05","55:53");
      dots.put("Friesland:06","210:135");
      dots.put("Friesland:07","90:81");
      dots.put("Friesland:08","148:81");
      dots.put("Friesland:09","108:151");
      dots.put("Friesland:10","105:167");
      dots.put("Friesland:11","208:0");
      dots.put("Friesland:12","138:94");
      dots.put("Friesland:13","115:94");
      dots.put("Friesland:14","201:115");
      dots.put("Friesland:15","102:68");
      dots.put("Friesland:16","90:164");
      dots.put("Gelderland:01","183:119");
      dots.put("Gelderland:02","166:119");
      dots.put("Gelderland:03","151:119");
      dots.put("Gelderland:04","253:158");
      dots.put("Gelderland:05","276:292");
      dots.put("Gelderland:06","238:197");
      dots.put("Gelderland:07","289:240");
      dots.put("Gelderland:08","190:184");
      dots.put("Gelderland:09","222:197");
      dots.put("Gelderland:10","275:253");
      dots.put("Gelderland:11","295:292");
      dots.put("Gelderland:12","289:279");
      dots.put("Gelderland:13","134:171");
      dots.put("Gelderland:14","214:106");
      dots.put("Gelderland:15","298:253");
      dots.put("Gelderland:16","178:145");
      dots.put("Gelderland:17","222:145");
      dots.put("Gelderland:18","219:119");
      dots.put("Gelderland:19","206:119");
      dots.put("Gelderland:20","235:145");
      dots.put("Gelderland:21","215:158");
      dots.put("Gelderland:22","196:106");
      dots.put("Gelderland:23","205:184");
      dots.put("Gelderland:24","151:228");
      dots.put("Gelderland:25","141:145");
      dots.put("Gelderland:26","252:253");
      dots.put("Gelderland:27","312:292");
      dots.put("Gelderland:28","177:132");
      dots.put("Gelderland:29","232:106");
      dots.put("Gelderland:30","189:158");
      dots.put("Gelderland:31","120:56");
      dots.put("Gelderland:32","241:305");
      dots.put("Gelderland:33","261:266");
      dots.put("Gelderland:34","154:132");
      dots.put("Gelderland:35","100:85");
      dots.put("Gelderland:36","111:69");
      dots.put("Gelderland:37","120:171");
      dots.put("Gelderland:38","146:158");
      dots.put("Gelderland:39","155:145");
      dots.put("Gelderland:40","178:171");
      dots.put("Gelderland:41","163:158");
      dots.put("Gelderland:42","257:292");
      dots.put("Gelderland:43","171:184");
      dots.put("Gelderland:44","227:132");
      dots.put("Gelderland:45","153:184");
      dots.put("Gelderland:46","118:43");
      dots.put("Gelderland:47","153:171");
      dots.put("Gelderland:48","194:145");
      dots.put("Gelderland:49","239:253");
      dots.put("Gelderland:50","214:132");
      dots.put("Gelderland:51","122:184");
      dots.put("Gelderland:52","301:305");
      dots.put("Gelderland:53","98:119");
      dots.put("Groningen:01","130:134");
      dots.put("Groningen:02","128:64"); 
      dots.put("Groningen:03","70:46");
      dots.put("Groningen:04","55:78");
      dots.put("Groningen:05","68:111");
      dots.put("Groningen:06","50:28");
      dots.put("Groningen:07","68:92");
      dots.put("Limburg:01","160:105");
      dots.put("Limburg:02","192:238");
      dots.put("Limburg:03","192:273");
      dots.put("Limburg:04","233:203");
      dots.put("Limburg:05","219:171");
      dots.put("Limburg:06","205:233");
      dots.put("Limburg:07","191:183");
      dots.put("Limburg:08","178:227");
      dots.put("Limburg:09","206:262");
      dots.put("Limburg:10","33:181");
      dots.put("Limburg:11","20:181");
      dots.put("Limburg:12","233:240");
      dots.put("Limburg:13","182:118");
      dots.put("Limburg:14","151:250");
      dots.put("Limburg:15","40:168");
      dots.put("Limburg:16","53:168");
      dots.put("Limburg:17","233:223");
      dots.put("Limburg:18","150:195");
      dots.put("Limburg:19","203:79");
      dots.put("Limburg:20","184:79");
      dots.put("Limburg:21","121:79");
      dots.put("Limburg:22","117:92");
      dots.put("Limburg:23","46:181");
      dots.put("Limburg:24","206:249");
      dots.put("Limburg:25","37:0");
      dots.put("Limburg:26","151:263");
      dots.put("Limburg:27","41:13");
      dots.put("Limburg:28","72:194");
      dots.put("Limburg:29","220:245");
      dots.put("Limburg:30","54:0");
      dots.put("Limburg:31","132:242");
      dots.put("Limburg:32","177:192");
      dots.put("Limburg:33","147:105");
      dots.put("Limburg:34","246:219");
      dots.put("Limburg:35","45:155");
      dots.put("Limburg:36","220:211");
      dots.put("Limburg:37","160:92");
      dots.put("Limburg:38","191:105");
      dots.put("Limburg:39","164:185");
      dots.put("Limburg:40","124:66");
      dots.put("Limburg:41","137:92");
      dots.put("Limburg:42","246:205");
      dots.put("Limburg:43","56:13");
      dots.put("Noord-Brabant:01","131:38");
      dots.put("Noord-Brabant:02","106:64");
      dots.put("Noord-Brabant:03","16:38");
      dots.put("Noord-Brabant:04","217:103");
      dots.put("Noord-Brabant:05","133:25");
      dots.put("Noord-Brabant:06","31:103");
      dots.put("Noord-Brabant:07","116:51");
      dots.put("Noord-Brabant:08","170:64");
      dots.put("Noord-Brabant:09","66:64");
      dots.put("Noord-Brabant:10","234:25");
      dots.put("Noord-Brabant:11","129:51");
      dots.put("Noord-Brabant:12","205:116");
      dots.put("Noord-Brabant:13","180:12");
      dots.put("Noord-Brabant:14","233:116");
      dots.put("Noord-Brabant:15","89:64");
      dots.put("Noord-Brabant:16","194:103");
      dots.put("Noord-Brabant:17","75:77");
      dots.put("Noord-Brabant:18","139:64");
      dots.put("Noord-Brabant:19","243:38");
      dots.put("Noord-Brabant:20","5:103");
      dots.put("Noord-Brabant:21","152:64");
      dots.put("Noord-Brabant:22","83:51");
      dots.put("Noord-Brabant:23","55:90");
      dots.put("Noord-Brabant:24","198:129");
      dots.put("Noord-Brabant:25","60:77");
      dots.put("Noord-Brabant:26","171:25");
      dots.put("Noord-Brabant:27","47:64");
      dots.put("Noord-Brabant:28","30:25");
      dots.put("Noord-Brabant:29","2:38");
      dots.put("Noord-Brabant:30","22:51");
      dots.put("Noord-Brabant:31","175:129");
      dots.put("Noord-Brabant:32","241:64");
      dots.put("Noord-Brabant:33","153:25");
      dots.put("Noord-Brabant:34","18:103");
      dots.put("Noord-Brabant:35","68:90");
      dots.put("Noord-Brabant:36","35:51");
      dots.put("Noord-Holland:01","157:158");
      dots.put("Noord-Holland:02","203:145");
      dots.put("Noord-Holland:03","207:158");
      dots.put("Noord-Holland:04","203:197");
      dots.put("Noord-Holland:05","220:219");
      dots.put("Noord-Holland:06","25:116");
      dots.put("Noord-Holland:07","-340:-267");
      dots.put("Noord-Holland:08","48:165");
      dots.put("Noord-Holland:09","61:167");
      dots.put("Noord-Holland:10","35:163");
      dots.put("Noord-Holland:11","140:184");
      dots.put("Noord-Holland:12","186:197");
      dots.put("Noord-Holland:13","20:97");
      dots.put("Noord-Holland:14","203:171");
      dots.put("Noord-Holland:15","140:145");
      dots.put("Noord-Holland:16","43:34");
      dots.put("Noord-Holland:17","150:171");
      dots.put("Noord-Holland:18","74:199");
      dots.put("Noord-Holland:19","163:184");
      dots.put("Noord-Holland:20","173:145");
      dots.put("Noord-Holland:21","47:21");
      dots.put("Noord-Holland:22","154:197");
      dots.put("Noord-Holland:23","31:129");
      dots.put("Noord-Holland:24","160:114");
      dots.put("Noord-Holland:25","33:142");
      dots.put("Noord-Holland:26","190:158");
      dots.put("Noord-Holland:27","206:184");
      dots.put("Noord-Holland:28","69:8");
      dots.put("Noord-Holland:29","190:171");
      dots.put("Noord-Holland:30","140:158");
      dots.put("Noord-Holland:31","190:184");
      dots.put("Noord-Holland:32","145:127");
      dots.put("Noord-Holland:33","35:180");
      dots.put("Noord-Holland:34","3:196");
      dots.put("Noord-Holland:35","30:81");
      dots.put("Overijssel:00","-337:-267");
      dots.put("Overijssel:01","156:113");
      dots.put("Overijssel:02","232:72");
      dots.put("Overijssel:03","130:189");
      dots.put("Overijssel:04","57:129");
      dots.put("Overijssel:05","242:118");
      dots.put("Overijssel:06","35:47");
      dots.put("Overijssel:07","51:116");
      dots.put("Overijssel:08","251:79");
      dots.put("Overijssel:09","100:103");
      dots.put("Overijssel:10","227:43");
      dots.put("Overijssel:11","228:56");
      dots.put("Overijssel:12","227:114");
      dots.put("Overijssel:13","247:66");
      dots.put("Overijssel:14","221:101");
      dots.put("Overijssel:15","249:26");
      dots.put("Overijssel:16","140:116");
      dots.put("Overijssel:17","89:130");
      dots.put("Overijssel:18","26:60");
      dots.put("Overijssel:19","219:72");
      dots.put("Overijssel:20","255:118");
      dots.put("Overijssel:21","59:60");
      dots.put("Overijssel:22","245:49");
      dots.put("Overijssel:23","74:130");
      dots.put("Overijssel:24","69:103");
      dots.put("Overijssel:25","225:20");
      dots.put("Overijssel:26","227:88");
      dots.put("Overijssel:27","94:148");
      dots.put("Overijssel:28","115:171");
      dots.put("Overijssel:29","26:98");
      dots.put("Overijssel:30","145:197");
      dots.put("Overijssel:31","23:73");
      dots.put("Utrecht:01","140:74");
      dots.put("Utrecht:02","35:17");
      dots.put("Utrecht:03","142:55");
      dots.put("Utrecht:04","125:8");
      dots.put("Utrecht:05","55:49");
      dots.put("Utrecht:06","59:10");
      dots.put("Utrecht:07","140:126");
      dots.put("Utrecht:08","63:87");
      dots.put("Utrecht:09","26:87");
      dots.put("Utrecht:10","150:109");
      dots.put("Utrecht:11","62:23");
      dots.put("Utrecht:12","109:58");
      dots.put("Utrecht:13","29:30");
      dots.put("Utrecht:14","122:56");
      dots.put("Utrecht:15","78:67");
      dots.put("Utrecht:16","65:36");
      dots.put("Zeeland:01","177:80");
      dots.put("Zeeland:02","64:83");
      dots.put("Zeeland:03","51:79");
      dots.put("Zeeland:04","77:40");
      dots.put("Zeeland:05","210:40");
      dots.put("Zeeland:06","60:40");
      dots.put("Zeeland:07","52:20");
      dots.put("Zeeland:08","196:80");
      dots.put("Zeeland:09","205:57");
      dots.put("Zeeland:10","55:5");
      dots.put("Zeeland:11","120:74");
      dots.put("Zeeland:12","77:83");
      dots.put("Zeeland:13","86:20");
      dots.put("Zeeland:14","221:88");
      dots.put("Zuid-Holland:01","67:99");
      dots.put("Zuid-Holland:02","56:86");
      dots.put("Zuid-Holland:03","67:142");
      dots.put("Zuid-Holland:04","147:44");
      dots.put("Zuid-Holland:05","80:72");
      dots.put("Zuid-Holland:06","131:60");
      dots.put("Zuid-Holland:08","26:130");
      dots.put("Zuid-Holland:09","80:144");
      dots.put("Zuid-Holland:10","13:125");
      dots.put("Zuid-Holland:11","43:112");
      dots.put("Zuid-Holland:12","58:112");
      dots.put("Zuid-Holland:13","132:47");
      dots.put("Zuid-Holland:14","97:73");
      dots.put("Zuid-Holland:15","52:125");
      dots.put("Zuid-Holland:16","39:140");
      dots.put("Zuid-Holland:17","0:132");
      dots.put("Zuid-Holland:18","67:155");
      dots.put("Zuid-Holland:19","70:86");
      dots.put("Zuid-Holland:20","28:112");
      dots.put("Zuid-Holland:21","-339:-267");
   %>
	<mm:list path="provincies,posrel,natuurgebieden">
		<% String sNatuurgebiedenBron = "";
			String sProvincieName = ""; %>
   	<mm:node element="natuurgebieden" id="natuurgebieden">
			<mm:field name="bron" jspvar="dummy" vartype="String" write="false">
				<% 
            sNatuurgebiedenBron = dummy; 
            log.info("Found natuurgebied: " + sNatuurgebiedenBron);
            %>
			</mm:field>
		</mm:node>
		<mm:node element="provincies" id="provincies">
			<mm:field name="naam" jspvar="dummy" vartype="String" write="false">
				<%
            sProvincieName = dummy; 
            log.info("Found provincie: " + sProvincieName);
            %>
			</mm:field>
         <%--
			<mm:listrelations role="posrel" type="natuurgebieden">
				<mm:deletenode deleterelations="true"/>
			</mm:listrelations>
         --%>
		</mm:node>
		<mm:createrelation role="pos4rel" source="provincies" destination="natuurgebieden">
			<% String sX = "0";
				String sY = "0"; 
				if (dots.containsKey(sProvincieName + ":" + sNatuurgebiedenBron)){
					String sCoords = (String) dots.get(sProvincieName + ":" + sNatuurgebiedenBron);
					int iColonIndex = sCoords.indexOf(":");
					sX = sCoords.substring(0,iColonIndex);
					sY = sCoords.substring(iColonIndex + 1);
				} 
            log.info("Settting coordinates: " + sX + "," + sY);
            %>
			<mm:setfield name="pos1"><%= sX %></mm:setfield>
			<mm:setfield name="pos2"><%= sY %></mm:setfield>
		</mm:createrelation>
		<mm:remove referid="provincies"/>	
		<mm:remove referid="natuurgebieden"/>	
   </mm:list>
   Done.
	</body>
  </html>
</mm:log>
</mm:cloud>
