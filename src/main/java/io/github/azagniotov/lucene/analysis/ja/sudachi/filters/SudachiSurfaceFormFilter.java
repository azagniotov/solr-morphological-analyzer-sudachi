/*
 * Copyright (c) 2017-2023 Sho Nakamura (https://github.com/sh0nk/solr-sudachi)
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
package io.github.azagniotov.lucene.analysis.ja.sudachi.filters;

import io.github.azagniotov.lucene.analysis.ja.sudachi.attributes.MorphemeConsumerAttribute;
import io.github.azagniotov.lucene.analysis.ja.sudachi.attributes.SudachiSurfaceFormAttribute;
import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/**
 * Replaces the SolrSudachiTokenizer's default normalized form term text
 * with the {@link io.github.azagniotov.lucene.analysis.ja.sudachi.attributes.SudachiSurfaceFormAttribute} (untouched input split token)
 */
public final class SudachiSurfaceFormFilter extends TokenFilter {
    private final CharTermAttribute termAtt;
    private final SudachiSurfaceFormAttribute surfaceAtt;
    private final MorphemeConsumerAttribute morphemeConsumerAttribute;

    public SudachiSurfaceFormFilter(TokenStream input) {
        super(input);
        termAtt = addAttribute(CharTermAttribute.class);
        surfaceAtt = addAttribute(SudachiSurfaceFormAttribute.class);

        this.morphemeConsumerAttribute = addAttribute(MorphemeConsumerAttribute.class);
        this.morphemeConsumerAttribute.setCurrentConsumer(this);
    }

    @Override
    public boolean incrementToken() throws IOException {
        if (input.incrementToken()) {

            if (morphemeConsumerAttribute.shouldConsume(this)) {
                if (surfaceAtt != null
                        && surfaceAtt.getSurface() != null
                        && !surfaceAtt.getSurface().isEmpty()) {
                    termAtt.setEmpty().append(surfaceAtt.getSurface());
                }
            }

            return true;
        }
        return false;
    }
}
