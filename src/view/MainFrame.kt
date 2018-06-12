package view

import com.sun.tools.javac.Main
import model.Board
import model.BoardEvent
import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.SwingUtilities
import javax.swing.WindowConstants

fun main(args: Array<String>) {
    MainFrame()
}

class MainFrame: JFrame(){
    private val board = Board(numRows = 16, numColumns = 30, numMines = 20)
    private val panelBoard = PanelBoard(board)

    init {
        board.onEvent(this::displayResult)
        add(panelBoard)

        setSize(600, 400)
        setLocationRelativeTo(null)
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        title = "Minefield"
        isVisible = true
    }

    private fun displayResult(boardEvent: BoardEvent){
        SwingUtilities.invokeLater {
            val msg = when(boardEvent){
                BoardEvent.VICTORY -> "YOU WON! #Congratzz :D"
                BoardEvent.DEFEATED -> "YOU LOSE... :("
            }
            JOptionPane.showMessageDialog(this, msg)
            board.restart()

            panelBoard.repaint()
            panelBoard.validate()
        }
    }
}