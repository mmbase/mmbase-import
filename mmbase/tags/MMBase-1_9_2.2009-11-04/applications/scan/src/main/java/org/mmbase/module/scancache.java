/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;

import java.io.*;
import java.sql.Date;
import java.util.Hashtable;

import javax.servlet.http.HttpServletResponse;

import org.mmbase.module.builders.NetFileSrv;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.gui.html.scanparser;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * File cache system.
 * This system only caches texts (it stores and retrieves strings).
 * Texts are asociated with a key, which is used both as a filename on disk and a
 * key into a memory cache.
 * While in theory it is possible to cache ANY text, this module is mainly used to store pages
 * based on their url.<br />
 * Caching is done in pools. Each pool has its own memory cache and files, and has
 * different ways to handle file caching. The pools currently supported are "PAGE" and "HENK".
 *
 * @application SCAN
 * @rename SCANCache
 * @author Daniel Ockeloen
 * @author Pierre van Rooden (javadocs)
 * @version $Id$
 */
public class scancache extends Module implements scancacheInterface {

    /**
     * Maximum size of a pool's memory cache.
     */
    public static final int MAX_CACHE_POOL_SIZE = 200;

    // logger
    private static Logger log = Logging.getLoggerInstance(scancache.class.getName());

        private scanparser scanparser;

    /**
     * Default expiration time for a cache entry, in seconds.
     * The default is 6 hours.
     * Should be made configurable through a property in scancache.xml
     */
    private static int defaultExpireTime = 21600;

    /**
     * Contains the memory caches for the cache pools.
     * Pools are identified by name. Various pools have different ways of cache-handling.
     * The pools currently supported are 'HENK' and 'PAGE'.
     * The key to retrieve a pool is the poolname. The value returned ia a LRUHashtable,
     * configured to hold a maximum of MAX_CACHE_POOL_SIZE entries.
     */
    Hashtable<String, LRUHashtable<String, String>> pools = new Hashtable<String, LRUHashtable<String, String>>();

    /**
     * Contains the last date (as an <code>Integer</code>) a file was stored in a pool.
     * The key to retrieve the time is poolname+filekey (where a filekey is generally the file URI).
     * There is a limit to the number of values stored in this pool. This means if you have
         * more then 4 pooltypes you have to bump that value or suffer performance degredation
     */
    LRUHashtable<String,Integer> timepool = new LRUHashtable<String,Integer>(MAX_CACHE_POOL_SIZE*4);

    // org.mmbas StatisticsInterface stats;

    /**
     * reference to MMBase module, used to retrieve netfiles and pagemakers builders
     * that support caching.
     */
    MMBase mmbase;
    /**
     * Determines whether the cache module is active.
     * Set by the status field in the scancache.xml configuration file.
     */
    boolean status=false;

    /**
     * The root of the cache filepath.
     * Set by the CacheRoot property in the scancache.xml configuration file.
     */
    private String cachepath="";

    /**
     * Scancache module constructor.
     */
    public scancache() {
    }

    /**
     * Event that should be triggered when the module is loaded.
     * Unused.
     */
    public void onload() {
    }

    /**
     * Event that sh*ould be triggered when the module is shut down.
     * Unused.
     */
    public void shutdown() {
    }

    /**
     * Event that should be triggered when the module is unloaded.
     * Unused.
     */
    public void unload() {
    }

    /**
     * Initializes the module.
     * Reads parameters from the scancache.xml configuration file.
     */
    public void init() {
        String tmp=getInitParameter("status");
        log.debug("status "+tmp);
        if (tmp!=null && tmp.equals("active")) status=true;

        cachepath=getInitParameter("CacheRoot");
        if (cachepath==null) {
            // XXX should set cache to inactive?
            log.error("SCANCACHE -> No CacheRoot property set in the scancache.xml configuration file");
        }
        mmbase=(MMBase)getModule("MMBASEROOT");
        /* org.mmbase
        if (statmode!=null && statmode.equals("yes")) {
            stats=(StatisticsInterface)getModule("STATS");
        } else {
            stats=null;
        }
        */
        scanparser=(scanparser)getModule("SCANPARSER");
    }

