package de.Pruvex.growpal.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CurrentRoomCard() {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Thermostat, contentDescription = "Temperatur", tint = Color(0xFF42A5F5))
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Aktiver Raum: Zelt 1", style = MaterialTheme.typography.titleMedium)
                Text("Temperatur: 24°C", style = MaterialTheme.typography.bodyMedium)
                Text("Luftfeuchtigkeit: 60%", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun ActivePlantsCard() {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.LocalFlorist, contentDescription = "Pflanze", tint = Color(0xFF66BB6A))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Aktive Pflanzen", style = MaterialTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("• Tomate – Wachstum: 60%\n• Basilikum – Wachstum: 80%", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun TodosTodayCard() {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.CalendarToday, contentDescription = "Kalender", tint = Color(0xFFFFA726))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Aufgaben heute", style = MaterialTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("• Gießen: Tomate\n• Düngen: Basilikum", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun QuickAccessCard() {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Schnellzugriff", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { /* TODO: Düngerechner */ }) {
                    Icon(Icons.Filled.Info, contentDescription = "Düngerechner")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Dünger")
                }
                Button(onClick = { /* TODO: Kalender */ }) {
                    Icon(Icons.Filled.CalendarToday, contentDescription = "Kalender")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Kalender")
                }
                Button(onClick = { /* TODO: Tipps */ }) {
                    Icon(Icons.Filled.Info, contentDescription = "Tipps")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tipps")
                }
            }
        }
    }
}
