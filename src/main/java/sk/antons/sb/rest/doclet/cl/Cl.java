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
import java.util.List;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import sk.antons.jaul.pojo.Messer;
import sk.antons.sb.rest.doclet.ClassResolver;
import sk.antons.sb.rest.doclet.wrap.TypeMirrorWrap;
import sk.antons.sb.rest.doclet.wrap.WrapEnv;

/**
 *
 * @author antons
 */
public class Cl {
    String classname;
    TypeElement element;
    List<Cl> children = new ArrayList<>();;
    List<Cl> parents = new ArrayList<>();;
   
    public Cl(String classes, TypeElement element) { 
        this.classname = classes; 
        this.element = element; 
    }
    public static Cl instance(String classname, TypeElement element) { return new Cl(classname, element); }

    public TypeElement element() { return element; }
    public List<Cl> parents() { return parents; }
    public List<Cl> children() { return children; }

    public void addChildren(Cl cl) { 
        if(!children.contains(cl)) children.add(cl); 
        if(!cl.parents.contains(this)) cl.parents.add(this); 
    }
    public List<Cl> descendands() { 
        List<Cl> list = new ArrayList<>();
        for(Cl cl : children) cl.descendands(list);
        return list;
    }

    protected void descendands(List<Cl> list) { 
        list.add(this); 
        for(Cl cl : children) cl.descendands(list);
    }

    public boolean isAbstract() {
        if(element.getKind() == ElementKind.INTERFACE) return true;
        if(element.getModifiers().contains(Modifier.ABSTRACT)) return true;
        return false;
    }

    public Cl nonAbstractDescendand() {
        if(!isAbstract()) return this;
        for(Cl cl : children) if(!cl.isAbstract()) return cl;
        return null;
    }

    public void addMesserMapping(Messer messer, ClassResolver clresolver) {
        if(!isAbstract()) return;
        Cl cl = nonAbstractDescendand();
        if(cl == null) return;
        Class cls1 = clresolver.resolve(classname);
        if(cls1 == null) return;
        Class cls2 = clresolver.resolve(cl.classname);
        if(cls2 == null) return;
        messer.map(cls1, cls2);
    }
    
    public void closureparent(WrapEnv env) {
        //System.out.println(" clcl " + element);
        TypeMirrorWrap.instance(element.asType(), env).closure();
        for(Cl parent : parents) parent.closureparent(env);
    }
    public void closurechild(WrapEnv env) {
        //System.out.println(" clcl " + element);
        TypeMirrorWrap.instance(element.asType(), env).closure();
        for(Cl parent : children) parent.closurechild(env);
    }

}
