package br.com.anteros.nosql.persistence.session.query;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import br.com.anteros.core.utils.Assert;

class TypedExampleMatcher implements ExampleMatcher {

	private NullHandler nullHandler;
	private StringMatcher defaultStringMatcher;
	private PropertySpecifiers propertySpecifiers;
	private Set<String> ignoredPaths;
	private boolean defaultIgnoreCase;
	private MatchMode mode;

	TypedExampleMatcher() {

		this(NullHandler.IGNORE, StringMatcher.DEFAULT, new PropertySpecifiers(), Collections.emptySet(), false,
				MatchMode.ALL);
	}

	public TypedExampleMatcher(NullHandler nullHandler, StringMatcher defaultStringMatcher,
			PropertySpecifiers propertySpecifiers, Set<String> ignoredPaths, boolean b, MatchMode mode) {
		this.nullHandler = nullHandler;
		this.defaultStringMatcher = defaultStringMatcher;
		this.propertySpecifiers = propertySpecifiers;
		this.ignoredPaths = ignoredPaths;
		this.mode = mode;
	}

	@Override
	public ExampleMatcher withIgnorePaths(String... ignoredPaths) {

		Assert.notEmpty(ignoredPaths, "IgnoredPaths must not be empty!");
		Assert.noNullElements(ignoredPaths, "IgnoredPaths must not contain null elements!");

		Set<String> newIgnoredPaths = new LinkedHashSet<>(this.ignoredPaths);
		newIgnoredPaths.addAll(Arrays.asList(ignoredPaths));

		return new TypedExampleMatcher(nullHandler, defaultStringMatcher, propertySpecifiers, newIgnoredPaths,
				defaultIgnoreCase, mode);
	}

	@Override
	public ExampleMatcher withStringMatcher(StringMatcher defaultStringMatcher) {

		Assert.notNull(ignoredPaths, "DefaultStringMatcher must not be empty!");

		return new TypedExampleMatcher(nullHandler, defaultStringMatcher, propertySpecifiers, ignoredPaths,
				defaultIgnoreCase, mode);
	}

	@Override
	public ExampleMatcher withIgnoreCase(boolean defaultIgnoreCase) {
		return new TypedExampleMatcher(nullHandler, defaultStringMatcher, propertySpecifiers, ignoredPaths,
				defaultIgnoreCase, mode);
	}

	@Override
	public ExampleMatcher withMatcher(String propertyPath, GenericPropertyMatcher genericPropertyMatcher) {

		Assert.hasText(propertyPath, "PropertyPath must not be empty!");
		Assert.notNull(genericPropertyMatcher, "GenericPropertyMatcher must not be empty!");

		PropertySpecifiers propertySpecifiers = new PropertySpecifiers(this.propertySpecifiers);
		PropertySpecifier propertySpecifier = new PropertySpecifier(propertyPath);

		if (genericPropertyMatcher.ignoreCase != null) {
			propertySpecifier = propertySpecifier.withIgnoreCase(genericPropertyMatcher.ignoreCase);
		}

		if (genericPropertyMatcher.stringMatcher != null) {
			propertySpecifier = propertySpecifier.withStringMatcher(genericPropertyMatcher.stringMatcher);
		}

		propertySpecifier = propertySpecifier.withValueTransformer(genericPropertyMatcher.valueTransformer);

		propertySpecifiers.add(propertySpecifier);

		return new TypedExampleMatcher(nullHandler, defaultStringMatcher, propertySpecifiers, ignoredPaths,
				defaultIgnoreCase, mode);
	}

	@Override
	public ExampleMatcher withTransformer(String propertyPath, PropertyValueTransformer propertyValueTransformer) {

		Assert.hasText(propertyPath, "PropertyPath must not be empty!");
		Assert.notNull(propertyValueTransformer, "PropertyValueTransformer must not be empty!");

		PropertySpecifiers propertySpecifiers = new PropertySpecifiers(this.propertySpecifiers);
		PropertySpecifier propertySpecifier = getOrCreatePropertySpecifier(propertyPath, propertySpecifiers);

		propertySpecifiers.add(propertySpecifier.withValueTransformer(propertyValueTransformer));

		return new TypedExampleMatcher(nullHandler, defaultStringMatcher, propertySpecifiers, ignoredPaths,
				defaultIgnoreCase, mode);
	}

