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
package sk.antons.sb.rest.doclet.resource;

import java.io.InputStream;
import sk.antons.jaul.util.Resource;
import sk.antons.jaul.util.TextFile;

/**
 *
 * @author antons
 */
public class ResourceLoader {
    public static String resource(String resource) {
        InputStream is = Resource.url("classpath:"+resource).inputStream(ResourceLoader.class.getClassLoader());
        String value = TextFile.read(is, "utf-8");
        return value;
    }
}
