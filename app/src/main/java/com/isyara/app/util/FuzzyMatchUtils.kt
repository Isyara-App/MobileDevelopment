package com.isyara.app.util

import java.util.Locale
import kotlin.math.max

object FuzzyMatchUtils {

    /**
     * Calculates the similarity between two strings using a combination of:
     * 1. Phonetic normalization for deaf/hard-of-hearing users
     * 2. Substring containment check
     * 3. Levenshtein Distance
     *
     * Returns a percentage from 0.0 to 1.0 (where 1.0 is exact match).
     */
    fun calculateSimilarity(target: String, spoken: String): Double {
        val a = sanitize(target)
        val b = sanitize(spoken)

        if (a == b) return 1.0
        if (a.isEmpty() || b.isEmpty()) return 0.0

        // 1. Apply phonetic normalization for deaf speech patterns
        val normA = normalizePhonetics(a)
        val normB = normalizePhonetics(b)

        if (normA == normB) return 1.0

        // 2. Safer containment check to avoid tiny partials like "a" counting as a full match.
        val containScore = containmentScore(normA, normB)

        // 3. Word-level matching with leniency for missing vowels / dropped first consonants.
        val targetWords = normA.split(" ").filter { it.isNotBlank() }
        val spokenWords = normB.split(" ").filter { it.isNotBlank() }
        var wordScore = 0.0
        if (targetWords.isNotEmpty() && spokenWords.isNotEmpty()) {
            val bestWordScores = targetWords.map { tw ->
                spokenWords.maxOf { sw -> wordSimilarity(tw, sw) }
            }
            val matchedWords = bestWordScores.count { it >= 0.65 }
            val wordMatchRatio = matchedWords.toDouble() / targetWords.size
            wordScore = when {
                wordMatchRatio >= 0.8 -> max(bestWordScores.average(), 0.85)
                wordMatchRatio >= 0.5 -> bestWordScores.average() * 0.9
                else -> bestWordScores.average() * 0.75
            }
        }

        // 4. Compare consonant skeletons to survive whisper-like recognition
        val skeletonScore = consonantSkeletonScore(normA, normB)
        val subsequenceScore = orderedSubsequenceScore(normA, normB)

        // 5. Levenshtein distance on normalized text
        val levenScore = levenshteinScore(normA, normB)

        // 6. Also try Levenshtein on original text (in case normalization hurts)
        val origScore = levenshteinScore(a, b)

        // 7. Bigram overlap — helps short words where Levenshtein is too strict
        val bigramScore = bigramSimilarity(normA, normB)

        return maxOf(containScore, wordScore, skeletonScore, subsequenceScore, levenScore, origScore, bigramScore)
    }

    /**
     * Word-level similarity for matching individual words.
     */
    private fun wordSimilarity(w1: String, w2: String): Double {
        if (w1 == w2) return 1.0
        if (w1.isEmpty() || w2.isEmpty()) return 0.0

        var bestScore = levenshteinScore(w1, w2)

        val droppedA = dropLeadingConsonant(w1)
        val droppedB = dropLeadingConsonant(w2)
        if (droppedA == w2 || droppedB == w1 || droppedA == droppedB) {
            bestScore = max(bestScore, 0.86)
        }

        val skeleton1 = consonantSkeleton(w1)
        val skeleton2 = consonantSkeleton(w2)
        if (skeleton1.isNotEmpty() && skeleton2.isNotEmpty()) {
            bestScore = max(bestScore, consonantSkeletonScore(skeleton1, skeleton2))
        }

        val containScore = containmentScore(w1, w2)
        if (containScore > 0.0) {
            bestScore = max(bestScore, containScore)
        }

        val subsequenceScore = orderedSubsequenceScore(w1, w2)
        if (subsequenceScore > 0.0) {
            bestScore = max(bestScore, subsequenceScore)
        }

        return bestScore
    }

