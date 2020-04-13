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

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.util.DocTreeScanner;
import com.sun.source.util.DocTrees;
import java.io.File;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementScanner9;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import java.util.logging.Logger;
import javax.tools.JavaFileManager;
import sk.antons.jaul.Get;
import sk.antons.jaul.Is;
import sk.antons.jaul.pojo.Messer;
import sk.antons.jaul.pojo.Pojo;
import sk.antons.jaul.util.TextFile;
import sk.antons.sb.rest.doclet.cl.Cl;
import sk.antons.sb.rest.doclet.cl.ClDb;
import sk.antons.sb.rest.doclet.finder.ControllerFinder;
import sk.antons.sb.rest.doclet.finder.EndpointFinder;
import sk.antons.sb.rest.doclet.json.Jsonizer;
import sk.antons.sb.rest.doclet.resource.ResourceLoader;
import sk.antons.sb.rest.doclet.wrap.ControllerWrap;
import sk.antons.sb.rest.doclet.wrap.EndpointWrap;
import sk.antons.sb.rest.doclet.wrap.ModelWrap;
import sk.antons.sb.rest.doclet.wrap.VariableWrap;
import sk.antons.sb.rest.doclet.wrap.WrapEnv;
/**
 *
 * @author antons
 */
public class SBRestDoclet implements Doclet {
    private static Logger log = Logger.getLogger(SBRestDoclet.class.getName());
    
    
    @Override
    public void init(Locale locale, Reporter reporter) { 
        this.reporter = reporter;
    }
    
    
    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
 
    @Override
    public Set<? extends Option> getSupportedOptions() {
        return options;
        //return Collections.emptySet();
    }
 
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

 
    private static final boolean OK = true;
    private static final boolean FAILED= false;
 
    private boolean showElements = true;
    private boolean showComments = true;
 
    private Reporter reporter;
    private DocTrees treeUtils;
    private JavaFileManager fileManager;
    private ClassResolver clresolver = new ClassResolver("");
    private String destination;
    private String doctitle;
    private String docencoding;
    WrapEnv env;
    ClDb classDb = new ClDb();
    Messer messer = Pojo.messer();
    Jsonizer jsonizer = Jsonizer.instance();
 
    @Override
    public boolean run(DocletEnvironment environment) {
        try {
            
            note("init");
            //System.out.println(System.getProperties());
            //System.out.println(" ------- BasicDoclet start -----------");
            
            //System.out.println(" init1 ");
            treeUtils = environment.getDocTrees();
            //System.out.println(" init2 ");
            fileManager = environment.getJavaFileManager();
            //System.out.println(" init3 ");
            //clresolver.init(fileManager);
            //System.out.println(" init4 ");
            if(Is.empty(docencoding)) docencoding = "UTF-8";
            if(Is.empty(doctitle)) doctitle = "REST API";
            if(Is.empty(destination)) destination = "./target/site/apidocs";
            env = new WrapEnv();
            env.setClassDb(classDb);
            env.setTreeUtils(treeUtils);
            
    //        for(Element includedElement : environment.getIncludedElements()) {
    //            System.out.println(" included: " + includedElement);
    //        }
    //        for(Element includedElement : environment.getSpecifiedElements()) {
    //            System.out.println(" specified: " + includedElement);
    //        }
            createFolders();
            
            //System.out.println(" build class list ");
            //traverseElements(environment.getIncludedElements(), "", classDb, included);
            buildClassDb(environment.getIncludedElements(), classDb);
            //classDb.print();
            //System.out.println(" build class tree start ");
            classDb.buildTree();
            //System.out.println(" build class tree end ");
            classDb.addMesserMapping(messer, clresolver);
            Set<? extends Element> specifiedElements = environment.getSpecifiedElements();
            //traverseElements(specifiedElements, "");

            
            ControllerFinder controllerfinder = new ControllerFinder();
            List<Element> controllers = controllerfinder.findIn(specifiedElements);
            note("numner of controllers " + Get.size(controllers));
            //System.out.println(" controllers: " + controllers);
            EndpointFinder endpointfinder = new EndpointFinder();
            List<Element> endpointsElems = endpointfinder.findIn(controllers);
            List<EndpointWrap> endpoints  = EndpointWrap.toEndpoints(endpointsElems, env);
            Set<String> modelClasses = new HashSet<>();
            for(EndpointWrap endpoint : endpoints) {
                endpoint.closure();
            }
            note("numner of endpoints " + Get.size(endpoints));
    //        for(String string : env.used()) {
    //            System.out.println(" used --- " +string);
    //        }
            classDb.closure(env);
            note("numner of model classes " + Get.size(env.used()));
            note("process model classes...");
            for(String string : env.used()) {
                processModel(string);
    //            System.out.println(" used --- " +string);
    //            Cl cl = classDb.get(string);
    //            if(cl == null) {
    //                System.out.println("    no cl");
    //            } else {
    //                System.out.println("     included --- " + environment.isIncluded(cl.element()));
    //                System.out.println("     selected --- " + environment.isSelected(cl.element()));
    //                String javadoc = ControllerWrap.instance(cl.element(), env).javadoc();
    //                if(Is.empty(javadoc)) System.out.println("    no javadoc");
    //            }
            }

            note("process endpoint index...");
            processEndpoints(endpointsElems);
            note("process model index...");
            processModels(env.used());
            note("process controllers...");
            for(Element element : controllers) {
                processController(element);
            }
            note("process done...");
        } catch(Exception e) {
            note("process failed..." + e);
        }
        return OK;
    }

