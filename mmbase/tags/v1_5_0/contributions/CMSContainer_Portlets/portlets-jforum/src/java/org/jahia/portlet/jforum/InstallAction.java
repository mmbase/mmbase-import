package org.jahia.portlet.jforum;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import net.jforum.Command;
import net.jforum.ConfigLoader;
import net.jforum.DBConnection;
import net.jforum.DataSourceConnection;
import net.jforum.SessionFacade;
import net.jforum.SimpleConnection;
import net.jforum.context.RequestContext;
import net.jforum.entities.UserSession;
import net.jforum.util.FileMonitor;
import net.jforum.util.I18n;
import net.jforum.util.MD5;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;
import net.jforum.util.preferences.SystemGlobalsListener;
import net.jforum.util.preferences.TemplateKeys;
import net.jforum.view.install.ParseDBDumpFile;
import net.jforum.view.install.ParseDBStructFile;

import org.apache.log4j.Logger;

import freemarker.template.SimpleHash;

public class InstallAction extends Command {

   private static Logger logger = Logger.getLogger(InstallAction.class);

   public InstallAction(){
      
   }
   
   public InstallAction(RequestContext request,SimpleHash context) {
      this.request = request;
      this.context = context;
   }
   /**
    *  Description of the Method
    *
    *@exception  Exception  Description of Exception
    */
   public void welcome() throws Exception {
      this.checkLanguage();

      this.context.put("language", this.getFromSession("language"));
      this.context.put("database", this.getFromSession("database"));
      this.context.put("dbhost", this.getFromSession("dbHost"));
      this.context.put("dbuser", this.getFromSession("dbUser"));
      this.context.put("dbname", this.getFromSession("dbName"));
      this.context.put("dbpasswd", this.getFromSession("dbPassword"));
      this.context.put("dbencoding", this.getFromSession("dbEncoding"));
      this.context.put("use_pool", this.getFromSession("usePool"));
      this.context.put("forumLink", this.getFromSession("forumLink"));
      this.context.put("siteLink", this.getFromSession("siteLink"));
      this.context.put("dbdatasource", this.getFromSession("dbdatasource"));

      this.setTemplateName(TemplateKeys.INSTALL_WELCOME);
   }


   /**
    *  Description of the Method
    *
    *@exception  Exception  Description of Exception
    */
   public void doInstall() throws Exception {
      Connection conn = null;
      if (!this.checkForWritableDir()) {
         return;
      }

      this.removeUserConfig();

      if (!"passed".equals(this.getFromSession("configureDatabase"))) {
         logger.info("Going to configure the database...");
         conn = this.configureDatabase();
         if (conn == null) {
            this.context.put("message", I18n.getMessage("Install.databaseError"));
            this.error();
            return;
         }
      }

      logger.info("Database configuration ok");

      // Database Configuration is ok
      this.addToSessionAndContext("configureDatabase", "passed");

      DBConnection simpleConnection = new SimpleConnection();
      if (conn == null) {
         conn = simpleConnection.getConnection();
      }

      if (!"passed".equals(this.getFromSession("createTables")) && !this.createTables(conn)) {
         this.context.put("message", I18n.getMessage("Install.createTablesError"));
         simpleConnection.releaseConnection(conn);
         this.error();
         return;
      }

      // Create tables is ok
      this.addToSessionAndContext("createTables", "passed");
      logger.info("Table creation is ok");

      if (!"passed".equals(this.getFromSession("importTablesData")) && !this.importTablesData(conn)) {
         this.context.put("message", I18n.getMessage("Install.importTablesDataError"));
         simpleConnection.releaseConnection(conn);
         this.error();
         return;
      }

      // Dump is ok
      this.addToSessionAndContext("importTablesData", "passed");

      if (!this.updateAdminPassword(conn)) {
         this.context.put("message", I18n.getMessage("Install.updateAdminError"));
         simpleConnection.releaseConnection(conn);
         this.error();
         return;
      }

      simpleConnection.releaseConnection(conn);

   // JForumExecutionContext.setRedirect(this.request.getContextPath() + "/install/install"
   //       + SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION)
   //       + "?module=install&action=finished");

   }


