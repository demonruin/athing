package com.github.ompc.athing.aliyun.framework.util;

import java.util.Scanner;

import static java.util.Objects.requireNonNull;

/**
 * I/O操作工具类
 */
public class IOUtils {

    /**
     * 获取LOGO
     *
     * @return LOGO
     */
    public static String getLogo(String path) {
        final ClassLoader loader = IOUtils.class.getClassLoader();
        final StringBuilder logoSB = new StringBuilder();
        try (final Scanner scanner = new Scanner(requireNonNull(loader.getResourceAsStream(path)))) {
            while (scanner.hasNextLine()) {
                logoSB.append(scanner.nextLine()).append("\n");
            }
        }
        return logoSB.toString();
    }

}
