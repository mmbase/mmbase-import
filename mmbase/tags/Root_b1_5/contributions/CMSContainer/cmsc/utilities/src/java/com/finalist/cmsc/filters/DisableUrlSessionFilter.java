/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.filters;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This Filter disables Url Rewriting and cleans Sessions for user agents which
 * do not support cookies. Example of these user agents are search engines.
 * 
 * URL Encoding is a process of transforming user input to a CGI form so it is fit
 * for travel across the network -- basically, stripping spaces and punctuation
 * and replacing with escape characters. URL Decoding is the reverse process.
 * 
 * URL Rewriting is a technique for saving state information on the user's
 * browser between page hits. It's sort of like cookies, only the information
 * gets stored inside the URL, as an additional parameter. The HttpSession API,
 * which is part of the Servlet API, sometimes uses URL Rewriting when cookies
 * are unavailable.
 */
public class DisableUrlSessionFilter implements Filter {

   private static final Log log = LogFactory.getLog(DisableUrlSessionFilter.class);

   private Pattern userAgentPattern;
    
   /**
    * Filters requests to disable URL-based session identifiers.
    */
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
         ServletException {

      if (!(request instanceof HttpServletRequest)) {
         chain.doFilter(request, response);
         return;
      }

      HttpServletRequest httpRequest = (HttpServletRequest) request;
      HttpServletResponse httpResponse = (HttpServletResponse) response;

      // clear session if session id in URL
      if (httpRequest.isRequestedSessionIdFromURL()) {
         HttpSession session = httpRequest.getSession(false);
         if (session != null) {
            session.invalidate();
         }
      }

      // wrap response to remove URL encoding
      HttpServletResponseWrapper wrappedResponse = new HttpServletResponseWrapper(httpResponse) {

         @Override
         public String encodeRedirectUrl(String url) {
            return getEncodedUrl(url);
         }


         @Override
         public String encodeRedirectURL(String url) {
            return getEncodedUrl(url);
         }


         @Override
         public String encodeUrl(String url) {
            return getEncodedUrl(url);
         }


         @Override
         public String encodeURL(String url) {
            return getEncodedUrl(url);
         }


         /**
          * Translates a string into
          * <code>application/x-www-form-urlencoded</code>
          * 
          * @param url
          * @return
          */
         private String getEncodedUrl(String url) {
            return url;
         }
      };

      chain.doFilter(request, wrappedResponse);
      
      cleanSessions(httpRequest);
   }

    private void cleanSessions(HttpServletRequest httpRequest) {
        try {
            String userAgent = httpRequest.getHeader("User-Agent");
            if (StringUtils.isNotBlank(userAgent) && userAgentPattern != null && userAgentPattern.matcher(userAgent).find()) {
                HttpSession session = httpRequest.getSession(false);
                if (session != null && session.isNew()) {
                    session.invalidate();
                }
            }
        }
        catch (Exception ex) {
            log.fatal("can't process useragent pattern", ex);
        }
    }

   public void init(FilterConfig config) {
       String useragents = config.getInitParameter("useragents");
       if (StringUtils.isBlank(useragents)) {
           String searchEngines = config.getInitParameter("searchEngines");
           if (StringUtils.isBlank(searchEngines)) {
               searchEngines = getSearchEngines();
           }
           String feedReaders = config.getInitParameter("feedReaders");
           if (StringUtils.isBlank(feedReaders)) {
               feedReaders = getFeedReaders();
           }

           useragents = searchEngines + "|" + feedReaders;
       }
       userAgentPattern = Pattern.compile(useragents);
   }

    private String getSearchEngines() {
        // Search engines / Crawlers
        // Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)
        // Googlebot/2.1 (+http://www.google.com/bot.html)
        // Mozilla/5.0 (compatible; Yahoo! Slurp/3.0; http://help.yahoo.com/help/us/ysearch/slurp)
        // msnbot-media/1.0 (+http://search.msn.com/msnbot.htm)
        // IlseBot/1.1
        // Gigabot/3.0 (http://www.gigablast.com/spider.html)
        // Baiduspider+(+http://www.baidu.com/search/spider.htm)
        // WebAlta Crawler/2.0 (http://www.webalta.net/ru/about_webmaster)
        // TinEye/1.1 (http://tineye.com/crawler.html)
        // ICC-Crawler(Mozilla-compatible; icc-crawl-contact(at)ml(dot)nict(dot)go(dot)jp)
        // Yeti/1.0 (+http://help.naver.com/robots/)
        // REAP-crawler Nutch/Nutch-1.0-dev (Reap Project; http://reap.cs.cmu.edu/REAP-crawler/;
        // Reap Project)
        // Snapbot/1.0 (Snap Shots, +http://www.snap.com)

        String searchEngines = "google.com|search.msn.com|yahoo.com|IlseBot|gigablast.com"
                + "|Baiduspider|WebAlta|tineye.com|ICC-Crawler|Yeti|REAP-crawler|Snapbot";
        return searchEngines;
   }

    private String getFeedReaders() {
        // feed readers
        // Feedfetcher-Google; (+http://www.google.com/feedfetcher.html;
        // msnbot-NewsBlogs/1.0 (+http://search.msn.com/msnbot.htm)
        // ilse-patgen/1.0
        // Telegraaf Koppensneller

        // FeedForAll rss2html.php v2
        // UniversalFeedParser/3.3 +http://feedparser.org/
        // RssReader/1.0.88.0 (http://www.rssreader.com)
        // Feedreader 3.12 (Powered by Newsbrain)
        // FeedHub FeedDiscovery/1.0 (http://www.feedhub.com)
        // SharpReader/0.9.7.0 (.NET CLR 1.1.4322.573; WinNT 5.1.2600.0)
        // YahooFeedSeeker/2.0
        // SimplePie/1.0 (Feed Parser; http://simplepie.org/

        // Snarfer/0.9.1 (http://www.snarfware.com/)
        // BlogBridge 5.0.1 (http://www.blogbridge.com/)
        // MagpieRSS/0.72 (+http://magpierss.sf.net)
        // Akregator/1.2.7; librss/remnants
        // Netvibes (http://www.netvibes.com/
        // Rasasa/1.2 (http://www.rasasa.com; 1 subscribers)
        String feedReaders = "google.com|search.msn.com|ilse-patgen|Telegraaf"
            + "|Feed|Reader"
            + "|Snarfer|BlogBridge|MagpieRSS|Akregator|Netvibes|Rasasa";
        return feedReaders;
    }

   public void destroy() {
      // nothing
   }
}