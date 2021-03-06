package org.jqassistant.contrib.plugin.javasrc.api.model;

import com.buschmais.jqassistant.plugin.common.api.model.ValueDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Relation;

/**
 * Represents an enumeration value.
 */
public interface EnumValueDescriptor extends TypedDescriptor, ValueDescriptor<FieldDescriptor>, EnumDescriptor {

    @Relation("IS")
    @Override
    FieldDescriptor getValue();

    @Override
    void setValue(FieldDescriptor fieldDescriptor);
}
