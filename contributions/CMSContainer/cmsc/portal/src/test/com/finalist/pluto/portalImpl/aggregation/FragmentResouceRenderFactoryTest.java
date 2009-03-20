package com.finalist.pluto.portalImpl.aggregation;

import junit.framework.TestCase;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FragmentResouceRenderFactoryTest extends TestCase {


    public void testGetRender(){
        assertNull(FragmentResouceRenderFactory.getRender("test.jsp"));

        FragmentResouceRender render = new TestRender();
        FragmentResouceRenderFactory.registerRender(render);

        assertNotNull(FragmentResouceRenderFactory.getRender("test:hellotiles"));
        assertNull(FragmentResouceRenderFactory.getRender("test.jsp"));
    }

    private class TestRender implements FragmentResouceRender {
        public void render(String resouce,HttpServletRequest request, HttpServletResponse response) {
            
        }

        public String getAccceptPrefix() {
            return "test";
        }

    }
}
