/*
 * Copyright 2013 Séven Le Mesle Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package fr.xebia.extras.selma.it.custom.mapper;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import fr.xebia.extras.selma.Selma;
import fr.xebia.extras.selma.it.utils.Compile;
import fr.xebia.extras.selma.it.utils.IntegrationTestBase;

@Compile(withClasses = CustomCdiSingletonMapperInMaps.class)
public class CustomCdiSingletonMapperInMapsIT extends IntegrationTestBase {
	
	@Test
	public void given_cdi_mapper_should_be_annotated_with_Inject_and_Singleton() {
		
		final CustomCdiMapperInMaps mapper = Selma.getMapper(CustomCdiMapperInMaps.class);
		assertThat(mapper.getClass().getAnnotation(javax.inject.Named.class), notNullValue());
		assertThat(mapper.getClass().getAnnotation(javax.inject.Singleton.class), notNullValue());
	}
}
