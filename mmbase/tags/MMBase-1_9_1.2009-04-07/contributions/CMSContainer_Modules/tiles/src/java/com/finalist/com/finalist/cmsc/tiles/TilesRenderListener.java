package com.finalist.com.finalist.cmsc.tiles;

import com.finalist.pluto.portalImpl.aggregation.FragmentResouceRenderFactory;
import com.finalist.pluto.portalImpl.aggregation.TilesRender;

import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;

public class TilesRenderListener implements ServletContextListener {


    /* Application Startup Event */
    public void contextInitialized(ServletContextEvent ce) {
        FragmentResouceRenderFactory.registerRender(new TilesRender());
    }

    /* Application Shutdown	Event */
    public void contextDestroyed(ServletContextEvent ce) {
    }
}