package com.sweetfun.utils;
import java.security.SecureRandom;
import java.util.Base64;

public class GenerateBase64Secret {
    /**
     * 生成指定字节长度的随机密钥，并转换为Base64编码字符串
     * @param byteLength 密钥字节长度
     * @return Base64编码的密钥字符串
     */
    public static String generateRandomKey(int byteLength) {
        // 使用安全的随机数生成器
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[byteLength];
        secureRandom.nextBytes(randomBytes);

        // 转换为Base64编码
        return Base64.getEncoder().encodeToString(randomBytes);
    }

    public static void main(String[] args) {
        // 生成64字节的随机密钥（Base64编码后长度约为88个字符）
        String key64Byte = generateRandomKey(64);
        System.out.println("64字节随机Base64密钥:");
        System.out.println(key64Byte);
        System.out.println("密钥Base64编码后长度: " + key64Byte.length() + " 字符");
    }
}
