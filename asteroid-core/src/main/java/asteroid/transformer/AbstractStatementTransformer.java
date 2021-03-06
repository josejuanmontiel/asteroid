package asteroid.transformer;

import groovy.lang.Closure;

import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.ast.stmt.Statement;

/**
 * This {@link Transformer} could be used as a base for transforming
 * {@link Statement} instances.
 * <br><br>
 * <b class="warning">IMPORTANT:</b> The parameter type is only used as a hint. If you
 * are not carefull on how you define the search criteria you could
 * get a {@link ClassCastException} at runtime. The criteria should ask
 * for the type of the statement in the first place.
 *
 * @param <T> use as a hint for the {@link AbstractStatementTransformer#transformStatement} method parameter
 * @since 0.2.0
 */
public abstract class AbstractStatementTransformer<T extends Statement> extends AbstractTransformer {

    private final Closure<Boolean> criteria;

    /**
     * Every instance needs the source unit awareness and the name of the method
     * it's going to transform
     *
     * @param sourceUnit Needed to apply scope
     * @param criteria the criteria used to search the interesting
     * {@link Statement}
     * @since 0.2.0
     */
    public AbstractStatementTransformer(final SourceUnit sourceUnit, final Closure<Boolean> criteria) {
        super(sourceUnit);
        this.criteria = criteria;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStatement(final Statement statement) {
        if (criteria.call(statement)) {
            transformStatement((T) statement);
            return;
        }
        statement.visit(this);
    }

    /**
     * This method will transform the statement into its final version.
     *
     * @param statement the method statement you want to transform
     * @since 0.2.0
     */
    public abstract void transformStatement(T statement);

    /**
     * This method returns a criteria to look for {@link Statement}
     * with a specific type
     *
     * @deprecated use {@link asteroid.Criterias}
     * @param stmtClass the type {@link java.lang.Class}
     * @return a search criteria
     * @since 0.2.0
     */
    @Deprecated
    public static <T extends Statement> Closure<Boolean> byType(final Class<T> stmtClass) {
        return new Closure<Boolean>(null) {
            public Boolean doCall(final Statement statement) {
                return stmtClass != null && stmtClass.isInstance(statement);
            }
        };
    }
}
