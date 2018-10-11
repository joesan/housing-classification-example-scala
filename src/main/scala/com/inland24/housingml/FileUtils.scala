package com.inland24.housingml

import java.io._

import org.apache.commons.compress.archivers.tar.{TarArchiveEntry, TarArchiveInputStream}
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream

import scala.util.Try


object FileUtils {

  private val BUFFER_SIZE: Int = 4096

  def extractTGZ(from: File, outputPath: String): Unit = {
    var fileCount: Int = 0
    var dirCount: Int = 0
    print("Extracting files")
    Try {
      val tais = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(from))))
      try {
        var entry: TarArchiveEntry = tais.getNextEntry.asInstanceOf[TarArchiveEntry]

        /** Read the tar entries using the getNextEntry method **/
        while (entry != null) {
          //println("Extracting file: " + entry.getName());

          //Create directories as required
          if (entry.isDirectory) {
            new File(outputPath + entry.getName).mkdirs
            dirCount += 1
          } else {
            val data = new Array[Byte](BUFFER_SIZE)
            val fos = new FileOutputStream(outputPath + entry.getName)
            val dest = new BufferedOutputStream(fos, BUFFER_SIZE)

            var count = tais.read(data, 0, BUFFER_SIZE)

            while (count != -1) {
              dest.write(data, 0, count)
              count = tais.read(data, 0, BUFFER_SIZE)
            }
            dest.close()
            fileCount += 1
          }
          if (fileCount % 1000 == 0) print(".")
          entry = tais.getNextEntry.asInstanceOf[TarArchiveEntry]
        }
      } finally {
        if (tais != null) tais.close()
      }
    }
    println("\n" + fileCount + " files and " + dirCount + " directories extracted to: " + outputPath)
  }

  def extractTGZ(filePath: String, outputPath: String): Unit =
    extractTGZ(new File(filePath), outputPath)
}