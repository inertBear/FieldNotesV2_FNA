package com.fieldnotes.fna.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Models a response received from FNP
 */

public class FNResponse {
    public FNReponseType mResponseType;
    public String mMessage;
    public ArrayList<HashMap<String, String>> mResultList;
    public Date mTimestamp;
    public Map<String, String> mMetadata;

    public FNResponse(final FNResponse.Builder builder) {
        mResponseType = builder.responseType;
        mMessage = builder.message;
        mResultList = builder.resultList;
        mTimestamp = builder.timestamp;
        mMetadata = builder.metadata;
    }

    public static FNResponse.Builder newBuilder() {
        return new FNResponse.Builder();
    }

    public static FNResponse.Builder newBuilder(final FNResponse copy) {
        return new FNResponse.Builder(copy);
    }

    public FNReponseType getResponseType() {
        return mResponseType;
    }

    public String getMessage() {
        return mMessage;
    }

    public ArrayList<HashMap<String, String>> getResultList() {
        return mResultList;
    }

    public Date getTimestamp() {
        return mTimestamp;
    }

    public Map<String, String> getMetadata() {
        return mMetadata;
    }

    public static final class Builder {
        private FNReponseType responseType;
        private String message;
        private ArrayList<HashMap<String, String>> resultList;
        private Date timestamp;
        private Map<String, String> metadata;

        private Builder() {
        }

        public Builder(final FNResponse copy) {
            responseType = copy.mResponseType;
            message = copy.mMessage;
            resultList = copy.mResultList;
            timestamp = copy.mTimestamp;
            metadata = copy.mMetadata;
        }

        public FNResponse.Builder setStatustype(final FNReponseType responseType) {
            this.responseType = responseType;
            return this;
        }

        public FNResponse.Builder setMessage(final String message) {
            this.message = message;
            return this;
        }

        public FNResponse.Builder setResultList(final ArrayList<HashMap<String, String>> resultList){
            this.resultList = resultList;
            return this;
        }

        // TODO: should be automatically set
        public FNResponse.Builder setTimestamp(final Date timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public FNResponse.Builder setMetadata(final Map<String, String> metadata) {
            this.metadata = metadata;
            return this;
        }

        public FNResponse build() {
            return new FNResponse(this);
        }
    }
}
