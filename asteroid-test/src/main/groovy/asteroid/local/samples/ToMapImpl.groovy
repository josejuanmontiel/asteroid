package asteroid.local.samples

import asteroid.A
import asteroid.Phase
import asteroid.AbstractLocalTransformation

import groovy.transform.CompileStatic

import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.MapExpression
import org.codehaus.groovy.ast.expr.MapEntryExpression

@CompileStatic
@Phase(Phase.LOCAL.INSTRUCTION_SELECTION)
class ToMapImpl extends AbstractLocalTransformation<ToMap, ClassNode> {

    @Override
    void doVisit(AnnotationNode annotation, ClassNode node) {
        List<FieldNode> instanceFields   = A.UTIL.CLASS.getInstancePropertyFields(node)
        List<MapEntryExpression> entries = instanceFields.collect(this.&fieldToMapEntry)
        MapExpression mapExpression      = new MapExpression(entries)
        MethodNode methodNode            = getToMapMethod(mapExpression)

        A.UTIL.CLASS.addMethod(node, methodNode)
    }

    private MapEntryExpression fieldToMapEntry(final FieldNode field) {
        return new MapEntryExpression(A.EXPR.constX(field.name), A.EXPR.varX(field.name))
    }

    private MethodNode getToMapMethod(final MapExpression map) {
        return A.NODES
            .method('toMap')
            .modifiers(A.ACC.ACC_PUBLIC)
            .returnType(Map)
            .code(A.STMT.returnS(map))
            .build()
    }

}
