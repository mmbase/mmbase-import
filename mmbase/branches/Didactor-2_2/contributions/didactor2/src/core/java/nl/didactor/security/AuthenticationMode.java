package nl.didactor.security;

import org.mmbase.module.core.*;

public class AuthenticationMode {

    private String name = null;

    private MMObjectNode user = null;

    public AuthenticationMode() {
    }

    public AuthenticationMode(String name, MMObjectNode user) {
        this.name = name;
        this.user = user;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUser(MMObjectNode user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public MMObjectNode getUser() {
        return user;
    }

}
