/*
 *      Kores-BytecodeWriter - Translates Kores Structure to JVM Bytecode <https://github.com/JonathanxD/CodeAPI-BytecodeWriter>
 *
 *         The MIT License (MIT)
 *
 *      Copyright (c) 2018 TheRealBuggy/JonathanxD (https://github.com/JonathanxD/) <jonathan.scripter@programmer.net>
 *      Copyright (c) contributors
 *
 *
 *      Permission is hereby granted, free of charge, to any person obtaining a copy
 *      of this software and associated documentation files (the "Software"), to deal
 *      in the Software without restriction, including without limitation the rights
 *      to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *      copies of the Software, and to permit persons to whom the Software is
 *      furnished to do so, subject to the following conditions:
 *
 *      The above copyright notice and this permission notice shall be included in
 *      all copies or substantial portions of the Software.
 *
 *      THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *      IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *      FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *      AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *      LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *      OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *      THE SOFTWARE.
 */
package com.github.jonathanxd.kores.bytecode.util

import com.github.jonathanxd.kores.base.MethodDeclarationBase
import com.github.jonathanxd.kores.base.TypeDeclaration
import com.github.jonathanxd.kores.base.TypeSpec
import com.github.jonathanxd.kores.common.MethodTypeSpec
import com.github.jonathanxd.kores.type.`is`
import com.github.jonathanxd.kores.type.concreteType
import java.lang.reflect.Method
import java.lang.reflect.Type

fun MethodTypeSpec.isConcreteEq(other: MethodTypeSpec) =
    this.methodName == other.methodName
            && this.typeSpec.returnType.concreteType.`is`(other.typeSpec.returnType.concreteType)
            && this.typeSpec.parameterTypes.map { it.concreteType }.`is`(other.typeSpec.parameterTypes.map { it.concreteType })


fun MethodDeclarationBase.getMethodSpec(typeDeclaration: TypeDeclaration): MethodTypeSpec =
    MethodTypeSpec(
        typeDeclaration,
        this.name,
        TypeSpec(this.returnType, this.parameters.map { it.type })
    )

fun MethodDeclarationBase.getMethodSpec(type: Type): MethodTypeSpec = MethodTypeSpec(
    type,
    this.name,
    TypeSpec(this.returnType, this.parameters.map { it.type })
)

fun Method.getMethodSpec(type: Type): MethodTypeSpec = MethodTypeSpec(
    type,
    this.name,
    TypeSpec(this.returnType, this.parameters.map { it.type })
)
