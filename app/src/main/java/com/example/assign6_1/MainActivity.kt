package com.example.assign6_1

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.assign6_1.ui.theme.Assign6_1Theme
import kotlin.math.pow

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var barometer: Sensor? = null

    private var _pressure by mutableFloatStateOf(0f)
    private var _accuracy by mutableStateOf("Unknown")


    override fun onCreate(savedInstanceState: Bundle?) {
        // Initialize Sensor Manager
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        barometer = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assign6_1Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BarometerScreen(pressure = _pressure, accuracy = _accuracy)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        barometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            _pressure = it.values[0]
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        _accuracy = when (accuracy) {
            SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> "High"
            SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> "Medium"
            SensorManager.SENSOR_STATUS_ACCURACY_LOW -> "Low"
            SensorManager.SENSOR_STATUS_UNRELIABLE -> "Unreliable"
            else -> "Unknown"
        }
    }
}


@Composable
fun BarometerScreen(pressure: Float, accuracy: String) {
    val pressureMax = 1100
    val pressureMin = 0
    val altitude = 44330 * (1 - (pressure / 1013.25).pow(1/5.255))
    val greyColor = (255 - (altitude / 40)).toInt().coerceIn(0, 255)
    val backgroundColor = Color(greyColor, greyColor, greyColor)
    val textColor = if (greyColor > 128) Color.Black else Color.White

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Altitude: $altitude meters",
            modifier = Modifier.padding(16.dp),
            color = textColor
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Accuracy $accuracy",
            modifier = Modifier.padding(16.dp),
            color = textColor
        )
    }
}