    /**
     * Retrieve a file from the indicated pool's cache.
     * When using the "HENK" pool, this method performs a check on expiration using
     * the default expiration time (6 hours).
     * For cache "PAGE", this method does not check expiratio, and retrieves the data
     * from the file from disk, NOT from the memory cache.
     * @param poolName name of the cache pool, either "HENK" or "PAGE"
     * @param key URL of the page to retrieve
     * @return the page's content as a string, or <code>null</code> if no entry was found
     *     (i.e. cache was empty or poolname was invalid).
     */
    public String get(String poolName, String key,scanpage sp) {
        if (status==false) return null; // no cache when inactive
        log.debug("poolName="+poolName+" key="+key);
        if (poolName.equals("HENK")) {
            String tmp=get(poolName,key,">",sp);
            return tmp;
        } else if (poolName.equals("PAGE")) {
            return getPage(poolName,key);
        }
        log.error("get("+poolName+","+key+"): poolname("+poolName+") is an unknown cachetype!");
        return null;
    }

    /**
     * Retrieve a file from the indicated pool's cache.
     * When using the "HENK" pool, this method performs a check on expiration using
     * the default expiration time (6 hours).
     * For cache "PAGE", this method does not check expiration, and retrieves the data
     * from the file from disk, NOT from the memory cache.
     * @param poolname name of the cache pool, either "HENK" or "PAGE"
     * @param key URL of the page to retrieve
     * @param line contains parameters for counters, expiration time, etc, in tag-format.
     *             For this method, these options are currently only used at the mmbase.org site.
     * @return the page's content as a string, or <code>null</code> if no entry was found
     *     (i.e. cache was empty or poolname was invalid).
     */
/*    public String getNew(String poolName, String key,String line,scanpage sp) {
        if (status==false) return null; // no cache when inactive
        line=line.substring(0,line.indexOf('>'));

        // org.mmbase
//        log.debug("scancache -> new poolName="+poolName+" key="+key+" line="+line+" tagger="+counter+" stats="+stats);
//        if (counter!=null && stats!=null) {
//            stats.setCount(counter,1);
//        }


        if (poolName.equals("HENK")) {
            String tmp=get(poolName,key,">",sp);
            return tmp;
        } else if (poolName.equals("PAGE")) {
            return getPage(poolName,key);
        }
        log.error("getNew("+poolName+","+key+"): poolname("+poolName+") is an unknown cachetype!");
        return null;
    }
*/
    /**
     * Retrieve a file from the indicated pool's cache.
     * It is first retrieved from meory. if that fails, the file is retrieved from disk.
     * This method performs a check on expiration using either the default expiration
     * time (6 hours), or the value in the line parameter.
         * This method returns an old version of the page if the page in the cache has expired.
         * It will signal the scanparser to calculate a new one in the background.
         * This avoids contention on a busy server as the page is only calculated once when expired
         * not calculate every request that comes in during the window of calculation.
     * @param poolName name of the cache pool, expected (but not verified) are "HENK" or "PAGE"
     * @param key URL of the page to retrieve
     * @param line the expiration value, either the expiration value in seconds or the word 'NOEXPIRE'. Due to
     *               legacy this value needs to be closed with a '>'.
     *               If the parameter is empty a default value is used.
     * @return the page's content as a string, or <code>null</code> if no entry was found
     */
    public String get(String poolName, String key, String line,scanpage sp) {
        if (status==false) return null; // no cache when inactive

        // get the interval time
        // (nothing, NOEXPIRE, or an int value)
        String tmp=line.substring(0,line.indexOf('>')).trim();
                int interval = getExpireInterval(tmp);

        int now=(int)(System.currentTimeMillis()/1000);

        // get pool memory cache
        LRUHashtable<String, String> pool=pools.get(poolName);
        if (pool!=null) {
            String value=pool.get(key);
            // Check expiration
            // XXX better to check value==null first...
                        if (value!=null) {
                    try {
                        // get the time from memory
                        Integer tmp2=timepool.get(poolName+key);
                        int then=tmp2.intValue();
                        log.debug("scancache -> file="+then+" now="+now
                                +" now-then="+(now-then)+" interval="+interval);
                        if (((now-then)-interval)<0) {
                            return value;
                        } else {
                                                // Don't handle flasvar
                                                if (key.indexOf(".flashvar")<0) {
                                                        log.debug("get("+poolName+","+key+","+line+"): Request is expired, return old version");
                                                        // RICO signal page-processor
                                                        signalProcessor(sp,key);
                                                        return value;
                                                } else return null;
                        }
                    } catch(Exception e) {}
                        }
        }
        // not in memorycache, get directly from file instead
        fileInfo fileinfo=loadFile(poolName,key);
        if (fileinfo!=null && fileinfo.value!=null) {
            if (((now-fileinfo.time)-interval)>0) {
                                if (key.indexOf(".flashvar")>=0) { // Don't handle flashvar;
                                        return null;
                                }
                 log.debug("get("+poolName+","+key+","+line+"): Diskfile expired for file("+fileinfo.time+"), now("+now+") and interval("+interval+") , return old version ");
                                // RICO signal page-processor
                                signalProcessor(sp,key);
            }
            if (pool==null) {
                // create a new pool
                pool=new LRUHashtable<String, String>(MAX_CACHE_POOL_SIZE);
                pools.put(poolName,pool);
            }
            pool.put(key,fileinfo.value); // store value in the memory cache
            timepool.put(poolName+key,new Integer(fileinfo.time)); // store expiration time
            return fileinfo.value;
        }
        return null;
    }

