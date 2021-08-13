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
package com.github.jonathanxd.kores.test.asm;

import com.github.jonathanxd.kores.base.TypeDeclaration;
import com.github.jonathanxd.kores.test.BitwiseIfTest_;
import com.github.jonathanxd.iutils.annotation.Named;
import com.github.jonathanxd.iutils.link.Invokables;
import com.github.jonathanxd.iutils.link.Link;
import com.github.jonathanxd.iutils.link.Links;

import org.junit.Test;

import java.util.function.UnaryOperator;

public class BitwiseIfTest {

    @Test
    public void testBitwiseIf() throws Exception {
        TypeDeclaration $ = BitwiseIfTest_.$();

        @Named("Instance") Link<?> test = CommonBytecodeTest.test(this.getClass(), $, UnaryOperator.identity(), aClass -> {
            try {
                return Links.ofInvokable(Invokables.fromConstructor(aClass.getConstructor(Boolean.TYPE, Boolean.TYPE)));
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });

        System.out.println("==============================================");
        test.invoke(true, true);
        System.out.println("==============================================");
        test.invoke(false, false);
        System.out.println("==============================================");
        test.invoke(true, false);
        System.out.println("==============================================");
        test.invoke(false, true);
        System.out.println("==============================================");

    }
}
