package dev.vicart.pixelcount.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandIn
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import dev.vicart.pixelcount.resources.Res
import dev.vicart.pixelcount.resources.add
import dev.vicart.pixelcount.resources.add_expense
import dev.vicart.pixelcount.resources.add_photo
import dev.vicart.pixelcount.resources.amount
import dev.vicart.pixelcount.resources.delete
import dev.vicart.pixelcount.resources.modify
import dev.vicart.pixelcount.resources.paid_by
import dev.vicart.pixelcount.resources.payment
import dev.vicart.pixelcount.resources.picture
import dev.vicart.pixelcount.resources.refund
import dev.vicart.pixelcount.resources.share_with
import dev.vicart.pixelcount.resources.title
import dev.vicart.pixelcount.resources.to
import dev.vicart.pixelcount.resources.transfer
import dev.vicart.pixelcount.shared.model.Expense
import dev.vicart.pixelcount.shared.model.ExpenseGroup
import dev.vicart.pixelcount.shared.model.Participant
import dev.vicart.pixelcount.shared.model.PaymentTypeEnum
import dev.vicart.pixelcount.shared.utils.prettyPrint
import dev.vicart.pixelcount.ui.components.BackButton
import dev.vicart.pixelcount.ui.components.ParticipantSelector
import dev.vicart.pixelcount.ui.transition.LocalSharedTransitionScope
import dev.vicart.pixelcount.ui.viewmodel.AddExpenseViewModel
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.Uuid

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    itemId: Uuid,
    initial: Expense?,
    vm: AddExpenseViewModel = viewModel(key = itemId.toString()) { AddExpenseViewModel(itemId, initial) },
    onBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopBar(
                onBack = onBack,
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
            .then(with(LocalSharedTransitionScope.current) {
                Modifier.sharedBounds(
                    sharedContentState = rememberSharedContentState("new_payment_fab"),
                    animatedVisibilityScope = LocalNavAnimatedContentScope.current
                ).sharedBounds(
                    sharedContentState = rememberSharedContentState("expense_item_${initial?.id}"),
                    animatedVisibilityScope = LocalNavAnimatedContentScope.current
                )
            })
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val group by vm.expenseGroup.collectAsStateWithLifecycle()

            val paymentType by vm.paymentType.collectAsStateWithLifecycle()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween,
                    Alignment.CenterHorizontally)
            ) {
                ToggleButton(
                    checked = paymentType == PaymentTypeEnum.PAYMENT,
                    onCheckedChange = { vm.paymentType.value = PaymentTypeEnum.PAYMENT },
                    shapes = ButtonGroupDefaults.connectedLeadingButtonShapes()
                ) {
                    AnimatedVisibility(
                        visible = paymentType == PaymentTypeEnum.PAYMENT
                    ) {
                        Row {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = null,
                                modifier = Modifier.size(ToggleButtonDefaults.IconSize)
                            )
                            Spacer(modifier = Modifier.width(ToggleButtonDefaults.IconSpacing))
                        }
                    }
                    Text(stringResource(Res.string.payment))
                }

                ToggleButton(
                    checked = paymentType == PaymentTypeEnum.REFUND,
                    onCheckedChange = { vm.paymentType.value = PaymentTypeEnum.REFUND },
                    shapes = ButtonGroupDefaults.connectedMiddleButtonShapes()
                ) {
                    AnimatedVisibility(
                        visible = paymentType == PaymentTypeEnum.REFUND
                    ) {
                        Row {
                            Icon(
                                imageVector = Icons.Default.Payments,
                                contentDescription = null,
                                modifier = Modifier.size(ToggleButtonDefaults.IconSize)
                            )
                            Spacer(modifier = Modifier.width(ToggleButtonDefaults.IconSpacing))
                        }
                    }
                    Text(stringResource(Res.string.refund))
                }

                ToggleButton(
                    checked = paymentType == PaymentTypeEnum.TRANSFER,
                    onCheckedChange = { vm.paymentType.value = PaymentTypeEnum.TRANSFER },
                    shapes = ButtonGroupDefaults.connectedTrailingButtonShapes()
                ) {
                    AnimatedVisibility(
                        visible = paymentType == PaymentTypeEnum.TRANSFER
                    ) {
                        Row {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.CompareArrows,
                                contentDescription = null,
                                modifier = Modifier.size(ToggleButtonDefaults.IconSize)
                            )
                            Spacer(modifier = Modifier.width(ToggleButtonDefaults.IconSpacing))
                        }
                    }
                    Text(stringResource(Res.string.transfer))
                }
            }

            AnimatedVisibility(
                visible = paymentType == PaymentTypeEnum.PAYMENT || paymentType == PaymentTypeEnum.REFUND,
                enter = expandVertically(MaterialTheme.motionScheme.fastEffectsSpec())
                    + fadeIn(MaterialTheme.motionScheme.fastEffectsSpec()),
                exit = shrinkVertically(MaterialTheme.motionScheme.fastEffectsSpec())
                    + fadeOut(MaterialTheme.motionScheme.fastEffectsSpec())
            ) {
                val title by vm.title.collectAsStateWithLifecycle()

                TextField(
                    value = title,
                    onValueChange = { vm.title.value = it },
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    label = { Text(stringResource(Res.string.title)) },
                    singleLine = true
                )
            }

            val amount by vm.amount.collectAsStateWithLifecycle()

            TextField(
                value = amount,
                onValueChange = {
                    if(it.isEmpty()) {
                        vm.amount.value = ""
                    } else {
                        try {
                            it.toDouble()
                            vm.amount.value = it
                        } catch (_: Exception) {}
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text(stringResource(Res.string.amount)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                trailingIcon = {
                    Text(
                        text = group?.currency?.symbol.orEmpty(),
                        fontSize = with(LocalDensity.current) { 24.dp.toSp() },
                        fontWeight = FontWeight.Bold
                    )
                }
            )

            Text(
                text = stringResource(Res.string.paid_by),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp)
            )

            val paidBy by vm.paidBy.collectAsStateWithLifecycle()

            ParticipantSelector(
                participants = group?.participants.orEmpty(),
                selectedParticipant = paidBy,
                onParticipantSelected = { vm.changePaidBy(it) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            AnimatedContent(
                targetState = paymentType == PaymentTypeEnum.PAYMENT || paymentType == PaymentTypeEnum.REFUND,
                transitionSpec = {
                    (expandIn(MotionScheme.expressive().defaultSpatialSpec()) + fadeIn(MotionScheme.expressive().defaultEffectsSpec()))
                        .togetherWith(shrinkOut(MotionScheme.expressive().defaultSpatialSpec()) + fadeOut(
                            MotionScheme.expressive().defaultEffectsSpec()))
                },
                contentAlignment = Alignment.TopCenter
            ) {
                if(it) {
                    SharePaymentParticipantList(
                        amount = amount,
                        paidBy = paidBy,
                        vm = vm,
                        group = group
                    )
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.to),
                            style = MaterialTheme.typography.titleMedium
                        )

                        val transferTo by vm.transferTo.collectAsStateWithLifecycle()

                        ParticipantSelector(
                            participants = group?.participants?.filterNot { it == paidBy }.orEmpty(),
                            selectedParticipant = transferTo,
                            onParticipantSelected = { vm.transferTo.value = it }
                        )
                    }
                }
            }

            Text(
                text = stringResource(Res.string.picture),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp)
            )

            val pictureBitmap by vm.pictureBitmap.collectAsStateWithLifecycle(null)

            if(pictureBitmap == null) {
                OutlinedButton(
                    onClick = dropUnlessResumed(block = vm::launchPickImage),
                    shapes = ButtonDefaults.shapes(
                        shape = ButtonDefaults.squareShape,
                        pressedShape = ButtonDefaults.largePressedShape
                    ),
                    modifier = Modifier.height(ButtonDefaults.LargeContainerHeight).fillMaxWidth(),
                    contentPadding = ButtonDefaults.LargeContentPadding
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.LargeIconSize)
                    )
                    Spacer(modifier = Modifier.width(ButtonDefaults.LargeIconSpacing))
                    Text(stringResource(Res.string.add_photo))
                }
            } else {
                Box {
                    Image(
                        bitmap = pictureBitmap!!,
                        contentDescription = null,
                        modifier = Modifier.clickable(
                            onClick = dropUnlessResumed(block = vm::launchPickImage)
                        )
                    )

                    FilledTonalIconButton(
                        onClick = { vm.removePicture() },
                        shapes = IconButtonDefaults.shapes(
                            shape = IconButtonDefaults.extraSmallRoundShape,
                            pressedShape = IconButtonDefaults.extraSmallPressedShape
                        ),
                        modifier = Modifier
                            .padding(16.dp)
                            .size(IconButtonDefaults.extraSmallContainerSize())
                            .align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(IconButtonDefaults.extraSmallIconSize)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = ButtonGroupDefaults.HorizontalArrangement
            ) {
                if(initial != null) {
                    OutlinedButton(
                        onClick = {
                            vm.deleteExpense()
                            onBack()
                        },
                        shapes = ButtonDefaults.shapesFor(ButtonDefaults.MediumContainerHeight),
                        modifier = Modifier.height(ButtonDefaults.MediumContainerHeight).weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(ButtonDefaults.MediumIconSize)
                        )
                        Spacer(modifier = Modifier.width(ButtonDefaults.MediumIconSpacing))
                        Text(
                            text = stringResource(Res.string.delete)
                        )
                    }
                }

                val canAdd by vm.canAdd.collectAsStateWithLifecycle(false)
                Button(
                    onClick = {
                        vm.addExpense()
                        onBack()
                    },
                    shapes = ButtonDefaults.shapesFor(ButtonDefaults.MediumContainerHeight),
                    modifier = Modifier.height(ButtonDefaults.MediumContainerHeight).weight(1f),
                    contentPadding = ButtonDefaults.MediumContentPadding,
                    enabled = canAdd
                ) {
                    Icon(
                        imageVector = if(initial == null) Icons.Default.Add else Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.MediumIconSize)
                    )
                    Spacer(modifier = Modifier.width(ButtonDefaults.MediumIconSpacing))
                    Text(
                        text = if(initial == null) stringResource(Res.string.add)
                            else stringResource(Res.string.modify)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SharePaymentParticipantList(
    amount: String,
    paidBy: Participant?,
    vm: AddExpenseViewModel,
    group: ExpenseGroup?
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(top = 16.dp)
    ) {
        Text(
            text = stringResource(Res.string.share_with),
            style = MaterialTheme.typography.titleMedium
        )

        val sharedWith by vm.sharedWith.collectAsStateWithLifecycle()

        group?.participants?.filterNot { it == paidBy }?.forEach { participant ->
            val containerColor by animateColorAsState(
                targetValue = if(sharedWith?.contains(participant) == true) MaterialTheme.colorScheme.secondary
                else MaterialTheme.colorScheme.surfaceContainer,
                animationSpec = MaterialTheme.motionScheme.fastEffectsSpec()
            )
            val amountForParticipant = remember(amount, sharedWith, group) {
                group.let {
                    (if(sharedWith?.contains(participant) == false)
                        0.0
                    else
                        ((amount.toDoubleOrNull() ?: 0.0) / (sharedWith.orEmpty().size + 1))
                            ).prettyPrint(it.currency)
                }
            }
            ListItem(
                headlineContent = { Text(participant.name) },
                colors = ListItemDefaults.colors().copy(
                    containerColor = containerColor,
                    headlineColor = if(sharedWith?.contains(participant) == true) MaterialTheme.colorScheme.onSecondary
                    else MaterialTheme.colorScheme.onSurface,
                    trailingIconColor = if(sharedWith?.contains(participant) == true) MaterialTheme.colorScheme.onSecondary
                    else MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .selectable(selected = sharedWith?.contains(participant) == true) {
                        vm.toggleShareParticipant(participant)
                    },
                trailingContent = {
                    Text(amountForParticipant)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TopBar(
    onBack: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    TopAppBar(
        title = { Text(stringResource(Res.string.add_expense)) },
        navigationIcon = {
            BackButton(onBack)
        },
        scrollBehavior = scrollBehavior
    )
}