        /**
         *  getExpireDate.
         * @param poolName
         * @param key
         * @param expireStr
         * @return long
         */
        public long getExpireDate(String poolName, String key, String expireStr) {
                int interval = getExpireInterval(expireStr);
                return getLastModDate(poolName, key) + (interval * 1000);
        }

        /**
         *  getLastModDate.
         * @param poolName
         * @param key
         * @return long
         */
        public long getLastModDate(String poolName, String key) {
                if (timepool.containsKey(poolName+key)) {
                        Integer tmp2=timepool.get(poolName+key);
                        log.debug("scancache last modified in timepool " + (tmp2.intValue()));
                        return ((long)tmp2.intValue()) * 1000;
                }
                log.debug("scancache last modified NOT in timepool");
                return 0; //don't know
        }

        /**
         * getExpireInterval.
         * @param cacheExpire
         * @return int
         */
        private int getExpireInterval(String cacheExpire) {
                int interval;
                if ("".equals(cacheExpire)) {
                         interval = defaultExpireTime;
                }
                else {
                        if ("NOEXPIRE".equals(cacheExpire.toUpperCase())) {
                                interval = Integer.MAX_VALUE;
                        }
                        else {
                                try {
                                interval = Integer.parseInt(cacheExpire);
                        }
                        catch (NumberFormatException n) {
                                log.error("Number format exception for expiration time ("+cacheExpire+")");
                                interval = defaultExpireTime;
                        }
                        }
                }
                log.debug("scancache expire interval: " + interval);
                return interval;
        }

    /**
     * Retrieve a file from disk.
     * This method loads a file from disk and returns the contents as a string.
     * It does not use the memory cache of a poolname, nor does it check for
     * expiration of the cache.
     * Also, it does not perform updates on the memory cache.
     * @param poolname name of the cache pool (expected, but not verified is "PAGE")
     * @param key URL of the page to retrieve
     * @param line cache line options, unspecified
     * @return the page's content as a string, or <code>null</code> if no entry was found
     */
    private String getPage(String poolName, String key) {
        if (status==false) return null;
        fileInfo fileinfo=loadFile(poolName,key);
        if (fileinfo!=null && fileinfo.value!=null) {
            return fileinfo.value;
        } else {
            return null;
        }
    }

    /**
     * Store a file in the indicated pool's cache (both on file system and in the memory cache).
     * Returns the old value if available.
     * When using the "HENK" pool, this method performs a check on expiration using
     * the default expiration time (6 hours).
     * For cache "PAGE", this method does not check expiration, and retrieves the data
     * from the file from disk, NOT from the memory cache.
     * @param poolName name of the cache pool, either "HENK" or "PAGE"
     * @param res response object for retrieving headers (used by mmbase.org?)
     *                    only needed for cachepool "PAGE"
     * @param key URL of the page to store
     * @param value the page content to store
     * @param mimeType the page's mime type, only needed for cachepool "PAGE"
     * @return the page's old content as a string, or <code>null</code> if no entry was found
     *     (i.e. cache was empty or poolName was invalid).
     */
    public String newput(String poolName,HttpServletResponse res, String key,String value, String mimeType) {
        if (status==false) return null;  // no caching if inactive
        LRUHashtable<String, String> pool=pools.get(poolName);
        if (pool==null) {
            // create a new pool
            pool=new LRUHashtable<String, String>();
            pools.put(poolName,pool);
        }
        // insert the new item and save to disk
        if (poolName.equals("HENK")) {
            saveFile(poolName,key,value);
            timepool.put(poolName+key,new Integer((int)(System.currentTimeMillis()/1000))); // store expiration time
            return pool.put(key,value);
        } else if (poolName.equals("PAGE")) {
            saveFile(poolName,key,value);
            // new file for asis support
            int pos=key.indexOf('?');
            String filename=key;
            if (pos!=-1) {
                filename=filename.replace('?',':');
                filename+=".asis";
            } else {
                filename+=":.asis";
            }
            // obtain and add headers
            // ----------------------
            // org.mmbase String body="Status:"+(((worker)res).getWriteHeaders()).substring(8);
            String body=getWriteHeaders(value, mimeType);
            body+=value;
            saveFile(poolName,filename,body);
            // signal to start transfer of file to mirrors
            signalNetFileSrv(filename);
            return pool.put(key,value);
        }
        log.error("newPut("+poolName+",HttpServletRequest,"+key+","+value+"): poolname("+poolName+") is not a valid cache name!");
        return null;
    }

