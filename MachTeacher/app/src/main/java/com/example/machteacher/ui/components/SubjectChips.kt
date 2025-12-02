package com.example.machteacher.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SubjectChips(
    subjects: List<String>?,
    modifier: Modifier = Modifier
) {
    if (subjects.isNullOrEmpty()) {
        Text("Sin materias", style = MaterialTheme.typography.bodyMedium)
        return
    }

    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        subjects.forEach { s ->
            SuggestionChip(
                onClick = {},
                label = { Text(s) }
            )
        }
    }
}