/**

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

*/

package org.mmbase.servlet.filter;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.GZIPOutputStream;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This servlet will drastically reduce network traffic by gzipping the outputstream
 * when a client (browser) signals with a header that this feature is enabled. 
 * With html and jsp pages, a drop of 60% can be expected. 
 *
 * It has to be defined as a filter in web.xml where the outputstream is 
 * being redirected to this filter. It will gzip the stream only of it meets
 * the following criteria:
 *  
 *   - it is not a jsp-include
 *   - it is not a mm:include
 *   - file is not already ached at client-side
 *   - the request is above HTTP/0.9 
 *   - client indicates it can handle gzipped stream
 *   - file to compress is not already compressed or empy 
 *
 * Special care has to be taken to not confuse the realplayer. It does not seem
 * to like the compressed stream. To avoid sending the player these streams,
 * this filter will not honour the gzip stream and return the normal uncompressed
 * stream if the filename matches '.smil' or '.smmil' (and rastreams and rmstreams
 * to be absolutely sure that nobody has bookmarked these in their realplayer).
 *
 * To enable this feature, open '<HTML-ROOT>/WEB-INF/web.xml' and add the following
 * lines :
 *
 * In web.xml
 * ----------
 * <filter>
 *    <filter-name>gzip</filter-name>
 *    <display-name>GZIPFilter</display-name>
 *    <filter-class>org.mmbase.servlet.filter.GZIPFilter</filter-class>
 *  </filter>
 *  <filter-mapping>
 *    <filter-name>gzip</filter-name>
 *    <url-pattern>*</url-pattern> 
 *  </filter-mapping> 
 *
 *  Browsers which do not seem to handle these requests are (quick grep):
 * -------------------------------------------------------------------------------------
 *   DA 5.0
 *   Eidetica/1.0 
 *   FAST-WebCrawler/3.6
 *   LeechGet 2002 
 *   Lite Bot 0916b)
 *   MSProxy/2.0)
 *   Microsoft URL Control - 6.00.8169
 *   Mozilla/3.0 (INGRID/3.0 MT; webcrawler@NOSPAMexperimental.net)
 *   Mozilla/3.0 (compatible; AvantGo 3.2)
 *   Mozilla/3.01 (compatible;)
 *   Mozilla/4.0 (                                  )
 *   Mozilla/4.0 (compatible; MSIE 4.5; Mac_PowerPC)
 *   Mozilla/4.0 (compatible; MSIE 5.01; Windows NT 5.0)(R1 1.1)
 *   Mozilla/4.0 (compatible; MSIE 5.01; Windows NT 5.0)
 *   Mozilla/4.0 (compatible; MSIE 5.01; Windows NT 5.0; (R1 1.3))
 *   Mozilla/4.0 (compatible; MSIE 5.01; Windows NT)
 *   Mozilla/4.0 (compatible; MSIE 5.0; Mac_PowerPC)
 *   Mozilla/4.0 (compatible; MSIE 5.0; Mac_PowerPC; E503628FREELER1)
 *   Mozilla/4.0 (compatible; MSIE 5.0; Windows 95; DigExt)
 *   Mozilla/4.0 (compatible; MSIE 5.0; Windows 98)
 *   Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; CNETHomeBuild051099; DigExt)
 *   Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)
 *   Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt; DTS Agent
 *   Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt; Tommy Hilfiger Europe b.v.)
 *   Mozilla/4.0 (compatible; MSIE 5.12; Mac_PowerPC)
 *   Mozilla/4.0 (compatible; MSIE 5.13; Mac_PowerPC)
 *   Mozilla/4.0 (compatible; MSIE 5.14; Mac_PowerPC)
 *   Mozilla/4.0 (compatible; MSIE 5.15; Mac_PowerPC)
 *   Mozilla/4.0 (compatible; MSIE 5.16; Mac_PowerPC)
 *   Mozilla/4.0 (compatible; MSIE 5.21; Mac_PowerPC)
 *   Mozilla/4.0 (compatible; MSIE 5.22; Mac_PowerPC)
 *   Mozilla/4.0 (compatible; MSIE 5.5; Windows 95)
 *   Mozilla/4.0 (compatible; MSIE 5.5; Windows 98)(R1 1.1)
 *   Mozilla/4.0 (compatible; MSIE 5.5; Windows 98)
 *   Mozilla/4.0 (compatible; MSIE 5.5; Windows 98; (R1 1.3))
 *   Mozilla/4.0 (compatible; MSIE 5.5; Windows 98; CHWIE_NL60)
 *   Mozilla/4.0 (compatible; MSIE 5.5; Windows 98; CWIE55NL60; CHWIE_NL60)
 *   Mozilla/4.0 (compatible; MSIE 5.5; Windows 98; HCCnet Internet Explorer)
 *   Mozilla/4.0 (compatible; MSIE 5.5; Windows 98; Het Net 4.0)
 *   Mozilla/4.0 (compatible; MSIE 5.5; Windows 98; SKY11)
 *   Mozilla/4.0 (compatible; MSIE 5.5; Windows 98; Supplied by blueyonder)
 *   Mozilla/4.0 (compatible; MSIE 5.5; Windows 98; T312461)
 *   Mozilla/4.0 (compatible; MSIE 5.5; Windows 98; Win 9x 4.90)
 *   Mozilla/4.0 (compatible; MSIE 5.5; Windows 98; Win 9x 4.90; CWIE55NL60; CHWIE_NL60; H010818)
 *   Mozilla/4.0 (compatible; MSIE 5.5; Windows 98; Win 9x 4.90; Hotbar 3.0)
 *   Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 4.0)
 *   Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 4.0; (R1 1.3))
 *   Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 4.0; H010818)
 *   Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 4.0; Unilever Research)
 *   Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0) Fetch API Request
 *   Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0)(R1 1.1)
 *   Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0)
 *   Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0; (R1 1.3))
 *   Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0; .NET CLR 1.0.3705)
 *   Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0; T312461)
 *   Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0; User attatched to NJIT's network in the NJSOA domain)
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows 98)
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows 98; (R1 1.3))
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows 98; CHWIE_NL60)
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows 98; CHWIE_NL70)
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows 98; Compaq; .NET CLR 1.0.3705)
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows 98; HCCnet)
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows 98; Hotbar 4.1.8.0)
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows 98; MSN 6.1; MSNbMSFT; MSNmnl-be; MSNc0z)
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows 98; QXW0339t)
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows 98; TNET5.0NL)
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows 98; TUCOWS)
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows 98; WELZORG)
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows 98; Win 9x 4.90)(R1 1.1)
 *   Mozilla/4.0 Win 9x 4.90)(R1 1.1)
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows 98; Win 9x 4.90)
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows 98; Win 9x 4.90; (R1 1.3))
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows 98; Win 9x 4.90; Q312461)
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows 98; YComp 5.0.0.0)(R1 1.1)
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows 98; {Tiscali}; MSN 6.1; MSNbMSFT; MSNmnl-nl; MSNc00)
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows 98; {Tiscali}; MSN 6.1; MSNbMSFT; MSNmnl-nl; MSNc0z)
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 4.0)(R1 1.1)
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 4.0)
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)(R1 1.1)
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0; (R1 1.1))
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0; CHWIE_BE70)
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0; DigExt; CHWIE_NL70)
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0; Q312461)
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0; vnunet 5.5 200012)
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)(R1 1.1)
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; (R1 1.3))
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 1.0.3705; (R1 1.3))
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; AIRF)
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; AtHome033)
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; CHWIE_NL60)
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; CWIE55NL60; CHWIE_NL60; MSN 6.1; MSNbMSFT; MSNmnl-nl; MSNcOTH)
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; Hotbar 4.0)
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; MSN 6.1; MSNbMSFT; MSNmnl-be; MSNcOTH)
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; MSOCD)
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; MSOCD; AIRF)
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; Q312461)
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; TNET5.0NL)
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; YComp 5.0.0.0; (R1 1.3))
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; Zon Breedband)
 *   Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; {Tiscali})
 *   Mozilla/5.0 (Slurp/cat; slurp@inktomi.com; http://www.inktomi.com/slurp.html)
 *   Mozilla/5.5 (compatible; MSIE 5.5)
 *   Netscape 3.0 Compatible (WhatsNew Robot)
 *   RMA/1.0 (compatible; RealMedia)
 *   RealPlayer 4.0
 *   RealPlayer 5.0
 *   Scooter/3.2.SF0
 *   contype
 *   ia_archiver
 *   larbin_2.6.2 tom@lislegaard.net
 *   none
 * -------------------------------------------------------------------------------------
 *  
 *  These clients will get the normal request returned and will experience nothing different.
 *  Thanks to orionserver.com for giving a nice explanation of this handy filter.
 *
 *  For futher explination, 
 *   see http://www.orionserver.com/tutorials/filters
 *
 * @author Marcel Maatkamp, VPRO Netherlands (marmaa_at_vpro.nl)
 * @version $Version$
 */

public class GZIPFilter extends GenericFilter {

    static Logger log = Logging.getLoggerInstance(GZIPFilter.class.getName());

    public GZIPFilter() {
        log.trace("");
    }

    /**
     * GZip the incoming request stream
     *
     * This will only happen if
     *  - client does not cache it locally already (notCached(wrapper))
     *  - this is not a include request (notInclude(request)
     *  - client supports gzip format (clientSupportsCompression(request))
     *  - this request is above 0.9 (aboveHttp0_9(request))
     *  - this page is not a mm:include (clientPage(request))
     *  - the filetype is already compressed or is empty (!excludeFile(request))
     *
     *  @param request from client
     *  @param response to client
     *  @param chain of filters
     */
    public void doFilter(final ServletRequest request, final ServletResponse response, FilterChain chain) 
        throws java.io.IOException, javax.servlet.ServletException {

        GenericResponseWrapper wrapper = new GenericResponseWrapper((HttpServletResponse) response);
        chain.doFilter(request, wrapper);
        OutputStream out = response.getOutputStream();
        if (notCached(wrapper) && notInclude(request) && clientSupportsCompression(request) && 
                aboveHttp0_9(request) && clientPage(request) && !excludeFile(request)) {
            ByteArrayOutputStream compressed = new ByteArrayOutputStream();
            GZIPOutputStream gzout = new GZIPOutputStream(compressed);
            gzout.write(wrapper.getData());
            gzout.close();
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setHeader("Content-Encoding", "gzip");
            out.write(compressed.toByteArray());
            response.setContentLength(compressed.size());
            log.info("url("+((HttpServletRequest)request).getRequestURI()+"): page("+wrapper.getData().length+
                "), gzip("+compressed.size()+"), ratio("+(int)(((double)compressed.size()/wrapper.getData().length)*100)+"%)");
        }
        else {
            log.info("url("+((HttpServletRequest)request).getRequestURI()+"): normal("+wrapper.getData().length+")");
            out.write(wrapper.getData());
        }
    }

    /**
     * Exclude the following in an url:
     *  - rastreams.db and rmstreams.db (realplayer does not like gzip)
     *  - gif and jpg files (add more compressed images here, except bmp)
     *  - smil and ssmill (realplayer does not like these gzipped)
     *  - jump.db (empty wrapper)
     *
     *  @param request from client
     *  @return true if file does not have to be gzipped
     */
    protected boolean excludeFile(final ServletRequest request) { 
        boolean result = false;
        String url = ((HttpServletRequest)request).getRequestURI();
        if(     url.indexOf("rastreams.db")!=-1 || url.indexOf("rmstreams.db")!=-1 || 
                url.endsWith(".gif") || url.endsWith(".jpg") || 
                url.endsWith(".smil") || url.endsWith(".ssmil") || url.indexOf("jump.db") != -1)
            result = true;
        else 
            result = false;
        log.trace("result("+result+")");
        return result;
    }


    /** 
     * Only execute filter if clientside http-request is above 0.9
     * It will only block 0.9 requests to speed it up.
     * 
     * @param request from client
     * @return true if the request indicates http1.0 or above
     */
    protected boolean aboveHttp0_9(final ServletRequest request) { 
        boolean result = false;

        String protocol = request.getProtocol();
        if(protocol.indexOf("0.9")!=-1)
            result = false;
        else 
            result = true;

        log.trace("protocol("+protocol+"): result("+result+")");
        return result;
    }

    /**
     *  Check if this is not an jsp-include page
     *  
     *  @param request to check if it is an include
     *  @return if the request is a jsp-include
     */
    protected boolean notInclude(ServletRequest request) {
        boolean result = false;
        String uri = (String) request.getAttribute("javax.servlet.include.request_uri");
        if (uri == null) {
            result = true;
        } else {
            result = false;
        }
        log.trace("result("+result+")");
        return result;
    }

    /**
     *  Check wether clients browser has a cached version
     *  
     *  @param wrapper the wrapper for response
     */
    protected boolean notCached(GenericResponseWrapper wrapper) {
        boolean result = false;
        if (wrapper.getData().length > 0) {
            result=true;
        } else {
            result=false;
        }
        log.trace("result("+result+")");
        return result;
    }

    /** 
     * Check if line is in the client's header
     * 
     * @param request the servlet request
     * @param line to check if it exsist in header
     * @return if line is in header
     */
    protected boolean containsHeader(ServletRequest request, String line) {
        boolean result = false;
        Enumeration e = ((HttpServletRequest)request).getHeaders(line);
        if (e != null && e.hasMoreElements()) 
            result = true;
        log.trace("line("+line+"): result("+result+")");
        return result;
    }

    /**
     * Check wether header contains this line with this value
     * Example: return containsHeaderValue(request, "Accept-Encoding", "gzip")
     *
     * @param request from client
     * @param header line to check for in header
     * @param value to check if this line contains certain value
     *
     * @return if the header contains this line with this value
     */
    protected boolean containsHeaderValue(ServletRequest request, String header, String value) {
        boolean result = false;
        Enumeration e = ((HttpServletRequest)request).getHeaders(header);
        while (e != null && e.hasMoreElements() && !result) {
            String name = (String)e.nextElement();
            if (name.indexOf(value) != -1)
                result = true;
        }
        log.trace("header("+header+"), value("+value+"): result("+result+")");
        return result;
    }


    /**
     *  Check if client accepts gzipped data 
     *
     *  @param request from client
     *  @return true if client indicates it will accept gzipped data
     */
    protected boolean clientSupportsCompression(ServletRequest request) {
        boolean result = containsHeaderValue(request, "Accept-Encoding", "gzip");
        if(!result) {
            String agent = "";
            Enumeration e = ((HttpServletRequest)request).getHeaders("USER-AGENT");
            while(e.hasMoreElements())
                agent += (String)e.nextElement();
            if(agent.indexOf("Java")==-1)
                log.info("no gzip, agent("+agent+")");
        }
        log.trace("result("+result+")");
        return result;
    }

    /**
     * Check if the request does not come from a mm:include
     *
     * This is nasty. The way current implementation of mm:include works is
     * by sending a new request for each include. Those cannot be gezipped (would
     * be the same as zipping a zipfile). If a proxy is used to buffer between
     * normal users and webserver, this is the safest way to detect these request. 
     *
     * To check if this is happening we get from the request the 'USER-AGENT'. If
     * a mm:include is the one making the request, we see a agent 'Java'. One
     * could add an extra check on ip/'localhost' to futher verify this. 
     *
     * @param request from client (could be squid, reject those)
     * @return true if it is a mm:included page (request will come fron localhost and USER-AGENT is 'Java')
     */
    protected boolean clientPage(ServletRequest request) { 
        boolean result = !containsHeaderValue(request, "USER-AGENT", "java");
        log.trace("result("+result+")");
        return result;
    }
}