    private void createFolders() {
        File f = new File(destination + "/css");
        if(!f.exists()) f.mkdirs();
        f = new File(destination + "/rest");
        if(!f.exists()) f.mkdirs();
        f = new File(destination + "/model");
        if(!f.exists()) f.mkdirs();
        //System.out.println(" ---- main.css");
        String css = ResourceLoader.resource("css/main.css");
        //System.out.println(" ---- main.css " + css);
        TextFile.save(destination + "/css/main.css", docencoding, css);
    }
    
    private void processModel(String fqn) {
        Cl cl = classDb.get(fqn);
        if(cl == null) {
            //System.out.println(" no class fqn");
            return;
        }
        TypeElement te = (TypeElement)cl.element();
        ModelWrap wrap = ModelWrap.instance(te, env);
        StringBuilder file = new StringBuilder();
        file.append(fileprefix("../css/main.css"));
        file.append(" <div class=\"main-container models\">\n");
        file.append(" <div class=\"main-background\">\n");
        file.append("\n" );
        file.append("     <div class=\"menu\">\n" );
        file.append("		 <span class=\"menu-item rests\"><a href=\"../index-rest.html\">rest</a></span>\n" );
        file.append("		 <span class=\"menu-item models\"><a href=\"../index-model.html\">model</a></span>\n" );
        file.append("	 </div>\n" );
        file.append("\n" );
        file.append("     <div class=\"header\">").append(wrap.simpleName()).append("</div>\n" );
        file.append("\n" );
        file.append("     <hr/>\n" );
        file.append("\n" );
        file.append(wrap.javadoc());
        file.append("\n" );
        file.append(wrap.annotations());
        file.append("\n" );
        file.append("  <hr/>\n" );
        file.append("\n" );
        List<VariableWrap> fields = wrap.fields();
        file.append("\n" );
        if(!Is.empty(fields)) {
            file.append("  <div class=\"fields\">\n" );
            file.append("  	<table>\n" );
            String value = wrap.ancestors();
            if(!Is.empty(value)) {
                file.append("            <tr>\n" );
                file.append("                <td>ancestors</td> \n" );
                file.append("                <td colspan=2>").append(value).append("</td> \n" );
                file.append("            </tr>\n" );
            }
            value = wrap.descendants();
            if(!Is.empty(value)) {
                file.append("            <tr>\n" );
                file.append("                <td>descendants</td> \n" );
                file.append("                <td colspan=2>").append(value).append("</td> \n" );
                file.append("            </tr>\n" );
            }
            for(VariableWrap field : fields) {
                file.append("            <tr>\n" );
                file.append("                <td><b>").append(field.simpleName()).append("</b></td> \n" );
                file.append("                <td>").append(field.javaTypeAsHtml(false)).append("</td> \n" );
                file.append("                <td>\n" );
                value = field.javadoc();
                if(!Is.empty(value)) {
                    file.append(value).append("\n" );
                }
                value = field.annotations();
                if(!Is.empty(value)) {
                    file.append(value).append("\n" );
                }
                file.append("                </td>\n" );
                file.append("            </tr>\n" );
            }
            file.append("  	</table>\n" );
            file.append("  </div>\n" );
            file.append("  <div class=\"json\">\n" );
            file.append(json(fqn));
            file.append("  </div>\n" );
            file.append("  </div>\n" );
            file.append("  </div>\n" );
        }
        file.append("  <hr/>\n" );

        file.append(ResourceLoader.resource("html/postfix.html"));
        TextFile.save(destination + "/model/"+te.getQualifiedName()+".html", docencoding, file.toString());
        
    }
    
