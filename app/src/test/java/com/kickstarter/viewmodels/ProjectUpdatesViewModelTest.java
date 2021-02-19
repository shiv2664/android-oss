package com.kickstarter.viewmodels;

import android.content.Intent;
import android.util.Pair;

import com.kickstarter.KSRobolectricTestCase;
import com.kickstarter.libs.Environment;
import com.kickstarter.libs.utils.EventName;
import com.kickstarter.mock.factories.ProjectDataFactory;
import com.kickstarter.mock.factories.ProjectFactory;
import com.kickstarter.mock.factories.UpdateFactory;
import com.kickstarter.mock.services.MockApiClient;
import com.kickstarter.models.Project;
import com.kickstarter.models.Update;
import com.kickstarter.services.apiresponses.UpdatesEnvelope;
import com.kickstarter.ui.IntentKey;
import com.kickstarter.ui.data.ProjectData;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import rx.Observable;
import rx.observers.TestSubscriber;

public class ProjectUpdatesViewModelTest extends KSRobolectricTestCase {
  private ProjectUpdatesViewModel.ViewModel vm;
  private final TestSubscriber<Boolean> horizontalProgressBarIsGone = new TestSubscriber<>();
  private final TestSubscriber<Boolean> isFetchingUpdates = new TestSubscriber<>();
  private final TestSubscriber<Pair<Project, List<Update>>> projectAndUpdates = new TestSubscriber<>();
  private final TestSubscriber<Pair<Project, Update>> startUpdateActivity = new TestSubscriber<>();

  private void setUpEnvironment(final @NonNull Environment env, final @NonNull Project project, final @NonNull ProjectData projectData) {
    this.vm = new ProjectUpdatesViewModel.ViewModel(env);
    this.vm.outputs.horizontalProgressBarIsGone().subscribe(this.horizontalProgressBarIsGone);
    this.vm.outputs.isFetchingUpdates().subscribe(this.isFetchingUpdates);
    this.vm.outputs.projectAndUpdates().subscribe(this.projectAndUpdates);
    this.vm.outputs.startUpdateActivity().subscribe(this.startUpdateActivity);

    // Configure the view model with a project intent.
    this.vm.intent(new Intent().putExtra(IntentKey.PROJECT, project).putExtra(IntentKey.PROJECT_DATA, projectData));
  }

  @Test
  public void init_whenViewModelInstantiated_shouldTrackPageViewEvent() {
    Project project = ProjectFactory.project();
    setUpEnvironment(environment(), project, ProjectDataFactory.Companion.project(project));

    this.lakeTest.assertValue(EventName.PAGE_VIEWED.getEventName());
  }

  @Test
  public void testHorizontalProgressBarIsGone() {
    Project project = ProjectFactory.project();
    setUpEnvironment(environment(), project, ProjectDataFactory.Companion.project(project));

    this.horizontalProgressBarIsGone.assertValues(false, true);
  }

  @Test
  public void testIsFetchingUpdates() {
    Project project = ProjectFactory.project();
    setUpEnvironment(environment(), project, ProjectDataFactory.Companion.project(project));

    this.isFetchingUpdates.assertValues(true, false);
  }

  @Test
  public void testProjectAndUpdates() {
    final List<Update> updates = Arrays.asList(
      UpdateFactory.update(),
      UpdateFactory.update()
    );

    final Project project = ProjectFactory.project();
    setUpEnvironment(environment().toBuilder().apiClient(new MockApiClient() {
      @NonNull
      @Override
      public Observable<UpdatesEnvelope> fetchUpdates(final @NonNull Project project) {
        return Observable.just(
          UpdatesEnvelope
            .builder()
            .updates(updates)
            .urls(urlsEnvelope())
            .build()
        );
      }
    }).build(), project, ProjectDataFactory.Companion.project(project));

    this.projectAndUpdates.assertValues(Pair.create(project, updates));
  }

  @Test
  public void test_projectAndUpdates_whenUpdatesListIsEmpty() {
    final Project project = ProjectFactory.project();
    setUpEnvironment(environment().toBuilder().apiClient(new MockApiClient() {
      @NonNull
      @Override
      public Observable<UpdatesEnvelope> fetchUpdates(final @NonNull Project project) {
        return Observable.just(
                UpdatesEnvelope
                        .builder()
                        .updates(Collections.emptyList())
                        .urls(urlsEnvelope())
                        .build()
        );
      }
    }).build(), project, ProjectDataFactory.Companion.project(project));

    this.projectAndUpdates.assertValues(Pair.create(project, Collections.emptyList()));
    this.isFetchingUpdates.assertValues(true, false);
    this.horizontalProgressBarIsGone.assertValues(false, true);
  }

  @Test
  public void testStartUpdateActivity() {
    final Update update = UpdateFactory.update();
    final List<Update> updates = Collections.singletonList(update);

    final Project project = ProjectFactory.project();
    setUpEnvironment(environment().toBuilder().apiClient(new MockApiClient() {
      @NonNull
      @Override
      public Observable<UpdatesEnvelope> fetchUpdates(final @NonNull Project project) {
        return Observable.just(
          UpdatesEnvelope
            .builder()
            .updates(updates)
            .urls(urlsEnvelope())
            .build()
        );
      }
    }).build(), project, ProjectDataFactory.Companion.project(project));

    this.vm.inputs.updateClicked(update);

    this.startUpdateActivity.assertValues(Pair.create(project, update));
  }

  private UpdatesEnvelope.UrlsEnvelope urlsEnvelope() {
    return UpdatesEnvelope.UrlsEnvelope
      .builder()
      .api(
        UpdatesEnvelope.UrlsEnvelope.ApiEnvelope
          .builder()
          .moreUpdates("http://more.updates.please")
          .build()
      )
      .build();
  }
}
