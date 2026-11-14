package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.UserPreferencesState
import com.example.localization.stringsFor
import com.example.ui.navigation.HidayahRoutes
import com.example.utils.WallpaperAndAlertUtils

private data class ExploreDestination(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val route: String?,
)

@Composable
fun ExploreHubScreen(
    prefs: UserPreferencesState,
    onNavigate: (String) -> Unit,
) {
    val context = LocalContext.current
    val strings = stringsFor(prefs.appLanguage)

    val itemsList = listOf(
        ExploreDestination("Prayer Times & Adhan", "Live countdown, 30-day calendar, Hijri & GPS", Icons.Filled.AccessTime, HidayahRoutes.PRAYERS),
        ExploreDestination(strings.qiblaCompass, "Real-time compass bearing & distance to Kaaba", Icons.Filled.Explore, HidayahRoutes.QIBLA),
        ExploreDestination(strings.duasAzkar, "16 categories with Bengali & English pronunciation", Icons.Filled.Favorite, HidayahRoutes.DUAS),
        ExploreDestination(strings.tasbihCounter, "Haptics, custom Dhikr, goals & session history", Icons.Filled.FormatListNumbered, HidayahRoutes.TASBIH),
        ExploreDestination("Daily Islamic Life Tracker", "5 Daily Prayers, Qada, Fasting, Khatam & Sunnah", Icons.Filled.Checklist, HidayahRoutes.LIFE_TRACKER),
        ExploreDestination(strings.asmaUlHusna, "All 99 Names of Allah with audio & meanings", Icons.Filled.AutoAwesome, HidayahRoutes.ASMA_99),
        ExploreDestination(strings.zakatCalculator, "Gold, silver, cash, trade & Nisab 2.5% calculator", Icons.Filled.Calculate, HidayahRoutes.ZAKAT),
        ExploreDestination("Hadith, Tafsir, Stories & Guides", "9 Hadith collections, Ibn Kathir/Jalalayn, Ramadan & Hajj", Icons.Filled.HistoryEdu, HidayahRoutes.HADITH_LIBRARY),
        ExploreDestination("Nearby Mosque Finder", "Find Masjids around your current location in Maps", Icons.Filled.LocationOn, null),
        ExploreDestination(strings.wallpapers, "Download 4K Islamic wallpapers & share", Icons.Filled.Wallpaper, HidayahRoutes.WALLPAPERS),
        ExploreDestination("HIDAYAH AI Assistant", "8 specialized Islamic educational AI modes", Icons.Filled.Psychology, HidayahRoutes.AI_ASSISTANT),
    )

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 840.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Filled.Mosque, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("Explore Worship & Lifestyle", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                }
            }

            items(itemsList) { dest ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("explore_item_${dest.route ?: "mosque_finder"}")
                        .clickable {
                            if (dest.route != null) {
                                onNavigate(dest.route)
                            } else {
                                WallpaperAndAlertUtils.openNearbyMosques(context)
                            }
                        },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(dest.icon, contentDescription = dest.title, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(dest.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(dest.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
