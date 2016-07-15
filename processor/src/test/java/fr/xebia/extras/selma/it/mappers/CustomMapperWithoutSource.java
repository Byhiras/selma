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

import fr.xebia.extras.selma.beans.CityIn;
import fr.xebia.extras.selma.beans.CityOut;
import fr.xebia.extras.selma.beans.CityOutWithDataSource;

/**
 *
 *
 */
public class CustomMapperWithoutSource {

    private String message = " Mapped by CustomMapper";

    public CustomMapperWithoutSource() {
    }

    public CustomMapperWithoutSource(String message) {
        this.message = message;
    }

    public CityOut mapCity(CityIn cityIn) {
        CityOut cityOut = new CityOut();
        cityOut.setName(cityIn.getName() + message);
        cityOut.setCapital(cityIn.isCapital());
        cityOut.setPopulation(cityIn.getPopulation() + 10000);
        return cityOut;
    }

    public CityOutWithDataSource asCityOutWithDataSource(CityIn cityIn) {
        CityOutWithDataSource cityOut = new CityOutWithDataSource(null);
        cityOut.setName(cityIn.getName() + message);
        cityOut.setCapital(cityIn.isCapital());
        cityOut.setPopulation(cityIn.getPopulation() + 10000);
        return cityOut;
    }


    public CityOutWithDataSource asCityOut(CityIn cityIn, CityOutWithDataSource out) {
        CityOutWithDataSource cityOut = new CityOutWithDataSource(null);
        cityOut.setName(cityIn.getName() + message);
        cityOut.setCapital(cityIn.isCapital());
        cityOut.setPopulation(cityIn.getPopulation() + 10000);
        return cityOut;
    }

}
