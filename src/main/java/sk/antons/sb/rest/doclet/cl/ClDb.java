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
package sk.antons.sb.rest.doclet.cl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import sk.antons.jaul.pojo.Messer;
import sk.antons.sb.rest.doclet.ClassResolver;
import sk.antons.sb.rest.doclet.wrap.WrapEnv;

/**
 *
 * @author antons
 */
public class ClDb {

    private Map<String, Cl> classes = new HashMap<String, Cl>();

    public void add(Element e) {
        if(e == null) return;
        if(e instanceof TypeElement) {
            TypeElement te = (TypeElement)e;
            String name = te.getQualifiedName().toString();
            classes.put(name, Cl.instance(name, te));
        }
    }

    public Cl get(String name) {
        return classes.get(name);
    }

    public void buildTree() {
        for(Cl value : classes.values()) {
            try {
                String cl = value.element.getQualifiedName().toString();
                String clp = "" + value.element.getSuperclass();
                Cl value2 = classes.get(clp);
                if(value2 != null) {
                    value2.addChildren(value);
                    //System.out.println(" --- biod tree - " + cl + " - " + clp );
                }
            } catch(Throwable e) {
            }
        }
    }

    public void print() {
        List<String> keys = new ArrayList<>();
        keys.addAll(classes.keySet());
        Collections.sort(keys);
        for(String key : keys) {
            System.out.println(" clmap: " + key + " - " + classes.get(key).element.getKind());
        }
            
    }
    
    public void closure(WrapEnv env) {
        List<String> old = new ArrayList<>(env.used());
        for(String string : old) {
            Cl cl = classes.get(string);
            if(cl != null) {
                cl.closureparent(env);
                cl.closurechild(env);
            }
        }
    }

    public void addMesserMapping(Messer messer, ClassResolver clresolver) {
        for(Cl value : classes.values()) {
            value.addMesserMapping(messer, clresolver);
        }
    }
    
}
