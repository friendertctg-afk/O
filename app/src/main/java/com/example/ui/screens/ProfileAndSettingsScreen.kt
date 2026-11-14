package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.BuildConfig
import com.example.data.local.BookmarkEntity
import com.example.data.local.HifzProgressEntity
import com.example.data.local.LessonProgressEntity
import com.example.data.local.UserPreferencesState
import com.example.firebase.onGoogleSignInClicked
import com.example.firebase.signOutFromFirebase
import com.example.localization.AppLanguage
import com.example.localization.stringsFor
import com.example.ui.components.BrandGuidelinesDialog
import com.example.ui.components.HidayahLogoMark
import com.example.ui.theme.IslamicThemePreset
import com.example.ui.viewmodel.CloudSyncViewModel
import com.example.utils.WallpaperAndAlertUtils
import com.google.firebase.auth.FirebaseUser

@Composable
fun ProfileAndSettingsScreen(
    prefs: UserPreferencesState,
    currentUser: FirebaseUser?,
    bookmarks: List<BookmarkEntity>,
    hifzList: List<HifzProgressEntity>,
    lessons: List<LessonProgressEntity>,
    onSelectTheme: (IslamicThemePreset) -> Unit,
    onSelectLanguage: (AppLanguage) -> Unit,
    onFeedbackSubmitted: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    val strings = stringsFor(prefs.appLanguage)

    var authError by remember { mutableStateOf<String?>(null) }
    var isSigningIn by remember { mutableStateOf(false) }
    var showBrandGuidelines by remember { mutableStateOf(false) }
    var showMembershipDialog by remember { mutableStateOf(false) }
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var legalDialogTitle by remember { mutableStateOf<String?>(null) }
    var feedbackToast by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 840.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Account & Cloud Sync Gate
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (currentUser == null) {
                            Text(strings.guestMode, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(
                                "You are using HIDAYAH offline-first. Sign in with Google anytime to back up and sync bookmarks, Hifz memorization progress, and Academy lessons across devices.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            authError?.let {
                                Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                            }
                            Button(
                                onClick = {
                                    isSigningIn = true
                                    authError = null
                                    onGoogleSignInClicked(
                                        context = context,
                                        credentialManager = credentialManager,
                                        onAuthSuccess = { isSigningIn = false },
                                        onAuthError = { err ->
                                            isSigningIn = false
                                            authError = err
                                        },
                                        scope = scope,
                                        onAuthCancelled = { isSigningIn = false }
                                    )
                                },
                                enabled = !isSigningIn,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("google_sign_in_button")
                            ) {
                                if (isSigningIn) {
                                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                } else {
                                    Text(strings.signInWithGoogle)
                                }
                            }
                        } else {
                            // Strictly instantiate CloudSyncViewModel ONLY when authenticated!
                            key(currentUser.uid) {
                                val syncVm: CloudSyncViewModel = viewModel()
                                val syncState by syncVm.state.collectAsStateWithLifecycle()

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Icon(Icons.Filled.CloudDone, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        Column {
                                            Text(currentUser.displayName ?: "HIDAYAH Member", fontWeight = FontWeight.Bold)
                                            Text(currentUser.email ?: "Signed in with Google", style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                    OutlinedButton(onClick = {
                                        signOutFromFirebase(credentialManager, scope)
                                    }) {
                                        Icon(Icons.Filled.Logout, contentDescription = "Sign Out", modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Sign Out")
                                    }
                                }

                                Text(
                                    "Cloud Records: ${syncState.cloudBookmarks.size} bookmarks • ${syncState.cloudHifz.size} Hifz Surahs • ${syncState.cloudLessons.size} Lessons",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                (syncState.statusMessage ?: syncState.errorMessage)?.let { msg ->
                                    Text(msg, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                }
                                Button(
                                    onClick = {
                                        syncVm.syncNow(
                                            displayName = currentUser.displayName ?: "HIDAYAH Member",
                                            prefs = prefs,
                                            localBookmarks = bookmarks,
                                            localHifz = hifzList,
                                            localLessons = lessons,
                                        )
                                    },
                                    enabled = !syncState.isSyncing,
                                    modifier = Modifier.fillMaxWidth().testTag("cloud_sync_now_button")
                                ) {
                                    Icon(Icons.Filled.CloudSync, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text(if (syncState.isSyncing) "Syncing..." else "Sync Local Progress to Cloud")
                                }
                            }
                        }
                    }
                }
            }

            // 2. 9 Languages Switcher
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("App Language (9 Languages • Live RTL)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AppLanguage.entries.forEach { lang ->
                                FilterChip(
                                    selected = prefs.appLanguage == lang,
                                    onClick = { onSelectLanguage(lang) },
                                    label = { Text("${lang.nativeName} (${lang.englishName})") }
                                )
                            }
                        }
                    }
                }
            }

            // 3. 8 Islamic Theme Presets
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Filled.Palette, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text("Islamic Theme Studio (8 Themes)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IslamicThemePreset.entries.forEach { preset ->
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { onSelectTheme(preset) },
                                    color = if (prefs.themePreset == preset) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(14.dp)
                                                .clip(RoundedCornerShape(7.dp))
                                                .background(preset.primary)
                                        )
                                        Text(preset.displayName, style = MaterialTheme.typography.labelLarge)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. Free & Premium Membership Overview
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showMembershipDialog = true },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Filled.WorkspacePremium, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        Column(modifier = Modifier.weight(1f)) {
                            Text("HIDAYAH Membership (Free Core Forever)", fontWeight = FontWeight.Bold)
                            Text("All essential Quran, Prayer, Qibla, Duas & Tasbih tools are 100% free. Tap to view Free vs Premium plan details.", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            // 5. About HIDAYAH, Founder & Support
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            HidayahLogoMark(size = 54.dp, darkSurfaceVariant = true)
                            Column {
                                Text("About HIDAYAH (هداية)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("Your Complete Islamic Companion", style = MaterialTheme.typography.bodySmall)
                            }
                        }

                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Founder & Owner: Mohammed Touhidul Islam", fontWeight = FontWeight.Bold)
                                Text("Son of: Sirajul Islam", style = MaterialTheme.typography.bodyMedium)
                                Text("WhatsApp: +966553182669", style = MaterialTheme.typography.bodySmall)
                                Text(
                                    "Founded and Owned by Mohammed Touhidul Islam, Son of Sirajul Islam.",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Button(
                            onClick = { WallpaperAndAlertUtils.openWhatsAppSupport(context) },
                            modifier = Modifier.fillMaxWidth().testTag("about_whatsapp_button")
                        ) {
                            Icon(Icons.Filled.Phone, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Contact Owner on WhatsApp (+966553182669)")
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { showBrandGuidelines = true }, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Filled.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Brand Logo")
                            }
                            OutlinedButton(onClick = { showFeedbackDialog = true }, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Filled.Feedback, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Feedback")
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(onClick = { legalDialogTitle = "Privacy Policy" }, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Filled.Policy, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Privacy Policy")
                            }
                            TextButton(onClick = { legalDialogTitle = "Terms & Conditions" }, modifier = Modifier.weight(1f)) {
                                Text("Terms & Conditions")
                            }
                        }

                        feedbackToast?.let {
                            Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }

                        Text(
                            "App Version ${BuildConfig.VERSION_NAME} (Build ${BuildConfig.VERSION_CODE})",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    if (showBrandGuidelines) {
        BrandGuidelinesDialog(onDismiss = { showBrandGuidelines = false })
    }

    if (showMembershipDialog) {
        AlertDialog(
            onDismissRequest = { showMembershipDialog = false },
            title = { Text("HIDAYAH Membership Plans") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Free Membership (Always Free)", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("• Complete 114 Surahs & 30 Juz Quran Reading\n• 20 Qaris Audio & Offline Downloads\n• Prayer Times, Qibla, Duas, Tasbih & Daily Tracker\n• Tajweed, Arabic & Kids Academy", style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(4.dp))
                    Text("Premium Membership", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                    Text("• Ad-free patron support for HIDAYAH development\n• Unlimited multi-device Cloud Sync & AI Tutor priority\n• Exclusive 4K Islamic Wallpaper packs & Study PDFs", style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = {
                TextButton(onClick = { showMembershipDialog = false }) { Text("Close") }
            }
        )
    }

    if (showFeedbackDialog) {
        var msg by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showFeedbackDialog = false },
            title = { Text("Send Feedback or Report a Problem") },
            text = {
                OutlinedTextField(
                    value = msg,
                    onValueChange = { msg = it },
                    label = { Text("Describe your suggestion or issue...") },
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (msg.isNotBlank()) {
                        onFeedbackSubmitted()
                        showFeedbackDialog = false
                        feedbackToast = "Thank you! Feedback recorded (#${prefs.submittedFeedbackCount + 1})."
                    }
                }) { Text("Submit") }
            },
            dismissButton = {
                TextButton(onClick = { showFeedbackDialog = false }) { Text("Cancel") }
            }
        )
    }

    legalDialogTitle?.let { title ->
        AlertDialog(
            onDismissRequest = { legalDialogTitle = null },
            title = { Text(title) },
            text = {
                Text(
                    if (title == "Privacy Policy") {
                        "HIDAYAH stores your bookmarks, Tasbih sessions, and Hifz progress locally on your device. Optional Google Sign-In encrypts and syncs your personal progress strictly to your own authenticated Firebase Firestore path (/users/{uid}). Location is used solely for local prayer times and Qibla calculation."
                    } else {
                        "HIDAYAH (هداية) is founded and owned by Mohammed Touhidul Islam, Son of Sirajul Islam. Authentic Quranic texts and verified Islamic references are presented for personal worship and education. Formal Fatwas should always be sought from qualified scholars."
                    },
                    style = MaterialTheme.typography.bodySmall
                )
            },
            confirmButton = { TextButton(onClick = { legalDialogTitle = null }) { Text("Close") } }
        )
    }
}
