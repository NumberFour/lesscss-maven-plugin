#LessCSS Maven Plugin

This plugin compiles Less to CSS at build time. It is configured as follows:

    <build>
    ...
      <plugins>
        <plugin>
          <artifactId>lesscss-maven-plugin</artifactId>
          <groupId>eu.numberfour.maven.plugins</groupId>
          <executions>
            <execution>
              <goals>
                <goal>compile</goal>
              </goals>
              <phase>compile</phase>
            </execution>
          </executions>
          <configuration>
          <outputDir>${basedir}/target/classes</outputDir>
        </configuration>    
      </plugin>
    ...
    </build>
