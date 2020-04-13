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

import java.util.List;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import sk.antons.jaul.Is;
import sk.antons.jaul.xml.Xml;
import sk.antons.sb.rest.doclet.cl.Cl;

/**
 *
 * @author antons
 */
public class TypeMirrorWrap {
    TypeMirror type;
    WrapEnv env;
    public TypeMirrorWrap(TypeMirror type, WrapEnv env) {
        this.type = type;
        this.env = env;
    }
    public static TypeMirrorWrap instance(TypeMirror type, WrapEnv env) { return new TypeMirrorWrap(type, env); }
   
    String baseType = null;
    public String baseType() {
        if(type == null) return "";
        if(baseType != null) return baseType;
        baseType = type.toString();
        int pos = baseType.indexOf("<");
        if(pos > -1) baseType = baseType.substring(0, pos);
        return baseType;
    }
    public String shortBaseType() {
        if(type == null) return "";
        String tp = baseType();
        if(Is.empty(tp)) return tp;
        int pos = tp.lastIndexOf('.');
        if(pos > -1) return tp.substring(pos+1);
        return tp;
    }
    
    public void closure() {
        if(type == null) return;
        String ctype = baseType();
        if(env.used.contains(ctype)) return;
        Cl cl = env.classDb.get(ctype);
        if(cl != null) {
            ModelWrap mw = ModelWrap.instance(cl.element(), env);
            env.used.add(ctype);
            mw.closure();
        }
        //System.out.println(" -- " + type);
        //System.out.println(" --   " + ctype);
        if(env.used.contains(ctype)) return;
        if(type instanceof DeclaredType) {
            DeclaredType dt = (DeclaredType)type;
            List<? extends TypeMirror> arguments = dt.getTypeArguments();
            if(!Is.empty(arguments)) {
                for(int i = 0; i < arguments.size(); i++) {
                    TypeMirrorWrap.instance(arguments.get(i), env).closure();
                }
            }
        }
    }
    
    public String javaType() {
        if(type == null) return "";
//        if(type instanceof DeclaredType) {
//            DeclaredType dt = (DeclaredType)type;
//            List<? extends TypeMirror> arguments = dt.getTypeArguments();
//            if(Is.empty(arguments)) {
//                return type.toString();
//            } else {
//                StringBuilder sb = new StringBuilder();
//                sb.append(type);
//                sb.append('<');
//                for(int i = 0; i < arguments.size(); i++) {
//                    if(i > 0) sb.append(", ");
//                    sb.append(TypeMirrorWrap.instance(arguments.get(i)).javaType());
//                }
//                sb.append('>');
//                return sb.toString();
//            }
//        } else {
//            return type.toString();
//        }
          return type.toString();
    }
    
    private String baseJavaTypeAsHtml(boolean root) {
        String jtype = baseType();
        String jstype = shortBaseType();
        if(env.used.contains(jtype)) {
            if(root) return "<a href=\"./model/"+jtype+".html\">"+jstype+"</a>";
            else return "<a href=\"../model/"+jtype+".html\">"+jstype+"</a>";
        } else {
            return Xml.escape(jstype);
        }
    }

    public String javaTypeAsHtml(boolean root) {
        if(type == null) return "";
        if(type instanceof DeclaredType) {
            DeclaredType dt = (DeclaredType)type;
            List<? extends TypeMirror> arguments = dt.getTypeArguments();
            if(Is.empty(arguments)) {
                return baseJavaTypeAsHtml(root);
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append(baseJavaTypeAsHtml(root));
                sb.append("&lt;");
                for(int i = 0; i < arguments.size(); i++) {
                    if(i > 0) sb.append(", ");
                    sb.append(TypeMirrorWrap.instance(arguments.get(i), env).javaTypeAsHtml(root));
                }
                sb.append("&gt.");
                return sb.toString();
            }
        } else {
            return baseJavaTypeAsHtml(root);
        }
        
    }
}
