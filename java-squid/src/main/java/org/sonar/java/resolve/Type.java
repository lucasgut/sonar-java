/*
 * Sonar Java
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
package org.sonar.java.resolve;

import java.util.List;

public class Type {

  Symbol.TypeSymbol symbol;

  public Type(Symbol.TypeSymbol symbol) {
    this.symbol = symbol;
  }

  public static class ClassType extends Type {

    /**
     * Supertype of this class.
     */
    Type supertype;

    /**
     * Interfaces of this class.
     */
    List<Type> interfaces;

    public ClassType(Symbol.TypeSymbol symbol) {
      super(symbol);
    }

  }

  public static class ArrayType extends Type {

    /**
     * Type of elements of this array.
     */
    Type elementType;

    /**
     * @param arrayClass {@link Symbols#arrayClass}
     */
    public ArrayType(Type elementType, Symbol.TypeSymbol arrayClass) {
      super(arrayClass);
      this.elementType = elementType;
    }

  }

}