xamarin-gradle-plugins
======================

Plugins to integrate Xamarin mobile apps into gradle and maven.
There are three plugins currently:

- xamarin-build-plugin: Allows you to build existing Xamarin.Android (compile and apk), Xamarin.iOS, and vanilla Xamarin projects by invoking builds against the .csproj/.sln files.
  Provides support for fetching dependencies under the 'references' configuration.
- xamarin-publish-plugin (optional): Adds configuration and tasks to publish a project configured with the build plugin to maven, eusing the maven-publish plugin.

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

apply plugin: 'xamarin-build-plugin'
apply plugin: 'xamarin-publish-plugin'
```

Using the build plugin
------------------------------
This plugin must be configured with enough information to invoke the xamarin build tools against your project's .csproj/.sln files in the correct build configuration.
All configuration for the build plugin is done under the 'xamarin' project extension.

A project block must be specified in the xamarin closure. Available project types are:
- androidAppProject
- iOSAppProject
- genericAppProject
- androidLibraryProject
- iOSLibraryProject
- genericLibraryProject


Customising tool paths
------------------------------
The block can optionally be configured with xbuildPath and mdtoolpath.
These default to 'xbuild' and '/Applications/Xamarin Studio.app/Contents/MacOS/mdtool' respectively, and will suit standard Xamarin installs.

*Example:*
```groovy
xamarin {
    xbuildPath '/usr/local/bin/xbuild'
    mdtoolPath '/usr/local/bin/mdtool'
    projectType {
        ...
    }
}
```

Dependencies
------------------------------
The 'references' configuration is added by the build plugin. DLL's which have been packaged as maven artifacts can be used here,
and will be copied into a 'dependencies' (by default) folder with the 'installDependencies\<configuration\>' task, which also runs before build steps.

The 'referencesMatched' configuration may also be used, which will use the correct maven classifier for your compiled configuration.
This is useful when used for library published with the xamarin-publishing-plugin, which published all configurations of a dll under classifiers named after the configuration.

You can use 'xamarin.referenceProject(projectPath)' to form a transitive dependency link to another gradle xamarin projecct, and all the dependent project's dependency dlls will be installed into this project.

*Example:*
```groovy
dependencies {
    references 'au.com.sample.group:SampleComponent:1.0@dll'

    //Or use debug dll for Debug configuration, release dll for Release configuration, etc
    referencesMatched 'au.com.sample.group:SampleComponent:1.0'

    //Depend on dlls references from other project
    xamarin.referenceProject(':subproject:sampleproject')
}
```

Debug symbols (.dll.mdb) get pushed to maven under the debug-symbols classifier, and can also be specified to allow debugging assemblies.

*Example:*
```groovy
dependencies {
    references 'au.com.sample.group:SampleComponent:1.0:debug@dll'
    references 'au.com.sample.group:SampleComponent:1.0:debug-symbols@dll.mdb'
}

```

*Tasks:*
- installDependencies\<configuration\>

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
    }
}
```

A typical Xamarin Android Library project will be configured like so:
```groovy
xamarin {
    androidLibraryProject {
        projectName 'Project'
    }
}
```

*Tasks:*
- build\<configuration\>
-- buildDebug
-- buildRelease
- buildAll

This should be sufficient for most projects.

projectName is optional, though it is the simplest way to configure the project. The plugin will that your project file is then ${projectName}.csproj within the same folder.
Alternatively you may specify 'projectFile' directly.

The blocks under configurations {} should match up with the build configurations specified in your Xamarin project.
For each configuration, you add build targets (build and/or androidPackage), optionally specifying the output file which has ben specified in the Xamarin project.
However if the projectName is specified, the output file defaults to 'bin/$configuration/$projectName.dll'
The default configurations include Debug and Release, which will suit most projects which haven't had configurations modified.

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
            CustomSomeOtherTarget
            CustomAnotherTargetWithCustomOutput {
                buildOutput 'bin/CustomOther/CustomApp.apk'
            }
        }
    }
}
```

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
    }
}
```

*Tasks:*
- build\<configuration\>
-- buildDebug
-- buildRelease
- buildAll

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
This will add the typical maven publishing tasks. The 'publish' and 'publishToMavenLocal' tasks will be configured to depend on 'buildAll', making an easy workflow where libraries can be published with one command.

The project could then be used as a dependency as such:
```groovy
dependencies {
	//For all configurations, use release dll
    references 'artifact.group:artifactid:1.0:release@dll'
    
    //Or use debug dll for Debug configuration, release dll for Release configuration, etc
    referencesMatched 'artifact.group:artifactid:1.0'
}
```

*Tasks:*
- Standard maven tasks

Applying this plugin is all the configuration usually required.
However, the artifactId can be overridden. By default it is derived from the project name.

*Custom artifactId:*
```groovy
xamarinPublish {
	artifactId 'CustomArtifact'
}
```

