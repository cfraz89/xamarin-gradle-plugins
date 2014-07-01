package au.org.trogdor.xamarin.plugins

import au.org.trogdor.xamarin.lib.XamarinProject
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.publish.maven.MavenPublication

class XamarinPublishPlugin implements Plugin<Project> {
	void apply(Project project) {
        project.extensions.create("xamarinPublish", XamarinPublishExtension, project)
        project.plugins.apply('maven-publish')
        ((ProjectInternal)project).getConfigurationActions().add(new Action<ProjectInternal>() {
            @java.lang.Override
            void execute(ProjectInternal projectInternal) {
                XamarinProject xamarinProject = projectInternal.xamarin.xamarinProject
                def resolvedArtifactId = projectInternal.xamarinPublish.artifactId ?: xamarinProject.projectName

                MavenPublication publication = projectInternal.publishing.publications.create('xamarin', MavenPublication)
                publication.artifactId = resolvedArtifactId
                xamarinProject.configurations.all {configuration->
                    addArtifacts(configuration, publication, projectInternal)
                }
                projectInternal.tasks.publishToMavenLocal.dependsOn('xamarinBuildAll')
                projectInternal.tasks.publish.dependsOn('xamarinBuildAll')
            }

            private void addArtifacts(configuration, publication, projectInternal) {
                def classifierName = configuration.name.toLowerCase()
                def buildOutput = configuration.resolvedBuildOutput
                if (projectInternal.file(buildOutput).exists())
                    publication.artifact(buildOutput) {
                        extension "dll"
                        classifier classifierName
                    }

                def symbolsPath = buildOutput + ".mdb"
                if (projectInternal.file(symbolsPath).exists())
                    publication.artifact(symbolsPath) {
                        extension "dll.mdb"
                        classifier "$classifierName-symbols"
                    }
            }
        })
    }
}

class XamarinPublishExtension {
    final def Project project
    private def String mArtifactId

    XamarinPublishExtension(Project project) {
        this.project = project
    }

    void artifactId(String artifactId) {
        mArtifactId = artifactId
    }

    String getArtifactId() {
        mArtifactId
    }
}
