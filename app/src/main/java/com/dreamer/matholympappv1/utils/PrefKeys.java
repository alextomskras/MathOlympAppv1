package com.dreamer.matholympappv1.utils;

/**
 * Единый словарь ключей локального хранилища.
 *
 * Раньше ключи были разбросаны по трём классам-обёрткам над SharedPreferences,
 * и одно и то же значение хранилось под разными именами в разных файлах:
 * лимиты решений — как "solution_limits" (int), "solutionlimits" (String)
 * и "solution_limits" в зашифрованном файле; счёт — как "zadacha_score"
 * и "user_score". Из-за этого значения рассинхронизировались.
 *
 * Все чтения и записи идут через UserPrefsRepository, имена ключей — только отсюда.
 */
public interface PrefKeys {

    String USERNAME = "username";
    String UID = "uid";
    String IS_LOGGED_IN = "is_logged_in";

    /** Число оставшихся попыток решения: единственный ключ для всех мест. */
    String SOLUTION_LIMITS = "solution_limits";
    /** Число оставшихся подсказок. */
    String HINT_LIMITS = "hint_limits";
    /** Счёт пользователя: единственный ключ (раньше был "zadacha_score"). */
    String USER_SCORE = "user_score";

    /** Настройка тёмной темы живёт в дефолтных SharedPreferences (PreferenceFragment). */
    String DARK_THEME_ENABLED = "dark_theme_enabled";

    /** Флаг: данные старого слоя уже перенесены в единое хранилище. */
    String MIGRATED_LEGACY = "migrated_from_legacy";

    int DEFAULT_SOLUTION_LIMITS = 1;
    int DEFAULT_HINT_LIMITS = 3;
}
