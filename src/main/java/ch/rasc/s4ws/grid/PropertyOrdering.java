package ch.rasc.s4ws.grid;

import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import com.google.common.collect.Ordering;

public class PropertyOrdering<T> extends Ordering<T> {
	private final static SpelExpressionParser parser = new SpelExpressionParser();

	private final Expression readPropertyExpression;

	public PropertyOrdering(String property) {
		this.readPropertyExpression = parser.parseExpression(property);
	}

	@SuppressWarnings("unchecked")
	@Override
	public int compare(T o1, T o2) {
		Object left = readPropertyExpression.getValue(o1);
		Object right = readPropertyExpression.getValue(o2);

		if (left == right) {
			return 0;
		}
		if (left == null) {
			return -1;
		}
		if (right == null) {
			return 1;
		}

		return ((Comparable<Object>) left).compareTo(right);
	}

}
