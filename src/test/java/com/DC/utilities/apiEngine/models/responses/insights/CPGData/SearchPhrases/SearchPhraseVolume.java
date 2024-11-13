package com.DC.utilities.apiEngine.models.responses.insights.CPGData.SearchPhrases;

import java.util.Objects;

public class SearchPhraseVolume {

    public String searchPhrase;

    public long volume;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SearchPhraseVolume)) return false;
        SearchPhraseVolume that = (SearchPhraseVolume) o;
        return volume == that.volume && searchPhrase.equals(that.searchPhrase);
    }

    @Override
    public int hashCode() {
        return Objects.hash(searchPhrase, volume);
    }

    @Override
    public String toString() {
        return "SearchPhraseVolume{" +
                "searchPhrase='" + searchPhrase + '\'' +
                ", volume=" + volume +
                '}';
    }
}
