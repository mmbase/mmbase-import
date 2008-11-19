package com.finalist.util.http;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HttpUtil {

   private static final String SITE_ENCODING = "cp1252";

   private static ThreadLocal<HttpClient> httpClient = new ThreadLocal<HttpClient>();

   private static Log log;


   protected static Log getLogger() {
      if (log == null) {
         log = LogFactory.getLog(HttpUtil.class);
      }
      return log;
   }


   public static String doGet(String url, Map<String, Object> parameterMap) throws PageNotFoundException {
      return doRequest(url, new GetMethod(url), parameterMap);
   }


   public static String doPost(String url, Map<String, Object> parameterMap) throws PageNotFoundException {
      return doRequest(url, new PostMethod(url), parameterMap);
   }


   public static String doRequest(String url, HttpMethod method, Map<String, Object> parameterMap)
         throws PageNotFoundException {
      String result;
      try {
         if (parameterMap != null) {
            StringBuffer queryString = new StringBuffer();
            for (Iterator<String> i = parameterMap.keySet().iterator(); i.hasNext();) {
               String key = i.next();
               Object objectValue = parameterMap.get(key);
               String value = null;
               if (objectValue instanceof String) {
                  value = (String) objectValue;
               }
               if (objectValue instanceof String[]) {
                  value = ((String[]) objectValue)[0];
               }

               queryString.append(URLEncoder.encode(key, SITE_ENCODING));
               queryString.append("=");
               queryString.append(URLEncoder.encode(value, SITE_ENCODING));
               if (i.hasNext()) {
                  queryString.append("&");
               }
            }
            method.setQueryString(queryString.toString());

         }

         int response = getHttpClient().executeMethod(method);

         if (response != HttpStatus.SC_OK) {
            getLogger().info(" HTTP response code: " + response);
         }

         result = method.getResponseBodyAsString();
      }
      catch (IOException e) {
         throw new PageNotFoundException("Could not read url " + url, e);
      }
      finally {
         method.releaseConnection();
      }

      if (result == null) {
         throw new PageNotFoundException("Could not read " + url);
      }

      if (result.contains("<title>Error ")) {
         throw new PageNotFoundException("Page does not exist: " + url);
      }
      if (result.contains("<title>The page cannot be found</title>")) {
         throw new PageNotFoundException("Page does not exist: " + url);
      }
      if (result.contains("<title>Directory Listing Denied</title>")) {
         throw new PageNotFoundException("Page does not exist: " + url);
      }

      return result;
   }


   private static HttpClient getHttpClient() {
      if (httpClient.get() == null) {

         HttpClient client = new HttpClient();
         client.getParams().setParameter(HttpMethodParams.USER_AGENT,
               "Jakarta Commons-HttpClient/3.0 (CMS Container Server Side Include Portlet)");
         client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, SITE_ENCODING);

         // Retry 10 times, even when request was already fully sent
         DefaultHttpMethodRetryHandler retryhandler = new DefaultHttpMethodRetryHandler(10, true);
         client.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, retryhandler);

         httpClient.set(client);
      }

      return httpClient.get();
   }
}
