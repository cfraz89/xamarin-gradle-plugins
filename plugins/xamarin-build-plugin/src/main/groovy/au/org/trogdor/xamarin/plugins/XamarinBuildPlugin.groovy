package au.org.trogdor.xamarin.plugins

import au.org.trogdor.xamarin.lib.nuget.FetchNugetTask
import au.org.trogdor.xamarin.lib.xpkg.ComponentRestoreTask
import au.org.trogdor.xamarin.lib.nuget.NugetRestoreTask
import au.org.trogdor.xamarin.lib.XamarinConfiguration
import au.org.trogdor.xamarin.lib.DependencyFetchTask
import au.org.trogdor.xamarin.lib.XamarinProject
import au.org.trogdor.xamarin.lib.xpkg.FetchXpkgTask
import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ProjectDependency

class XamarinBuildPlugin implements Plugin<Project> {
    public static String EXTENSION_NAME = "xamarin"
    public static String CONFIG_BASE_NAME = "references"
    public static String CONFIG_ALL_NAME = "referencesMatched"
    public static String TASK_BUILD_ALL_NAME = "buildAll"
    public static String TASK_CLEAN_NAME = "clean"
    public static String TASK_INSTALL_DEPENDENCIES_NAME = "installDependencies"
    public static String TASK_RESTORE_NUGET_NAME = "restoreNugetPackages"
    public static String TASK_RESTORE_COMPONENTS_NAME = "restoreXamarinComponents"
    public static String TASK_FETCH_NUGET_NAME = "fetchNuget"
    public static String TASK_FETCH_XPKG_NAME = "fetchXpkg"
    public static String TASK_BUILD_NAME = "restore"
    public static String TASK_RESTORE_NAME = "restore"
    public static String TASK_TEST_NAME = "test"
    public static String TASK_GROUP = "Xamarin"
    public static String TASK_GROUP_SUPPORT = "Xamarin support"
    public static String TASK_GROUP_DEPENDENCIES = "Xamarin dependencies"

	void apply(Project project) {
        project.with {
            configurations.create("default")
            configurations.create(CONFIG_BASE_NAME)
            configurations.create(CONFIG_ALL_NAME)
		    extensions.create(EXTENSION_NAME, XamarinBuildExtension, project)
            dependencies.extensions.create(EXTENSION_NAME, XamarinDependenciesExtension, project)

            afterEvaluate {
                if (xamarin.solution) {
                    task(TASK_FETCH_NUGET_NAME, description: "Fetch the nuget tool", group: TASK_GROUP_SUPPORT, type: FetchNugetTask) {
                        paths = xamarin.paths
                    }
                    task(TASK_RESTORE_NUGET_NAME, description: "Restore nuget packages into project", group: TASK_GROUP_DEPENDENCIES, dependsOn: TASK_FETCH_NUGET_NAME, type: NugetRestoreTask) {
                        paths = xamarin.paths
                        solution = xamarin.solution
                    }
                    task(TASK_FETCH_XPKG_NAME, description: "Fetch the xpkg tool for restoring components", group: TASK_GROUP_SUPPORT, type: FetchXpkgTask) {
                        paths = xamarin.paths
                    }
                    task(TASK_RESTORE_COMPONENTS_NAME, description: "Restore Xamarin Components into project", group: TASK_GROUP_DEPENDENCIES, dependsOn: TASK_FETCH_XPKG_NAME, type: ComponentRestoreTask) {
                        paths = xamarin.paths
                        solution = xamarin.solution
                    }
                }
                if (xamarin.xamarinProject) {
                    task(TASK_BUILD_ALL_NAME, description: "Build all configurations", group: TASK_GROUP)
                    task(TASK_CLEAN_NAME, description: "Clean the Xamarin project", group: TASK_GROUP, type: xamarin.xamarinProject.cleanTask()) {
                        xamarinProject = xamarin.xamarinProject
                    }

                    if (xamarin.xamarinProject.configurations.empty)
                        createDefaultConfigurations(xamarin.xamarinProject)

                    xamarin.xamarinProject.configurations.each {
                        setupConfiguration(project, it)
                        setupTaskOrder(project, it)
                    }
                }
            }
        }
    }

    def setupConfiguration(Project project, XamarinConfiguration xConf) {
        def config = project.configurations.create(CONFIG_BASE_NAME+xConf.name) {
            extendsFrom project.configurations.getByName(CONFIG_BASE_NAME)
        }
        makeDependencyTasks(project, xConf)
        xConf.makeTasks()
        mapConfiguration(project, config, xConf.name)
    }

    def createDefaultConfigurations(XamarinProject xProj) {
        xProj.configurations {
            Debug
            Release
        }
    }

    def mapConfiguration(Project project, Configuration config, String configName) {
        project.configurations.findByName(CONFIG_ALL_NAME).dependencies.each { dep ->
            if (!(dep instanceof ProjectDependency)) {
                def mapping = configName.toLowerCase()
                config.dependencies << project.dependencies.create("${dep.group}:${dep.name}:${dep.version}:${mapping}@dll")
            } else {
                def depProject = dep.dependencyProject
                project.evaluationDependsOn(depProject.path)
                project.tasks.findByName("build${configName}")
                        .dependsOn(depProject.tasks.findByName(TASK_INSTALL_DEPENDENCIES_NAME+configName))
                mapConfiguration(depProject, config, configName)
            }
        }
    }

    def makeDependencyTasks(Project project, XamarinConfiguration xConf) {
        def config = project.configurations.findByName(CONFIG_BASE_NAME + xConf.name)
        def installDependencies = project.task("$TASK_INSTALL_DEPENDENCIES_NAME$xConf.name", description: "Copy dependency dlls into project", group: TASK_GROUP_DEPENDENCIES, type: DependencyFetchTask) {
            xamarinProject = project.xamarin.xamarinProject
            configuration = config
        }
        project.xamarin.xamarinProject.solutionProject.with {
            project.evaluationDependsOn(it.path)
            project.task("$TASK_RESTORE_NAME$xConf.name", description: "Restore all project dependencies for $xConf.name", group: TASK_GROUP_DEPENDENCIES, dependsOn: [installDependencies, tasks.findByName(TASK_RESTORE_NUGET_NAME), tasks.findByName(TASK_RESTORE_COMPONENTS_NAME)])
        }
    }

    def setupTaskOrder(Project project, XamarinConfiguration xConf) {
        project.with {
            def buildTask = tasks.findByName("$TASK_BUILD_NAME$xConf.name")
            def restoreTask = tasks.findByName("$TASK_RESTORE_NAME$xConf.name")
            def cleanTask = tasks.findByName("$TASK_CLEAN_NAME")

            buildTask.mustRunAfter cleanTask
            restoreTask.mustRunAfter cleanTask
        }
    }
}