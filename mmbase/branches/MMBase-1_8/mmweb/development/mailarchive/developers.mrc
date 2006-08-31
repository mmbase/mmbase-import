<!-- MHonArc Resource File -->
<!-- This resource file utilizes the day grouping feature of MHonArc
     to format the main index.
  -->

<!--    Specify date sorting.
  -->
<Sort>
<title>
Developers mail
</title>

<ttitle>
Developers mail (by thread)
</ttitle>

<idxsize>
300
</idxsize>

<multipg>

<Reverse>
<tReverse>

<!--	Set USELOCALTIME since local date formats are used when displaying
	dates.
  -->
<UseLocalTime>

<!--    Define message local date format to print day of the week, month,
	month day, and year.  Format used for day group heading.
  -->
<MsgLocalDateFmt>
%d %B %Y 
</MsgLocalDateFmt>

<MSGSEP>
^From \S+\s+\S+\s+\S+\s+\d+\s+\d+:\d+:\d+\s+\d+
</MSGSEP>
<SPAMMODE>
<NODOC>
<HTMLEXT>
html
</HTMLEXT>

<!-- Main Index Page -->

<IdxPgBegin>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML//EN">
<HTML>
<HEAD>
<TITLE>$IDXTITLE$</TITLE>
</HEAD>
<link rel="stylesheet" href="/css/mmbase-dev.css" type="text/css">
<body>

<BR>
<span class="grz">$IDXTITLE$</span>
<BR><BR>
</IdxPgBegin>

<!--	Redefine LISTBEGIN since a table will be used for index listing.
  -->
<ListBegin>
<UL>
<LI><span class="miz"><A HREF="$TIDXFNAME$">Thread Index</A></span></LI>
</UL>
<span class="miz">
$PGLINK(PREV)$$PGLINK(NEXT)$
</span>
<HR>
<table border=0>
</ListBegin>

<!--	DAYBEGIN defines the markup to be printed when a new day group
	is started.
  -->
<DayBegin>
<tr><td colspan=4><span class="miz"><strong>$MSGLOCALDATE$</strong></span></td></tr>
</DayBegin>

<!--	DAYBEND defines the markup to be printed when a day group
	ends.  No markup is needed in this case, so we leave it blank.
  -->
<DayEnd>

</DayEnd>

<!--	Define LITEMPLATE to display the time of day the message was
	sent, message subject, author, and any annotation for the
	message.
  -->
<LiTemplate>
<tr valign=top>
<td><span class="miz">$MSGLOCALDATE(CUR;%H:%M)$</span></td>
<td><span class="miz">$SUBJECT$</span></td>
<td><span class="miz">$FROMNAME$</span></td>
<td><span class="miz">$NOTE$</span></td>
</tr>
</LiTemplate>

<!--	Define LISTEND to close table
  -->
<ListEnd>
</table>
</ListEnd>

<IdxPgEnd>
<BR><BR>
</body>
</html>
</IdxPgEnd>


<!-- Thread index page -->

<TIdxPgBegin>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML//EN">
<HTML>
<HEAD>
<TITLE>$TIDXTITLE$</TITLE>
</HEAD>
<link rel="stylesheet" href="/css/mmbase-dev.css" type="text/css">
<body>

<BR>
<span class="grz">$TIDXTITLE$</span>
<BR><BR>
<span class="miz">
</TIdxPgBegin>

<TIdxPgEnd>
<BR><BR>
</body>
</html>
</TIdxPgEnd>

<!-- Message page -->

<MsgPgBegin>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML//EN">
<HTML>
<HEAD>
<TITLE>$SUBJECTNA:72$</TITLE>
</HEAD>
<link rel="stylesheet" href="/css/mmbase-dev.css" type="text/css">
<body>

</MsgPgBegin>

<TopLinks>
<span class="miz">
$BUTTON(PREV)$$BUTTON(NEXT)$$BUTTON(TPREV)$$BUTTON(TNEXT)$[<A
HREF="$IDXFNAME$#$MSGNUM$">Date Index</A>][<A
HREF="$TIDXFNAME$#$MSGNUM$">Thread Index</A>]
</span>
</TopLinks>

<SubjectHeader>
<BR><BR>
<span class="grz">$SUBJECTNA$</span>
<BR>
<span class="miz">
</SubjectHeader>

<MsgPgEnd>
<BR><BR>
</body>
</html>
</MsgPgEnd>

