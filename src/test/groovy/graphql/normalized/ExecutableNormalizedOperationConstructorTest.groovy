package graphql.normalized

import graphql.TestUtil
import graphql.execution.CoercedVariables
import spock.lang.Specification

class ExecutableNormalizedOperationConstructorTest extends Specification {

    def "the 18.5 constructor derives the field count and depth from the fields"() {
        def schema = TestUtil.schema("""
            type Query { a : A }
            type A { b : B c : String }
            type B { d : String }
        """)
        def document = TestUtil.toDocument("{ a { b { d } c } }")
        def built = ExecutableNormalizedOperationFactory.createExecutableNormalizedOperation(schema, document, null, CoercedVariables.emptyVariables())

        when:
        def eno = new ExecutableNormalizedOperation(built.getOperation(), built.getOperationName(), built.getTopLevelFields(),
                built.getFieldToNormalizedField(), built.getNormalizedFieldToMergedField(), built.getCoordinatesToNormalizedFields())

        then:
        eno.getOperationFieldCount() == built.getOperationFieldCount()
        eno.getOperationDepth() == built.getOperationDepth()
        eno.getOperationFieldCount() == 4
        eno.getOperationDepth() == 3
    }
}
