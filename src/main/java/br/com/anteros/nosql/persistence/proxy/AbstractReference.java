package br.com.anteros.nosql.persistence.proxy;


import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.thoughtworks.proxy.kit.ObjectReference;

import br.com.anteros.nosql.persistence.converters.Key;
import br.com.anteros.nosql.persistence.session.NoSQLSession;



@SuppressWarnings({"rawtypes"})
public abstract class AbstractReference implements Serializable, ObjectReference, ProxiedReference {

    private static final long serialVersionUID = 1L;
    //CHECKSTYLE:OFF
    private final NoSQLSession<?> session;
    protected final boolean ignoreMissing;
    protected final Class referenceObjClass;
    protected Object object;
    //CHECKSTYLE:ON
    private boolean isFetched;

    protected AbstractReference(final NoSQLSession<?> session, final Class referenceObjClass, final boolean ignoreMissing) {
        this.session = session;
        this.referenceObjClass = referenceObjClass;
        this.ignoreMissing = ignoreMissing;
    }

    //CHECKSTYLE:OFF
    @Override
    public final Class __getReferenceObjClass() {
        //CHECKSTYLE:ON
        return referenceObjClass;
    }

    //CHECKSTYLE:OFF
    @Override
    public final boolean __isFetched() {
        //CHECKSTYLE:ON
        return isFetched;
    }

    //CHECKSTYLE:OFF
    @Override
    public Object __unwrap() {
        //CHECKSTYLE:ON
        return get();
    }

    @Override
    public final synchronized Object get() {
        if (isFetched) {
            return object;
        }

        try {
        	isFetched = true;
			object = fetch();			
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        return object;
    }

    @Override
    public final void set(final Object arg0) {
        throw new UnsupportedOperationException();
    }

    protected void beforeWriteObject() {
    }

    @SuppressWarnings("unchecked")
    protected final Object fetch(final Key<?> id) throws Exception {
        return getSession().findById(id.getId(),referenceObjClass);
    }

    protected abstract Object fetch() throws Exception;

    private void writeObject(final ObjectOutputStream out) throws IOException {
        beforeWriteObject();
        isFetched = false;
        out.defaultWriteObject();
    }

    public NoSQLSession getSession() {
        return session;
    }

	@Override
	public boolean isInitialized() {
		return isFetched;
	}

	@Override
	public void initialize() {
		this.get();
		
	}

	@Override
	public Object initializeAndReturnObject() {
		return this.get();
	}
}
