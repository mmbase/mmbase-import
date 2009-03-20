package com.finalist.pluto.portalImpl.aggregation;

import org.apache.tiles.TilesContainer;
import org.apache.tiles.servlet.context.ServletUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TilesRender implements FragmentResouceRender {

    public void render(String resouce,HttpServletRequest request, HttpServletResponse response) {

        System.out.println("-----=======++++++++++++");

        TilesContainer container = ServletUtil.getContainer(request.getSession().getServletContext());


        container.render(resouce.replaceFirst("tiles:","").trim(), request, response);
    }

    public String getAccceptPrefix() {
        return "tiles";
    }


}
