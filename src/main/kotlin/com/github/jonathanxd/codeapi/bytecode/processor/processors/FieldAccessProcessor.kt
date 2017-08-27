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
package com.github.jonathanxd.codeapi.bytecode.processor.processors

import com.github.jonathanxd.codeapi.base.Access
import com.github.jonathanxd.codeapi.base.FieldAccess
import com.github.jonathanxd.codeapi.bytecode.GENERATE_SYNTHETIC_ACCESS
import com.github.jonathanxd.codeapi.bytecode.processor.IN_EXPRESSION
import com.github.jonathanxd.codeapi.bytecode.processor.METHOD_VISITOR
import com.github.jonathanxd.codeapi.bytecode.processor.incrementInContext
import com.github.jonathanxd.codeapi.processor.Processor
import com.github.jonathanxd.codeapi.processor.ProcessorManager
import com.github.jonathanxd.codeapi.type.CodeType
import com.github.jonathanxd.codeapi.util.safeForComparison
import com.github.jonathanxd.codeapi.util.typeDesc
import com.github.jonathanxd.iutils.data.TypedData
import com.github.jonathanxd.jwiutils.kt.require
import org.objectweb.asm.Opcodes

object FieldAccessProcessor : Processor<FieldAccess> {

    override fun process(part: FieldAccess, data: TypedData, processorManager: ProcessorManager<*>) {
        val mv = METHOD_VISITOR.require(data).methodVisitor

        val localization: CodeType = Util.resolveType(part.localization, data)

        val at = part.target
        val safeAt = at.safeForComparison

        val access =
                if (processorManager.options[GENERATE_SYNTHETIC_ACCESS]) accessMemberOfType(localization, part, data)
                else null

        if (access != null) {
            val invk = access.createInvokeToNewElement(at, emptyList())
            processorManager.process(invk, data)
        } else {

            if (safeAt !is Access || safeAt != Access.STATIC) {
                IN_EXPRESSION.incrementInContext(data) {
                    processorManager.process(at::class.java, at, data)
                }
            }

            if (safeAt is Access && safeAt == Access.STATIC) {
                mv.visitFieldInsn(Opcodes.GETSTATIC, localization.internalName, part.name, part.type.typeDesc)
            } else {
                // THIS
                mv.visitFieldInsn(Opcodes.GETFIELD, localization.internalName, part.name, part.type.typeDesc)
            }
        }
    }

}