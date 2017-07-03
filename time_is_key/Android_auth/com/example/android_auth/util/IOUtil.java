package com.example.android_auth.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOUtil {
    public static String readFully(InputStream inputStream) throws IOException {
        Throwable th;
        if (inputStream == null) {
            return "";
        }
        BufferedInputStream bufferedInputStream = null;
        try {
            BufferedInputStream bufferedInputStream2 = new BufferedInputStream(inputStream);
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                try {
                    byte[] buffer = new byte[1024];
                    while (true) {
                        int available = bufferedInputStream2.read(buffer);
                        if (available < 0) {
                            break;
                        }
                        byteArrayOutputStream.write(buffer, 0, available);
                    }
                    String byteArrayOutputStream2 = byteArrayOutputStream.toString();
                    if (bufferedInputStream2 == null) {
                        return byteArrayOutputStream2;
                    }
                    bufferedInputStream2.close();
                    return byteArrayOutputStream2;
                } catch (Throwable th2) {
                    th = th2;
                    ByteArrayOutputStream byteArrayOutputStream3 = byteArrayOutputStream;
                    bufferedInputStream = bufferedInputStream2;
                    if (bufferedInputStream != null) {
                        bufferedInputStream.close();
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                bufferedInputStream = bufferedInputStream2;
                if (bufferedInputStream != null) {
                    bufferedInputStream.close();
                }
                throw th;
            }
        } catch (Throwable th4) {
            th = th4;
            if (bufferedInputStream != null) {
                bufferedInputStream.close();
            }
            throw th;
        }
    }
}
