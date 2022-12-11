package gitinternals

import java.io.FileInputStream
import java.util.zip.InflaterInputStream

fun main() {

    println("Enter git object location:")
    val inputPath = readln()
    val fileInputStream = FileInputStream(inputPath)
    val inflaterInputStream = InflaterInputStream(fileInputStream)

    inflaterInputStream.readAllBytes().map {

        if ( it.toChar() == '\u0000') println()
        else print(it.toChar()) }

}
