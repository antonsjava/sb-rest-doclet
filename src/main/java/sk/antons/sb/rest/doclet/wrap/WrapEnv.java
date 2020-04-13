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
import java.util.ArrayList;
import java.util.List;
import sk.antons.sb.rest.doclet.cl.ClDb;

/**
 *
 * @author antons
 */
public class WrapEnv {
    DocTrees treeUtils;
    ClDb classDb;
    List<String> used = new ArrayList<>();

    public DocTrees getTreeUtils() { return treeUtils; }
    public void setTreeUtils(DocTrees treeUtils) { this.treeUtils = treeUtils; }
    public ClDb getClassDb() { return classDb; }
    public void setClassDb(ClDb classDb) { this.classDb = classDb; }

    public List<String> used() { return used; }


    
}
