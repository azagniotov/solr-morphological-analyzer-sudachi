/*
 * Copyright (c) 2023-2024 Alexander Zagniotov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.azagniotov.lucene.analysis.ja.sudachi.tokenizer;

import static com.google.common.truth.Truth.assertThat;
import static org.apache.lucene.analysis.TokenStream.DEFAULT_TOKEN_ATTRIBUTE_FACTORY;

import com.worksap.nlp.sudachi.JapaneseDictionary;
import com.worksap.nlp.sudachi.Morpheme;
import com.worksap.nlp.sudachi.MorphemeList;
import io.github.azagniotov.lucene.analysis.ja.sudachi.cache.DictionaryCache;
import io.github.azagniotov.lucene.analysis.ja.sudachi.util.Strings;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class SudachiTokenizerTest {

    private static SudachiTokenizer sudachiTokenizer;
    private static final boolean DISCARD_PUNCTUATION = true;

    @BeforeClass
    public static void beforeClass() throws Exception {
        final Map<String, String> args = new HashMap<String, String>() {
            {
                put("mode", TokenizerMode.SEARCH.desc());
                put("discardPunctuation", String.valueOf(DISCARD_PUNCTUATION));
            }
        };
        final SudachiTokenizerFactory sudachiTokenizerFactory = new SudachiTokenizerFactory(args);
        sudachiTokenizer = (SudachiTokenizer) sudachiTokenizerFactory.create(DEFAULT_TOKEN_ATTRIBUTE_FACTORY);
    }

    @Test
    public void sanityCheck() {
        assertThat(sudachiTokenizer).isNotNull();
        assertThat(DictionaryCache.INSTANCE.get()).isNotNull();
        assertThat(DictionaryCache.INSTANCE.get()).isInstanceOf(JapaneseDictionary.class);
    }

    @DataProvider(name = "querySurfaces")
    public static Object[][] querySurfaces() {
        return new Object[][] {
            // https://raw.githubusercontent.com/WorksApplications/Sudachi/develop/docs/user_dict.md

            /* === Example: Start of custom tokenization using the user dictionary === */

            // ぼのぼの (https://en.wikipedia.org/wiki/Bonobono) is over-tokenized
            {"ぼのぼの", new Object[] {"ぼのぼの"}},
            {"ぼのぼのキャラクター", new Object[] {"ぼのぼの", "キャラクター"}},
            {"ぼのぼのアニメ公式サイト", new Object[] {"ぼのぼの", "アニメ", "公式", "サイト"}},

            // にじさんじ (https://en.wikipedia.org/wiki/Nijisanji) is tokenized incorrectly
            // ましろ (https://www.nijisanji.jp/talents/l/meme-mashiro) is tokenized incorrectly
            {"にじさんじ", new Object[] {"にじさんじ"}},
            {"にじさんじましろ", new Object[] {"にじさんじ", "ましろ"}},
            {"にじさんじのましろ", new Object[] {"にじさんじ", "の", "ましろ"}},

            /* === Example: End of custom tokenization using the user dictionary === */

            {"令和", new Object[] {"令和"}},
            {"機能性食品", new Object[] {"機能", "性", "食品"}},
            {"労働者協同組合", new Object[] {"労働", "者", "協同", "組合"}},
            {"客室乗務員", new Object[] {"客室", "乗務", "員"}},
            // MeCab IPAdic: 医薬品, 安全, 管理, 責任, 者
            {"医薬品安全管理責任者", new Object[] {"医薬", "品", "安全", "管理", "責任", "者"}},
            {
                "I was playing on the computers all day",
                new Object[] {"I", "was", "playing", "on", "the", "computers", "all", "day"}
            },
            {
                "The quick 客室乗務員 brown fox jumps over the lazy dog 医薬品安全管理責任者",
                new Object[] {
                    "The", "quick", "客室", "乗務", "員", "brown", "fox", "jumps", "over", "the", "lazy", "dog", "医薬", "品",
                    "安全", "管理", "責任", "者"
                }
            },
            {"消費者安全調査委員会", new Object[] {"消費", "者", "安全", "調査", "委員", "会"}},
            {"選挙管理委員会", new Object[] {"選挙", "管理", "委員", "会"}},
            // MeCab IPAdic: カンヌ国際映画祭
            {"カンヌ国際映画祭", new Object[] {"カンヌ", "国際", "映画", "祭"}},
            // MeCab IPAdic: さ, っぽ, ろ, テレビ塔
            {"さっぽろテレビ塔", new Object[] {"さっぽろ", "テレビ", "塔"}},
            {"京都。東京.東京都。京都", new Object[] {"京都", "東京", "東京", "都", "京都"}},
            {"清水寺は東京都にあります", new Object[] {"清水寺", "は", "東京", "都", "に", "あり", "ます"}},
            // MeCab IPAdic: ちい, か, わ
            {"ちいかわ", new Object[] {"ちいかわ"}},
            {"くら寿司ちいかわ", new Object[] {"くら", "寿司", "ちいかわ"}},
            {"ソフトウェアエンジニア", new Object[] {"ソフトウェア", "エンジニア"}},
            // MeCab IPAdic: 関西国際空港
            {"関西国際空港", new Object[] {"関西", "国際", "空港"}},
            {"五十嵐淳子", new Object[] {"五十嵐", "淳子"}},
            {"鬼滅", new Object[] {"鬼", "滅"}},
            // MeCab IPAdic: 鬼, 滅, の, 刃
            {"鬼滅の刃", new Object[] {"鬼滅の刃"}}, // Kimetsu no Yaiba
            // MeCab IPAdic: 呪術, 廻, 戦
            {"呪術廻戦", new Object[] {"呪術廻戦"}}, // Jujutsu Kaizen
            // MeCab IPAdic: お, 試し, 用, 使い切り
            {"お試し用(使い切り)", new Object[] {"お", "試し", "用", "使い", "切り"}},
            // MeCab IPAdic: 聖川, 真, 斗
            {"聖川真斗", new Object[] {"聖川", "真斗"}},
            {"IKEAの椅子", new Object[] {"IKEA", "の", "椅子"}},
            {"京都東部", new Object[] {"京都", "東部"}},
            {"水 / 化粧+水", new Object[] {"水", "化粧", "水"}},
            {"平成", new Object[] {"平成"}},
            {"季節感···冬", new Object[] {"季節", "感", "冬"}},
            // MeCab IPAdic: ユニクロポロシャツ
            {"ユニクロポロシャツ", new Object[] {"ユニクロ", "ポロシャツ"}},
            {"アンパスィ", new Object[] {"アンパスィ"}},
            // MeCab IPAdic: トラックボール
            {"トラックボール", new Object[] {"トラック", "ボール"}},
            {"日本限定ソファ", new Object[] {"日本", "限定", "ソファ"}},
            {"吾輩は猫である", new Object[] {"吾輩", "は", "猫", "で", "ある"}},
            {"あなたが誰かを殺した", new Object[] {"あなた", "が", "誰", "か", "を", "殺し", "た"}},
            // MeCab IPAdic: 日本語, 単, 話, 版
            {"日本語【単話版】", new Object[] {"日本", "語", "単", "話", "版"}},
            // MeCab IPAdic: 日本語, 単, 話, 版
            {"日本語 単話版", new Object[] {"日本", "語", "単", "話", "版"}},
            {"アフラ・マヅダ", new Object[] {"アフラ・マヅダ"}},
            {"楷・行書", new Object[] {"楷・行書"}},
            // MeCab IPAdic: 楷行, 書
            {"楷行書", new Object[] {"楷行書"}},
            {"日本語と「記号」の話し", new Object[] {"日本", "語", "と", "記号", "の", "話し"}},
            {"桃太郎電鉄", new Object[] {"桃太郎", "電鉄"}},
            {"甲斐田晴", new Object[] {"甲斐田", "晴"}},
            {"椎名ニキ", new Object[] {"椎名", "ニキ"}},
            {"シルバニア", new Object[] {"シルバニア"}},
            {"ポケカ", new Object[] {"ポケカ"}},
            // MeCab IPAdic: おそ, 松
            {"おそ松", new Object[] {"おそ松"}},
            // MeCab IPAdic: 日本経済新聞
            {"日本経済新聞", new Object[] {"日本", "経済", "新聞"}},
            // MeCab IPAdic: りん, ご, ジュース, を, 飲ん, だ
            {"りんごジュースを飲んだ", new Object[] {"りんご", "ジュース", "を", "飲ん", "だ"}},
            // MeCab IPAdic: 日本人
            {"日本人", new Object[] {"日本", "人"}},
            {"セカオワ", new Object[] {"セカオワ"}},
            // MeCab IPAdic: 魔法使い, の, 嫁
            {"魔法使いの嫁", new Object[] {"魔法", "使い", "の", "嫁"}},
            {"ステラ ルー", new Object[] {"ステラ", "ルー"}},
            {"ステラルー", new Object[] {"ステラルー"}},
            {"パラライ", new Object[] {"パラライ"}},
            {"ルイヴィトン財布", new Object[] {"ルイヴィトン", "財布"}},
            {"終わりのセラフ", new Object[] {"終わり", "の", "セラフ"}},
            {"グランドセイコー", new Object[] {"グランド", "セイコー"}},
            {"神宮寺レン", new Object[] {"神宮寺", "レン"}},
            {"ストウブ", new Object[] {"ストウブ"}},
            {"マキタ", new Object[] {"マキタ"}},
            // MeCab IPAdic: クロ, ミ
            {"クロミ", new Object[] {"クロミ"}},
            // {"すもももももももものうち", new Object[] {"すもも", "も", "もも", "も", "もも", "の", "うち"}},
            {"イーブイヒーローズbox未開封シュリンク", new Object[] {"イーブイヒーローズ", "box", "未", "開封", "シュリンク"}},
            {"いいいいいいいい", new Object[] {"いい", "いい", "いい", "いい"}},
        };
    }

    @Test(dataProvider = "querySurfaces")
    public void testBasicTokenization(final Object query, final Object... expected) throws Exception {
        final Reader stringReader = new StringReader(query.toString());
        assertThat(tokens(sudachiTokenizer.tokenize(stringReader))).containsExactly(expected);
    }

    @Test
    public void testLongKatakana() throws Exception {
        final int limit = 5;
        final String katakanaWord = "テスト";

        final StringBuilder sb = new StringBuilder();
        sb.append(new String(new char[limit]).replace("\0", katakanaWord));

        final Reader stringReader = new StringReader(sb.toString());
        final List<String> tokens = tokens(sudachiTokenizer.tokenize(stringReader));

        assertThat(tokens.size()).isEqualTo(limit);
        assertThat(tokens).containsExactly("テスト", "テスト", "テスト", "テスト", "テスト");
    }

    private List<String> tokens(final Iterator<MorphemeList> morphemeList) {
        final List<Morpheme> result = new ArrayList<>();

        for (final Iterator<MorphemeList> it = morphemeList; it.hasNext(); ) {
            final MorphemeList sentence = it.next();

            sentence.iterator().forEachRemaining(morpheme -> {
                if (DISCARD_PUNCTUATION) {
                    if (!Strings.isPunctuation(morpheme.normalizedForm())) {
                        result.add(morpheme);
                    }
                } else {
                    result.add(morpheme);
                }
            });
        }

        return result.stream().map(Morpheme::surface).map(String::trim).collect(Collectors.toList());
    }
}
