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

import com.koresframework.kores.Types
import com.koresframework.kores.type.KoresType
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import java.lang.reflect.Array as ReflectArray


object ArrayUtil {

    fun arrayOpcodeFromType(koresType: KoresType): Int {
        when (koresType.type) {
            "int" -> return Opcodes.T_INT
            "boolean" -> return Opcodes.T_BOOLEAN
            "byte" -> return Opcodes.T_BYTE
            "char" -> return Opcodes.T_CHAR
            "double" -> return Opcodes.T_DOUBLE
            "float" -> return Opcodes.T_FLOAT
            "short" -> return Opcodes.T_SHORT
            "long" -> return Opcodes.T_LONG
            else -> return Integer.MIN_VALUE
        }
    }

    fun getArrayType(opcode: Int): KoresType {
        when (opcode) {
            Opcodes.T_BYTE -> return Types.CHAR
            Opcodes.T_BOOLEAN -> return Types.BOOLEAN
            Opcodes.T_CHAR -> return Types.CHAR
            Opcodes.T_DOUBLE -> return Types.DOUBLE
            Opcodes.T_FLOAT -> return Types.FLOAT
            Opcodes.T_INT -> return Types.INT
            Opcodes.T_LONG -> return Types.LONG
            Opcodes.T_SHORT -> return Types.SHORT
            else -> throw IllegalArgumentException("Cannot get type of array type opcode '$opcode'!")
        }
    }

    fun visitArrayStore(arrayType: KoresType, mv: MethodVisitor) {
        when (arrayType.canonicalName) {
            "int" -> {
                mv.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_INT)
            }
            "boolean" -> {
                mv.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_BOOLEAN)
            }
            "byte" -> {
                mv.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_BYTE)
            }
            "char" -> {
                mv.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_CHAR)
            }
            "double" -> {
                mv.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_DOUBLE)
            }
            "float" -> {
                mv.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_FLOAT)
            }
            "short" -> {
                mv.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_SHORT)
            }
            "long" -> {
                mv.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_LONG)
            }
            else -> {
                mv.visitTypeInsn(Opcodes.ANEWARRAY, arrayType.internalName)
            }
        }
    }
}