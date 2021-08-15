/*
 *      Kores-BytecodeWriter - Translates Kores Structure to JVM Bytecode <https://github.com/JonathanxD/CodeAPI-BytecodeWriter>
 *
 *         The MIT License (MIT)
 *
 *      Copyright (c) 2021 TheRealBuggy/JonathanxD (https://github.com/JonathanxD/) <jonathan.scripter@programmer.net>
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
package com.koresframework.kores.bytecode.processor.processors

import com.koresframework.kores.KoresElement
import com.koresframework.kores.Instruction
import com.koresframework.kores.Instructions
import com.koresframework.kores.base.*
import com.koresframework.kores.bytecode.processor.*
import com.koresframework.kores.bytecode.util.ReflectType
import com.koresframework.kores.bytecode.util.allInnerTypes
import com.koresframework.kores.bytecode.util.allTypes
import com.koresframework.kores.common.getNewName
import com.koresframework.kores.common.getNewNameBasedOnNameList
import com.koresframework.kores.factory.*
import com.koresframework.kores.type.*
import com.koresframework.kores.util.conversion.access
import com.github.jonathanxd.iutils.data.TypedData
import com.github.jonathanxd.iutils.kt.add
import com.github.jonathanxd.iutils.kt.require
import java.lang.reflect.Type

object Util {

    fun resolveType(spec: TypeSpec, data: TypedData): TypeSpec =
        spec.copy(returnType = resolveType(spec.returnType, data),
            parameterTypes = spec.parameterTypes.map { resolveType(it, data) })

    fun resolveType(koresType: ReflectType, data: TypedData): KoresType {

        val type by lazy {
            TYPE_DECLARATION.require(data)
        }

        return when (koresType) {
            is Alias.THIS -> type
            is Alias.SUPER -> (type as? SuperClassHolder)?.superClass?.koresType
                    ?: throw IllegalStateException("Type '$type' as no super types.")
            is Alias.INTERFACE -> {
                val n = koresType.n

                (type as? ImplementationHolder)?.implementations?.map { it.koresType }?.getOrNull(n)
                        ?: throw IllegalStateException("Type '$type' as no implementation or the index '$n' exceed the amount of implementations in the type.")

            }
            else -> koresType.koresType
        }

    }

    tailrec fun getOwner(typeDeclaration: TypeDeclaration): TypeDeclaration =
        if (typeDeclaration.outerType == null || typeDeclaration.outerType !is TypeDeclaration)
            typeDeclaration
        else
            this.getOwner(typeDeclaration.outerType as TypeDeclaration)


}

val MethodInvocation.isSuperConstructorInvocation get() = this.spec.methodName == "<init>" && this.target == Alias.SUPER

fun getTypes(current: TypeDeclaration, data: TypedData): List<TypeDeclaration> {
    val list = mutableListOf<TypeDeclaration>()
    var parent: TypedData? = data
    var found = false

    while (parent != null) {
        TYPES.getOrNull(parent)?.let {
            it.forEach {
                if (it.modifiers.contains(KoresModifier.STATIC)) {
                    if (!found && !it.`is`(current))
                        throw IllegalStateException("Found static outer class before finding the current class.")
                    if (found) list.add(it)
                    return list
                } else {
                    if (found) list.add(it)
                }

                if (!found && it.`is`(current))
                    found = true
            }

        }

        parent = parent.parent
    }

    return list
}

/**
 * Gets arguments to be used to construct an inner type.
 *
 * [typeToFind] is the type of the inner class
 */
fun getInnerSpec(typeToFind: Type, data: TypedData): InnerConstructorSpec? {
    val type = TYPE_DECLARATION.getOrNull(data)

    val all = type.allInnerTypes()

    val first = all.firstOrNull { it.`is`(typeToFind) }

    if (first != null) {
        val argTypes = mutableListOf<Type>()
        val args = mutableListOf<Instruction>()

        if (first.modifiers.contains(KoresModifier.STATIC))
            return null

        argTypes += type
        args += Access.THIS

        return InnerConstructorSpec(argTypes, args)
    }

    return null
}

data class InnerConstructorSpec(val argTypes: List<Type>, val args: List<Instruction>)

