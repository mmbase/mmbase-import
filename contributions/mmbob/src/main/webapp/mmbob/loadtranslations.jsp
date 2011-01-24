<%--  this is an @ include--%>
<mm:import externid="mlg" id="mlgtest" />
<mm:notpresent referid="mlgtest">
    <mm:context id="mlg">
        <mm:import id="setname" reset="true">mmbob</mm:import>
        <mm:compare referid="forumid" value="" inverse="true">
            <mm:node referid="forumid">
                <mm:import id="lang" reset="true"><mm:field name="language" /></mm:import>
            </mm:node>
        </mm:compare>
        <mm:compare referid="forumid" value="">
            <mm:import id="lang" reset="true">en</mm:import>
        </mm:compare>

        <mm:nodelistfunction set="mlg" name="getKeywordPerLanguage" referids="setname,lang">
            <mm:field name="keyword">
                <mm:import id="$_"><mm:field name="translation" /></mm:import>
            </mm:field>
        </mm:nodelistfunction>
        <mm:write referid="mlg" session="mlg" />
    </mm:context>
</mm:notpresent>
<mm:present referid="mlgtest">
    <mm:import externid="mlg" />
    <mm:context referid="mlg" />
    <mm:import id="setname" reset="true">mmbob</mm:import>
    <mm:compare referid="forumid" value="" inverse="true">
        <mm:node referid="forumid">
            <mm:import id="lang" reset="true"><mm:field name="language" /></mm:import>
        </mm:node>
    </mm:compare>
    <mm:compare referid="forumid" value="">
        <mm:import id="lang" reset="true">en</mm:import>
    </mm:compare>
    <mm:compare referid="mlg.lang" referid2="lang" inverse="true">
        <mm:nodelistfunction set="mlg" name="getKeywordPerLanguage" referids="setname,lang">
            <mm:field name="keyword">
                <mm:import id="mlg.$_" reset="true"><mm:field name="translation" /></mm:import>
            </mm:field>
        </mm:nodelistfunction>
        <mm:import id="mlg.lang" reset="true"><mm:write referid="lang" /></mm:import>
    </mm:compare>
</mm:present>
