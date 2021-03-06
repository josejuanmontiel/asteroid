package asteroid.local.collector

import asteroid.A
import asteroid.Phase
import asteroid.AbstractLocalTransformation

import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.ast.expr.MapExpression
import org.codehaus.groovy.ast.expr.MapEntryExpression

import groovy.json.JsonOutput
import groovy.transform.CompileStatic

@CompileStatic
@Phase(Phase.LOCAL.SEMANTIC_ANALYSIS)
class ToJsonImpl extends AbstractLocalTransformation<ToJson,ClassNode> {

    @Override
    void doVisit(AnnotationNode annotation, ClassNode annotatedNode) {
        A.UTIL.CLASS.addMethodIfNotPresent(annotatedNode, createToJsonNode(annotatedNode))
    }

    MethodNode createToJsonNode(ClassNode classNode) {
        MapExpression map   = createMapExpression(classNode)
        Statement statement = callJsonOutput(map)

        return A.NODES.method('toJson')
            .modifiers(A.ACC.ACC_PUBLIC)
            .returnType(String)
            .code(statement)
            .build()
    }

    Statement callJsonOutput(final MapExpression mapExpression) {
        return A.STMT.stmt(A.EXPR.staticCallX(JsonOutput, 'toJson', mapExpression))
    }

    MapExpression createMapExpression(final ClassNode classNode) {
        List<MapEntryExpression> entries = A.UTIL.CLASS.getInstancePropertyFields(classNode).collect(this.&toMapEntry)

        return new MapExpression(entries)
    }

    MapEntryExpression toMapEntry(final FieldNode field) {
        return new MapEntryExpression(A.EXPR.constX(field.name), A.EXPR.varX(field.name))
    }
}
