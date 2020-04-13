/*
 * Copyright 2020 Anton Straka
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
 */
package sk.antons.sb.rest.doclet.finder;

import java.util.List;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

/**
 *
 * @author antons
 */
public class EndpointFinder extends ElementFinder {

    @Override
    protected boolean filter(Element element) {
        if(element == null) return false;
        if(element.getKind() != ElementKind.METHOD) return false;
        List<? extends AnnotationMirror> annotations = element.getAnnotationMirrors();
        if(annotations == null) return false;
        if(annotations.isEmpty()) return false;
        for(AnnotationMirror annotation : annotations) {
            String fqn = annotation.getAnnotationType().toString();
            if(fqn.startsWith("org.springframework.web.bind.annotation.") 
                 && fqn.endsWith("Mapping")
                ) return true;
        }
        return false;
    }

}
