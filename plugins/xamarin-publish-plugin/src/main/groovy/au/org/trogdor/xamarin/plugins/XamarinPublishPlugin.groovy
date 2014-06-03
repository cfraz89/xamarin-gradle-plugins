package au.org.trogdor.xamarin.plugins

import au.org.trogdor.xamarin.lib.XamarinProject
import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.publish.maven.MavenPublication

class XamarinPublishPlugin implements Plugin<Project> {
	void apply(Project project) {
        project.extensions.create("xamarinPublish", XamarinPublishExtension, project)
    }
}

class XamarinPublishExtension {
    final def Project project

    XamarinPublishExtension(Project project) {
        this.project = project
    }

    void mavenTask(String configuration) {
        project.configure(project) {
            apply plugin: 'maven-publish'

            project.publishing {
                publications {
                    xamarinComponent(MavenPublication) {
                        XamarinProject xamarinProject = project.xamarin.xamarinProject
                        def configurations = xamarinProject.configurationContainer;
                        artifactId xamarinProject.projectName
                        artifact configurations.getByName(configuration).buildOutput
                    }
                }
            }
        }

        def taskName = configuration.replaceAll(~/\|/, "")
        def buildTaskName = "xamarinBuild-$taskName"

        project.task('xamarinPublishMavenLocal', description: "Publish the Xamarin component using configuration $configuration to the local Maven repository", group: 'Xamarin', dependsOn: [buildTaskName, 'publishToMavenLocal'])
        project.task('xamarinPublishMaven', description: "Publish the Xamarin component using configuration $configuration to Maven", group: 'Xamarin', dependsOn: [buildTaskName, 'publish'])
    }

    //void addMavenPublishTask() {

    //}
}
