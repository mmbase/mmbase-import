package org.mmbase.security.implementation.context;

import org.mmbase.security.Rank;
import java.util.HashMap;

public class AnonymousLogin extends ContextLoginModule {
    public ContextUserContext login(HashMap userLoginInfo, Object[] userParameters) throws org.mmbase.security.SecurityException {
        return getValidUserContext("anonymous", Rank.ANONYMOUS);
    }
}
