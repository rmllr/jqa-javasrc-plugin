package org.unileipzig.jqassistant.plugin.parser.api.scanner.visitor;

import java.util.Optional;

import org.unileipzig.jqassistant.plugin.parser.api.model.FieldDescriptor;
import org.unileipzig.jqassistant.plugin.parser.api.model.JavaSourceFileDescriptor;
import org.unileipzig.jqassistant.plugin.parser.api.model.PrimitiveValueDescriptor;
import org.unileipzig.jqassistant.plugin.parser.api.model.TypeDescriptor;
import org.unileipzig.jqassistant.plugin.parser.impl.scanner.TypeResolver;
import org.unileipzig.jqassistant.plugin.parser.impl.scanner.TypeResolverUtils;

import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedEnumConstantDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;

/**
 * This visitor handles parsed fields and creates corresponding descriptors. The
 * type resolver is used to get full qualified names of parsed declarations and
 * to determine the field type.
 * 
 * @author Richard Müller
 *
 */
public class FieldVisitor extends VoidVisitorAdapter<JavaSourceFileDescriptor> {
	private TypeResolver typeResolver;

	public FieldVisitor(TypeResolver typeResolver) {
		this.typeResolver = typeResolver;
	}

	@Override
	public void visit(FieldDeclaration fieldDeclaration, JavaSourceFileDescriptor javaSourceFileDescriptor) {
		super.visit(fieldDeclaration, javaSourceFileDescriptor);

		// signature, name
		ResolvedFieldDeclaration resolvedFieldDeclaration = fieldDeclaration.resolve();
		TypeDescriptor fieldTypeDescriptor = typeResolver
				.resolveType(TypeResolverUtils.getQualifiedName(resolvedFieldDeclaration.getType()));
		FieldDescriptor fieldDescriptor = typeResolver.addFieldDescriptor(resolvedFieldDeclaration.declaringType().getQualifiedName(),
				fieldTypeDescriptor.getFullQualifiedName() + " " + resolvedFieldDeclaration.getName());
		fieldDescriptor.setName(resolvedFieldDeclaration.getName());

		// visibility and access modifiers
		fieldDescriptor.setVisibility(TypeResolverUtils.getAccessSpecifier(fieldDeclaration.getModifiers()).getValue());
		fieldDescriptor.setFinal(fieldDeclaration.isFinal());
		fieldDescriptor.setStatic(fieldDeclaration.isStatic());

		// type
		fieldDescriptor.setType(fieldTypeDescriptor);

		// field value (of first variable)
		// TODO many variables for one field, type of values
		VariableDeclarator variable = fieldDeclaration.getVariables().get(0);
		Optional<Expression> value = variable.getInitializer();
		if (value.isPresent()) {
			PrimitiveValueDescriptor valueDescriptor = typeResolver.getValueDescriptor(PrimitiveValueDescriptor.class);
			if (value.get().isLiteralStringValueExpr()) {
				valueDescriptor.setValue(value.get().toString().replace("\"", ""));
			} else {
				valueDescriptor.setValue(value.get());
			}

			fieldDescriptor.setValue(valueDescriptor);
		}
	}

	@Override
	public void visit(EnumConstantDeclaration enumConstantDeclaration, JavaSourceFileDescriptor javaSourceFileDescriptor) {
		super.visit(enumConstantDeclaration, javaSourceFileDescriptor);
		
		EnumDeclaration declaringType = (EnumDeclaration) enumConstantDeclaration.getParentNode().get();
		
		// fqn, name
		ResolvedEnumConstantDeclaration resolvedEnumConstantDeclaration = enumConstantDeclaration.resolve();
		TypeDescriptor fieldTypeDescriptor = typeResolver
				.resolveType(TypeResolverUtils.getQualifiedName(resolvedEnumConstantDeclaration.getType()));

		FieldDescriptor fieldDescriptor = typeResolver.addFieldDescriptor(declaringType.resolve().getQualifiedName(),
				fieldTypeDescriptor.getFullQualifiedName() + " " + resolvedEnumConstantDeclaration.getName());
		fieldDescriptor.setName(resolvedEnumConstantDeclaration.getName());
	}
}
