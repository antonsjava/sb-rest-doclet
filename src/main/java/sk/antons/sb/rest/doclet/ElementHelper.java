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
package sk.antons.sb.rest.doclet;

import java.util.List;
import java.util.Map;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

/**
 *
 * @author antons
 */
public class ElementHelper {

    public static AnnotationMirror annotatoonByClass(Element element, String classname) {
        if(element == null) return null;
        List<? extends AnnotationMirror> annotations = element.getAnnotationMirrors();
        if(annotations == null) return null;
        if(annotations.isEmpty()) return null;
        for(AnnotationMirror annotation : annotations) {
            if(classname.equals(annotation.getAnnotationType().toString())) return annotation;
        }
        return null;
    }

    public static String annotationParam(AnnotationMirror annotation, String name) {
        String rv = null;
        if((annotation != null) && (annotation.getElementValues() != null)) {
            for(Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotation.getElementValues().entrySet()) {
                //System.out.println(" -- " + entry.getKey().getSimpleName().toString());
                if(name.equals(entry.getKey().getSimpleName().toString())) {
                    rv = entry.getValue().getValue().toString();
                    if(rv.startsWith("\"")) rv = rv.substring(1, rv.length()-1);
                }
            }
        }
        return rv;
    
    }
}
