package model

import java.util.*

enum class BoardEvent {
    VICTORY, DEFEATED
}

class Board(val numRows: Int, val numColumns: Int, private val numMines: Int){
    private val fields = ArrayList<ArrayList<Field>>()
    private val callbacks = ArrayList<(BoardEvent) -> Unit>()

    init {
        createField()
        sortNeighbors()
        sortMines()
    }

    private fun createField() {
        for (row in 0 until numRows) {
            fields.add(ArrayList())
            for (column in 0 until numColumns) {
                val newField = Field(row, column)
                newField.onEvent(this::verifyVictoryOrDefeat)
                fields[row].add(newField)
            }
        }
    }

    private fun sortNeighbors(){
        forEachField { sortNeighbors(it) }
    }

    private fun sortNeighbors(field: Field){
        val (row, column) = field
        val rows = arrayOf(row - 1, row, row + 1)
        val columns = arrayOf(column - 1, column, column + 1)

        rows.forEach { r ->
            columns.forEach { c ->
                val current = fields.getOrNull(r)?.getOrNull(c)
                current?.takeIf { field != it }?.let { field.addNeighbor(it) }
            }
        }
    }

    private fun sortMines() {
        val generator = Random()

        var rowSorted = -1
        var columnSorted = -1
        var numMines = 0

        while (numMines < this.numMines) {
            rowSorted = generator.nextInt(numRows)
            columnSorted = generator.nextInt(numColumns)

            val fieldSorted = fields[rowSorted][columnSorted]
            if (fieldSorted.isSafe) {
                fieldSorted.mineIt()
                numMines++
            }
        }
    }

    private fun isDone(): Boolean {
        var playerWon = true
        forEachField { if (!it.isDone) playerWon = false }
        return playerWon
    }

    private fun verifyVictoryOrDefeat(field: Field, event: FieldEvent) {
        if (event == FieldEvent.EXPLOSION) {
            callbacks.forEach { it(BoardEvent.DEFEATED) }
        } else  if (isDone()){
            callbacks.forEach { it(BoardEvent.VICTORY) }
        }
    }

    fun forEachField(callback: (Field) -> Unit){
        fields.forEach { row -> row.forEach(callback) }
    }

    fun onEvent(callback: (BoardEvent) -> Unit) {
        callbacks.add(callback)
    }

    fun restart() {
        forEachField { it.restart() }
        sortMines()
    }
}