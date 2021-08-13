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

import com.github.jonathanxd.kores.Instructions;
import com.github.jonathanxd.kores.Types;
import com.github.jonathanxd.kores.base.ClassDeclaration;
import com.github.jonathanxd.kores.base.KoresModifier;
import com.github.jonathanxd.kores.base.FieldDeclaration;
import com.github.jonathanxd.kores.base.MethodDeclaration;
import com.github.jonathanxd.kores.base.TypeDeclaration;
import com.github.jonathanxd.kores.bytecode.BytecodeClass;
import com.github.jonathanxd.kores.bytecode.classloader.CodeClassLoader;
import com.github.jonathanxd.kores.bytecode.processor.BytecodeGenerator;
import com.github.jonathanxd.kores.factory.Factories;
import com.github.jonathanxd.kores.factory.InvocationFactory;
import com.github.jonathanxd.kores.helper.Predefined;

import org.junit.Test;

import java.io.PrintStream;
import java.util.Collection;
import java.util.List;

import static com.github.jonathanxd.kores.Types.VOID;
import static com.github.jonathanxd.kores.factory.Factories.accessStaticField;
import static com.github.jonathanxd.kores.factory.Factories.accessVariable;
import static com.github.jonathanxd.kores.factory.Factories.typeSpec;
import static com.github.jonathanxd.kores.factory.VariableFactory.variable;
import static com.github.jonathanxd.kores.literal.Literals.STRING;
import static kotlin.collections.CollectionsKt.listOf;

public class Wiki {


    @SuppressWarnings("unchecked")
    @Test
    public void wiki() throws Throwable {

        TypeDeclaration decl = ClassDeclaration.Builder.builder()
                .modifiers(KoresModifier.PUBLIC)
                .name("com.MyClass")
                .fields(
                        FieldDeclaration.Builder.builder()
                                .modifiers(KoresModifier.PRIVATE, KoresModifier.FINAL)
                                .type(Types.STRING)
                                .name("myField")
                                .value(STRING("Hello"))
                                .build()
                )
                .methods(MethodDeclaration.Builder.builder()
                        .modifiers(KoresModifier.PUBLIC)
                        .name("test")
                        .parameters(Factories.parameter(String.class, "name"))
                        .body(Instructions.fromVarArgs(
                                variable(Types.STRING, "variable"),
                                Predefined.invokePrintlnStr(STRING("Hello world")),
                                Predefined.invokePrintlnStr(accessVariable(String.class, "name")),
                                accessStaticField(System.class, PrintStream.class, "out"),
                                InvocationFactory.invokeVirtual(
                                        PrintStream.class,
                                        accessStaticField(System.class, PrintStream.class, "out"),
                                        "println",
                                        typeSpec(VOID, Types.STRING),
                                        listOf(STRING("Hello")))
                        ))
                        .build()
                )
                .build();

        BytecodeGenerator bytecodeGenerator = new BytecodeGenerator();

        List<? extends BytecodeClass> gen = bytecodeGenerator.process(decl);

        ResultSaver.save(this.getClass(), gen);

        CodeClassLoader codeClassLoader = new CodeClassLoader();

        Class<?> define = codeClassLoader.define((Collection<BytecodeClass>) gen);

        define.getDeclaredMethod("test", String.class).invoke(define.newInstance(), "codeapi");


    }
}
