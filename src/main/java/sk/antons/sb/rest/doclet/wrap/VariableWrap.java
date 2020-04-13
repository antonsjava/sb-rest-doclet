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
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import sk.antons.sb.rest.doclet.cl.ClDb;

/**
 *
 * @author antons
 */
public class VariableWrap extends ElementWrap {
    VariableElement ve;
    public VariableWrap(Element element, WrapEnv env) {
        super(element, env);
        this.ve = (VariableElement)element;
    }
    public static VariableWrap instance(Element element, WrapEnv env) { return new VariableWrap(element, env); }
    
    public void closure() {
        if(ve == null) return;
        TypeMirrorWrap.instance(ve.asType(), env).closure();
    }

    public String javaType() {
        if(ve == null) return null;
        return TypeMirrorWrap.instance(ve.asType(), env).javaType();
    }
    
    public String javaTypeAsHtml(boolean root) {
        if(ve == null) return null;
        return TypeMirrorWrap.instance(ve.asType(), env).javaTypeAsHtml(root);
    }
    
}
