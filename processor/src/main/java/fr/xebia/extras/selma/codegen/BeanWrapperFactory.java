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
package fr.xebia.extras.selma.codegen;

import javax.lang.model.element.TypeElement;
import java.util.HashMap;

/**
 * Created by slemesle on 27/02/15.
 */
public class BeanWrapperFactory {


    private final HashMap<TypeElement, BeanWrapper> beanWrapperMap = new HashMap<TypeElement, BeanWrapper>();

    public final BeanWrapper getBeanWrapperOrNew(MapperGeneratorContext context, TypeElement typeElement) {

        BeanWrapper beanWrapper = beanWrapperMap.get(typeElement);
        if (beanWrapper == null) {
            beanWrapper = new BeanWrapper(context, typeElement);
            beanWrapperMap.put(typeElement, beanWrapper);
        }
        return beanWrapper;
    }

}
