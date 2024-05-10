package org.wasmium.wasm.binary

import kotlinx.io.asSink
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.wasmium.wasm.binary.reader.ModuleReader
import org.wasmium.wasm.binary.reader.ReaderOptions
import org.wasmium.wasm.binary.tree.SectionKind.CODE
import org.wasmium.wasm.binary.tree.SectionKind.CUSTOM
import org.wasmium.wasm.binary.tree.SectionKind.DATA
import org.wasmium.wasm.binary.tree.SectionKind.DATA_COUNT
import org.wasmium.wasm.binary.tree.SectionKind.ELEMENT
import org.wasmium.wasm.binary.tree.SectionKind.EXPORT
import org.wasmium.wasm.binary.tree.SectionKind.FUNCTION
import org.wasmium.wasm.binary.tree.SectionKind.GLOBAL
import org.wasmium.wasm.binary.tree.SectionKind.IMPORT
import org.wasmium.wasm.binary.tree.SectionKind.MEMORY
import org.wasmium.wasm.binary.tree.SectionKind.START
import org.wasmium.wasm.binary.tree.SectionKind.TABLE
import org.wasmium.wasm.binary.tree.SectionKind.TYPE
import org.wasmium.wasm.binary.writer.ModuleWriter
import org.wasmium.wasm.binary.writer.WriterContext
import org.wasmium.wasm.binary.writer.WriterOptions
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ModuleWriterTest {

    @Test
    fun validWasmReadAndWriteSuccess() {
        val file = "src/jvmTest/resources/hello.wasm"
        val generatedFile = "src/jvmTest/resources/hello-generated.wasm"

        FileInputStream(file).use {
            val source = WasmBinaryReader(SourceBinaryReader(it.asSource().buffered()))

            val readerOptions = ReaderOptions {
                debugNames(true)
                skipSections(listOf())

                features {
                    enableAll()
                }
            }

            FileOutputStream(generatedFile).use {
                val sink = it.asSink().buffered()
                val wasmSink = WasmBinaryWriter(SinkBinaryWriter(sink))

                val writerOptions = WriterOptions {
                    debugNames(true)
                    canonical(false)
                    features {
                        enableAll()
                    }
                }
                val writerContext = WriterContext(writerOptions, wasmSink)
                ModuleReader(readerOptions).readModule(source, ModuleWriter(writerContext))

                sink.flush()
            }
        }

        assertTrue(File(generatedFile).exists())
    }

    @Test
    fun validWasmRegenerateSuccess() {
        val file = "src/jvmTest/resources/hello.wasm"
        val generatedFile = "src/jvmTest/resources/hello-generated.wasm"
        val rewriteFile = "src/jvmTest/resources/hello-generated-rewrite.wasm"

        val readerOptions = ReaderOptions {
            debugNames(true)
            skipSections(listOf())

            features {
                enableAll()
            }
        }

        FileInputStream(file).use {
            val source = WasmBinaryReader(SourceBinaryReader(it.asSource().buffered()))

            FileOutputStream(generatedFile).use {
                val sink = it.asSink().buffered()
                val wasmSink = WasmBinaryWriter(SinkBinaryWriter(sink))

                val writerOptions = WriterOptions {
                    debugNames(true)
                    canonical(false)
                    features {
                        enableAll()
                    }
                }

                val writerContext = WriterContext(writerOptions, wasmSink)
                ModuleReader(readerOptions).readModule(source, ModuleWriter(writerContext))

                sink.flush()
            }
        }

        FileInputStream(generatedFile).use {
            val source = WasmBinaryReader(SourceBinaryReader(it.asSource().buffered()))

            FileOutputStream(rewriteFile).use {
                val sink = it.asSink().buffered()
                val wasmSink = WasmBinaryWriter(SinkBinaryWriter(sink))

                val writerOptions = WriterOptions {
                    debugNames(true)
                    canonical(false)
                    features {
                        enableAll()
                    }
                }

                val writerContext = WriterContext(writerOptions, wasmSink)
                ModuleReader(readerOptions).readModule(source, ModuleWriter(writerContext))

                sink.flush()
            }
        }

        assertContentEquals(File(generatedFile).readBytes(), File(rewriteFile).readBytes())
    }
}
