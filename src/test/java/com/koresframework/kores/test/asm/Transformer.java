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

import com.koresframework.kores.Instructions;
import com.koresframework.kores.bytecode.BytecodeClass;
import com.koresframework.kores.bytecode.BytecodeOptions;
import com.koresframework.kores.bytecode.VisitLineType;
import com.koresframework.kores.bytecode.common.MethodVisitorHelper;
import com.koresframework.kores.bytecode.processor.BytecodeGenerator;
import com.koresframework.kores.bytecode.processor.KeysKt;
import com.koresframework.kores.bytecode.util.ConstsKt;
import com.koresframework.kores.factory.Factories;
import com.koresframework.kores.helper.Predefined;
import com.koresframework.kores.literal.Literals;
import com.koresframework.kores.test.InvocationsTest_;
import com.github.jonathanxd.iutils.data.TypedData;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.List;

public class Transformer {

    public static byte[] toByteArray(InputStream input) throws IOException {
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        return output.toByteArray();
    }

    @Test
    public void transform() throws Throwable {

        WrappedPrintStream wrappedPrintStream = new WrappedPrintStream(System.out);
        System.setOut(wrappedPrintStream);

        BytecodeGenerator bytecodeGenerator = new BytecodeGenerator();

        bytecodeGenerator.getOptions().set(BytecodeOptions.VISIT_LINES, VisitLineType.FOLLOW_CODE_SOURCE);

        List<? extends BytecodeClass> bytecodeClasses = bytecodeGenerator.process(InvocationsTest_.$());

        byte[] bytes = bytecodeClasses.get(0).getBytecode();

        ClassReader cr = new ClassReader(bytes);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

        cr.accept(new MyClassVisitor(ConstsKt.ASM_API, cw), 0);

        byte[] bytes1 = cw.toByteArray();

        ResultSaver.save(this.getClass(), bytes1);

        BCLoader bcLoader = new BCLoader();

        Class<?> define = bcLoader.define("fullName.InvocationsTest__Generated", bytes1);

        Object o = define.newInstance();

        MethodHandle check = MethodHandles.lookup().findVirtual(define, "check", MethodType.methodType(Boolean.TYPE, Integer.TYPE));

        try {
            boolean b = (boolean) check.bindTo(o).invoke(9);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertTrue(wrappedPrintStream.printed.contains("Inicializado!"));
        Assert.assertTrue(wrappedPrintStream.printed.contains("XSD"));
    }


    static class MyClassVisitor extends ClassVisitor {
        public MyClassVisitor(int api) {
            super(api);
        }

        public MyClassVisitor(int api, ClassVisitor cv) {
            super(api, cv);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(52, access, name, signature, superName, interfaces);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (name.equals("<init>")) {
                return new MyVisitor(ConstsKt.ASM_API, super.visitMethod(access, name, desc, signature, exceptions));
            }

            if (name.contains("lambda$")) {
                if (desc.endsWith("Ljava/lang/String;")) {
                    return new FragmentTransformer(ConstsKt.ASM_API, super.visitMethod(access, name, desc, signature, exceptions));
                }
            }

            return super.visitMethod(access, name, desc, signature, exceptions);
        }
    }

    private static class FragmentTransformer extends MethodVisitor {

        private final BytecodeGenerator bytecodeGenerator = new BytecodeGenerator();

        public FragmentTransformer(int api, MethodVisitor mv) {
            super(api, mv);
        }

        @Override
        public void visitInsn(int opcode) {

            if (opcode == Opcodes.ARETURN) {
                MethodVisitorHelper methodVisitorHelper = new MethodVisitorHelper(super.mv, new ArrayList<>());
                TypedData data = new TypedData();

                KeysKt.getMETHOD_VISITOR().set(data, methodVisitorHelper);
                KeysKt.getIN_EXPRESSION().set(data, 0);

                super.visitInsn(Opcodes.POP);

                bytecodeGenerator.process(
                        Instructions.fromPart(Factories.returnValue(String.class, Literals.STRING("XSD"))),
                        data);

                //bytecodeGenerator.gen
            }

            super.visitInsn(opcode);
        }
    }

    private static class MyVisitor extends MethodVisitor {

        private final BytecodeGenerator bytecodeGenerator = new BytecodeGenerator();

        public MyVisitor(int api, MethodVisitor mv) {
            super(api, mv);
        }

        public MyVisitor(int api) {
            super(api);
        }

        @Override
        public void visitInsn(int opcode) {

            if (opcode == Opcodes.RETURN) {
                MethodVisitorHelper methodVisitorHelper = new MethodVisitorHelper(super.mv, new ArrayList<>());

                TypedData data = new TypedData();

                KeysKt.getMETHOD_VISITOR().set(data, methodVisitorHelper);
                KeysKt.getIN_EXPRESSION().set(data, 0);

                bytecodeGenerator.process(
                        Instructions.fromPart(Predefined.invokePrintln(Literals.STRING("Inicializado!"))),
                        data);

                //bytecodeGenerator.gen
            }

            super.visitInsn(opcode);
        }
    }

    static class WrappedPrintStream extends PrintStream {

        public List<String> printed = new ArrayList<>();

        public WrappedPrintStream(PrintStream toWrap) {
            super(toWrap);
        }

        @Override
        public void println(String str) {
            super.println(str);
            printed.add(str);
        }

        @Override
        public void println(Object obj) {
            super.println(obj);
            printed.add(String.valueOf(obj));
        }
    }
}
