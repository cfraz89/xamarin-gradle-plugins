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

*Tasks:*
- fetchXamarinDependencies


Android Projects
------------------------------
A typical Xamarin Android project will be configured like so:
```groovy
xamarin {
    androidProject {
        projectName 'Project'
        configurations {
            Debug {
                build()
            }
            Release {
                build()
                androidPackage()
            }
        }
    }
}
```

*Tasks:*
- xamarinBuild-Debug
- xamarinBuild-Release
- xamarinPackage-Release

This should be sufficient for most projects.

projectName is optional, though it is the simplest way to configure the project. The plugin will that your project file is then ${projectName}.csproj within the same folder.
Alternatively you may specify 'projectFile' directly.

The blocks under configurations {} should match up with the build configurations specified in your Xamarin project.
For each configuration, you add build targets (build and/or androidPackage), optionally specifying the output file which has ben specified in the Xamarin project.
However if the projectName is specified, the output file defaults to 'bin/$configuration/$projectName.dll'

dependencyDir can also be specified, and will define where downloaded dependencies get copied into

*Example configuration with all parameters:*
```groovy
xamarin {
    androidProject {
        projectFile '../Project.csproj'
        dependencyDir 'libs'
        configurations {
            CustomDebugTarget {
                build 'bin/CustomFolder/CustomAssembly.dll'
            }
            CustomReleaseTarget {
                build()
                androidPackage 'bin/CustomFolder/CustomApp.apk'
            }
            CustomOtherTarget {
                androidPackage()
            }
        }
    }
}
```

*Tasks:*
- xamarinBuild-CustomDebugTarget
- xamarinBuild-CustomReleaseTarget
- xamarinPackage-CustomReleaseTarget
- xamarinPackage-CustomOtherTarget



iOS Projects
--------------------------
A typical Xamarin iOS project will be configured like so:
```groovy
xamarin {
    iOSProject {
        projectName 'Project'
        solutionFile 'Solution.sln'
        dependencyDir 'libs'
        configurations {
            Debug {
                build()
            }
            Release {
                build()
            }
        }
    }
}
```

*Tasks:*
- xamarinBuild-Debug
- xamarinBuild-Release
- xamarinPackage-Release

This is very similar to android projects, however instead of just project name, the solution file is required to be provided for mdtool to be able to build the project and its project dependencies.
There is no package target for iOS apps, as an ipa package is automatically built when specified under the build configuration in Xamarin Studio.

Using the publishing plugin
---------------------------
The publishing plugin leverages the maven-publish plugin, adding a maven publishing configuration for a specified configuration's build() target.
Under this maven configuration, the output dll will be added as an artifact, with artifactId equal to to the projectName by default, and other parameters (group, version) pulled from the project properties.
This will add the typical maven publishing tasks, as well as two more - xamarinPublishMaven and xamarinPublishMavenLocal.
These are convenience tasks which invoke the corresponding maven publish tasks, however also have a dependency on the build step of the selected configuration.

*Typical configuration:*
```groovy
xamarinPublish {
	mavenTask 'Release'
}
```

*Tasks:*
- Standard maven tasks
- xamarinPublishMaven
- xamarinPublishMavenLocal

In this example, the dll produced by the Release configuration of the specified project will be published.

*Custom artifactId:*
```groovy
xamarinPublish {
	mavenTask 'Release'
	artifactId 'CustomArtifact'
}
```

