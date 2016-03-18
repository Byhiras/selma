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
package fr.xebia.extras.selma.it;

import fr.xebia.extras.selma.Selma;
import fr.xebia.extras.selma.beans.*;
import fr.xebia.extras.selma.beans.BuilderBeanOut2.FixedIntValBuilder;
import fr.xebia.extras.selma.it.mappers.*;
import fr.xebia.extras.selma.it.utils.Compile;
import fr.xebia.extras.selma.it.utils.IntegrationTestBase;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 *
 */
@Compile(withClasses = {
        BuilderMapper.class,
        BuilderClassPatternMapper.class,
        BuilderExtendedMapper.class,
        BuilderFactoryMapper.class,
        BuilderFieldsMapper.class,
        BuilderFixedBuilderClassMapper.class,
        BuilderFqClassMapper.class,
        BuilderMethodMapper.class,
        BuilderPackageAndClassMapper.class
})
public class BuilderMapperIT extends IntegrationTestBase {
    private BuilderBeanIn builderBeanIn;

    @Before
    public void before() {
        Random random = new Random();

        builderBeanIn = new BuilderBeanIn();
        builderBeanIn.setIntVal(random.nextInt(100));
        builderBeanIn.setStr("a string " + random.nextInt(100));
    }

    @Test
    public void builderMapper_should_map_properties() throws Exception {
        BuilderMapper mapper = Selma.getMapper(BuilderMapper.class);

        BuilderBeanOut res = mapper.asBuilderOut(builderBeanIn);

        assertThat(res, notNullValue());
        assertThat(res.getIntVal(), equalTo(builderBeanIn.getIntVal()));
        assertThat(res.getStr(), equalTo(builderBeanIn.getStr()));
    }

    @Test
    public void builderExtendedMapper_maps_should_use_builder_class() throws Exception {
        BuilderExtendedMapper mapper = Selma.getMapper(BuilderExtendedMapper.class);

        BuilderBeanOut res = mapper.asBuilderOut(builderBeanIn);

        assertThat(res, notNullValue());
        assertThat(res, instanceOf(ExtendedBuilderBeanOut.class));
        assertThat(res.getIntVal(), equalTo(builderBeanIn.getIntVal()));
        // should be using the builder with fixed string
        assertThat(res.getStr(), equalTo(builderBeanIn.getStr()));
    }

    @Test
    public void builderFixedBuilderClassMapper_should_use_builder_class() throws Exception {
        BuilderFixedBuilderClassMapper mapper = Selma.getMapper(BuilderFixedBuilderClassMapper.class);

        BuilderBeanOut2 res = mapper.asBuilderOut(builderBeanIn);

        assertThat(res, notNullValue());
        assertThat(res.getIntVal(), equalTo(builderBeanIn.getIntVal()));
        assertThat(res.getStr(), equalTo(builderBeanIn.getStr() + " bean2"));
    }

    @Test
    public void builderFqClassMapper_should_replace_fqClass() throws Exception {
        BuilderFqClassMapper mapper = Selma.getMapper(BuilderFqClassMapper.class);

        BuilderBeanOut2 res = mapper.asBuilderOut(builderBeanIn);

        assertThat(res, notNullValue());
        // should be using the builder with fixed int val
        assertThat(res.getIntVal(), equalTo(FixedIntValBuilder.FIXED_VALUE));
        assertThat(res.getStr(), equalTo(builderBeanIn.getStr()));
    }

    @Test
    public void builderPackageAndClassMapper_should_replace_package_and_class() throws Exception {
        BuilderPackageAndClassMapper mapper = Selma.getMapper(BuilderPackageAndClassMapper.class);

        BuilderBeanOut2 res = mapper.asBuilderOut(builderBeanIn);

        assertThat(res, notNullValue());
        assertThat(res.getIntVal(), equalTo(Builders.EXTERNAL_INT_VALUE));
        assertThat(res.getStr(), equalTo(Builders.EXTERNAL_STR));
    }

    @Test
    public void builderFieldsMapper_maps_should_map_fields_to_builder() throws Exception {
        BuilderFieldsMapper mapper = Selma.getMapper(BuilderFieldsMapper.class);

        BuilderBeanOut2 res = mapper.asBuilderOut(builderBeanIn);

        assertThat(res, notNullValue());
        assertThat(res.getIntVal(), equalTo(builderBeanIn.getIntVal()));
        assertThat(res.getStr(), equalTo(builderBeanIn.getStr() + " other field"));
    }

    @Test
    public void builderMethodMapper_maps_should_use_custom_build_method() throws Exception {
        BuilderMethodMapper mapper = Selma.getMapper(BuilderMethodMapper.class);

        BuilderBeanOut2 res = mapper.asBuilderOut(builderBeanIn);

        assertThat(res, notNullValue());
        assertThat(res.getIntVal(), equalTo(builderBeanIn.getIntVal()));
        assertThat(res.getStr(), equalTo(builderBeanIn.getStr() + " made"));
    }

    @Test
    public void builderClassPatternMapper_earlier_match_should_override_later_match()throws Exception{
        BuilderClassPatternMapper mapper = Selma.getMapper(BuilderClassPatternMapper.class);

        BuilderBeanOut2 res = mapper.asBuilderOut2(builderBeanIn);

        assertThat(res, notNullValue());
        assertThat(res.getIntVal(), equalTo(builderBeanIn.getIntVal()));
        assertThat(res.getStr(), equalTo(builderBeanIn.getStr() + " bean2"));

        BuilderBeanOut3 res2 = mapper.asBuilderOut3(builderBeanIn);

        assertThat(res2, notNullValue());
        assertThat(res2.getIntVal(), equalTo(builderBeanIn.getIntVal()));
        assertThat(res2.getStr(), equalTo(builderBeanIn.getStr() + " bean3"));
    }

    @Test
    public void builderClassPatternMapper_classes_match_should_override_classesPattern_match()throws Exception{
        BuilderClassPatternMapper mapper = Selma.getMapper(BuilderClassPatternMapper.class);

        Book bookIn = new Book();
        bookIn.setName("Candide");
        bookIn.setAuthor("Voltaire");

        BookDTO res = mapper.asBookDTO(bookIn);

        assertThat(res, notNullValue());
        assertThat(res.getName(), equalTo(bookIn.getName()));
        assertThat(res.getAuthor(), equalTo(bookIn.getAuthor()));

        CityIn cityIn = new CityIn();
        cityIn.setName("London");
        cityIn.setCapital(true);
        cityIn.setPopulation(10000000);

        CityOut res2 = mapper.asCityOut(cityIn);

        assertThat(res2, notNullValue());
        assertThat(res2.getName(), equalTo(cityIn.getName()));
        assertThat(res2.isCapital(), equalTo(cityIn.isCapital()));
        assertThat(res2.getPopulation(), equalTo(cityIn.getPopulation()));
    }

    @Test
    public void builderFactoryMapper_should_create_builder_with_factory() throws Exception {
        BuilderFactoryMapper mapper = Selma.getMapper(BuilderFactoryMapper.class);

        BuilderBeanIn builderBeanIn = new BuilderBeanIn();
        builderBeanIn.setIntVal(5);
        builderBeanIn.setStr("a string");

        BuilderBeanOut res = mapper.asBuilderOut(builderBeanIn);

        assertThat(res, notNullValue());
        assertThat(res.getIntVal(), equalTo(builderBeanIn.getIntVal() + 1));
        assertThat(res.getStr(), equalTo(builderBeanIn.getStr() + "factory"));
    }
}
