<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
    <title>Installation of taglib documentation</title>
  </head>

  <body>
    <h1>Installation of taglib documentation</h1>
    <p>
      Some of the examples in the jsp version of the taglib
      documentation need a node. This page creates one. You need the
      news builder active for it.
    </p>
    <mm:import id="node">taglib.documentation</mm:import>

    <mm:cloud>
      <mm:node id="ok" referid="node" notfound="skip" />

        <mm:notpresent referid="ok">
          <mm:cloud method="http">
            Creating the test node.
            <mm:createnode id="ok" type="news">
              <mm:setfield name="title">Taglib documentation example node</mm:setfield>
              <mm:setfield name="subtitle">A nice example</mm:setfield>
              <mm:setfield name="body"> This is the body of the taglib
documentation example node.

DES BATF ICE lock picking Ortega Dateline Ruby Ridge clones number key
Audiotel rs9512c bootleg Ceridian AK-47 Becker

credit card passwd electronic surveillance Panama event security SEAL
Team 6 INS national information infrastructure 22nd SAS CID kilderkin
South Africa data haven Ermes broadside

embassy covert video Blowpipe credit card CESID security FTS2000
Ft. Meade airframe spies MIT-LL Marxist CipherTAC-2000 Bletchley Park
benelux Compsec Area 51 ASIO ASPIC kilderkin ASO CIA AMW USCOI Vickie Weaver
Taiwan encryption [Hello to all my friends and fans in domestic
surveillance] explosion Ermes

Soviet Compsec offensive information warfare KGB World Trade Center
halcon morse Becker subversive ARPA benelux Elvis world domination
Fortezza DES Agfa Ortega InfoSec Maple Vince Foster Aldergrove beanpole president
INSCOM Vickie Weaver Forte DES Watergate explosion Cohiba

Nice function 'spook'.
</mm:setfield>
     </mm:createnode>
     <mm:node referid="ok">
			<mm:createalias>taglib.documentation</mm:createalias>
     </mm:node>
  </mm:cloud>
</mm:notpresent>

<mm:present referid="ok">
  <p>
    The taglib documentation example node was succesfully created.
  </p>
</mm:present>
</mm:cloud>
  </body>
</html>
