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

import fr.xebia.extras.selma.beans.CityIn;
import fr.xebia.extras.selma.beans.CityOut;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by slemesle on 19/11/14.
 */
public class CustomImmutableMapper {


    public static final String IMMUTABLY_MAPPED = " immutably mapped";
    public static final int POPULATION_INC = 10000;

    public CityOut mapCity(CityIn cityIn) {
        CityOut cityOut = new CityOut();
        cityOut.setName(cityIn.getName() + IMMUTABLY_MAPPED);
        cityOut.setCapital(cityIn.isCapital());
        cityOut.setPopulation(cityIn.getPopulation() + POPULATION_INC);
        return cityOut;
    }

    public Collection<String> mapStringCollection(Collection<String> inCollection) {
        Collection<String> res = Collections.EMPTY_LIST;
        if (inCollection != null) {
            res = new ArrayList<String>(inCollection);
        }
        return res;
    }

}
