package org.wasmium.wir.ast.visitor

import org.wasmium.wir.ast.WirData
import org.wasmium.wir.ast.WirElement
import org.wasmium.wir.ast.WirFunction
import org.wasmium.wir.ast.WirGlobal
import org.wasmium.wir.ast.WirLocal
import org.wasmium.wir.ast.WirMemory
import org.wasmium.wir.ast.WirModule
import org.wasmium.wir.ast.WirTable
import org.wasmium.wir.ast.expressions.BlockExpression
import org.wasmium.wir.ast.expressions.BranchExpression
import org.wasmium.wir.ast.expressions.BranchTableExpression
import org.wasmium.wir.ast.expressions.CallExpression
import org.wasmium.wir.ast.expressions.CallIndirectExpression
import org.wasmium.wir.ast.expressions.ConditionalExpression
import org.wasmium.wir.ast.expressions.ConstantFloat32Expression
import org.wasmium.wir.ast.expressions.ConstantFloat64Expression
import org.wasmium.wir.ast.expressions.ConstantInt32Expression
import org.wasmium.wir.ast.expressions.ConstantInt64Expression
import org.wasmium.wir.ast.expressions.ConvertExpression
import org.wasmium.wir.ast.expressions.DemoteExpression
import org.wasmium.wir.ast.expressions.DropExpression
import org.wasmium.wir.ast.expressions.FloatBinaryExpression
import org.wasmium.wir.ast.expressions.FloatUnaryExpression
import org.wasmium.wir.ast.expressions.GetGlobalExpression
import org.wasmium.wir.ast.expressions.GetLocalExpression
import org.wasmium.wir.ast.expressions.IfExceptionExpression
import org.wasmium.wir.ast.expressions.IntBinaryExpression
import org.wasmium.wir.ast.expressions.IntUnaryExpression
import org.wasmium.wir.ast.expressions.LabelExpression
import org.wasmium.wir.ast.expressions.LoadFloatExpression
import org.wasmium.wir.ast.expressions.LoadIntExpression
import org.wasmium.wir.ast.expressions.NopExpression
import org.wasmium.wir.ast.expressions.PromoteExpression
import org.wasmium.wir.ast.expressions.ReinterpretExpression
import org.wasmium.wir.ast.expressions.RethrowExpression
import org.wasmium.wir.ast.expressions.ReturnExpression
import org.wasmium.wir.ast.expressions.SelectExpression
import org.wasmium.wir.ast.expressions.SetGlobalExpression
import org.wasmium.wir.ast.expressions.SetLocalExpression
import org.wasmium.wir.ast.expressions.StoreFloatExpression
import org.wasmium.wir.ast.expressions.StoreIntExpression
import org.wasmium.wir.ast.expressions.TruncateExpression
import org.wasmium.wir.ast.expressions.TryExpression
import org.wasmium.wir.ast.expressions.UnreachableExpression

public class WirVisitorVoidAdapter(private val failWhenUnhandled: Boolean) : WirVisitorVoid {
    private fun defaultVisit() {
        check (!failWhenUnhandled)
    }

    public override fun visitConvert(convertExpression: ConvertExpression) {
        defaultVisit()
    }

    public override fun visitPromote(promoteExpression: PromoteExpression) {
        defaultVisit()
    }

    public override fun visitDemote(demoteExpression: DemoteExpression) {
        defaultVisit()
    }

    public override fun visitReinterpret(reinterpretExpression: ReinterpretExpression) {
        defaultVisit()
    }

    public override fun visitTruncate(truncateExpression: TruncateExpression) {
        defaultVisit()
    }

    public override fun visitReturn(returnExpression: ReturnExpression) {
        defaultVisit()
    }

    public override fun visitDrop(dropExpression: DropExpression) {
        defaultVisit()
    }

    public override fun visitNop(nopExpression: NopExpression) {
        defaultVisit()
    }

    public override fun visitUnreachable(unreachableExpression: UnreachableExpression) {
        defaultVisit()
    }

    public override fun visitFloatUnary(floatUnaryExpression: FloatUnaryExpression) {
        defaultVisit()
    }

    public override fun visitIntUnary(intUnaryExpression: IntUnaryExpression) {
        defaultVisit()
    }

    public override fun visitFloatBinary(floatBinaryExpression: FloatBinaryExpression) {
        defaultVisit()
    }

    public override fun visitIntBinary(intBinaryExpression: IntBinaryExpression) {
        defaultVisit()
    }

    public override fun visitConstFloat32(constantFloat32Expression: ConstantFloat32Expression) {
        defaultVisit()
    }

    public override fun visitConstFloat64(constantFloat64Expression: ConstantFloat64Expression) {
        defaultVisit()
    }

    public override fun visitConstInt32(constantInt32Expression: ConstantInt32Expression) {
        defaultVisit()
    }

    public override fun visitConstInt64(constantInt64Expression: ConstantInt64Expression) {
        defaultVisit()
    }

    public override fun visitCall(callExpression: CallExpression) {
        defaultVisit()
    }

    public override fun visitLoadFloat(loadFloatExpression: LoadFloatExpression) {
        defaultVisit()
    }

    public override fun visitLoadInt(loadIntExpression: LoadIntExpression) {
        defaultVisit()
    }

    public override fun visitSetGlobal(setGlobalExpression: SetGlobalExpression) {
        defaultVisit()
    }

    public override fun visitGetGlobal(getGlobalExpression: GetGlobalExpression) {
        defaultVisit()
    }

    public override fun visitGetLocal(getLocalExpression: GetLocalExpression) {
        defaultVisit()
    }

    public override fun visitSetLocal(setLocalExpression: SetLocalExpression) {
        defaultVisit()
    }

    public override fun visitStoreInt32(storeIntExpression: StoreIntExpression) {
        defaultVisit()
    }

    public override fun visitStoreFloat32(storeFloatExpression: StoreFloatExpression) {
        defaultVisit()
    }

    public override fun visitSelect(selectExpression: SelectExpression) {
        defaultVisit()
    }

    public override fun visitBlock(blockExpression: BlockExpression) {
        defaultVisit()
    }

    public override fun visitBranchTable(branchTableExpression: BranchTableExpression) {
        defaultVisit()
    }

    public override fun visitBranch(branchExpression: BranchExpression) {
        defaultVisit()
    }

    public override fun visitLabel(labelExpression: LabelExpression) {
        defaultVisit()
    }

    public override fun visitConditional(conditionalExpression: ConditionalExpression) {
        defaultVisit()
    }

    public override fun visitCallIndirect(callIndirectExpression: CallIndirectExpression) {
        defaultVisit()
    }

    public override fun visitRethrow(rethrowExpression: RethrowExpression) {
        defaultVisit()
    }

    public override fun visitTry(tryExpression: TryExpression) {
        defaultVisit()
    }

    public override fun visitIfException(ifExceptionExpression: IfExceptionExpression) {
        defaultVisit()
    }

    public override fun visitTable(node: WirTable) {
        defaultVisit()
    }

    public override fun visitModule(node: WirModule) {
        defaultVisit()
    }

    public override fun visitMemory(node: WirMemory) {
        defaultVisit()
    }

    public override fun visitLocal(node: WirLocal) {
        defaultVisit()
    }

    public override fun visitGlobal(node: WirGlobal) {
        defaultVisit()
    }

    public override fun visitFunction(node: WirFunction) {
        defaultVisit()
    }

    public override fun visitElement(node: WirElement) {
        defaultVisit()
    }

    public override fun visitData(node: WirData) {
        defaultVisit()
    }
}
