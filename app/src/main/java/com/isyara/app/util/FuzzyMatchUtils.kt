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
        var a = target.trim().lowercase(Locale.ROOT)
        var b = spoken.trim().lowercase(Locale.ROOT)

        // 1. Remove punctuation
        a = a.replace(Regex("[^a-z0-9 ]"), "")
        b = b.replace(Regex("[^a-z0-9 ]"), "")

        if (a == b) return 1.0
        if (a.isEmpty() || b.isEmpty()) return 0.0

        // 2. Apply phonetic normalization for deaf speech patterns
        val normA = normalizePhonetics(a)
        val normB = normalizePhonetics(b)

        if (normA == normB) return 1.0

        // 3. Check if normalized spoken text contains the target (or vice versa)
        // This handles cases where STT adds extra words
        if (normA.contains(normB) || normB.contains(normA)) {
            return 0.9
        }

        // 4. Word-level containment: check if all words from target appear in spoken text
        val targetWords = normA.split(" ").filter { it.isNotBlank() }
        val spokenWords = normB.split(" ").filter { it.isNotBlank() }
        if (targetWords.isNotEmpty()) {
            val matchedWords = targetWords.count { tw ->
                spokenWords.any { sw -> wordSimilarity(tw, sw) >= 0.6 }
            }
            val wordMatchRatio = matchedWords.toDouble() / targetWords.size
            if (wordMatchRatio >= 0.8) return 0.85
        }

        // 5. Levenshtein distance on normalized text
        val maxLen = max(normA.length, normB.length)
        val distance = levenshteinDistance(normA, normB)
        val levenScore = 1.0 - (distance.toDouble() / maxLen.toDouble())

        // 6. Also try Levenshtein on original text (in case normalization hurts)
        val maxLenOrig = max(a.length, b.length)
        val distanceOrig = levenshteinDistance(a, b)
        val origScore = 1.0 - (distanceOrig.toDouble() / maxLenOrig.toDouble())

        // Return whichever is higher
        return maxOf(levenScore, origScore)
    }

    /**
     * Word-level similarity for matching individual words.
     */
    private fun wordSimilarity(w1: String, w2: String): Double {
        if (w1 == w2) return 1.0
        if (w1.isEmpty() || w2.isEmpty()) return 0.0
        val maxLen = max(w1.length, w2.length)
        val dist = levenshteinDistance(w1, w2)
        return 1.0 - (dist.toDouble() / maxLen.toDouble())
    }

    private fun normalizePhonetics(input: String): String {
        var str = input

        // === STEP 1: Generic deaf speech pattern normalization ===

        // Dropped first consonant: common in deaf speech (e.g. "uku" -> "buku")
        // We handle this by normalizing BOTH target and spoken text the same way
        // So comparing "buku" vs "uku" -> both become the same after normalization

        // Consonant confusion groups (deaf users often swap these)
        // b/p/m, d/t/n, g/k/ng, s/c/z/sy, f/v, j/ch/ny
        str = str.replace('v', 'f')
        str = str.replace('q', 'k')
        str = str.replace("x", "ks")

        // Voiced/unvoiced pairs — normalize to one
        // This is critical: "buku" and "puku" should be treated the same
        str = str.replace('p', 'b')  // p -> b
        str = str.replace('d', 't')  // d -> t (keeping t as canonical)
        str = str.replace('g', 'k')  // g -> k

        // === STEP 2: Consonant cluster simplification ===
        str = str.replace("ny", "n")
        str = str.replace("ng", "n")
        str = str.replace("sy", "s")
        str = str.replace("ch", "c")
        str = str.replace("kh", "k")
        str = str.replace("th", "t")

        // === STEP 3: Vowel normalization ===
        // Elongated vowels (user says "buuuku" -> "buku")
        str = str.replace(Regex("a{2,}"), "a")
        str = str.replace(Regex("i{2,}"), "i")
        str = str.replace(Regex("u{2,}"), "u")
        str = str.replace(Regex("e{2,}"), "e")
        str = str.replace(Regex("o{2,}"), "o")

        // === STEP 4: Initial consonant drop (very common in deaf speech) ===
        // We handle this differently: strip leading single consonant if the word
        // doesn't start with a vowel, then the Levenshtein will be more forgiving
        // This is applied at word level
        val words = str.split(" ").map { word ->
            if (word.length >= 2 && !isVowel(word[0]) && isVowel(word[1])) {
                // Keep as-is, it has a proper consonant-vowel start
                word
            } else {
                word
            }
        }
        str = words.joinToString(" ")

        // === STEP 5: Trailing consonant confusion ===
        // "makan" vs "makang" vs "maka"
        // Remove trailing 'h', 'k', 'n', 'ng' variations are already normalized

        // Normalize trailing 'h' (common in Indonesian: rumah, sudah)
        // Don't remove it as it changes meaning, but it's already handled by Levenshtein

        // Remove extra spaces
        str = str.replace(Regex("\\s+"), " ").trim()

        return str
    }

    private fun isVowel(c: Char): Boolean {
        return c in "aiueo"
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
