package au.org.trogdor.xamarin.plugins

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler

/**
 * Created by chrisfraser on 7/07/2014.
 */
class XamarinDependenciesExtension {
    private Project mProject;
    private List<String> mReferences;

    XamarinDependenciesExtension(Project project) {
        mProject = project
        mReferences = []
    }

    def referenceProject(String projectName) {
        mProject.with {
            def baseConfig = configurations.findByName(XamarinBuildPlugin.CONFIG_BASE_NAME)
            def mappedConfig = configurations.findByName(XamarinBuildPlugin.CONFIG_ALL_NAME)
            baseConfig.dependencies << dependencies.project(path: projectName, configuration: XamarinBuildPlugin.CONFIG_BASE_NAME)
            mappedConfig.dependencies << dependencies.project(path: projectName, configuration: XamarinBuildPlugin.CONFIG_ALL_NAME)
        }
        mReferences.add(projectName)
    }

    def getReferences() {
        mReferences
    }
}
