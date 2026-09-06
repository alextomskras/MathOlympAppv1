package com.dreamer.matholympappv1.utils;

import android.text.TextUtils;
import android.util.Log;
import java.util.regex.Pattern;

/**
 * Утилитный класс для валидации и санитизации пользовательского ввода.
 * Предотвращает инъекции, XSS атаки и другие уязвимости безопасности.
 */
public class InputValidator {
    
    private static final String TAG = "InputValidator";
    
    // Максимальная длина для различных типов ввода
    private static final int MAX_EMAIL_LENGTH = 254;
    private static final int MAX_USERNAME_LENGTH = 50;
    private static final int MAX_PASSWORD_LENGTH = 128;
    private static final int MAX_INPUT_LENGTH = 500;
    private static final int MAX_ANSWER_LENGTH = 50;
    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MIN_PASSWORD_LENGTH = 8;
    
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
    
    // Паттерн для обнаружения HTML/XML тегов
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]*>");
    
    // Паттерн для обнаружения JavaScript событий
    private static final Pattern JAVASCRIPT_EVENT_PATTERN = Pattern.compile(
            "(?i)\\b(on\\w+)\\s*=", 
            Pattern.CASE_INSENSITIVE
    );
    
    // Паттерн для опасных URL схем
    private static final Pattern DANGEROUS_URL_PATTERN = Pattern.compile(
            "(?i)(javascript|data|vbscript):", 
            Pattern.CASE_INSENSITIVE
    );
    
    // Паттерн для SQL инъекций
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
            "(?i)('\\s*(or|and)\\s*'?\\d*'?\\s*=\\s*'?\\d*|'\\s*or\\s+'.*'\\s*=\\s*'|--\\s*$|/\\*.*\\*/|;\\s*(drop|delete|update|insert|alter))",
            Pattern.CASE_INSENSITIVE
    );
    
    // Паттерн для телефонного номера (международный формат)
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9\\s\\-\\(\\)]{7,20}$");
    
    // Запрещенные символы для предотвращения инъекций
    private static final String[] DANGEROUS_CHARS = {"<", ">", "\"", "'", ";", "--", "/*", "*/", "@@", "@"};
    
    // Опасные HTML сущности
    private static final String[] DANGEROUS_HTML_ENTITIES = {
        "&lt;", "&gt;", "&amp;", "&quot;", "&#x", "&#0", "&#39;"
    };
    
    /**
     * Проверяет валидность email адреса
     * @param email Email для проверки
     * @return true если email валиден
     */
    public static boolean isValidEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        }
        // Проверка на максимальную длину
        if (email.length() > MAX_EMAIL_LENGTH) {
            Log.w(TAG, "Email слишком длинный: " + email.length() + " символов");
            return false;
        }
        // Проверка на наличие HTML тегов
        if (containsHtmlTags(email)) {
            Log.w(TAG, "Email содержит HTML теги");
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
        String trimmedUsername = username.trim();
        // Проверка на минимальную длину
        if (trimmedUsername.length() < MIN_USERNAME_LENGTH) {
            Log.w(TAG, "Имя слишком короткое: " + trimmedUsername.length() + " символов");
            return false;
        }
        // Проверка на максимальную длину
        if (trimmedUsername.length() > MAX_USERNAME_LENGTH) {
            Log.w(TAG, "Имя слишком длинное: " + trimmedUsername.length() + " символов");
            return false;
        }
        // Проверка на наличие HTML тегов
        if (containsHtmlTags(trimmedUsername)) {
            Log.w(TAG, "Имя содержит HTML теги");
            return false;
        }
        return USERNAME_PATTERN.matcher(trimmedUsername).matches();
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
        // Проверка на минимальную длину
        if (password.length() < MIN_PASSWORD_LENGTH) {
            Log.w(TAG, "Пароль слишком короткий: " + password.length() + " символов");
            return false;
        }
        // Проверка на максимальную длину
        if (password.length() > MAX_PASSWORD_LENGTH) {
            Log.w(TAG, "Пароль слишком длинный: " + password.length() + " символов");
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
        // Проверка на максимальную длину
        if (answer.length() > MAX_ANSWER_LENGTH) {
            Log.w(TAG, "Ответ слишком длинный: " + answer.length() + " символов");
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
        
        // Проверка на максимальную длину
        if (sanitized.length() > MAX_INPUT_LENGTH) {
            Log.w(TAG, "Входные данные слишком длинные: " + sanitized.length() + " символов");
            sanitized = sanitized.substring(0, MAX_INPUT_LENGTH);
        }
        
        // Удаляем HTML теги
        sanitized = removeHtmlTags(sanitized);
        
        // Удаляем опасные символы
        for (String dangerousChar : DANGEROUS_CHARS) {
            sanitized = sanitized.replace(dangerousChar, "");
        }
        
        // Удаляем опасные HTML сущности
        for (String htmlEntity : DANGEROUS_HTML_ENTITIES) {
            sanitized = sanitized.replace(htmlEntity, "");
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
        
        // Проверка на максимальную длину
        if (sanitized.length() > MAX_ANSWER_LENGTH) {
            Log.w(TAG, "Ответ слишком длинный: " + sanitized.length() + " символов");
            sanitized = sanitized.substring(0, MAX_ANSWER_LENGTH);
        }
        
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
     * Проверяет наличие HTML/XML тегов в строке
     * @param input Строка для проверки
     * @return true если найдены HTML теги
     */
    public static boolean containsHtmlTags(String input) {
        if (TextUtils.isEmpty(input)) {
            return false;
        }
        return HTML_TAG_PATTERN.matcher(input).find();
    }
    
    /**
     * Проверяет наличие JavaScript событий в строке
     * @param input Строка для проверки
     * @return true если найдены JavaScript события
     */
    public static boolean containsJavaScriptEvents(String input) {
        if (TextUtils.isEmpty(input)) {
            return false;
        }
        return JAVASCRIPT_EVENT_PATTERN.matcher(input).find();
    }
    
    /**
     * Проверяет наличие опасных URL схем (javascript:, data:, vbscript:)
     * @param input Строка для проверки
     * @return true если найдены опасные URL схемы
     */
    public static boolean containsDangerousUrl(String input) {
        if (TextUtils.isEmpty(input)) {
            return false;
        }
        return DANGEROUS_URL_PATTERN.matcher(input).find();
    }
    
    /**
     * Проверяет наличие потенциальных SQL инъекций
     * @param input Строка для проверки
     * @return true если найдены SQL инъекции
     */
    public static boolean containsSqlInjection(String input) {
        if (TextUtils.isEmpty(input)) {
            return false;
        }
        return SQL_INJECTION_PATTERN.matcher(input).find();
    }
    
    /**
     * Проверяет валидность телефонного номера
     * @param phone Телефон для проверки
     * @return true если телефон валиден
     */
    public static boolean isValidPhone(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return false;
        }
        // Проверка на максимальную длину
        if (phone.length() > 20) {
            Log.w(TAG, "Телефон слишком длинный: " + phone.length() + " символов");
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }
    
    /**
     * Удаляет HTML теги из строки
     * @param input Строка для обработки
     * @return Строка без HTML тегов
     */
    public static String removeHtmlTags(String input) {
        if (TextUtils.isEmpty(input)) {
            return input;
        }
        return HTML_TAG_PATTERN.matcher(input).replaceAll("");
    }
    
    /**
     * Полная проверка на XSS атаки
     * @param input Строка для проверки
     * @return true если найдены XSS уязвимости
     */
    public static boolean containsXssAttack(String input) {
        if (TextUtils.isEmpty(input)) {
            return false;
        }
        
        // Проверка на HTML теги
        if (containsHtmlTags(input)) {
            Log.w(TAG, "Обнаружены HTML теги");
            return true;
        }
        
        // Проверка на JavaScript события
        if (containsJavaScriptEvents(input)) {
            Log.w(TAG, "Обнаружены JavaScript события");
            return true;
        }
        
        // Проверка на опасные URL схемы
        if (containsDangerousUrl(input)) {
            Log.w(TAG, "Обнаружены опасные URL схемы");
            return true;
        }
        
        // Проверка на HTML сущности
        for (String htmlEntity : DANGEROUS_HTML_ENTITIES) {
            if (input.contains(htmlEntity)) {
                Log.w(TAG, "Обнаружены опасные HTML сущности");
                return true;
            }
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
        
        // Проверка на максимальную длину
        if (input.length() > MAX_INPUT_LENGTH) {
            Log.w(TAG, "Входные данные слишком длинные: " + input.length() + " символов");
            return null;
        }
        
        // Проверка на XSS атаки
        if (containsXssAttack(input)) {
            Log.e(TAG, "Обнаружена XSS атака");
            return null;
        }
        
        // Проверка на SQL инъекции
        if (containsSqlInjection(input)) {
            Log.e(TAG, "Обнаружена SQL инъекция");
            return null;
        }
        
        // Проверка на NoSQL инъекции
        if (containsNoSqlInjection(input)) {
            Log.e(TAG, "Обнаружена NoSQL инъекция");
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
