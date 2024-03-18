/*
* Copyright 2010-2023 Australian Signals Directorate
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
 */
package au.gov.asd.tac.constellation.plugins.importexport.svg.tasks;

import au.gov.asd.tac.constellation.plugins.importexport.svg.GraphVisualisationReferences;
import au.gov.asd.tac.constellation.plugins.importexport.svg.resources.SVGObjectConstants;
import au.gov.asd.tac.constellation.plugins.importexport.svg.resources.SVGTemplateConstants;
import au.gov.asd.tac.constellation.utilities.color.ConstellationColor;
import au.gov.asd.tac.constellation.utilities.file.FileNameCleaner;
import au.gov.asd.tac.constellation.utilities.graphics.Vector4f;
import au.gov.asd.tac.constellation.utilities.icon.ConstellationIcon;
import au.gov.asd.tac.constellation.utilities.icon.DefaultIconProvider;
import au.gov.asd.tac.constellation.utilities.icon.FileIconData;
import au.gov.asd.tac.constellation.utilities.icon.IconData;
import au.gov.asd.tac.constellation.utilities.icon.IconManager;
import au.gov.asd.tac.constellation.utilities.svg.SVGAttributeConstants;
import au.gov.asd.tac.constellation.utilities.svg.SVGData;
import au.gov.asd.tac.constellation.utilities.svg.SVGObject;
import au.gov.asd.tac.constellation.utilities.svg.SVGParser;
import au.gov.asd.tac.constellation.utilities.svg.SVGTypeConstants;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * A runnable task designed to build SVG assets representing graph nodes.
 * This task is designed to run concurrently and can represent anything from the process 
 * of building one node or the process o building all nodes on one thread.
 * 
 * @author capricornunicorn123
 */
public class GenerateSVGNodesTask implements Runnable, ThreadWithCommonPluginInteraction {

    private final GraphVisualisationReferences graph;
    private final List<Integer> vertexIndicies;
    private final List<SVGObject> output;
    private final int totalSteps;
    private int currentStep;
    private boolean complete = false;
    
    public GenerateSVGNodesTask(final GraphVisualisationReferences graph, final List<Integer> vertexIndicies, final List<SVGObject> output){
        this.graph = graph;
        this.vertexIndicies = vertexIndicies;
        this.output = output;
        this.totalSteps = vertexIndicies.size();
    }

