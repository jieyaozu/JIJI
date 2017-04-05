package com.yaozu.object.entity;

import java.util.List;

/**
 * Created by jxj42 on 2017/4/4.
 */

public class SectionReqData extends BaseEntity<SectionReqData.BodyEntity> {
    public class BodyEntity {
        private String code;
        private String message;
        private List<Section> sections;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public List<Section> getSections() {
            return sections;
        }

        public void setSections(List<Section> sections) {
            this.sections = sections;
        }
    }

    public class Section {
        private String section_id;
        private String section_name;

        public String getSection_id() {
            return section_id;
        }

        public void setSection_id(String section_id) {
            this.section_id = section_id;
        }

        public String getSection_name() {
            return section_name;
        }

        public void setSection_name(String section_name) {
            this.section_name = section_name;
        }
    }
}
