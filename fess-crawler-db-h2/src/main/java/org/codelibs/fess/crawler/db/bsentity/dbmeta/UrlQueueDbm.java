/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.fess.crawler.db.bsentity.dbmeta;

import java.util.List;
import java.util.Map;

import org.dbflute.Entity;
import org.dbflute.dbmeta.AbstractDBMeta;
import org.dbflute.dbmeta.info.*;
import org.dbflute.dbmeta.name.*;
import org.dbflute.dbmeta.property.PropertyGateway;
import org.dbflute.dbway.DBDef;
import org.codelibs.fess.crawler.db.allcommon.*;
import org.codelibs.fess.crawler.db.exentity.*;

/**
 * The DB meta of URL_QUEUE. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class UrlQueueDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final UrlQueueDbm _instance = new UrlQueueDbm();
    private UrlQueueDbm() {}
    public static UrlQueueDbm getInstance() { return _instance; }

    // ===================================================================================
    //                                                                       Current DBDef
    //                                                                       =============
    public String getProjectName() { return DBCurrent.getInstance().projectName(); }
    public String getProjectPrefix() { return DBCurrent.getInstance().projectPrefix(); }
    public String getGenerationGapBasePrefix() { return DBCurrent.getInstance().generationGapBasePrefix(); }
    public DBDef getCurrentDBDef() { return DBCurrent.getInstance().currentDBDef(); }

    // ===================================================================================
    //                                                                    Property Gateway
    //                                                                    ================
    // -----------------------------------------------------
    //                                       Column Property
    //                                       ---------------
    protected final Map<String, PropertyGateway> _epgMap = newHashMap();
    { xsetupEpg(); }
    protected void xsetupEpg() {
        setupEpg(_epgMap, et -> ((UrlQueue)et).getId(), (et, vl) -> ((UrlQueue)et).setId(ctl(vl)), "id");
        setupEpg(_epgMap, et -> ((UrlQueue)et).getSessionId(), (et, vl) -> ((UrlQueue)et).setSessionId((String)vl), "sessionId");
        setupEpg(_epgMap, et -> ((UrlQueue)et).getMethod(), (et, vl) -> ((UrlQueue)et).setMethod((String)vl), "method");
        setupEpg(_epgMap, et -> ((UrlQueue)et).getUrl(), (et, vl) -> ((UrlQueue)et).setUrl((String)vl), "url");
        setupEpg(_epgMap, et -> ((UrlQueue)et).getMetaData(), (et, vl) -> ((UrlQueue)et).setMetaData((String)vl), "metaData");
        setupEpg(_epgMap, et -> ((UrlQueue)et).getEncoding(), (et, vl) -> ((UrlQueue)et).setEncoding((String)vl), "encoding");
        setupEpg(_epgMap, et -> ((UrlQueue)et).getParentUrl(), (et, vl) -> ((UrlQueue)et).setParentUrl((String)vl), "parentUrl");
        setupEpg(_epgMap, et -> ((UrlQueue)et).getDepth(), (et, vl) -> ((UrlQueue)et).setDepth(cti(vl)), "depth");
        setupEpg(_epgMap, et -> ((UrlQueue)et).getLastModified(), (et, vl) -> ((UrlQueue)et).setLastModified(ctl(vl)), "lastModified");
        setupEpg(_epgMap, et -> ((UrlQueue)et).getCreateTime(), (et, vl) -> ((UrlQueue)et).setCreateTime(ctl(vl)), "createTime");
    }
    public PropertyGateway findPropertyGateway(String prop)
    { return doFindEpg(_epgMap, prop); }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "URL_QUEUE";
    protected final String _tableDispName = "URL_QUEUE";
    protected final String _tablePropertyName = "urlQueue";
    protected final TableSqlName _tableSqlName = new TableSqlName("URL_QUEUE", _tableDbName);
    { _tableSqlName.xacceptFilter(DBFluteConfig.getInstance().getTableSqlNameFilter()); }
    public String getTableDbName() { return _tableDbName; }
    public String getTableDispName() { return _tableDispName; }
    public String getTablePropertyName() { return _tablePropertyName; }
    public TableSqlName getTableSqlName() { return _tableSqlName; }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected final ColumnInfo _columnId = cci("ID", "ID", null, null, Long.class, "id", null, true, true, true, "BIGINT", 19, 0, "NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_5FB78C51_6F00_4BF9_89B9_AE15B959225E", false, null, null, null, null, null, false);
    protected final ColumnInfo _columnSessionId = cci("SESSION_ID", "SESSION_ID", null, null, String.class, "sessionId", null, false, false, true, "VARCHAR", 20, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnMethod = cci("METHOD", "METHOD", null, null, String.class, "method", null, false, false, true, "VARCHAR", 10, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUrl = cci("URL", "URL", null, null, String.class, "url", null, false, false, true, "VARCHAR", 65536, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnMetaData = cci("META_DATA", "META_DATA", null, null, String.class, "metaData", null, false, false, false, "VARCHAR", 65536, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnEncoding = cci("ENCODING", "ENCODING", null, null, String.class, "encoding", null, false, false, false, "VARCHAR", 20, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnParentUrl = cci("PARENT_URL", "PARENT_URL", null, null, String.class, "parentUrl", null, false, false, false, "VARCHAR", 65536, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnDepth = cci("DEPTH", "DEPTH", null, null, Integer.class, "depth", null, false, false, true, "INTEGER", 10, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnLastModified = cci("LAST_MODIFIED", "LAST_MODIFIED", null, null, Long.class, "lastModified", null, false, false, false, "BIGINT", 19, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnCreateTime = cci("CREATE_TIME", "CREATE_TIME", null, null, Long.class, "createTime", null, false, false, true, "BIGINT", 19, 0, null, false, null, null, null, null, null, false);

    /**
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnId() { return _columnId; }
    /**
     * SESSION_ID: {IX+, NotNull, VARCHAR(20)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnSessionId() { return _columnSessionId; }
    /**
     * METHOD: {NotNull, VARCHAR(10)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnMethod() { return _columnMethod; }
    /**
     * URL: {NotNull, VARCHAR(65536)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnUrl() { return _columnUrl; }
    /**
     * META_DATA: {VARCHAR(65536)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnMetaData() { return _columnMetaData; }
    /**
     * ENCODING: {VARCHAR(20)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnEncoding() { return _columnEncoding; }
    /**
     * PARENT_URL: {VARCHAR(65536)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnParentUrl() { return _columnParentUrl; }
    /**
     * DEPTH: {NotNull, INTEGER(10)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnDepth() { return _columnDepth; }
    /**
     * LAST_MODIFIED: {BIGINT(19)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnLastModified() { return _columnLastModified; }
    /**
     * CREATE_TIME: {NotNull, BIGINT(19)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnCreateTime() { return _columnCreateTime; }

    protected List<ColumnInfo> ccil() {
        List<ColumnInfo> ls = newArrayList();
        ls.add(columnId());
        ls.add(columnSessionId());
        ls.add(columnMethod());
        ls.add(columnUrl());
        ls.add(columnMetaData());
        ls.add(columnEncoding());
        ls.add(columnParentUrl());
        ls.add(columnDepth());
        ls.add(columnLastModified());
        ls.add(columnCreateTime());
        return ls;
    }

    { initializeInformationResource(); }

    // ===================================================================================
    //                                                                         Unique Info
    //                                                                         ===========
    // -----------------------------------------------------
    //                                       Primary Element
    //                                       ---------------
    protected UniqueInfo cpui() { return hpcpui(columnId()); }
    public boolean hasPrimaryKey() { return true; }
    public boolean hasCompoundPrimaryKey() { return false; }

    // ===================================================================================
    //                                                                       Relation Info
    //                                                                       =============
    // cannot cache because it uses related DB meta instance while booting
    // (instead, cached by super's collection)
    // -----------------------------------------------------
    //                                      Foreign Property
    //                                      ----------------

    // -----------------------------------------------------
    //                                     Referrer Property
    //                                     -----------------

    // ===================================================================================
    //                                                                        Various Info
    //                                                                        ============
    public boolean hasIdentity() { return true; }

    // ===================================================================================
    //                                                                           Type Name
    //                                                                           =========
    public String getEntityTypeName() { return "org.codelibs.fess.crawler.db.exentity.UrlQueue"; }
    public String getConditionBeanTypeName() { return "org.codelibs.fess.crawler.db.cbean.UrlQueueCB"; }
    public String getBehaviorTypeName() { return "org.codelibs.fess.crawler.db.exbhv.UrlQueueBhv"; }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    public Class<UrlQueue> getEntityType() { return UrlQueue.class; }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    public UrlQueue newEntity() { return new UrlQueue(); }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    public void acceptPrimaryKeyMap(Entity et, Map<String, ? extends Object> mp)
    { doAcceptPrimaryKeyMap((UrlQueue)et, mp); }
    public void acceptAllColumnMap(Entity et, Map<String, ? extends Object> mp)
    { doAcceptAllColumnMap((UrlQueue)et, mp); }
    public Map<String, Object> extractPrimaryKeyMap(Entity et) { return doExtractPrimaryKeyMap(et); }
    public Map<String, Object> extractAllColumnMap(Entity et) { return doExtractAllColumnMap(et); }
}