	@Override
	public ExampleMatcher withIgnoreCase(String... propertyPaths) {

		Assert.notEmpty(propertyPaths, "PropertyPaths must not be empty!");
		Assert.noNullElements(propertyPaths, "PropertyPaths must not contain null elements!");

		PropertySpecifiers propertySpecifiers = new PropertySpecifiers(this.propertySpecifiers);

		for (String propertyPath : propertyPaths) {
			PropertySpecifier propertySpecifier = getOrCreatePropertySpecifier(propertyPath, propertySpecifiers);
			propertySpecifiers.add(propertySpecifier.withIgnoreCase(true));
		}

		return new TypedExampleMatcher(nullHandler, defaultStringMatcher, propertySpecifiers, ignoredPaths,
				defaultIgnoreCase, mode);
	}

	@Override
	public ExampleMatcher withNullHandler(NullHandler nullHandler) {

		Assert.notNull(nullHandler, "NullHandler must not be null!");
		return new TypedExampleMatcher(nullHandler, defaultStringMatcher, propertySpecifiers, ignoredPaths,
				defaultIgnoreCase, mode);
	}

	@Override
	public NullHandler getNullHandler() {
		return nullHandler;
	}

	@Override
	public StringMatcher getDefaultStringMatcher() {
		return defaultStringMatcher;
	}

	@Override
	public boolean isIgnoreCaseEnabled() {
		return this.defaultIgnoreCase;
	}

	@Override
	public Set<String> getIgnoredPaths() {
		return ignoredPaths;
	}

	@Override
	public PropertySpecifiers getPropertySpecifiers() {
		return propertySpecifiers;
	}

	@Override
	public MatchMode getMatchMode() {
		return mode;
	}

	private PropertySpecifier getOrCreatePropertySpecifier(String propertyPath, PropertySpecifiers propertySpecifiers) {

		if (propertySpecifiers.hasSpecifierForPath(propertyPath)) {
			return propertySpecifiers.getForPath(propertyPath);
		}

		return new PropertySpecifier(propertyPath);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (defaultIgnoreCase ? 1231 : 1237);
		result = prime * result + ((defaultStringMatcher == null) ? 0 : defaultStringMatcher.hashCode());
		result = prime * result + ((ignoredPaths == null) ? 0 : ignoredPaths.hashCode());
		result = prime * result + ((mode == null) ? 0 : mode.hashCode());
		result = prime * result + ((nullHandler == null) ? 0 : nullHandler.hashCode());
		result = prime * result + ((propertySpecifiers == null) ? 0 : propertySpecifiers.hashCode());
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
		TypedExampleMatcher other = (TypedExampleMatcher) obj;
		if (defaultIgnoreCase != other.defaultIgnoreCase)
			return false;
		if (defaultStringMatcher != other.defaultStringMatcher)
			return false;
		if (ignoredPaths == null) {
			if (other.ignoredPaths != null)
				return false;
		} else if (!ignoredPaths.equals(other.ignoredPaths))
			return false;
		if (mode != other.mode)
			return false;
		if (nullHandler != other.nullHandler)
			return false;
		if (propertySpecifiers == null) {
			if (other.propertySpecifiers != null)
				return false;
		} else if (!propertySpecifiers.equals(other.propertySpecifiers))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TypedExampleMatcher [nullHandler=" + nullHandler + ", defaultStringMatcher=" + defaultStringMatcher
				+ ", propertySpecifiers=" + propertySpecifiers + ", ignoredPaths=" + ignoredPaths
				+ ", defaultIgnoreCase=" + defaultIgnoreCase + ", mode=" + mode + "]";
	}

	public ExampleMatcher withMode(MatchMode mode) {
		this.mode = mode;
		return this;
	}
}
