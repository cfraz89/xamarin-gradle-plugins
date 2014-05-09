xamarin-gradle-plugins
======================

Plugins to build xamarin mobile apps using gradle.

The plugins can be installed into the local maven repository with "gradle install"

Android plugin properties
-------------------------
xbuildPath: Path to the xbuild command. Probably won't need to be changed.
projectFile: Path to the .csproj file for the project you want to build.
configurations: List of build configurations used in the Xamarin project. A build target will be added for each one. Defaults to Debug, Release.

iOS plugin properties
---------------------
mdtoolPath: Path to the mdtool command. Probably won't need to be changed.
projectName: Name of the project to compile
solutionFile: Path to the .sln file for the project
configurations: List of build configurations used in the Xamarin project. A build target will be added for each one. Defaults to Debug, Release.

Example usage (Android plugin)
------------------------------

	buildscript {
	    repositories {
	        mavenLocal()
	    }
	    dependencies {
	    	classpath 'au.org.trogdor:xamarin-android-plugin:0.1'
	    }
	}

	apply plugin: 'xamarin-android-plugin'

	xamarinAndroid {
		projectFile = 'Project.csproj'
	}

Example usage (iOS plugin)
--------------------------

	buildscript {
	    repositories {
	        mavenLocal()
	    }
	    dependencies {
	    	classpath 'au.org.trogdor:xamarin-ios-plugin:0.1'
	    }
	}

	apply plugin: 'xamarin-ios-plugin'

	xamariniOS {
		projectName = 'Project'
		solutionFile = 'Solution.sln'
	}
