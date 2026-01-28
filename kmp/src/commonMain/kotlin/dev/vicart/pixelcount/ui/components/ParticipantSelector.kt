package dev.vicart.pixelcount.ui.components

import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.vicart.pixelcount.shared.model.Participant

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ParticipantSelector(
    modifier: Modifier = Modifier,
    participants: List<Participant>,
    selectedParticipant: Participant?,
    onParticipantSelected: (Participant) -> Unit
) {
    ButtonGroup(
        overflowIndicator = {
            ButtonGroupDefaults.OverflowIndicator(
                menuState = it
            )
        },
        modifier = modifier
    ) {
        participants.forEach { participant ->
            toggleableItem(
                checked = selectedParticipant == participant,
                label = participant.name,
                onCheckedChange = { onParticipantSelected(participant) }
            )
        }
    }
}