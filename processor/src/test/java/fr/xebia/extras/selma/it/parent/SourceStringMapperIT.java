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
package fr.xebia.extras.selma.it.parent;

import fr.xebia.extras.selma.Selma;
import fr.xebia.extras.selma.beans.DestinationString;
import fr.xebia.extras.selma.beans.SourceString;
import fr.xebia.extras.selma.it.utils.Compile;
import fr.xebia.extras.selma.it.utils.IntegrationTestBase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by slemesle on 15/04/2017.
 */
@Compile(withClasses = SourceStringMapper.class)
public class SourceStringMapperIT extends IntegrationTestBase {


    @Test
    public void given_String_in_parent_class_should_copy_to_destination_field(){
        SourceString sourceString = new SourceString();
        sourceString.setId("coucou");

        SourceStringMapper mapper = Selma.builder(SourceStringMapper.class).build();

        DestinationString res = mapper.asDestination(sourceString);

        Assert.assertTrue(res.getId() == sourceString.getId());
    }

}
