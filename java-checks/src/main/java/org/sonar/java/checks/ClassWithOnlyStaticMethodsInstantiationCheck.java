/*
 * SonarQube Java
 * Copyright (C) 2012 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.java.checks;

import com.google.common.collect.ImmutableList;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.java.model.AbstractTypedTree;
import org.sonar.java.resolve.Symbol;
import org.sonar.java.resolve.Symbol.TypeSymbol;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.MemberSelectExpressionTree;
import org.sonar.plugins.java.api.tree.NewClassTree;
import org.sonar.plugins.java.api.tree.ParameterizedTypeTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.Tree.Kind;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

import javax.annotation.Nullable;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;

@Rule(
  key = "S2440",
  name = "Classes with only \"static\" methods should not be instantiated",
  priority = Priority.MAJOR,
  tags = {"clumsy"})
@ActivatedByDefault
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.UNDERSTANDABILITY)
@SqaleConstantRemediation("2min")
public class ClassWithOnlyStaticMethodsInstantiationCheck extends SubscriptionBaseVisitor {

  @Override
  public List<Kind> nodesToVisit() {
    return ImmutableList.of(Kind.NEW_CLASS);
  }

  @Override
  public void visitNode(Tree tree) {
    Tree identifier = ((NewClassTree) tree).identifier();
    TypeSymbol newClassTypeSymbol = ((AbstractTypedTree) identifier).getSymbolType().getSymbol();
    if (!newClassTypeSymbol.isEnum() && hasOnlyStaticMethod(newClassTypeSymbol)) {
      String message = "Remove this instantiation.";
      String name = getNewClassName(identifier);
      if (name != null) {
        message = "Remove this instantiation of \"{0}\".";
      }
      addIssue(tree, MessageFormat.format(message, name));
    }
  }

  private boolean hasOnlyStaticMethod(TypeSymbol typeSymbol) {
    Collection<Symbol> members = typeSymbol.members().scopeSymbols();
    boolean hasStaticMethod = false;
    for (Symbol symbol : members) {
      if (symbol.isKind(Symbol.MTH)) {
        if (!symbol.isStatic()) {
          return false;
        }
        hasStaticMethod = true;
      }
    }
    return hasStaticMethod;
  }

  @Nullable
  private String getNewClassName(Tree tree) {
    if (tree.is(Kind.IDENTIFIER)) {
      return ((IdentifierTree) tree).name();
    } else if (tree.is(Kind.MEMBER_SELECT)) {
      return ((MemberSelectExpressionTree) tree).identifier().name();
    } else if (tree.is(Kind.PARAMETERIZED_TYPE)) {
      return getNewClassName(((ParameterizedTypeTree) tree).type());
    }
    return null;
  }
}
