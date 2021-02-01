package com.github.ompc.athing.aliyun.thing.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpUtils {

    private static String getContentTypeCharset(HttpURLConnection connection) {
        final Pattern pattern = Pattern.compile("charset=\\S*");
        final Matcher matcher = pattern.matcher(connection.getContentType());
        final String charset;
        if (matcher.find()) {
            charset = matcher.group().replace("charset=", "");
        } else {
            charset = "UTF-8";
        }
        return charset;
    }

    /**
     * 从指定URL下载文本内容
     *
     * @param url              URL地址
     * @param connectTimeoutMs 连接超时时间
     * @param timeoutMs        超时时间
     * @return URL的文本信息
     * @throws IOException 下载文本出错
     */
    public static String getAsString(URL url, long connectTimeoutMs, long timeoutMs) throws IOException {

        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(false);
        connection.setUseCaches(false);
        connection.setRequestMethod("GET");
        connection.setConnectTimeout((int) (connectTimeoutMs / 1000));
        connection.setReadTimeout((int) (timeoutMs / 1000));
        connection.connect();

        final int code = connection.getResponseCode();
        if (code != 200) {
            throw new IOException("http response code: " + code);
        }

        try (final InputStream input = connection.getInputStream()) {

            final String charset = getContentTypeCharset(connection);
            final byte[] data = new byte[connection.getContentLength()];
            final byte[] buffer = new byte[2048];

            int size, sum = 0;
            while ((size = input.read(buffer)) != -1) {
                System.arraycopy(buffer, 0, data, sum, size);
                sum += size;
            }
            return new String(data, charset);
        } finally {
            connection.disconnect();
        }

    }

    /**
     * 从指定URL下载文件
     *
     * @param url              URL地址
     * @param connectTimeoutMs 连接超时时间
     * @param timeoutMs        超时时间
     * @param file             目标下载文件
     * @param downloading      下载进度
     * @throws IOException 下载文件出错
     */
    public static void download(URL url, long connectTimeoutMs, long timeoutMs, File file, Downloading downloading) throws IOException {
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(false);
        connection.setUseCaches(false);
        connection.setRequestMethod("GET");
        connection.setConnectTimeout((int) (connectTimeoutMs / 1000));
        connection.setReadTimeout((int) (timeoutMs / 1000));
        connection.connect();

        final int code = connection.getResponseCode();
        if (code != 200) {
            throw new IOException("http response code: " + code);
        }

        try (final FileOutputStream output = new FileOutputStream(file);
             final InputStream input = connection.getInputStream()) {

            final byte[] buffer = new byte[2048];
            final int total = connection.getContentLength();
            int size, sum = 0;
            while ((size = input.read(buffer)) != -1) {
                sum += size;
                output.write(buffer, 0, size);
                if (null != downloading) {
                    downloading.processing((int) (sum * 1.0f / total * 100));
                }
            }
            output.flush();
        } finally {
            connection.disconnect();
        }
    }

    /**
     * 下载进度
     */
    public interface Downloading {

        /**
         * 报告下载进度(0~100)
         *
         * @param process 进度
         */
        void processing(int process);

    }

}
