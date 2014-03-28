package ch.rasc.s4ws.grid;

import java.io.IOException;
import java.util.Collection;

import org.springframework.expression.ParseException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Ordering;

public class PropertyOrderingFactory {

	private final static ObjectMapper mapper = new ObjectMapper();

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

	public static <T> Ordering<T> createOrderingFromSorters(String sortInfo) throws JsonParseException,
			JsonMappingException, IOException {
		if (sortInfo == null) {
			return null;
		}

		Collection<SortInfo> sortInfos = mapper.readValue(sortInfo, new TypeReference<Collection<SortInfo>>() {
			// nothing here
		});

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
