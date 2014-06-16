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

    void mavenPublish() {
        project.configure(project) {
            apply plugin: 'maven-publish'
        }

        XamarinProject xamarinProject = project.xamarin.xamarinProject
        def resolvedArtifactId = project.xamarinPublish.artifactId ?: xamarinProject.projectName

        MavenPublication publication = project.publishing.publications.create('xamarinComponent', MavenPublication)
        publication.artifactId = resolvedArtifactId
        xamarinProject.configurations.all() { configuration ->
            def classifierName = configuration.name.toLowerCase()
            if (project.file(configuration.resolvedBuildOutput).exists())
                publication.artifact(resolvedBuildOutput) {
                    extension "dll"
                    classifier classifierName
                }

            def symbolsPath = configuration.resolvedBuildOutput + ".mdb"
            if (project.file(symbolsPath).exists())
                publication.artifact(symbolsPath) {
                    extension "dll.mdb"
                    classifier "$classifierName-symbols"
                }
        }
    }
}
