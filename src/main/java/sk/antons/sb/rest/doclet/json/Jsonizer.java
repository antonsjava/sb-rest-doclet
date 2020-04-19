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
package sk.antons.sb.rest.doclet.json;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;

/**
 *
 * @author antons
 */
public class Jsonizer {

    private ObjectMapper om;

    public Jsonizer() {
        om = objectMapper();
    }
    public static Jsonizer instance() { return new Jsonizer(); }

    public String jsonize(Object o) {
        if(o == null) return "";
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            String text = om.writeValueAsString(o);
            return text;
        } catch (Exception e) {
            return "" + e;
        }
    }

    protected static ObjectMapper objectMapper() {
        ObjectMapper om = new ObjectMapper();

        om.setSerializationInclusion(Include.NON_EMPTY);
        om.disable(SerializationFeature.WRAP_ROOT_VALUE);
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        //om.disable(DeserializationFeature.UNWRAP_ROOT_VALUE);
        om.enable(SerializationFeature.INDENT_OUTPUT);
        
        //om.setDateFormat(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss"));
        om.registerModule(new JavaTimeModule());

        return om;
    }
    
}
