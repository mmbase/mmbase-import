<% 
    long lastact = Long.parseLong((String)o[5]);
    long currtime = System.currentTimeMillis()/1000;
    long twomonthssec = 2*30*24*60*60;
    if ( lastact > 1 ) {
        if ( !( (currtime - lastact > twomonthssec) && (lastSent - lastact < twomonthssec)) ) {
            it.remove();
        }
    }
    else {
        it.remove();
    }
%>