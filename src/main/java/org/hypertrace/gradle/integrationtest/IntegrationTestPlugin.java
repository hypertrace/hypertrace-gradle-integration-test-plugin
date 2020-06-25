package org.hypertrace.gradle.integrationtest;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.testing.Test;
import org.gradle.language.base.plugins.LifecycleBasePlugin;

public class IntegrationTestPlugin implements Plugin<Project> {
  public static final String INTEGRATION_TEST_SOURCE_SET_NAME = "integrationTest";
  public static final String INTEGRATION_TEST_TASK_NAME = "integrationTest";

  @Override
  public void apply(Project target) {
    target
        .getPluginManager()
        .withPlugin(
            "java",
            appliedPlugin -> {
              SourceSet intTestSourceSet =
                  this.createExtendingSourceSet(
                      target, INTEGRATION_TEST_SOURCE_SET_NAME, SourceSet.MAIN_SOURCE_SET_NAME);
              this.registerIntegrationTestTask(
                  target, INTEGRATION_TEST_TASK_NAME, intTestSourceSet);
            });
  }

  private SourceSet createExtendingSourceSet(Project project, String name, String extendingFrom) {
    SourceSetContainer sourceSets = project.getExtensions().getByType(SourceSetContainer.class);
    SourceSet baseSourceSet = sourceSets.getByName(extendingFrom);
    SourceSet createdSourceSet = sourceSets.maybeCreate(name);
    createdSourceSet.setCompileClasspath(
        createdSourceSet.getCompileClasspath().plus(baseSourceSet.getOutput()));
    createdSourceSet.setRuntimeClasspath(
        createdSourceSet.getRuntimeClasspath().plus(baseSourceSet.getOutput()));
    updateConfigurationsToExtend(project, createdSourceSet, baseSourceSet);
    return createdSourceSet;
  }

  private void updateConfigurationsToExtend(
      Project project, SourceSet sourceSet, SourceSet extendsFromSourceSet) {
    project
        .getConfigurations()
        .named(sourceSet.getImplementationConfigurationName())
        .configure(
            configuration ->
                configuration.extendsFrom(
                    project
                        .getConfigurations()
                        .getByName(extendsFromSourceSet.getImplementationConfigurationName())));

    project
        .getConfigurations()
        .named(sourceSet.getRuntimeOnlyConfigurationName())
        .configure(
            configuration ->
                configuration.extendsFrom(
                    project
                        .getConfigurations()
                        .getByName(extendsFromSourceSet.getRuntimeOnlyConfigurationName())));
  }

  private void registerIntegrationTestTask(Project project, String name, SourceSet sourceSet) {
    project
        .getTasks()
        .register(
            name,
            Test.class,
            task -> {
              task.setGroup(LifecycleBasePlugin.VERIFICATION_GROUP);
              task.setDescription("Runs integration tests");
              task.shouldRunAfter(JavaPlugin.TEST_TASK_NAME);
              task.setClasspath(sourceSet.getRuntimeClasspath());
              task.setTestClassesDirs(sourceSet.getOutput().getClassesDirs());
            });
  }
}