    /**
     * Store a file in the indicated pool's cache (both in file and in the memory cache).
     * Returns the old value if available.
     * Is used in scanpage to recalculate the cached page.
     * @param poolName name of the cache pool, either "HENK" or "PAGE"
     * @param key URL of the page to store
     * @param value the page content to store
     * @param cachetype only needed for cachepool "PAGE".
     *        If 0, no file transfer is performed. Otherwise the {@link NetFileSrv} builder is
     *        invoked to start the VWM that handles the transfer.
     * @param mimeType the page's mime type, only needed for cachepool "PAGE"
     * @return the page's old content as a string, or <code>null</code> if no entry was found
     *     (i.e. cache was empty or poolName was invalid).
     */
    public String newput2(String poolName,String key,String value,int cachetype, String mimeType) {
        if (status==false) return null; // no caching when inactive
        LRUHashtable<String, String> pool=pools.get(poolName);
        if (pool==null) {
            // create a new pool
            pool=new LRUHashtable<String, String>();
            pools.put(poolName,pool);
        }
        log.debug("newput2("+poolName+","+key+","+value+","+cachetype+"): NEWPUT");
        // insert the new item and save to disk
        // XXX (why not call put ?)
        if (poolName.equals("HENK")) {
            saveFile(poolName,key,value);
            // also add time to timepool??
            timepool.put(poolName+key,new Integer((int)(System.currentTimeMillis()/1000))); // store expiration time
            return pool.put(key,value);
        } else if (poolName.equals("PAGE")) {
            saveFile(poolName,key,value);
            // new file for asis support
            // -------------------------
            int pos=key.indexOf('?');
            String filename=key;
            if (pos!=-1) {
                filename=filename.replace('?',':');
                filename+=".asis";
            } else {
                filename+=":.asis";
            }
            // obtain and add headers
            String body=getWriteHeaders(value, mimeType);
            body+=value;
            log.debug("newput2("+poolName+","+key+","+value+","+cachetype+"): NEWPUT=SAVE");
            saveFile(poolName,filename,body);
            if (cachetype!=0) signalNetFileSrv(filename);
            return pool.put(key,value);
        }
        log.error("newput2("+poolName+","+key+","+value+","+cachetype+"): poolName("+poolName+") is not a valid cachetype!");
        return null;
    }

    /**
     * Store a file in the indicated pool's cache (both on file and in the memory cache).
     * Returns the old value if available.
     * @param poolName name of the cache pool
     * @param key URL of the page to store
     * @param value the page content to store
     * @return the page's old content as a string, or <code>null</code> if no entry was found
     */
    public String put(String poolName, String key,String value) {
        if (status==false) return null; // no caching if inactive
        LRUHashtable<String, String> pool=pools.get(poolName);
        if (pool==null) {
            // create a new pool
            pool=new LRUHashtable<String, String>();
            pools.put(poolName,pool);
        }
        // got pool now insert the new item and save to disk
        saveFile(poolName,key,value);

        return pool.put(key,value);
    }


    /**
     * Retrieve a description of the module's function.
     */
    public String getModuleInfo() {
        return "This module provides cache functionality for text pages";
    }

    /**
     * Saves a file to disk.
     * The file is stored under the cache cache root directory, followed by the poolname
     * (HENK or PAGE), followed by the 'original' file name.
     * @param pool The name of the pool
     * @param filename the name of the file
     * @param value the value to store in the file
     */
    private boolean saveFile(String pool,String filename,String value) {
        log.debug("saveFile("+pool+","+filename+",length("+value.length()+" bytes): saving!");
        File sfile = new File(cachepath+pool+filename);
        try {
            DataOutputStream scan = new DataOutputStream(new FileOutputStream(sfile));
            scan.writeBytes(value);
            scan.flush();
            scan.close();
        } catch(Exception e) {
            // make dirs only when an exception occurs... argh
            // e.printStackTrace();
            String dname=cachepath+pool+filename;
            int pos=dname.lastIndexOf('/');
            String dirname=dname.substring(0,pos);
            File file = new File(dirname);
            try {
                if (file.mkdirs()) {
                    DataOutputStream scan = new DataOutputStream(new FileOutputStream(sfile));
                    scan.writeBytes(value);
                    scan.flush();
                    scan.close();
                } else {
                    log.error("scandisk cache -> making "+dirname+" failed ");
                }
            } catch (Exception f) {
                    log.error("scandisk cache -> Saving file "+filename+" failed "+f);
            }
            return false;
        }
        return true;
    }

