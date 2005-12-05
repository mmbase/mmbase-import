<mm:import id="days" vartype="list">monday,tuesday,wednesday,thursday,friday,saturday,sunday</mm:import>

<mm:stringlist referid="days">
test1 <mm:index/> <mm:time time="$_" format=":FULL.FULL" />
</mm:stringlist>

change the offset <mm:import id="offset"><%=  60 * 60 * 24 * 7 %></mm:import>

<mm:stringlist referid="days">
test2 <mm:index/> <mm:time time="$_" offset="$offset" format=":FULL.FULL" />
</mm:stringlist>

#first do some calculations
#the current day
<mm:time referid="now" time="now" format=":FULL.FULL" precision="days" write="false" vartype="date"/>
#the current week(start of the week)
<mm:time id="this_week" referid="now" precision="weeks" write="false" vartype="date"/>
#the last week (the current week minus one second
<mm:time id="last_week" referid="this_week" offset="-1" precision="weeks" write="false" vartype="date"/>
#the next week (the current week +  offset)
<mm:time id="next_week" referid="this_week" offset="$offset" precision="weeks" write="false" vartype="date"/>
#the week after (the next week + offset , to check if offset is base on current time or the referid
<mm:time id="the_week_after" referid="next_week" offset="$offset" precision="weeks" write="false" vartype="date"/>

#
# 
last week number <mm:time referid="last_week" format="w"/>
this week number <mm:time referid="this_week" format="w"/>
next week number <mm:time referid="next_week" format="w"/>
the week after next week number <mm:time referid="the_week_after" format="w"/>
#
#list of days in this week
<mm:import id="counter" vartype="list">1,2,3,4,5,6,7</mm:import>
<mm:stringlist referid="counter">
<mm:time referid="this_week" offset="$[+$_ * 60 * 60 * 24 ]" format=":FULL.FULL"/>
</mm:stringlist>
