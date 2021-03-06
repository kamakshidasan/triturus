/**
 * Copyright (C) 2007-2016 52 North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *  - Apache License, version 2.0
 *  - Apache Software License, version 1.0
 *  - GNU Lesser General Public License, version 3
 *  - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *  - Common Development and Distribution License (CDDL), version 1.0.
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public License 
 * version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details.
 *
 * Contact: Benno Schmidt and Martin May, 52 North Initiative for Geospatial 
 * Open Source Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, 
 * Germany, info@52north.org
 */
package org.n52.v3d.triturus.core;

import java.util.HashMap;
import java.io.*;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * Translation of framework-specific exceptions into error messages. The 
 * message text will be read from an ASCII file, which holds unique error
 * numbers and (separated with a comma ',') corresponding error messages
 * line-by-line.  
 * <p>
 * Note: The implemented error message mechanism is primarily used by the 
 * 52N terrainServer.
 * 
 * @author Benno Schmidt
 * @see T3dException
 * @see org.n52.v3d.coordinatetransform1.suite.terrainservice.povraywts.WebTerrainServlet</tt>. 
 */
public class T3dExceptionMessage
{
    private static T3dExceptionMessage sInstance = null;
    private static HashMap<Integer, String> sMessages = null;

    private T3dExceptionMessage() {
    }

    /**
     * returns the <tt>T3dExceptionMessage</tt> instance (Singleton 
     * implementation). 
     *
     * @return <tt>T3dExceptionMessage</tt> instance
     */
    static public T3dExceptionMessage getInstance() {
        if (sInstance == null) {
            sInstance = new T3dExceptionMessage();
        }
        return sInstance;
    }

    /**
     * reads the configuration file containing the message texts. If 
     * <tt>pLocation</tt> is <i>null</i> or the empty string, no file will
     * be read.
     *
     * @param pLocation File name (incl. path) of message file, or valid URL.
     */
    public void readConfiguration(String pLocation)
    {
        if (pLocation == null || pLocation.length() <= 0)
            return;

        InputStream is;
        try {
            if (pLocation.startsWith("http"))
                is = this.createInputStream(new URL(pLocation));
            else
                is = this.createInputStream(pLocation);
        }
        catch (MalformedURLException e) {
            throw new T3dException(
            	"Couldn't read location \"" + pLocation + "\" (malformed URL)." );
        } catch (IOException e) {
            throw new T3dException(
            	"Couldn't read location \"" + pLocation + "\" (IO error)." );
        }

        BufferedReader pDatRead = this.createBufferedReader(is);

        try {
            sMessages = new HashMap<Integer, String>();

            String line = pDatRead.readLine();
            while (line != null)
            {
                int errNo = this.parseErrNo(line);
                if (errNo >= 0) {
                    String errMsg = this.parseErrMsg(line);
                    //System.out.println("Read line \"" + line + "\"");
                    //System.out.println("  -> #" + errNo + ", \"" + errMsg + "\"");
                    sMessages.put(new Integer(errNo), errMsg);
                }

                line = pDatRead.readLine();
            }
        }
        catch (IOException e) {
            throw new T3dException(e.getMessage());
        }
    }

    private InputStream createInputStream(URL url) 
    		throws IOException 
    {
    	return url.openConnection().getInputStream();
    }

    private InputStream createInputStream(String pFilename) 
    		throws FileNotFoundException 
    {
        InputStream  input  = this.getClass().getClassLoader().getResourceAsStream(pFilename);
    	if (input == null) {
    		input =  new FileInputStream(pFilename);
    	}
    	return input;
    }

    private BufferedReader createBufferedReader(InputStream pInputStream) 
    {
        return new BufferedReader( new InputStreamReader(pInputStream) );
    }

    private int parseErrNo(String pLine) {
        String line = pLine.trim();
        if (line.length() <= 0 || line.startsWith("#")) {
            return -1;
        }
        int k = pLine.indexOf(",");
        if (k < 0) {
            throw new T3dException(
            	"Format error in message configuration file (line \"" + pLine + "\"...");
        }
        return Integer.parseInt(pLine.substring(0, k));
    }

    private String parseErrMsg(String pLine) {
        int k = pLine.indexOf(",");
        if (k < 0) {
            throw new T3dException(
            	"Format error in message configuration file (line \"" + pLine + "\"...");
        }
        return pLine.substring(k + 1);
    }

    /**
     * returns the error message text for a given {@link Throwable}-object. 
     * If this object is a {@link T3dException} <i>and</i> an error number 
     * is available, the corresponding message text from the error message 
     * file will be returned.
     * 
     * @param e Throwable-object
     * @return Error message text
     */
    public String translate(Throwable e)
    {
        String res = "";
        if (e == null)
            return res;

        res = e.getMessage();
        if (! (e instanceof T3dException))
            return res;

        if (sInstance == null)
            sInstance = new T3dExceptionMessage();
        int id = ((T3dException) e).getId();
        if (id >= 0) {
            String str = this.lookUpMessage(id);
            if (str != null)
                res = str;
        }

        return res;
    }

    private String lookUpMessage(int id) {
        if (sMessages == null)
            return null;
        String res = (String) sMessages.get(new Integer(id));
        return res;
    }
}