    private void processModels(List<String> used) {
        Collections.sort(used);
        StringBuilder file = new StringBuilder();
        file.append(fileprefix("./css/main.css"));
        file.append(" <div class=\"main-container models\">\n");
        file.append(" <div class=\"main-background\">\n");
        file.append("	 \n" );
        file.append("     <div class=\"menu\">\n" );
        file.append("		 <span class=\"menu-item rests\"><a href=\"./index-rest.html\">rest</a></span>\n" );
        file.append("		 <span class=\"menu-item models\"><a href=\"./index-model.html\">model</a></span>\n" );
        file.append("	 </div>\n" );
        file.append("  \n" );
        file.append("     <div class=\"header\">model list</div>\n" );
        file.append("  \n" );
        file.append("     <hr/>\n" );
        file.append("  \n" );
        file.append("\n" );
        if(!Is.empty(used)) {
            file.append("  	<table class=\"rest-list\">\n" );
            for(String use : used) {
                ModelWrap model = ModelWrap.instance(classDb.get(use).element(), env);
                file.append("        <tr><td><a href=\"./model/").append(model.qualifiedName()).append(".html\">")
                    .append(model.simpleName())
                    .append("</td><td>")
                    .append(model.javadocFirst()).append("</td> </tr>\n" );
            }
            file.append("  	</table>\n" );
            file.append("    \n" );
            file.append("  <hr/>\n" );
            file.append("  </div/>\n" );
            file.append("  </div/>\n" );
        }
        file.append(ResourceLoader.resource("html/postfix.html"));
        TextFile.save(destination + "/index-model.html", docencoding, file.toString());
    }

    private void processEndpoints(List<Element> elements) {
        StringBuilder file = new StringBuilder();
        file.append(fileprefix("./css/main.css"));
        file.append(" <div class=\"main-container rests\">\n");
        file.append(" <div class=\"main-background\">\n");
        file.append("	 \n" );
        file.append("     <div class=\"menu\">\n" );
        file.append("		 <span class=\"menu-item rests\"><a href=\"./index-rest.html\">rest</a></span>\n" );
        file.append("		 <span class=\"menu-item models\"><a href=\"./index-model.html\">model</a></span>\n" );
        file.append("	 </div>\n" );
        file.append("  \n" );
        file.append("     <div class=\"header\">rest list</div>\n" );
        file.append("  \n" );
        file.append("     <hr/>\n" );
        file.append("  \n" );
        file.append("\n" );
        if(!Is.empty(elements)) {
            file.append("  	<table class=\"rest-list\">\n" );
            List<EndpointWrap> endpoints = EndpointWrap.toEndpoints(elements, env);
            Collections.sort(endpoints);
            for(EndpointWrap endpoint : endpoints) {
                file.append("        <tr><td> <span class=\"method\">")
                    .append(endpoint.method())
                    .append("</span></td> <td><a href=\"./rest/").append(endpoint.controllerName()).append(".html#").append(endpoint.id()).append("\"><span class=path>").append(endpoint.fullRootPath()).append("</span></a></td> <td>")
                    .append(endpoint.javadocFirst()).append("</td> </tr>\n" );
            }
            file.append("  	</table>\n" );
            file.append("    \n" );
            file.append("  <hr/>\n" );
            file.append("  </div/>\n" );
            file.append("  </div/>\n" );
        }
        file.append(ResourceLoader.resource("html/postfix.html"));
        TextFile.save(destination + "/index-rest.html", docencoding, file.toString());
    }

