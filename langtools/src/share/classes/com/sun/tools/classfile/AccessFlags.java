/*
 * Copyright 2007-2008 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package com.sun.tools.classfile;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * See JVMS3, sections 4.2, 4.6, 4.7.
 *
 *  <p><b>This is NOT part of any API supported by Sun Microsystems.  If
 *  you write code that depends on this, you do so at your own risk.
 *  This code and its internal interfaces are subject to change or
 *  deletion without notice.</b>
 */
public class AccessFlags {
    public static final int ACC_PUBLIC        = 0x0001; // class, inner, field, method
    public static final int ACC_PRIVATE       = 0x0002; //        inner, field, method
    public static final int ACC_PROTECTED     = 0x0004; //        inner, field, method
    public static final int ACC_STATIC        = 0x0008; //        inner, field, method
    public static final int ACC_FINAL         = 0x0010; // class, inner, field, method
    public static final int ACC_SUPER         = 0x0020; // class
    public static final int ACC_SYNCHRONIZED  = 0x0020; //                      method
    public static final int ACC_VOLATILE      = 0x0040; //               field
    public static final int ACC_BRIDGE        = 0x0040; //                      method
    public static final int ACC_TRANSIENT     = 0x0080; //               field
    public static final int ACC_VARARGS       = 0x0080; //                      method
    public static final int ACC_NATIVE        = 0x0100; //                      method
    public static final int ACC_INTERFACE     = 0x0200; // class, inner
    public static final int ACC_ABSTRACT      = 0x0400; // class, inner,        method
    public static final int ACC_STRICT        = 0x0800; //                      method
    public static final int ACC_SYNTHETIC     = 0x1000; // class, inner, field, method
    public static final int ACC_ANNOTATION    = 0x2000; // class, inner
    public static final int ACC_ENUM          = 0x4000; // class, inner, field
    public static final int ACC_MODULE        = 0x8000; // class, inner, field, method

    private static enum Type { Class, InnerClass, Field, Method};

    AccessFlags(ClassReader cr) throws IOException {
        this(cr.readUnsignedShort());
    }

    public AccessFlags(int flags) {
        this.flags = flags;
    }

    public AccessFlags ignore(int mask) {
        return new AccessFlags(flags & ~mask);
    }

    public boolean is(int mask) {
        return (flags & mask) != 0;
    }

    private static final int[] classModifiers = {
        ACC_PUBLIC, ACC_FINAL, ACC_ABSTRACT, ACC_MODULE
    };

    private static final int[] classFlags = {
        ACC_PUBLIC, ACC_FINAL, ACC_SUPER, ACC_INTERFACE, ACC_ABSTRACT,
        ACC_SYNTHETIC, ACC_ANNOTATION, ACC_ENUM, ACC_MODULE
    };

    public Set<String> getClassModifiers() {
        int f = ((flags & ACC_INTERFACE) != 0 ? flags & ~ACC_ABSTRACT : flags);
        return getModifiers(f, classModifiers, Type.Class);
    }

    public Set<String> getClassFlags() {
        return getFlags(classFlags, Type.Class);
    }

    private static final int[] innerClassModifiers = {
        ACC_PUBLIC, ACC_PRIVATE, ACC_PROTECTED, ACC_STATIC, ACC_FINAL,
        ACC_ABSTRACT, ACC_MODULE
    };

    private static final int[] innerClassFlags = {
        ACC_PUBLIC, ACC_PRIVATE, ACC_PROTECTED, ACC_STATIC, ACC_FINAL, ACC_SUPER,
        ACC_INTERFACE, ACC_ABSTRACT, ACC_SYNTHETIC, ACC_ANNOTATION, ACC_ENUM, ACC_MODULE
    };

    public Set<String> getInnerClassModifiers() {
        int f = ((flags & ACC_INTERFACE) != 0 ? flags & ~ACC_ABSTRACT : flags);
        return getModifiers(f, innerClassModifiers, Type.InnerClass);
    }

    public Set<String> getInnerClassFlags() {
        return getFlags(innerClassFlags, Type.InnerClass);
    }

    private static final int[] fieldModifiers = {
        ACC_PUBLIC, ACC_PRIVATE, ACC_PROTECTED, ACC_STATIC, ACC_FINAL,
        ACC_VOLATILE, ACC_TRANSIENT, ACC_MODULE
    };

