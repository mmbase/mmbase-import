package org.mmbase.security.implementation;

import java.util.HashMap;

public interface LoginModule {
    public void load(HashMap properties);
    public boolean login(NameContext user, HashMap loginInfo,  Object[] parameters);
}
