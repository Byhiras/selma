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
package fr.xebia.extras.selma.it.custom.mapper;

import fr.xebia.extras.selma.Selma;
import fr.xebia.extras.selma.beans.AddressIn;
import fr.xebia.extras.selma.beans.CityIn;
import fr.xebia.extras.selma.beans.PersonIn;
import fr.xebia.extras.selma.beans.PersonOut;
import fr.xebia.extras.selma.it.utils.Compile;
import fr.xebia.extras.selma.it.utils.IntegrationTestBase;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by slemesle on 19/11/14.
 */
@Compile(withClasses = {CustomImmutableMapper.class, CustomMapperDefaultsToImmutable.class})
public class CustomMapperDefaultsToImmutableIT extends IntegrationTestBase {

    @Test
    public void should_map_bean_with_custom_mapper() throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        CustomMapperDefaultsToImmutable mapper = Selma.getMapper(CustomMapperDefaultsToImmutable.class);

        PersonIn personIn = new PersonIn();
        personIn.setAddress(new AddressIn());
        personIn.getAddress().setCity(new CityIn());
        personIn.getAddress().getCity().setCapital(true);
        personIn.getAddress().getCity().setName("Paris");
        personIn.getAddress().getCity().setPopulation(3 * 1000 * 1000);

        personIn.getAddress().setPrincipal(true);
        personIn.getAddress().setNumber(55);
        personIn.getAddress().setStreet("rue de la truanderie");

        PersonOut res = mapper.mapWithCustom(personIn);

        Assert.assertNotNull(res);

        Assert.assertEquals(personIn.getAddress().getStreet(), res.getAddress().getStreet());
        Assert.assertEquals(personIn.getAddress().getNumber(), res.getAddress().getNumber());
        Assert.assertEquals(personIn.getAddress().getExtras(), res.getAddress().getExtras());

        Assert.assertEquals(personIn.getAddress().getCity().getName() + CustomImmutableMapper.IMMUTABLY_MAPPED, res.getAddress().getCity().getName());
        Assert.assertEquals(personIn.getAddress().getCity().getPopulation() + CustomImmutableMapper.POPULATION_INC, res.getAddress().getCity().getPopulation());
        Assert.assertEquals(personIn.getAddress().getCity().isCapital(), res.getAddress().getCity().isCapital());
    }

    @Test
    public void should_map_bean_with_custom_mapper_when_update_graph() throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        CustomMapperDefaultsToImmutable mapper = Selma.getMapper(CustomMapperDefaultsToImmutable.class);

        PersonIn personIn = new PersonIn();
        personIn.setAddress(new AddressIn());
        personIn.getAddress().setCity(new CityIn());
        personIn.getAddress().getCity().setCapital(true);
        personIn.getAddress().getCity().setName("Paris");
        personIn.getAddress().getCity().setPopulation(3 * 1000 * 1000);

        personIn.getAddress().setPrincipal(true);
        personIn.getAddress().setNumber(55);
        personIn.getAddress().setStreet("rue de la truanderie");

        PersonOut out = new PersonOut();
        PersonOut res = mapper.mapWithCustom(personIn, out);

        Assert.assertNotNull(res);
        Assert.assertTrue(out == res);
        Assert.assertEquals(personIn.getAddress().getStreet(), res.getAddress().getStreet());
        Assert.assertEquals(personIn.getAddress().getNumber(), res.getAddress().getNumber());
        Assert.assertEquals(personIn.getAddress().getExtras(), res.getAddress().getExtras());

        Assert.assertEquals(personIn.getAddress().getCity().getName() + CustomImmutableMapper.IMMUTABLY_MAPPED, res.getAddress().getCity().getName());
        Assert.assertEquals(personIn.getAddress().getCity().getPopulation() + CustomImmutableMapper.POPULATION_INC, res.getAddress().getCity().getPopulation());
        Assert.assertEquals(personIn.getAddress().getCity().isCapital(), res.getAddress().getCity().isCapital());
    }

    @Test
    public void should_map_bean_with_custom_mapper_for_null_values() throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        CustomMapperDefaultsToImmutable mapper = Selma.getMapper(CustomMapperDefaultsToImmutable.class);

        PersonIn personIn = new PersonIn();
        personIn.setAddress(new AddressIn());
        personIn.getAddress().setCity(new CityIn());
        personIn.getAddress().getCity().setCapital(true);
        personIn.getAddress().getCity().setName("Paris");
        personIn.getAddress().getCity().setPopulation(3 * 1000 * 1000);

        personIn.getAddress().setPrincipal(true);
        personIn.getAddress().setNumber(55);
        personIn.getAddress().setStreet("rue de la truanderie");

        PersonOut res = mapper.mapWithCustom(personIn);

        Assert.assertNotNull(res);
        Assert.assertNotNull(res.getTags());
        Assert.assertEquals(0, res.getTags().size());
        Assert.assertEquals(personIn.getAddress().getStreet(), res.getAddress().getStreet());
        Assert.assertEquals(personIn.getAddress().getNumber(), res.getAddress().getNumber());
        Assert.assertEquals(personIn.getAddress().getExtras(), res.getAddress().getExtras());

        Assert.assertEquals(personIn.getAddress().getCity().getName() + CustomImmutableMapper.IMMUTABLY_MAPPED, res.getAddress().getCity().getName());
        Assert.assertEquals(personIn.getAddress().getCity().getPopulation() + CustomImmutableMapper.POPULATION_INC, res.getAddress().getCity().getPopulation());
        Assert.assertEquals(personIn.getAddress().getCity().isCapital(), res.getAddress().getCity().isCapital());
    }

    @Test
    public void should_map_bean_with_custom_mapper_for_null_values_when_update_graph() throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        CustomMapperDefaultsToImmutable mapper = Selma.getMapper(CustomMapperDefaultsToImmutable.class);

        PersonIn personIn = new PersonIn();
        personIn.setAddress(new AddressIn());
        personIn.getAddress().setCity(new CityIn());
        personIn.getAddress().getCity().setCapital(true);
        personIn.getAddress().getCity().setName("Paris");
        personIn.getAddress().getCity().setPopulation(3 * 1000 * 1000);

        personIn.getAddress().setPrincipal(true);
        personIn.getAddress().setNumber(55);
        personIn.getAddress().setStreet("rue de la truanderie");

        PersonOut out = new PersonOut();
        PersonOut res = mapper.mapWithCustom(personIn, out);

        Assert.assertNotNull(res);
        Assert.assertTrue(out == res);
        Assert.assertNotNull(res.getTags());
        Assert.assertEquals(0, res.getTags().size());
        Assert.assertEquals(personIn.getAddress().getStreet(), res.getAddress().getStreet());
        Assert.assertEquals(personIn.getAddress().getNumber(), res.getAddress().getNumber());
        Assert.assertEquals(personIn.getAddress().getExtras(), res.getAddress().getExtras());

        Assert.assertEquals(personIn.getAddress().getCity().getName() + CustomImmutableMapper.IMMUTABLY_MAPPED, res.getAddress().getCity().getName());
        Assert.assertEquals(personIn.getAddress().getCity().getPopulation() + CustomImmutableMapper.POPULATION_INC, res.getAddress().getCity().getPopulation());
        Assert.assertEquals(personIn.getAddress().getCity().isCapital(), res.getAddress().getCity().isCapital());
    }
}
