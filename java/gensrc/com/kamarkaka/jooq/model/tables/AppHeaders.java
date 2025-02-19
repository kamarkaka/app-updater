/*
 * This file is generated by jOOQ.
 */
package com.kamarkaka.jooq.model.tables;


import com.kamarkaka.jooq.model.Keys;
import com.kamarkaka.jooq.model.Public;
import com.kamarkaka.jooq.model.tables.records.AppHeadersRecord;

import java.util.UUID;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function5;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row5;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AppHeaders extends TableImpl<AppHeadersRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.app_headers</code>
     */
    public static final AppHeaders APP_HEADERS = new AppHeaders();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<AppHeadersRecord> getRecordType() {
        return AppHeadersRecord.class;
    }

    /**
     * The column <code>public.app_headers.header_id</code>.
     */
    public final TableField<AppHeadersRecord, Integer> HEADER_ID = createField(DSL.name("header_id"), SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.app_headers.app_id</code>.
     */
    public final TableField<AppHeadersRecord, UUID> APP_ID = createField(DSL.name("app_id"), SQLDataType.UUID.nullable(false), this, "");

    /**
     * The column <code>public.app_headers.type</code>.
     */
    public final TableField<AppHeadersRecord, String> TYPE = createField(DSL.name("type"), SQLDataType.VARCHAR(8).nullable(false), this, "");

    /**
     * The column <code>public.app_headers.key</code>.
     */
    public final TableField<AppHeadersRecord, String> KEY = createField(DSL.name("key"), SQLDataType.VARCHAR(16).nullable(false), this, "");

    /**
     * The column <code>public.app_headers.value</code>.
     */
    public final TableField<AppHeadersRecord, String> VALUE = createField(DSL.name("value"), SQLDataType.CLOB.nullable(false), this, "");

    private AppHeaders(Name alias, Table<AppHeadersRecord> aliased) {
        this(alias, aliased, null);
    }

    private AppHeaders(Name alias, Table<AppHeadersRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.app_headers</code> table reference
     */
    public AppHeaders(String alias) {
        this(DSL.name(alias), APP_HEADERS);
    }

    /**
     * Create an aliased <code>public.app_headers</code> table reference
     */
    public AppHeaders(Name alias) {
        this(alias, APP_HEADERS);
    }

    /**
     * Create a <code>public.app_headers</code> table reference
     */
    public AppHeaders() {
        this(DSL.name("app_headers"), null);
    }

    public <O extends Record> AppHeaders(Table<O> child, ForeignKey<O, AppHeadersRecord> key) {
        super(child, key, APP_HEADERS);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    public Identity<AppHeadersRecord, Integer> getIdentity() {
        return (Identity<AppHeadersRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<AppHeadersRecord> getPrimaryKey() {
        return Keys.APP_HEADERS_PKEY;
    }

    @Override
    public AppHeaders as(String alias) {
        return new AppHeaders(DSL.name(alias), this);
    }

    @Override
    public AppHeaders as(Name alias) {
        return new AppHeaders(alias, this);
    }

    @Override
    public AppHeaders as(Table<?> alias) {
        return new AppHeaders(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public AppHeaders rename(String name) {
        return new AppHeaders(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public AppHeaders rename(Name name) {
        return new AppHeaders(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public AppHeaders rename(Table<?> name) {
        return new AppHeaders(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row5 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row5<Integer, UUID, String, String, String> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function5<? super Integer, ? super UUID, ? super String, ? super String, ? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function5<? super Integer, ? super UUID, ? super String, ? super String, ? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
