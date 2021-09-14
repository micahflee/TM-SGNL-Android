package org.tm.archive;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.tm.archive.components.settings.DSLSettingsActivity;
import org.tm.archive.components.settings.app.AppSettingsActivity;
import org.tm.archive.conversation.ConversationIntents;
import org.tm.archive.conversationlist.ConversationListArchiveFragment;
import org.tm.archive.conversationlist.ConversationListFragment;
import org.tm.archive.groups.ui.creategroup.CreateGroupActivity;
import org.tm.archive.insights.InsightsLauncher;
import org.tm.archive.recipients.RecipientId;

public class MainNavigator {

  public static final int REQUEST_CONFIG_CHANGES = 901;

  private final MainActivity activity;

  public MainNavigator(@NonNull MainActivity activity) {
    this.activity = activity;
  }

  public static MainNavigator get(@NonNull Activity activity) {
    if (!(activity instanceof MainActivity)) {
      throw new IllegalArgumentException("Activity must be an instance of MainActivity!");
    }

    return ((MainActivity) activity).getNavigator();
  }

  public void onCreate(@Nullable Bundle savedInstanceState) {
    if (savedInstanceState != null) {
      return;
    }

    getFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, ConversationListFragment.newInstance())
                        .commit();
  }

  /**
   * @return True if the back pressed was handled in our own custom way, false if it should be given
   *         to the system to do the default behavior.
   */
  public boolean onBackPressed() {
    Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment_container);

    if (fragment instanceof BackHandler) {
      return ((BackHandler) fragment).onBackPressed();
    }

    return false;
  }

  public void goToConversation(@NonNull RecipientId recipientId, long threadId, int distributionType, int startingPosition) {
    Intent intent = ConversationIntents.createBuilder(activity, recipientId, threadId)
                                       .withDistributionType(distributionType)
                                       .withStartingPosition(startingPosition)
                                       .build();

    activity.startActivity(intent);
    activity.overridePendingTransition(R.anim.slide_from_end, R.anim.fade_scale_out);
  }

  public void goToAppSettings() {
    activity.startActivityForResult(AppSettingsActivity.home(activity), REQUEST_CONFIG_CHANGES);
  }

  public void goToArchiveList() {
    getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_from_end, R.anim.slide_to_start, R.anim.slide_from_start, R.anim.slide_to_end)
                        .replace(R.id.fragment_container, ConversationListArchiveFragment.newInstance())
                        .addToBackStack(null)
                        .commit();
  }

  public void goToGroupCreation() {
    activity.startActivity(CreateGroupActivity.newIntent(activity));
  }

  public void goToInvite() {
    Intent intent = new Intent(activity, InviteActivity.class);
    activity.startActivity(intent);
  }

  public void goToInsights() {
    InsightsLauncher.showInsightsDashboard(activity.getSupportFragmentManager());
  }

  private @NonNull FragmentManager getFragmentManager() {
    return activity.getSupportFragmentManager();
  }

  public interface BackHandler {
    /**
     * @return True if the back pressed was handled in our own custom way, false if it should be given
     *         to the system to do the default behavior.
     */
    boolean onBackPressed();
  }
}
