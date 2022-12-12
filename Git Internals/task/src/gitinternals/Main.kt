package gitinternals

import java.io.FileInputStream
import java.util.zip.InflaterInputStream

fun main() {

    println("Enter .git directory location:")
    val inputPath = readln()
    println("Enter git object hash:")
    val inputHash = readln()

    val filePath = inputPath + "/objects/" + inputHash.substring(0, 2) + "/" + inputHash.substring(2)
    val fileInputStream = FileInputStream(filePath)
    val inflaterInputStream = InflaterInputStream(fileInputStream)

    var inflatedInput = ""

    inflaterInputStream.readAllBytes().map {
        inflatedInput += it.toChar()
    }
    val firstLine = inflatedInput.split('\u0000')[0].split(' ')
    println("type:${firstLine[0]} length:${firstLine[1]}")
}
