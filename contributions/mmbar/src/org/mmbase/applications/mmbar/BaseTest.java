/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.mmbar;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.util.*;
import org.mmbase.bridge.*;
import org.mmbase.cache.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * @author     Daniel Ockeloen
 * @created    February 28, 2005
 */
public class BaseTest implements Runnable {

    // logger
    private static Logger log = Logging.getLoggerInstance(BaseTest.class);

    // name of the test
    String name;

    // description of the test
    String description;

    // action (gui) string
    String action, oldaction;

    // result score 
    float result;

    // resulttype
    String resulttype = "mmops";

    // state of the test (gui)
    String state = "waiting";

    // count of how many times we want to run it
    private int count = 1;

    // properties
    private HashMap properties =  new HashMap();


    // current position if the test is running
    public int currentpos = 0;

    // number of threads we want this test run in
    private int threads = 1;

    private int runcounter = 0;

    private long starttime = 0;

    private long endtime = 0;

    Cache cache = null;

    boolean oldcachemode = true;

    // benchmarks defined for the test
    private ArrayList benchmarks = new ArrayList();


    /**
     * construct the test
     */
    public BaseTest() { }


    /**
     * create a new Benchmark
     *
     * @return   the new benchmark 
     */
    public Benchmark getNewBenchmark() {
        Benchmark bm = new Benchmark();
        benchmarks.add(bm);
        return bm;
    }


    /**
     * get the defined benchmarks
     *
     * @return   iterator of the benchmarks
     */
    public Iterator getBenchmarks() {
        return benchmarks.iterator();
    }


    /**
     * get the name of the test
     *
     * @return    name of the test
     */
    public String getName() {
        return name;
    }


    /**
     * set the name of the test
     *
     * @param  name  name of the test
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     *  Sets the description attribute of the BaseTest object
     *
     * @param  description  The new description value
     */
    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * get the description of the test
     *
     * @return   description of the test
     */
    public String getDescription() {
	if (description==null || description.equals("null")) {
		return "";
	}
        return description;
    }


    /**
     *  Gets the action attribute of the BaseTest object
     *
     * @return    action string of the test
     */
    public String getAction() {
        return action;
    }


    /**
     *  Sets the action attribute of the BaseTest object
     *
     * @param  action  The new action value
     */
    public void setAction(String action) {
        this.action = action;
    }


    /**
     *  Gets the count attribute of the BaseTest object
     *
     * @return    The count value
     */
    public int getCount() {
        return count;
    }


    /**
     *  Gets the currentPos attribute of the BaseTest object
     *
     * @return    The currentPos value
     */
    public int getCurrentPos() {
        return currentpos;
    }


    /**
     *  Sets the count attribute of the BaseTest object
     *
     * @param  count  The new count value
     */
    public void setCount(int count) {
        this.count = count;
    }


    /**
     *  Gets the threads attribute of the BaseTest object
     *
     * @return    The threads value
     */
    public int getThreads() {
        return threads;
    }

    /**
     *  Sets the threads attribute of the BaseTest object
     *
     * @param  threads  The new threads value
     */
    public void setThreads(int threads) {
        this.threads = threads;
    }


    /**
     *  Gets the state attribute of the BaseTest object
     *
     * @return    The state value
     */
    public String getState() {
        return state;
    }


    /**
     *  Sets the state attribute of the BaseTest object
     *
     * @param  state  The new state value
     */
    public void setState(String state) {
        this.state = state;
    }


    /**
     *  Gets the result attribute of the BaseTest object
     *
     * @return    The result value
     */
    public float getResult() {
        return result;
    }


    /**
     * Sets the result value
     *
     * @param  result The result of the test (score)
     */
    public void setResult(float result) {
        this.result = result;
    }


    /**
     *  Sets the result based on count and time
     *
     * @param  count  The total test count
     * @param  time   Time it took to complete
     */
    public void setResult(int count, long time) {
        this.result = (float) count / ((time / (float) 1000));
    }