   /**
    *  Description of the Method
    *
    *@exception  Exception  Description of Exception
    */
   public void finished() throws Exception {
   // this.setTemplateName(TemplateKeys.INSTALL_FINISHED);

      this.context.put("clickHere", I18n.getMessage("Install.clickHere"));
      this.context.put("forumLink", this.getFromSession("forumLink"));

      String lang = this.getFromSession("language");
      if (lang == null) {
         lang = "en_US";
      }

      this.context.put("lang", lang);

      this.doFinalSteps();

      this.configureSystemGlobals();

      SystemGlobals.loadQueries(SystemGlobals.getValue(ConfigKeys.SQL_QUERIES_GENERIC));
      SystemGlobals.loadQueries(SystemGlobals.getValue(ConfigKeys.SQL_QUERIES_DRIVER));
      SessionFacade.remove(this.request.getSessionContext().getId());
   }


   /**
    *  Description of the Method
    *
    *@exception  Exception  Description of Exception
    */
   public void checkInformation() throws Exception {
      //this.setTemplateName(TemplateKeys.INSTALL_CHECK_INFO);
      String language = this.request.getAttribute("language").toString();
      String database = this.request.getAttribute("database").toString();
      String dbHost = null;//this.request.getAttribute("dbhost").toString();
      String dbUser = null;//this.request.getAttribute("dbuser").toString();
      String dbName = null;//this.request.getAttribute("dbname").toString();
      String dbPassword = null;//this.request.getAttribute("dbpasswd").toString();
      String dbEncoding = this.request.getAttribute("dbencoding").toString();
      String dbEncodingOther = this.request.getAttribute("dbencoding_other").toString();
      String usePool = this.request.getAttribute("use_pool").toString();
      String forumLink = this.request.getAttribute("forum_link").toString();
      String adminPassword = this.request.getAttribute("admin_pass1").toString();

      dbHost = this.notNullDefault(dbHost, "localhost");
      dbEncodingOther = this.notNullDefault(dbEncodingOther, "utf-8");
      dbEncoding = this.notNullDefault(dbEncoding, dbEncodingOther);
      forumLink = this.notNullDefault(forumLink, "http://localhost");
      dbName = this.notNullDefault(dbName, "jforum");
      // ktlili: jahia fork
      dbPassword = this.notNullDefault(dbPassword, "");

      if ("hsqldb".equals(database)) {
         dbUser = this.notNullDefault(dbUser, "sa");
      }
      // ktlili: jahia fork
      else {
         dbUser = this.notNullDefault(dbUser, "");
      }

      this.addToSessionAndContext("language", language);
      this.addToSessionAndContext("database", database);
      this.addToSessionAndContext("dbHost", dbHost);
      this.addToSessionAndContext("dbUser", dbUser);
      this.addToSessionAndContext("dbName", dbName);
      this.addToSessionAndContext("dbPassword", dbPassword);
      this.addToSessionAndContext("dbEncoding", dbEncoding);
      this.addToSessionAndContext("usePool", usePool);
      this.addToSessionAndContext("forumLink", forumLink);
      this.addToSessionAndContext("siteLink", this.request.getAttribute("site_link").toString());
      this.addToSessionAndContext("adminPassword", adminPassword);
      this.addToSessionAndContext("dbdatasource", this.request.getAttribute("dbdatasource").toString());
      this.addToSessionAndContext("db_connection_type", this.request.getAttribute("db_connection_type").toString());

      this.addToSessionAndContext("configureDatabase", null);
      this.addToSessionAndContext("createTables", null);
      this.addToSessionAndContext("importTablesData", null);

      this.context.put("canWriteToWebInf", this.canWriteToWebInf());
      this.context.put("canWriteToIndex", this.canWriteToIndex());

      //this.context.put("moduleAction", "install_check_info.htm");
   }


   /**
    *@throws Exception 
    * @exception  Exception  Description of Exception
    *@see                   net.jforum.Command#list()
    */


   /**
    *@param  request        Description of Parameter
    *@param  response       Description of Parameter
    *@param  context        Description of Parameter
    *@return                Description of the Returned Value
    *@exception  Exception  Description of Exception
    *@see                   net.jforum.Command#process()
    */

   /**
    *  Gets the FromSession attribute of the InstallAction object
    *
    *@param  key  Description of Parameter
    *@return      The FromSession value
    */
   private String getFromSession(String key) {
      return (String) this.request.getSessionContext().getAttribute(key);
   }


