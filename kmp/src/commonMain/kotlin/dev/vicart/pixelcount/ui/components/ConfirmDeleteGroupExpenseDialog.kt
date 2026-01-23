package dev.vicart.pixelcount.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import dev.vicart.pixelcount.resources.Res
import dev.vicart.pixelcount.resources.cancel
import dev.vicart.pixelcount.resources.delete
import dev.vicart.pixelcount.resources.delete_group_expense
import dev.vicart.pixelcount.resources.delete_group_expense_info
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ConfirmDeleteGroupExpenseDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if(isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = onConfirm,
                    shapes = ButtonDefaults.shapes(),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(Res.string.delete))
                }
            },
            icon = {
                Icon(Icons.Default.Delete, null)
            },
            dismissButton = {
                OutlinedButton(
                    onClick = onDismiss,
                    shapes = ButtonDefaults.shapes()
                ) {
                    Text(stringResource(Res.string.cancel))
                }
            },
            title = {
                Text(
                    text = stringResource(Res.string.delete_group_expense),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(stringResource(Res.string.delete_group_expense_info))
            }
        )
    }
}