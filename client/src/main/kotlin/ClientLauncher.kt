import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubDarkContrastIJTheme
import java.applet.Applet
import java.applet.AppletContext
import java.applet.AppletStub
import java.awt.Color
import java.awt.Dimension
import java.awt.GridLayout
import java.net.URL
import java.net.URLClassLoader
import javax.swing.ImageIcon
import javax.swing.JDialog
import javax.swing.JFrame

object ClientLauncher {

    @JvmStatic
    fun main(args: Array<String>) {
        start()
    }

    const val CLIENT_TITLE = "RSBox Client"
    const val DEFAULT_WORLD = "301"
    const val CACHE_DIR = "jagexcache"
    //const val SERVER_IP = "oldschool1.runescape.com"
    const val SERVER_IP = "127.0.0.1"
    //val RSA_MODULUS = "837aa9f02cc04c9b343d5266504f7fb5b16a966ed0c040361d86894e88a6f50d1fd0f1e7d8ab3c0d44eff369029a1e6e13a1f0890ba4ac0dc315e76e6f8c38fb9df4c6d04831f525e1dd4b7e1c38cc3c44888a7e9d3ae2120f12877bdd671dffcb137690ab80971e9af17d19644615b37eebd59b204317b2445bcebe6b66949d"
    val RSA_MODULUS = ClientLauncher::class.java.getResourceAsStream("/modulus.txt")?.bufferedReader()?.readText() ?: error("Server has not been setup. Run the 'setup server' gradle task first.")

    private val params = hashMapOf(
            "title" to "Old School RuneScape",
            "adverturl" to "http://www.runescape.com/g" to "oldscape/bare_advert.ws",
            "codebase" to "http://$SERVER_IP/",
            "cachedir" to CACHE_DIR,
            "storebase" to "0",
            "initial_jar" to "gamepack_7673457.jar",
            "initial_class" to "client.class",
            "viewerversion" to "124",
            "win_sub_version" to "1",
            "mac_sub_version" to "2",
            "other_sub_version" to "2",
            "download" to "2312255",
            "window_preferredwidth" to "800",
            "window_preferredheight" to "600",
            "advert_height" to "96",
            "applet_minwidth" to "765",
            "applet_minheight" to "503",
            "applet_maxwidth" to "5760",
            "applet_maxheight" to "2160",
            "3" to "false",
            "13" to ".runescape.com",
            "22" to "https://auth.runescape.com/",
            "10" to "5",
            "11" to "https://auth.jagex.com/",
            "19" to "196515767263-1oo20deqm6edn7ujlihl6rpadk9drhva.apps.googleusercontent.com",
            "4" to "1",
            "6" to "0",
            "2" to "https://payments.jagex.com/",
            "15" to "0",
            "17" to "http://$SERVER_IP/world_list.ws",
            "12" to "301",
            "21" to "0",
            "8" to "true",
            "25" to "217",
            "18" to "",
            "20" to "https://social.auth.jagex.com/",
            "9" to "ElZAIrq5NpKN6D3mDdihco3oPeYN2KFy2DCquj7JMmECPmLrDP3Bnw",
            "16" to "false",
            "5" to "0",
            "14" to "0",
            "7" to "0",
            "28" to "https://account.jagex.com/"
    )

    private lateinit var applet: Applet
    private lateinit var frame: JFrame

    private fun start() {
        val applet = Client()
        applet.background = Color.BLACK
        applet.preferredSize = Dimension(params["applet_minwidth"]!!.toInt(), params["applet_minheight"]!!.toInt())
        applet.size = applet.preferredSize
        applet.layout = null
        applet.setStub(applet.createStub())
        applet.isVisible = true
        applet.init()
        applet.start()

        JFrame.setDefaultLookAndFeelDecorated(true)
        JDialog.setDefaultLookAndFeelDecorated(true)
        FlatGitHubDarkContrastIJTheme.setup()

        frame = JFrame(CLIENT_TITLE)
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.iconImage = ClientLauncher::class.java.getResource("/rsbox.png")!!.let { ImageIcon(it).image }
        frame.layout = GridLayout(1, 0)
        frame.add(applet)
        frame.pack()
        frame.setLocationRelativeTo(null)
        frame.isVisible = true
        frame.minimumSize = frame.size
    }

    private fun Applet.createStub() = object : AppletStub {
        override fun getCodeBase(): URL = URL(params["codebase"])
        override fun getDocumentBase(): URL = URL(params["codebase"])
        override fun isActive(): Boolean = true
        override fun getAppletContext(): AppletContext? = null
        override fun getParameter(name: String): String? = params[name]
        override fun appletResize(width: Int, height: Int) { this@createStub.size = Dimension(width, height) }
    }

}