package nl.vpro.redactie.config;

import org.apache.commons.lang.builder.ToStringBuilder;

public class PathElement {

    private String name;
    private String url;
    private String node;
    private String globalUrl;

    /**
     * give a comma-separated list of the following values:
     * <ul>
     * <li>name
     * <li>url
     * <li>node
     * <li>globalUrl
     * </ul>
     */
    public void setAll(String values) {
        String[] v = values.split(",");
        if (v.length != 4) {
            throw new RuntimeException("setting all values on a PathElement, you must have 4 values and not " + v.length
                    + "Add '*' characters for empty values");
        }
        if (!"*".equals(v[0])) setName(v[0]);
        if (!"*".equals(v[1])) setUrl(v[1]);
        if (!"*".equals(v[2])) setNode(v[2]);
        if (!"*".equals(v[3])) setGlobalUrl(v[3]);

    }

    public String getGlobalUrl() {
        return globalUrl;
    }

    public void setGlobalUrl(String globalUrl) {
        this.globalUrl = globalUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String toString() {
        return new ToStringBuilder(this).append(name).append(url).append(globalUrl).append(node).toString();
    }
}