    private void processController(Element element) {
        TypeElement te = (TypeElement)element;
        ControllerWrap wrap = ControllerWrap.instance(element, env);
        StringBuilder file = new StringBuilder();
        file.append(fileprefix("../css/main.css"));
        file.append(" <div class=\"main-container rests\">\n");
        file.append(" <div class=\"main-background\">\n");
        file.append("	 \n" );
        file.append("     <div class=\"menu\">\n" );
        file.append("		 <span class=\"menu-item rests\"><a href=\"../index-rest.html\">controllers</a></span>\n" );
        file.append("		 <span class=\"menu-item models\"><a href=\"../index-model.html\">model</a></span>\n" );
        file.append("	 </div>\n" );
        file.append("  \n" );
        file.append("     <div class=\"header\">").append(wrap.simpleName()).append("</div>\n" );
        file.append("  \n" );
        file.append("     <hr/>\n" );
        file.append("  \n" );
        file.append("      <div>\n" );
        file.append("            Root path: <span class=\"path\">").append(wrap.rootPath()).append("</span>\n" );
        file.append("      </div>\n" );
        file.append(wrap.javadoc());
        file.append("\n" );
        file.append(wrap.annotations());
        file.append("\n" );
        file.append("  <hr/>\n" );
        file.append("\n" );
        List<EndpointWrap> endpoints = wrap.endpoints();
        Collections.sort(endpoints);
        if(!Is.empty(endpoints)) {
            file.append("  	<table class=\"rest-list\">\n" );
            for(EndpointWrap endpoint : endpoints) {
                file.append("        <tr><td> <span class=\"method\">")
                    .append(endpoint.method())
                    .append("</span></td> <td><a href=\"#").append(endpoint.id()).append("\"><span class=path>").append(endpoint.fullRootPath()).append("</span></a></td> <td>")
                    .append(endpoint.javadocFirst()).append("</td> </tr>\n" );
            }
            file.append("  	</table>\n" );
            file.append("    \n" );
            file.append("  <hr/>\n" );
        }
        file.append("\n" );
        if(!Is.empty(endpoints)) {
            for(EndpointWrap endpoint : endpoints) {
                file.append("  <div class=\"rest\"> <a id=\"").append(endpoint.id()).append("\"></a>\n" );
                file.append("  	<table class=\"rest-list\">\n" );
                file.append("            <tr>\n" );
                file.append("                <td> <span class=\"method\">").append(endpoint.method()).append("</span></td> \n" );
                file.append("                <td><span class=path>").append(endpoint.fullRootPath()).append("</span></td> \n" );
                file.append("            </tr>\n" );
                file.append("  	</table>\n" );
                file.append(endpoint.javadoc());
                file.append("      \n" );
                file.append(endpoint.annotations());
                file.append("      \n" );
                file.append("  	<table class=\"param-list\">\n" );
                file.append("        <tr>\n" );
                file.append("            <td>Returns:</td> \n" );
                file.append("            <td colspan=2>").append(endpoint.returnTypeAsHtml(false)).append("</td> \n" );
                file.append("        </tr>\n" );
                List<VariableWrap> params = endpoint.params();
                if(!Is.empty(params)) {
                    for(VariableWrap param : params) {
                        file.append("        <tr>\n" );
                        file.append("            <td>param <b>").append(param.simpleName()).append("</b></td> \n" );
                        file.append("            <td>").append(param.javaTypeAsHtml(false)).append("</td> \n" );
                        file.append("            <td>").append(param.annotations()).append("</td> \n" );
                        file.append("        </tr>\n" );
                    }
                }
                file.append("        <tr>\n" );
                file.append("            <td>throws</td> \n" );
                file.append("            <td colspan=2>").append(endpoint.throwsAsHtml(false)).append("</td> \n" );
                file.append("        </tr>\n" );
                file.append("  	</table>\n" );
                file.append("  </div>\n" );
            }
        }
        file.append("  <hr/>\n" );
        file.append("  </div/>\n" );
        file.append("  </div/>\n" );
//        System.out.println(" ---------- " + element.getSimpleName() + " -----------");
//        System.out.println(" " + te.getQualifiedName());
//        AnnotationMirror requestMapping = ElementHelper.annotatoonByClass(element, "org.springframework.web.bind.annotation.RequestMapping");
//        String root = ElementHelper.annotationParam(requestMapping, "path");
//        System.out.println(" - root: " + root);
//        TreePath dct = treeUtils.getPath(element);
//        System.out.println(" - tp: " + dct);
//        //DocCommentTree dcTree = treeUtils.getDocCommentTree(dct);
//        //DocCommentTree dcTree = treeUtils.getDocCommentTree(dct.getCompilationUnit().getSourceFile());
//        DocCommentTree dcTree = treeUtils.getDocCommentTree(element);
//        if(dcTree != null) {
//            System.out.println(" - first: " + dcTree.getFirstSentence());
//            System.out.println(" - fullbody: " + dcTree.getFullBody());
//            System.out.println(" - getBody: " + dcTree.getBody());
//            System.out.println(" - getPreamble: " + dcTree.getPreamble());
//            System.out.println(" - getPostamble: " + dcTree.getPostamble());
//            System.out.println(" - getBlockTags: " + dcTree.getBlockTags());
//            System.out.println(" - all: " + dcTree);
//            System.out.println(" - getDoc: " + treeUtils.getDocComment(dct));
//        }

        file.append(ResourceLoader.resource("html/postfix.html"));
        TextFile.save(destination + "/rest/"+te.getQualifiedName()+".html", docencoding, file.toString());
        
    }
        
