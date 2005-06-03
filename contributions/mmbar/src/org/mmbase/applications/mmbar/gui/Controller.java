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
        if (!PerformanceTestsManager.isRunning()) {
            PerformanceTestsManager.init();
        }
    }


    /**
     *  Description of the Method
     *
     * @param  name  Description of the Parameter
     * @return       Description of the Return Value
     */
    public boolean performWriteTest(String name) {
        if (PerformanceTestsManager.performWriteTest(name)) {
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
        if (PerformanceTestsManager.performReadTest(name)) {
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
        if (PerformanceTestsManager.performMixedTest(name)) {
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
        if (PerformanceTestsManager.performEnduranceTest(name)) {
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
    public MMObjectNode getStateInfo() {
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        MMObjectNode virtual = builder.getNewNode("admin");
        String state = PerformanceTestsManager.getState();
        virtual.setValue("state", state);
        if (state.equals("running")) {
            virtual.setValue("os", PerformanceTestsManager.getOS());
            virtual.setValue("cpu", PerformanceTestsManager.getCPU());
            virtual.setValue("server", PerformanceTestsManager.getServer());
            virtual.setValue("database", PerformanceTestsManager.getDatabase());
            virtual.setValue("driver", PerformanceTestsManager.getDriver());
            virtual.setValue("java", PerformanceTestsManager.getJava());
            virtual.setValue("runningname", PerformanceTestsManager.getRunningName());
            int count = PerformanceTestsManager.getRunningCount();
            int pos = PerformanceTestsManager.getRunningPos();
            virtual.setValue("runningpos", pos);
            virtual.setValue("runningcount", count);
            if (pos == 0) {
                virtual.setValue("progressbar", "1");
            } else {
                float bar = (pos / (float) count) * 100;
                virtual.setValue("progressbar", "" + bar);
            }
        }
        return virtual;
    }


    /**
     *  Gets the writeTest attribute of the Controller object
     *
     * @param  name  Description of the Parameter
     * @return       The writeTest value
     */
    public MMObjectNode getWriteTest(String name) {
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        MMObjectNode virtual = builder.getNewNode("admin");
        WriteTest wt = PerformanceTestsManager.getWriteTest(name);
        if (wt != null) {
            virtual.setValue("os", PerformanceTestsManager.getOS());
            virtual.setValue("cpu", PerformanceTestsManager.getCPU());
            virtual.setValue("server", PerformanceTestsManager.getServer());
            virtual.setValue("database", PerformanceTestsManager.getDatabase());
            virtual.setValue("driver", PerformanceTestsManager.getDriver());
            virtual.setValue("java", PerformanceTestsManager.getJava());
            virtual.setValue("name", wt.getName());
            virtual.setValue("description", wt.getDescription());
            virtual.setValue("state", wt.getState());
            virtual.setValue("result", getFormattedResult(wt.getResult()));
            virtual.setValue("resulttype", wt.getResultType());
            virtual.setValue("count", wt.getCount());
            virtual.setValue("currentpos", wt.getCurrentPos());
        }
        return virtual;
    }


    /**
     *  Gets the mixedTest attribute of the Controller object
     *
     * @param  name  Description of the Parameter
     * @return       The mixedTest value
     */
    public MMObjectNode getMixedTest(String name) {
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        MMObjectNode virtual = builder.getNewNode("admin");
        MixedTest mt = PerformanceTestsManager.getMixedTest(name);
        if (mt != null) {
            virtual.setValue("os", PerformanceTestsManager.getOS());
            virtual.setValue("cpu", PerformanceTestsManager.getCPU());
            virtual.setValue("server", PerformanceTestsManager.getServer());
            virtual.setValue("database", PerformanceTestsManager.getDatabase());
            virtual.setValue("driver", PerformanceTestsManager.getDriver());
            virtual.setValue("java", PerformanceTestsManager.getJava());
            virtual.setValue("name", mt.getName());
            virtual.setValue("description", mt.getDescription());
            virtual.setValue("state", mt.getState());
            virtual.setValue("result", getFormattedResult(mt.getResult()));
            virtual.setValue("resulttype", mt.getResultType());
            virtual.setValue("count", mt.getCount());
            virtual.setValue("currentpos", mt.getCurrentPos());
        }
        return virtual;
    }


    /**
     *  Gets the enduranceTest attribute of the Controller object
     *
     * @param  name  Description of the Parameter
     * @return       The enduranceTest value
     */
    public MMObjectNode getEnduranceTest(String name) {
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        MMObjectNode virtual = builder.getNewNode("admin");
        EnduranceTest et = PerformanceTestsManager.getEnduranceTest(name);
        if (et != null) {
            virtual.setValue("os", PerformanceTestsManager.getOS());
            virtual.setValue("cpu", PerformanceTestsManager.getCPU());
            virtual.setValue("server", PerformanceTestsManager.getServer());
            virtual.setValue("database", PerformanceTestsManager.getDatabase());
            virtual.setValue("driver", PerformanceTestsManager.getDriver());
            virtual.setValue("java", PerformanceTestsManager.getJava());
            virtual.setValue("name", et.getName());
            virtual.setValue("description", et.getDescription());
            virtual.setValue("state", et.getState());
            virtual.setValue("result", getFormattedResult(et.getResult()));
            virtual.setValue("resulttype", et.getResultType());
            virtual.setValue("count", et.getCount());
            virtual.setValue("currentpos", et.getCurrentPos());
        }
        return virtual;
    }


    /**
     *  Gets the readTest attribute of the Controller object
     *
     * @param  name  Description of the Parameter
     * @return       The readTest value
     */
    public MMObjectNode getReadTest(String name) {
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        MMObjectNode virtual = builder.getNewNode("admin");
        ReadTest rt = PerformanceTestsManager.getReadTest(name);
        if (rt != null) {
            virtual.setValue("os", PerformanceTestsManager.getOS());
            virtual.setValue("cpu", PerformanceTestsManager.getCPU());
            virtual.setValue("server", PerformanceTestsManager.getServer());
            virtual.setValue("database", PerformanceTestsManager.getDatabase());
            virtual.setValue("driver", PerformanceTestsManager.getDriver());
            virtual.setValue("java", PerformanceTestsManager.getJava());
            virtual.setValue("name", rt.getName());
            virtual.setValue("description", rt.getDescription());
            virtual.setValue("state", rt.getState());
            virtual.setValue("result", getFormattedResult(rt.getResult()));
            virtual.setValue("resulttype", rt.getResultType());
            virtual.setValue("count", rt.getCount());
            virtual.setValue("currentpos", rt.getCurrentPos());
        }
        return virtual;
    }


    /**
     *  Gets the writeTests attribute of the Controller object
     *
     * @return    The writeTests value
     */
    public List getWriteTests() {
        List list = new ArrayList();
        try {
            VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

            for (Iterator i = PerformanceTestsManager.getWriteTests(); i.hasNext(); ) {
                MMObjectNode virtual = builder.getNewNode("admin");
                WriteTest wt = (WriteTest) i.next();
                virtual.setValue("name", wt.getName());
                virtual.setValue("state", wt.getState());
                virtual.setValue("action", wt.getAction());
                virtual.setValue("result", getFormattedResult(wt.getResult()));
                virtual.setValue("resulttype", wt.getResultType());
                list.add(virtual);
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
            VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

            for (Iterator i = PerformanceTestsManager.getMixedTests(); i.hasNext(); ) {
                MMObjectNode virtual = builder.getNewNode("admin");
                MixedTest mt = (MixedTest) i.next();
                virtual.setValue("name", mt.getName());
                virtual.setValue("state", mt.getState());
                virtual.setValue("action", mt.getAction());
                virtual.setValue("result", getFormattedResult(mt.getResult()));
                virtual.setValue("resulttype", mt.getResultType());
                list.add(virtual);
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
            VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

            for (Iterator i = PerformanceTestsManager.getEnduranceTests(); i.hasNext(); ) {
                MMObjectNode virtual = builder.getNewNode("admin");
                EnduranceTest et = (EnduranceTest) i.next();
                virtual.setValue("name", et.getName());
                virtual.setValue("state", et.getState());
                virtual.setValue("action", et.getAction());
                virtual.setValue("result", getFormattedResult(et.getResult()));
                virtual.setValue("resulttype", et.getResultType());
                list.add(virtual);
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
            VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

            for (Iterator i = PerformanceTestsManager.getReadTests(); i.hasNext(); ) {
                MMObjectNode virtual = builder.getNewNode("admin");
                ReadTest rt = (ReadTest) i.next();
                virtual.setValue("name", rt.getName());
                virtual.setValue("state", rt.getState());
                virtual.setValue("action", rt.getAction());
                virtual.setValue("result", getFormattedResult(rt.getResult()));
                virtual.setValue("resulttype", rt.getResultType());
                list.add(virtual);
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
        WriteTest wt = PerformanceTestsManager.getWriteTest(name);
        if (wt != null) {
            try {
                VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

                for (Iterator i = wt.getBenchmarks(); i.hasNext(); ) {
                    MMObjectNode virtual = builder.getNewNode("admin");
                    Benchmark bm = (Benchmark) i.next();
                    virtual.setValue("result", "" + bm.getResult());
                    virtual.setValue("os", bm.getOS());
                    virtual.setValue("cpu", bm.getCPU());
                    virtual.setValue("server", bm.getServer());
                    virtual.setValue("database", bm.getDatabase());
                    virtual.setValue("driver", bm.getDriver());
                    virtual.setValue("java", bm.getJava());
                    list.add(virtual);
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
        MixedTest mt = PerformanceTestsManager.getMixedTest(name);
        if (mt != null) {
            try {
                VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

                for (Iterator i = mt.getBenchmarks(); i.hasNext(); ) {
                    MMObjectNode virtual = builder.getNewNode("admin");
                    Benchmark bm = (Benchmark) i.next();
                    virtual.setValue("result", "" + bm.getResult());
                    virtual.setValue("os", bm.getOS());
                    virtual.setValue("cpu", bm.getCPU());
                    virtual.setValue("server", bm.getServer());
                    virtual.setValue("database", bm.getDatabase());
                    virtual.setValue("driver", bm.getDriver());
                    virtual.setValue("java", bm.getJava());
                    list.add(virtual);
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
        EnduranceTest et = PerformanceTestsManager.getEnduranceTest(name);
        if (et != null) {
            try {
                VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

                for (Iterator i = et.getBenchmarks(); i.hasNext(); ) {
                    MMObjectNode virtual = builder.getNewNode("admin");
                    Benchmark bm = (Benchmark) i.next();
                    virtual.setValue("result", "" + bm.getResult());
                    virtual.setValue("os", bm.getOS());
                    virtual.setValue("cpu", bm.getCPU());
                    virtual.setValue("server", bm.getServer());
                    virtual.setValue("database", bm.getDatabase());
                    virtual.setValue("driver", bm.getDriver());
                    virtual.setValue("java", bm.getJava());
                    list.add(virtual);
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
        ReadTest rt = PerformanceTestsManager.getReadTest(name);
        if (rt != null) {
            try {
                VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

                for (Iterator i = rt.getBenchmarks(); i.hasNext(); ) {
                    MMObjectNode virtual = builder.getNewNode("admin");
                    Benchmark bm = (Benchmark) i.next();
                    virtual.setValue("result", "" + bm.getResult());
                    virtual.setValue("os", bm.getOS());
                    virtual.setValue("cpu", bm.getCPU());
                    virtual.setValue("server", bm.getServer());
                    virtual.setValue("database", bm.getDatabase());
                    virtual.setValue("driver", bm.getDriver());
                    virtual.setValue("java", bm.getJava());
                    list.add(virtual);
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

