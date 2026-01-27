package dev.vicart.pixelcount.ui.components

import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ButtonGroupMenuState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import dev.vicart.pixelcount.shared.model.Participant

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ParticipantSelector(
    participants: List<Participant>,
    selectedParticipant: Participant?,
    onParticipantSelected: (Participant) -> Unit
) {
    ButtonGroup(
        overflowIndicator = {
            ButtonGroupDefaults.OverflowIndicator(
                menuState = remember { ButtonGroupMenuState() }
            )
        }
    ) {
        participants.forEach { participant ->
            toggleableItem(
                checked = selectedParticipant == participant,
                label = participant.name,
                onCheckedChange = { onParticipantSelected(participant) },
                weight = 1f
            )
        }
    }
}