    private String fileprefix(String css) {
        String s = ResourceLoader.resource("html/prefix.html");
        s = s.replace("CSS_URL", css);
        return s;
    }

    
    
    
    private void processEndpoint(Element element) {
        //System.out.println(" -m--------- " + element.getSimpleName() + " -----------");
        ExecutableElement ee = (ExecutableElement)element;
        //System.out.println(" - return: " + ee.getReturnType());
        //System.out.println(" - params: " + ee.getParameters());
    }
    
    private void traverseElements(Collection<? extends Element> collection, String prefix, ClDb classDb, Set<String> included) {
        if(collection == null) return;
        for(Element element : collection) {
            traverseElement(element, prefix, classDb, included);
        }
    }

    private void traverseElement(Element e, String prefix, ClDb classDb, Set<String> included) {
        if(e == null) return;
        if(
            (e.getKind() == ElementKind.CLASS) 
            || (e.getKind() == ElementKind.INTERFACE) 
            || (e.getKind() == ElementKind.ENUM) 
            )  {
            String text = e.toString();
            if(included.contains(text)) classDb.add(e);
        }
        traverseElements(e.getEnclosedElements(), prefix + "  ", classDb, included);
//        System.out.println(prefix + " " + e.getKind() + " - " + e.getSimpleName());
//        DocCommentTree dcTree = treeUtils.getDocCommentTree(e);
//        System.out.println(prefix + "    dcTree: " + dcTree);
//        if(e.getKind() == ElementKind.CLASS) {
//            TypeElement te = (TypeElement)e;
//            String clname = te.getQualifiedName().toString();
//            try {
//                //Class dl = Class.forName(clname, true, SBRestDoclet.class.getClassLoader().getParent().getParent());
//                //Class dl = SBRestDoclet.class.getClassLoader().loadClass(clname);
//                //Class dl = Thread.currentThread().getContextClassLoader().loadClass(clname);
//                //Class dl = clresolver.resolve(clname);
//                Class dl = Class.forName(clname);
//                System.out.println(" class "+clname+" existexist");
//                System.out.println(" - json: " + Pojo.dumper().jsonPretty(Pojo.messer().junk(dl), "  "));
//            } catch(Throwable ex) {
//                System.out.println(" class "+clname+" notexist " + ex);
//            }
//            
//        }
    }
    
