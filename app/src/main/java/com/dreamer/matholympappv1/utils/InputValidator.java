package com.dreamer.matholympappv1.utils;

import android.text.TextUtils;
import java.util.regex.Pattern;

/**
 * Утилитный класс для валидации и санитизации пользовательского ввода.
 * Предотвращает инъекции, XSS атаки и другие уязвимости безопасности.
 */
public class InputValidator {
    
    private static final String TAG = "InputValidator";
    
    // Паттерн для проверки email
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+"
    );
    
    // Паттерн для проверки имени пользователя (только буквы, цифры, подчеркивания)
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_А-Яа-яЁё]+$");
    
    // Паттерн для проверки пароля (минимум 8 символов, буквы и цифры)
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]{8,}$");
    
    // Паттерн для числового ответа (только цифры)
    private static final Pattern NUMERIC_ANSWER_PATTERN = Pattern.compile("^\\d+$");
    
    // Запрещенные символы для предотвращения инъекций
    private static final String[] DANGEROUS_CHARS = {"<", ">", "\"", "'", ";", "--", "/*", "*/", "@@", "@"};
    
    /**
     * Проверяет валидность email адреса
     * @param email Email для проверки
     * @return true если email валиден
     */
    public static boolean isValidEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Проверяет валидность имени пользователя
     * @param username Имя пользователя для проверки
     * @return true если имя валидно
     */
    public static boolean isValidUsername(String username) {
        if (TextUtils.isEmpty(username)) {
            return false;
        }
        // Минимальная длина 3 символа
        if (username.trim().length() < 3) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username.trim()).matches();
    }
    
    /**
     * Проверяет надежность пароля
     * Требования: минимум 8 символов, содержит буквы и цифры
     * @param password Пароль для проверки
     * @return true если пароль надежен
     */
    public static boolean isStrongPassword(String password) {
        if (TextUtils.isEmpty(password)) {
            return false;
        }
        // Минимальная длина 8 символов
        if (password.length() < 8) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }
    
    /**
     * Проверяет что ответ содержит только цифры
     * @param answer Ответ для проверки
     * @return true если ответ числовой
     */
    public static boolean isNumericAnswer(String answer) {
        if (TextUtils.isEmpty(answer)) {
            return false;
        }
        return NUMERIC_ANSWER_PATTERN.matcher(answer.trim()).matches();
    }
    
    /**
     * Санитизирует строку - удаляет опасные символы и лишние пробелы
     * @param input Строка для санитизации
     * @return Очищенная строка
     */
    public static String sanitizeInput(String input) {
        if (TextUtils.isEmpty(input)) {
            return "";
        }
        
        String sanitized = input.trim();
        
        // Удаляем опасные символы
        for (String dangerousChar : DANGEROUS_CHARS) {
            sanitized = sanitized.replace(dangerousChar, "");
        }
        
        // Удаляем невидимые символы и управляющие последовательности
        sanitized = sanitized.replaceAll("[\\p{Cntrl}]", "");
        
        // Нормализуем пробелы
        sanitized = sanitized.replaceAll("\\s+", " ");
        
        return sanitized;
    }
    
    /**
     * Санитизирует ответ на задачу (разрешены только цифры и базовые математические символы)
     * @param answer Ответ для санитизации
     * @return Очищенный ответ
     */
    public static String sanitizeAnswer(String answer) {
        if (TextUtils.isEmpty(answer)) {
            return "";
        }
        
        String sanitized = answer.trim();
        
        // Разрешаем только цифры, запятую, точку и пробелы
        sanitized = sanitized.replaceAll("[^0-9,.\\s]", "");
        
        // Нормализуем пробелы
        sanitized = sanitized.replaceAll("\\s+", " ");
        
        return sanitized;
    }
    
    /**
     * Проверяет строку на наличие потенциальных NoSQL инъекций
     * @param input Строка для проверки
     * @return true если найдены подозрительные паттерны
     */
    public static boolean containsNoSqlInjection(String input) {
        if (TextUtils.isEmpty(input)) {
            return false;
        }
        
        String lowerInput = input.toLowerCase();
        
        // Проверка на специальные MongoDB/Firebase операторы
        String[] suspiciousPatterns = {
            "$where", "$ne", "$gt", "$lt", "$gte", "$lte", 
            "$in", "$nin", "$or", "$and", "$not", "$nor",
            "$exists", "$type", "$regex", "$mod"
        };
        
        for (String pattern : suspiciousPatterns) {
            if (lowerInput.contains(pattern.toLowerCase())) {
                return true;
            }
        }
        
        // Проверка на специальные символы JSON
        if (input.contains("{") || input.contains("}") || 
            input.contains("[") || input.contains("]")) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Полная проверка и санитизация пользовательского ввода перед отправкой в Firebase
     * @param input Входная строка
     * @return Очищенная строка или null если ввод недопустим
     */
    public static String validateAndSanitize(String input) {
        if (TextUtils.isEmpty(input)) {
            return null;
        }
        
        // Проверка на инъекции
        if (containsNoSqlInjection(input)) {
            return null;
        }
        
        // Санитизация
        return sanitizeInput(input);
    }
    
    /**
     * Проверяет и санитизирует ответ на задачу
     * @param answer Введенный ответ
     * @return Очищенный ответ или null если ответ недопустим
     */
    public static String validateAndSanitizeAnswer(String answer) {
        if (TextUtils.isEmpty(answer)) {
            return null;
        }
        
        // Санитизация ответа (только цифры)
        String sanitized = sanitizeAnswer(answer);
        
        if (TextUtils.isEmpty(sanitized)) {
            return null;
        }
        
        return sanitized;
    }
}