   /**
    *  Description of the Method
    *
    *@exception  IOException  Description of Exception
    */
   private void checkLanguage() throws IOException {
      String lang = this.request.getParameter("l");
      if (lang == null || !I18n.languageExists(lang)) {
         return;
      }

      I18n.load(lang);

      UserSession us = new UserSession();
      us.setLang(lang);

      SessionFacade.add(us);
      this.addToSessionAndContext("language", lang);
   }


   /**
    *  Description of the Method
    */
   private void error() {
      this.setTemplateName(TemplateKeys.INSTALL_ERROR);
   }


   /**
    *  Description of the Method
    */
   private void removeUserConfig() {
      File f = new File(SystemGlobals.getValue(ConfigKeys.INSTALLATION_CONFIG));
      if (f.exists() && f.canWrite()) {
         try {
            f.delete();
         }
         catch (Exception e) {
            logger.info(e.toString());
         }
      }
   }


   /**
    *  Description of the Method
    */
   private void doFinalSteps() {
      try {
         // Modules Mapping
         String modulesMapping = SystemGlobals.getValue(ConfigKeys.CONFIG_DIR) + "/modulesMapping.properties";
         if (new File(modulesMapping).canWrite()) {
            Properties p = new Properties();
            p.load(new FileInputStream(modulesMapping));

            if (p.containsKey("install")) {
               p.remove("install");

               p.store(new FileOutputStream(modulesMapping), "Modified by JForum Installer");

               this.addToSessionAndContext("mappingFixed", "true");
               ConfigLoader.loadModulesMapping(SystemGlobals.getValue(ConfigKeys.CONFIG_DIR));
            }
         }
      }
      catch (Exception e) {
         logger.warn("Error while working on modulesMapping.properties: " + e);
      }

      try {
         // Index renaming
         String index = SystemGlobals.getApplicationPath() + "/index.htm";
         File indexFile = new File(index);

         if (indexFile.canWrite()) {
            String newIndex = SystemGlobals.getApplicationPath() + "/__index.redirect";
            File newIndexFile = new File(newIndex);

            if (newIndexFile.exists()) {
               indexFile.delete();
               newIndexFile.renameTo(indexFile);

               this.addToSessionAndContext("indexFixed", "true");
            }
         }
      }
      catch (Exception e) {
         logger.warn("Error while renaming index.htm: " + e, e);
      }
   }


   /**
    *  Description of the Method
    *
    *@exception  Exception  Description of Exception
    */
   private void configureSystemGlobals() throws Exception {
      SystemGlobals.setValue(ConfigKeys.USER_HASH_SEQUENCE, MD5.crypt(this.getFromSession("dbPassword")
            + System.currentTimeMillis()));

      SystemGlobals.setValue(ConfigKeys.FORUM_LINK, this.getFromSession("forumLink"));
      SystemGlobals.setValue(ConfigKeys.HOMEPAGE_LINK, this.getFromSession("siteLink"));
      SystemGlobals.setValue(ConfigKeys.I18N_DEFAULT, this.getFromSession("language"));
      SystemGlobals.setValue(ConfigKeys.INSTALLED, "true");
      SystemGlobals.saveInstallation();

      this.restartSystemGlobals();
   }


   /**
    *  Description of the Method
    *
    *@param  conn           Description of Parameter
    *@return                Description of the Returned Value
    *@exception  Exception  Description of Exception
    */
   private boolean importTablesData(Connection conn) throws Exception {
      boolean status = true;
      boolean autoCommit = conn.getAutoCommit();
      conn.setAutoCommit(false);

      String dbType = this.getFromSession("database");

      List statements = ParseDBDumpFile.parse(SystemGlobals.getValue(ConfigKeys.CONFIG_DIR)
            + "/database/"
            + dbType
            + "/" + dbType + "_data_dump.sql");

      for (Iterator iter = statements.iterator(); iter.hasNext(); ) {
         String query = (String) iter.next();

         if (query == null || "".equals(query.trim())) {
            continue;
         }

         query = query.trim();

         Statement s = conn.createStatement();

         try {
            if (query.startsWith("UPDATE") || query.startsWith("INSERT")
                  || query.startsWith("SET")) {
               s.executeUpdate(query);
            }
            else if (query.startsWith("SELECT")) {
               s.executeQuery(query);
            }
            else {
               throw new Exception("Invalid query: " + query);
            }
         }
         catch (SQLException ex) {
            status = false;
            conn.rollback();
            logger.error("Error importing data for " + query + ": " + ex, ex);
            this.context.put("exceptionMessage", ex.getMessage() + "\n" + query);
            break;
         }
         finally {
            s.close();
         }
      }

      conn.setAutoCommit(autoCommit);
      return status;
   }


