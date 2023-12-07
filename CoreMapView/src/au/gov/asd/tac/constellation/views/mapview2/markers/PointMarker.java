/*
 * Copyright 2010-2022 Australian Signals Directorate
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
package au.gov.asd.tac.constellation.views.mapview2.markers;

import au.gov.asd.tac.constellation.utilities.color.ConstellationColor;
import au.gov.asd.tac.constellation.utilities.text.SeparatorConstants;
import au.gov.asd.tac.constellation.views.mapview2.MapView;
import au.gov.asd.tac.constellation.views.mapview2.MapViewPane;
import au.gov.asd.tac.constellation.views.mapview2.utilities.MapConversions;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.apache.commons.lang3.StringUtils;

/**
 * Point marker that represents a geo coordinate on the map
 *
 * @author altair1673
 */
public class PointMarker extends AbstractMarker {

    // The actual marker path raw string
    private String path = "l-75-200 l150 0 Z";
    private double lattitude;
    private double longitude;
    private double x = 0;
    private double y = 0;
    private double scale;

    private String defaultColour = "#FF0000";
    private String multiValCol = "#D3D3D3";
    private String attributeColour = defaultColour;
    private String blazeColour = null;
    private int blazeColourCount = 0;
    private int overlayColourCount = 0;
    private String overlayColour = null;
    private String currentColour = defaultColour;

    private String labelAttr = null;
    private int labelAttrCount = 0;

    private String identifierAttr = null;
    private int identifierCount = 0;

    private Rectangle posRect = new Rectangle();

    public PointMarker(final MapView parent, final int markerID, final int nodeId, final double lattitude, final double longitude, final double scale, final double xOffset, final double yOffset, final String attrColour) {
        super(parent, markerID, nodeId, xOffset, yOffset, AbstractMarker.MarkerType.POINT_MARKER);

        this.lattitude = lattitude;
        this.longitude = longitude;
        this.scale = scale;
        this.attributeColour = StringUtils.isBlank(attrColour) ? defaultColour : attrColour;

        markerPath.setScaleX(scale);
        markerPath.setScaleY(scale);

        markerPath.setFill(Color.web(currentColour));
        markerPath.setStroke(Color.BLACK);
        markerPath.setOpacity(0.6);
        markerPath.setStrokeWidth(5);

        // Event handlers for the marker
        markerPath.setOnMouseEntered((final MouseEvent e) -> {
            if (!isSelected) {
                markerPath.setFill(Color.ORANGE);
            }
            e.consume();
        });

        markerPath.setOnMouseExited((final MouseEvent e) -> {
            if (!isSelected) {
                markerPath.setFill(Color.web(currentColour));
            }
            e.consume();
        });

        markerPath.setOnMouseClicked((final MouseEvent e) -> {
            parent.deselectAllMarkers();
            select();
            parent.addMarkerIdToSelectedList(markerID, idList, true);
            e.consume();
        });

    }

    @Override
    public void deselect() {
        markerPath.setFill(Color.web(currentColour));
        isSelected = false;
    }

    @Override
    public void select() {
        isSelected = true;
        markerPath.setFill(Color.BLUE);
    }

