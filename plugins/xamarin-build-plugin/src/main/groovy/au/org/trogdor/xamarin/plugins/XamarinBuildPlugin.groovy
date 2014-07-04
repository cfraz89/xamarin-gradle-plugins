package au.org.trogdor.xamarin.plugins

import au.org.trogdor.xamarin.lib.XamarinProject
import au.org.trogdor.xamarin.lib.XBuildProject
import au.org.trogdor.xamarin.lib.AndroidAppProject
import au.org.trogdor.xamarin.lib.AndroidLibraryProject
import au.org.trogdor.xamarin.lib.iOSAppProject
import au.org.trogdor.xamarin.lib.iOSLibraryProject
import au.org.trogdor.xamarin.lib.GenericLibraryProject
import au.org.trogdor.xamarin.lib.GenericAppProject
import au.org.trogdor.xamarin.lib.MDToolProject
import au.org.trogdor.xamarin.lib.DependencyFetchTask
import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.artifacts.ExternalModuleDependency

class XamarinBuildPlugin implements Plugin<Project> {
    public static String EXTENSION_NAME = "xamarin"
    public static String CONFIG_BASE_NAME = "xamarinCompile"
    public static String CONFIG_ALL_NAME = "xamarinCompileMapped"
    public static String TASK_BUILD_ALL_NAME = "xamarinBuildAll"
    public static String TASK_CLEAN_NAME = "xamarinClean"
    public static String TASK_FETCH_DEPENDENCIES_NAME = "fetchXamarinDependencies"
    public static String TASK_GROUP = "Xamarin"

	void apply(Project project) {
        project.with {
            configurations.create("default")
            def baseConfig = configurations.create(CONFIG_BASE_NAME)
            configurations.create(CONFIG_ALL_NAME)
		    extensions.create(EXTENSION_NAME, XamarinBuildExtension, project)

            afterEvaluate {
                if (xamarin.xamarinProject) {
                    task(TASK_BUILD_ALL_NAME, description: "Build all configurations", group: TASK_GROUP)
                    task(TASK_CLEAN_NAME, description: "Clean the Xamarin project", group: TASK_GROUP, type: xamarin.xamarinProject.cleanTask()) {
                        xamarinProject = xamarin.xamarinProject
                    }

                    xamarin.xamarinProject.configurations.each { xConf ->
                        def config = configurations.create(CONFIG_BASE_NAME+xConf.name) {
                            extendsFrom baseConfig
                        }
                        configurations.findByName(CONFIG_ALL_NAME).dependencies.each { dep->
                            def mapping = xConf.name.toLowerCase()
                            dependencies.add(config.name, "${dep.group}:${dep.name}:${dep.version}:${mapping}@dll")
                        }
                    }

                    addDependenciesToProject(project)

                    setupDependencyTasks(project)
                }
            }
        }
    }

    def addDependenciesToProject(Project project) {
        def xProj = project.xamarin.xamarinProject
        xProj.referencedProjects.each() { dependencyProjectName ->
            project.evaluationDependsOn(dependencyProjectName)

            def configSuffixes = [""] + xProj.configurations.collect {it.name}
            configSuffixes.each { configSuffix ->
                def configName = CONFIG_BASE_NAME + configSuffix
                def config = project.configurations.findByName(configName)
                def dependencyProject = project.findProject(dependencyProjectName)
                def dependentConfig = dependencyProject.configurations.findByName(configName)
                if (dependentConfig && config)
                    config.dependencies.addAll(dependentConfig.dependencies)
            }
        }
    }

    def setupDependencyTasks(Project project) {
        def xProj = project.xamarin.xamarinProject
        xProj.configurations.each { xConf ->
            def config = project.configurations.findByName(CONFIG_BASE_NAME + xConf.name)
            def taskName = TASK_FETCH_DEPENDENCIES_NAME + xConf.name
            project.task(taskName, description: "Copy dependency dlls into project", group: TASK_GROUP, type: DependencyFetchTask) {
                xamarinProject = xProj
                configuration = config
            }
            xConf.makeTasks()
        }
    }
}


class XamarinBuildExtension {
	private def Project project
	private def XamarinProject mXamarinProject

	private String mXBuildPath = "xbuild"
	private String mMDToolPath = "/Applications/Xamarin Studio.app/Contents/MacOS/mdtool"

	XamarinBuildExtension(Project prj) {
		project = prj
	}

	private def setProject(XamarinProject xprj, Closure closure) {
		if (this.mXamarinProject != null)
			throw new Exception("You may only define one Xamarin project per Gradle project!")

		project.configure(xprj, closure)
		this.mXamarinProject = xprj
	}

    XamarinProject getXamarinProject() {
        mXamarinProject
    }

    def xbuildPath(String xbuildPath) {
        mXBuildPath = xbuildPath
    }

    def getXbuildPath() {
        return mXBuildPath
    }

    def mdtoolPath(String mdtoolpath) {
        mMDToolPath = mdtoolpath
    }

    def getMdtoolPath() {
        return mMDToolPath
    }

	def androidAppProject(Closure closure) {
		setProject(new AndroidAppProject(project), closure)
	}

    def androidLibraryProject(Closure closure) {
        setProject(new AndroidLibraryProject(project), closure)
    }

	def iOSAppProject(Closure closure) {
		setProject(new iOSAppProject(project), closure)
	}

    def iOSLibraryProject(Closure closure) {
        setProject(new iOSLibraryProject(project), closure)
    }

    def genericLibraryProject(Closure closure) {
        setProject(new GenericLibraryProject(project), closure)
    }

	def genericAppProject(Closure closure) {
		setProject(new GenericAppProject(project), closure)
	}

	def xbuildProject(Closure closure) {
		setProject(new XBuildProject(project), closure)
	}

	def mdtoolProject(Closure closure) {
		setProject(new MDToolProject(project), closure)
	}
}
