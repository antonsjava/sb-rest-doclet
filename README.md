# sb-rest-doclet

 Spring boot rest javadoc doclet

## Motivation
 
 I was unable to find maven plugin which generates simple static documentation of spring 
 boot rest API.

 I was using enunciate plugin but I missed some trivial features like transfer javadoc info to generated documentation.

## Excuse

 I provide this software as open source, but it is horrible code. Doclet API is purely documented 
 and I have no strength to fix the code after I make it runnable. (maybe later)

 I publish it only to have possibility to put it to maven central repo so I can use it.

 So please excuse me.

## Basic configuration
 
 Configuration of doclet itself is still surprising me. I must enable standard doclet 
 configuration in plugin to make it working at all. But I'm reflecting only
  - -d (./target/site/apidocs)
  - -docencoding (UTF-8)
  
## Basic usage
 
 In your spring boot module with REST API you can use plugin in this form
 It generate documentation for controller classes in this module and model classes 
 which are in this module only.

```xml
 <plugin>
   <groupId>org.apache.maven.plugins</groupId>
   <artifactId>maven-javadoc-plugin</artifactId>
   <version>3.2.0</version>
   <configuration>
       <doclet>sk.antons.sb.rest.doclet.SBRestDoclet</doclet>
       <docletArtifacts>
           <docletArtifact>
               <groupId>com.github.antonsjava</groupId>
               <artifactId>sb-rest-doclet</artifactId>
               <version>1.0</version>
           </docletArtifact>
       </docletArtifacts>
       <useStandardDocletOptions>true</useStandardDocletOptions>
   </configuration>
 </plugin>
```

## Multimodule usage
 
 In your spring boot module with REST API you can use plugin in this form
 It generate documentation for controller classes in this module and model classes 
 which are in specified module. (It is necessary, that this module is explicitly listed
 as dependency of rest module)

 (Don't use only includeDependencySources property but explicitly name that module too.)

```xml
 <plugin>
   <groupId>org.apache.maven.plugins</groupId>
   <artifactId>maven-javadoc-plugin</artifactId>
   <version>3.2.0</version>
   <configuration>
       <doclet>sk.antons.sb.rest.doclet.SBRestDoclet</doclet>
       <docletArtifacts>
           <docletArtifact>
               <groupId>com.github.antonsjava</groupId>
               <artifactId>sb-rest-doclet</artifactId>
               <version>1.0</version>
           </docletArtifact>
       </docletArtifacts>
       <useStandardDocletOptions>true</useStandardDocletOptions>
       <includeDependencySources>true</includeDependencySources>
       <dependencySourceIncludes>
           <dependencySourceInclude>camp.xit.kiwi.msender:msender-model:*</dependencySourceInclude>
       </dependencySourceIncludes>
   </configuration>
 </plugin>
```
## Dummy json examples for model classes

 You need to add model modul as doclet dependence using docletArtifact.
 In this way model classes can be instantiated and doclet generate json example 
 using jackson object mapper.

```xml
 <plugin>
   <groupId>org.apache.maven.plugins</groupId>
   <artifactId>maven-javadoc-plugin</artifactId>
   <version>3.2.0</version>
   <configuration>
       <doclet>sk.antons.sb.rest.doclet.SBRestDoclet</doclet>
       <docletArtifacts>
           <docletArtifact>
               <groupId>com.github.antonsjava</groupId>
               <artifactId>sb-rest-doclet</artifactId>
               <version>1.0</version>
           </docletArtifact>
           <docletArtifact>
               <groupId>camp.xit.kiwi.msender</groupId>
               <artifactId>msender-model</artifactId>
               <version>${project.version}</version>
           </docletArtifact>
       </docletArtifacts>
       <useStandardDocletOptions>true</useStandardDocletOptions>
       <includeDependencySources>true</includeDependencySources>
       <dependencySourceIncludes>
           <dependencySourceInclude>camp.xit.kiwi.msender:msender-model:*</dependencySourceInclude>
       </dependencySourceIncludes>
   </configuration>
 </plugin>
```
## Example

 Simple project with pure documentation [here](./example/sb-rest-doclet-example)
 
 And resulted documentation [here](./example/sb-rest-doclet-example-result/site/apidocs/index-rest.html)