   /**
    *  Description of the Method
    *
    *@param  conn           Description of Parameter
    *@return                Description of the Returned Value
    *@exception  Exception  Description of Exception
    */
   private boolean createTables(Connection conn) throws Exception {
      logger.info("Going to create tables...");
      String dbType = this.getFromSession("database");

      if ("postgresql".equals(dbType)) {
         this.dropPostgresqlTables(conn);
      }
      else if ("oracle".equals(dbType)) {
         this.dropOracleTables(conn);
      }

      boolean status = true;

      List statements = ParseDBStructFile.parse(SystemGlobals.getValue(ConfigKeys.CONFIG_DIR)
            + "/database/"
            + dbType
            + "/" + dbType + "_db_struct.sql");

      for (Iterator iter = statements.iterator(); iter.hasNext(); ) {
         String query = (String) iter.next();

         if (query == null || "".equals(query.trim())) {
            continue;
         }

         Statement s = conn.createStatement();

         try {
            s.executeUpdate(query);
         }
         catch (SQLException ex) {
            status = false;

            logger.error("Error executing query: " + query + ": " + ex, ex);
            this.context.put("exceptionMessage", ex.getMessage() + "\n" + query);

            break;
         }
         finally {
            s.close();
         }
      }

      return status;
   }


   /**
    *  Description of the Method
    *
    *@param  conn  Description of Parameter
    */
   private void dropOracleTables(Connection conn) {
      Statement s = null;

      try {
         List statements = ParseDBStructFile.parse(SystemGlobals.getValue(ConfigKeys.CONFIG_DIR)
               + "/database/oracle/oracle_db_struct_drop.sql");

         for (Iterator iter = statements.iterator(); iter.hasNext(); ) {
            try {
               String query = (String) iter.next();

               if (query == null || "".equals(query.trim())) {
                  continue;
               }

               s = conn.createStatement();
               s.executeQuery(query);
               s.close();
            }
            catch (Exception e) {
               logger.error("IGNORE: " + e.toString());
            }
         }
      }
      catch (Exception e) {
         logger.error(e.toString(), e);
      }
      finally {
         if (s != null) {
            try {
               s.close();
            }
            catch (Exception e) {
            }
         }
      }
   }


   /**
    *  Description of the Method
    *
    *@return    Description of the Returned Value
    */
   private boolean checkForWritableDir() {
      boolean canWriteToWebInf = this.canWriteToWebInf();
      boolean canWriteToIndex = this.canWriteToIndex();

      if (!canWriteToWebInf || !canWriteToIndex) {
         this.context.put("message", I18n.getMessage("Install.noWritePermission"));
         this.context.put("tryAgain", true);
         this.error();
         return false;
      }

      return true;
   }


   /**
    *  Description of the Method
    *
    *@return    Description of the Returned Value
    */
   private boolean canWriteToWebInf() {
      return new File(SystemGlobals.getValue(ConfigKeys.CONFIG_DIR) + "/modulesMapping.properties").canWrite();
   }


   /**
    *  Description of the Method
    *
    *@return    Description of the Returned Value
    */
   private boolean canWriteToIndex() {
      return new File(SystemGlobals.getApplicationPath() + "/index.htm").canWrite();
   }