    @Override
    public void run() {
        try {
            graph.initialise();
            vertexIndicies.forEach(vertexIndex -> {
                if (graph.inView(vertexIndex) && (!graph.selectedElementsOnly || graph.isVertexSelected(vertexIndex)) && graph.getVertexVisibility(vertexIndex) > 0) {

                    // Retrieve values of relevent vertex attributes
                    final Vector4f position = graph.getVertexPosition(vertexIndex);
                    final float radius = graph.getVertexScaledRadius(vertexIndex);
                    final ConstellationColor color = graph.getVertexColor(vertexIndex);
                    final ConstellationIcon backgroundIcon = IconManager.getIcon(graph.getBackgroundIcon(vertexIndex));
                    final ConstellationIcon foregroundIcon = IconManager.getIcon(graph.getForegroundIcon(vertexIndex));

                    // Build the SVGobject representing the Node
                    final SVGObject svgNode = SVGObject.loadFromTemplate(SVGTemplateConstants.NODE);
                    svgNode.setPosition(position.getX() - radius, position.getY() - radius);
                    svgNode.setID(String.format("node-%s", graph.getVertexId(vertexIndex)));
                    svgNode.setSortOrderValue(position.getW());
                    svgNode.setDimension(radius * 2, radius * 2);
                    output.add(svgNode);

                    // Add labels to the Node if required
                    if (graph.exportNodeLabels()) {
                        buildTopLabel(vertexIndex, svgNode);
                        buildBottomLabel(vertexIndex, svgNode);
                    } else {
                        SVGObjectConstants.TOP_LABELS.removeFrom(svgNode);
                        SVGObjectConstants.BOTTOM_LABELS.removeFrom(svgNode);
                    }          

                    // Add background image to the node
                    final SVGData svgBackgroundImage = getSVGIcon(backgroundIcon, color.getJavaColor()); 
                    if (svgBackgroundImage != null) {
                        svgBackgroundImage.setParent(SVGObjectConstants.BACKGROUND_IMAGE.findIn(svgNode));
                    } else {
                        SVGObjectConstants.BACKGROUND_IMAGE.removeFrom(svgNode);
                    }

                    // Add foreground image to the node
                    final SVGData svgForegroundImage = getSVGIcon(foregroundIcon, null);
                    if (svgForegroundImage != null) {
                        svgForegroundImage.setParent(SVGObjectConstants.FOREGROUND_IMAGE.findIn(svgNode));
                    } else {
                        SVGObjectConstants.FOREGROUND_IMAGE.removeFrom(svgNode);
                    }

                    // Add decorators to the node       
                    this.buildDecorator(SVGObjectConstants.NORTH_WEST_DECORATOR.findIn(svgNode), graph.getNWDecorator(vertexIndex));
                    this.buildDecorator(SVGObjectConstants.NORTH_EAST_DECORATOR.findIn(svgNode), graph.getNEDecorator(vertexIndex));
                    this.buildDecorator(SVGObjectConstants.SOUTH_WEST_DECORATOR.findIn(svgNode), graph.getSWDecorator(vertexIndex));
                    this.buildDecorator(SVGObjectConstants.SOUTH_EAST_DECORATOR.findIn(svgNode), graph.getSEDecorator(vertexIndex));

                    // Add dimmed property if dimmed
                    // Note, this implementation is not a precice sollution, luminocity to alpha conversion would be better
                    if (graph.isVertexDimmed(vertexIndex)) {
                        SVGObjectConstants.NODE_IMAGES.findIn(svgNode).applyGrayScaleFilter();
                        SVGObjectConstants.BACKGROUND_IMAGE.findIn(svgNode).setOpacity(0.5F);
                    }
                }
                currentStep++;
            });
        } finally {
            graph.terminate();
            complete = true;
        }
    }
    /**
     * Generates decorator images for Nodes.
     * @param svgDecorator
     * @param decoratorName
     */
    private void buildDecorator(final SVGObject svgDecorator, final String decoratorName) {
        
        // Do not build a decorator if the decorator is for a Pinned attribute value of false.
        if (decoratorName != null && !"false_pinned".equals(decoratorName) && IconManager.iconExists(decoratorName)) {
            final SVGData svgIcon = getSVGIcon(IconManager.getIcon(decoratorName), null);
            svgIcon.setParent(svgDecorator);
        } else {
            svgDecorator.getParent().removeChild(svgDecorator.getID());
        }
    }
    
    /**
     * Constructs bottom label SVG elements for a given vertex.
     * This method considers the bottom label requirements for nodes.
     * @param vertexIndex
     * @param svgBottomLabels
     */
    private void buildBottomLabel(final int vertexIndex, final SVGObject svgNode) {    
        
        final SVGObject svgBottomLabels = SVGObjectConstants.BOTTOM_LABELS.findIn(svgNode);
        
        // Track the distance bewteen the top of the svgBottomLabels element and the bottom of the most recently created svgLabel
        float offset = 0;
        for (int labelIndex = 0; labelIndex < graph.getBottomLabelCount(); labelIndex++) {
            final String labelString = graph.getVertexBottomLabelText(vertexIndex, labelIndex);
            
            // Only add the label if the label value exists.
            if (labelString != null) {
                final SVGObject svgLabel = SVGTemplateConstants.LABEL.getSVGObject();
                final float size = graph.getBottomLabelSize(labelIndex) * 64;
                svgLabel.setFontSize(size);
                svgLabel.setYPosition(offset);
                svgLabel.setFillColor(graph.getBottomLabelColor(labelIndex));
                svgLabel.setBaseline("hanging");
                svgLabel.setID(String.format("bottom-label-%s", labelIndex));
                svgLabel.setContent(labelString);
                svgLabel.setParent(svgBottomLabels);
                offset = offset + size;
            }
        }
    }
    
