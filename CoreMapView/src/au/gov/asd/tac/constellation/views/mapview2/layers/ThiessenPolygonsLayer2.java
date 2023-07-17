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
package au.gov.asd.tac.constellation.views.mapview2.layers;

import au.gov.asd.tac.constellation.utilities.color.ConstellationColor;
import au.gov.asd.tac.constellation.views.mapview2.MapView;
import au.gov.asd.tac.constellation.views.mapview2.markers.AbstractMarker;
import au.gov.asd.tac.constellation.views.mapview2.markers.PointMarker;
import au.gov.asd.tac.constellation.views.mapview2.markers.UserPointMarker;
import au.gov.asd.tac.constellation.views.mapview2.utilities.EdgeEvent;
import au.gov.asd.tac.constellation.views.mapview2.utilities.Parabola;
import au.gov.asd.tac.constellation.views.mapview2.utilities.SiteEvent;
import au.gov.asd.tac.constellation.views.mapview2.utilities.Vec3;
import au.gov.asd.tac.constellation.views.mapview2.utilities.VoronoiEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Group;
import javafx.scene.shape.Rectangle;

/**
 * More efficient away of calculating closest zones but will implement in the
 * future
 *
 * @author altair1673
 */
public class ThiessenPolygonsLayer2 extends AbstractMapLayer {

    private static final Logger LOGGER = Logger.getLogger(ThiessenPolygonsLayer2.class.getName());

    private final Group layer;
    private final Map<Integer, AbstractMarker> nodesOnScreen = new HashMap<>();

    private List<AbstractMarker> markers = new ArrayList<>();
    private final List<Vec3> markersYSorted = new ArrayList<>();

    private final PriorityQueue<VoronoiEvent> eventQueue;

    public ThiessenPolygonsLayer2(final MapView parent, final int id, final List<AbstractMarker> markers) {
        super(parent, id);

        layer = new Group();
        this.markers = markers;

        eventQueue = new PriorityQueue<VoronoiEvent>((v1, v2) -> {
            if (v1.getYCoord() > v2.getYCoord()) {
                return 1;
            }

            return -1;
        });
    }

    @Override
    public void setUp() {
        layer.getChildren().clear();

        if (nodesOnScreen.size() == 1) {
            final ConstellationColor[] palette = ConstellationColor.createPalette(1);

            Rectangle r = new Rectangle();
            r.setX(0);
            r.setY(0);
            r.setWidth(MapView.MAP_WIDTH);
            r.setHeight(MapView.MAP_HEIGHT);
            r.setFill(palette[0].getJavaFXColor());
            r.setStroke(palette[0].getJavaFXColor());
            r.setOpacity(0.5);
            r.setMouseTransparent(true);
            layer.getChildren().add(r);
        } else {
            for (final AbstractMarker m : markers) {
                if (m instanceof PointMarker || m instanceof UserPointMarker) {

                    double x = m.getX() - 97;
                    double y = m.getY() + 93;

                    final SiteEvent siteEvent = new SiteEvent(y, new Vec3(x, y));

                    eventQueue.add(siteEvent);
                }
            }

            calculateVoronoi();
        }

    }

    private void calculateVoronoi() {
        while (!eventQueue.isEmpty()) {
            LOGGER.log(Level.SEVERE, "Y coord: " + eventQueue.poll().getYCoord());

            if (eventQueue.peek() instanceof SiteEvent) {
                final SiteEvent e = (SiteEvent) eventQueue.poll();


            } else if (eventQueue.peek() instanceof EdgeEvent) {

            }
        }
    }

    @Override
    public Group getLayer() {
        return layer;
    }

}
