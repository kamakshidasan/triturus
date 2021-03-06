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

import org.n52.v3d.triturus.vgis.VgAttrFeature;
import org.n52.v3d.triturus.vgis.VgGeomObject;
import org.n52.v3d.triturus.vgis.VgFeature;
import org.n52.v3d.triturus.core.T3dException;

import java.util.ArrayList;

/**
 * Implementation for attributed geometric objects. Attributes will be held in main memory.
 *
 * @author Benno Schmidt
 */
public class GmAttrFeature extends VgAttrFeature
{
    private VgGeomObject mGeom = null;
    private ArrayList mAttrNames = new ArrayList();
    private ArrayList mAttrValues = new ArrayList();
    private ArrayList mAttrTypes = new ArrayList();
    
    public GmAttrFeature() {
    	mAttrNames.clear();
    	mAttrValues.clear();
    	mAttrTypes.clear();
    }

	/**
     * returns the object's geometry.
     *
     * @return Object geometry or <i>null</i>
     */
    public VgGeomObject getGeometry() {
        return mGeom;
    }

    /** 
     * assigns a geometry to the object. E.g. for a point feature:
     * <pre>
     * GmAttrFeature myFeature = new GmAttrFeature();
     * myFeature.setGeometry(new GmPoint(1000., 1500., 0.));
     * </pre>
     *
     * @param pGeom geometric object
     */
    public void setGeometry(VgGeomObject pGeom) {
        mGeom = pGeom;
    }

    public VgFeature getFeature(int i) throws T3dException
    {
    	throw new T3dException("Tried to access sub-feature of atomic object.");
    }

    public boolean isCollection() {
    	return false;
    }

    public int numberOfSubFeatures() {
        return 0;
    }

    public void addAttribute(String pAttrName, String pAttrType) throws T3dException
    {
    	if (this.hasAttribute(pAttrName))
    	    throw new T3dException("The attribute \"" + pAttrName + "\" is already present.");
    	// else:    
    	mAttrNames.add(pAttrName);
    	mAttrTypes.add(pAttrType);
    	mAttrValues.add("empty");
    }

	/**
	 * defines a thematic attribute. Attribute name, data-type and initial value have to be given. If an attribute
     * already exists, an exception will be thrown.
     * <p>
     * Example:
     * <pre>
     * GmAttrFeature myFeature = new GmAttrFeature();
     * myFeature.addAttribute("FEATURE_ID", "java.lang.String", "p1545");
     * </pre>
     *
	 * @param pAttrName Attribute name
	 * @param pAttrType Attribute type as Java class-name
     * @param pVal Attribute value
     * @throws T3dException if an error occurs
     */
    public void addAttribute(String pAttrName, String pAttrType, Object pVal) throws T3dException
    {
    	if (this.hasAttribute(pAttrName))
    	    throw new T3dException("The attribute \"" + pAttrName + "\" is already present.");
    	// else:    
    	mAttrNames.add(pAttrName);
    	mAttrTypes.add(pAttrType);
    	mAttrValues.add(pVal);
    }

    public String[] getAttributeNames() {
    	String[] lTmp = new String[ mAttrNames.size() ];
    	for (int i = 0; i < mAttrNames.size(); i++)
    	    lTmp[i] = (String) mAttrNames.get(i);
    	return lTmp;
    }

    /** 
     * checks if the given attribute has been defined.
     *
     * @param pAttrName Attribute name
     * @return <i>true</i> if an attribute has been defined
     */
    public boolean hasAttribute(String pAttrName) 
    {
    	return (this.internalAttributePos(pAttrName) >= 0);
    }

    private int internalAttributePos(String pAttrName) {
    	for (int i = 0; i < mAttrNames.size(); i++) {
    	    if (((String) mAttrNames.get(i)).equalsIgnoreCase( pAttrName ))
    	        return i;
    	}
    	return -1;
    }
    	     
	/**
	 * returns a thematic attribute's value. If the given attribute is not defined, a <tt>T3dException</tt> will be
     * thrown.
     * <p>
     * Query example for a String-valued attribute:
     * <pre>
     * String val = myFeature.getAttributeValue("FEATURE_ID");
     * System.out.println("The value of the attributs \"FEATURE_ID\" is: " + val );
     * </pre>
     *
	 * @param pAttrName Name of the queried attribute
	 * @return Object of type of the queried attribute
	 * @throws T3dException
	 */
    public Object getAttributeValue(String pAttrName) throws T3dException
    {
    	int i = this.internalAttributePos(pAttrName);
    	if (i < 0)
    	    throw new T3dException("Tried to access non-present attribute \"" + pAttrName + "\".");
    	// else:
    	return mAttrValues.get(i);
    }    	

    /**
     * sets a thematic attribute's value. If the attribute has not been defined, or the given object types can not be
     * mapped on each other, a <tt>T3dException</tt> will be thrown.
     * <p>
     * Example:
     * <pre>
     * GmAttrFeature myFeature = new GmAttrFeature();
     * myFeature.addAttribute("FEATURE_ID", "java.lang.String");
     * myFeature.setAttributeValue("FEATURE_ID", "p1546");
     * </pre>
     *
     * @param pAttrName Attribute name
     * @param pVal Value to be set
     * @throws T3dException
     */
    public void setAttributeValue(String pAttrName, Object pVal) throws T3dException
    {
    	int i = this.internalAttributePos(pAttrName);
    	if (i <= 0)
    	    throw new T3dException("Tried to access non-present attribute \"" + pAttrName + "\".");
    	// else:
    	mAttrValues.set(i, pVal);
    }

    public String getAttributeType(String pAttrName)
    {
    	int i = this.internalAttributePos(pAttrName);
    	if (i < 0)
    	    throw new T3dException("Tried to access non-present attribute \"" + pAttrName + "\".");
    	// else:
    	return (String) mAttrTypes.get(i);
    }

    public String toString() {
        String strGeom = "<empty geometry>";
        if (mGeom != null)
            strGeom = mGeom.toString(); 
        String lRet = "[{";
    	for (int i = 0; i < mAttrNames.size(); i++)
    	    lRet = lRet + "(" + (String) mAttrNames.get( i ) + ": " + mAttrValues.get( i ) + ")";
        lRet = lRet + "}, ";
        lRet = lRet + strGeom + "]";
        return lRet;
    }
}
