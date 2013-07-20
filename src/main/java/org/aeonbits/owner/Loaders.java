/*
 * Copyright (c) 2013, Luigi R. Viggiano
 * All rights reserved.
 *
 * This software is distributable under the BSD license.
 * See the terms of the BSD license in the documentation provided with this software.
 */

package org.aeonbits.owner;

import org.aeonbits.owner.loaders.Loader;
import org.aeonbits.owner.loaders.PropertiesLoader;
import org.aeonbits.owner.loaders.XMLLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import static org.aeonbits.owner.Util.prohibitInstantiation;
import static org.aeonbits.owner.Util.unsupported;


/**
 * This class is responsible of locating an appropriate Loader for a given URL (based the extension in the resource 
 * name)and load the properties from it.
 * 
 * @author Luigi R. Viggiano
 * @since 1.0.5
 */
abstract class Loaders {
    private static final List<Loader> loaders = Collections.synchronizedList(new LinkedList<Loader>());
    
    static {
        registerLoader(new PropertiesLoader());
        registerLoader(new XMLLoader());
    }
            
    Loaders() {
        prohibitInstantiation();
    }
    
    private static InputStream getInputStream(URL url) throws IOException {
        URLConnection conn = url.openConnection();
        if (conn == null)
            return null;
        return conn.getInputStream();
    }

    static boolean load(Properties result, URL url) throws IOException {
        InputStream stream = getInputStream(url);
        if (stream != null)
            try {
                Loader loader = findLoader(url);
                loader.load(result, stream);
                return true;
            } finally {
                close(stream);
            }
        return false;
    }

    private static Loader findLoader(URL url) {
        for (Loader loader : loaders)
            if (loader.accept(url))
                return loader;
        throw unsupported("Can't resolve a Loader for the URL %s.", url.toString());
    }

    private static void close(InputStream inputStream) throws IOException {
        if (inputStream != null)
            inputStream.close();
    }

    /**
     * Allows the user to register a {@link Properties properties} {@link Loader}.
     * 
     * @since 1.0.5
     * @param loader the {@link Loader} to register.
     */
    private static void registerLoader(Loader loader) {
        loaders.add(0, loader);
    }

}