/*
 * Copyright 2010-2024 Australian Signals Directorate
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
package au.gov.asd.tac.constellation.graph.schema.visual.attribute.compatibility;

import au.gov.asd.tac.constellation.graph.attribute.AttributeDescription;
import au.gov.asd.tac.constellation.graph.schema.visual.attribute.BlazeAttributeDescription;
import au.gov.asd.tac.constellation.graph.schema.visual.attribute.objects.Blaze;
import au.gov.asd.tac.constellation.graph.versioning.AttributeUpdateProvider;
import au.gov.asd.tac.constellation.graph.versioning.UpdateProvider;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author twilight_sparkle
 */
@SuppressWarnings("deprecation")
@ServiceProvider(service = UpdateProvider.class)
public class BlazeV1AttributeUpdateProvider extends AttributeUpdateProvider {

    private static final AttributeDescription FROM_ATTRIBUTE;
    private static final AttributeDescription TO_ATTRIBUTE;

    static {
        try {
            FROM_ATTRIBUTE = BlazeAttributeDescriptionV0.class.newInstance();
            TO_ATTRIBUTE = BlazeAttributeDescription.class.newInstance();
        } catch (IllegalAccessException | InstantiationException ex) {
            throw new IllegalArgumentException(String.format("Version provider %s unable to access required attribute descriptions %s or %s", BlazeV1AttributeUpdateProvider.class.getName(), BlazeAttributeDescriptionV0.class.getName(), BlazeAttributeDescription.class.getName()));
        }
    }

    @Override
    public AttributeDescription getAttributeDescription() {
        return FROM_ATTRIBUTE;
    }

    @Override
    public AttributeDescription getUpdatedAttributeDescription() {
        return TO_ATTRIBUTE;
    }

    @Override
    public Object updateAttributeValue(final Object value) {
        BlazeV0 v = (BlazeV0) value;
        return v == null ? null : new Blaze(v.getAngle(), v.getColor());
    }

}
