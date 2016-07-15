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
package fr.xebia.extras.selma.beans;

import fr.xebia.extras.selma.Selma;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by slemesle on 02/12/14.
 */
public class CustomMapperTest {

    @Test
    public void given_custom_mapper_selma_should_use_it_for_IOTypes() {
        DateBOMapper mapper = Selma.mapper(DateBOMapper.class);
        TypeIn in = new TypeIn("1245432");
        TypeOut out = mapper.asTypeOut(in);

        Assert.assertEquals(out.getDate(), new DateBO(in.getDate()));

    }

}
