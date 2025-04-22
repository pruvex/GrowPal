package de.Pruvex.growpal.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.Pruvex.growpal.R

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import de.Pruvex.growpal.ui.home.CurrentRoomCard
import de.Pruvex.growpal.ui.home.ActivePlantsCard
import de.Pruvex.growpal.ui.home.TodosTodayCard
import de.Pruvex.growpal.ui.home.QuickAccessCard

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 16.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CurrentRoomCard()
        ActivePlantsCard()
        TodosTodayCard()
        QuickAccessCard()
        Spacer(modifier = Modifier.height(16.dp))
    }
}

