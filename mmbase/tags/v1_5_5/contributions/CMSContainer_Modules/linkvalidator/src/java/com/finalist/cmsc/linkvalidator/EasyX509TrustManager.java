package com.finalist.cmsc.linkvalidator;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EasyX509TrustManager implements X509TrustManager {
   private X509TrustManager standardTrustManager = null;

   /** Log object for this class. */
   private static final Log LOG = LogFactory.getLog(EasyX509TrustManager.class);


   /**
    * Constructor for EasyX509TrustManager.
    * 
    * @param keystore
    * @throws NoSuchAlgorithmException
    * @throws KeyStoreException
    */
   public EasyX509TrustManager(KeyStore keystore) throws NoSuchAlgorithmException, KeyStoreException {
      super();
      TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      factory.init(keystore);
      TrustManager[] trustmanagers = factory.getTrustManagers();
      if (trustmanagers.length == 0) {
         throw new NoSuchAlgorithmException("no trust manager found");
      }
      this.standardTrustManager = (X509TrustManager) trustmanagers[0];
   }


   /**
    * @see javax.net.ssl.X509TrustManager#checkClientTrusted(X509Certificate[],String)
    */
   public void checkClientTrusted(X509Certificate[] certificates, String authType) throws CertificateException {
      standardTrustManager.checkClientTrusted(certificates, authType);
   }


   /**
    * @see javax.net.ssl.X509TrustManager#checkServerTrusted(X509Certificate[],String)
    */
   public void checkServerTrusted(X509Certificate[] certificates, String authType) throws CertificateException {
      if ((certificates != null) && LOG.isDebugEnabled()) {
         LOG.debug("Server certificate chain:");
         for (int i = 0; i < certificates.length; i++) {
            LOG.debug("X509Certificate[" + i + "]=" + certificates[i]);
         }
      }
      if ((certificates != null) && (certificates.length == 1)) {
         certificates[0].checkValidity();
      }
      else {
         try {
            standardTrustManager.checkServerTrusted(certificates, authType);
         }
         catch (CertificateException ce) {
            // just carry on
            // LOG.warn(ce.getMessage());
         }

      }
   }


   /**
    * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
    */
   public X509Certificate[] getAcceptedIssuers() {
      return this.standardTrustManager.getAcceptedIssuers();
   }
}