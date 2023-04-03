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
package au.gov.asd.tac.constellation.views.welcome.plugins;

import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.testfx.api.FxToolkit;
import static org.testng.AssertJUnit.assertEquals;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test class for OpenGraphWelcomePlugin
 *
 * @author Delphinus8821
 */
public class OpenGraphWelcomePluginNGTest {

    private static final Logger LOGGER = Logger.getLogger(OpenGraphWelcomePluginNGTest.class.getName());

    @BeforeClass
    public static void setUpClass() throws Exception {
        if (!FxToolkit.isFXApplicationThreadRunning()) {
            FxToolkit.registerPrimaryStage();
        }
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        try {
            FxToolkit.cleanupStages();
        } catch (TimeoutException ex) {
            LOGGER.log(Level.WARNING, "FxToolkit timed out trying to cleanup stages", ex);
        }
    }

    /**
     * Test of getName method, of class OpenGraphWelcomePlugin.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        final OpenGraphWelcomePlugin instance = new OpenGraphWelcomePlugin();
        final String expResult = "Open Graph Welcome";
        final String result = instance.getName();
        assertEquals(result, expResult);
    }

    /**
     * Test of getButton method, of class OpenGraphWelcomePlugin.
     */
    @Test
    public void testGetButton() {
        System.out.println("getButton");
        final OpenGraphWelcomePlugin instance = new OpenGraphWelcomePlugin();
        final VBox expResult = new VBox();
        final Button result = instance.getButton();
        assertEquals(result.getGraphic().getClass(), expResult.getClass());
    }

}
