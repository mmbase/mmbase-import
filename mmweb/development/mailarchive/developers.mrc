<!-- MHonArc Resource File -->
<!-- This resource file utilizes the day grouping feature of MHonArc
     to format the main index.
  -->

<!--    Specify date sorting.
  -->
<Sort>
<title>Developers mail</title>

<ttitle>Developers mail (by thread)</ttitle>

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
<link rel="stylesheet" type="text/css" href="/css/mmbase-dev.css">
<body>

<BR>
<H1>$IDXTITLE$</H1>
<BR><BR>
</IdxPgBegin>

<!--	Redefine LISTBEGIN since a table will be used for index listing.
  -->
<ListBegin>
<UL>
<LI><H2><A HREF="$TIDXFNAME$">Thread Index</A></H2></LI>
</UL>
$PGLINK(PREV)$$PGLINK(NEXT)$
<HR>
<table border=0>
</ListBegin>

<!--	DAYBEGIN defines the markup to be printed when a new day group
	is started.
  -->
<DayBegin>
<tr><td colspan=4><strong>$MSGLOCALDATE$</strong></td></tr>
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
<td>$MSGLOCALDATE(CUR;%H:%M)$</td>
<td>$SUBJECT$</td>
<td>$FROMNAME$</td>
<td>$NOTE$</td>
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
<link rel="stylesheet" type="text/css" href="/css/mmbase-dev.css">
<body>

<BR>
<H3>$TIDXTITLE$</H3>
<BR><BR>
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
<link rel="stylesheet" type="text/css" href="/css/mmbase-dev.css">
<body>

</MsgPgBegin>

<TopLinks>
$BUTTON(PREV)$$BUTTON(NEXT)$$BUTTON(TPREV)$$BUTTON(TNEXT)$[<A
HREF="$IDXFNAME$#$MSGNUM$">Date Index</A>][<A
HREF="$TIDXFNAME$#$MSGNUM$">Thread Index</A>]
</TopLinks>

<SubjectHeader>
<BR><BR>
<H3>$SUBJECTNA$</H3>
<BR>
</SubjectHeader>

<MsgPgEnd>
<BR><BR>
</body>
</html>
</MsgPgEnd>

