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
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import sk.antons.jaul.Is;
import sk.antons.sb.rest.doclet.cl.Cl;

/**
 *
 * @author antons
 */
public class ModelWrap extends ElementWrap {
    TypeElement te;
    public ModelWrap(Element element, WrapEnv env) {
        super(element, env);
        this.te = (TypeElement)element;
    }
    public static ModelWrap instance(Element element, WrapEnv env) { return new ModelWrap(element, env); }
    
    public String qualifiedName() { return te.getQualifiedName().toString(); }
    
    
    public void closure() {
        List<? extends Element> fields = te.getEnclosedElements();
        if(Is.empty(fields)) return;
        for(Element field : fields) {
            if(field.getKind() != ElementKind.FIELD) continue;
            VariableWrap v = VariableWrap.instance(field, env);
            v.closure();
        }
    }
    
    private List<VariableWrap> fields = null;
    public List<VariableWrap> fields() {
        if(fields != null) return fields;
        fields = new ArrayList<>();
        if(te == null) return fields;
        List<? extends Element> list = te.getEnclosedElements();
        if(!Is.empty(list)) {
            for(Element field : list) {
                if((field.getKind() != ElementKind.FIELD)
                    && (field.getKind() != ElementKind.ENUM_CONSTANT)
                    ) continue;
                if(badModifiers(field.getModifiers())) continue;
                VariableWrap v = VariableWrap.instance(field, env);
                fields.add(v);
            }
        }
        return fields;
    }

    private boolean badModifiers(Set<Modifier> modifiers) {
        if(te == null) return true;
        if(Is.empty(modifiers)) return false;
        if(te.getKind() == ElementKind.ENUM) return false;
        for(Modifier modifier : modifiers) {
            if(modifier == Modifier.NATIVE) return true;
            if(modifier == Modifier.STATIC) return true;
            if(modifier == Modifier.TRANSIENT) return true;
        }
        return false;
    }

    public String ancestors() {
        Cl cl = env.classDb.get(qualifiedName());
        if(cl == null) return "";
        List<Cl> list = cl.parents();
        if(Is.empty(list)) return "";
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for(Cl cls : list) {
            if(first) first = false;
            else sb.append(", ");
            TypeMirrorWrap mw = TypeMirrorWrap.instance(cls.element().asType(), env);
            if(mw != null) sb.append(mw.javaTypeAsHtml(false));
        }
        return sb.toString();
    }
    public String descendants() {
        Cl cl = env.classDb.get(qualifiedName());
        if(cl == null) return "";
        List<Cl> list = cl.children();
        if(Is.empty(list)) return "";
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for(Cl cls : list) {
            if(first) first = false;
            else sb.append(", ");
            TypeMirrorWrap mw = TypeMirrorWrap.instance(cls.element().asType(), env);
            if(mw != null) sb.append(mw.javaTypeAsHtml(false));
        }
        return sb.toString();
    }
}
