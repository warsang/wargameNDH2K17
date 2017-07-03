/*
 * Decompiled with CFR 0_115.
 */
package com.example.android_auth.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOUtil {
    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    public static String readFully(InputStream var0) throws IOException {
        if (var0 == null) {
            return "";
        }
        var1_2 = new BufferedInputStream(var0);
        var2_3 = new ByteArrayOutputStream();
        try {
            var5_4 = new byte[1024];
            while ((var6_5 = var1_2.read(var5_4)) >= 0) {
                var2_3.write(var5_4, 0, var6_5);
            }
            var8_1 = var7_11 = var2_3.toString();
            if (var1_2 == null) return var8_1;
            ** GOTO lbl18
        }
        catch (Throwable var3_6) {
            block9 : {
                var4_10 = var1_2;
                ** GOTO lbl25
lbl18: // 1 sources:
                var1_2.close();
                return var8_1;
                catch (Throwable var3_8) {
                    var4_10 = null;
                    break block9;
                }
                catch (Throwable var3_9) {
                    var4_10 = var1_2;
                }
            }
            if (var4_10 == null) throw var3_7;
            var4_10.close();
            throw var3_7;
        }
    }
}