    /**
     * loads a file from the disk.
     * The file retrieved is stored in the cache root directory, followed by the poolname
     * (HENK or PAGE), followed by the 'original' file name.
     * @param pool The name of the pool
     * @param filename the name of the file
     * @return the content of the file in a {@link fileInfo} object.
     */
    public fileInfo loadFile(String pool,String filename) {
        fileInfo fileinfo=new fileInfo();
        try {
            File sfile = new File(cachepath+pool+filename);
            FileInputStream scan =new FileInputStream(sfile);
            int filesize = (int)sfile.length();
            byte[] buffer=new byte[filesize];
            int len=scan.read(buffer,0,filesize);
            if (len!=-1) {
                String value=new String(buffer,0);
                fileinfo.value=value;
                fileinfo.time=(int)(sfile.lastModified()/1000);
                                log.debug("loadFile last modified " + sfile.lastModified()/1000);
                return fileinfo;
            }
            scan.close();
        } catch(Exception e) {
            // e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * Signal the NetFileServ builder that the .asis file for a page has changed.
     * The builder then searches the responsible VWM that handles the mirrorring of the pages,
     * and activates it.
     * @param filename the .asis filename that changed
     */
    public void signalNetFileSrv(String filename) {
        log.debug("signalNetFileSrv("+filename+"): SIGNAL");
        if (mmbase!=null) {
            NetFileSrv bul=(NetFileSrv)mmbase.getMMObject("netfilesrv");
            if (bul!=null) {
                (bul).fileChange("pages","main",filename);
            }
        } else {
            log.error("signalNetFileSrv("+filename+"): can't use NetFileSrv builder");
        }
    }

    /**
     * Returns the headers for a .asis file to be stored for a "PAGE" cache.
     * @param value page content, used to set the Content-length header.
     * @param mimeType the mimetype of the page. default (if unspecified) is text/html; iso-8859-1
     * @return the page headers as a <code>String</code>
     */
    String getWriteHeaders(String value, String mimeType) {
        if ((mimeType==null) || mimeType.equals("") || mimeType.equals("text/html"))
            mimeType = "text/html; charset=\"iso-8859-1\"";
        String now = RFC1123.makeDate(new Date(System.currentTimeMillis()));
        String expireTime = RFC1123.makeDate(new Date(System.currentTimeMillis()+15000));
        String body="Status: 200 OK\n";
        body+="Server: OrionCache\n";  // orion cache ???
        body+="Content-type: "+mimeType+"\n";
        body+="Content-length: "+value.length()+"\n";
        body+="Expires: "+expireTime+"\n";
        body+="Date: "+now+"\n";
                // Internet explorer refuses to see resulting page as HTML
                // when cache control header added to .asis file
        // body+="Cache-Control: no-cache\n";
        body+="Pragma: no-cache\n";
        body+="Last-Modified: "+now+"\n\n";
        return body;
    }

    /**
     * Removes an entry from the cache pool (both the file on disk and in the memory cache).
     * If the pool is "PAGE", the file will only be removed from the local cache,
     * not from any mirror servers.
     * @param poolName name of cache pool, expected (but not verified) "HENK" or "PAGE"
     * @param key URL of the page to remove
     */
    public void remove(String poolName, String key) {
            File file = new File(cachepath + poolName + key);
            file.delete();
            LRUHashtable pool=pools.get(poolName);
            if (pool!=null) pool.remove(key);
            timepool.remove(poolName + key);
    }

    /**
     * Returns the status of this module.
     * @return <code>true</code> if the module is active, <code>false</code> otherwise
     */
    public boolean getStatus() {
        return status;
    }

        /**
         * This method signals the scanparser to start caclulation on the page
         * given in uri/scanpage, it will duplicate the request as not to interfere
         * with the original request.
         * @param sp The original requests scanpage
         * @param uri of the request
         */
        private void signalProcessor(scanpage sp, String uri) {
                scanpage fakesp=sp.duplicate();
                scanparser.processPage(fakesp,uri);
        }
}
