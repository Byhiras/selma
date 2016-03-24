/*
 * Copyright 2013 Xebia and Séven Le Mesle
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
 *
 */
package fr.xebia.extras.selma.it.mappers;

import fr.xebia.extras.selma.Builder;
import fr.xebia.extras.selma.EnumMapper;
import fr.xebia.extras.selma.Mapper;
import fr.xebia.extras.selma.beans.*;

/**
 * Test that builder that returns sub-class of target type is matched successfully.  Also tests that enums
 * are excluded from builders, even if a builder config matches them.
 */
@Mapper(
        withEnums = @EnumMapper(from = EnumA.class, to = EnumB.class),
        withBuilders = {
                @Builder(classes = BuilderBeanOut.class, builderClass = ExtendedBuilderBeanOut.Builder.class),
                @Builder(classPatterns = "^fr\\.xebia\\.extras\\.selma\\.beans\\.")
        }
)
public interface BuilderExtendedMapper {
    BuilderBeanOut asBuilderOut(ExtendedBuilderBeanIn in);
}
