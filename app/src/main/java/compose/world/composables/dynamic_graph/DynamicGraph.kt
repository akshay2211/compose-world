package compose.world.composables.dynamic_graph

import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import compose.world.composables.dynamic_graph.components.Node
import compose.world.composables.dynamic_graph.components.NodeConnection
import compose.world.composables.dynamic_graph.components.NodeConnectionWidget
import compose.world.composables.dynamic_graph.components.NodeWidget
import compose.world.composables.dynamic_graph.components.nodeSize
import compose.world.composables.dynamic_graph.dialog_contents.AddNodeDialog
import compose.world.composables.dynamic_graph.dialog_contents.GenericSettingsDialogContent
import compose.world.composables.dynamic_graph.dialog_contents.NodeConnectionSettingsDialogContent
import compose.world.composables.dynamic_graph.dialog_contents.NodeSettingsDialogContent
import compose.world.composables.dynamic_graph.layers.ActionsLayer
import compose.world.composables.dynamic_graph.layers.DialogLayer
import compose.world.composables.dynamic_graph.layers.GridLayer
import compose.world.composables.dynamic_graph.layers.GridLayerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.hypot

class GraphScreenState {
    var nodes = mutableStateListOf<Node>()

    // states that affect the whole screen
    var globalOffset by mutableStateOf(Offset.Zero)
    var globalScale by mutableFloatStateOf(1f)
    var globalRotation by mutableFloatStateOf(0f)

    //dialog related states
    var shouldDisplayAddNodeDialog by mutableStateOf(false)
    var shouldDisplayGeneralSettingsDialog by mutableStateOf(false)
    var selectedNode by mutableStateOf<Node?>(null)
    var selectedConnection by mutableStateOf<NodeConnection?>(null)

    // grid
    val gridLayerState = GridLayerState()

    fun addNode(title: String) {
        nodes.add(Node(title = title, coordinate = -globalOffset))
    }

    fun dismissDialogs() {
        shouldDisplayAddNodeDialog = false
        shouldDisplayGeneralSettingsDialog = false
        selectedNode = null
        selectedConnection = null
    }

    fun isAnyDialogDisplayed() =
        shouldDisplayAddNodeDialog || shouldDisplayGeneralSettingsDialog || selectedNode != null || selectedConnection != null
}

@Composable
fun DynamicGraph(state: GraphScreenState) {

    val transformableState =
        rememberTransformableState { zoomChange, offsetChange, rotationChange ->
            state.globalOffset += offsetChange
            state.globalScale *= zoomChange
            state.globalRotation += rotationChange
            state.dismissDialogs()
        }


    GridLayer(
        state = state.gridLayerState,
        globalScale = state.globalScale,
        globalOffset = state.globalOffset
    )


    // Main Layer
    Box(
        modifier = Modifier
            .fillMaxSize()
            .transformable(state = transformableState)
            .graphicsLayer {
                translationX = state.globalOffset.x
                translationY = state.globalOffset.y
                scaleX = state.globalScale
                scaleY = state.globalScale
                rotationZ = state.globalRotation
            }) {
        val density = LocalDensity.current
        state.nodes.forEach { node ->
            // used box composable instead of draw line function, to use modifiers
            node.connections.forEach { connection ->
                with(density) {
                    val startX = node.coordinate.x + nodeSize.toPx() / 2
                    val startY = node.coordinate.y + nodeSize.toPx() / 2
                    val endX = (connection.connectedNode?.coordinate?.x ?: 0f) + nodeSize.toPx() / 2
                    val endY = (connection.connectedNode?.coordinate?.y ?: 0f) + nodeSize.toPx() / 2

                    val lineHeight = hypot(
                        endX - startX, endY - startY
                    )  // sqrt((endX - startX).pow(2) + (endY - startY).pow(2))
                    val angle = Math.toDegrees(atan2(endY - startY, endX - startX).toDouble())
                        .toFloat() - 90f // atan2(endY - startY, endX - startX) * (180 / PI).toFloat()

                    // Node connection
                    val scope = rememberCoroutineScope()
                    NodeConnectionWidget(
                        connection = connection,
                        connectionLength = lineHeight.toDp(),
                        offset = IntOffset(startX.toInt(), startY.toInt()),
                        angle = angle,
                        isSelected = state.selectedConnection == connection,
                        onClick = {
                            scope.launch {
                                state.dismissDialogs()
                                delay(100)
                                state.selectedConnection = connection
                            }
                        })
                }
            }

            // Node
            NodeWidget(
                node = node,
                isSelected = state.selectedNode?.title == node.title,
                globalRotation = state.globalRotation,
                onClick = {
                    if (state.selectedNode == null) {
                        state.dismissDialogs()
                        state.selectedNode = node
                        return@NodeWidget
                    }

                    if (state.selectedNode?.connections?.any { it.connectedNode == node } == true) return@NodeWidget

                    state.selectedNode?.connections = state.selectedNode?.connections?.plus(
                        NodeConnection(
                            parentNode = state.selectedNode, connectedNode = node
                        )
                    ).orEmpty()
                })
        }
    }

    ActionsLayer(screenState = state, onOpenAddNodeDialog = {
        state.dismissDialogs()
        state.shouldDisplayAddNodeDialog = true
    }, onOpenGeneralSettingsDialog = {
        state.dismissDialogs()
        state.shouldDisplayGeneralSettingsDialog = true
    })

    DialogLayer(
        isVisible = state.isAnyDialogDisplayed(),
        onDismiss = state::dismissDialogs,
    ) {
        when {
            state.shouldDisplayGeneralSettingsDialog -> {

                GenericSettingsDialogContent(
                    gridLayerState = state.gridLayerState
                )

            }

            state.selectedNode != null -> {

                NodeSettingsDialogContent(selectedNode = state.selectedNode!!)

            }

            state.selectedConnection != null -> {

                NodeConnectionSettingsDialogContent(
                    selectedConnection = state.selectedConnection!!, onRemoveConnection = {
                        state.selectedConnection?.let {
                            it.parentNode?.connections =
                                it.parentNode?.connections?.minus(state.selectedConnection!!)
                                    .orEmpty()
                        }
                        state.dismissDialogs()
                    })

            }

            state.shouldDisplayAddNodeDialog -> {

                AddNodeDialog(
                    onAdd = state::addNode
                )

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DynamicGraphPrev() {
    remember {
        Node(
            title = "A"
        ).apply {
            coordinate = Offset(100f, 100f)
        }
    }
    remember {
        Node(
            title = "B"
        ).apply {
            coordinate = Offset(500f, 100f)
        }
    }
    remember {
        Node(
            title = "C"
        ).apply {
            coordinate = Offset(300f, 500f)
        }
    }

    DynamicGraph(state = GraphScreenState())
}