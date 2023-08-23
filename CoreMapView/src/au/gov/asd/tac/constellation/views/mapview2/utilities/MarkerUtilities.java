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
package au.gov.asd.tac.constellation.views.mapview2.utilities;

import au.gov.asd.tac.constellation.views.mapview2.MapView;

/**
 * Class that has functions to convert x and y coordinates to latitude and
 * longitude and vise versa
 *
 * @author altair1673
 */
public class MarkerUtilities {

    private MarkerUtilities() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Convert longitude to x coordinate
     *
     * @param longitude - the longitude in degrees
     * @param minLong - the left most longitude on map
     * @param mapWidth - width of map
     * @param lonDelta - different between right most longitude and left most
     * longitude
     * @return x coordinate
     */
    public static double longToX(final double longitude, final double minLong, final double mapWidth, final double lonDelta) {
        return (longitude - minLong) * (mapWidth / lonDelta);
    }

    private static double latToY(final double lat) {
        return Math.log(Math.tan((lat / 360 * Math.PI) + (Math.PI / 4)));
    }

    /**
     * Convert latitude to y coordinate
     *
     * @param lat - latitude in degrees
     * @param mapWidth
     * @param mapHeight
     * @return y coordinate
     */
    public static double latToY(final double lat, final double mapWidth, final double mapHeight) {
        final double lattitude = lat * (Math.PI / 180);
        final double y = Math.log(Math.tan((Math.PI / 4) + (lattitude / 2)));
        //return (mapHeight / 2) - (mapWidth * y / (2 * Math.PI));

        final double minY = latToY(MapView.MAX_LAT);
        final double maxY = latToY(MapView.MIN_LAT);

        return mapHeight * (latToY(lat) - minY) / (maxY - minY);
    }

    /**
     * Convert x coordinate to longitude
     *
     * @param x - x coordinate
     * @param minLong - left most longitude
     * @param mapWidth
     * @param lonDelta - difference between right and left most longitude
     * @return - longitude
     */
    public static double xToLong(final double x, final double minLong, final double mapWidth, final double lonDelta) {
        return (x / (mapWidth / lonDelta)) + minLong;
    }

    /**
     * Convert y coordinate to latitude
     *
     * @param yCoord
     * @param mapWidth
     * @param mapHeight
     * @return latitude
     */
    public static double yToLat(final double yCoord, final double mapWidth, final double mapHeight) {
        double y = ((-yCoord + (mapHeight / 2)) * (2 * Math.PI)) / mapWidth;
        y = (Math.atan(Math.exp(y)) - (Math.PI / 4)) * 2;
        return y / (Math.PI / 180);
    }

}
