<mm:field name="begindate" jspvar="begindate" vartype="String" write="false"
	><mm:field name="enddate" jspvar="enddate" vartype="String" write="false"><% 
	      long td = Integer.parseInt(begindate); td = 1000 * td;
	      Date dd = new Date(td); 
			cal.setTime(dd);
	      int day_of_month = cal.get(Calendar.DAY_OF_MONTH);
	      int month = cal.get(Calendar.MONTH);
	      int year = cal.get(Calendar.YEAR);
		%><%= day_of_month %> <%= months_lcase[month] %> <%= year %><%
			td = Integer.parseInt(enddate); td = 1000 * td;
	      dd = new Date(td); cal.setTime(dd);
	      day_of_month = cal.get(Calendar.DAY_OF_MONTH);
	      month = cal.get(Calendar.MONTH);
	      year = cal.get(Calendar.YEAR);
		%> - <%= day_of_month %> <%= months_lcase[month] %> <%= year 
	%></mm:field
></mm:field>