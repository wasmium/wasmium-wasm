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

public interface WirVisitorVoid {
    public fun visitConvert(convertExpression: ConvertExpression)

    public fun visitPromote(promoteExpression: PromoteExpression)

    public fun visitDemote(demoteExpression: DemoteExpression)

    public fun visitReinterpret(reinterpretExpression: ReinterpretExpression)

    public fun visitTruncate(truncateExpression: TruncateExpression)

    public fun visitReturn(returnExpression: ReturnExpression)

    public fun visitDrop(dropExpression: DropExpression)

    public fun visitNop(nopExpression: NopExpression)

    public fun visitUnreachable(unreachableExpression: UnreachableExpression)

    public fun visitFloatUnary(floatUnaryExpression: FloatUnaryExpression)

    public fun visitIntUnary(intUnaryExpression: IntUnaryExpression)

    public fun visitFloatBinary(floatBinaryExpression: FloatBinaryExpression)

    public fun visitIntBinary(intBinaryExpression: IntBinaryExpression)

    public fun visitConstFloat32(constantFloat32Expression: ConstantFloat32Expression)

    public fun visitConstFloat64(constantFloat64Expression: ConstantFloat64Expression)

    public fun visitConstInt32(constantInt32Expression: ConstantInt32Expression)

    public fun visitConstInt64(constantInt64Expression: ConstantInt64Expression)

    public fun visitCall(callExpression: CallExpression)

    public fun visitLoadFloat(loadFloatExpression: LoadFloatExpression)

    public fun visitLoadInt(loadIntExpression: LoadIntExpression)

    public fun visitSetGlobal(setGlobalExpression: SetGlobalExpression)

    public fun visitGetGlobal(getGlobalExpression: GetGlobalExpression)

    public fun visitGetLocal(getLocalExpression: GetLocalExpression)

    public fun visitSetLocal(setLocalExpression: SetLocalExpression)

    public fun visitStoreInt32(storeIntExpression: StoreIntExpression)

    public fun visitStoreFloat32(storeFloatExpression: StoreFloatExpression)

    public fun visitSelect(selectExpression: SelectExpression)

    public fun visitBlock(blockExpression: BlockExpression)

    public fun visitBranchTable(branchTableExpression: BranchTableExpression)

    public fun visitBranch(branchExpression: BranchExpression)

    public fun visitLabel(labelExpression: LabelExpression)

    public fun visitConditional(conditionalExpression: ConditionalExpression)

    public fun visitCallIndirect(callIndirectExpression: CallIndirectExpression)

    public fun visitRethrow(rethrowExpression: RethrowExpression)

    public fun visitTry(tryExpression: TryExpression)

    public fun visitIfException(ifExceptionExpression: IfExceptionExpression)

    public fun visitTable(node: WirTable)

    public fun visitModule(node: WirModule)

    public fun visitMemory(node: WirMemory)

    public fun visitLocal(node: WirLocal)

    public fun visitGlobal(node: WirGlobal)

    public fun visitFunction(node: WirFunction)

    public fun visitElement(node: WirElement)

    public fun visitData(node: WirData)
}
