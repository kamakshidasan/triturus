/**
 * Copyright (C) 2007-2015 52°North Initiative for Geospatial Open Source
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
 * if the distribution is compliant with both the GNU General Public
 * icense version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * Contact: Benno Schmidt & Martin May, 52 North Initiative for Geospatial Open Source
 * Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, Germany, info@52north.org
 */
package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.core.IoObject;
import org.n52.v3d.triturus.core.T3dException;

import java.io.*;
import java.util.ArrayList;

/**
 * Import of features consisting of point-geometries and thematic attributes from a file. Each line of the data file
 * gives x, y and z coordinate of a point as well as the feature's attribute values separated by white spaces.
 * Additionally, a meta-file that specifies the attribute names and corresponding data types has to be given.
 * <br /><br />
 * <i>German:</i> Einlesen punkthafter Geoobjekte. In den Eingabedateien stehen zeilenweise x-, y- und z-Koordinaten und
 * Attributwerte durch Leerzeichen voneinander getrennt. Die Namen der Attribute werden aus einer Metadaten-Datei
 * eingelesen.<br />
 * Sample data file:<br />
 * <pre>
 * 57.37 42.34 1.2 18.8 City_Hall
 * 65.31 48.12 9.8 15.8 Railway_Station
 * 60.26 49.05 3.0 16.8 High_Street
 * </pre><br />
 * Sample meta-data file:<br />
 * <pre>
 * temperature double
 * station String
 * </pre><br />
 * Code example showing class use:<br />
 * <pre>
 * IoPointFeatureReader reader = new IoPointFeatureReader();
 * try {
 *     reader.readFromMetaFile("example.metadata");
 *     reader.readFromFile("example.data");
 * }
 * catch (T3dException e) {
 *     ...
 * }
 * </pre>
 * @author Benno Schmidt
 */
public class IoPointFeatureReader extends IoObject
{
    private String mLogString = "";

    private String mFormat;
    private ArrayList mFeatureList = null;

    private int mNumberOfAttributes = 0;

    private ArrayList mAttrNames = null;
    private ArrayList mAttrTypes = null;
        
    public IoPointFeatureReader() {
        mLogString = this.getClass().getName();
    }

    public String log() {
        return mLogString;
    }

    /**
     * reads a set of 3-D points with thematic attributes from an ASCII file.<br /><br />
     * <i>German:</i> liest eine Menge attributierter 3D-Punkte aus einer ASCII-Datei ein.
     * @param pFilename File path
     * @return <tt>ArrayList</tt> of <tt>VgAttrFeature</tt>-objects with <tt>VgPoint</tt>-geometries
     * @throws org.n52.v3d.triturus.core.T3dException
     */
    public ArrayList readFromFile(String pFilename) throws T3dException
    {
    	String line = "";
        int lineNumber = 0;

        mFeatureList = new ArrayList();

        try {
            FileReader lFileRead = new FileReader(pFilename);
            BufferedReader lDatRead = new BufferedReader(lFileRead);

            String[] tok = new String[3 /* x, y und z */ + mNumberOfAttributes];
            double x, y, z;
  
            line = lDatRead.readLine();
            while (line != null) {
                lineNumber++;

                x = this.toDouble(this.getStrTok(line, 0, " " ));
                y = this.toDouble(this.getStrTok(line, 1, " " ));
                z = this.toDouble(this.getStrTok(line, 2, " " ));
 
                GmAttrFeature lFeat = new GmAttrFeature();
                lFeat.setGeometry(new GmPoint(x, y, z));
               
                for (int i = 0; i < mNumberOfAttributes; i++)
                {
                    String lType = (String) mAttrTypes.get(i);
                    String lAttrName = (String) mAttrNames.get(i);
                    String lVal = this.getStrTok(line, 3 + i, " ");
                    
                    if (lType.equals("int") || lType.equals("long"))
                        lFeat.addAttribute(lAttrName, lType, new Integer(this.toInt(lVal)));
                    else {
                    if (lType.equals("float") || lType.equals("double"))
                        lFeat.addAttribute(lAttrName, lType, new Double(this.toDouble(lVal)));
                    else
                        lFeat.addAttribute(lAttrName, lType, lVal);
                    }
                }
                mFeatureList.add(lFeat);
                line = lDatRead.readLine();
            }
            lDatRead.close();
        }
        catch (FileNotFoundException e) {
            throw new T3dException("Could not access file \"" + pFilename + "\".");
        }
        catch (IOException e) {
            throw new T3dException(e.getMessage());
        }
        catch (T3dException e) {
            throw new T3dException(e.getMessage());
        }
        catch (Exception e) {
            throw new T3dException("Parser error in \"" + pFilename + "\":" + lineNumber);
        }

        return mFeatureList;
    }

    /**
     * reads names and types of the thematic attributes from a metadata file in ASCII format.<br /><br />
     * <i>German:</i> liest die Namen und Typen der verwendeten Attribute aus einer Metadaten-Datei im ASCII-Format ein.
     * @param pFilename File path
     * @throws org.n52.v3d.triturus.core.T3dException
     */
    public void readFromMetaFile(String pFilename) throws T3dException
    {
    	String line = "";
        int lineNumber = 0;

        mAttrNames = new ArrayList();
        mAttrTypes = new ArrayList();

        try {
            FileReader lFileRead = new FileReader(pFilename);
            BufferedReader lDatRead = new BufferedReader(lFileRead);

            String tok1, tok2; // Attributname und -typ
  
            line = lDatRead.readLine();
            while (line != null) 
            {
                mAttrNames.add(this.getStrTok(line, 0, " "));
                mAttrTypes.add(this.getStrTok(line, 1, " "));

                lineNumber++;
                line = lDatRead.readLine();
            }
            lDatRead.close();
            
            mNumberOfAttributes = mAttrNames.size();
        }
        catch (FileNotFoundException e) {
            throw new T3dException("Could not access file \"" + pFilename + "\".");
        }
        catch (IOException e) {
            throw new T3dException(e.getMessage());
        }
        catch (T3dException e) {
            throw new T3dException(e.getMessage());
        }
        catch (Exception e) {
            throw new T3dException("Parser error in \"" + pFilename + "\":" + lineNumber);
        }
    }

    // Extraktion des i-ten Tokens (i >= 0, i max. = this.cMaxNumberOfTokensPerLine - 1) aus einem String 
    // ('pSep" als Trenner):
    private String getStrTok(String pStr, int i, String pSep) throws T3dException
    {
        ArrayList lStrArr = new ArrayList(); 
        lStrArr.add( pStr );
        int i0 = 0, i1 = 0, k = 0;
        while (i1 >= 0) {
           i1 = pStr.indexOf(pSep, i0);
           if (i1 >= 0) {
               if (k == 0)
                   lStrArr.set(0, pStr.substring(i0, i1));
               else
                   lStrArr.add(pStr.substring( i0, i1 ));
               i0 = i1 + 1;
               k++;
           }
        }
        lStrArr.add(pStr.substring(i0));
        if (i < 0 || i >= lStrArr.size())
            return ""; 
        return (String) lStrArr.get(i);
    } 

    // Konvertierung String -> Gleitpunktzahl:
    private double toDouble(String pStr) 
    {
        return Double.parseDouble(pStr);
    } 

    // Konvertierung String -> Ganzzahl:
    private int toInt(String pStr)
    {
        return Integer.parseInt( pStr );
    } 
}
