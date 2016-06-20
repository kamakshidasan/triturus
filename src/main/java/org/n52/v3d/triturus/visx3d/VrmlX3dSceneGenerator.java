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
package org.n52.v3d.triturus.visx3d;

import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.core.T3dNotYetImplException;
import org.n52.v3d.triturus.gisimplm.GmPoint;
import org.n52.v3d.triturus.gisimplm.GmSimple2dGridGeometry;
import org.n52.v3d.triturus.gisimplm.IoAbstractWriter;
import org.n52.v3d.triturus.t3dutil.T3dColor;
import org.n52.v3d.triturus.t3dutil.T3dSymbolInstance;
import org.n52.v3d.triturus.t3dutil.T3dVector;
import org.n52.v3d.triturus.vgis.VgElevationGrid;
import org.n52.v3d.triturus.vscene.MultiTerrainScene;
import org.n52.v3d.triturus.vscene.VsScene;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * VRML/X3D scene generator. The current implementation takes 
 * {@link MultiTerrainScene}-objects as input.
 *
 * @author Benno Schmidt
 * @see MultiTerrainScene
 */
public class VrmlX3dSceneGenerator extends IoAbstractWriter
{
    private String mLogString = "";

    private VsScene mScene;
    private BufferedWriter mDoc;

    /**
     * Constructor.
     *
     * @param pScene {@link MultiTerrainScene}-object
     */
    public VrmlX3dSceneGenerator(VsScene pScene)
    {
        mLogString = this.getClass().getName();

        mScene = pScene;

        if (!(mScene instanceof MultiTerrainScene)) {
            throw new T3dNotYetImplException(
            		"MultiTerrainScene expected for VRML/X3D visualization...");
        }
    }

    public String log() {
        return mLogString;
    }

