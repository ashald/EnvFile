# EnvFile

## Description

**Env File** is a plugin for JetBrains IDEs that allows you to set environment variables for your run configurations
from one or multiple files.

### Supported Formats

- **.env**
- **YAML** dictionary
- **JSON** dictionary *(parsed with YAML parser since [JSON is subset of YAML][json-is-yaml])*

**All formats assume that both keys and values are strings.**

### Supported Platforms

<em>
Expand to see supported run configuration types. Italic means that run configuration is only available in paid
version of the product.
</em>
<br/>
<br/>
<details>
    <summary><strong>PyCharm</strong></summary>
    <ul>
        <li><em>App Engine server</em></li>
        <li><em>Behave</em></li>
        <li><em>Django server</em></li>
        <li><em>Django tests</em></li>
        <li>Lettuce</li>
        <li>Pyramid server</li>
        <li>Python</li>
        <li>
            Python docs
            <ul>
                <li>Docutils task</li>
                <li>Sphinx task</li>
            </ul>
        </li>
        <li>
            Python test
            <ul>
                <li>Unittests</li>
                <li>Doctests</li>
                <li>Nosetests</li>
                <li>py.test</li>
                <li>Attests</li>
            </ul>
        </li>        
        <li>Tox</li>
    </ul>
</details>

<details>
    <summary><strong>IDEA</strong></summary>
    <ul>
        <li>Application</li>
        <li><em>Arquillian JUnit</em></li>
        <li><em>Arquillian TestNG</em></li>
        <li><em>CloudBees Server</em></li>
        <li><em>Cucumber Java</em></li>
        <li>Gradle (requires "Enable Experimental Integrations")</li>
        <li><em>GWT Configuration</em></li>
        <li>
            <em>Geronimo Server</em>
            <ul>
                <li><em>Local</em></li>
                <li><em>Remote</em></li>
            </ul>
        </li>
        <li>
            <em>GlassFish Server</em>
            <ul>
                <li><em>Local</em></li>
                <li><em>Remote</em></li>
            </ul>
        </li>        
        <li><em>Google AppEngine Dev Server</em></li>
        <li><em>Grails</em></li>
        <li>JAR Application</li>
        <li>
            <em>JBoss Server</em>
            <ul>
                <li><em>Local</em></li>
                <li><em>Remote</em></li>
            </ul>
        </li>
        <li>
            <em>JSR45 Compatible Server</em>
            <ul>
                <li><em>Local</em></li>
                <li><em>Remote</em></li>
            </ul>
        </li>
        <li>
            <em>Jetty Server</em>
            <ul>
                <li><em>Local</em></li>
                <li><em>Remote</em></li>
            </ul>
        </li>
        <li>JUnit</li>
        <li>Kotlin</li>
        <li>Kotlin script</li>
        <li>
            <em>Resin</em>
            <ul>
                <li><em>Local</em></li>
                <li><em>Remote</em></li>
            </ul>
        </li>
        <li><em>Spring Boot</em></li>
        <li>
            <em>Spring dmServer</em>
            <ul>
                <li><em>Spring dmServer (Local)</em></li>
                <li><em>Spring dmServer (Remote)</em></li>
            </ul>
        </li>
        <li>TestNG</li>
        <li>
            <em>TomEE Server</em>
            <ul>
                <li><em>Local</em></li>
                <li><em>Remote</em></li>
            </ul>
        </li>
        <li>
            <em>TomCat Server</em>
            <ul>
                <li><em>Local</em></li>
                <li><em>Remote</em></li>
            </ul>
        </li>
        <li>
            <em>WebLogic Server</em>
            <ul>
                <li><em>Local</em></li>
                <li><em>Remote</em></li>
            </ul>
        </li>
        <li>
            <em>WebSphere Server</em>
            <ul>
                <li><em>Local</em></li>
                <li><em>Remote</em></li>
            </ul>
        </li>
    </ul>
</details>

<details>
    <summary><strong>RubyMine</strong></summary>
        <ul>
            <li>Capistrano</li>
            <li>Cucumber</li>
            <li>Gem Command</li>
            <li>IRB Console</li>
            <li>RSpec</li>
            <li>Rack</li>
            <li>Rails</li>
            <li>Rake</li>
            <li>Ruby</li>
            <li>Spork DRb</li>
            <li>Test::Unit/Shoulda/Minitest</li>
            <li>Zeus Server</li>  
        </ul>
</details>

<details>
    <summary><strong>GoLand</strong></summary>
        <ul>
            <li>Go App Engine</li>
            <li>Go Build</li>
            <li>Go Test</li>  
        </ul>
</details>

<details>
    <summary><strong>Cursive</strong> (>=1.8.2)</summary>
        <ul>
            <li>Local Clojure REPL</li>
            <li>Clojure Application</li>
            <li>Leiningen</li>
        </ul>
</details>

## Installation

- Using IDE built-in plugin system:
  - <kbd>Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Browse repositories...</kbd> > <kbd>Search for "Env File"</kbd> > <kbd>Install Plugin</kbd>

- Manually:
  - Download the [latest release][latest-release] and install it manually using <kbd>Preferences</kbd> >
  <kbd>Plugins</kbd> > <kbd>Install plugin from disk...</kbd>

Restart IDE.

## Usage

0) Add new *Run/Debug configuration*: <kbd>+</kbd> <kbd>Add new configuration</kbd> > <kbd>...</kbd>
1) Switch to <kbd>EnvFile</kbd> tab
2) Select <kbd>Enable EnvFile</kbd> checkbox
3) Select <kbd>Substitute Environment Variables</kbd> checkbox (if needed)
4) Select <kbd>Process JetBrains path macro references</kbd> checkbox (if needed)
5) Select <kbd>Ignore missing files</kbd> checkbox (if needed)
6) Select <kbd>Enable experimental integrations</kbd> checkbox (if needed)
7) Click on <kbd>+</kbd> to add a file
8) Adjust order as needed
6) Even variables defined within run configuration can be processed, ordered and substituted 

