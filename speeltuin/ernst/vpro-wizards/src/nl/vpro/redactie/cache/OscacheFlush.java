package nl.vpro.redactie.cache;

import java.io.File;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import nl.vpro.mmbase.util.AuthorizedCloud;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logging;
import org.springframework.web.bind.*;

import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.web.ServletCacheAdministrator;

/**
 * OscacheFlush bepaald of een aanpassing in de redactieomgeving moet leiden tot
 * een flush in oscache.
 * Deze class wordt gebruikt door de DrieVoorTwaalfCacheHandlerInterceptor
 *
 * @author Rob Vermeulen (VPRO)
 */
public class OscacheFlush {
    private static org.mmbase.util.logging.Logger log = Logging.getLoggerInstance(OscacheFlush.class);


    public static void flushRelation(HttpServletRequest request,
            String relationNumber) {
        flushRelation(request,
           AuthorizedCloud.getAuthorizedCloud().getRelation(relationNumber));

    }
    /**
     * Flush relation.
     *
     * @param request with page uri
     * @param relation to flush
     */
    public static void flushRelation(HttpServletRequest request,
            Relation relation) {
        Cache cache = getCache(request);

        if(relation.getSource().getNumber()>0) {
            log.info("relation(" + relation.getNumber()
                + ") change: flushing source("
                + relation.getSource().getNumber() + ")");

            flushGroup(cache, "" + relation.getSource().getNumber());
            // flushEntries(cache, request, ""+ relation.getSource().getNumber());
        }

        if(relation.getDestination().getNumber()>0) {
            log.info("relation(" + relation.getNumber()
                + ") change: flushing destination("
                + relation.getDestination().getNumber() + ")");
            flushGroup(cache, "" + relation.getDestination().getNumber());
            // flushEntries(cache, request, ""+
            // relation.getDestination().getNumber());
        }
    }

    public static void flushNodeNumber(HttpServletRequest request, String nodenr) {
        Cache cache = getCache(request);
        flushGroup(cache, nodenr);
    }

