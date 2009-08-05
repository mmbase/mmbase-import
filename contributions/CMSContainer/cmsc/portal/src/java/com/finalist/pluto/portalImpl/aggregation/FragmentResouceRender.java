package com.finalist.pluto.portalImpl.aggregation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface FragmentResouceRender {

    void render(String resouce,HttpServletRequest request, HttpServletResponse response);

    String getAccceptPrefix();

}
