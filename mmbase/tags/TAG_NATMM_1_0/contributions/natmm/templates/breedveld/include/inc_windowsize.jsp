<%  int windowWidth = 600;
	Cookie[] cookies = request.getCookies();
	if(cookies!=null){
		for(int i=0; i<cookies.length; i++) {
			Cookie cookie = cookies[i];
			if (cookie.getName().equals("windowWidth")){
    	    	try{
					windowWidth = Integer.parseInt(cookie.getValue())-20; 
				}catch(java.lang.NumberFormatException exp){
					windowWidth	= 600;
				}
			}
		}
	}
	session.setAttribute("width", String.valueOf(windowWidth));
%>
<!-- windowWidth in inc_windowsize <%= windowWidth %> -->