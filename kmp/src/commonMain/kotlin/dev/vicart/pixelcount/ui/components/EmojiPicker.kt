package dev.vicart.pixelcount.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import dev.vicart.pixelcount.model.Emoji
import dev.vicart.pixelcount.resources.Res
import dev.vicart.pixelcount.resources.search_emoji
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.jetbrains.compose.resources.stringResource
import kotlin.collections.emptyList

@OptIn(ExperimentalSerializationApi::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun EmojiPicker(
    emoji: String,
    onEmojiSelected: (emoji: String) -> Unit
) {

    val emojis by produceState(emptyMap()) {
        value = withContext(Dispatchers.IO) {
            Res.readBytes("files/openmoji.json").inputStream().use {
                Json {
                    ignoreUnknownKeys = true
                }.decodeFromStream<List<Emoji>>(it)
                    .groupBy { it.group }
            }
        }
    }

    var showPicker by remember { mutableStateOf(false) }
    FilledTonalIconButton(
        onClick = { showPicker = true },
        shapes = IconButtonDefaults.shapes()
    ) {
        Text(
            text = emoji,
            fontSize = with(LocalDensity.current) {
                IconButtonDefaults.smallIconSize.toSp()
            }
        )
    }

    if(showPicker) {
        val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showPicker = false },
            sheetState = state
        ) {

            if(emojis.isNotEmpty()) {
                val coroutineScope = rememberCoroutineScope()

                val textFieldState = rememberTextFieldState()

                val filteredEmojis by produceState(emptyList(), textFieldState.text, emojis) {
                    value = withContext(Dispatchers.Default) {
                        emojis.filter { it.value.any { it.contains(textFieldState.text) } }.toList()
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        val searchbarState = rememberSearchBarState()
                        val inputField = @Composable {
                            SearchBarDefaults.InputField(
                                textFieldState = textFieldState,
                                onSearch = {},
                                searchBarState = searchbarState,
                                placeholder = { Text(stringResource(Res.string.search_emoji)) }
                            )
                        }
                        SearchBar(
                            state = searchbarState,
                            inputField = inputField,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    items(filteredEmojis, key = { it.first }) {
                        val filteredGroupEmoji by produceState<Pair<String, List<Emoji>>?>(null, textFieldState.text) {
                            value = withContext(Dispatchers.Default) {
                                it.let { it.first to it.second.filter { it.contains(textFieldState.text) } }
                            }
                        }
                        filteredGroupEmoji?.let {
                            EmojiDisplay(it) {
                                onEmojiSelected(it)
                                coroutineScope.launch {
                                    state.hide()
                                    showPicker = false
                                }
                            }
                        }
                    }
                }
            } else {
                CircularWavyProgressIndicator()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun EmojiDisplay(
    emojis: Pair<String, List<Emoji>>,
    emojiSelected: (emoji: String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = emojis.first.replace('-', ' ').capitalize(Locale.current),
            style = MaterialTheme.typography.titleSmall
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            emojis.second.forEach { emoji ->
                IconButton(
                    onClick = {
                        emojiSelected(emoji.emoji)
                    },
                    shapes = IconButtonDefaults.shapes()
                ) {
                    Text(
                        text = emoji.emoji,
                        fontSize = with(LocalDensity.current) {
                            IconButtonDefaults.smallIconSize.toSp()
                        }
                    )
                }
            }
        }
    }
}

private fun Emoji.contains(query: CharSequence) : Boolean {
    return this.annotation.contains(query, true) || this.tags.split(',')
        .any { it.contains(query, true) }
}