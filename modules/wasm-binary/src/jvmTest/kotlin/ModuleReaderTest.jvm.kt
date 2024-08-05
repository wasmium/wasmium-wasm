package org.wasmium.wasm.binary

import kotlinx.io.asSource
import kotlinx.io.buffered
import org.wasmium.wasm.binary.reader.ModuleReader
import org.wasmium.wasm.binary.reader.ReaderOptions
import org.wasmium.wasm.binary.tree.ModuleNode
import org.wasmium.wasm.binary.tree.WasmVersion
import java.io.File
import java.io.FileInputStream
import kotlin.test.Test
import kotlin.test.assertEquals

class ModuleReaderTest {

    @Test
    fun validWasmReadSuccess() {
//        val file = "src/jvmTest/resources/hello.wasm"
        val file = "../../repository/bots.wasm"

        println(File(".").absolutePath)
        val module = ModuleNode()
        FileInputStream(file).use {
            val source = WasmBinaryReader(SourceBinaryReader(it.asSource().buffered()))

            val readerOptions = ReaderOptions {
                debugNames(true)
                skipSections(listOf())
                features {
                    enableAll()
                }
            }

            ModuleReader(readerOptions).readModule(source, module)
        }

        assertEquals(WasmVersion.V1.version, module.version)
    }
}
