package com.example.firebase

import android.content.Context
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class CloudSyncRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth,
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing cloud sync.")
    }

    fun observeUserProfile(userId: String): Flow<UserSyncProfile?> {
        val docRef = db.collection("users").document(userId)
        return docRef.snapshots()
            .map { snapshot ->
                snapshot.toObject(
                    UserSyncProfile::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val exceptionToLog = firestoreError ?: (error as? Exception)
                if (exceptionToLog != null && auth.currentUser?.uid == userId) {
                    handleFirestoreError(exceptionToLog, OperationType.GET, docRef.path)
                }
                throw error
            }
    }

    suspend fun upsertUserProfile(
        displayName: String,
        lastSurah: Int,
        lastAyah: Int,
        totalTasbihCount: Long,
        hifzAyahsCount: Int,
        preferredTheme: String,
        preferredLanguage: String,
    ): Result<Unit> {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        val cleanName = displayName.trim().ifBlank { "AL-QURAN Reader" }.take(80)
        return try {
            val existing = docRef.get().await()
            if (existing.exists()) {
                val updates = mapOf(
                    "displayName" to cleanName,
                    "lastSurah" to lastSurah.coerceIn(1, 114).toLong(),
                    "lastAyah" to lastAyah.coerceIn(1, 286).toLong(),
                    "totalTasbihCount" to totalTasbihCount.coerceAtLeast(0L),
                    "hifzAyahsCount" to hifzAyahsCount.coerceIn(0, 6236).toLong(),
                    "preferredTheme" to preferredTheme.take(40),
                    "preferredLanguage" to preferredLanguage.take(10),
                    "updatedAt" to FieldValue.serverTimestamp(),
                )
                docRef.update(updates).await()
            } else {
                val payload = mapOf(
                    "userId" to uid,
                    "displayName" to cleanName,
                    "lastSurah" to lastSurah.coerceIn(1, 114).toLong(),
                    "lastAyah" to lastAyah.coerceIn(1, 286).toLong(),
                    "totalTasbihCount" to totalTasbihCount.coerceAtLeast(0L),
                    "hifzAyahsCount" to hifzAyahsCount.coerceIn(0, 6236).toLong(),
                    "preferredTheme" to preferredTheme.take(40),
                    "preferredLanguage" to preferredLanguage.take(10),
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp(),
                )
                docRef.set(payload).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }

    fun observeBookmarks(userId: String): Flow<List<CloudQuranBookmark>> = flow {
        val path = "users/$userId/bookmarks"
        val collectionRef = db.collection("users").document(userId).collection("bookmarks")
        collectionRef.snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    CloudQuranBookmark::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val exceptionToLog = firestoreError ?: (error as? Exception)
                if (exceptionToLog != null && auth.currentUser?.uid == userId) {
                    handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                }
                throw error
            }
            .collect { emit(it) }
    }

    suspend fun saveBookmark(
        surahNumber: Int,
        ayahNumber: Int,
        surahName: String,
        note: String? = null,
    ): Result<String> {
        val uid = requireUserId()
        val docId = "s${surahNumber}_a${ayahNumber}"
        val docRef = db.collection("users").document(uid)
            .collection("bookmarks").document(docId)
        return try {
            val existing = docRef.get().await()
            val cleanNote = note?.trim()?.take(500)?.takeIf { it.isNotEmpty() }
            if (existing.exists()) {
                val updates = buildMap<String, Any?> {
                    put("surahNumber", surahNumber.coerceIn(1, 114).toLong())
                    put("ayahNumber", ayahNumber.coerceIn(1, 286).toLong())
                    put("surahName", surahName.trim().ifBlank { "Surah $surahNumber" }.take(80))
                    if (cleanNote != null) put("note", cleanNote)
                    put("updatedAt", FieldValue.serverTimestamp())
                }.filterValues { it != null } as Map<String, Any>
                docRef.update(updates).await()
            } else {
                val payload = buildMap<String, Any?> {
                    put("userId", uid)
                    put("surahNumber", surahNumber.coerceIn(1, 114).toLong())
                    put("ayahNumber", ayahNumber.coerceIn(1, 286).toLong())
                    put("surahName", surahName.trim().ifBlank { "Surah $surahNumber" }.take(80))
                    if (cleanNote != null) put("note", cleanNote)
                    put("createdAt", FieldValue.serverTimestamp())
                    put("updatedAt", FieldValue.serverTimestamp())
                }.filterValues { it != null } as Map<String, Any>
                docRef.set(payload).await()
            }
            Result.success(docId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun getBookmark(bookmarkId: String): Result<CloudQuranBookmark?> {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
            .collection("bookmarks").document(bookmarkId)
        return try {
            val snap = docRef.get().await()
            Result.success(
                snap.toObject(
                    CloudQuranBookmark::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun deleteBookmark(bookmarkId: String): Result<Unit> {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
            .collection("bookmarks").document(bookmarkId)
        return try {
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            Result.failure(e)
        }
    }

    fun observeHifzProgress(userId: String): Flow<List<CloudHifzProgress>> = flow {
        val path = "users/$userId/hifz_progress"
        db.collection("users").document(userId).collection("hifz_progress")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    CloudHifzProgress::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val exceptionToLog = firestoreError ?: (error as? Exception)
                if (exceptionToLog != null && auth.currentUser?.uid == userId) {
                    handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                }
                throw error
            }
            .collect { emit(it) }
    }

    suspend fun saveHifzProgress(
        surahNumber: Int,
        memorizedAyahs: Int,
        targetAyahs: Int,
        status: String,
    ): Result<Unit> {
        val uid = requireUserId()
        val docId = "surah_$surahNumber"
        val docRef = db.collection("users").document(uid)
            .collection("hifz_progress").document(docId)
        val safeTarget = targetAyahs.coerceIn(1, 286)
        val safeMemorized = memorizedAyahs.coerceIn(0, safeTarget)
        val safeStatus = if (status in listOf("in_progress", "memorized", "revision")) status else "in_progress"
        return try {
            val existing = docRef.get().await()
            if (existing.exists()) {
                docRef.update(
                    mapOf(
                        "memorizedAyahs" to safeMemorized.toLong(),
                        "targetAyahs" to safeTarget.toLong(),
                        "status" to safeStatus,
                        "updatedAt" to FieldValue.serverTimestamp(),
                    )
                ).await()
            } else {
                docRef.set(
                    mapOf(
                        "userId" to uid,
                        "surahNumber" to surahNumber.coerceIn(1, 114).toLong(),
                        "memorizedAyahs" to safeMemorized.toLong(),
                        "targetAyahs" to safeTarget.toLong(),
                        "status" to safeStatus,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp(),
                    )
                ).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }

    fun observeLearningProgress(userId: String): Flow<List<CloudLearningProgress>> = flow {
        val path = "users/$userId/learning_progress"
        db.collection("users").document(userId).collection("learning_progress")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    CloudLearningProgress::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val exceptionToLog = firestoreError ?: (error as? Exception)
                if (exceptionToLog != null && auth.currentUser?.uid == userId) {
                    handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                }
                throw error
            }
            .collect { emit(it) }
    }

    suspend fun saveLearningProgress(
        moduleType: String,
        lessonKey: String,
        completed: Boolean,
        quizScore: Int,
    ): Result<Unit> {
        val uid = requireUserId()
        val cleanModule = if (moduleType == "tajweed") "tajweed" else "arabic"
        val sanitizedKey = lessonKey.replace(Regex("[^a-zA-Z0-9_\\-]"), "_").take(80).ifBlank { "lesson_1" }
        val docId = "${cleanModule}_$sanitizedKey"
        val docRef = db.collection("users").document(uid)
            .collection("learning_progress").document(docId)
        return try {
            val existing = docRef.get().await()
            if (existing.exists()) {
                docRef.update(
                    mapOf(
                        "completed" to completed,
                        "quizScore" to quizScore.coerceIn(0, 100).toLong(),
                        "updatedAt" to FieldValue.serverTimestamp(),
                    )
                ).await()
            } else {
                docRef.set(
                    mapOf(
                        "userId" to uid,
                        "moduleType" to cleanModule,
                        "lessonKey" to sanitizedKey,
                        "completed" to completed,
                        "quizScore" to quizScore.coerceIn(0, 100).toLong(),
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp(),
                    )
                ).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }
}
