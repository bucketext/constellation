/*
 * Copyright 2010-2021 Australian Signals Directorate
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
package au.gov.asd.tac.constellation.views.dataaccess.plugins.clean;

import au.gov.asd.tac.constellation.graph.GraphWriteMethods;
import au.gov.asd.tac.constellation.graph.schema.visual.VisualSchemaPluginRegistry;
import au.gov.asd.tac.constellation.plugins.PluginException;
import au.gov.asd.tac.constellation.plugins.PluginExecution;
import au.gov.asd.tac.constellation.plugins.PluginInteraction;
import au.gov.asd.tac.constellation.plugins.parameters.PluginParameter;
import au.gov.asd.tac.constellation.plugins.parameters.PluginParameterController;
import au.gov.asd.tac.constellation.plugins.parameters.PluginParameters;
import au.gov.asd.tac.constellation.plugins.parameters.types.BooleanParameterType.BooleanParameterValue;
import au.gov.asd.tac.constellation.plugins.parameters.types.IntegerParameterType.IntegerParameterValue;
import au.gov.asd.tac.constellation.plugins.parameters.types.SingleChoiceParameterType.SingleChoiceParameterValue;
import au.gov.asd.tac.constellation.views.dataaccess.DataAccessPluginCoreType;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author formalhaunt
 */
public class MergeNodesPluginNGTest {

    private MergeNodesPlugin mergeNodesPlugin;

    public MergeNodesPluginNGTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod
    public void setUpMethod() throws Exception {
        mergeNodesPlugin = new MergeNodesPlugin();
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
    }

    @Test
    public void getPosition() {
        assertEquals(mergeNodesPlugin.getPosition(), 100);
    }

    @Test
    public void getType() {
        assertEquals(mergeNodesPlugin.getType(), DataAccessPluginCoreType.CLEAN);
    }

    @Test
    public void getDescription() {
        assertEquals(mergeNodesPlugin.getDescription(), "Merge nodes in your graph together");
    }

    @Test
    public void createParameters() {
        final PluginParameters actual = mergeNodesPlugin.createParameters();

        final Map<String, PluginParameter<?>> parameters = actual.getParameters();

        assertEquals(parameters.keySet(), Set.of(
                "MergeNodesPlugin.merge_type",
                "MergeNodesPlugin.threshold",
                "MergeNodesPlugin.merger",
                "MergeNodesPlugin.lead",
                "MergeNodesPlugin.selected"));

        final PluginParameter<SingleChoiceParameterValue> mergeTypeParameter
                = (PluginParameter<SingleChoiceParameterValue>) parameters.get("MergeNodesPlugin.merge_type");

        assertEquals(mergeTypeParameter.getName(), "Merge By");
        assertEquals(mergeTypeParameter.getDescription(), "Nodes will be merged based on this");
        assertEquals(mergeTypeParameter.getParameterValue().getOptions(), List.of(
                TestMergeType.NAME,
                "Geospatial Distance",
                "Identifier Prefix Length",
                "Identifier Suffix Length",
                "Supported Type"));
        assertEquals(mergeTypeParameter.getProperty("choices").getClass(), Object.class);

        final PluginParameter<IntegerParameterValue> thresholdParameter
                = (PluginParameter<IntegerParameterValue>) parameters.get("MergeNodesPlugin.threshold");

        assertEquals(thresholdParameter.getName(), "Threshold");
        assertEquals(thresholdParameter.getDescription(), "The maximum nodes to merge");
        assertFalse(thresholdParameter.isEnabled());

        final PluginParameter<SingleChoiceParameterValue> mergingRuleParameter
                = (PluginParameter<SingleChoiceParameterValue>) parameters.get("MergeNodesPlugin.merger");

        assertEquals(mergingRuleParameter.getName(), "Merging Rule");
        assertEquals(mergingRuleParameter.getDescription(), "The rule deciding how attributes are merged");
        assertEquals(mergingRuleParameter.getParameterValue().getOptions(), List.of(
                "Retain lead vertex attributes if present",
                "Retain lead vertex attributes always",
                "Copy merged vertex attributes if present",
                "Copy merged vertex attributes always"
        ));
        assertEquals(mergingRuleParameter.getProperty("choices").getClass(), Object.class);
        assertEquals(mergingRuleParameter.getParameterValue().getChoice(), "Retain lead vertex attributes if present");
        assertFalse(mergingRuleParameter.isEnabled());

        final PluginParameter<SingleChoiceParameterValue> leadNodeParameter
                = (PluginParameter<SingleChoiceParameterValue>) parameters.get("MergeNodesPlugin.lead");

        assertEquals(leadNodeParameter.getName(), "Lead Node");
        assertEquals(leadNodeParameter.getDescription(), "The rule deciding how to choose the lead node");
        assertEquals(leadNodeParameter.getParameterValue().getOptions(), List.of(
                "Longest Value",
                "Shortest Value",
                "Ask Me"
        ));
        assertEquals(leadNodeParameter.getProperty("choices").getClass(), Object.class);
        assertEquals(leadNodeParameter.getParameterValue().getChoice(), "Longest Value");
        assertFalse(leadNodeParameter.isEnabled());

        final PluginParameter<BooleanParameterValue> selectedOnlyParameter
                = (PluginParameter<BooleanParameterValue>) parameters.get("MergeNodesPlugin.selected");

        assertEquals(selectedOnlyParameter.getName(), "Selected Only");
        assertEquals(selectedOnlyParameter.getDescription(), "Merge Only Selected Nodes");
        assertTrue(selectedOnlyParameter.getBooleanValue());
        assertFalse(selectedOnlyParameter.isEnabled());

        final Map<String, PluginParameterController> controllers = actual.getControllers();

        assertEquals(controllers.keySet(), Set.of("MergeNodesPlugin.merge_type"));
    }

