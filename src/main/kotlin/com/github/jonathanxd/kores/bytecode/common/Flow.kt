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
package com.github.jonathanxd.kores.bytecode.common

import org.objectweb.asm.Label
import java.time.Instant
import com.github.jonathanxd.kores.base.Label as CodeLabel

/**
 * A class that hold information about the flow of the code.
 *
 * Example:
 *
 * <pre>{@code
 *     //@outsideStart
 *     for(int x = 0; x < 10: ++x) {
 *         //@insideStart
 *         body
 *         //@insideEnd
 *     }
 *     //@outsideEnd
 *
 * }</pre>
 *
 * <pre>{@code
 *     //@outsideStart
 *     switch(a) {
 *         //@insideStart
 *         case A: ...
 *         case B: ...
 *         //@insideEnd
 *     }
 *     //@outsideEnd
 *
 *
 * }</pre>
 */
data class Flow(
    val label: CodeLabel?,
    val outsideStart: Label,
    val insideStart: Label,
    val insideEnd: Label,
    val outsideEnd: Label
) : Timed {
    override val creationInstant: Instant = Instant.now()
}