    private fun sanitize(input: String): String {
        return input.trim()
            .lowercase(Locale.ROOT)
            .replace(Regex("[^a-z0-9 ]"), "")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private fun containmentScore(a: String, b: String): Double {
        val shorter = if (a.length <= b.length) a else b
        val longer = if (a.length <= b.length) b else a
        // Lowered from 3 to 2 to handle very short Indonesian words (e.g. "ibu", "api")
        if (shorter.length < 2 || !longer.contains(shorter)) return 0.0

        val coverage = shorter.length.toDouble() / longer.length.toDouble()
        return when {
            coverage >= 0.9 -> 0.96
            coverage >= 0.75 -> 0.9
            coverage >= 0.6 -> 0.82
            else -> 0.0
        }
    }

    private fun orderedSubsequenceScore(a: String, b: String): Double {
        val shorter = if (a.length <= b.length) a else b
        val longer = if (a.length <= b.length) b else a
        if (shorter.length < 2 || !isSubsequence(shorter, longer)) return 0.0

        val coverage = shorter.length.toDouble() / longer.length.toDouble()
        return when {
            coverage >= 0.85 -> 0.88
            coverage >= 0.7 -> 0.78
            else -> 0.65
        }
    }

    private fun consonantSkeletonScore(a: String, b: String): Double {
        val skeletonA = consonantSkeleton(a)
        val skeletonB = consonantSkeleton(b)
        if (skeletonA.isEmpty() || skeletonB.isEmpty()) return 0.0
        if (skeletonA == skeletonB) {
            return if (minOf(skeletonA.length, skeletonB.length) >= 2) 0.9 else 0.72
        }

        var bestScore = levenshteinScore(skeletonA, skeletonB)
        val containScore = containmentScore(skeletonA, skeletonB)
        if (containScore > 0.0) {
            bestScore = max(bestScore, containScore)
        }
        return bestScore
    }

    private fun consonantSkeleton(input: String): String {
        return input.filterNot { it == ' ' || isVowel(it) }
    }

    private fun dropLeadingConsonant(word: String): String {
        return if (word.length > 2 && !isVowel(word.first())) {
            word.drop(1)
        } else {
            word
        }
    }

    private fun isSubsequence(shorter: String, longer: String): Boolean {
        var index = 0
        for (char in longer) {
            if (index < shorter.length && char == shorter[index]) {
                index++
            }
        }
        return index == shorter.length
    }

    private fun normalizePhonetics(input: String): String {
        var str = input

        // === STEP 1: Generic deaf speech pattern normalization ===
        str = str.replace('v', 'f')
        str = str.replace('q', 'k')
        str = str.replace("x", "ks")

        // Voiced/unvoiced pairs — normalize to canonical form
        str = str.replace('p', 'b')  // p -> b
        str = str.replace('d', 't')  // d -> t
        str = str.replace('g', 'k')  // g -> k
        str = str.replace('z', 'j')  // z -> j (common swap)
        str = str.replace('m', 'n')  // m/n are often confused in deaf speech

        // === STEP 2: Consonant cluster simplification ===
        str = str.replace("ny", "n")
        str = str.replace("ng", "n")
        str = str.replace("sy", "s")
        str = str.replace("ch", "c")
        str = str.replace("kh", "k")
        str = str.replace("th", "t")

        // === STEP 3: Vowel normalization ===
        str = str.replace(Regex("a{2,}"), "a")
        str = str.replace(Regex("i{2,}"), "i")
        str = str.replace(Regex("u{2,}"), "u")
        str = str.replace(Regex("e{2,}"), "e")
        str = str.replace(Regex("o{2,}"), "o")

        // === STEP 4: Strip trailing nasal/stop that deaf speakers often add or drop ===
        // e.g. "makann" -> "makan", "bukuu" already handled above
        str = str.replace(Regex("([bcdfhjklnrstw])\\1+"), "$1") // collapse repeated consonants

        str = str.replace(Regex("\\s+"), " ").trim()
        return str
    }

    private fun isVowel(c: Char): Boolean {
        return c in "aiueo"
    }

    /**
     * Bigram (character 2-gram) overlap score.
     * Particularly useful for short words where edit distance is too harsh.
     */
    private fun bigramSimilarity(a: String, b: String): Double {
        if (a.length < 2 || b.length < 2) return 0.0
        fun bigrams(s: String) = (0 until s.length - 1).map { s.substring(it, it + 2) }
        val bigramsA = bigrams(a)
        val bigramsB = bigrams(b).toMutableList()
        var matches = 0
        for (bg in bigramsA) {
            val idx = bigramsB.indexOf(bg)
            if (idx >= 0) {
                matches++
                bigramsB.removeAt(idx)
            }
        }
        return (2.0 * matches) / (bigramsA.size + bigrams(b).size).toDouble()
    }

    private fun levenshteinScore(a: String, b: String): Double {
        val maxLen = max(a.length, b.length)
        if (maxLen == 0) return 1.0
        val dist = levenshteinDistance(a, b)
        return 1.0 - (dist.toDouble() / maxLen.toDouble())
    }

    private fun levenshteinDistance(a: String, b: String): Int {
        val dp = Array(a.length + 1) { IntArray(b.length + 1) }

        for (i in 0..a.length) {
            for (j in 0..b.length) {
                if (i == 0) {
                    dp[i][j] = j
                } else if (j == 0) {
                    dp[i][j] = i
                } else {
                    val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                    dp[i][j] = minOf(
                        dp[i - 1][j] + 1, // deletion
                        dp[i][j - 1] + 1, // insertion
                        dp[i - 1][j - 1] + cost // substitution
                    )
                }
            }
        }
        return dp[a.length][b.length]
    }
}
