package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserPreferencesState
import com.example.ui.viewmodel.PrayerUiState
import com.example.utils.PrayerAndQiblaUtils
import com.example.utils.PrayerMethod
import com.example.utils.WallpaperAndAlertUtils
import java.util.Locale
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerTimesScreen(
    prefs: UserPreferencesState,
    prayerState: PrayerUiState,
    onSelectCity: (String, Double, Double) -> Unit,
    onRequestGps: () -> Unit,
    onChangeMethod: (Int, Boolean) -> Unit,
    onChangeNotifications: (Boolean, Boolean, Boolean) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var showCityDialog by remember { mutableStateOf(false) }
    var feedbackMsg by remember { mutableStateOf<String?>(null) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            onRequestGps()
        } else {
            feedbackMsg = "Location permission denied — choose a city manually."
        }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            WallpaperAndAlertUtils.sendPrayerTestNotification(
                context,
                "HIDAYAH Prayer Alerts Active",
                "You will receive reminders for ${prayerState.nextPrayerName} and Jummah.",
                withSound = prefs.adhanNotificationEnabled
            )
            feedbackMsg = "Prayer notifications enabled!"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Prayer Times & Adhan") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                    }) {
                        Icon(Icons.Filled.MyLocation, contentDescription = "Detect GPS Location")
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
                PrimaryTabRow(selectedTabIndex = selectedTab) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Today") })
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("30-Day Calendar") })
                    Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Settings") })
                }

                when (selectedTab) {
                    0 -> {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            item {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(18.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                "${prayerState.schedule.cityName} • ${prayerState.schedule.hijriDate}",
                                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                                                style = MaterialTheme.typography.labelLarge
                                            )
                                            OutlinedButton(onClick = { showCityDialog = true }) {
                                                Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                                Spacer(Modifier.width(4.dp))
                                                Text("City", color = Color.White)
                                            }
                                        }
                                        Text(
                                            "Next: ${prayerState.nextPrayerName} (${prayerState.nextPrayerTime})",
                                            style = MaterialTheme.typography.headlineSmall,
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            "Countdown: ${prayerState.countdownText}",
                                            style = MaterialTheme.typography.titleLarge,
                                            color = Color(0xFFE7C563),
                                            fontWeight = FontWeight.Bold
                                        )
                                        (feedbackMsg ?: prayerState.locationStatus)?.let {
                                            Text(it, style = MaterialTheme.typography.bodySmall, color = Color(0xFFE7C563))
                                        }
                                    }
                                }
                            }

                            val rows = listOf(
                                "Fajr" to prayerState.schedule.fajr,
                                "Sunrise (Ishraq)" to prayerState.schedule.sunrise,
                                "Dhuhr" to prayerState.schedule.dhuhr,
                                "Asr" to prayerState.schedule.asr,
                                "Maghrib" to prayerState.schedule.maghrib,
                                "Isha" to prayerState.schedule.isha,
                            )
                            items(rows) { (name, time) ->
                                val isNext = name.startsWith(prayerState.nextPrayerName)
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isNext) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        Text(time, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                    1 -> {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(prayerState.monthlyCalendar) { day ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("${day.gregorian} • ${day.hijri}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                                        Spacer(Modifier.height(4.dp))
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("F: ${day.fajr}", style = MaterialTheme.typography.bodySmall)
                                            Text("D: ${day.dhuhr}", style = MaterialTheme.typography.bodySmall)
                                            Text("A: ${day.asr}", style = MaterialTheme.typography.bodySmall)
                                            Text("M: ${day.maghrib}", style = MaterialTheme.typography.bodySmall)
                                            Text("I: ${day.isha}", style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else -> {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Text("Calculation Method", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    PrayerMethod.entries.forEach { method ->
                                        FilterChip(
                                            selected = prefs.prayerMethodIndex == method.index,
                                            onClick = { onChangeMethod(method.index, prefs.asrHanafi) },
                                            label = { Text(method.name) }
                                        )
                                    }
                                }
                            }
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Hanafi Asr Timing", fontWeight = FontWeight.Bold)
                                        Text("Later Asr shadow threshold (2x shadow)", style = MaterialTheme.typography.bodySmall)
                                    }
                                    Switch(
                                        checked = prefs.asrHanafi,
                                        onCheckedChange = { onChangeMethod(prefs.prayerMethodIndex, it) }
                                    )
                                }
                            }
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Prayer Reminders", modifier = Modifier.weight(1f))
                                    Switch(
                                        checked = prefs.prayerNotificationsEnabled,
                                        onCheckedChange = {
                                            onChangeNotifications(it, prefs.adhanNotificationEnabled, prefs.jummahReminderEnabled)
                                            if (it && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                            }
                                        }
                                    )
                                }
                            }
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Adhan Sound Alert", modifier = Modifier.weight(1f))
                                    Switch(
                                        checked = prefs.adhanNotificationEnabled,
                                        onCheckedChange = {
                                            onChangeNotifications(prefs.prayerNotificationsEnabled, it, prefs.jummahReminderEnabled)
                                        }
                                    )
                                }
                            }
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Jummah Friday Reminder", modifier = Modifier.weight(1f))
                                    Switch(
                                        checked = prefs.jummahReminderEnabled,
                                        onCheckedChange = {
                                            onChangeNotifications(prefs.prayerNotificationsEnabled, prefs.adhanNotificationEnabled, it)
                                        }
                                    )
                                }
                            }
                            item {
                                Button(
                                    onClick = {
                                        val ok = WallpaperAndAlertUtils.sendPrayerTestNotification(
                                            context,
                                            "HIDAYAH Prayer Alert",
                                            "Next prayer: ${prayerState.nextPrayerName} at ${prayerState.nextPrayerTime}.",
                                            withSound = prefs.adhanNotificationEnabled
                                        )
                                        if (!ok && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Filled.NotificationsActive, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Send Test Prayer Alert")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCityDialog) {
        AlertDialog(
            onDismissRequest = { showCityDialog = false },
            title = { Text("Select City") },
            text = {
                LazyColumn(modifier = Modifier.height(300.dp)) {
                    items(PrayerAndQiblaUtils.presetCities) { city ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectCity(city.name, city.latitude, city.longitude)
                                    showCityDialog = false
                                }
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${city.name}, ${city.country}", fontWeight = FontWeight.Medium)
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showCityDialog = false }) { Text("Close") } }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QiblaCompassScreen(
    prefs: UserPreferencesState,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    var azimuthDeg by remember { mutableFloatStateOf(0f) }
    var hasCompassSensor by remember { mutableStateOf(true) }

    val qiblaBearing = remember(prefs.latitude, prefs.longitude) {
        PrayerAndQiblaUtils.qiblaBearingDegrees(prefs.latitude, prefs.longitude)
    }
    val distanceKm = remember(prefs.latitude, prefs.longitude) {
        PrayerAndQiblaUtils.distanceToKaabaKm(prefs.latitude, prefs.longitude)
    }

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnet = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        if (accel == null || magnet == null) {
            hasCompassSensor = false
            onDispose { }
        } else {
            val gravity = FloatArray(3)
            val geomagnetic = FloatArray(3)
            val rMat = FloatArray(9)
            val iMat = FloatArray(9)
            val orientation = FloatArray(3)

            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    val alpha = 0.85f
                    if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                        for (i in 0..2) gravity[i] = alpha * gravity[i] + (1 - alpha) * event.values[i]
                    } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                        for (i in 0..2) geomagnetic[i] = alpha * geomagnetic[i] + (1 - alpha) * event.values[i]
                    }
                    if (SensorManager.getRotationMatrix(rMat, iMat, gravity, geomagnetic)) {
                        SensorManager.getOrientation(rMat, orientation)
                        val deg = Math.toDegrees(orientation[0].toDouble()).toFloat()
                        azimuthDeg = (deg + 360f) % 360f
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
            }
            sensorManager.registerListener(listener, accel, SensorManager.SENSOR_DELAY_UI)
            sensorManager.registerListener(listener, magnet, SensorManager.SENSOR_DELAY_UI)
            onDispose {
                sensorManager.unregisterListener(listener)
            }
        }
    }

    val needleAngle = (qiblaBearing.toFloat() - azimuthDeg + 360f) % 360f
    val aligned = abs(((needleAngle + 180f) % 360f) - 180f) <= 6f
    val primaryColor = MaterialTheme.colorScheme.primary
    val goldColor = MaterialTheme.colorScheme.secondary

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Qibla Compass") },
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
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 560.dp)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "${prefs.selectedCityName} -> Holy Kaaba (Makkah)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = String.format(Locale.US, "Qibla Bearing: %.1f° • Distance: %.0f km", qiblaBearing, distanceKm),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Box(modifier = Modifier.size(260.dp), contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val radius = size.minDimension * 0.44f
                        drawCircle(
                            color = if (aligned) Color(0xFF198754) else primaryColor.copy(alpha = 0.25f),
                            radius = radius,
                            center = center,
                            style = Stroke(width = 10f)
                        )
                        rotate(degrees = -azimuthDeg, pivot = center) {
                            drawLine(
                                color = Color.Gray,
                                start = center,
                                end = Offset(center.x, center.y - radius * 0.78f),
                                strokeWidth = 5f,
                                cap = StrokeCap.Round
                            )
                        }
                        rotate(degrees = needleAngle, pivot = center) {
                            drawLine(
                                color = goldColor,
                                start = center,
                                end = Offset(center.x, center.y - radius * 0.90f),
                                strokeWidth = 14f,
                                cap = StrokeCap.Round
                            )
                            drawCircle(
                                color = Color(0xFF1B1B1B),
                                radius = 18f,
                                center = Offset(center.x, center.y - radius * 0.90f)
                            )
                        }
                        drawCircle(color = primaryColor, radius = 14f, center = center)
                    }
                    Text(
                        text = if (aligned) "Aligned to Qibla!" else "🕋",
                        fontSize = if (aligned) 15.sp else 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (aligned) Color(0xFF198754) else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp)
                    )
                }

                if (!hasCompassSensor) {
                    Text(
                        "Hardware magnetometer unavailable on this device/emulator. Face ${String.format(Locale.US, "%.1f°", qiblaBearing)} clockwise from True North.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                } else {
                    Text(
                        "Hold your phone flat and move in a gentle figure-8 motion if compass calibration is needed.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
