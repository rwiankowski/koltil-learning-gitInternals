package gitinternals

import java.io.FileInputStream
import java.time.Instant
import java.time.ZoneId
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
        inflatedInput += it.toInt().toChar()
    }

    //println(inflatedInput)

    val inflatedInputSplit = inflatedInput.split('\u0000')
    val objectType = inflatedInputSplit[0].split(' ')[0]
    var output = "*${objectType.uppercase()}*"

    when(objectType) {
        "commit" -> {
            val lines = inflatedInputSplit[1].split("\n")
            lines.forEach { line ->
                when {
                    (line.matches(("""tree .*""").toRegex())) -> {
                        val body = line.split(' ')[1]
                        output += "\ntree: $body"
                    }

                    (line.matches(("""parent .*""").toRegex())) -> {
                        val body = line.split(' ')[1]
                        output += if (output.contains("parents:")) " | $body"
                        else "\nparents: $body"
                    }

                    (line.matches(("""author .*""").toRegex())) -> {
                        val body = line.split(' ')[1]
                        val contact = line.split(' ')[2]
                        val contactClean = contact.substring(1, contact.length - 1)
                        val epochSecond = line.split(' ')[3]
                        val zoneId = ZoneId.of(line.split(' ')[4])
                        val instant = Instant.ofEpochSecond(epochSecond.toLong()).atZone(zoneId)
                        output += "\nauthor: $body $contactClean original timestamp: ${instant.toLocalDate()} ${instant.toLocalTime()} ${instant.zone}"
                    }

                    (line.matches(("""committer .*""").toRegex())) -> {
                        val body = line.split(' ')[1]
                        val contact = line.split(' ')[2]
                        val contactClean = contact.substring(1, contact.length - 1)
                        val epochSecond = line.split(' ')[3]
                        val zoneId = ZoneId.of(line.split(' ')[4])
                        val instant = Instant.ofEpochSecond(epochSecond.toLong()).atZone(zoneId)
                        output += "\ncommitter: $body $contactClean commit timestamp: ${instant.toLocalDate()} ${instant.toLocalTime()} ${instant.zone}"
                    }

                    (line.matches(("""""").toRegex())) -> {
                        if(!output.contains("commit message:")) output += "\ncommit message:"
                    }

                    else -> {
                        output += "\n$line"
                    }
                }
            }
        }
        "blob" -> {
            output += "\n${inflatedInputSplit[1]}"
        }
        else -> return
    }

    println(output)
}
