package nl.vpro.redactie.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

public class Field {
    private String name;
    private String type;
    private List<Param> params = new ArrayList<Param>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Param> getParams() {
        return params;
    }

    public void setParams(List<Param> params) {
        this.params = params;
    }

    public void setParam(Param param) {
        params.add(param);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String toString() {
        return new ToStringBuilder("Field").append(name).append(type).append(params).toString();
    }

}
