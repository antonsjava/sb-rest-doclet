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

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import javax.tools.JavaFileManager;
import sk.antons.jaul.util.AsRuntimeEx;

/**
 *
 * @author antons
 */
public class ClassResolver {
    private String path;
    private ClassLoader cl = null;
    
    public ClassResolver(String path) {
        this.path = path;
    }
    
    public void init(JavaFileManager fileManager) {
        try {
                List<File> paths = new ArrayList<>();
                if (path != null) {
                    for (String pathname : path.split(File.pathSeparator)) {
                        paths.add(new File(pathname));
                    }
                }
                //System.out.println(" == " + paths);

                URL[] urls = new URL[paths.size()];
                for(int i = 0; i < urls.length; i++) {
                    URL u = new URL("file://" + paths.get(i).getAbsolutePath());
                    urls[i] = u;
                    //urls[i] = paths.get(i).toURI().toURL();
                    //System.out.println(" uri2 " + urls[i]);
                    
                }
                cl = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
        
        } catch(Exception e) {
            System.out.println(" error " + e);
            e.printStackTrace(System.out);
        }
    }

    public Class resolve(String fqn) {
        try {
            return cl == null ? Class.forName(fqn) : cl.loadClass(fqn);
        } catch(Exception e) {
            throw AsRuntimeEx.argument(e, "unable to resolve {}", fqn);
        }
    }
    
}
