package com.finalist.pluto.portalImpl.aggregation;

import java.util.Map;
import java.util.HashMap;

public class FragmentResouceRenderFactory {


    private static Map<String, FragmentResouceRender> renders = new HashMap<String, FragmentResouceRender>();

    public static void registerRender(FragmentResouceRender render) {
        renders.put(render.getAccceptPrefix(), render);

    }

    public static FragmentResouceRender getRender(String resource) {
        return renders.get(getPrefix(resource));
    }


    private static String getPrefix(String resource) {
        if (resource.indexOf(":") < 0) return null;

        return resource.substring(0, resource.indexOf(":"));
    }

    

}
