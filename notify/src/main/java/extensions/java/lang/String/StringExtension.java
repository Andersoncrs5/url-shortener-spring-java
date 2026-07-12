package extensions.java.lang.String;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
public class StringExtension {

    @Extension
    public static boolean isNullOrEmpty(@This String thiz) {
        return thiz == null || thiz.trim().isEmpty();
    }
}