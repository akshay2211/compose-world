package compose.world.composables.reorderable_lazt_list_remastered

import kotlinx.coroutines.flow.StateFlow

interface ReorderableCollection <T> {
    val items : StateFlow<List<T>>
    val key: (Int, T) -> Any
    fun add(addIndex: Int, item: T)
    fun removeAt(index: Int)
    operator fun get(index: Int) : T
}