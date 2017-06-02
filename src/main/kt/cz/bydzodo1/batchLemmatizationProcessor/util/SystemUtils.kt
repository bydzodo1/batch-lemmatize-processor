package cz.bydzodo1.batchLemmatizationProcessor.util

class SystemUtils {
    companion object {
        private val OS = System.getProperty("os.name").toLowerCase()

        fun isWindows(): Boolean {
            return OS.indexOf("win") >= 0
        }

        fun isMac(): Boolean {
            return OS.indexOf("mac") >= 0
        }

        fun isUnix(): Boolean {
            return OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0
        }
    }
}