    private String json(String fqn) {
        if(Is.empty(fqn)) return "";
            try {
                Class dl = clresolver.resolve(fqn);
                Object o = Pojo.messer().junk(dl);
                //String json = Pojo.dumper().jsonPretty(o, "  ");
                String json = jsonizer.jsonize(o);
                return json;
            } catch(Throwable ex) {
                //System.out.println(" class "+fqn+" notexist " + ex);
                return "";
            }
    }

    private void note(String note) {
        System.out.println("sbrd: " + note);
    }

    private void buildClassDb(Set<? extends Element> list, ClDb classDb) {
        if(Is.empty(list)) return;
        for(Element e: list) {
            if(
                (e.getKind() == ElementKind.CLASS) 
                || (e.getKind() == ElementKind.INTERFACE) 
                || (e.getKind() == ElementKind.ENUM) 
                )  {
                classDb.add(e);
            }
        }
    }


    abstract class Option implements Doclet.Option {
        private final String name;
        private final int argCount;
        private final String description;
        private final String parameters;
 
        Option(String name, int argCount,
               String description, String parameters) {
            this.name = name;
            this.argCount = argCount;
            this.description = description;
            this.parameters = parameters;
        }
 
        @Override
        public int getArgumentCount() {
            return argCount;
        }
 
        @Override
        public String getDescription() {
            return description;
        }
 
        @Override
        public Kind getKind() {
            return Kind.STANDARD;
        }
 
        @Override
        public List<String> getNames() {
            return List.of(name);
        }
 
        @Override
        public String getParameters() {
            return argCount > 0 ? parameters : null;
        }

        @Override
        public boolean process(String option,
                               List<String> arguments) {
            //System.out.println(" option '" + option + "' - "+ arguments);
            return OK;
        }
    }
 
    private final Set<Option> options = Set.of(
            new Option("-author", 0, "an author", "<string>") {}
            , new Option("-bottom", 1, "a bottom", "<string>") {}
            , new Option("-charset", 1, "a charset", "<string>") {}
            , new Option("-d", 1, "a directory", "<string>") {
                    @Override
                    public boolean process(String option, List<String> arguments) {
                        destination = arguments.get(0);
                        return OK;
                    }
            }
            , new Option("-docencoding", 1, "a docencoding", "<string>") {
                    @Override
                    public boolean process(String option, List<String> arguments) {
                        docencoding = arguments.get(0);
                        return OK;
                    }
            }
            , new Option("-doctitle", 1, "a title", "<string>") {
                    @Override
                    public boolean process(String option, List<String> arguments) {
                        doctitle = arguments.get(0);
                        return OK;
                    }
            }
            , new Option("-linkoffline", 2, "a link", "<string>") {}
            , new Option("-use", 1, "an use", "") {}
            , new Option("-version", 1, "a version", "") {}
            , new Option("-windowtitle", 1, "a value", "") {}
            , new Option("-docletpath", 1, "a value", "") {
                    @Override
                    public boolean process(String option, List<String> arguments) {
//                        String path = arguments.get(0);
//                        try {
//                            System.out.println(" docletpath " + path);
//                            clresolver = new ClassResolver(path);
//                            System.out.println(" docletpath resolved " +clresolver);
//                        } catch(Exception e) {
//                            System.out.println(" error " + e);
//                            e.printStackTrace();
//                        }
                        return OK;
                    }
                
            }
    );
 


}
