/***************************************************************************************
 * Copyright (C) 2011 by 52 North Initiative for Geospatial Open Source Software GmbH  *
 *                                                                                     *
 * Contact: Benno Schmidt & Martin May, 52 North Initiative for Geospatial Open Source *
 * Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, Germany, info@52north.org *
 *                                                                                     *
 * This program is free software; you can redistribute and/or modify it under the      *
 * terms of the GNU General Public License version 2 as published by the Free Software *
 * Foundation.                                                                         *
 *                                                                                     *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied WARRANTY *
 * OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public  *
 * License for more details.                                                           *
 *                                                                                     *
 * You should have received a copy of the GNU General Public License along with this   *
 * program (see gnu-gpl v2.txt). If not, write to the Free Software Foundation, Inc.,  *
 * 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA, or visit the Free Software *
 * Foundation web page, http://www.fsf.org.                                            *
 **************************************************************************************/
package org.n52.v3d.triturus.vgis;

import org.n52.v3d.triturus.core.T3dException;

/**
 * General base class to manage geo-objects (<i>&quot;features&quot;</i>).<br /><br />
 * <i>German:</i> Der Realisierung der vorliegenden Klassen-Bibliothek lagen folgende Entwurfsziele zugrunde:
 * <ul>
 * <li>Anwendungsbereich sind Anwendungen zur 3D-Geovisualisierung.</li>
 * <li>Die Basisklassen sind abstrakt definiert, so dass unterschiedliche Implementierungen 
 * (z. B. org.n52.v3d.triturus.gisimplm) verwendet werden k�nnen.</li>
 * <li>Die Bibliothek sollte schnell zu realisieren sein.</li>
 * </ul>
 * @see org.n52.v3d.triturus.vgis.VgAttrFeature
 * @author Benno Schmidt
 */
abstract public class VgFeature 
{
	private String mName = "";

	/**
	 * sets the geo-object name (e.g., a designator or a title). Note that inside the framework this object name must
     * not be unique.
     * @param pName arbitrary string
	 */
	public void setName(String pName) {
		mName = pName;
	}

	/**
     * return the geo-object's name.
     * @return Object name (must not be unique)
     */
	public String getName() {
		return mName;
	}

	/**
     * return an (atomic) <tt>VgFeature</tt> object's geometry.
     * @return Object geometry
     */
	abstract public VgGeomObject getGeometry();

	/**
	 * returns the i-th sub-object of an geo-object.<br /><br />
	 * Note that the condition 0 &lt;= i &lt; <tt>this.numberOfSubFeatures()</tt> mist hold; otherwise a
     * <tt>T3dException</tt> will be thrown.
     * @param i Index
     * @return Geo-object
	 * @throws T3dException
	 */
	abstract public VgFeature getFeature(int i) throws T3dException;

	/**
	 * returns the information whether the geo-object consists of more than one geo-object (&quot;feature
     * collection&quot;).
     * @return <i>true</i> if the geo-object consists of more than one sub-object, else <i>false</i>
	 */
	abstract public boolean isCollection();

	/**
     * returns the information whether the geo-object is consists of only one geo-object. Note that the assertion
     * <i>obj.isCollection() == !obj.isAtomic()</i> always must hold.
	 * @return <i>true</i>, if the object does not consist of sub-objects, else <i>false</i>
	 */
	public boolean isAtomic() {
		return! (this.isCollection());
	}

	/**
	 * returns the number of sub-objects. For an atomic geo-object, the return-value will be 1.
     * @return Number of sub-objects
	 */
	abstract public int numberOfSubFeatures();

	abstract public String toString();
}