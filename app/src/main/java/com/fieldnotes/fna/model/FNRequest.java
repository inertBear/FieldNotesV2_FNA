package com.devhunter.fna.model;

import org.apache.http.NameValuePair;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Models a request sent to FNP
 */

public class FNRequest {
    public String mRequestingUser;
    public String mProductKey;
    public FNRequestType mRequestType;
    public List<NameValuePair> mRequestParams;
    public Date mTimestamp;
    public Map<String, String> mMetadata;

    public FNRequest(final FNRequest.Builder builder) {
        mRequestingUser = builder.requestingUser;
        mProductKey = builder.productKey;
        mRequestType = builder.requestType;
        mTimestamp = builder.timestamp;
        mRequestParams = builder.requestParams;
        mMetadata = builder.metadata;
    }

    public static FNRequest.Builder newBuilder() {
        return new FNRequest.Builder();
    }

    public static FNRequest.Builder newBuilder(final FNRequest copy) {
        return new FNRequest.Builder(copy);
    }

    public String getRequestingUser() {
        return mRequestingUser;
    }

    public String getProductKey() {
        return mProductKey;
    }

    public FNRequestType getRequestType() {
        return mRequestType;
    }

    public Date getTimetamp() {
        return mTimestamp;
    }

    public List<NameValuePair> getRequestParams() {
        return mRequestParams;
    }

    public Map<String, String> getMetadata() {
        return mMetadata;
    }

    public static final class Builder {
        private String requestingUser;
        private String productKey;
        private FNRequestType requestType;
        private Date timestamp;
        private List<NameValuePair> requestParams;
        private Map<String, String> metadata;


        private Builder() {
        }

        public Builder(final FNRequest copy) {
            requestingUser = copy.mRequestingUser;
            productKey = copy.mProductKey;
            requestType = copy.mRequestType;
            timestamp = copy.mTimestamp;
            requestParams = copy.mRequestParams;
            metadata = copy.mMetadata;
        }

        //TODO: should match the login user
        public FNRequest.Builder setRequestingUser(final String requestingUser) {
            this.requestingUser = requestingUser;
            return this;
        }

        public FNRequest.Builder setProductKey(final String productKey) {
            this.productKey = productKey;
            return this;
        }

        public FNRequest.Builder setRequestType(final FNRequestType requestType) {
            this.requestType = requestType;
            return this;
        }

        // TODO: should be automatically set
        public FNRequest.Builder setTimestamp(final Date timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public FNRequest.Builder setRequestParams(final List<NameValuePair> requestParams) {
            this.requestParams = requestParams;
            return this;
        }

        public FNRequest.Builder setMetadata(final Map<String, String> metadata) {
            this.metadata = metadata;
            return this;
        }

        public FNRequest build() {
            return new FNRequest(this);
        }
    }
}
