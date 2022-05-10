package com.gsy.re2graphbackend.utils

import com.opencsv.CSVWriter
import com.opencsv.ResultSetHelperService
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.sql.ResultSet

class CSVUtils {

    companion object FileExporter {

        private fun getCSVWriter(fileName: String): CSVWriter {
            return CSVWriter(
                OutputStreamWriter(FileOutputStream(fileName), StandardCharsets.UTF_8),
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.NO_ESCAPE_CHARACTER,
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END
            ).also { writer ->
                writer.setResultService(ResultSetHelperService().also { helper ->
                    helper.setDateFormat("yyyy-MM-dd")
                    helper.setDateTimeFormat("yyyy-MM-dd")
                })
            }
        }

        fun exportResultSet(rs: ResultSet, fileName: String) {
            getCSVWriter(fileName).run {
                writeAll(rs, false)
                close()
            }
        }

        fun exportString(data: Array<String>, fileName: String) {
            getCSVWriter(fileName).run {
                writeNext(data)
                close()
            }
        }
    }
}