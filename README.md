xamarin-gradle-plugins
======================

Plugins to integrate Xamarin mobile apps into gradle and maven.
There are three plugins currently:

- xamarin-build-plugin: Allows you to build existing Xamarin.Android (compile and apk), Xamarin.iOS, and vanilla Xamarin projects by invoking builds against the .csproj/.sln files.
  Provides support for fetching dependencies under the 'xamarinCompile' configuration.
- xamarin-publish-plugin (optional): Adds configuration and tasks to publish a project configured with the build plugin to maven, using the maven-publish plugin.

Using these plugins in tandem will allow you to integrate Maven dependency management into your Xamarin projects for more modular builds.


The plugins can be installed into the local maven repository with "gradle install"

Applying the plugins
------------------------------
Apply the plugins like such:

```groovy
buildscript {
    repositories {
        mavenLocal()
    }
    dependencies {
        classpath 'au.org.trogdor.xamarin-gradle-plugins:xamarin-build-plugin:0.1'
        classpath 'au.org.trogdor.xamarin-gradle-plugins:xamarin-publish-plugin:0.1'
    }
}

apply plugin: 'xamarin-android-plugin'
apply plugin: 'xamarin-publish-plugin'
```

Using the build plugin
------------------------------
This plugin must be configured with enough information to invoke the xamarin build tools against your project's .csproj/.sln files in the correct build configuration.
All configuration for the build plugin is done under the 'xamarin' project extension.

A project block must be specified in the xamarin closure. Available project types are:
- androidProject
- iOSProject
- xbuildProject
- mdtoolProject
- genericProject

The block can optionally be configured with xbuildPath and mdtoolpath.
These default to 'xbuild' and '/Applications/Xamarin Studio.app/Contents/MacOS/mdtool' respectively, and will suit standard Xamarin installs.

*Example:*
```groovy
xamarin {
    xbuildPath '/usr/local/bin/xbuild'
    mdtoolPath '/usr/local/bin/mdtool'
    configurations {
        ...
    }
}
```

Dependencies
------------------------------
The 'xamarinCompile' configuration is added by the build plugin. DLL's which have been packaged as maven artifacts can be used here,
and will be copied into a 'dependencies' (by default) folder with the 'fetchXamarinDependencies' task, which also runs before build steps.

*Example:*
```groovy
dependencies {
    xamarinCompile 'au.com.sample.group:SampleComponent:1.0'
}
```

Debug symbols (.dll.mdb) get pushed to maven under the debug-symbols classifier, and can also be specified to allow debugging assemblies.

*Example:*
```groovy
dependencies {
    xamarinCompile 'au.com.sample.group:SampleComponent:1.0'
    xamarinCompile 'au.com.sample.group:SampleComponent:1.0:debug-symbols@dll.mdb'
}

Configurations are also added per configuration specified in the project. These configurations exend xamarinCompile. Eg if configurations are 'Debug' and 'Release' - 'xamarinCompileDebug' and 'xamarinCompileRelease' will be available which apply only when building these configurations.
```

*Tasks:*
- fetchXamarinDependencies


Android Projects
------------------------------
Android projects come in two flavors:
- androidAppProject - For Xamarin Android Application projects. Builds an apk file from the project
- androidLibraryProject - For Xamarin Android Library projects. Builds a dll file

A typical Xamarin Android Application project will be configured like so:
```groovy
xamarin {
    androidAppProject {
        projectName 'Project'
        configurations {
            Debug
            Release
        }
    }
}
```

A typical Xamarin Android Library project will be configured like so:
```groovy
xamarin {
    androidLibraryProject {
        projectName 'Project'
        configurations {
            Debug
            Release
        }
    }
}
```

*Tasks:*
- xamarinBuild-Debug
- xamarinBuild-Release
- xamarinBuildAll

This should be sufficient for most projects.

projectName is optional, though it is the simplest way to configure the project. The plugin will that your project file is then ${projectName}.csproj within the same folder.
Alternatively you may specify 'projectFile' directly.

The blocks under configurations {} should match up with the build configurations specified in your Xamarin project.
For each configuration, you add build targets (build and/or androidPackage), optionally specifying the output file which has ben specified in the Xamarin project.
However if the projectName is specified, the output file defaults to 'bin/$configuration/$projectName.dll'

dependencyDir can also be specified, and will define where downloaded dependencies get copied into

*Example app configuration with all parameters:*
```groovy
xamarin {
    androidAppProject {
        projectFile '../Project.csproj'
        dependencyDir 'libs'
        configurations {
            CustomDebugTarget {
                buildOutput 'bin/CustomDebugFolder/CustomApp.apk'
            }
            CustomReleaseTarget {
                 buildOutput 'bin/CustomReleaseFolder/CustomApp.apk'
            }
        }
    }
}
```

*Tasks:*
- xamarinBuild-CustomDebugTarget
- xamarinBuild-CustomReleaseTarget
- xamarinBuildAll



iOS Projects
--------------------------
iOS projects similarly come in two flavors:
- iOSAppProject: For Xamarin iOS Application projects
- iOSLibraryProject: For Xamarin iOS Library projects

A typical Xamarin iOS app project will be configured like so:
```groovy
xamarin {
    iOSAppProject {
        projectName 'Project'
        solutionFile 'Solution.sln'
        dependencyDir 'libs'
        configurations {
            Debug
            Release
        }
    }
}
```

A typical Xamarin iOS library project will be configured like so:
```groovy
xamarin {
    iOSLibraryProject {
        projectName 'Project'
        solutionFile 'Solution.sln'
        dependencyDir 'libs'
        configurations {
            Debug
            Release
        }
    }
}
```

*Tasks:*
- xamarinBuild-Debug
- xamarinBuild-Release
- xamarinBuildAll

This is very similar to android projects, however instead of just project name, the solution file is required to be provided for mdtool to be able to build the project and its project dependencies.
For app projects, an ipa will be built if specified in the Xamarin project settings for the built configuration.

Generic Projects
--------------------------
Similarly, there are configurations for standard Xamarin/Mono projects:
- genericAppProject
- genericLibraryProject

Configuration follows the same pattern as Android and iOS projects

Using the publishing plugin
---------------------------
The publishing plugin leverages the maven-publish plugin, adding a maven publishing configuration for the project.
Under this maven configuration, the output dll for each configuration will be added as an artifact, with artifactId equal to to the projectName by default, and other parameters (group, version) pulled from the project properties. The configuration name will be used as the classifier
If the mdb debug file exists in the specified configuration, it will be published with the 'debug-symbols' classifier. 
This will add the typical maven publishing tasks. The 'publish' and 'publishToMavenLocal' tasks will be configured to depend on 'xamarinBuildAll', making an easy workflow where libraries can be published with one command.

*Typical configuration:*
```groovy
xamarinPublish {
	mavenPublish()
}
```

The project could then be used as a dependency as such:
```groovy
dependencies {
	//For all configurations
    xamarinCompile 'artifact.group:artifactid:1.0:release@dll'
    
    //Or
    xamarinCompileDebug 'artifact.group:artifactid:1.0:debug@dll'
    'artifact.group:artifactid:1.0:debug-symbols@dll.mdb'

    xamarinCompileRelease 'artifact.group:artifactid:1.0:release@dll',
}
```

*Tasks:*
- Standard maven tasks

In this example, the dll produced by the Release configuration of the specified project will be published.

*Custom artifactId:*
```groovy
xamarinPublish {
	artifactId 'CustomArtifact'
	mavenPublish()
}
```