   /**
    *  Description of the Method
    *
    *@exception  Exception  Description of Exception
    */
   private void configureJDBCConnection() throws Exception {
      String username = this.getFromSession("dbUser");
      String password = this.getFromSession("dbPassword");
      String dbName = this.getFromSession("dbName");
      String host = this.getFromSession("dbHost");
      String type = this.getFromSession("database");
      String encoding = this.getFromSession("dbEncoding");
      System.err.println(username + "," + password + "," + dbName + "," + host + "," + type + "," + encoding);

      Properties p = new Properties();
      p.load(new FileInputStream(SystemGlobals.getValue(ConfigKeys.CONFIG_DIR)
            + "/database/" + type + "/" + type + ".properties"));

      // Write database information to the respective file
      p.setProperty(ConfigKeys.DATABASE_CONNECTION_HOST, host);
      p.setProperty(ConfigKeys.DATABASE_CONNECTION_USERNAME, username);
      p.setProperty(ConfigKeys.DATABASE_CONNECTION_PASSWORD, password);
      p.setProperty(ConfigKeys.DATABASE_CONNECTION_DBNAME, dbName);
      p.setProperty(ConfigKeys.DATABASE_CONNECTION_ENCODING, encoding);

      FileOutputStream fos = null;

      try {
         fos = new FileOutputStream(SystemGlobals.getValue(ConfigKeys.CONFIG_DIR)
               + "/database/" + type + "/" + type + ".properties");
         p.store(fos, null);
      }
      catch (Exception e) {
         logger.warn("Error while trying to write to " + type + ".properties: " + e);
      }
      finally {
         if (fos != null) {
            fos.close();
         }
      }

      // Proceed to SystemGlobals / jforum-custom.conf configuration
      for (Enumeration e = p.keys(); e.hasMoreElements(); ) {
         String key = (String) e.nextElement();
         SystemGlobals.setValue(key, p.getProperty(key));
      }

      SystemGlobals.setValue(ConfigKeys.DATABASE_CONNECTION_HOST, host);
      SystemGlobals.setValue(ConfigKeys.DATABASE_CONNECTION_USERNAME, username);
      SystemGlobals.setValue(ConfigKeys.DATABASE_CONNECTION_PASSWORD, password);
      SystemGlobals.setValue(ConfigKeys.DATABASE_CONNECTION_DBNAME, dbName);
      SystemGlobals.setValue(ConfigKeys.DATABASE_CONNECTION_ENCODING, encoding);

   }


   /**
    *  Description of the Method
    *
    *@param  from           Description of Parameter
    *@param  to             Description of Parameter
    *@exception  Exception  Description of Exception
    */
   private void copyFile(String from, String to) throws Exception {
      FileChannel source = new FileInputStream(new File(from)).getChannel();
      FileChannel dest = new FileOutputStream(new File(to)).getChannel();

      source.transferTo(0, source.size(), dest);
      source.close();
      dest.close();
   }


   /**
    *  Description of the Method
    *
    *@return                Description of the Returned Value
    *@exception  Exception  Description of Exception
    */
   private Connection configureDatabase() throws Exception {
      String database = this.getFromSession("database");
      String connectionType = this.getFromSession("db_connection_type");
      String implementation;
      boolean isDs = false;

      if ("JDBC".equals(connectionType)) {
         implementation = "yes".equals(this.getFromSession("usePool")) && !"hsqldb".equals(database)
               ? "net.jforum.PooledConnection"
               : "net.jforum.SimpleConnection";

         this.configureJDBCConnection();
      }
      else {
         isDs = true;
         implementation = "net.jforum.DataSourceConnection";
         SystemGlobals.setValue(ConfigKeys.DATABASE_DATASOURCE_NAME, this.getFromSession("dbdatasource"));
      }

      SystemGlobals.setValue(ConfigKeys.DATABASE_CONNECTION_IMPLEMENTATION, implementation);
      SystemGlobals.setValue(ConfigKeys.DATABASE_DRIVER_NAME, database);

      SystemGlobals.saveInstallation();
      this.restartSystemGlobals();
      int fileChangesDelay = SystemGlobals.getIntValue(ConfigKeys.FILECHANGES_DELAY);
      if (fileChangesDelay > 0) {
         FileMonitor.getInstance().addFileChangeListener(new SystemGlobalsListener(),
               SystemGlobals.getValue(ConfigKeys.INSTALLATION_CONFIG), fileChangesDelay);
      }

      Connection conn = null;

      try {
         DBConnection s;

         if (!isDs) {
            s = new SimpleConnection();
         }
         else {
            s = new DataSourceConnection();
         }

         s.init();

         conn = s.getConnection();
      }
      catch (Exception e) {
         logger.warn("Error while trying to get a connection: " + e);
         this.context.put("exceptionMessage", e.getMessage());
         return null;
      }

      return conn;
   }


   /**
    *  Description of the Method
    *
    *@exception  Exception  Description of Exception
    */
   private void restartSystemGlobals() throws Exception {
      String appPath = SystemGlobals.getApplicationPath();
      SystemGlobals.initGlobals(appPath, appPath + "/WEB-INF/config/SystemGlobals.properties");
      SystemGlobals.loadAdditionalDefaults(SystemGlobals.getValue(ConfigKeys.DATABASE_DRIVER_CONFIG));

      if (new File(SystemGlobals.getValue(ConfigKeys.INSTALLATION_CONFIG)).exists()) {
         SystemGlobals.loadAdditionalDefaults(SystemGlobals.getValue(ConfigKeys.INSTALLATION_CONFIG));
      }
   }


