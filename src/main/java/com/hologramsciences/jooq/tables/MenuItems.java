/*
 * This file is generated by jOOQ.
 */
package com.hologramsciences.jooq.tables;


import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row3;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;

import com.hologramsciences.jooq.Keys;
import com.hologramsciences.jooq.Public;
import com.hologramsciences.jooq.tables.records.MenuItemsRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class MenuItems extends TableImpl<MenuItemsRecord> {

    private static final long serialVersionUID = -1631019903;

    /**
     * The reference instance of <code>PUBLIC.MENU_ITEMS</code>
     */
    public static final MenuItems MENU_ITEMS = new MenuItems();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<MenuItemsRecord> getRecordType() {
        return MenuItemsRecord.class;
    }

    /**
     * The column <code>PUBLIC.MENU_ITEMS.ID</code>.
     */
    public final TableField<MenuItemsRecord, Long> ID = createField(DSL.name("ID"), org.jooq.impl.SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>PUBLIC.MENU_ITEMS.RESTAURANT_ID</code>.
     */
    public final TableField<MenuItemsRecord, Long> RESTAURANT_ID = createField(DSL.name("RESTAURANT_ID"), org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>PUBLIC.MENU_ITEMS.NAME</code>.
     */
    public final TableField<MenuItemsRecord, String> NAME = createField(DSL.name("NAME"), org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * Create a <code>PUBLIC.MENU_ITEMS</code> table reference
     */
    public MenuItems() {
        this(DSL.name("MENU_ITEMS"), null);
    }

    /**
     * Create an aliased <code>PUBLIC.MENU_ITEMS</code> table reference
     */
    public MenuItems(String alias) {
        this(DSL.name(alias), MENU_ITEMS);
    }

    /**
     * Create an aliased <code>PUBLIC.MENU_ITEMS</code> table reference
     */
    public MenuItems(Name alias) {
        this(alias, MENU_ITEMS);
    }

    private MenuItems(Name alias, Table<MenuItemsRecord> aliased) {
        this(alias, aliased, null);
    }

    private MenuItems(Name alias, Table<MenuItemsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> MenuItems(Table<O> child, ForeignKey<O, MenuItemsRecord> key) {
        super(child, key, MENU_ITEMS);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<MenuItemsRecord, Long> getIdentity() {
        return Keys.IDENTITY_MENU_ITEMS;
    }

    @Override
    public UniqueKey<MenuItemsRecord> getPrimaryKey() {
        return Keys.CONSTRAINT_4;
    }

    @Override
    public List<UniqueKey<MenuItemsRecord>> getKeys() {
        return Arrays.<UniqueKey<MenuItemsRecord>>asList(Keys.CONSTRAINT_4);
    }

    @Override
    public List<ForeignKey<MenuItemsRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<MenuItemsRecord, ?>>asList(Keys.CONSTRAINT_4D);
    }

    public Restaurants restaurants() {
        return new Restaurants(this, Keys.CONSTRAINT_4D);
    }

    @Override
    public MenuItems as(String alias) {
        return new MenuItems(DSL.name(alias), this);
    }

    @Override
    public MenuItems as(Name alias) {
        return new MenuItems(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public MenuItems rename(String name) {
        return new MenuItems(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public MenuItems rename(Name name) {
        return new MenuItems(name, null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<Long, Long, String> fieldsRow() {
        return (Row3) super.fieldsRow();
    }
}
