package com.example.geofenceapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.geofenceapp.helper.addSingleGeofence
import com.example.geofenceapp.ui.theme.GeofenceAppTheme
import com.google.android.gms.location.LocationServices


const val GEOFENCE_ID = "kampus_geofence"
const val GEOFENCE_RADIUS_IN_METERS = 10f

const val GEOFENCE_LATITUDE = -7.952555
const val GEOFENCE_LONGITUDE = 112.614680

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GeofenceApp()
        }
    }
}

@Composable
fun GeofenceApp() {
    val context = LocalContext.current

    var hasForegroundLocation by remember { mutableStateOf(false) }
    var hasBackgroundLocation by remember { mutableStateOf(false) }

    // Launcher foreground
    val foregroundPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val fineGranted =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
            val coarseGranted =
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            hasForegroundLocation = fineGranted || coarseGranted
        }

    // Launcher background (Android 10+)
    val backgroundPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->
            hasBackgroundLocation = granted
        }

    // Cek awal
    LaunchedEffect(Unit) {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val bgGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        hasForegroundLocation = fineGranted || coarseGranted
        hasBackgroundLocation = bgGranted
    }

    val geofencingClient = remember {
        LocationServices.getGeofencingClient(context)
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Demo Geofencing Satu Titik",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(Modifier.height(16.dp))

            Text(
                "Izin foreground: ${if (hasForegroundLocation) "SUDAH" else "BELUM"}"
            )
            Text(
                "Izin background: ${if (hasBackgroundLocation) "SUDAH" else "BELUM"}"
            )

            Spacer(Modifier.height(16.dp))

            Button(onClick = {
                foregroundPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }) {
                Text("Minta izin lokasi (foreground)")
            }

            Spacer(Modifier.height(8.dp))

            // Tombol khusus background (Android 10+)
            Button(onClick = {
                backgroundPermissionLauncher.launch(
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            }) {
                Text("Minta izin lokasi background")
            }

            Spacer(Modifier.height(16.dp))

            if (hasForegroundLocation) {
                Button(onClick = {
                    addSingleGeofence(context, geofencingClient)
                }) {
                    Text("Aktifkan geofence statis")
                }
            }
        }
    }
}



@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GeofenceAppTheme {
        GeofenceApp()
    }
}