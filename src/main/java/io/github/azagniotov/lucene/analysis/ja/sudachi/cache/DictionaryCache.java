package io.github.azagniotov.lucene.analysis.ja.sudachi.cache;

import com.worksap.nlp.sudachi.Dictionary;
import com.worksap.nlp.sudachi.dictionary.UserDictionaryBuilder;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum DictionaryCache {
    INSTANCE;

    private static final String DICTIONARY_KEY = "dictionary";
    public static final String SYSTEM_DICT_LOCAL_PATH = "/tmp/sudachi/system-dict/system_core.dic";
    public static final String USER_DICT_LOCAL_PATH = "/tmp/sudachi/user_lexicon.dic";

    static {
        try {
            final String userLexiconCsvPath = "/tmp/sudachi/user_lexicon.csv";
            UserDictionaryBuilder.main(
                    new String[] {"-o", USER_DICT_LOCAL_PATH, "-s", SYSTEM_DICT_LOCAL_PATH, userLexiconCsvPath});
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to create JapaneseDictionary instance", e);
        }
    }

    private final Map<String, Dictionary> japaneseDictionaryCache;

    DictionaryCache() {
        japaneseDictionaryCache = new ConcurrentHashMap<>();
    }

    public void warmup() {
        // Does nothing but causes the static block to be executed
    }

    public void cache(final Dictionary dictionary) {
        if (!INSTANCE.japaneseDictionaryCache.containsKey(DICTIONARY_KEY)) {
            INSTANCE.japaneseDictionaryCache.put(DICTIONARY_KEY, dictionary);
        }
    }

    public Dictionary get() {
        return INSTANCE.japaneseDictionaryCache.get(DICTIONARY_KEY);
    }

    public void invalidate() {
        INSTANCE.japaneseDictionaryCache.clear();
    }
}
