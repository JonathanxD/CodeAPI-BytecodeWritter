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
package com.koresframework.kores.bytecode.util

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

object InsnUtil {

    fun visitInt(num: Int, mv: MethodVisitor) {
        if (num == -1) {
            mv.visitInsn(Opcodes.ICONST_M1)
        } else if (num == 0) {
            mv.visitInsn(Opcodes.ICONST_0)
        } else if (num == 1) {
            mv.visitInsn(Opcodes.ICONST_1)
        } else if (num == 2) {
            mv.visitInsn(Opcodes.ICONST_2)
        } else if (num == 3) {
            mv.visitInsn(Opcodes.ICONST_3)
        } else if (num == 4) {
            mv.visitInsn(Opcodes.ICONST_4)
        } else if (num == 5) {
            mv.visitInsn(Opcodes.ICONST_5)
        } else if (num > java.lang.Byte.MIN_VALUE && num < java.lang.Byte.MAX_VALUE) {
            mv.visitIntInsn(Opcodes.BIPUSH, num)
        } else if (num > java.lang.Short.MIN_VALUE && num < java.lang.Short.MAX_VALUE) {
            mv.visitIntInsn(Opcodes.SIPUSH, num)
        } else {
            mv.visitLdcInsn(num)
        }
    }

    fun visitLong(num: Long, mv: MethodVisitor) {
        if (num == 0L) {
            mv.visitInsn(Opcodes.LCONST_0)
        } else if (num == 1L) {
            mv.visitInsn(Opcodes.LCONST_1)
        } else {
            mv.visitLdcInsn(num)
        }
    }

    fun visitDouble(num: Double, mv: MethodVisitor) {
        if (num == 0.0) {
            mv.visitInsn(Opcodes.DCONST_0)
        } else if (num == 1.0) {
            mv.visitInsn(Opcodes.DCONST_1)
        } else {
            mv.visitLdcInsn(num)
        }
    }

    fun visitFloat(num: Float, mv: MethodVisitor) {
        if (num == 0f) {
            mv.visitInsn(Opcodes.FCONST_0)
        } else if (num == 1f) {
            mv.visitInsn(Opcodes.FCONST_1)
        } else if (num == 2f) {
            mv.visitInsn(Opcodes.FCONST_2)
        } else {
            mv.visitLdcInsn(num)
        }
    }

    fun visitBoolean(num: Boolean, mv: MethodVisitor) {

        // True -> 1
        if (num) {
            mv.visitInsn(Opcodes.ICONST_1)
        } else {
            // False -> 0
            mv.visitInsn(Opcodes.ICONST_0)
        }
    }

}