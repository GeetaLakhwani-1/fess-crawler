package org.codelibs.fess.crawler.util;

import java.util.ArrayList;

public class EsResultList<E> extends ArrayList<E> {

    private static final long serialVersionUID = 1L;

    private long totalHits;

    private long tookInMillis;

    public void setTotalHits(final long totalHits) {
        this.totalHits = totalHits;
    }

    public long getTotalHits() {
        return totalHits;
    }

    public void setTookInMillis(final long tookInMillis) {
        this.tookInMillis = tookInMillis;
    }

    public long getTookInMillis() {
        return tookInMillis;
    }

}
