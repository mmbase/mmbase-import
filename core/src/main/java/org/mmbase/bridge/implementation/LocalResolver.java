package org.mmbase.bridge.implementation;
import org.mmbase.bridge.*;
/**
 * The resolver recognizing only the string 'local', which then returns {@link LocalContext#getCloudContext}.
 *
 * @since MMBase-2.0
 */

public class LocalResolver extends ContextProvider.Resolver {
    @Override
    public CloudContext resolve(String uri) {
        if (uri.equals("local")){
            return LocalContext.getCloudContext();
        } else {
            return null;
        }
    }
    @Override
    public boolean equals(Object o) {
        return o != null && o instanceof LocalResolver;
    }
    @Override
    public String toString() {
        return "local";
    }
}
