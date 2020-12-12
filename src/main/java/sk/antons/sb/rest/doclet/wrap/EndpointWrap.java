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

import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import sk.antons.jaul.Get;
import sk.antons.jaul.Is;
import sk.antons.sb.rest.doclet.ElementHelper;

/**
 *
 * @author antons
 */
public class EndpointWrap extends ElementWrap implements Comparable<EndpointWrap> {
    ExecutableElement ee;;
    public EndpointWrap(Element element, WrapEnv env) {
        super(element, env);
        this.ee = (ExecutableElement)element;
    }
    public static EndpointWrap instance(Element element, WrapEnv env) { return new EndpointWrap(element, env); }
   
    public String controllerName() {
        return ((TypeElement)element.getEnclosingElement()).getQualifiedName().toString();
    }
    
    private String returnType = null;
    public String returnType() {
        if(returnType != null) return returnType;
        if(ee == null) return "";
        returnType = TypeMirrorWrap.instance(ee.getReturnType(), env).javaType();
        return returnType;
    }
    
    public String returnTypeAsHtml(boolean root) {
        if(ee == null) return "";
        return TypeMirrorWrap.instance(ee.getReturnType(), env).javaTypeAsHtml(root);
    }
    public String throwsAsHtml(boolean root) {
        if(ee == null) return "";
        StringBuilder sb = new StringBuilder();
        List<? extends TypeMirror> thrs = ee.getThrownTypes();
        if(!Is.empty(thrs)) {
            for(int i = 0; i < thrs.size(); i++) {
                if(i > 0) sb.append(", ");
                sb.append(TypeMirrorWrap.instance(thrs.get(i), env).javaTypeAsHtml(root));
            }
        }
        return sb.toString();
    }

    
    private String root = null;
    public String rootPath() {
        if(root != null) return root;
        if(element == null) return "";
        AnnotationMirror mapping = ElementHelper.annotatoonByClass(element, "org.springframework.web.bind.annotation.GetMapping");
        if(mapping == null) mapping = ElementHelper.annotatoonByClass(element, "org.springframework.web.bind.annotation.PostMapping");
        if(mapping == null) mapping = ElementHelper.annotatoonByClass(element, "org.springframework.web.bind.annotation.PutMapping");
        if(mapping == null) mapping = ElementHelper.annotatoonByClass(element, "org.springframework.web.bind.annotation.DeleteMapping");
        if(mapping == null) mapping = ElementHelper.annotatoonByClass(element, "org.springframework.web.bind.annotation.PatchMapping");
        if(mapping == null) mapping = ElementHelper.annotatoonByClass(element, "org.springframework.web.bind.annotation.RequestMapping");
        root = ElementHelper.annotationParam(mapping, "path");
        if(Is.empty(root)) root = ElementHelper.annotationParam(mapping, "value");
        if(Is.empty(root)) root = "";
        return root;
    }

    public String id() {
        String value = method() + fullRootPath();
        return "" + value.hashCode();
    }
    
    String fullRootPath = null;
    public String fullRootPath() {
        if(fullRootPath != null) return fullRootPath;
        ControllerWrap cw = ControllerWrap.instance(element.getEnclosingElement(), env);
        fullRootPath = cw.rootPath() + rootPath();
        return fullRootPath;
    }
    
    private String method = null;
    public void method(String value) { this.method = value; }
    public String method() {
        if(method != null) return method;
        if(element == null) return "";
        AnnotationMirror mapping = ElementHelper.annotatoonByClass(element, "org.springframework.web.bind.annotation.GetMapping");
        if(mapping != null) return method = "GET";
        mapping = ElementHelper.annotatoonByClass(element, "org.springframework.web.bind.annotation.PostMapping");
        if(mapping != null) return method = "POST";
        mapping = ElementHelper.annotatoonByClass(element, "org.springframework.web.bind.annotation.PutMapping");
        if(mapping != null) return method = "PUT";
        mapping = ElementHelper.annotatoonByClass(element, "org.springframework.web.bind.annotation.DeleteMapping");
        if(mapping != null) return method = "DELETE";
        mapping = ElementHelper.annotatoonByClass(element, "org.springframework.web.bind.annotation.PatchMapping");
        if(mapping != null) return method = "PATCH";
        return method = "";
    }
    
    public List<String> methods() {
        if(element == null) return null;
        AnnotationMirror mapping = ElementHelper.annotatoonByClass(element, "org.springframework.web.bind.annotation.RequestMapping");
        if(mapping == null) return null;
        return ElementHelper.annotationParamValues(mapping, "method");
    
    }

    @Override
    public int compareTo(EndpointWrap o) {
        if(o == null) return 1;
        int cmp = fullRootPath().compareTo(o.fullRootPath());
        if(cmp != 0) return cmp;
        return method().compareTo(o.method());
    }
    
    public static List<EndpointWrap> toEndpoints(List<Element> elements, WrapEnv env) {
        List<EndpointWrap> endpoints = new ArrayList<>();
        if(Is.empty(elements)) return endpoints;
        for(Element element : elements) {
            EndpointWrap ew = EndpointWrap.instance(element, env);
            List<String> methods = ew.methods();
            if(Get.size(methods) > 1) {
                for(String method : methods) {
                    method = method.replace("org.springframework.web.bind.annotation.RequestMethod.", "");
                    EndpointWrap ew2 = EndpointWrap.instance(element, env);
                    ew2.method(method);
                    endpoints.add(ew2);
                }
            } else {
                endpoints.add(ew);
            }
        }
        return endpoints;
    }
    
    public void closure() {
        if(ee == null) return;
        //System.out.println(" closure " + element);
        //System.out.println("   before " + env.used);
        TypeMirrorWrap.instance(ee.getReturnType(), env).closure();
        List<? extends TypeMirror> thrs = ee.getThrownTypes();
        if(!Is.empty(thrs)) {
            for(TypeMirror thr : thrs) {
                //System.out.println(" add " + thr);
                TypeMirrorWrap.instance(thr, env).closure();
            }
        }
        List<? extends VariableElement> vars = ee.getParameters();
        if(!Is.empty(vars)) {
            for(VariableElement varko : vars) {
                //System.out.println(" add " + varko);
                //System.out.println(" add " + varko.asType());
                TypeMirrorWrap.instance(varko.asType(), env).closure();
            }
        }
        //System.out.println("   after " + env.used);
    }
    
    public List<VariableWrap> params() {
        List<VariableWrap> list = new ArrayList<>();
        if(ee == null) return list;
        List<? extends VariableElement> vars = ee.getParameters();
        if(!Is.empty(vars)) {
            for(VariableElement varko : vars) {
                list.add(VariableWrap.instance(varko, env));
            }
        }
        return list;
    }
}
