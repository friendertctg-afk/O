package com.example.firebase

import com.example.base.FirestoreEmulatorTestBase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CloudSyncRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun saveAndObserveBookmark_authenticatedOwner_succeeds() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = CloudSyncRepository(firestore, auth)

        val saveResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.saveBookmark(
                surahNumber = 1,
                ayahNumber = 1,
                surahName = "Al-Fatihah",
                note = "Opening prayer"
            )
        }
        assertTrue("Save bookmark should succeed: ${saveResult.exceptionOrNull()}", saveResult.isSuccess)
        val docId = saveResult.getOrThrow()

        val observed = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeBookmarks(uid).first { list -> list.any { it.id == docId } }
        }
        assertTrue(observed.any { it.surahNumber == 1L && it.ayahNumber == 1L })
    }

    @Test
    fun upsertUserProfile_andHifzAndLearning_authenticatedOwner_succeeds() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = CloudSyncRepository(firestore, auth)

        val profileResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.upsertUserProfile(
                displayName = "Amina",
                lastSurah = 2,
                lastAyah = 255,
                totalTasbihCount = 99L,
                hifzAyahsCount = 15,
                preferredTheme = "EMERALD_GOLD",
                preferredLanguage = "en"
            )
        }
        assertTrue("Profile upsert should succeed: ${profileResult.exceptionOrNull()}", profileResult.isSuccess)

        val hifzResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.saveHifzProgress(
                surahNumber = 112,
                memorizedAyahs = 4,
                targetAyahs = 4,
                status = "memorized"
            )
        }
        assertTrue("Hifz save should succeed: ${hifzResult.exceptionOrNull()}", hifzResult.isSuccess)

        val learningResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.saveLearningProgress(
                moduleType = "tajweed",
                lessonKey = "qalqalah",
                completed = true,
                quizScore = 100
            )
        }
        assertTrue("Learning save should succeed: ${learningResult.exceptionOrNull()}", learningResult.isSuccess)

        val hifzList = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeHifzProgress(uid).first { it.isNotEmpty() }
        }
        assertEquals(112L, hifzList.first().surahNumber)
    }

    @Test
    fun observeBookmarks_crossUserAccess_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val aliceRepo = CloudSyncRepository(firestore, auth)
        withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.saveBookmark(1, 2, "Al-Fatihah", null).getOrThrow()
        }

        signInTestUser(BOB_EMAIL)
        val bobRepo = CloudSyncRepository(firestore, auth)
        var failed = false
        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                bobRepo.observeBookmarks(aliceUid).first()
            }
        } catch (_: Exception) {
            failed = true
        }
        assertTrue("Cross-user bookmark observation must fail with PERMISSION_DENIED", failed)
    }

    @Test
    fun observeBookmarks_unauthenticated_failsWithPermissionDenied() = runBlocking {
        auth.signOut()
        val repo = CloudSyncRepository(firestore, auth)
        var failed = false
        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repo.observeBookmarks("some_uid_123").first()
            }
        } catch (_: Exception) {
            failed = true
        }
        assertTrue("Unauthenticated bookmark observation must fail", failed)
    }

    private companion object {
        const val ALICE_EMAIL = "alice_quran@test.com"
        const val BOB_EMAIL = "bob_quran@test.com"
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 4000L
    }
}
