package ch.rasc.s4ws.grid;

import java.util.Collection;

import org.springframework.expression.ParseException;

import com.google.common.collect.Ordering;

public class PropertyOrderingFactory {

	private PropertyOrderingFactory() {
		// singleton
	}

	public static <T> Ordering<T> createOrdering(String propertyName) {
		try {
			Ordering<T> ordering = new PropertyOrdering<>(propertyName);
			return ordering;
		} catch (ParseException e) {
			return null;
		}
	}

	public static <T> Ordering<T> createOrdering(String propertyName, SortDirection sortDirection) {
		try {
			Ordering<T> ordering = new PropertyOrdering<>(propertyName);

			if (sortDirection == SortDirection.DESC) {
				ordering = ordering.reverse();
			}

			return ordering;
		} catch (ParseException e) {
			return null;
		}
	}

	public static <T> Ordering<T> createOrderingFromSorters(Collection<SortInfo> sortInfos) {
		Ordering<T> ordering = null;

		if (sortInfos != null) {
			for (SortInfo sorter : sortInfos) {
				Ordering<T> propertyOrdering = createOrdering(sorter.getProperty(), sorter.getDirection());
				if (ordering == null) {
					ordering = propertyOrdering;
				} else {
					ordering = ordering.compound(propertyOrdering);
				}
			}
		}

		return ordering;
	}

}
