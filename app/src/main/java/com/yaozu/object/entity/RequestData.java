package com.yaozu.object.entity;

/**
 * Created by jieyaozu on 2016/8/2.
 */
public class RequestData extends BaseEntity<RequestData.BodyEntity> {


    public class BodyEntity {
        private String code;
        private String message;
        private String status;
        private String errorCode;

        public void setCode(String code) {
            this.code = code;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public String getStatus() {
            return status;
        }

        public String getErrorCode() {
            return errorCode;
        }
    }
}
