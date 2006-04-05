<%! public String filterChars(String text) {
    char [] textChars = text.toCharArray();
    text = "";
    for(int i=0; i<textChars.length; i++) {
      char c = textChars[i];
        if(('a'<=c&&c<='z')||('A'<=c&&c<='Z')||('0'<=c&&c<='9')||(c=='-')||(c=='_')) {
        text += c;
        }
      }
    return text;
} 
%><!-- authenticate website [<%= websiteName %>],  servername [<%= request.getServerName() %>] --><%
// *** code below does not work from within a function ***
String domain = "";
String username = "";

if(isProduction
    &&(websiteName.indexOf(request.getServerName())>-1)
    &&(request.getHeader("User-Agent").indexOf("IE")>-1)) { // *** following code only works in IE ***

    String auth = request.getHeader("Authorization");
    if (auth == null)
    {
      response.setStatus(response.SC_UNAUTHORIZED);
      response.setHeader("WWW-Authenticate", "NTLM");
      response.flushBuffer();
      return;
    }
    if (auth.startsWith("NTLM "))
    {
        byte[] msg = new sun.misc.BASE64Decoder().decodeBuffer(auth.substring(5));
        int off = 0, length, offset;
        if (msg[8] == 1)
        {
            byte z = 0;
            byte[] msg1 = {(byte)'N', (byte)'T', (byte)'L', (byte)'M', (byte)'S', (byte)'S', (byte)'P', 
              z,(byte)2, z, z, z, z, z, z, z,(byte)40, z, z, z, 
              (byte)1, (byte)130, z, z,z, (byte)2, (byte)2,
              (byte)2, z, z, z, z, z, z, z, z, z, z, z, z};
            response.setHeader("WWW-Authenticate", "NTLM " + 
               new sun.misc.BASE64Encoder().encodeBuffer(msg1));
            response.sendError(response.SC_UNAUTHORIZED);
            return;
        }
        else if (msg[8] == 3)
        {
            off = 30;

            length = msg[off+17]*256 + msg[off+16];
            offset = msg[off+19]*256 + msg[off+18];
            String remoteHost = new String(msg, offset, length);

            length = msg[off+1]*256 + msg[off];
            offset = msg[off+3]*256 + msg[off+2];
            domain = new String(msg, offset, length);
            domain = filterChars(domain);
            
            length = msg[off+9]*256 + msg[off+8];
            offset = msg[off+11]*256 + msg[off+10];
            username = new String(msg, offset, length);
            username = filterChars(username);
            
           // out.println("Username:"+username+"<BR>");
           // out.println("RemoteHost:"+remoteHost+"<BR>");
           // out.println("Domain:"+domain+"<BR>");
        }
	  session.setAttribute("username",username);
    }
}
if (username == "") {
	username="IWS";
}

%>
<!-- authenticate found user [<%= username %>] -->
