/*
 * Copyright 2013  Séven Le Mesle
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
package fr.xebia.extras.selma.it;

import fr.xebia.extras.selma.Selma;
import fr.xebia.extras.selma.beans.SimplePerson;
import fr.xebia.extras.selma.beans.SimplePersonDto;
import fr.xebia.extras.selma.it.mappers.CustomFieldMapper;
import fr.xebia.extras.selma.it.utils.Compile;
import fr.xebia.extras.selma.it.utils.IntegrationTestBase;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by slemesle on 12/03/2014.
 */
@Compile(withClasses = {CustomFieldMapper.class})
public class CustomFieldMapperIT extends IntegrationTestBase {

    @Test
    public void given_a_mapper_with_custom_field_mapping_should_use_described_name_to_name_for_mapping() {
        CustomFieldMapper mapper = Selma.mapper(CustomFieldMapper.class);

        SimplePerson person = new SimplePerson();
        person.setNom("toto");
        person.setPrenom("tutu");

        SimplePersonDto personDto = mapper.asPersonDto(person);

        Assert.assertEquals(person.getPrenom(), personDto.getFirstName());
        Assert.assertEquals(person.getNom(), personDto.getLastName());
    }

    @Test
    public void given_a_mapper_update_graph_with_custom_field_mapping_should_use_described_name_to_name_for_mapping() {
        CustomFieldMapper mapper = Selma.mapper(CustomFieldMapper.class);

        SimplePerson person = new SimplePerson();
        person.setNom("toto");
        person.setPrenom("tutu");

        SimplePersonDto dest = new SimplePersonDto();
        SimplePersonDto res = mapper.asPersonDto(person, dest);

        Assert.assertTrue(dest == res);
        Assert.assertEquals(person.getPrenom(), res.getFirstName());
        Assert.assertEquals(person.getNom(), res.getLastName());
    }

    @Test
    public void given_a_mapper_with_custom_field_mapping_should_use_default_same_field_name_for_mapping() {
        CustomFieldMapper mapper = Selma.mapper(CustomFieldMapper.class);

        SimplePerson person = new SimplePerson();
        person.setAge(12);

        SimplePersonDto personDto = mapper.asPersonDto(person);

        Assert.assertEquals(person.getAge(), personDto.getAge());
    }


    @Test
    public void given_a_mapper_with_custom_field_mapping_method_should_use_default_same_field_name_for_mapping() {
        CustomFieldMapper mapper = Selma.mapper(CustomFieldMapper.class);

        SimplePerson person = new SimplePerson();
        person.setNom("toto");
        person.setPrenom("tutu");
        person.setAge(12);

        SimplePersonDto personDto = mapper.asPersonDtoReverseName(person);

        Assert.assertEquals(person.getAge(), personDto.getAge());

        Assert.assertEquals(person.getPrenom(), personDto.getLastName());
        Assert.assertEquals(person.getNom(), personDto.getFirstName());
    }

    @Test
    public void given_a_mapper_update_graph_with_custom_field_mapping_method_should_use_default_same_field_name_for_mapping() {
        CustomFieldMapper mapper = Selma.mapper(CustomFieldMapper.class);

        SimplePerson person = new SimplePerson();
        person.setNom("toto");
        person.setPrenom("tutu");
        person.setAge(12);

        SimplePersonDto dest = new SimplePersonDto();
        SimplePersonDto res = mapper.asPersonDtoReverseName(person, dest);

        Assert.assertTrue(dest == res);

        Assert.assertEquals(person.getAge(), res.getAge());

        Assert.assertEquals(person.getPrenom(), res.getLastName());
        Assert.assertEquals(person.getNom(), res.getFirstName());
    }

    @Test
    public void given_custom_fields_not_existing_for_class_compiler_should_report_warning() throws Exception {

        assertCompilationWarning(CustomFieldMapper.class, "public interface CustomFieldMapper {", "Custom @Field({\"classfieldfrom\",\"classfieldto\"}) mapping is never used !");
    }

    @Test
    public void given_custom_fields_not_existing_for_method_compiler_should_report_warning() throws Exception {

        assertCompilationWarning(CustomFieldMapper.class, "SimplePersonDto asPersonDtoReverseName(SimplePerson in);", "Custom @Field({\"methodfieldfrom\",\"methodfieldto\"}) mapping is never used !");
    }

}