fun accessMemberOfType(memberOwner: Type, accessor: Accessor, data: TypedData): MemberAccess? {
    val type = TYPE_DECLARATION.getOrNull(data)

    val top = getTopLevelOuter(data) ?: return null

    val all = top.allTypes()

    val target = all.firstOrNull { it.`is`(memberOwner) }
    val targetData = data.mainData

    if (target != null) {
        if (!accessor.localization.`is`(type) && accessor.localization.`is`(target)) {
            val member: KoresElement? =
                if (accessor is FieldAccess) {
                    val field = target.fields.firstOrNull {
                        it.name == accessor.name
                                && it.type.isConcreteIdEq(accessor.type)
                    }

                    if (field != null && field.modifiers.contains(KoresModifier.PRIVATE))
                        field
                    else null
                } else if (accessor is MethodInvocation) {
                    val method = target.methods.firstOrNull {
                        it.name == accessor.spec.methodName
                                && it.typeSpec.isConreteEq(accessor.spec.typeSpec)
                    }

                    val ctr = getConstructors(target).firstOrNull {
                        it.name == accessor.spec.methodName
                                && it.typeSpec.isConreteEq(accessor.spec.typeSpec)
                    }

                    if (method != null && method.modifiers.contains(KoresModifier.PRIVATE))
                        method
                    else if (accessor.invokeType == InvokeType.INVOKE_SPECIAL
                            && (ctr != null && ctr.modifiers.contains(KoresModifier.PRIVATE))
                            || ((target as? ConstructorsHolder)?.constructors.orEmpty().isEmpty()
                                    && target.modifiers.contains(KoresModifier.PRIVATE))
                    )
                        ctr
                    else null
                } else null

            member?.let {
                MEMBER_ACCESSES.getOrNull(targetData)?.forEach { e ->
                    if (e.member == it)
                        return e
                }

                val existingNames =
                    MEMBER_ACCESSES.getOrNull(targetData).orEmpty().filter { it.owner.`is`(target) }
                        .map {
                            (it.newElementToAccess as Named).name
                        }

                val name = getNewNameBasedOnNameList("accessor\$",
                    target.methods.map { it.name } + existingNames
                )

                val isStatic = it is ModifiersHolder && it.modifiers.contains(KoresModifier.STATIC)

                val baseParameters =
                    if (isStatic)
                        listOf()
                    else
                        listOf(parameter(type = target, name = "this"))

                val acc = if (isStatic) Access.STATIC else Access.THIS

                val newMember: MethodDeclarationBase = when (it) {
                    is FieldDeclaration -> MethodDeclaration.Builder.builder()
                        .modifiers(
                            KoresModifier.PACKAGE_PRIVATE,
                            KoresModifier.SYNTHETIC,
                            KoresModifier.STATIC
                        )
                        .returnType(it.type)
                        .name(name)
                        .parameters(baseParameters)
                        .body(
                            Instructions.fromPart(
                                returnValue(
                                    it.type,
                                    accessField(Alias.THIS, acc, it.type, it.name)
                                )
                            )
                        )
                        .build()
                    is MethodDeclaration -> {
                        accessor as MethodInvocation

                        MethodDeclaration.Builder.builder()
                            .modifiers(
                                KoresModifier.PACKAGE_PRIVATE,
                                KoresModifier.SYNTHETIC,
                                KoresModifier.STATIC
                            )
                            .returnType(it.returnType)
                            .name(name)
                            .parameters(baseParameters + it.parameters)
                            .body(
                                Instructions.fromPart(
                                    returnValue(
                                        it.type,
                                        invoke(
                                            accessor.invokeType,
                                            Alias.THIS,
                                            acc,
                                            it.name,
                                            it.typeSpec,
                                            it.parameters.access
                                        )
                                    )

                                )
                            )
                            .build()
                    }
                    is ConstructorDeclaration -> {
                        val newPname = getNewName(
                            "access\$",
                            it.parameters
                        )

                        val newName = getNewName("\$", INNER_CLASSES.require(data))

                        val innerType = ClassDeclaration.Builder.builder()
                            .outerType(target)
                            .modifiers(
                                KoresModifier.PACKAGE_PRIVATE,
                                KoresModifier.SYNTHETIC,
                                KoresModifier.STATIC
                            )
                            .name(newName)
                            .build()

                        INNER_CLASSES.add(data, innerType)

                        ConstructorDeclaration.Builder.builder()
                            .modifiers(KoresModifier.PACKAGE_PRIVATE, KoresModifier.SYNTHETIC)
                            .innerTypes(innerType)
                            .parameters(
                                it.parameters + parameter(
                                    type = innerType,
                                    name = newPname
                                )
                            )
                            .body(
                                Instructions.fromPart(
                                    invokeThisConstructor(it.typeSpec, it.parameters.access)
                                )
                            )
                            .build()

                    }
                    else -> TODO()
                }

                MemberAccess(type, member, target, newMember).also {
                    MEMBER_ACCESSES.add(targetData, it)
                    return it
                }
            }
        }
    }

    return null
}

fun getTopLevelOuter(data: TypedData): TypeDeclaration? {
    var parent: TypedData? = data
    var last: TypeDeclaration? = null

    while (parent != null) {
        TYPE_DECLARATION.getOrNull(parent)?.let {
            last = it
        }

        parent = parent.parent
    }

    return last
}

fun getConstructors(part: TypeDeclaration): List<ConstructorDeclaration> {
    val isStatic = part.modifiers.contains(KoresModifier.STATIC)
    val outerType = part.outerType

    if (!isStatic && outerType != null) {

        val localLocalPart = part
        if (localLocalPart is ConstructorsHolder && !isStatic && !outerType.isInterface) {

            val allNames =
                localLocalPart.fields.map { it.name } + localLocalPart.constructors.flatMap {
                    it.parameters.map { it.name }
                }

            val singleName =
                getNewNameBasedOnNameList(TypeDeclarationProcessor.baseOuterName, allNames)

            val newCtrs =
                if (localLocalPart.constructors.isNotEmpty()) {
                    localLocalPart.constructors.map {
                        val newParams =
                            listOf(parameter(type = outerType, name = singleName)) + it.parameters

                        it.builder()
                            .parameters(newParams)
                            .build()
                    }
                } else {
                    listOf(
                        ConstructorDeclaration.Builder.builder()
                            .modifiers(part.modifiers.filter { it.modifierType == ModifierType.VISIBILITY }.toSet())
                            .parameters(parameter(type = outerType, name = singleName))
                            .build()
                    )
                }

            return newCtrs
        }
    }

    return (part as? ConstructorsHolder)?.constructors.orEmpty()
}

val String.internal get() = this.replace('.', '/')