package filters;

public class gt<Titem> implements Filter<Titem> {
	final String field;
	final Titem value;

	public gt(String field, Titem value) {
		this.field = field;
		this.value = value;
	}
}