    /**
     * Change marker colour based on marker colour type chosen by the user
     *
     * @param option
     */
    @Override
    public void changeMarkerColour(final String option) {
        // Depending on the option change the colour of the marker
        if (option.equals(MapViewPane.DEFAULT_COLOURS)) {
            currentColour = defaultColour;
            markerPath.setFill(Color.web(currentColour));
        } else if (option.equals(MapViewPane.USE_COLOUR_ATTR)) {
            if (idList.size() > 1) {
                currentColour = multiValCol;
                markerPath.setFill(Color.web(currentColour));
            } else {

                markerPath.setFill(Color.web(attributeColour));
                currentColour = attributeColour;

            }
        } else if (option.equals(MapViewPane.USE_BLAZE_COL)) {
            if (blazeColour != null) {
                final ConstellationColor colour = ConstellationColor.getColorValue(blazeColour);
                if (blazeColourCount == 1) {
                    currentColour = colour.getHtmlColor();
                    markerPath.setFill(Color.web(currentColour));
                } else {
                    markerPath.setFill(Color.web(multiValCol));
                    currentColour = multiValCol;
                }

            } else {
                markerPath.setFill(Color.web(defaultColour));
            }
        } else if (option.equals(MapViewPane.USE_OVERLAY_COL)) {
            if (overlayColour != null) {
                final ConstellationColor colour = ConstellationColor.getColorValue(overlayColour);

                if (overlayColourCount == 1) {
                    currentColour = colour.getHtmlColor();
                    markerPath.setFill(Color.web(currentColour));
                } else {
                    markerPath.setFill(Color.web(multiValCol));
                    currentColour = multiValCol;
                }

            } else {
                markerPath.setFill(Color.web(defaultColour));
            }
        }

    }

    public double getLattitude() {
        return lattitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(final double scale) {
        this.scale = scale;
    }

    public String getPath() {
        return path;
    }

    /**
     * Sets the marker position on the map
     *
     * @param mapWidth
     * @param mapHeight
     */
    @Override
    public void setMarkerPosition(final double mapWidth, final double mapHeight) {
        x = MapConversions.lonToMapX(longitude);
        y = MapConversions.latToMapY(lattitude);

        super.setX(x);
        super.setY(y);

        posRect.setX(x);
        posRect.setY(y);
        posRect.setWidth(1);
        posRect.setHeight(1);
        posRect.setFill(Color.GREEN);
        posRect.setOpacity(0.5);

        x += xOffset;
        y += yOffset;
        path = "M " + x + SeparatorConstants.COMMA + " " + y + " Z " + path;


        markerPath.setContent(path);
    }

    public void applyOffsets(final double xOffset, final double yOffset) {
        x += xOffset;
        y += yOffset;
    }

    /**
     * Sets one of the colour types of the marker
     *
     * @param blazeCol
     */
    public void setBlazeColour(final String blaze) {
        // Get the blaze colure in the correct format
        final String blazeCol = blaze.split(SeparatorConstants.SEMICOLON)[1];

        if (blazeColourCount == 0) {
            blazeColour = blazeCol;
        }

        blazeColourCount++;
    }

    public void setOverlayColour(final String overlayCol) {

        if (overlayColourCount == 0) {
            overlayColour = overlayCol;
        }

        overlayColourCount++;
    }

    public String getBlazeColour() {
        return blazeColour;
    }

    @Override
    public double getX() {
        return this.x;
    }

    @Override
    public double getY() {
        return this.y;
    }

    public Rectangle getPosRect() {
        return posRect;
    }

    public void setLabelAttr(final String labelAttribute) {

        if (labelAttrCount == 0) {
            labelAttr = labelAttribute;
        }

        labelAttrCount++;
    }

    public void setIdentAttr(final String identAttribute) {

        if (identifierCount == 0) {
            identifierAttr = identAttribute;
        }

        identifierCount++;
    }

    public String getLabelAttr() {

        if (labelAttrCount > 1) {
            return "<Multiple Values>";
        }

        return labelAttr;
    }

    public String getIdentAttr() {

        if (identifierCount > 1) {
            return "<Multiple Values>";
        }

        return identifierAttr;
    }

    public String getCurrentColour() {
        return currentColour;
    }

    public String getDefaultColour() {
        return defaultColour;
    }

    public void scaleAndReposition(final double scale) {
        setScale(scale);
        markerPath.setScaleX(scale);
        markerPath.setScaleY(scale);

        final double heightDifference = (getY()) - (markerPath.getBoundsInParent().getCenterY() + (markerPath.getBoundsInParent().getHeight() / 2));
        markerPath.setTranslateY(markerPath.getTranslateY() + heightDifference);
    }

}
