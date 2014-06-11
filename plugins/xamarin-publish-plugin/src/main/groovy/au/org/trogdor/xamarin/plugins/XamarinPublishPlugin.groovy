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
    private def String mArtifactId
    private def String mRepository

    XamarinPublishExtension(Project project) {
        this.project = project
    }

    void artifactId(String artifactId) {
        mArtifactId = artifactId
    }

    String getArtifactId() {
        mArtifactId
    }

    void repository(String repo) {
        mRepository = repo
    }

    String getRepository() {
        mRepository
    }

    void mavenTask(String configuration) {

        project.configure(project) {
            XamarinProject xamarinProject = project.xamarin.xamarinProject
            def configurations = xamarinProject.configurationContainer
            def resolvedArtifactId = project.xamarinPublish.artifactId ?: xamarinProject.projectName

            apply plugin: 'maven-publish'

            project.publishing {
                publications {
                    xamarinComponent(MavenPublication) {
                        artifactId resolvedArtifactId
                        artifact(configurations.getByName(configuration).buildOutput) {
                            extension "dll"
                        }
                    }
                }
            }
        }
        /*
        def taskName = configuration.replaceAll(~/\|/, "")
        def buildTaskName = "xamarinBuild-$taskName"
        def buildTask = project.tasks.findByName(buildTaskName)

        project.tasks.findByName('publishToMavenLocal').mustRunAfter(buildTask)
        project.tasks.findByName('publish').mustRunAfter(buildTask)
        project.task('xamarinPublishMavenLocal', description: "Publish the Xamarin component using configuration $configuration to the local Maven repository", group: 'Xamarin', dependsOn: [buildTaskName, 'publishToMavenLocal'])
        project.task('xamarinPublishMaven', description: "Publish the Xamarin component using configuration $configuration to Maven", group: 'Xamarin', dependsOn: [buildTaskName, 'publish'])
        */
    }
}
