package org.talend.mdm;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.talend.mdm.commmon.metadata.MetadataRepository;
import org.talend.mdm.query.QueryParser;

import com.amalto.core.query.user.*;

/**
 *
 */
public class QueryParserTest extends TestCase {

    private MetadataRepository repository;

    public void setUp() throws Exception {
        super.setUp();
        repository = new MetadataRepository();
        repository.load(QueryParserTest.class.getResourceAsStream("metadata.xsd"));
    }

    public void testArguments() {
        try {
            QueryParser.newParser(null);
            fail();
        } catch (IllegalArgumentException e) {
            // Expected
        }
        QueryParser parser = QueryParser.newParser(new MetadataRepository());
        try {
            parser.parse(((String) null));
            fail();
        } catch (IllegalArgumentException e) {
            // Expected
        }
        try {
            parser.parse(((InputStream) null));
            fail();
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    public void testQuery1() {
        QueryParser parser = QueryParser.newParser(repository);
        Expression expression = parser.parse(QueryParserTest.class.getResourceAsStream("query1.json")); //$NON-NLS-1$
        assertTrue(expression instanceof Select);
        Select select = (Select) expression;
        assertEquals(1, select.getTypes().size());
        assertEquals("Type1", select.getTypes().get(0).getName()); //$NON-NLS-1$
    }

    public void testQuery2() {
        QueryParser parser = QueryParser.newParser(repository);
        Expression expression = parser.parse(QueryParserTest.class.getResourceAsStream("query2.json")); //$NON-NLS-1$
        assertTrue(expression instanceof Select);
        Select select = (Select) expression;
        Condition condition = select.getCondition();
        assertNotNull(condition);
        assertEquals(Compare.class, condition.getClass());
        assertEquals(Predicate.EQUALS, ((Compare) condition).getPredicate());
        assertEquals(StringConstant.class, ((Compare) condition).getRight().getClass());
        assertEquals(Field.class, ((Compare) condition).getLeft().getClass());
    }

    public void testQuery3() {
        QueryParser parser = QueryParser.newParser(repository);
        Expression expression = parser.parse(QueryParserTest.class.getResourceAsStream("query3.json")); //$NON-NLS-1$
        assertTrue(expression instanceof Select);
        Select select = (Select) expression;
        Condition condition = select.getCondition();
        assertNotNull(condition);
        assertEquals(BinaryLogicOperator.class, condition.getClass());
        assertEquals(Predicate.AND, ((BinaryLogicOperator) condition).getPredicate());
        assertEquals(Compare.class, ((BinaryLogicOperator) condition).getLeft().getClass());
        assertEquals(Compare.class, ((BinaryLogicOperator) condition).getRight().getClass());
    }

    public void testQuery4() {
        QueryParser parser = QueryParser.newParser(repository);
        Expression expression = parser.parse(QueryParserTest.class.getResourceAsStream("query4.json")); //$NON-NLS-1$
        assertTrue(expression instanceof Select);
        Select select = (Select) expression;
        Condition condition = select.getCondition();
        assertNotNull(condition);
        assertEquals(BinaryLogicOperator.class, condition.getClass());
        assertEquals(Predicate.OR, ((BinaryLogicOperator) condition).getPredicate());
        assertEquals(Compare.class, ((BinaryLogicOperator) condition).getLeft().getClass());
        assertEquals(Compare.class, ((BinaryLogicOperator) condition).getRight().getClass());
    }

    public void testQuery5() {
        QueryParser parser = QueryParser.newParser(repository);
        Expression expression = parser.parse(QueryParserTest.class.getResourceAsStream("query5.json")); //$NON-NLS-1$
        assertTrue(expression instanceof Select);
        Select select = (Select) expression;
        Condition condition = select.getCondition();
        assertNotNull(condition);
        assertEquals(BinaryLogicOperator.class, condition.getClass());
        assertEquals(Predicate.OR, ((BinaryLogicOperator) condition).getPredicate());
        assertEquals(Compare.class, ((BinaryLogicOperator) condition).getLeft().getClass());
        assertEquals(BinaryLogicOperator.class, ((BinaryLogicOperator) condition).getRight().getClass());
        assertEquals(Predicate.AND, ((BinaryLogicOperator) ((BinaryLogicOperator) condition).getRight()).getPredicate());
    }

    public void testQuery6() {
        QueryParser parser = QueryParser.newParser(repository);
        Expression expression = parser.parse(QueryParserTest.class.getResourceAsStream("query6.json")); //$NON-NLS-1$
        assertTrue(expression instanceof Select);
        Select select = (Select) expression;
        assertEquals(1, select.getSelectedFields().size());
        assertEquals(Field.class, select.getSelectedFields().get(0).getClass());
    }

    public void testQuery7() {
        QueryParser parser = QueryParser.newParser(repository);
        Expression expression = parser.parse(QueryParserTest.class.getResourceAsStream("query7.json")); //$NON-NLS-1$
        assertTrue(expression instanceof Select);
        Select select = (Select) expression;
        assertEquals(2, select.getSelectedFields().size());
        assertEquals(Field.class, select.getSelectedFields().get(0).getClass());
        assertEquals(Alias.class, select.getSelectedFields().get(1).getClass());
    }

    public void testQuery8() {
        QueryParser parser = QueryParser.newParser(repository);
        Expression expression = parser.parse(QueryParserTest.class.getResourceAsStream("query8.json")); //$NON-NLS-1$
        assertTrue(expression instanceof Select);
        Select select = (Select) expression;
        Condition condition = select.getCondition();
        final List<Predicate> metPredicates = new ArrayList<Predicate>();
        condition.accept(new VisitorAdapter<Object>() {

            @Override
            public Object visit(BinaryLogicOperator condition) {
                condition.getLeft().accept(this);
                condition.getRight().accept(this);
                return null;
            }

            @Override
            public Object visit(UnaryLogicOperator condition) {
                condition.getCondition().accept(this);
                return null;
            }

            @Override
            public Object visit(Compare condition) {
                metPredicates.add(condition.getPredicate());
                return null;
            }
        });
        assertEquals(7, metPredicates.size());
        assertTrue(metPredicates.contains(Predicate.EQUALS));
        assertTrue(metPredicates.contains(Predicate.GREATER_THAN));
        assertTrue(metPredicates.contains(Predicate.GREATER_THAN_OR_EQUALS));
        assertTrue(metPredicates.contains(Predicate.LOWER_THAN));
        assertTrue(metPredicates.contains(Predicate.LOWER_THAN_OR_EQUALS));
        assertTrue(metPredicates.contains(Predicate.CONTAINS));
        assertTrue(metPredicates.contains(Predicate.STARTS_WITH));
    }

    public void testQuery9() {
        QueryParser parser = QueryParser.newParser(repository);
        Expression expression = parser.parse(QueryParserTest.class.getResourceAsStream("query9.json")); //$NON-NLS-1$
        assertTrue(expression instanceof Select);
        Select select = (Select) expression;
        Condition condition = select.getCondition();
        assertNotNull(condition);
        assertEquals(UnaryLogicOperator.class, condition.getClass());
        assertEquals(Predicate.NOT, ((UnaryLogicOperator) condition).getPredicate());
        assertEquals(Compare.class, ((UnaryLogicOperator) condition).getCondition().getClass());
        assertEquals(Field.class, ((Compare) ((UnaryLogicOperator) condition).getCondition()).getLeft().getClass());
    }

    public void testQuery10() {
        QueryParser parser = QueryParser.newParser(repository);
        Expression expression = parser.parse(QueryParserTest.class.getResourceAsStream("query10.json")); //$NON-NLS-1$
        assertTrue(expression instanceof Select);
        Select select = (Select) expression;
        assertEquals(2, select.getJoins().size());
        assertEquals("fk2", select.getJoins().get(0).getLeftField().getFieldMetadata().getName());
        assertEquals("fk3", select.getJoins().get(1).getLeftField().getFieldMetadata().getName());
    }

    public void testQuery11() {
        QueryParser parser = QueryParser.newParser(repository);
        Expression expression = parser.parse(QueryParserTest.class.getResourceAsStream("query11.json")); //$NON-NLS-1$
        assertTrue(expression instanceof Select);
        Select select = (Select) expression;
        assertEquals(0, select.getPaging().getStart());
        assertEquals(10, select.getPaging().getLimit());
    }

    public void testQuery12() {
        QueryParser parser = QueryParser.newParser(repository);
        Expression expression = parser.parse(QueryParserTest.class.getResourceAsStream("query12.json")); //$NON-NLS-1$
        assertTrue(expression instanceof Select);
        Select select = (Select) expression;
        Condition condition = select.getCondition();
        assertTrue(condition instanceof Compare);
        Expression left = ((Compare) condition).getLeft();
        assertTrue(left instanceof IndexedField);
        IndexedField indexedField = (IndexedField) left;
        assertEquals(0, indexedField.getPosition());
    }

    public void testQuery13() {
        QueryParser parser = QueryParser.newParser(repository);
        Expression expression = parser.parse(QueryParserTest.class.getResourceAsStream("query13.json")); //$NON-NLS-1$
        assertTrue(expression instanceof Select);
        Select select = (Select) expression;
        assertEquals(2, select.getSelectedFields().size());
        assertTrue(select.getSelectedFields().get(0) instanceof Alias);
        Alias alias = (Alias) select.getSelectedFields().get(0);
        assertTrue(alias.getTypedExpression() instanceof Max);
        assertTrue(select.getSelectedFields().get(0) instanceof Alias);
        alias = (Alias) select.getSelectedFields().get(1);
        assertTrue(alias.getTypedExpression() instanceof Min);
    }

    public void testQuery14() {
        QueryParser parser = QueryParser.newParser(repository);
        Expression expression = parser.parse(QueryParserTest.class.getResourceAsStream("query14.json")); //$NON-NLS-1$
        assertTrue(expression instanceof Select);
        Select select = (Select) expression;
        Condition condition = select.getCondition();
        assertNotNull(condition);
        assertEquals(Isa.class, condition.getClass());
        assertEquals("Contained2", ((Isa) condition).getType().getName());
        assertEquals(Field.class, ((Isa) condition).getExpression().getClass());
    }
    
    public void testQuery15() {
        QueryParser parser = QueryParser.newParser(repository);
        Expression expression = parser.parse(QueryParserTest.class.getResourceAsStream("query15.json")); //$NON-NLS-1$
        assertTrue(expression instanceof Select);
        Select select = (Select) expression;
        At condition = select.getHistory();
        assertNotNull(condition);
        assertEquals(At.Swing.BEFORE, condition.getSwing());
        assertEquals(1000, condition.getDateTime());
    }

    public void testQuery16() {
        QueryParser parser = QueryParser.newParser(repository);
        Expression expression = parser.parse(QueryParserTest.class.getResourceAsStream("query16.json")); //$NON-NLS-1$
        assertTrue(expression instanceof Select);
        Select select = (Select) expression;
        assertEquals(2, select.getOrderBy().size());
        OrderBy orderBy = select.getOrderBy().get(0);
        assertTrue(orderBy.getField() instanceof Field);
        assertEquals("id", ((Field) orderBy.getField()).getFieldMetadata().getPath());
        assertEquals("Type1", ((Field) orderBy.getField()).getFieldMetadata().getEntityTypeName());
        assertEquals(OrderBy.Direction.DESC, orderBy.getDirection());
        orderBy = select.getOrderBy().get(1);
        assertTrue(orderBy.getField() instanceof Field);
        assertEquals("value1", ((Field) orderBy.getField()).getFieldMetadata().getPath());
        assertEquals("Type1", ((Field) orderBy.getField()).getFieldMetadata().getEntityTypeName());
        assertEquals(OrderBy.Direction.ASC, orderBy.getDirection());
    }

    public void testQuery17() {
        QueryParser parser = QueryParser.newParser(repository);
        Expression expression = parser.parse(QueryParserTest.class.getResourceAsStream("query17.json")); //$NON-NLS-1$
        assertTrue(expression instanceof Select);
        Select select = (Select) expression;
        Condition condition = select.getCondition();
        assertNotNull(condition);
        assertEquals(FieldFullText.class, condition.getClass());
        assertEquals("1", ((FieldFullText) condition).getValue());
    }

    public void testQuery18() {
        QueryParser parser = QueryParser.newParser(repository);
        Expression expression = parser.parse(QueryParserTest.class.getResourceAsStream("query18.json")); //$NON-NLS-1$
        assertTrue(expression instanceof Select);
        Select select = (Select) expression;
        Condition condition = select.getCondition();
        assertNotNull(condition);
        assertEquals(FullText.class, condition.getClass());
        assertEquals("1", ((FullText) condition).getValue());
    }
}