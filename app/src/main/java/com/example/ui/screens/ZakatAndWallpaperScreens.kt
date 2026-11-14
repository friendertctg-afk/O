package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.local.UserPreferencesState
import com.example.data.seed.IslamicLibraryData
import com.example.utils.WallpaperAndAlertUtils
import kotlinx.coroutines.launch
import java.util.Locale

private data class CurrencyNisab(
    val code: String,
    val symbol: String,
    val goldNisabValue: Double,
    val silverNisabValue: Double,
)

private val currencies = listOf(
    CurrencyNisab("USD", "$", 7200.0, 580.0),
    CurrencyNisab("BDT", "৳", 860000.0, 72000.0),
    CurrencyNisab("SAR", "﷼", 27000.0, 2180.0),
    CurrencyNisab("GBP", "£", 5700.0, 460.0),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZakatCalculatorScreen(
    onBack: () -> Unit,
) {
    var currency by remember { mutableStateOf(currencies.first()) }
    var useSilverNisab by remember { mutableStateOf(true) }
    var cash by remember { mutableStateOf("") }
    var bankSavings by remember { mutableStateOf("") }
    var goldValue by remember { mutableStateOf("") }
    var silverValue by remember { mutableStateOf("") }
    var businessInventory by remember { mutableStateOf("") }
    var investments by remember { mutableStateOf("") }
    var receivables by remember { mutableStateOf("") }
    var liabilities by remember { mutableStateOf("") }

    fun parseAmt(s: String): Double = s.toDoubleOrNull()?.coerceAtLeast(0.0) ?: 0.0

    val totalAssets = parseAmt(cash) + parseAmt(bankSavings) + parseAmt(goldValue) +
        parseAmt(silverValue) + parseAmt(businessInventory) + parseAmt(investments) + parseAmt(receivables)
    val totalDebt = parseAmt(liabilities)
    val netZakatable = (totalAssets - totalDebt).coerceAtLeast(0.0)
    val nisabThreshold = if (useSilverNisab) currency.silverNisabValue else currency.goldNisabValue
    val zakatDue = if (netZakatable >= nisabThreshold) netZakatable * 0.025 else 0.0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Zakat Calculator") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .widthIn(max = 840.dp)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Estimated Zakat Due (2.5%)", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f))
                            Text(
                                "${currency.symbol}${String.format(Locale.US, "%,.2f", zakatDue)}",
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                "Net Zakatable Wealth: ${currency.symbol}${String.format(Locale.US, "%,.2f", netZakatable)} • Nisab (${if (useSilverNisab) "Silver 612.36g" else "Gold 87.48g"}): ${currency.symbol}${String.format(Locale.US, "%,.0f", nisabThreshold)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                            )
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        currencies.forEach { c ->
                            FilterChip(selected = currency == c, onClick = { currency = c }, label = { Text("${c.code} (${c.symbol})") })
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = useSilverNisab, onClick = { useSilverNisab = true }, label = { Text("Silver Nisab (612.36g)") })
                        FilterChip(selected = !useSilverNisab, onClick = { useSilverNisab = false }, label = { Text("Gold Nisab (87.48g)") })
                    }
                }

                item {
                    ZakatNumberField("Cash on Hand", cash) { cash = it }
                    Spacer(Modifier.height(8.dp))
                    ZakatNumberField("Bank Balances & Savings", bankSavings) { bankSavings = it }
                    Spacer(Modifier.height(8.dp))
                    ZakatNumberField("Gold Market Value", goldValue) { goldValue = it }
                    Spacer(Modifier.height(8.dp))
                    ZakatNumberField("Silver Market Value", silverValue) { silverValue = it }
                    Spacer(Modifier.height(8.dp))
                    ZakatNumberField("Business Trade Inventory", businessInventory) { businessInventory = it }
                    Spacer(Modifier.height(8.dp))
                    ZakatNumberField("Shares & Investments", investments) { investments = it }
                    Spacer(Modifier.height(8.dp))
                    ZakatNumberField("Money Owed to You (Receivables)", receivables) { receivables = it }
                    Spacer(Modifier.height(8.dp))
                    ZakatNumberField("Short-Term Liabilities / Debts Due", liabilities) { liabilities = it }
                }

                item {
                    Text(
                        "Scholarly Guidance: Zakat is obligatory when net eligible wealth meets or exceeds Nisab for one full lunar year (Hawl). Confirm local gold/silver market spot rates and consult a scholar for specialized business/agricultural assets.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ZakatNumberField(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) onChange(it) },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WallpapersScreen(
    prefs: UserPreferencesState,
    favoriteIds: List<String>,
    onToggleFavorite: (String) -> Unit,
    onSetDailyWallpaper: (String) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var filterMode by remember { mutableStateOf("All") }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    val itemsList = remember(filterMode, favoriteIds) {
        IslamicLibraryData.wallpapers.filter { w ->
            when (filterMode) {
                "Dark" -> w.isDark
                "Light" -> !w.isDark
                "Favorites" -> w.id in favoriteIds
                else -> true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Islamic 4K Wallpapers") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 840.dp)
                    .fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("All", "Dark", "Light", "Favorites").forEach { f ->
                        FilterChip(selected = filterMode == f, onClick = { filterMode = f }, label = { Text(f) })
                    }
                }

                statusMessage?.let { msg ->
                    Text(
                        msg,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }

                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(itemsList, key = { it.id }) { wp ->
                        val isFav = wp.id in favoriteIds
                        val isDaily = prefs.dailyWallpaperId == wp.id
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Image(
                                    painter = painterResource(wp.drawableRes),
                                    contentDescription = wp.title,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(190.dp)
                                        .clip(RoundedCornerShape(14.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(wp.title, fontWeight = FontWeight.Bold)
                                        Text("${wp.category} • ${wp.resolutionLabel}", style = MaterialTheme.typography.bodySmall)
                                    }
                                    IconButton(onClick = { onToggleFavorite(wp.id) }) {
                                        Icon(
                                            if (isFav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                            contentDescription = "Favorite Wallpaper",
                                            tint = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {
                                            scope.launch {
                                                val res = WallpaperAndAlertUtils.saveWallpaperToGallery(context, wp.drawableRes, wp.title)
                                                statusMessage = res.fold(
                                                    onSuccess = { it },
                                                    onFailure = { "Unable to save: ${it.localizedMessage}" }
                                                )
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Filled.Download, contentDescription = null)
                                        Spacer(Modifier.padding(2.dp))
                                        Text("Save")
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            scope.launch {
                                                WallpaperAndAlertUtils.shareWallpaper(context, wp.drawableRes, wp.title)
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Filled.Share, contentDescription = null)
                                        Spacer(Modifier.padding(2.dp))
                                        Text("Share")
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            onSetDailyWallpaper(wp.id)
                                            statusMessage = "${wp.title} set as your Daily Wallpaper!"
                                        }
                                    ) {
                                        Icon(Icons.Filled.Wallpaper, contentDescription = "Set Daily")
                                        if (isDaily) Text(" ✓")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
