/*
 * This file is generated by jOOQ.
 */
package com.kamarkaka.jooq.model.tables.records;


import com.kamarkaka.jooq.model.tables.AppHeaders;

import java.util.UUID;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.Row5;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AppHeadersRecord extends UpdatableRecordImpl<AppHeadersRecord> implements Record5<Integer, UUID, String, String, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.app_headers.header_id</code>.
     */
    public void setHeaderId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.app_headers.header_id</code>.
     */
    public Integer getHeaderId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>public.app_headers.app_id</code>.
     */
    public void setAppId(UUID value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.app_headers.app_id</code>.
     */
    public UUID getAppId() {
        return (UUID) get(1);
    }

    /**
     * Setter for <code>public.app_headers.type</code>.
     */
    public void setType(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.app_headers.type</code>.
     */
    public String getType() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.app_headers.key</code>.
     */
    public void setKey(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.app_headers.key</code>.
     */
    public String getKey() {
        return (String) get(3);
    }

    /**
     * Setter for <code>public.app_headers.value</code>.
     */
    public void setValue(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.app_headers.value</code>.
     */
    public String getValue() {
        return (String) get(4);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record5 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row5<Integer, UUID, String, String, String> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    @Override
    public Row5<Integer, UUID, String, String, String> valuesRow() {
        return (Row5) super.valuesRow();
    }

    @Override
    public Field<Integer> field1() {
        return AppHeaders.APP_HEADERS.HEADER_ID;
    }

    @Override
    public Field<UUID> field2() {
        return AppHeaders.APP_HEADERS.APP_ID;
    }

    @Override
    public Field<String> field3() {
        return AppHeaders.APP_HEADERS.TYPE;
    }

    @Override
    public Field<String> field4() {
        return AppHeaders.APP_HEADERS.KEY;
    }

    @Override
    public Field<String> field5() {
        return AppHeaders.APP_HEADERS.VALUE;
    }

    @Override
    public Integer component1() {
        return getHeaderId();
    }

    @Override
    public UUID component2() {
        return getAppId();
    }

    @Override
    public String component3() {
        return getType();
    }

    @Override
    public String component4() {
        return getKey();
    }

    @Override
    public String component5() {
        return getValue();
    }

    @Override
    public Integer value1() {
        return getHeaderId();
    }

    @Override
    public UUID value2() {
        return getAppId();
    }

    @Override
    public String value3() {
        return getType();
    }

    @Override
    public String value4() {
        return getKey();
    }

    @Override
    public String value5() {
        return getValue();
    }

    @Override
    public AppHeadersRecord value1(Integer value) {
        setHeaderId(value);
        return this;
    }

    @Override
    public AppHeadersRecord value2(UUID value) {
        setAppId(value);
        return this;
    }

    @Override
    public AppHeadersRecord value3(String value) {
        setType(value);
        return this;
    }

    @Override
    public AppHeadersRecord value4(String value) {
        setKey(value);
        return this;
    }

    @Override
    public AppHeadersRecord value5(String value) {
        setValue(value);
        return this;
    }

    @Override
    public AppHeadersRecord values(Integer value1, UUID value2, String value3, String value4, String value5) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached AppHeadersRecord
     */
    public AppHeadersRecord() {
        super(AppHeaders.APP_HEADERS);
    }

    /**
     * Create a detached, initialised AppHeadersRecord
     */
    public AppHeadersRecord(Integer headerId, UUID appId, String type, String key, String value) {
        super(AppHeaders.APP_HEADERS);

        setHeaderId(headerId);
        setAppId(appId);
        setType(type);
        setKey(key);
        setValue(value);
        resetChangedOnNotNull();
    }
}
