package org.tm.archive.conversation.mutiselect.forward

import androidx.fragment.app.FragmentManager
import org.tm.archive.contacts.paged.ContactSearchConfiguration
import org.tm.archive.contacts.paged.ContactSearchState

/**
 * Allows a parent of MultiselectForwardFragment to provide a custom search page configuration.
 */
interface SearchConfigurationProvider {
  /**
   * @param fragmentManager    The child fragment manager of the MultiselectForwardFragment, to launch actions in to.
   * @param contactSearchState The search state, to build the configuration from.
   *
   * @return A configuration or null. Returning null will result in MultiselectForwardFragment using it's default configuration.
   */
  fun getSearchConfiguration(fragmentManager: FragmentManager, contactSearchState: ContactSearchState): ContactSearchConfiguration? = null
}
