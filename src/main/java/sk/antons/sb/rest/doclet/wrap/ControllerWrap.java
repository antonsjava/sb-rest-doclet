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
package sk.antons.sb.rest.doclet.wrap;

import com.sun.source.util.DocTrees;
import java.util.Collections;
import java.util.List;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import sk.antons.jaul.Is;
import sk.antons.sb.rest.doclet.ElementHelper;
import sk.antons.sb.rest.doclet.cl.ClDb;
import sk.antons.sb.rest.doclet.finder.EndpointFinder;

/**
 *
 * @author antons
 */
public class ControllerWrap extends ElementWrap {
    TypeElement te;
    public ControllerWrap(Element element, WrapEnv env) {
        super(element, env);
        this.te = (TypeElement)element;
    }
    public static ControllerWrap instance(Element element, WrapEnv env) { return new ControllerWrap(element, env); }
    
    public String qualifiedName() { return te.getQualifiedName().toString(); }
    
    public String rootPath() {
        if(element == null) return "";
        AnnotationMirror requestMapping = ElementHelper.annotatoonByClass(element, "org.springframework.web.bind.annotation.RequestMapping");
        String root = ElementHelper.annotationParam(requestMapping, "path");
        if(Is.empty(root)) return "";
        return root;
    }
    
    private List<EndpointWrap> endpoints = null;
    public List<EndpointWrap> endpoints() {
        if(endpoints != null) return endpoints;
        if(element == null) return null;
        EndpointFinder endpointfinder = new EndpointFinder();
        List<Element> elems = endpointfinder.findIn(Collections.singletonList(element));
        endpoints = EndpointWrap.toEndpoints(elems, env);
        return endpoints;
    }
    
}
