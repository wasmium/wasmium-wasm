package org.wasmium.wasm.binary

import kotlinx.io.asSource
import kotlinx.io.buffered
import org.wasmium.wasm.binary.reader.ModuleReader
import org.wasmium.wasm.binary.reader.ReaderOptions
import org.wasmium.wasm.binary.verifier.ModuleVerifier
import org.wasmium.wasm.binary.verifier.VerifierOptions
import java.io.FileInputStream
import kotlin.test.Test

class ModuleVerifierTest {

    @Test
    fun validWasmVerifySuccess() {
        val file = "src/jvmTest/resources/hello.wasm"

        FileInputStream(file).use {
            val source = WasmBinaryReader(SourceBinaryReader(it.asSource().buffered()))

            val readerOptions = ReaderOptions {
                debugNames(true)
                skipSections(listOf())

                features {
                    enableAll()
                }
            }

            val verifierOptions = VerifierOptions {
                features {
                    enableAll()
                }
            }
            ModuleReader(readerOptions).readModule(source, ModuleVerifier(options = verifierOptions))
        }
    }
}
