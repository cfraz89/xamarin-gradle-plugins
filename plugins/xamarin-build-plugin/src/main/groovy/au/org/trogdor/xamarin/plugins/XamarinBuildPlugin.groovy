package au.org.trogdor.xamarin.plugins

import au.org.trogdor.xamarin.lib.XamarinConfiguration
import au.org.trogdor.xamarin.lib.DependencyFetchTask
import au.org.trogdor.xamarin.lib.XamarinProject
import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.tasks.Copy

class XamarinBuildPlugin implements Plugin<Project> {
    public static String EXTENSION_NAME = "xamarin"
    public static String CONFIG_BASE_NAME = "references"
    public static String CONFIG_ALL_NAME = "referencesMatched"
    public static String TASK_BUILD_ALL_NAME = "buildAll"
    public static String TASK_CLEAN_NAME = "clean"
    public static String TASK_INSTALL_DEPENDENCIES_NAME = "installDependencies"
    public static String TASK_GROUP = "Xamarin"

	void apply(Project project) {
        project.with {
            configurations.create("default")
            def baseConfig = configurations.create(CONFIG_BASE_NAME)
            configurations.create(CONFIG_ALL_NAME)
		    extensions.create(EXTENSION_NAME, XamarinBuildExtension, project)
            dependencies.extensions.create(EXTENSION_NAME, XamarinDependenciesExtension, project)

            afterEvaluate {
                if (xamarin.xamarinProject) {
                    task(TASK_BUILD_ALL_NAME, description: "Build all configurations", group: TASK_GROUP)
                    task(TASK_CLEAN_NAME, description: "Clean the Xamarin project", group: TASK_GROUP, type: xamarin.xamarinProject.cleanTask()) {
                        xamarinProject = xamarin.xamarinProject
                    }

                    if (xamarin.xamarinProject.configurations.empty)
                        createDefaultConfigurations(xamarin.xamarinProject)

                    xamarin.xamarinProject.configurations.each { xConf ->
                        def config = configurations.create(CONFIG_BASE_NAME+xConf.name) {
                            extendsFrom baseConfig
                        }
                        makeDependencyTask(project, xConf)
                        xConf.makeTasks()
                        mapConfiguration(project, config, xConf.name)
                    }
                }
            }
        }
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

    def makeDependencyTask(Project project, XamarinConfiguration xConf) {
        def config = project.configurations.findByName(CONFIG_BASE_NAME + xConf.name)
        def taskName = TASK_INSTALL_DEPENDENCIES_NAME + xConf.name
        def depDir = project.xamarin.xamarinProject.dependencyDir
        project.task(taskName, description: "Copy dependency dlls into project", group: TASK_GROUP, type: DependencyFetchTask) {
            xamarinProject = project.xamarin.xamarinProject
            configuration = config
        }
    }
}