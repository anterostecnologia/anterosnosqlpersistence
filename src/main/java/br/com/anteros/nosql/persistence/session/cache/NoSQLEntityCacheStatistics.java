package br.com.anteros.nosql.persistence.session.cache;


public class NoSQLEntityCacheStatistics {
    private int entities;
    private int hits;
    private int misses;

    public NoSQLEntityCacheStatistics copy() {
        final NoSQLEntityCacheStatistics copy = new NoSQLEntityCacheStatistics();
        copy.entities = entities;
        copy.hits = hits;
        copy.misses = misses;
        return copy;
    }

    public void incEntities() {
        entities++;
    }

    public void incHits() {
        hits++;
    }

    public void incMisses() {
        misses++;
    }

    public void reset() {
        entities = 0;
        hits = 0;
        misses = 0;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": " + entities + " entities, " + hits + " hits, " + misses + " misses.";
    }
}
