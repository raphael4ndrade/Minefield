package model

enum class FieldEvent {
    OPENED, MARKED, UNMARKED, EXPLOSION, RESTART
}

data class Field(val row: Int, val column: Int) {
    private val neighbors = ArrayList<Field>()
    private val callbacks = ArrayList<(Field, FieldEvent) -> Unit>()

    var isMarked: Boolean = false
    var isOpened: Boolean = false
    var isMined: Boolean = false

    val isUnmarked: Boolean get() = !isMarked
    val isClosed: Boolean get() = !isOpened
    val isSafe: Boolean get() = !isMined

    val isDone: Boolean get() = isSafe && isOpened || isMined && isMarked
    val numNeighborsMined: Int get() = neighbors.filter { it.isMined }.size
    val safeZone: Boolean get() = neighbors.map { it.isSafe }.reduce { result, safe -> result && safe }


    fun addNeighbor(neighbor: Field) {
        neighbors.add(neighbor)
    }

    fun onEvent(callback: (Field, FieldEvent) -> Unit) {
        callbacks.add(callback)
    }

    fun open() {
        if (isClosed) {
            isOpened = true
            if (isMined) {
                callbacks.forEach { it(this, FieldEvent.EXPLOSION) }
            } else {
                callbacks.forEach { it(this, FieldEvent.OPENED) }
                neighbors.filter { it.isClosed && it.isSafe && safeZone }.forEach { it.open() }
            }
        }
    }

    fun alterMark() {
        if (isClosed) {
            isMarked = !isMarked
            val event = if (isMarked) FieldEvent.MARKED else FieldEvent.UNMARKED
            callbacks.forEach { it(this, event) }
        }
    }

    fun mineIt() {
        isMined = true
    }

    fun restart() {
        isOpened = false
        isMined = false
        isMarked = false
        callbacks.forEach { it(this, FieldEvent.RESTART) }
    }
}