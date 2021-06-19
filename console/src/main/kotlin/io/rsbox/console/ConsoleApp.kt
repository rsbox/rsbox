package io.rsbox.console

import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.gui2.*
import com.googlecode.lanterna.screen.TerminalScreen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import org.tinylog.kotlin.Logger

class ConsoleApp {

    val terminal = DefaultTerminalFactory().createTerminal()
    val screen = TerminalScreen(terminal)

    lateinit var window: BasicWindow private set
    lateinit var gui: MultiWindowTextGUI private set

    fun start() {
        Logger.info("Starting RSBox console application...")

        /*
         * Start the terminal screen.
         */
        screen.startScreen()

        /*
         * Create the gui window.
         */
        window = BasicWindow()
        window.setHints(listOf(Window.Hint.CENTERED))
        window.title = "RSBox Server"

        gui = MultiWindowTextGUI(screen, DefaultWindowManager(), EmptySpace(TextColor.ANSI.BLACK))
        gui.addWindowAndWait(window)
    }

    fun stop() {
        Logger.info("Stopping RSBox console application...")
        screen.stopScreen()
        window.close()
    }
}