package com.isyara.app.util

import org.junit.Assert.assertTrue
import org.junit.Test

class FuzzyMatchUtilsTest {

    @Test
    fun `missing vowels still keeps similarity above practice threshold`() {
        val score = FuzzyMatchUtils.calculateSimilarity("buku", "bku")

        assertTrue(score >= 0.4)
    }

    @Test
    fun `dropped first consonant is still tolerated`() {
        val score = FuzzyMatchUtils.calculateSimilarity("buku", "uku")

        assertTrue(score >= 0.4)
    }

    @Test
    fun `single character partial is not treated as strong match`() {
        val score = FuzzyMatchUtils.calculateSimilarity("ayam", "a")

        assertTrue(score < 0.4)
    }
}
