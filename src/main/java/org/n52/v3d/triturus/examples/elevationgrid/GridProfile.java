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
package org.n52.v3d.triturus.examples.elevationgrid;

import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.gisimplm.*;
import org.n52.v3d.triturus.vgis.VgLineString;
import org.n52.v3d.triturus.vgis.VgPoint;
import org.n52.v3d.triturus.vgis.VgProfile;

/**
 * Triturus example application: Generates a cross-section for a given 
 * elevation grid.
 * 
 * @author Benno Schmidt
 * @see GridProfileApp
 */
public class GridProfile
{
	public static void main(String args[])
	{
		GridProfile app = new GridProfile();
		app.run();
	}
	
	public void run() 
	{
        // Read the elevation grid from file:
		IoElevationGridReader reader = new IoElevationGridReader(
				IoElevationGridReader.X3DOM);
		GmSimpleElevationGrid grid = null;
		try {
			grid = reader.readFromFile("data/test.html");
		}
		catch (T3dException e) {
			e.printStackTrace();
		}

        // This is just some control output:
    	System.out.println(grid);
        System.out.print("The elevation grid's bounding-box: ");
	System.out.println(grid.envelope().toString());
        
        VgPoint point1 = grid.getPoint(0, 0);
        String value1 = point1.getX()+","+point1.getY()+","+point1.getZ();
        
        VgPoint point2 = grid.getPoint(59, 79);
        String value2 = point2.getX()+","+point2.getY()+","+point2.getZ();
        
        System.out.println(value1 + " "+ value2);

        // Give definition-line (sequence of x, y, z coordinates, z will be 
		// ignored):
		VgLineString defLine = new GmLineString(
				value1 + "," +
				//"3532000,5505500,0," + 
				value2 //+ 
				//"3534000,5506000,0"
                );
		System.out.println(defLine); // control output
		
		// Generate cross-section:
		FltElevationGrid2Profile proc = new FltElevationGrid2Profile();
		VgProfile prof = proc.transform(grid, defLine);

		// Cross-section output to console...
        for (int i = 0; i < prof.numberOfTZPairs(); i++) {
            System.out.println((prof.getTZPair(i))[0] + ", " + (prof.getTZPair(i))[1]);
        }
        // to SVG ...
        System.out.println("Writing SVG-file...");
        IoProfileWriter lWriter = new IoProfileWriter(IoProfileWriter.SVG);
        lWriter.writeToFile(prof, "data/cross-section.svg");
        // and to ASCII-file:
        System.out.println("Exporting to ASCII-file...");
        lWriter.setFormatType(IoProfileWriter.ACGEO);
        lWriter.writeToFile(prof, "data/cross-section.prf");
        System.out.println("Success!");
    }
}