    private static final int[] fieldFlags = {
        ACC_PUBLIC, ACC_PRIVATE, ACC_PROTECTED, ACC_STATIC, ACC_FINAL,
        ACC_VOLATILE, ACC_TRANSIENT, ACC_SYNTHETIC, ACC_ENUM, ACC_MODULE
    };

    public Set<String> getFieldModifiers() {
        return getModifiers(fieldModifiers, Type.Field);
    }

    public Set<String> getFieldFlags() {
        return getFlags(fieldFlags, Type.Field);
    }

    private static final int[] methodModifiers = {
        ACC_PUBLIC, ACC_PRIVATE, ACC_PROTECTED, ACC_STATIC, ACC_FINAL,
        ACC_SYNCHRONIZED, ACC_NATIVE, ACC_ABSTRACT, ACC_STRICT, ACC_MODULE
    };

    private static final int[] methodFlags = {
        ACC_PUBLIC, ACC_PRIVATE, ACC_PROTECTED, ACC_STATIC, ACC_FINAL,
        ACC_SYNCHRONIZED, ACC_BRIDGE, ACC_VARARGS, ACC_NATIVE, ACC_ABSTRACT,
        ACC_STRICT, ACC_SYNTHETIC, ACC_MODULE
    };

    public Set<String> getMethodModifiers() {
        return getModifiers(methodModifiers, Type.Method);
    }

    public Set<String> getMethodFlags() {
        return getFlags(methodFlags, Type.Method);
    }

    private Set<String> getModifiers(int[] modifierFlags, Type t) {
        return getModifiers(flags, modifierFlags, t);
    }

    private static Set<String> getModifiers(int flags, int[] modifierFlags, Type t) {
        Set<String> s = new LinkedHashSet<String>();
        for (int m: modifierFlags) {
            if ((flags & m) != 0)
                s.add(flagToModifier(m, t));
        }
        return s;
    }

    private Set<String> getFlags(int[] expectedFlags, Type t) {
        Set<String> s = new LinkedHashSet<String>();
        int f = flags;
        for (int e: expectedFlags) {
            if ((f & e) != 0) {
                s.add(flagToName(e, t));
                f = f & ~e;
            }
        }
        while (f != 0) {
            int bit = Integer.highestOneBit(f);
            s.add("0x" + Integer.toHexString(bit));
            f = f & ~bit;
        }
        return s;
    }

    private static String flagToModifier(int flag, Type t) {
        switch (flag) {
            case ACC_PUBLIC:
                return "public";
            case ACC_PRIVATE:
                return "private";
            case ACC_PROTECTED:
                return "protected";
            case ACC_STATIC:
                return "static";
            case ACC_FINAL:
                return "final";
            case ACC_SYNCHRONIZED:
                return "synchronized";
            case 0x80:
                return (t == Type.Field ? "transient" : null);
            case ACC_VOLATILE:
                return "volatile";
            case ACC_NATIVE:
                return "native";
            case ACC_ABSTRACT:
                return "abstract";
            case ACC_STRICT:
                return "strictfp";
            case ACC_MODULE:
                return "module";
            default:
                return null;
        }
    }

    private static String flagToName(int flag, Type t) {
        switch (flag) {
        case ACC_PUBLIC:
            return "ACC_PUBLIC";
        case ACC_PRIVATE:
            return "ACC_PRIVATE";
        case ACC_PROTECTED:
            return "ACC_PROTECTED";
        case ACC_STATIC:
            return "ACC_STATIC";
        case ACC_FINAL:
            return "ACC_FINAL";
        case 0x20:
            return (t == Type.Class ? "ACC_SUPER" : "ACC_SYNCHRONIZED");
        case 0x40:
            return (t == Type.Field ? "ACC_VOLATILE" : "ACC_BRIDGE");
        case 0x80:
            return (t == Type.Field ? "ACC_TRANSIENT" : "ACC_VARARGS");
        case ACC_NATIVE:
            return "ACC_NATIVE";
        case ACC_INTERFACE:
            return "ACC_INTERFACE";
        case ACC_ABSTRACT:
            return "ACC_ABSTRACT";
        case ACC_STRICT:
            return "ACC_STRICT";
        case ACC_SYNTHETIC:
            return "ACC_SYNTHETIC";
        case ACC_ANNOTATION:
            return "ACC_ANNOTATION";
        case ACC_ENUM:
            return "ACC_ENUM";
        case ACC_MODULE:
            return "ACC_MODULE";
        default:
            return null;
        }
    }

    final int flags;
}