   /**
    *  Description of the Method
    *
    *@param  conn           Description of Parameter
    *@return                Description of the Returned Value
    *@exception  Exception  Description of Exception
    */
   private boolean updateAdminPassword(Connection conn) throws Exception {
      logger.info("Going to update the administrator's password");

      boolean status = false;

      try {
         PreparedStatement p = conn.prepareStatement("UPDATE jforum_users SET user_password = ? WHERE username = 'Admin'");
         p.setString(1, MD5.crypt(this.getFromSession("adminPassword")));
         p.executeUpdate();
         p.close();

         status = true;
      }
      catch (Exception e) {
         logger.warn("Error while trying to update the administrator's password: " + e);
         this.context.put("exceptionMessage", e.getMessage());
      }

      return status;
   }


   /**
    *  Description of the Method
    *
    *@param  conn           Description of Parameter
    *@exception  Exception  Description of Exception
    */
   private void dropPostgresqlTables(Connection conn) throws Exception {
      String[] tables = {"jforum_banlist", "jforum_banlist_seq", "jforum_categories",
            "jforum_categories_order_seq", "jforum_categories_seq", "jforum_config",
            "jforum_config_seq", "jforum_forums", "jforum_forums_seq", "jforum_groups",
            "jforum_groups_seq", "jforum_posts", "jforum_posts_seq", "jforum_posts_text",
            "jforum_privmsgs", "jforum_privmsgs_seq", "jforum_privmsgs_text",
            "jforum_ranks", "jforum_ranks_seq", "jforum_role_values", "jforum_roles",
            "jforum_roles_seq", "jforum_search_results", "jforum_search_topics",
            "jforum_search_wordmatch", "jforum_search_words", "jforum_search_words_seq", "jforum_sessions",
            "jforum_smilies", "jforum_smilies_seq", "jforum_themes", "jforum_themes_seq",
            "jforum_topics", "jforum_topics_seq", "jforum_topics_watch", "jforum_user_groups",
            "jforum_users", "jforum_users_seq", "jforum_vote_desc", "jforum_vote_desc_seq",
            "jforum_vote_results", "jforum_vote_voters", "jforum_words", "jforum_words_seq",
            "jforum_karma_seq", "jforum_karma", "jforum_bookmarks_seq", "jforum_bookmarks",
            "jforum_quota_limit", "jforum_quota_limit_seq", "jforum_extension_groups_seq",
            "jforum_extension_groups", "jforum_extensions_seq", "jforum_extensions",
            "jforum_attach_seq", "jforum_attach", "jforum_attach_desc_seq", "jforum_attach_desc",
            "jforum_attach_quota_seq", "jforum_attach_quota", "jforum_banner", "jforum_banner_seq",
            "jforum_forums_watch"};

      for (int i = 0; i < tables.length; i++) {
         Statement s = conn.createStatement();
         String query = tables[i].endsWith("_seq") ? "DROP SEQUENCE " : "DROP TABLE ";
         query += tables[i];

         try {
            s.executeUpdate(query);
         }
         catch (SQLException e) {
            logger.info("IGNORE: " + e.getMessage());
         }

         s.close();
      }
   }


   /**
    *  Adds a feature to the ToSessionAndContext attribute of the InstallAction
    *  object
    *
    *@param  key    The feature to be added to the ToSessionAndContext attribute
    *@param  value  The feature to be added to the ToSessionAndContext attribute
    */
   private void addToSessionAndContext(String key, String value) {
      this.request.getSessionContext().setAttribute(key, value);
      this.context.put(key, value);
   }


   /**
    *  Description of the Method
    *
    *@param  value       Description of Parameter
    *@param  useDefault  Description of Parameter
    *@return             Description of the Returned Value
    */
   private String notNullDefault(String value, String useDefault) {
      if (value == null || value.trim().equals("")) {
         return useDefault;
      }

      return value;
   }
   public void ignoreAction() {
      super.ignoreAction();
  }

   @Override
   public void list() {
      // TODO Auto-generated method stub
      
   }

}