    /**
     * Constructs top label SVG elements for a given vertex.
     * This method considers the bottom label requirements for nodes.
     * @param vertexIndex
     * @param svgTopLabels
     */
    private void buildTopLabel(final int vertexIndex, final SVGObject svgNode) {
        
        final SVGObject svgTopLabels = SVGObjectConstants.TOP_LABELS.findIn(svgNode);
        
        // Track the distance bewteen the bottom of the svgTopLabels element and the top of the most recently created svgLabel
        float offset = 0;
        for (int labelIndex = 0; labelIndex < graph.getTopLabelCount(); labelIndex++) {
            final String labelString = graph.getVertexTopLabelText(vertexIndex, labelIndex);
            
            // Only add the label if the label value exists.
            if (labelString != null) {
                final SVGObject svgLabel = SVGTemplateConstants.LABEL.getSVGObject();
                final float size = graph.getTopLabelSize(labelIndex) * 64;
                svgLabel.setFontSize(size);
                svgLabel.setYPosition(offset);
                svgLabel.setFillColor(graph.getTopLabelColor(labelIndex));
                svgLabel.setBaseline("after-edge");
                svgLabel.setID(String.format("top-label-%s", labelIndex));
                svgLabel.setContent(labelString);
                svgLabel.setParent(svgTopLabels);
                offset = offset - size;
            }
        }
    }

    @Override
    public int getTotalSteps() {
        return totalSteps;
    }

    @Override
    public int getCurrentStep() {
        return currentStep;
    }

    @Override
    public boolean isComplete() {
        return complete;
    }
    
    /**
     * Generates an SVG Icon with either a SVG asset, an embedded image or an externally referenced image.
     * @param icon
     * @param color
     * @return 
     */
    private SVGData getSVGIcon(ConstellationIcon icon, Color color){
        final SVGData svgIcon;
        
        // Some common icons are not visable and should not be exported
        if (DefaultIconProvider.isVisable(icon)){
            svgIcon = icon.buildSVG(color);
            
            // The SVGIcon built by the ConstellationIcon should be used in all cases except or then an external image directory is provided and the icon has an imbedded raster image.
            if (graph.directory.exists() && svgIcon.getType().equals(SVGTypeConstants.IMAGE.getTypeString())){

                // Build the name of the referenced Raster Image
                final StringBuilder fileNameBuilder = new StringBuilder();                
                
                if (color != null) {
                    fileNameBuilder.append(ConstellationColor.fromJavaColor(color).getHtmlColor().substring(1, 7));
                    fileNameBuilder.append(" ");
                }
                
                if (icon.getIconData() instanceof FileIconData){
                    FileIconData iconData = (FileIconData) icon.getIconData();
                    fileNameBuilder.append(new File(iconData.getFilePath()).getName());
                    
                } else { 
                    fileNameBuilder.append(icon.getExtendedName());
                    fileNameBuilder.append(".png");
                }
                

                
                
                final String fileName = FileNameCleaner.cleanFileName(fileNameBuilder.toString());
                
                // As the raster image refwerence will have a parent folder that sits in the same folder as the output svg file, a relative path can be generated.
                final StringBuilder relativePathBuilder = new StringBuilder();
                relativePathBuilder.append(".");
                relativePathBuilder.append(File.separator);
                relativePathBuilder.append(graph.directory.getName());
                relativePathBuilder.append(File.separator);
                relativePathBuilder.append(SVGParser.sanitisePlanText(fileName));

                // Update the svgIcons image reference to be the realitive path to th image. 
                svgIcon.setAttribute(SVGAttributeConstants.HREF, relativePathBuilder.toString());
                
                // Build the complete path to the raster image reference.
                final StringBuilder completePathBuilder = new StringBuilder();
                completePathBuilder.append(graph.directory.getAbsolutePath());
                completePathBuilder.append(File.separator);
                completePathBuilder.append(fileName);
 
                // Save the reference image file.
                try {
                    
                    // Other threads or nodes may have already saved this file so only save the file if it does not already exist.  
                    final File outputFile = new File(completePathBuilder.toString());
                    if (!outputFile.exists()){
                        ImageIO.write(icon.buildBufferedImage(color), "png", outputFile);
                    }
                } catch (IOException ex) {
                    //TODO: throw an exception here
                }

            }
        } else {
            svgIcon = null;
        }
        return svgIcon;
    }
}
