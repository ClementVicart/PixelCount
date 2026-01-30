package dev.vicart.pixelcount.model

import dev.vicart.pixelcount.resources.Res
import dev.vicart.pixelcount.resources.error_importing_file
import org.jetbrains.compose.resources.StringResource

enum class ErrorEnum(val messageRes: StringResource) {
    IMPORT_FILE_ERROR(Res.string.error_importing_file)
}