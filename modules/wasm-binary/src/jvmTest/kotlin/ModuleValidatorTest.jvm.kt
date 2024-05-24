package org.wasmium.wasm.binary

import kotlinx.io.asSource
import kotlinx.io.buffered
import org.wasmium.wasm.binary.reader.ModuleReader
import org.wasmium.wasm.binary.reader.ReaderOptions
import org.wasmium.wasm.binary.validator.ModuleValidator
import org.wasmium.wasm.binary.validator.ValidatorOptions
import java.io.File
import java.io.FileInputStream
import kotlin.test.Test

class ModuleValidatorTest {

    @Test
    fun validWasmValidateSuccess() {
        println(File(".").absolutePath)
        val file = "../../repository/bots.wasm"

        FileInputStream(file).use {
            val source = WasmBinaryReader(SourceBinaryReader(it.asSource().buffered()))

            val readerOptions = ReaderOptions {
                debugNames(true)
                skipSections(listOf())

                features {
                    enableAll()
                }
            }

            val validatorOptions = ValidatorOptions {
                features {
                    enableAll()
                }
            }
            ModuleReader(readerOptions).readModule(source, ModuleValidator(options = validatorOptions))
        }
    }
}
