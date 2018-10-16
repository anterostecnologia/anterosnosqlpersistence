package br.com.anteros.nosql.persistence.session.event;

import br.com.anteros.core.log.Logger;
import br.com.anteros.core.log.LoggerProvider;
import br.com.anteros.core.utils.ReflectionUtils;
import br.com.anteros.nosql.persistence.session.event.NoSQLEvent;
import br.com.anteros.nosql.persistence.session.event.NoSQLEventListener;

public abstract class AbstractNoSQLEventListener<T> implements NoSQLEventListener<T> {

	private static Logger LOG = LoggerProvider.getInstance().getLogger(AbstractNoSQLEventListener.class);
	private final Class<?> domainClass;

	public AbstractNoSQLEventListener() {
		Class<?> typeArgument = ReflectionUtils.resolveTypeArgument(this.getClass(),
				AbstractNoSQLEventListener.class);
		this.domainClass = typeArgument == null ? Object.class : typeArgument;
	}

	@Override
	public void onEvent(NoSQLEvent<T> event) {
//
//		if (event instanceof AbstractAfterLoadEvent) {
//			MongoAfterLoadEvent afterLoadEvent = (AbstractAfterLoadEvent<?>) event;
//
//			if (domainClass.isAssignableFrom(afterLoadEvent.getType())) {
//				onAfterLoad((AbstractAfterLoadEvent<T>) event);
//			}
//
//			return;
//		}
//
//		if (event instanceof AbstractDeleteEvent) {
//
//			Class<?> eventDomainType = ((AbstractDeleteEvent<T,D>) event).getType();
//
//			if (eventDomainType != null && domainClass.isAssignableFrom(eventDomainType)) {
//				if (event instanceof AbstractBeforeDeleteEvent) {
//					onBeforeDelete((AbstractBeforeDeleteEvent<T>) event);
//				}
//				if (event instanceof AbstractAfterDeleteEvent) {
//					onAfterDelete((AbstractAfterDeleteEvent<T>) event);
//				}
//			}
//
//			return;
//
//		}
//
//		Object source = event.getSource();
//
//		if (source != null && !domainClass.isAssignableFrom(source.getClass())) {
//			return;
//		}
//
//		if (event instanceof AbstractBeforeConvertEvent) {
//			onBeforeConvert((AbstractBeforeConvertEvent<T>) event);
//		} else if (event instanceof AbstractBeforeSaveEvent) {
//			onBeforeSave((AbstractBeforeSaveEvent<T>) event);
//		} else if (event instanceof AbstractAfterSaveEvent) {
//			onAfterSave((AbstractAfterSaveEvent<T>) event);
//		} else if (event instanceof AbstractAfterConvertEvent) {
//			onAfterConvert((AbstractAfterConvertEvent<T>) event);
//		}
	}

	public void onBeforeConvert(AbstractBeforeConvertEvent<T> event) {

		if (LOG.isDebugEnabled()) {
			LOG.debug("onBeforeConvert({})", event.getSource());
		}
	}

	public void onBeforeSave(AbstractBeforeSaveEvent<T> event) {

		if (LOG.isDebugEnabled()) {
			LOG.debug("onBeforeSave({}, {})", event.getSource(), event.getDocument());
		}
	}

	public void onAfterSave(AbstractAfterSaveEvent<T> event) {

		if (LOG.isDebugEnabled()) {
			LOG.debug("onAfterSave({}, {})", event.getSource(), event.getDocument());
		}
	}

	public void onAfterLoad(AbstractAfterLoadEvent<T> event) {

		if (LOG.isDebugEnabled()) {
			LOG.debug("onAfterLoad({})", event.getDocument());
		}
	}

	public void onAfterConvert(AbstractAfterConvertEvent<T> event) {

		if (LOG.isDebugEnabled()) {
			LOG.debug("onAfterConvert({}, {})", event.getDocument(), event.getSource());
		}
	}

	public void onAfterDelete(AbstractAfterDeleteEvent<T> event) {

		if (LOG.isDebugEnabled()) {
			LOG.debug("onAfterDelete({})", event.getDocument());
		}
	}

	public void onBeforeDelete(AbstractBeforeDeleteEvent<T> event) {

		if (LOG.isDebugEnabled()) {
			LOG.debug("onBeforeDelete({})", event.getDocument());
		}
	}
}
