package br.com.anteros.nosql.persistence.session.query;


class TypedExample<T> implements Example<T> {

	private T probe;
	private ExampleMatcher matcher;
	
	
	public TypedExample(T probe, ExampleMatcher matcher) {
		this.probe = probe;
		this.matcher = matcher;
	}
	
	@Override
	public T getProbe() {
		return probe;
	}
	@Override
	public ExampleMatcher getMatcher() {
		return matcher;
	}
	public void setProbe(T probe) {
		this.probe = probe;
	}
	public void setMatcher(ExampleMatcher matcher) {
		this.matcher = matcher;
	}
	@Override
	public String toString() {
		return "TypedExample [probe=" + probe + ", matcher=" + matcher + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((matcher == null) ? 0 : matcher.hashCode());
		result = prime * result + ((probe == null) ? 0 : probe.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TypedExample<?> other = (TypedExample<?>) obj;
		if (matcher == null) {
			if (other.matcher != null)
				return false;
		} else if (!matcher.equals(other.matcher))
			return false;
		if (probe == null) {
			if (other.probe != null)
				return false;
		} else if (!probe.equals(other.probe))
			return false;
		return true;
	}
}
