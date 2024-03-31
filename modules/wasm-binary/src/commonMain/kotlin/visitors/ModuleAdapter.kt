package org.wasmium.wasm.binary.visitors

public open class ModuleAdapter(protected val delegate: ModuleVisitor) : ModuleVisitor by delegate
