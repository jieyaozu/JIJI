package com.jiji.iready.entity;

/**
 * Created by jieyaozu on 2016/7/26.
 */
public class BaseEntity<T> {

    private Header header;

    private T body;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public class Header {
        private String status;
        private String markid;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMarkid() {
            return markid;
        }

        public void setMarkid(String markid) {
            this.markid = markid;
        }

        public String getMessage() {
            String result = "";
            if (status == null || status.equals("")) {
                return "数据出错了~";
            }
            int index = Integer.parseInt(status);
            switch (index) {
                case 1:
                    result = "正常";
                    break;
                case 2:
                    result = "无数据";
                    break;
                case 3:
                    result = "服务异常";
                    break;
                case 4:
                    result = "数据无变化";
                    break;
                case 5:
                    result = "参数错误";
                    break;
                case 6:
                    result = "屏蔽";
                    break;
                default:
                    result = "数据出错了~";
                    break;
            }
            return result;
        }
    }
}
