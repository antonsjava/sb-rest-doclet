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

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.util.TreePath;
import java.util.List;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import sk.antons.jaul.Is;
import sk.antons.jaul.binary.Unicode;
import sk.antons.jaul.xml.Xml;

/**
 *
 * @author antons
 */
public abstract class ElementWrap {
    Element element;
    WrapEnv env;
    public ElementWrap(Element element, WrapEnv env) {
        this.element = element;
        this.env = env;
    }
    
    public String simpleName() { return element.getSimpleName().toString(); }
    
    DocCommentTree dcTree = null;
    protected DocCommentTree dcTree(){
        if(dcTree != null) return dcTree;
        if(element == null) return null;
        try {
            //TreePath dct = env.treeUtils.getPath(element);
            dcTree = env.treeUtils.getDocCommentTree(element);
        } catch(Throwable e) {
        }
        return dcTree;

    }
    
    public String javadoc() {
        if(element == null) return "";
        if(dcTree() == null) return "";

        StringBuilder html = new StringBuilder();
        html.append("<div class=\"javadoc\">\n");
        String value = toString(dcTree.getFirstSentence());
        String value2 = toString(dcTree.getBody());
        if((value2 != null) || (value != null)) {
            html.append("<div>");
            if(value != null) {
                html.append("<span class=\"important\">");
                html.append(value);
                html.append("</span>");
            }
            if(value2 != null) {
                html.append(" ");
                html.append(value2);
                html.append("\n");
            }
            html.append("</div>\n");

        }
        List<? extends DocTree> list = dcTree.getBlockTags();
        if(!Is.empty(list)) {
            html.append("<div class=\"javadoc-block\">");
            for(DocTree docTree : list) {
                html.append("<div>");
                String text = docTree.toString();
                text = enhanceImportant(text);
                html.append(text);
                html.append("</div>\n");
            }
            html.append("</div>\n");
        }
        html.append("</div>\n");
        String javadoc = html.toString();
        return unicodeToHtml(javadoc);
    }

    private static String enhanceImportant(String value) {
        if(value == null) return value;
        value = value.replace("@return", "<span class=\"important\">@return</span>");
        value = value.replace("@throws", "<span class=\"important\">@throws</span>");
        int lastpos = 0;
        int pos = value.indexOf("@param ", lastpos);
        while(pos > -1) {
            int pos2 = value.indexOf(" ", pos + 7);
            if(pos2 > -1) {
                value = value.substring(0, pos + 7)
                    + "<span class=\"important\">"
                    + value.substring(pos + 7, pos2)
                    + "</span>"
                    + value.substring(pos2);
            }
            lastpos = pos + 8;
            pos = value.indexOf("@param ", lastpos);
        }
        return value;
    }
    
    public String fixannotations(String text) {
        if(element == null) return "";
        StringBuilder sb = new StringBuilder();
        int lastpos = 0;
        int atpos = text.indexOf('@', lastpos);
        while(atpos > -1) {            
            sb.append(text.substring(lastpos, atpos+1));
            lastpos = atpos + 1;
            int pos = lastpos;
            char c = text.charAt(pos++);
            while(
                (pos < text.length()) && (
                    (c == '.')
                    || (c == '_')
                    || (c == '@')
                    || (('a'<=c) && (c<='z'))
                    || (('A'<=c) && (c<='Z'))
                    || (('0'<=c) && (c<='9'))
                )) {
                if(c == '.') lastpos = pos;
                c = text.charAt(pos++);
            }
            atpos = text.indexOf('@', lastpos);
            
        }
        sb.append(text.substring(lastpos));
        return unicodeToHtml(sb.toString());
    }
    public String annotations() {
        if(element == null) return "";
        List<? extends AnnotationMirror> list = element.getAnnotationMirrors();
        if(Is.empty(list)) return "";

        StringBuilder html = new StringBuilder();
        html.append("<div class=\"annotations\">\n");
        for(AnnotationMirror annotationMirror : list) {
            String text = annotationMirror.toString();
            text = fixannotations(text);
            html.append("<div>");
            html.append(text);
            html.append("</div>\n");
        }
        html.append("</div>\n");
        return unicodeToHtml(html.toString());
    }
    
    public String javadocFirst() {
        if(element == null) return "";
        if(dcTree() == null) return "";

        String value = toString(dcTree.getFirstSentence());
        return unicodeToHtml(value);
    }

    private static String unicodeToHtml(String text) {
        if(Is.empty(text)) return text;
        return text.replaceAll("\\\\u(....)", "&#x$1;");
    }
    
    private String toString(List<? extends DocTree> list) {
        if(Is.empty(list)) return "";
        StringBuilder sb = new StringBuilder();
        for(DocTree docTree : list) {
            sb.append(" ").append(docTree);
        }
        if(sb.length() == 0 ) return null;
        return Unicode.unescape(sb.toString());
    } 

//    public static void main(String[] argv) {
//        String text = "Va&#x0161;a zna\\u010dka z vá\\u0161ho systému slú\\u017eiaca na mo\\u017ené párovanie výsledku a ur\\u010dujúc mo\\u017ený dôvod lustrovania v CRE, povinná pre orgán verejnej moci.";
//        System.out.println(" text1: " + text);
//        System.out.println(" text2: " + unicodeToHtml(text));
//    }
}