    /**
     * generates a VRML 2.0 file representing the content of the scene that 
     * has been passed to the constructor.
     *
     * @param pFilename Output-file name (complete file path)
     */
    public void writeToVrmlFile(String pFilename)
    {
        MultiTerrainScene s = (MultiTerrainScene) mScene;

        try {
            mDoc = new BufferedWriter(new FileWriter(pFilename));

            wl("#VRML V2.0 utf8");
            wl("WorldInfo {");
            wl("  info [\"Scene generated by 52N Triturus\"]");
            wl("  title \"MultiTerrainScene\"");
            wl("}");
            wl("NavigationInfo {");
            wl("  type \"EXAMINE\"");
            wl("}");
            wl("Background { skyColor " +
                    s.getBackgroundColor().getRed() + " " +
                    s.getBackgroundColor().getGreen() + " " +
                    s.getBackgroundColor().getBlue() + "}");
            wl("Transform {");
            wl("  scale 1 " + s.getDefaultExaggeration() + " 1");
            wl("  children [");
            wl("    Shape {");
            wl("      appearance Appearance {");
            wl("        material Material {");
            wl("          diffuseColor " +
                    s.getBBoxColor().getRed() + " " +
                    s.getBBoxColor().getGreen() + " " +
                    s.getBBoxColor().getBlue());
            wl("        }");
            wl("      }");
            wl("      geometry IndexedLineSet {");

            T3dVector
                pos1 = s.norm(new GmPoint(
                		s.envelope().getXMin(), 
                		s.envelope().getYMin(), 
                		s.envelope().getZMin())),
                pos2 = s.norm(new GmPoint(
                		s.envelope().getXMax(), 
                		s.envelope().getYMax(), 
                		s.envelope().getZMax()));

            wl("        coord Coordinate {");
            wl("          point [");
            /*
            if (false) {
            wl("            -1 " + s.normZMin() + " -1,");
            wl("            -1 " + s.normZMin() + " 1,");
            wl("            -1 " + s.normZMax() + " 1,");
            wl("            -1 " + s.normZMax() + " -1,");
            wl("            1 " + s.normZMin() + " -1,");
            wl("            1 " + s.normZMin() + " 1,");
            wl("            1 " + s.normZMax() + " 1,");
            wl("            1 " + s.normZMax() + " -1");
            } else {
            */
            wl("            " + pos1.getX() + " " + pos1.getZ() + " " + (-pos1.getY()) + ",");
            wl("            " + pos1.getX() + " " + pos1.getZ() + " " + (-pos2.getY()) + ",");
            wl("            " + pos1.getX() + " " + pos2.getZ() + " " + (-pos2.getY()) + ",");
            wl("            " + pos1.getX() + " " + pos2.getZ() + " " + (-pos1.getY()) + ",");
            wl("            " + pos2.getX() + " " + pos1.getZ() + " " + (-pos1.getY()) + ",");
            wl("            " + pos2.getX() + " " + pos1.getZ() + " " + (-pos2.getY()) + ",");
            wl("            " + pos2.getX() + " " + pos2.getZ() + " " + (-pos2.getY()) + ",");
            wl("            " + pos2.getX() + " " + pos2.getZ() + " " + (-pos1.getY()) + ",");
            // }
            wl("          ]");
            wl("        }");
            wl("        coordIndex [");
            wl("          0, 1, 2, 3, 0, -1,");
            wl("          4, 5, 6, 7, 4, -1,");
            wl("          0, 4, -1,");
            wl("          1, 5, -1,");
            wl("          2, 6, -1,");
            wl("          3, 7, -1");
            wl("        ]");
            wl("      }");
            wl("    },");

            for (int i = 0; i < s.getTerrains().size(); i++) 
            {
                VgElevationGrid terr = s.getTerrains().get(i);
                GmSimple2dGridGeometry terrGeom = (GmSimple2dGridGeometry) terr.getGeometry();
                double scale = s.getScale();

                T3dVector upperLeftCornerNormalized = s.norm(new GmPoint(
                        terrGeom.envelope().getXMin(),
                        terrGeom.envelope().getYMax(),
                        0.0));
    
                double dx = upperLeftCornerNormalized.getX();
                double dz = -upperLeftCornerNormalized.getY();

                wl("    Transform {");
                wl("      scale " + scale + " " + scale + " " + scale);
                wl("      translation " + dx + " 0 " + dz);
                wl("      children Shape {");
                wl("        appearance Appearance {");
                wl("          material Material {");

                T3dColor terrCol = s.getDefaultReliefColor();

                wl("            diffuseColor " +
                        terrCol.getRed() + " " +
                        terrCol.getGreen() + " " +
                        terrCol.getBlue());
                wl("          }");
                wl("        }");
                wl("        geometry ElevationGrid {");
                wl("          xDimension " + terrGeom.numberOfColumns());
                wl("          zDimension " + terrGeom.numberOfRows());
                wl("          xSpacing " + terrGeom.getDeltaX());
                wl("          zSpacing " + terrGeom.getDeltaY());
                wl("          height [");

                // Write elevations:
                DecimalFormat dfZ = this.getDecimalFormatZ();
                for (int ii = terrGeom.numberOfRows() - 1; ii >= 0; ii--) {
                    for (int jj = 0; jj < terrGeom.numberOfColumns(); jj++) {
                        w(dfZ.format(terr.getValue(ii, jj)) + ",");
                    }
                    wl();
                }

                wl("          ]");

                if (s.getHypsometricColorMapper() != null)
                {
                    wl("          color Color {");
                    wl("            color [");

                    for (int ii = terrGeom.numberOfRows() - 1; ii >= 0; ii--) {
                        for (int jj = 0; jj < terrGeom.numberOfColumns(); jj++) {
                            T3dColor col = s.getHypsometricColorMapper().transform(
                            		terr.getValue(ii, jj));
                            wl(col.getRed() + " " + col.getGreen() + " " + col.getBlue() + ",");
                        }
                    }

                    wl("            ]");
                    wl("          }");
                }

                wl("        }");
                wl("      }");
                wl("    },");
            }

            wl("  ]");
            wl("}");

            // Markers:

            if (s.getMarkers() != null)
            {
                wl("PROTO MarkerProto [");
                wl("  exposedField SFColor color 0.5 0.5 0.5");
                wl("  exposedField SFVec3f position 0 0 0");
                wl("] {");
                wl("  Transform {");
                wl("    translation IS position");
                wl("    children [");
                wl("      Shape {");
                wl("        appearance Appearance {");
                wl("          material Material {");
                wl("            diffuseColor IS color");
                wl("          }");
                wl("        }");
                wl("        geometry Sphere {"); 
                // TODO Currently all markers are visualized using spheres
                wl("          radius 0.025");
                wl("        }");
                wl("      }");
                wl("    ]");
                wl("  }");
                wl("}");

                for (int i = 0; i < s.getMarkers().size(); i++) {
                    T3dSymbolInstance m = s.getMarkers().get(i);
                    T3dColor col = m.getColor();
                    T3dVector pos = s.norm(m.getPosition());

                    wl("MarkerProto {");
                    wl("  color " + 
                    		col.getRed() + " " + col.getGreen() + " " + col.getBlue());
                    wl("  position " + 
                    		pos.getX() + " " + 
                    		(pos.getZ() * s.getDefaultExaggeration()) + " " + 
                    		(-pos.getY()));
                    wl("}");
                }
            }

            mDoc.close();
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
    }

    /**
     * generates an X3D file representing the content of the scene that has 
     * been passed to the constructor.
     *
     * @param pFilename Output-file name (complete file path)
     */
    public void writeToX3dFile(String pFilename)
    {
        this.writeToX3dFile(pFilename, false);
    }

    /**
     * generates an XHTML document containing a simple X3DOM model.
     *
     * @param pFilename Output-file name (complete file path)
     */
    public void writeToX3domFile(String pFilename)
    {
        this.writeToX3dFile(pFilename, true);

    }

    /**
     * generates an XHTML document containing a simple X3DOM model. Note that the 
     * flag <tt>pX3dom</tt> must be set to <i>true</i> to embed the X3D model into 
     * XHTML using X3DOM. Otherwise, for <tt>pX3dom</tt> set to <i>false</i>, regular 
     * X3D output will be produced (you could call the method {@link writeToX3dFile} 
     * without <tt>pX3dom</tt> parameter instead).
     *
     * @param pFilename Output-file name (complete file path)
     * @param pX3dom controls whether an X3D or an XHTML/X3DOM document will be generated
     */
    public void writeToX3dFile(String pFilename, boolean pX3dom)
    {
        MultiTerrainScene s = (MultiTerrainScene) mScene;

        try {
            mDoc = new BufferedWriter(new FileWriter(pFilename));

            if (pX3dom) {
                String lTitle = "52N Triturus XHTML/X3DOM document";
                wl("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
                wl("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
                wl("  <head>");
                wl("    <meta http-equiv=\"X-UA-Compatible\" content=\"chrome=1\" />");
                wl("    <meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\" />");
                wl("    <title>" + lTitle + "</title>");
                wl("    <link rel=\"stylesheet\" type=\"text/css\" href=\"http://www.x3dom.org/x3dom/release/x3dom.css\" />");
                wl("    <script type=\"text/javascript\" src=\"http://www.x3dom.org/x3dom/release/x3dom.js\"></script>");
	            wl("  </head>");
                wl("  <body>");
                wl("    <h1>" + lTitle + "</h1>");
                wl("    <p>");
                wl("      This XHTML/X3DOM has been generated automatically by the 52N Triturus framework's VrmlX3dGenerator.");
                wl("    <p>");
                wl();
                wl("<X3D xmlns=\"http://www.web3d.org/specifications/x3d-namespace\" showStat=\"false\" showLog=\"true\"");
                wl("  x=\"0px\" y=\"0px\" width=\"400px\" height=\"400px\">");
            }
            else {
                wl("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                wl("<!DOCTYPE X3D PUBLIC \"ISO//Web3D//DTD X3D 3.2//EN\" \"http://www.web3d.org/specifications/x3d-3.2.dtd\">");
                wl("<X3D version=\"3.2\" profile=\"Immersive\" xmlns:xsd='http://www.w3.org/2001/XMLSchema-instance' xsd:noNamespaceSchemaLocation='http://www.web3d.org/specifications/x3d-3.2.xsd'>");
            }

            wl("  <Scene>");
            wl("    <WorldInfo info='Scene generated by 52N Triturus' title='MultiTerrainScene'></WorldInfo>");
            wl("    <NavigationInfo type=\"EXAMINE\"></NavigationInfo>");
            wl("    <Background skyColor=\"" +
                    s.getBackgroundColor().getRed() + " " +
                    s.getBackgroundColor().getGreen() + " " +
                    s.getBackgroundColor().getBlue() + "\"></Background>");
            wl("    <Transform scale=\"1 " + s.getDefaultExaggeration() + " 1\">");
            wl("      <Shape>");
            wl("        <Appearance>");
            wl("          <Material emissiveColor=\"" +
                    s.getBBoxColor().getRed() + " " +
                    s.getBBoxColor().getGreen() + " " +
                    s.getBBoxColor().getBlue() + "\"></Material>");
            wl("        </Appearance>");
            wl("        <IndexedLineSet coordIndex='");
            wl("          0 1 2 3 0 -1,");
            wl("          4 5 6 7 4 -1,");
            wl("          0 4 -1,");
            wl("          1 5 -1,");
            wl("          2 6 -1,");
            wl("          3 7 -1");
            wl("        '>");

            T3dVector
                pos1 = s.norm(new GmPoint(s.envelope().getXMin(), s.envelope().getYMin(), s.envelope().getZMin())),
                pos2 = s.norm(new GmPoint(s.envelope().getXMax(), s.envelope().getYMax(), s.envelope().getZMax()));

            wl("          <Coordinate point='");
            wl("            " + pos1.getX() + " " + pos1.getZ() + " " + (-pos1.getY()) + ",");
            wl("            " + pos1.getX() + " " + pos1.getZ() + " " + (-pos2.getY()) + ",");
            wl("            " + pos1.getX() + " " + pos2.getZ() + " " + (-pos2.getY()) + ",");
            wl("            " + pos1.getX() + " " + pos2.getZ() + " " + (-pos1.getY()) + ",");
            wl("            " + pos2.getX() + " " + pos1.getZ() + " " + (-pos1.getY()) + ",");
            wl("            " + pos2.getX() + " " + pos1.getZ() + " " + (-pos2.getY()) + ",");
            wl("            " + pos2.getX() + " " + pos2.getZ() + " " + (-pos2.getY()) + ",");
            wl("            " + pos2.getX() + " " + pos2.getZ() + " " + (-pos1.getY()) + ",");
            wl("            '></Coordinate>");
            wl("        </IndexedLineSet>");
            wl("      </Shape>");
            wl("    </Transform>");

            for (int i = 0; i < s.getTerrains().size(); i++) {
                VgElevationGrid terr = s.getTerrains().get(i);
                GmSimple2dGridGeometry terrGeom = (GmSimple2dGridGeometry) terr.getGeometry();
                double scale = s.getScale();

                T3dVector upperLeftCornerNormalized = s.norm(new GmPoint(
                        terrGeom.envelope().getXMin(),
                        terrGeom.envelope().getYMax(),
                        0.0));
                double dx = upperLeftCornerNormalized.getX();
                double dz = -upperLeftCornerNormalized.getY();

                wl("    <Transform scale=\"1 " + s.getDefaultExaggeration() + " 1\">");
                w("       <Transform scale=\"" + scale + " " + scale + " " + scale + "\"");
                w(" translation=\"" + dx + " 0 " + dz + "\"");
                wl(">");

                T3dColor terrCol = s.getDefaultReliefColor();

                wl("        <Shape>");
                wl("          <Appearance>");
                wl("            <Material diffuseColor=\"" +
                        terrCol.getRed() + " " +
                        terrCol.getGreen() + " " +
                        terrCol.getBlue() + "\"></Material>");
                wl("          </Appearance>");
                wl("          <ElevationGrid " +
                        "xDimension='" + terrGeom.numberOfColumns() + "' " +
                        "zDimension='" + terrGeom.numberOfRows() + "' " +
                        "xSpacing='" + terrGeom.getDeltaX() + "' " +
                        "zSpacing='" + terrGeom.getDeltaY() + "' " +
                        "height='");

                // Write elevations:
                DecimalFormat dfZ = this.getDecimalFormatZ();
                for (int ii = terrGeom.numberOfRows() - 1; ii >= 0; ii--) {
                    for (int jj = 0; jj < terrGeom.numberOfColumns(); jj++) {
                        w(dfZ.format(terr.getValue(ii, jj)) + ",");
                    }
                    wl();
                }

                wl("          '>");

                if (s.getHypsometricColorMapper() != null)
                {
                    wl("          <Color color='");

                    for (int ii = terrGeom.numberOfRows() - 1; ii >= 0; ii--) {
                        for (int jj = 0; jj < terrGeom.numberOfColumns(); jj++) {
                            T3dColor col = s.getHypsometricColorMapper().transform(terr.getValue(ii, jj));
                            wl(col.getRed() + " " + col.getGreen() + " " + col.getBlue() + ",");
                        }
                    }

                    wl("          '></Color>");
                }

                wl("          </ElevationGrid>");

                wl("        </Shape>");
                wl("      </Transform>");
                wl("    </Transform>");
            }

            // Markers:

            if (s.getMarkers() != null)
            {
                wl("    <ProtoDeclare name='MarkerProto'>");
                wl("      <ProtoInterface>");
                wl("        <field name=\"color\" value=\"0.5 0.5 0.5\" type=\"SFColor\" accessType=\"inputOutput\"></field>");
                wl("        <field name=\"position\" value=\"0 0 0\" type=\"SFVec3f\" accessType=\"inputOutput\"></field>");
                wl("      </ProtoInterface>");
                wl("      <ProtoBody>");
                wl("        <Transform>");
                wl("          <IS><connect nodeField=\"translation\" protoField=\"position\"></connect></IS>");
                wl("          <Shape>");
                wl("            <Appearance>");
                wl("              <Material>");
                wl("                <IS><connect nodeField=\"diffuseColor\" protoField=\"color\"></connect></IS>");
                wl("              </Material>");
                wl("            </Appearance>");
                wl("            <Sphere radius=\"0.025\"></Sphere>"); // todo bislang werden alle marker als Kugeln dargestellt
                wl("          </Shape>");
                wl("        </Transform>");
                wl("      </ProtoBody>");
                wl("    </ProtoDeclare>");

                for (int i = 0; i < s.getMarkers().size(); i++) {
                    T3dSymbolInstance m = s.getMarkers().get(i);
                    T3dColor col = m.getColor();
                    T3dVector pos = s.norm(m.getPosition());

                    wl("    <ProtoInstance name=\"MarkerProto\">");
                    wl("      <fieldValue name=\"color\" value=\"" + col.getRed() + " " + col.getGreen() + " " + col.getBlue() + "\"></fieldValue>");
                    wl("      <fieldValue name=\"position\" value=\"" + pos.getX() + " " + (pos.getZ() * s.getDefaultExaggeration()) + " " + (-pos.getY())+ "\"></fieldValue>");
                    wl("    </ProtoInstance>");
                }
            }

            wl("  </Scene>");
            wl("</X3D>");

            if (pX3dom) {
                wl();
                wl("  </body>");
                wl("</html>");
            }

            mDoc.close();
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
    }

    private void w(String pLine) {
        try {
            mDoc.write(pLine);
        }
        catch (IOException e) {
            throw new T3dException(e.getMessage());
        }
    }

    private void wl(String pLine) {
        try {
            mDoc.write(pLine);
            mDoc.newLine();
        }
        catch (IOException e) {
            throw new T3dException(e.getMessage());
        }
    }

    private void wl() {
        try {
            mDoc.newLine();
        }
        catch (IOException e) {
            throw new T3dException(e.getMessage());
        }
    }
}
