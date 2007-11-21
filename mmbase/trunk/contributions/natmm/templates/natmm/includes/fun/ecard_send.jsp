<%@include file="/taglibs.jsp" %>
<%@page import="javax.mail.*,javax.mail.internet.*" %>
<%
   String imgID = request.getParameter("i");
%>
<mm:import jspvar="toname" externid="toname">-</mm:import>
<mm:import jspvar="toemail" externid="toemail">-</mm:import>
<mm:import jspvar="fromname" externid="fromname">-</mm:import>
<mm:import jspvar="fromemail" externid="fromemail">-</mm:import>
<mm:import jspvar="body" externid="body">-</mm:import>
<mm:cloud logon="admin" pwd="<%= (String) com.finalist.mmbase.util.CloudFactory.getAdminUserCredentials().get("password") %>" method="pagelogon" jspvar="cloud">
<%
boolean sendOK = false;
Random rand = new Random();
String[] chars1 = new String[] {"b","c","d","f","g","h","j","k","m","n","p","r","s","t","v","w","x","z","Y","G","2","0","Q"};
String[] chars2 = new String[] {"a","e","i","o","u","y","2","3","4","5","6","7","8","9"};
String account = "";
for (int i=0;i<8;i++){
   int ri1 = rand.nextInt(chars1.length);
   int ri2 = rand.nextInt(chars2.length);
   account += chars1[ri1];
   account += chars2[ri2];
}

String emailNew = toemail.toLowerCase();
emailNew = emailNew.replace('$','.').replace('\'','.').replace('%','.');
String hostName = HttpUtils.getRequestURL(request).toString();
hostName = hostName.substring(0,hostName.lastIndexOf("/"));
            
try {
   String msg_subject = "Er ligt een ecard klaar van een vriend(in)";
   if(NatMMConfig.getCompanyName().equals("Natuurmonumenten")) { 
      msg_subject = "Er ligt een ecard klaar van een natuurvriend(in)";
   }
   String msg_body = "Beste " + toname + ",\n\n";
   msg_body += fromname + " heeft je een " +  NatMMConfig.getCompanyName() + " e-card gestuurd. Klik op de link om je e-card op te halen.\n\n";
   msg_body += hostName+"/ecard.jsp?id="+imgID+"&card="+account+"\n\n";
   if(NatMMConfig.getCompanyName().equals("Natuurmonumenten")) { 
      msg_body += "Op de hoogte blijven van de laatste natuurnieuwtjes? Meld je aan en ontvang maandelijks de Natuurbrief.\nhttp://www.natuurmonumenten.nl/natuurbrief\n\n";
      msg_body += "Natuurmonumenten beschermt de natuur in Nederland. Help mee en steun ons:\nhttp://www.natuurmonumenten.nl/steunons\n\n";
   }
   Properties props = new Properties();
   props.put( "mail.smtp.host", "localhost" );
   Session s = Session.getInstance( props, null );
   MimeMessage message = new MimeMessage( s );
   InternetAddress from = new InternetAddress(NatMMConfig.getFromEmailAddress(), NatMMConfig.getCompanyName(), "iso8859-1" );
   message.setFrom( from );
   InternetAddress to = new InternetAddress( emailNew );
   message.addRecipient( Message.RecipientType.TO, to );
   message.setSubject( msg_subject );
   message.setText( msg_body );
   Transport.send( message );
   sendOK = true; 
   %>
   <strong>Uw <%= NatMMConfig.getCompanyName() %> e-card is verzonden!</strong><br/><br/>
   Verstuurd naar: <strong><%= toemail %></strong><br/>
   Inhoud tekst: <%= body %><br/><br/>
   Nog een e-card verzenden? <a href="ecard.jsp">Klik hier.</a><br/><br/>
   <%    
} catch (Exception e) { 
   sendOK = false;
}  
if(sendOK){%>
   <mm:transaction id="datapost" name="my_trans" commitonclose="true">  
      <mm:createnode type="ecard" id="this_ecard">
         <mm:setfield name="toemail"><%=toemail%></mm:setfield>   
         <mm:setfield name="toname"><%=toname%></mm:setfield>  
         <mm:setfield name="mailkey"><%= account %></mm:setfield>
         <mm:setfield name="fromemail"><%=fromemail%></mm:setfield>  
         <mm:setfield name="fromname"><%=fromname%></mm:setfield> 
         <mm:setfield name="body"><%=body%></mm:setfield>
         <mm:setfield name="estat">1</mm:setfield>
         <mm:setfield name="viewstat">0</mm:setfield>
      </mm:createnode>
      <mm:node number="<%=imgID%>" id="this_img" />
      <mm:createrelation role="related" source="this_ecard" destination="this_img" />
      <mm:remove referid="this_img" />
      <mm:remove referid="this_ecard" />
   </mm:transaction>
<%} else {%>
   
   E-mail naar het adres <strong><%= toemail %></strong> is niet gelukt<br>
   Pas het e-mailadres aan: <a href="javascript:history.go(-1)">terug naar het formulier</a>

<% } %>
</mm:cloud>