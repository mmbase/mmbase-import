/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.mmbar.gui;

import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.util.*;
import java.util.jar.*;

import org.mmbase.applications.mmbar.*;
import org.mmbase.applications.mmbar.readtests.*;
import org.mmbase.applications.mmbar.writetests.*;
import org.mmbase.applications.mmbar.mixedtests.*;
import org.mmbase.applications.mmbar.endurancetests.*;

import org.mmbase.bridge.*;
import org.mmbase.bridge.implementation.*;
import org.mmbase.util.logging.*;
import org.mmbase.module.core.*;

/**
 * @author     Daniel Ockeloen
 * @created    February 28, 2005
 */
public class Controller {

    private static Logger log = Logging.getLoggerInstance(Controller.class);
    private static Cloud cloud;
    NodeManager manager;
    CloudContext context;


    /**
     *Constructor for the Controller object
     */
    public Controller() {
        cloud = LocalContext.getCloudContext().getCloud("mmbase");

        // hack needs to be solved
        manager = cloud.getNodeManager("typedef");
        if (manager == null) {
            log.error("Can't access builder typedef");
        }
        context = LocalContext.getCloudContext();
        if (!MMBarManager.isRunning()) {
            MMBarManager.init();
        }
    }


    /**
     *  Description of the Method
     *
     * @param  name  Description of the Parameter
     * @return       Description of the Return Value
     */
    public boolean performWriteTest(String name) {
        if (MMBarManager.performWriteTest(name)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     *  Description of the Method
     *
     * @param  name  Description of the Parameter
     * @return       Description of the Return Value
     */
    public boolean performReadTest(String name) {
        if (MMBarManager.performReadTest(name)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     *  Description of the Method
     *
     * @param  name  Description of the Parameter
     * @return       Description of the Return Value
     */
    public boolean performMixedTest(String name) {
        if (MMBarManager.performMixedTest(name)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     *  Description of the Method
     *
     * @param  name  Description of the Parameter
     * @return       Description of the Return Value
     */
    public boolean performEnduranceTest(String name) {
        if (MMBarManager.performEnduranceTest(name)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     *  Gets the stateInfo attribute of the Controller object
     *
     * @return    The stateInfo value
     */
    public HashMap getStateInfo() {
	HashMap map = new HashMap();
        String state = MMBarManager.getState();
        map.put("state", state);
        if (state.equals("running")) {
            map.put("os", MMBarManager.getOS());
            map.put("cpu", MMBarManager.getCPU());
            map.put("server", MMBarManager.getServer());
            map.put("database", MMBarManager.getDatabase());
            map.put("driver", MMBarManager.getDriver());
            map.put("java", MMBarManager.getJava());
            map.put("runningname", MMBarManager.getRunningName());
            int count = MMBarManager.getRunningCount();
            int pos = MMBarManager.getRunningPos();
            map.put("runningpos", new Integer(pos));
            map.put("runningcount",new Integer(count));
            if (pos == 0) {
                map.put("progressbar", "1");
            } else {
                float bar = (pos / (float) count) * 100;
                map.put("progressbar", "" + bar);
            }
        }
        return map;
    }


    /**
     *  Gets the writeTest attribute of the Controller object
     *
     * @param  name  Description of the Parameter
     * @return       The writeTest value
     */
    public HashMap getWriteTest(String name) {
	HashMap map = new HashMap();
        WriteTest wt = MMBarManager.getWriteTest(name);
        if (wt != null) {
            map.put("os", MMBarManager.getOS());
            map.put("cpu", MMBarManager.getCPU());
            map.put("server", MMBarManager.getServer());
            map.put("database", MMBarManager.getDatabase());
            map.put("driver", MMBarManager.getDriver());
            map.put("java", MMBarManager.getJava());
            map.put("name", wt.getName());
            map.put("description", wt.getDescription());
            map.put("state", wt.getState());
            map.put("result", getFormattedResult(wt.getResult()));
            map.put("resulttype", wt.getResultType());
            map.put("count", new Integer(wt.getCount()));
            map.put("currentpos", new Integer(wt.getCurrentPos()));
        }
        return map;
    }


    /**
     *  Gets the mixedTest attribute of the Controller object
     *
     * @param  name  Description of the Parameter
     * @return       The mixedTest value
     */
    public HashMap getMixedTest(String name) {
	HashMap map = new HashMap();
        MixedTest mt = MMBarManager.getMixedTest(name);
        if (mt != null) {
            map.put("os", MMBarManager.getOS());
            map.put("cpu", MMBarManager.getCPU());
            map.put("server", MMBarManager.getServer());
            map.put("database", MMBarManager.getDatabase());
            map.put("driver", MMBarManager.getDriver());
            map.put("java", MMBarManager.getJava());
            map.put("name", mt.getName());
            map.put("description", mt.getDescription());
            map.put("state", mt.getState());
            map.put("result", getFormattedResult(mt.getResult()));
            map.put("resulttype", mt.getResultType());
            map.put("count",new Integer(mt.getCount()));
            map.put("currentpos",new Integer(mt.getCurrentPos()));
        }
        return map;
    }


    /**
     *  Gets the enduranceTest attribute of the Controller object
     *
     * @param  name  Description of the Parameter
     * @return       The enduranceTest value
     */
    public HashMap getEnduranceTest(String name) {
	HashMap map = new HashMap();
        EnduranceTest et = MMBarManager.getEnduranceTest(name);
        if (et != null) {
            map.put("os", MMBarManager.getOS());
            map.put("cpu", MMBarManager.getCPU());
            map.put("server", MMBarManager.getServer());
            map.put("database", MMBarManager.getDatabase());
            map.put("driver", MMBarManager.getDriver());
            map.put("java", MMBarManager.getJava());
            map.put("name", et.getName());
            map.put("description", et.getDescription());
            map.put("state", et.getState());
            map.put("result", getFormattedResult(et.getResult()));
            map.put("resulttype", et.getResultType());
            map.put("count", new Integer(et.getCount()));
            map.put("currentpos",new Integer(et.getCurrentPos()));
        }
        return map;
    }


    /**
     *  Gets the readTest attribute of the Controller object
     *
     * @param  name  Description of the Parameter
     * @return       The readTest value
     */
    public HashMap getReadTest(String name) {
	HashMap map = new HashMap();
        ReadTest rt = MMBarManager.getReadTest(name);
        if (rt != null) {
            map.put("os", MMBarManager.getOS());
            map.put("cpu", MMBarManager.getCPU());
            map.put("server", MMBarManager.getServer());
            map.put("database", MMBarManager.getDatabase());
            map.put("driver", MMBarManager.getDriver());
            map.put("java", MMBarManager.getJava());
            map.put("name", rt.getName());
            map.put("description", rt.getDescription());
            map.put("state", rt.getState());
            map.put("result", getFormattedResult(rt.getResult()));
            map.put("resulttype", rt.getResultType());
            map.put("count", new Integer(rt.getCount()));
            map.put("currentpos", new Integer(rt.getCurrentPos()));
        }
        return map;
    }


    /**
     *  Gets the writeTests attribute of the Controller object
     *
     * @return    The writeTests value
     */
    public List getWriteTests() {
        List list = new ArrayList();
        try {
            for (Iterator i = MMBarManager.getWriteTests(); i.hasNext(); ) {
		HashMap map = new HashMap();
                WriteTest wt = (WriteTest) i.next();
                map.put("name", wt.getName());
                map.put("state", wt.getState());
                map.put("action", wt.getAction());
                map.put("result", getFormattedResult(wt.getResult()));
                map.put("resulttype", wt.getResultType());
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     *  Gets the mixedTests attribute of the Controller object
     *
     * @return    The mixedTests value
     */
    public List getMixedTests() {
        List list = new ArrayList();
        try {
            for (Iterator i = MMBarManager.getMixedTests(); i.hasNext(); ) {
		HashMap map = new HashMap();
                MixedTest mt = (MixedTest) i.next();
                map.put("name", mt.getName());
                map.put("state", mt.getState());
                map.put("action", mt.getAction());
                map.put("result", getFormattedResult(mt.getResult()));
                map.put("resulttype", mt.getResultType());
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     *  Gets the enduranceTests attribute of the Controller object
     *
     * @return    The enduranceTests value
     */
    public List getEnduranceTests() {
        List list = new ArrayList();
        try {
            for (Iterator i = MMBarManager.getEnduranceTests(); i.hasNext(); ) {
		HashMap map = new HashMap();
                EnduranceTest et = (EnduranceTest) i.next();
                map.put("name", et.getName());
                map.put("state", et.getState());
                map.put("action", et.getAction());
                map.put("result", getFormattedResult(et.getResult()));
                map.put("resulttype", et.getResultType());
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     *  Gets the readTests attribute of the Controller object
     *
     * @return    The readTests value
     */
    public List getReadTests() {
        List list = new ArrayList();
        try {
            for (Iterator i = MMBarManager.getReadTests(); i.hasNext(); ) {
		HashMap map = new HashMap();
                ReadTest rt = (ReadTest) i.next();
                map.put("name", rt.getName());
                map.put("state", rt.getState());
                map.put("action", rt.getAction());
                map.put("result", getFormattedResult(rt.getResult()));
                map.put("resulttype", rt.getResultType());
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     *  Gets the writeTestBenchmarks attribute of the Controller object
     *
     * @param  name  Description of the Parameter
     * @return       The writeTestBenchmarks value
     */
    public List getWriteTestBenchmarks(String name) {
        List list = new ArrayList();
        WriteTest wt = MMBarManager.getWriteTest(name);
        if (wt != null) {
            try {
                for (Iterator i = wt.getBenchmarks(); i.hasNext(); ) {
		    HashMap map = new HashMap();
                    Benchmark bm = (Benchmark) i.next();
                    map.put("result", "" + bm.getResult());
                    map.put("os", bm.getOS());
                    map.put("cpu", bm.getCPU());
                    map.put("server", bm.getServer());
                    map.put("database", bm.getDatabase());
                    map.put("driver", bm.getDriver());
                    map.put("java", bm.getJava());
                    list.add(map);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }


    /**
     *  Gets the mixedTestBenchmarks attribute of the Controller object
     *
     * @param  name  Description of the Parameter
     * @return       The mixedTestBenchmarks value
     */
    public List getMixedTestBenchmarks(String name) {
        List list = new ArrayList();
        MixedTest mt = MMBarManager.getMixedTest(name);
        if (mt != null) {
            try {
                for (Iterator i = mt.getBenchmarks(); i.hasNext(); ) {
		    HashMap map = new HashMap();
                    Benchmark bm = (Benchmark) i.next();
                    map.put("result", "" + bm.getResult());
                    map.put("os", bm.getOS());
                    map.put("cpu", bm.getCPU());
                    map.put("server", bm.getServer());
                    map.put("database", bm.getDatabase());
                    map.put("driver", bm.getDriver());
                    map.put("java", bm.getJava());
                    list.add(map);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }


    /**
     *  Gets the enduranceTestBenchmarks attribute of the Controller object
     *
     * @param  name  Description of the Parameter
     * @return       The enduranceTestBenchmarks value
     */
    public List getEnduranceTestBenchmarks(String name) {
        List list = new ArrayList();
        EnduranceTest et = MMBarManager.getEnduranceTest(name);
        if (et != null) {
            try {
                for (Iterator i = et.getBenchmarks(); i.hasNext(); ) {
		    HashMap map = new HashMap();
                    Benchmark bm = (Benchmark) i.next();
                    map.put("result", "" + bm.getResult());
                    map.put("os", bm.getOS());
                    map.put("cpu", bm.getCPU());
                    map.put("server", bm.getServer());
                    map.put("database", bm.getDatabase());
                    map.put("driver", bm.getDriver());
                    map.put("java", bm.getJava());
                    list.add(map);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }


    /**
     *  Gets the readTestBenchmarks attribute of the Controller object
     *
     * @param  name  Description of the Parameter
     * @return       The readTestBenchmarks value
     */
    public List getReadTestBenchmarks(String name) {
        List list = new ArrayList();
        ReadTest rt = MMBarManager.getReadTest(name);
        if (rt != null) {
            try {
                for (Iterator i = rt.getBenchmarks(); i.hasNext(); ) {
		    HashMap map = new HashMap();
                    Benchmark bm = (Benchmark) i.next();
                    map.put("result", "" + bm.getResult());
                    map.put("os", bm.getOS());
                    map.put("cpu", bm.getCPU());
                    map.put("server", bm.getServer());
                    map.put("database", bm.getDatabase());
                    map.put("driver", bm.getDriver());
                    map.put("java", bm.getJava());
                    list.add(map);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }


    /**
     *  Gets the formattedResult attribute of the Controller object
     *
     * @param  wtr  Description of the Parameter
     * @return      The formattedResult value
     */
    private String getFormattedResult(float wtr) {
        String wts = "";
        if (wtr > 1000000) {
            wtr = wtr / 1000000;
            wts += wtr;
            int pos = wts.indexOf('.');
            if (pos != -1) {
                try {
                    wts = wts.substring(0, pos + 3) + "M";
                } catch (Exception h) {}
            }
        } else {
            wts += wtr;
            int pos = wts.indexOf('.');
            if (pos != -1) {
                try {
                    wts = wts.substring(0, pos + 3);
                } catch (Exception h) {}
            }
        }
        return wts;
    }

}