    @Test(expectedExceptions = PluginException.class)
    public void editNoMergeOptionSelected() throws InterruptedException, PluginException {
        final GraphWriteMethods graph = mock(GraphWriteMethods.class);
        final PluginInteraction interaction = mock(PluginInteraction.class);
        final PluginParameters parameters = mock(PluginParameters.class);
        final PluginParameter pluginParameter = mock(PluginParameter.class);

        final Map<String, PluginParameter<?>> pluginParameters = Map.of("MergeNodesPlugin.merge_type", pluginParameter);

        when(parameters.getParameters()).thenReturn(pluginParameters);
        when(pluginParameter.getStringValue()).thenReturn(null);

        mergeNodesPlugin.edit(graph, interaction, parameters);
    }

    @Test(expectedExceptions = PluginException.class)
    public void editMergeNodeTypeNotFound() throws InterruptedException, PluginException {
        final GraphWriteMethods graph = mock(GraphWriteMethods.class);
        final PluginInteraction interaction = mock(PluginInteraction.class);
        final PluginParameters parameters = mock(PluginParameters.class);
        final PluginParameter pluginParameter = mock(PluginParameter.class);

        final Map<String, PluginParameter<?>> pluginParameters = Map.of("MergeNodesPlugin.merge_type", pluginParameter);

        when(parameters.getParameters()).thenReturn(pluginParameters);
        when(pluginParameter.getStringValue()).thenReturn("Something Random");

        mergeNodesPlugin.edit(graph, interaction, parameters);
    }

    @Test(expectedExceptions = PluginException.class)
    public void editMergeError() throws InterruptedException, PluginException, MergeNodeType.MergeException {
        final GraphWriteMethods graph = mock(GraphWriteMethods.class);
        final PluginInteraction interaction = mock(PluginInteraction.class);
        final PluginParameters parameters = mock(PluginParameters.class);

        final PluginParameter mergeTypeParameter = mock(PluginParameter.class);
        final PluginParameter thresholdParameter = mock(PluginParameter.class);
        final PluginParameter mergerParameter = mock(PluginParameter.class);
        final PluginParameter leadParameter = mock(PluginParameter.class);
        final PluginParameter selectedParameter = mock(PluginParameter.class);

        final Map<String, PluginParameter<?>> pluginParameters = Map.of(
                "MergeNodesPlugin.merge_type", mergeTypeParameter,
                "MergeNodesPlugin.threshold", thresholdParameter,
                "MergeNodesPlugin.merger", mergerParameter,
                "MergeNodesPlugin.lead", leadParameter,
                "MergeNodesPlugin.selected", selectedParameter
        );

        when(parameters.getParameters()).thenReturn(pluginParameters);

        when(mergeTypeParameter.getStringValue()).thenReturn(TestMergeType.NAME);
        when(thresholdParameter.getIntegerValue()).thenReturn(TestMergeType.MERGE_EXCEPTION_THRESHOLD);
        when(mergerParameter.getStringValue()).thenReturn("Retain lead vertex attributes if present");
        when(leadParameter.getStringValue()).thenReturn("Longest Value");
        when(selectedParameter.getBooleanValue()).thenReturn(true);

        mergeNodesPlugin.edit(graph, interaction, parameters);
    }

    @Test
    public void edit() throws InterruptedException, PluginException, MergeNodeType.MergeException {
        final GraphWriteMethods graph = mock(GraphWriteMethods.class);
        final PluginInteraction interaction = mock(PluginInteraction.class);
        final PluginParameters parameters = mock(PluginParameters.class);
        final PluginExecution pluginExecution = mock(PluginExecution.class);

        final PluginParameter mergeTypeParameter = mock(PluginParameter.class);
        final PluginParameter thresholdParameter = mock(PluginParameter.class);
        final PluginParameter mergerParameter = mock(PluginParameter.class);
        final PluginParameter leadParameter = mock(PluginParameter.class);
        final PluginParameter selectedParameter = mock(PluginParameter.class);

        final Map<String, PluginParameter<?>> pluginParameters = Map.of(
                "MergeNodesPlugin.merge_type", mergeTypeParameter,
                "MergeNodesPlugin.threshold", thresholdParameter,
                "MergeNodesPlugin.merger", mergerParameter,
                "MergeNodesPlugin.lead", leadParameter,
                "MergeNodesPlugin.selected", selectedParameter
        );

        when(parameters.getParameters()).thenReturn(pluginParameters);

        when(mergeTypeParameter.getStringValue()).thenReturn(TestMergeType.NAME);
        when(thresholdParameter.getIntegerValue()).thenReturn(TestMergeType.MERGE_SUCCESS_THRESHOLD);
        when(mergerParameter.getStringValue()).thenReturn("Retain lead vertex attributes if present");
        when(leadParameter.getStringValue()).thenReturn("Longest Value");
        when(selectedParameter.getBooleanValue()).thenReturn(true);

        doReturn(null).when(pluginExecution).executeNow(graph);

        try (MockedStatic<PluginExecution> pluginExecutionMockedStatic
                = Mockito.mockStatic(PluginExecution.class)) {
            pluginExecutionMockedStatic.when(() -> PluginExecution.withPlugin(VisualSchemaPluginRegistry.COMPLETE_SCHEMA))
                    .thenReturn(pluginExecution);

            mergeNodesPlugin.edit(graph, interaction, parameters);

            verify(pluginExecution).executeNow(graph);
            verify(interaction).setProgress(1, 0, "Merged 2 nodes.", true);

            // Due to accessibility issues the call to mergeVerticies and its follow
            // on logic cannot be verified without tying this test to the logic of
            // one of the concrete implementations of GraphElementMerger.
        }
    }
}
