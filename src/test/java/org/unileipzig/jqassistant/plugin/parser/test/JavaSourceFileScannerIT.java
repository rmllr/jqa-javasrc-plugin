package org.unileipzig.jqassistant.plugin.parser.test;

import com.buschmais.jqassistant.plugin.common.api.model.FileDescriptor;
import org.junit.Ignore;
import org.junit.Test;
import org.unileipzig.jqassistant.plugin.parser.api.model.*;
import org.unileipzig.jqassistant.plugin.parser.api.scanner.JavaScope;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JavaSourceFileScannerIT extends com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT {
    private void scanFileHelper(String path, Consumer<JavaSourceFileDescriptor> then) {
        File folder = new File(path).getParentFile(), parent = folder.getParentFile();
        store.beginTransaction();
        JavaSourceDirectoryDescriptor dirDescriptor = getScanner().scan(folder, parent.getPath(), JavaScope.CLASSPATH);
        for (FileDescriptor fileDescriptor : dirDescriptor.getContains()) {
            if (path.contains(fileDescriptor.getFileName()))
                then.accept((JavaSourceFileDescriptor) fileDescriptor);
        }
        store.commitTransaction();
    }

    @Test
    @Ignore
    public void scanConstructors() {
        scanFileHelper("src/test/java/samples3/ConstructorExample.java", (fileDescriptor) -> {
            fileDescriptor.getTypes().forEach((type) -> {
                //System.out.println("scanned " + fileDescriptor.getFileName() + " " + type);
                assertEquals("ConstructorExample", type.getName());
            });
        });
    }

    @Test
    @Ignore
    public void scanMethodCalls() {
        scanFileHelper("src/test/java/samples3/MethodCallExample.java", (fileDescriptor) -> {
            fileDescriptor.getTypes().forEach((type) -> {
                assertEquals("MethodCallExample", type.getName());
                for (Object o : type.getDeclaredMethods()) {
                    if (o instanceof MethodDescriptor) {
                        MethodDescriptor method = (MethodDescriptor) o;
                        //System.out.println(method.getSignature()); // why is this redundantly in type.getDeclaredMethods()?
                        //   -> solved: the redundancy came from adding methods and fields (also) to the List of memberDescriptors
                        assertEquals("MethodCallExample", method.getDeclaringType().getName());
                        Set<String> signaturesOfCalledMethods = new HashSet<>();
                        Set<String> expectedSignaturesOfCalledMethods = new HashSet<>();
                        expectedSignaturesOfCalledMethods.add("calledMethod0()");
                        expectedSignaturesOfCalledMethods.add("calledMethod1()");
                        expectedSignaturesOfCalledMethods.add("calledMethod2()");
                        if (method.getName().equals("callingMethod")) {
                            method.getInvokes().forEach(invoke -> {
                                signaturesOfCalledMethods.add(invoke.getInvokedMethod().getSignature());
                            });
                            assertEquals(expectedSignaturesOfCalledMethods, signaturesOfCalledMethods);
                        }
                    } else if (o instanceof ClassTypeDescriptor) {
                        System.out.println("How did this get here: " + ((ClassTypeDescriptor) o).getFullQualifiedName());
                    } else {
                        throw new RuntimeException("...");
                    }
                }
                ;
            });
        });
    }

    @Test
    @Ignore
    public void scanThrowingMethod() {
        scanFileHelper("src/test/java/samples3/ThrowsExample.java", (fileDescriptor) -> {
            fileDescriptor.getTypes().forEach((type) -> {
                for (Object o : type.getDeclaredMethods()) {
                    if (o instanceof MethodDescriptor) { // analogous scanMethodCalls Test
                        MethodDescriptor method = (MethodDescriptor) o;
                        Set<String> exceptionNames = new HashSet<>();
                        Set<String> expectedExceptionNames = new HashSet<>();
                        method.getDeclaredThrowables().forEach((t) -> {
                            exceptionNames.add(t.getName());
                        });
                        switch (method.getName()) {
                            case "f1":
                                expectedExceptionNames.add("IOException");
                                assertEquals(expectedExceptionNames, exceptionNames);
                                break;
                            case "f2":
                                expectedExceptionNames.add("RuntimeException");
                                expectedExceptionNames.add("MyException");
                                assertEquals(expectedExceptionNames, exceptionNames);
                                break;
                        }
                    }
                }
            });
        });
    }

    @Test
    @Ignore
    public void scanEnumExample() {
        scanFileHelper("src/test/java/samples3/EnumExample.java", (fileDescriptor) -> {
            fileDescriptor.getTypes().forEach((type) -> {
                assert (type instanceof EnumTypeDescriptor);
                EnumTypeDescriptor enumDescriptor = (EnumTypeDescriptor) type;
                enumDescriptor.getDeclaredFields().forEach((field) -> {
                    assertTrue(field.getName().equals("ONE") || field.getName().equals("TWO"));
                    if (field.getName().equals("ONE")) {
                        assertTrue(field.getValue().getValue() == ((Object) 1));
                    } else {
                        assertTrue(field.getValue().getValue() == ((Object) 2));
                    }
                    ;
                });
            });
        });
    }

    @Test
    public void scanAnnotationExample() {
        scanFileHelper("src/test/java/samples3/AnnotationExample.java", (fileDescriptor) -> {
            fileDescriptor.getTypes().forEach((type) -> {
                assert (type instanceof ClassTypeDescriptor);
                ClassTypeDescriptor classDescriptor = (ClassTypeDescriptor) type;
            });
        });
    }
}