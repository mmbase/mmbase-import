package org.mmbase.bridge.implementation;

/**
 * The resolver recognizing only the string 'local', which then returns {@link LocalContext#getCloudContext}.
 *
 * @since MMBase-2.0
 */

public class LocalResolver extends ContextProvider.Resolver {
    @Override
    public CloudContext resolve(String uri) {
        if (uri.equals("local")){
            return org.mmbase.bridge.LocalContext.getCloudContext();
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
