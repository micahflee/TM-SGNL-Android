package org.tm.archive.backup

import org.junit.Assert.assertEquals
import org.junit.Test

class FullBackupImporterTest {

  @Test
  fun `computeTableOrder - empty`() {
    val order = FullBackupImporter.computeTableDropOrder(mapOf())

    assertEquals(listOf<String>(), order)
  }

  /**
   * A B C
   */
  @Test
  fun `computeTableOrder - no dependencies`() {
    val order = FullBackupImporter.computeTableDropOrder(
      mapOf(
        "A" to setOf(),
        "B" to setOf(),
        "C" to setOf()
      )
    )

    assertEquals(listOf("A", "B", "C"), order)
  }

  /**
   * A
   * |
   * B
   * |
   * C
   */
  @Test
  fun `computeTableOrder - single chain`() {
    val order = FullBackupImporter.computeTableDropOrder(
      mapOf(
        "A" to setOf("B"),
        "B" to setOf("C")
      )
    )

    assertEquals(listOf("A", "B", "C"), order)
  }

  /**
   *   ┌──A──┐    B   C
   * ┌─D   ┌─E─┐
   * F     G   H
   */
  @Test
  fun `computeTableOrder - complex 1`() {
    val order = FullBackupImporter.computeTableDropOrder(
      mapOf(
        "A" to setOf("D", "E"),
        "B" to setOf(),
        "C" to setOf(),
        "D" to setOf("F"),
        "E" to setOf("G", "H"),
        "F" to setOf(),
        "G" to setOf(),
        "H" to setOf()
      )
    )

    assertEquals(listOf("A", "B", "C", "D", "E", "F", "G", "H"), order)
  }

  /**
   *   ┌────A────┐
   *   │    |    │
   * ┌─B─┐  C  ┌─D─┐
   * │   │  |  │   │
   * E   F  G  H   I
   */
  @Test
  fun `computeTableOrder - complex 2`() {
    val order = FullBackupImporter.computeTableDropOrder(
      mapOf(
        "A" to setOf("B", "C", "D"),
        "B" to setOf("E", "F"),
        "C" to setOf("G"),
        "D" to setOf("H", "I"),
        "E" to setOf(),
        "F" to setOf(),
        "G" to setOf(),
        "H" to setOf(),
        "I" to setOf()
      )
    )

    assertEquals(listOf("A", "B", "C", "D", "E", "F", "G", "H", "I"), order)
  }

  /**
   * ┌─A─┐  B  ┌─C─┐
   * │   │  |  │   │
   * D   E  F  G   H
   */
  @Test
  fun `computeTableOrder - multiple roots`() {
    val order = FullBackupImporter.computeTableDropOrder(
      mapOf(
        "A" to setOf("D", "E"),
        "B" to setOf("F"),
        "C" to setOf("G", "H"),
        "D" to setOf(),
        "E" to setOf(),
        "F" to setOf(),
        "G" to setOf(),
        "H" to setOf()
      )
    )

    assertEquals(listOf("A", "B", "C", "D", "E", "F", "G", "H"), order)
  }

  /**
   * ┌─A─┐  B  ┌─C─┐
   * │   │  |  │   │
   * D   E  D  D   E
   */
  @Test
  fun `computeTableOrder - multiple roots, dupes across graphs`() {
    val order = FullBackupImporter.computeTableDropOrder(
      mapOf(
        "A" to setOf("D", "E"),
        "B" to setOf("D"),
        "C" to setOf("D", "E"),
        "D" to setOf(),
        "E" to setOf()
      )
    )

    assertEquals(listOf("A", "B", "C", "D", "E"), order)
  }

  /**
   * A  B
   * │  │
   * D  C
   *    │
   *    D
   */
  @Test
  fun `computeTableOrder - multiple roots, new dependencies across roots`() {
    val order = FullBackupImporter.computeTableDropOrder(
      mapOf(
        "A" to setOf("D"),
        "B" to setOf("C"),
        "C" to setOf("D"),
        "D" to setOf()
      )
    )

    assertEquals(listOf("A", "B", "C", "D"), order)
  }
}
