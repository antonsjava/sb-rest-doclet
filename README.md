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
       <docletPath>${basedir}/target/classes</docletPath>
       <docletArtifacts>
           <docletArtifact>
               <groupId>com.github.antonsjava</groupId>
               <artifactId>sb-rest-doclet</artifactId>
               <version>1.13</version>
           </docletArtifact>
           <docletArtifact>
               <groupId>${project.groupId}</groupId>
               <artifactId>${project.artifactId}</artifactId>
               <version>${project.version}</version>
           </docletArtifact>
       </docletArtifacts>
       <!--additionalOptions> some substring of controller fqn if you need filter docdumented controllers
           -include sk.antons.project.api
           -exclude AdminController
       </additionalOptions-->
       <useStandardDocletOptions>true</useStandardDocletOptions>
       <includeDependencySources>true</includeDependencySources>
       <dependencySourceIncludes>
           <dependencySourceInclude>${project.groupId}:${project.artifactId}:${project.version}</dependencySourceInclude>
       </dependencySourceIncludes>
   </configuration>
   <executions>
       <execution>
           <id>rest-doc</id>
           <phase>deploy</phase>
           <goals>
               <goal>javadoc</goal>
           </goals>
       </execution>
   </executions>
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
       <docletPath>${basedir}/target/classes</docletPath>
       <docletArtifacts>
           <docletArtifact>
               <groupId>com.github.antonsjava</groupId>
               <artifactId>sb-rest-doclet</artifactId>
               <version>1.3</version>
           </docletArtifact>
           <docletArtifact>
               <groupId>${project.groupId}</groupId>
               <artifactId>${project.artifactId}</artifactId>
               <version>${project.version}</version>
           </docletArtifact>
       </docletArtifacts>
       <useStandardDocletOptions>true</useStandardDocletOptions>
       <includeDependencySources>true</includeDependencySources>
       <dependencySourceIncludes>
           <dependencySourceInclude>${project.groupId}:${project.artifactId}:${project.version}</dependencySourceInclude>
           <dependencySourceInclude>camp.xit.kiwi.msender:msender-model:*</dependencySourceInclude>
       </dependencySourceIncludes>
   </configuration>
   <executions>
       <execution>
           <id>rest-doc</id>
           <phase>deploy</phase>
           <goals>
               <goal>javadoc</goal>
           </goals>
       </execution>
   </executions>
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
       <docletPath>${basedir}/target/classes</docletPath>
       <docletArtifacts>
           <docletArtifact>
               <groupId>io.github.antonsjava</groupId>
               <artifactId>sb-rest-doclet</artifactId>
               <version>LASTVERSION</version>
           </docletArtifact>
           <docletArtifact>
               <groupId>${project.groupId}</groupId>
               <artifactId>${project.artifactId}</artifactId>
               <version>${project.version}</version>
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
           <dependencySourceInclude>${project.groupId}:${project.artifactId}:${project.version}</dependencySourceInclude>
           <dependencySourceInclude>camp.xit.kiwi.msender:msender-model:*</dependencySourceInclude>
       </dependencySourceIncludes>
   </configuration>
   <executions>
       <execution>
           <id>rest-doc</id>
           <phase>deploy</phase>
           <goals>
               <goal>javadoc</goal>
           </goals>
       </execution>
   </executions>
 </plugin>
```
## Example

 Simple project with pure documentation [here](./example/sb-rest-doclet-example)
 
 And resulted documentation [here](./example/sb-rest-doclet-example-result/site/apidocs/index-rest.html)

 You can download there files and look for them. You can also build example project and 
 generate that documentation. (plugin geterate during deploy or directly start plugin mvn clean javadoc:javadoc)

