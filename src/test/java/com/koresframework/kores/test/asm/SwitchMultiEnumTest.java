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
package com.koresframework.kores.test.asm;

import com.github.jonathanxd.iutils.annotation.Named;
import com.github.jonathanxd.iutils.exception.RethrowException;
import com.koresframework.kores.Instruction;
import com.koresframework.kores.Instructions;
import com.koresframework.kores.base.ClassDeclaration;
import com.koresframework.kores.base.ConstructorDeclaration;
import com.koresframework.kores.base.KoresModifier;
import com.koresframework.kores.base.TypeDeclaration;
import com.koresframework.kores.factory.Factories;
import com.koresframework.kores.helper.Predefined;
import com.koresframework.kores.literal.Literals;
import com.koresframework.kores.test.SwitchTest_;
import com.koresframework.kores.type.KoresTypes;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.function.UnaryOperator;

public class SwitchMultiEnumTest {

    @Test
    public void switchTest() {

        TypeDeclaration $ = $();

        @Named("Instance") Object test = CommonBytecodeTest.test(this.getClass(), $, UnaryOperator.identity(), aClass -> {
            try {
                return aClass.getDeclaredConstructor(TestEnum.class, String.class, Object.class)
                        .newInstance(TestEnum.B, "AHEAD", new MyObj());
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                throw new RethrowException(e);
            }
        });
    }

    public static TypeDeclaration $() {

        ClassDeclaration classDeclaration = ClassDeclaration.Builder.builder()
                .qualifiedName("com.SwitchTestClass")
                .modifiers(KoresModifier.PUBLIC)
                .constructors(
                        ConstructorDeclaration.Builder.builder()
                                .modifiers(KoresModifier.PUBLIC)
                                .parameters(
                                        Factories.parameter(TestEnum.class, "testEnum"),
                                        Factories.parameter(String.class, "str"),
                                        Factories.parameter(Object.class, "o")
                                )
                                .body(
                                        Instructions.fromVarArgs(
                                                // Enum
                                                Factories.switchEnum(Factories.accessVariable(TestEnum.class, "testEnum"), Arrays.asList(
                                                        Factories.caseStatement(Factories.enumValue(KoresTypes.getKoresType(TestEnum.class), "A"), Instructions.fromVarArgs(print("A"), Factories.breakFlow())),
                                                        Factories.caseStatement(Factories.enumValue(KoresTypes.getKoresType(TestEnum.class), "B"), Instructions.fromVarArgs(print("B"))),
                                                        Factories.defaultCase(Instructions.fromVarArgs(print("B or default"))))
                                                ),
                                                // Enum
                                                Factories.switchEnum(Factories.accessVariable(TestEnum.class, "testEnum"), Arrays.asList(
                                                        Factories.caseStatement(Factories.enumValue(KoresTypes.getKoresType(TestEnum.class), "A"), Instructions.fromVarArgs(print("A"), Factories.breakFlow())),
                                                        Factories.caseStatement(Factories.enumValue(KoresTypes.getKoresType(TestEnum.class), "B"), Instructions.fromVarArgs(print("B"))),
                                                        Factories.defaultCase(Instructions.fromVarArgs(print("B or default"))))
                                                )

                                        )
                                )
                                .build()

                )
                .build();


        return classDeclaration;
    }

    private static Instruction print(String str) {
        return Predefined.invokePrintln(Literals.STRING(str));
    }

    public enum TestEnum {
        A,
        B
    }
    public static class MyObj {
        @Override
        public int hashCode() {
            return 0;
        }
    }
}
