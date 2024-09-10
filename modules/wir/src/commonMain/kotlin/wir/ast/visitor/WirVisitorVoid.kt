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
    public fun visitConvert(expression: ConvertExpression)

    public fun visitPromote(expression: PromoteExpression)

    public fun visitDemote(expression: DemoteExpression)

    public fun visitReinterpret(expression: ReinterpretExpression)

    public fun visitTruncate(expression: TruncateExpression)

    public fun visitReturn(expression: ReturnExpression)

    public fun visitDrop(expression: DropExpression)

    public fun visitNop(expression: NopExpression)

    public fun visitUnreachable(expression: UnreachableExpression)

    public fun visitFloatUnary(expression: FloatUnaryExpression)

    public fun visitIntUnary(expression: IntUnaryExpression)

    public fun visitFloatBinary(expression: FloatBinaryExpression)

    public fun visitIntBinary(expression: IntBinaryExpression)

    public fun visitConstFloat32(expression: ConstantFloat32Expression)

    public fun visitConstFloat64(expression: ConstantFloat64Expression)

    public fun visitConstInt32(expression: ConstantInt32Expression)

    public fun visitConstInt64(expression: ConstantInt64Expression)

    public fun visitCall(expression: CallExpression)

    public fun visitLoadFloat(expression: LoadFloatExpression)

    public fun visitLoadInt(expression: LoadIntExpression)

    public fun visitSetGlobal(expression: SetGlobalExpression)

    public fun visitGetGlobal(expression: GetGlobalExpression)

    public fun visitGetLocal(expression: GetLocalExpression)

    public fun visitSetLocal(expression: SetLocalExpression)

    public fun visitStoreInt32(expression: StoreIntExpression)

    public fun visitStoreFloat32(expression: StoreFloatExpression)

    public fun visitSelect(expression: SelectExpression)

    public fun visitBlock(expression: BlockExpression)

    public fun visitBranchTable(expression: BranchTableExpression)

    public fun visitBranch(expression: BranchExpression)

    public fun visitLabel(expression: LabelExpression)

    public fun visitConditional(expression: ConditionalExpression)

    public fun visitCallIndirect(expression: CallIndirectExpression)

    public fun visitRethrow(expression: RethrowExpression)

    public fun visitTry(expression: TryExpression)

    public fun visitIfException(expression: IfExceptionExpression)

    public fun visitTable(node: WirTable)

    public fun visitModule(node: WirModule)

    public fun visitMemory(node: WirMemory)

    public fun visitLocal(node: WirLocal)

    public fun visitGlobal(node: WirGlobal)

    public fun visitFunction(node: WirFunction)

    public fun visitElement(node: WirElement)

    public fun visitData(node: WirData)
}