    /**
     *  get the resulttype
     *
     * @return   type of the result 
     */
    public String getResultType() {
        return resulttype;
    }


    /**
     *  Sets the resultType attribute of the BaseTest object
     *
     * @param  resulttype  The new resultType value
     */
    public void setResultType(String resulttype) {
        this.resulttype = resulttype;
    }


    /**
     *  method is called by the manager if a test is started
     *  it starts a new thread to run the test in. The run() this
     *  threads takes over the rest of the action.
     *
     * @return  true if test thread is started, false it fails
     */
    public boolean perform() {

	// signal the manager we are about to run and make this the running test
        MMBarManager.setRunningTest(this);

	// change our own state to running (gui)
        setState("Running");

	// remember our own action button value (gui)
        oldaction = getAction();

	// set the action button to 'stop' (gui)
        setAction("stop");

	// start the performance threads

	// set the run counter to zero
	runcounter = 0;

	// first init the test
	initTest();

	 // so we want cache on or not ?
	 String cachemode =  getProperty("cache");
	 if (cachemode!=null && cachemode.equals("off")) {
             cache = NodeCache.getCache();
	     oldcachemode = cache.isActive();
             cache.setActive(false);
	 }

	for (int i=0;i<threads;i++) {
        	Thread kicker = new Thread(this, "mmbar thread ("+i+") : " + this);
        	kicker.setDaemon(true);
        	kicker.start();
		runcounter = runcounter + 1;
		starttime = System.currentTimeMillis();
	}

        return true;
    }


    /**
     *  run(), handles the real test. It calls testRun() method and
     *  provides error catching
     */
    public void run() {
        try {
	    // call the test method itself, this will mostly be overridden
            testRun();

	    runcounter = runcounter - 1;

	    // if we are the last one running the rest is over
	    if (runcounter == 0) { 
		endtime = System.currentTimeMillis();
                long endtime = System.currentTimeMillis();
                setResult(getCount(), endtime - starttime);

	        // return the action to old state (gui)
                setAction(oldaction);

	        // set out own state to finished (gui)
                setState("finished");


	        if (cache!=null) cache.setActive(oldcachemode);

	        // clear the running test in the manager
                MMBarManager.cleanRunningTest();

	        // reset the current position count to 0;
		currentpos = 0;
            }
        } catch (Exception e) {
            log.info("Error inside the test run : " + this);
            e.printStackTrace();
        }
    }


    /**
     *  main test method, this in extended classes should hold the basic test
     */
    public void testRun() {
        log.error("testRun not implemented, should be overridden : " + this);
    }



    public int getURLBytes(URL url) {
      int totallen = 0;
      try {
    	 HttpURLConnection connection = (HttpURLConnection) url.openConnection();
         BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
         int buffersize = 64*1024;
         byte[] buffer = new byte[buffersize];

         int len;
         while ((len = in.read(buffer, 0, buffersize)) != -1) {
     		totallen+=len;
         }
      } catch(Exception e) {
	log.error("Error while reading http page : "+url);
	e.printStackTrace();
      }
      return totallen;
    }
  
    public void setProperty(String name,String value) {
	properties.put(name,value);
    }

    public String getProperty(String name) {
	Object o = properties.get(name);
	if (o != null) return (String)o;
	return null;
    }

    public boolean savebenchmark() {
	Benchmark bm = getNewBenchmark();
	bm.setResult(getResult());
	bm.setOS(MMBarManager.getOS());
	bm.setCPU(MMBarManager.getCPU());
	bm.setServer(MMBarManager.getServer());
	bm.setDatabase(MMBarManager.getDatabase());
	bm.setDriver(MMBarManager.getDriver());
	bm.setJava(MMBarManager.getJava());
	return true;
    }


    public boolean deletebenchmark(int pos) {
	benchmarks.remove(pos-1);
	return true;
     }

     public void initTest() {
     }

}