    /**
     * Als een object vanuit de wizardcontroller wordt bewaard wordt deze method
     * aangeroepen. Gekeken wordt of het om een nieuwe danwel een al bestaand
     * object gaat. De benodgide paginas worden geflushed.
     *
     * @param request with request.URI
     */
    static void flush(HttpServletRequest request) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            log.error("Exception: " + e);
        }

        String nodenr = request.getParameter("nodenr");
        Cache cache = getCache(request);

        try {

            String flush = ServletRequestUtils.getStringParameter(request, "flush");
            String flushname = ServletRequestUtils.getStringParameter(request,
                    "flushname");
            if (flush != null) {

                log.info("flushing page(" + request.getRequestURI() + "), "
                        + flushname + "(" + nodenr + ")");
            }

        } catch (Exception e) {
            log.error("Exception: " + e);
        }

        if (!needsFlush(request)) {
            log.debug("No flushing.");
            return;
        }

        if (!isExistingNode(nodenr)) {
            log.debug("object is new.");
            nodenr = (String) request.getAttribute("newObject");
        }

        log.info("flushing node '" + nodenr + "'");
        if (isExistingNode(nodenr)) {
            flushGroup(cache, nodenr);
        }
        flushEntries(cache, request, nodenr);
    }

    /**
     * Als in 'list paginas' van de wizards objecten worden losgekoppeld dan
     * moeten de juiste paginas geflushed worden. b.v. Nieuwsartikel wordt
     * losgekoppeld van magazine.
     */
    public static void deleteNode(HttpServletRequest request) {
        String nodenr = null;
        try {
            nodenr = ServletRequestUtils.getRequiredStringParameter(request,
                    "flushnode");
        } catch (ServletRequestBindingException srbe) {
            log.error("Cannot find flushnode, nodenr=" + nodenr);
        }
        log.fatal("delete: flushing deleted node " + nodenr);
        Cache cache = getCache(request);
        flushGroup(cache, nodenr);
        flushEntries(cache, request, nodenr);
    }

    /**
     * Als is 'list paginas' van de wizards objecten worden gekoppeld dan moeten
     * de juiste paginas geflushed worden. b.v. Nieuwsartikel wordt gekoppeld
     * aan magazine.
     */
    public static void addNode(HttpServletRequest request) {
        String nodenr = null;
        try {
            nodenr = ServletRequestUtils.getRequiredStringParameter(request,
                    "flushnode");
        } catch (ServletRequestBindingException sqbe) {
            log.error("Cannot find flushnode, nodenr=" + nodenr);
        }
        log.info("flushing added node " + nodenr);
        Cache cache = getCache(request);
        flushEntries(cache, request, nodenr);
    }

    private static void flushGroup(Cache cache, String nodenr) {
        Cloud cloud = AuthorizedCloud.getAuthorizedCloud();
        String buildername = null;

        try {
            Node node = cloud.getNode(nodenr);
            buildername = node.getNodeManager().getName();
        } catch (NotFoundException nfe) {
            log.error("Node " + nodenr + " does not exists." + nfe);
            return;
        }
        log.info("flushing group " + buildername + File.separator + nodenr);
        cache.flushGroup(buildername + File.separator + nodenr);
        cache.flushEntry(buildername + File.separator + nodenr);

        // maps zijn dan ook natuurlijk eigenlijk weer programma's natuurlijk
        // dan wel weer ineens
        if ("maps".equals(buildername)) {
            log.info("flushing group programs" + File.separator + nodenr);
            cache.flushGroup("programmas" + File.separator + nodenr);
            cache.flushEntry("programmas" + File.separator + nodenr);
        }

    }

    /**
     * Bepaal welke paginas er geflushed moeten worden. Afhankelijk van de
     * flushname, die in de wizards wordt gezet, worden er verschillende paginas
     * geflushed.
     *
     * @param nodenr
     *            Het object dat nieuws, aangepast, losgekoppeld, vastgekoppeld
     *            wordt.
     */
    private static void flushEntries(Cache cache, HttpServletRequest request,
            String nodenr) {
        String flushname = "";
        try {
            flushname = ServletRequestUtils.getRequiredStringParameter(request,
                    "flushname");
            log.debug("flushname = " + flushname);

            if (flushname.equals("rebelnews")) {
                flushRebelbass(cache, nodenr);
            } else if (flushname.equals("nieuwsbericht")) {
                flushNews(cache, nodenr);
            } else if (flushname.equals("festival")) {
                flushFestivals(cache, nodenr);
            } else if (flushname.equals("artiest")) {
                flushArtists(cache, nodenr);
            } else if (flushname.equals("dossier")) {
                flushDossiers(cache, nodenr);
            } else if (flushname.equals("luisterpaal")) {
                flushLuisterpaal(cache);
            } else if (flushname.equals("aflevering")) {
                flushAflevering(cache, nodenr);
            } else if (flushname.equals("seizoen")) {
                flushSeizoen(cache, nodenr);
            } else if (flushname.equals("programma")) {
                flushProgramma(cache);
            } else if (flushname.equals("tracks")) {
                flushTrack(cache);
            } else if (flushname.equals("teasers")) {
                flushTeasers(cache, nodenr);
            } else if (flushname.equals("messages")) {
                flushMessages(cache, nodenr);
            } else {
                log.info("No need to flush " + flushname+"/"+nodenr);
            }
        } catch (ServletRequestBindingException srbe) {
            log.error("Cannot flush entries, flushname=" + flushname);
        }
    }

    private static void flushMessages(Cache cache, String nodenr) {
        log.debug("flushing messages");
        cache.flushGroup("/messageService/findCurrentSystemNotification");
    }
    /**
     * Is een flush gewenst? Als er geen flush="true" in de wizard staat zal er
     * niet geflushed worden.
     */
    private static boolean needsFlush(HttpServletRequest request) {
        return ServletRequestUtils.getBooleanParameter(request, "flush", false);
    }

    private static  boolean isExistingNode(String nodenr) {
        return nodenr != null && !nodenr.equals("");
    }

    public static void flushTeasers(Cache cache, String nodenr) {
        log.debug("flushing teasers entries");
        cache.flushEntry("/frontpage");
        cache.flushEntry("/index.jsp");
        cache.flushEntry("/kijkenluister/overzicht/");
        cache.flushEntry("/teasers/" + nodenr);
    }

    public static void flushNews(Cache cache, String nodenr) {
        log.debug("flushing nieuwsbericht entries");
        cache.flushEntry("/artikelen/");
        cache.flushEntry("/artikelen/artikel/" + nodenr);
        cache.flushEntry("/components/artikelrelated");
        cache.flushEntry("/components/nieuwsenachtergrondenrelatedtop");
        cache.flushEntry("/components/nieuwsenachtergrondenrelatedbottom");
        cache.flushEntry("/components/nieuwsenachtergrondenrelated");
        cache.flushEntry("/components/roeleerartikelen");
        cache.flushEntry("/feeds/artikelen");
    }

    //
    public static void flushTrack(Cache cache) {
        log.debug("flushing track entries");
        cache.flushEntry("/kijkenluister/overzicht/");
        cache.flushEntry("/luisterpaal/");
        cache.flushEntry("/songs/");
        cache.flushEntry("/concerten/");
        cache.flushEntry("/djsets/");
        cache.flushEntry("/exclusief/");
        cache.flushEntry("/frontpage");
        cache.flushEntry("/index.jsp");
        cache.flushEntry("/programmas/nieuweafleveringen/");
        cache.flushGroup("/showService/findAllEpisodes");
        cache.flushGroup("/showService/findPopularEpisodes");
    }

    public static void flushFestivals(Cache cache, String nodenr) {
        log.debug("flushing festival entries");
        cache.flushEntry("components/festivalsrelated");
        cache.flushEntry("/festivals/");

        int year = getRelatedYear(nodenr);
        if (year != -1) {
            log.debug("flushing /festivals/" + year);
            cache.flushEntry("/festivals/" + year);
        }
    }

    public static void flushProgramma(Cache cache) {
        log.info("flushing programma entries");
        cache.flushEntry("/programmas/nieuweafleveringen/");
        cache.flushGroup("/showService/findPopularEpisodes");
        cache.flushGroup("/showService/findAllEpisodes");
        cache.flushEntry("/components/episodesrelated");
        cache.flushEntry("/components/showsrelated");
    }

    public static void flushSeizoen(Cache cache, String nodenr) {
        log.debug("flushing seizoen entries");

        cache.flushEntry("/programmas/nieuweafleveringen/");
        cache.flushGroup("/showService/findPopularEpisodes");
        cache.flushGroup("/showService/findAllEpisodes");
        cache.flushEntry("/components/episodesrelated");
        cache.flushEntry("/components/showsrelated");

        // dit *kan* ook de informatie bevatten bij de map, want
        // het laatste programma levert automatisch het huidige informatie

        // voorbeeld:
        //  - http://3voor12.vpro.nl/programmas/29794118 (map met laatste programma)
        // de tekst bij deze map is de tekst van het laatste programma:
        //  - http://edit.vpro.nl/preditor/nodeView.jsp?nodeNumber=30459094

        // dus komt er een flush binnen voor programma, *kan* het zijn dat de
        // map ook geflusht moet worden

        NodeList nl = AuthorizedCloud.getAuthorizedCloud().getNode(nodenr).getRelatedNodes("maps");
        if(nl != null && nl.size()>0) {
            Node map = (Node)nl.get(0);
            cache.flushEntry("programmas" + File.separator + map.getNumber());
        } else {
            log.info("geen map gevonden aan programma "+nodenr);
        }
    }

    public static void flushAflevering(Cache cache, String nodenr) {
        log.info("flushing aflevering entries");

        cache.flushEntry("/components/episodesrelated");
        cache.flushEntry("/components/showsrelated");
        cache.flushEntry("/programmas/nieuweafleveringen/");
        cache.flushGroup("/showService/findPopularEpisodes");
        cache.flushGroup("/showService/findAllEpisodes");
        cache.flushEntry("/programmas/afleveringen/" + nodenr);

        int programnr = getRelatedProgram(nodenr);
        if (programnr != -1) {
            // Bereken in welk jaar het programma valt.
            int year = getRelatedYear(nodenr);
            if (year != -1) {
                log.debug("flushing /programmas/" + programnr + "/" + year);
                cache.flushEntry("/programmas/" + programnr + "/" + year);
            }
        }
    }

    public static void flushRebelbass(Cache cache, String nodenr) {
        log.debug("flushing rebelbass entries");
        cache.flushEntry("/weblogs/rebelbass/bericht/" + nodenr);
        cache.flushEntry("/weblogs/rebelbass/");
        cache.flushEntry("/components/rebelbassrelated");
        cache.flushEntry("/components/artikelrelated");
        cache.flushEntry("/components/nieuwsenachtergrondenrelatedtop");
        cache.flushEntry("/components/nieuwsenachtergrondenrelatedbottom");
        cache.flushEntry("/components/nieuwsenachtergrondenrelated");
        cache.flushEntry("/frontpage");
        cache.flushEntry("/index.jsp");
        cache.flushEntry("/feeds/rebelbass");
    }

    public static void flushArtists(Cache cache, String nodenr) {
        log.debug("flushing artists entries");
        cache.flushEntry("/artiesten/overzicht/");
        cache.flushEntry("/artiesten/artiest/" + nodenr);
        cache.flushEntry("/components/artistsfrontpagerelated");
        cache.flushEntry("/components/artistrelated");
        cache.flushGroup("/artistsService/load");
        cache.flushGroup("/artistsService/findRecentArtists");
        cache.flushGroup("/artistsService/findPopulairArtists");
        cache.flushGroup("/artistsService/findHighlyRatedArtists");
        cache.flushGroup("/artistsService/findAllArtist");
    }

    public static void flushDossiers(Cache cache, String nodenr) {
        log.debug("flushing dossiers entries");
        cache.flushEntry("/dossiers/");
        cache.flushEntry("/dossiers/*");
        cache.flushEntry("/dossiers/dossier/" + nodenr);
        cache.flushEntry("/components/dossierrelated");

        // rebelbass dossier
        cache.flushEntry("/weblogs/rebelbass/dossier/" + nodenr);
        cache.flushEntry("/components/rebelbassrelated");
    }

    public static void flushLuisterpaal(Cache cache) {
        log.debug("flushing luisterpaal entries");
        cache.flushEntry("/frontpage");
        cache.flushEntry("/index.jsp");
        cache.flushEntry("/luisterpaal/");
        cache.flushEntry("/kijkenluister/overzicht/");
        cache.flushEntry("/luisterpaalservice/findCDs");
    }

    /**
     * Haal het mmevent op aan nodenr, en bepaal daar het jaar van.
     *
     * @return -1 indien het evalueren van het jaar niet mogelijk is.
     */
    private static int getRelatedYear(String nodenr) {
        Cloud cloud = AuthorizedCloud.getAuthorizedCloud();
        Node node = null;
        try {
            node = cloud.getNode(nodenr);
        } catch (NotFoundException nfe) {
            log.error("Node " + nodenr + " does not exists." + nfe);
            return -1;
        }
        NodeList nodelist = node.getRelatedNodes("mmevents");
        if (nodelist.size() == 0) {
            log.error("No mmevent related to " + nodenr);
            return -1;
        }
        Node mmevent = nodelist.getNode(0);
        long starttime = mmevent.getLongValue("start");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(starttime * 1000);
        return calendar.get(Calendar.YEAR);
    }

    private static int getRelatedProgram(String nodenr) {
        Cloud cloud = AuthorizedCloud.getAuthorizedCloud();

        NodeList nodelist = cloud.getList(nodenr, "episodes,programs,maps",
                "maps.number", null, null, null, null, true);
        if (nodelist.size() == 0) {
            log.error("No program related to " + nodenr);
            return -1;
        }
        Node node = nodelist.getNode(0);
        return node.getIntValue("maps.number");
    }

    private static Cache getCache(HttpServletRequest request) {
        return ServletCacheAdministrator.getInstance(
                request.getSession().getServletContext()).getCache(request,
                PageContext.APPLICATION_SCOPE);
    }
}