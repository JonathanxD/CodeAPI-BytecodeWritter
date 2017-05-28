/*
 *      CodeAPI-BytecodeWriter - Framework to generate Java code and Bytecode code. <https://github.com/JonathanxD/CodeAPI-BytecodeWriter>
 *
 *         The MIT License (MIT)
 *
 *      Copyright (c) 2017 TheRealBuggy/JonathanxD (https://github.com/JonathanxD/ & https://github.com/TheRealBuggy/) <jonathan.scripter@programmer.net>
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
package com.github.jonathanxd.codeapi.bytecode.processor.visitor

import com.github.jonathanxd.codeapi.base.ArgumentHolder
import com.github.jonathanxd.codeapi.base.InvokeType
import com.github.jonathanxd.codeapi.base.MethodInvocation
import com.github.jonathanxd.codeapi.bytecode.processor.IN_INVOKE_DYNAMIC
import com.github.jonathanxd.codeapi.bytecode.processor.METHOD_VISITOR
import com.github.jonathanxd.codeapi.bytecode.processor.require
import com.github.jonathanxd.codeapi.bytecode.util.CodeTypeUtil
import com.github.jonathanxd.codeapi.bytecode.util.InvokeTypeUtil
import com.github.jonathanxd.codeapi.bytecode.util.TypeSpecUtil
import com.github.jonathanxd.codeapi.processor.CodeProcessor
import com.github.jonathanxd.codeapi.processor.Processor
import com.github.jonathanxd.codeapi.type.CodeType
import com.github.jonathanxd.iutils.data.TypedData
import org.objectweb.asm.Opcodes
import java.lang.reflect.Type

object MethodInvocationVisitor : Processor<MethodInvocation> {

    override fun process(part: MethodInvocation, data: TypedData, codeProcessor: CodeProcessor<*>) {
        // MUST be retrieved here to avoid the data to be removed too late
        val isInInvokeDynamic = IN_INVOKE_DYNAMIC.getOrNull(data) != null

        val mv = METHOD_VISITOR.require(data).methodVisitor

        val localization: Type = Util.resolveType(part.localization, data)

        val invokeType: InvokeType = part.invokeType
        val target = part.target
        val specification = part.spec

        // Throw exception in case of invalid invoke type
        if (invokeType == InvokeType.INVOKE_VIRTUAL || invokeType == InvokeType.INVOKE_INTERFACE) {

            val correctInvokeType = InvokeType.get(localization)

            if (invokeType != correctInvokeType) {
                throw IllegalStateException("Invalid invocation type '$invokeType' for CodeType: '$localization' (correct invoke type: '$correctInvokeType')")
            }
        }

        if (!part.isSuperConstructorInvocation) {
            // Invoke constructor
            mv.visitTypeInsn(Opcodes.NEW, CodeTypeUtil.codeTypeToBinaryName(localization))
            mv.visitInsn(Opcodes.DUP)
        }

        if (target !is CodeType && !part.isSuperConstructorInvocation) {
            codeProcessor.process(target::class.java, target, data)
        }

        if (isInInvokeDynamic)
            IN_INVOKE_DYNAMIC.set(data, Unit, true)

        codeProcessor.process(ArgumentHolder::class.java, part, data)

        if (!isInInvokeDynamic) {
            mv.visitMethodInsn(
                    /*Type like invokestatic*/InvokeTypeUtil.toAsm(invokeType),
                    /*Localization*/CodeTypeUtil.codeTypeToBinaryName(localization),
                    /*Method name*/specification.methodName,
                    /*(ARGUMENT)RETURN*/TypeSpecUtil.typeSpecToAsm(specification.typeSpec),
                    invokeType.isInterface())
        }
    }

}