package graphql.normalized

import graphql.TestUtil
import graphql.language.Field
import graphql.language.OperationDefinition
import spock.lang.Specification

class ExecutableNormalizedOperationFactoryDelegatesTest extends Specification {

    def schema = TestUtil.schema("""
        type Query { a : A }
        type A { b : B c : String }
        type B { d : String }
    """)

    def "the old instance methods collect the same fields as the factory"() {
        def document = TestUtil.toDocument("{ a { b { d } c } a { c } }")
        def operation = document.getDefinitionsOfType(OperationDefinition)[0]
        def parameters = FieldCollectorNormalizedQueryParams.newParameters().schema(schema).build()
        def factory = new ExecutableNormalizedOperationFactory()

        when:
        def top = factory.collectFromOperation(parameters, operation, schema.getQueryType())
        def a = top.children[0]
        def next = factory.collectFromMergedField(parameters, a, top.normalizedFieldToAstFields.get(a), 2)

        then:
        top.children*.fieldName == ["a"]
        top.normalizedFieldToAstFields.get(a)*.field*.name == ["a", "a"]
        next.children*.fieldName == ["b", "c"]
    }
}
