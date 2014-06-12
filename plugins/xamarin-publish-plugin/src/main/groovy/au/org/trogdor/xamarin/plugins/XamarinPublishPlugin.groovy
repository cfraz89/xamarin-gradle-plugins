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

    void mavenTask(String conf) {

        project.configure(project) {
            apply plugin: 'maven-publish'
        }

        XamarinProject xamarinProject = project.xamarin.xamarinProject
        def resolvedArtifactId = project.xamarinPublish.artifactId ?: xamarinProject.projectName

        MavenPublication publication = project.publishing.publications.create('xamarinComponent', MavenPublication)
        xamarinProject.configurations.all() {configuration->
            def classifierName = configuration.name.toLowerCase()
            if (project.file(configuration.buildOutput).exists())
                publication.artifact(buildOutput) {
                    extension "dll"
                    classifier classifierName
                }

            def symbolsPath = configuration.buildOutput + ".mdb"
            if (project.file(symbolsPath).exists())
                publication.artifact(symbolsPath) {
                    extension "dll.mdb"
                    classifier "$classifierName-symbols"
                }
        }

//
//            def debugSymbolsPath = configurations.getByName(configuration).buildOutput + ".mdb"
//            def debugFile = project.file(debugSymbolsPath)
//            if (debugFile.exists()) {
//                project.publishing {
//                    publications {
//                        xamarinComponent(MavenPublication) {
//                            artifactId resolvedArtifactId
//                            artifact(buildOutput) {
//                                extension "dll"
//                            }
//                            artifact(debugSymbolsPath) {
//                                classifier "debug-symbols"
//                                extension "dll.mdb"
//                            }
//                        }
//                    }
//                }
//            } else {
//                project.publishing {
//                    publications {
//                        xamarinComponent(MavenPublication) {
//                            artifactId resolvedArtifactId
//                            artifact(buildOutput) {
//                                extension "dll"
//                            }
//                        }
//                    }
//                }
//            }
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
