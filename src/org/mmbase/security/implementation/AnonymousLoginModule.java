package org.mmbase.security.implementation;

import java.util.HashMap;

import org.mmbase.security.implementation.LoginModule;
import org.mmbase.security.UserContext;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class AnonymousLoginModule implements LoginModule {
    private static Logger log=Logging.getLoggerInstance(AnonymousLoginModule.class.getName());

    public void load(HashMap properties) {
        // nah do nothing..
    }

    public boolean login(NameContext user, HashMap loginInfo,  Object[] parameters) {
        log.info("anonymous login..");

        // set the identifier...
        user.setIdentifier("anonymous");
        return true;
    }
}
