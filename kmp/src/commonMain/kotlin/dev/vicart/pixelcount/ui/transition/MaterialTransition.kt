package dev.vicart.pixelcount.ui.transition

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.IntOffset
import androidx.navigation3.scene.Scene

enum class TransitionAxis(
    val slideDirectionProvider: (Boolean) -> AnimatedContentTransitionScope.SlideDirection
) {
    X(
        slideDirectionProvider = { if(it) AnimatedContentTransitionScope.SlideDirection.Start else AnimatedContentTransitionScope.SlideDirection.End }
    ),
    Y(
        slideDirectionProvider = { if(it) AnimatedContentTransitionScope.SlideDirection.Up else AnimatedContentTransitionScope.SlideDirection.Down }
    )
}

fun transitionAxisMetadata(axis: TransitionAxis) = mapOf("transitionAxis" to axis)

val <T: Any> Scene<T>.transitionAxis: TransitionAxis
    get() = metadata["transitionAxis"] as? TransitionAxis ?: TransitionAxis.X

class MaterialTransition(
    private val animationSpec: FiniteAnimationSpec<IntOffset>
) {

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    val <T: Any> AnimatedContentTransitionScope<Scene<T>>.transitionSpec: ContentTransform
        get() = ContentTransform(
            targetContentEnter = slideIntoContainer(
                towards = (targetState.transitionAxis).slideDirectionProvider(true),
                animationSpec = animationSpec,
                initialOffset = { it / 4 }
            ) + fadeIn(MotionScheme.standard().slowEffectsSpec()),
            initialContentExit = slideOutOfContainer(
                towards = (targetState.transitionAxis).slideDirectionProvider(true),
                animationSpec = animationSpec,
                targetOffset = { it / 4 }
            ) + fadeOut(MotionScheme.standard().slowEffectsSpec())
        )

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    val <T: Any> AnimatedContentTransitionScope<Scene<T>>.popTransitionSpec: ContentTransform
        get() = ContentTransform(
            targetContentEnter = slideIntoContainer(
                towards = (initialState.transitionAxis).slideDirectionProvider(false),
                animationSpec = animationSpec,
                initialOffset = { it / 4 }
            ) + fadeIn(MotionScheme.standard().slowEffectsSpec()),
            initialContentExit = slideOutOfContainer(
                towards = (initialState.transitionAxis).slideDirectionProvider(false),
                animationSpec = animationSpec,
                targetOffset = { it / 4 }
            ) + fadeOut(MotionScheme.standard().slowEffectsSpec())
        )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun rememberMaterialTransition(
    animationSpec: FiniteAnimationSpec<IntOffset> = MotionScheme.standard().slowSpatialSpec()
) : MaterialTransition {
    return remember(animationSpec) {
        MaterialTransition(animationSpec)
    }
}