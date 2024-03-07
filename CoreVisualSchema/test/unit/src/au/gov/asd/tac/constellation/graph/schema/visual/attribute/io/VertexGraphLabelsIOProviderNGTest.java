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
package au.gov.asd.tac.constellation.graph.schema.visual.attribute.io;

import au.gov.asd.tac.constellation.graph.schema.visual.attribute.VertexGraphLabelsAttributeDescription;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author serpens24
 */
import au.gov.asd.tac.constellation.utilities.testing.ConstellationTest; 
 public class VertexGraphLabelsIOProviderNGTest extends ConstellationTest {
    
    // Create object under test
    VertexGraphLabelsIOProvider instance;

    public VertexGraphLabelsIOProviderNGTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod
    public void setUpMethod() throws Exception {
        instance = new VertexGraphLabelsIOProvider();
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
    }

    /**
     * Test of getName method, of class VertexGraphLabelsIOProvider.
     */
    @Test
    public void testGetName() {
        System.out.println("VertexGraphLabelsIOProvider.testGetName");
        assertEquals(instance.getName(), VertexGraphLabelsAttributeDescription.ATTRIBUTE_NAME);
    } 
}
