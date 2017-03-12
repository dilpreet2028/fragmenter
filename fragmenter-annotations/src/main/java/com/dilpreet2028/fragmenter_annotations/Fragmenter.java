package com.dilpreet2028.fragmenter_annotations;

/**
 * Created by dilpreet on 12/3/17.
 */

public class Fragmenter {
    private static Injector injector;
    private static final String MAPPING_PACKAGE = "com.dilpreet2028.fragmenter";
    private static final String MAPPING_CLASS = "FieldInjector";

    public static void inject(Object fragment) {
        try {
            Class<?> clazz = Class.forName(MAPPING_PACKAGE+"."+MAPPING_CLASS);
            injector = (Injector) clazz.newInstance();
        } catch (Exception e){

        }

        if(injector != null) {
            injector.inject(fragment);
        }
    }
}