![Read from file](./docs/example.png)

### Caveats

#### Hidden files 
Hidden files (starting with a dot) are not displayed in Finder on `macOS` by default. To toggle
hidden files in the Open dialog, press <kbd>COMMAND</kbd> + <kbd>SHIFT</kbd> + <kbd>.</kbd>.
Alternatively, one can either tweak `macOS` to show hidden files or select any file using
standard Finder dialog and then manually edit path by double-clicking on the entry in the table.

#### Experimental Integrations
Not all run configurations available in IDEA-based IDEs are implemented similarly. Some of them differ significantly.
In certain cases (so far, only `Gradle` has been confirmed) the implementation exposes interfaces to integrate the EnvFile UI
but doesn't provide interfaces for it to actually make its work. Luckily, it was possible to make few assumptions about
IDEA's internal implementation and make it work. Such integration is very fragile and it's not immediately clear if it
will affect any existing integrations and when it will break. For that reason there is a special option to
`Enable Experimental Integrations` that can be enabled when desired and should prevent other integrations from breaking. 

### Examples

#### .env

```ini
# This line is ignored since it's a comment
SECRET_KEY=hip-hip-env-files
VERSION=1.0
```

or

```ini
# This line is ignored since it's a comment
SECRET_KEY hip-hip-env-files
VERSION 1.0
```

#### JSON

```yaml
{
    # JSON doesn't have comments but since JSON is subset of YAML
    # We parse it with YAML parser and therefore have comments
    # And even trialling commas in objects :)
    "SECRET_KEY": "hip-hip-env-files",
    "VERSION": "1.0", # All non-string literals should be enclosed in quotes; btw this is ignored too
}
```

#### YAML

```yaml
# This line is ignored since it's a comment
SECRET_KEY: hip-hip-env-files
VERSION: "1.0" # All non-string literals should be enclosed in quotes; btw this is ignored too
```

#### Bash (workaround)

There was a number of requests to support extracting environment variables from bash scripts like:

```bash
export SECRET_KEY="hip-hip-env-files"
export VERSION="1.0"
```

The feasible way to do that is yet to be discovered (if any at all) so the plugin does not support that at the moment.
On the other hand there is a simple workaround that can be used for the time being. The example bash script from above
can be split into an `.env` file and a generic script that can be used to set environment variables on a command line:

**.env**
```ini
SECRET_KEY="hip-hip-env-files"
VERSION="1.0"
```

**set-env.sh**
```bash
while read -r line; do export $line; done < .env
```
**usage**
```
$ . set-env.sh
$ echo $VERSION
1.0
```

### Variable Expansion

`EnvFile` also supports environment variable substitution. It's optional and disabled by default.
Implementation is based on [StringSubstitutor] so it's the best reference for how it works.

#### Examples

Syntax is *_derived_* from Bash but is way more primitive:
```
A=${FOO}            # A=""        <- unknown variables replaced by empty strings
B=${FOO:-default}   # B="default" <- default values can be set as in Bash
C=${B}              # C="default" <- it's possible to refer to other variables that were previously evaluated
D=$${C}             # D="$${C}"   <- double dollar serves as an escape character
E=$C                $ E="$C"      <- curly brackets are required
```

#### Precedence

Environment variables are evaluated in the order they are defined in files.
Files are evaluated in the order defined in EnvFile UI.
Environment variables defined in run configuration can be ordered relatively to files.
Order between environment variables defined in run configuration is not defined.  

It is possible to refer to any environment variables that were evaluated previously - within same file or from other sources.

### Path Macro Substitution

`EnvFile` can substitute JetBrains path macro references such as `$PROJECT_DIR$` etc. It's optional and disabled by default.
For details - see ["Working with $PROJECT_DIR$ in plugin configuration options"][Working with $PROJECT_DIR$ in plugin configuration options].  

# Further Development

- Add more formats (upon requests)
- Add support for other JetBrains products/plugins (upon requests)
- Add more tests (¯\\\_(ツ)_/¯)

# Building

EnvFile uses Gradle for building.

```bash
$ ./gradlew clean test build
  
  BUILD SUCCESSFUL in 22s
  59 actionable tasks: 59 executed
  
$ ls -1 build/distributions
  Env File-2.1.1-SNAPSHOT.zip
```

In order to open plugin's project in IDE, just open it as a Gradle project.
Then use `intelij:runId` on the root of the project to launch an IDEA instance.
You can set `IDE` env var for the run configuration pointing at IDE folder (`IDE Folder/Content` on macOS) to test
plugin with different versions.

# Feedback

Any feedback, bug reports and feature requests are highly appreciated!

Feel free to create an issue, contact me using `Github` or just drop me an email to the address specified in
[plugin.xml](./META-INF/plugin.xml)`/idea-plugin/vendor@email`.

# License

Copyright (c) 2017 Borys Pierov. See the [LICENSE](./LICENSE) file for license rights and limitations (MIT).

[json-is-yaml]:                                               https://en.wikipedia.org/wiki/YAML#Comparison_with_JSON
[latest-release]:                                             https://github.com/Ashald/EnvFile/releases/latest
[StringSubstitutor]:                                          https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/StringSubstitutor.html
[Working with $PROJECT_DIR$ in plugin configuration options]: https://intellij-support.jetbrains.com/hc/en-us/community/posts/206781805-Working-with-PROJECT-DIR-in-plugin-configuration-options
