package org.tm.archive.conversation.colors

import org.tm.archive.util.Projection

/**
 * Denotes that a class can be colorized. The class is responsible for
 * generating its own projection.
 */
interface Colorizable {
  val colorizerProjections: List<Projection>
}
