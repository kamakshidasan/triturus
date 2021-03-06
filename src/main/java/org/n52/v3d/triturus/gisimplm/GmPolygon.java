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

import java.util.ArrayList;
import java.util.List;

import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.vgis.VgEnvelope;
import org.n52.v3d.triturus.vgis.VgGeomObject;
import org.n52.v3d.triturus.vgis.VgLinearRing;
import org.n52.v3d.triturus.vgis.VgPolygon;

/**
 * <tt>VgPolygon</tt>-implementation. Object information will be kept in main
 * memory.<br />
 * x- and y-values have to be given with respect to the spatial reference system
 * (SRS) that has been set for the geometric object. z-values might be provided
 * for the object's vertices.<br />
 * <br />
 * <i>German:</i> <tt>VgPolygon</tt>-Implementierung, bei der die
 * Punktkoordinaten im Speicher vorgehalten werden. x- und y-Werte sind bezogen
 * auf das eingestellte r�umliche Bezugssystem (SRS) anzugeben. Die Eckpunkte
 * k&ouml;nnen mit einer z-Koordinate versehen sein.<br />
 * Bem.: In der vorliegenden Implementierung erfolgt keine Pr&uuml;fung auf
 * &Uuml;berlappung der Liniensegmente. Es k&ouml;nnen auch innere L&ouml;cher
 * definiert werden. In der aktuellen Version wird jedoch nicht
 * &uuml;berpr&uuml;ft, ob die L&ouml;cher g&uuml;ltig sind, sprich ob sie den
 * Simple-Feature-Bedingungen des OGC entsprechen. Ein inneres Loch darf den
 * �u&szlig;eren Rand (outer boundary) des Polygons maximal in einem Punkt
 * ber&uuml;hren. Dies wird jedoch ebenfalls aktuell nicht gepr&uuml;ft.<br/>
 * 
 * @author Christian Danowski, Benno Schmidt
 * 
 */
public class GmPolygon extends VgPolygon {

	private VgLinearRing outerBoundary;
	private List<VgLinearRing> holes;

	public GmPolygon(VgLinearRing outerBoundary) {
		this.outerBoundary = outerBoundary;
		this.holes = new ArrayList<VgLinearRing>();
	}

	/**
	 * Constructor. Note, that up to now, there are no checks on the holes (if
	 * they overlap or split the outer boundary or anything like that). So, for
	 * now, you are responsible to create a correct Polygon.
	 * 
	 * @param outerBoundary
	 * @param holes
	 */
	public GmPolygon(VgLinearRing outerBoundary, List<VgLinearRing> holes) {
		// assert same CRS!!!!!
		String srsOuterBoundary = outerBoundary.getSRS();
		if (holes != null) {
			for (VgLinearRing innerRing : holes) {
				if (!innerRing.getSRS().equals(srsOuterBoundary))
					throw new T3dException(
							"The spatial reference system (SRS) of at least one hole is not equal to the SRS of the outer boundary!");
			}
			this.holes = holes;
		} else
			this.holes = new ArrayList<VgLinearRing>();

		// if the program reaches this part, then everything is fine, no SRS
		// exception has been detected.
		// TODO add further checks on holes (if they overlap or split the outer
		// boundary...)
		this.outerBoundary = outerBoundary;
	}

	/**
	 * Note, that up to now, there are no checks on the holes (if they overlap
	 * or split the outer boundary or anything like that). So, for now, you are
	 * responsible to create a correct Polygon.
	 * 
	 * @param hole
	 */
	public void addHole(VgLinearRing hole) {
		// assert same CRS as outerBoundary
	}

	@Override
	public VgLinearRing getOuterBoundary() {
		return this.outerBoundary;
	}

	@Override
	public int getNumberOfHoles() {
		return this.holes.size();
	}

	@Override
	public VgLinearRing getHole(int i) {
		if (i < 0 || i >= this.holes.size())
			throw new T3dException("Index out of bounds.");
		// else:
		return this.holes.get(i);
	}

	@Override
	public VgEnvelope envelope() {
		if (this.outerBoundary.getNumberOfVertices() > 0) {
			GmEnvelope mEnv = new GmEnvelope(this.outerBoundary.getVertex(0));
			for (int i = 0; i < this.outerBoundary.getNumberOfVertices(); i++)
				mEnv.letContainPoint(this.outerBoundary.getVertex(i));
			return mEnv;
		} else
			return null;
	}

	/**
	 * returns the object's footprint geometry (projection to the x-y-plane).
	 * 
	 * @return &quot;Footprint&quot; as <tt>GmPolygon</tt>-object
	 */
	@Override
	public VgGeomObject footprint() {

		// construct footprint of outer boundary
		GmLinearRing outerBoundaryFootprint = (GmLinearRing) this.outerBoundary
				.footprint();

		// construct list of footprints of inner holes
		List<VgLinearRing> holesFootprints = new ArrayList<VgLinearRing>(
				this.holes.size());

		for (VgLinearRing hole : this.holes) {
			GmLinearRing holeFootprint = (GmLinearRing) hole.footprint();
			holesFootprints.add(holeFootprint);
		}

		return new GmPolygon(outerBoundaryFootprint, holesFootprints);
	}

}
