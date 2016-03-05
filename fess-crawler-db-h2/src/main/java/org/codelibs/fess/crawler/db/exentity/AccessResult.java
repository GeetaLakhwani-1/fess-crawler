/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.fess.crawler.db.exentity;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.lang.SystemUtil;
import org.codelibs.fess.crawler.db.bsentity.BsAccessResult;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.ResultData;
import org.dbflute.optional.OptionalEntity;

/**
 * The entity of ACCESS_RESULT.
 * <p>
 * You can implement your original methods here. This class remains when
 * re-generating.
 * </p>
 *
 * @author DBFlute(AutoGenerator)
 */
public class AccessResult extends BsAccessResult implements
        org.codelibs.fess.crawler.entity.AccessResult<Long> {

    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    @Override
    public void init(final ResponseData responseData,
            final ResultData resultData) {

        setCreateTime(SystemUtil.currentTimeMillis()); // TODO response
        // time
        BeanUtil.copyBeanToBean(responseData, this);

        final AccessResultData accessResultData = new AccessResultData();
        BeanUtil.copyBeanToBean(resultData, accessResultData);
        setAccessResultDataAsOne(OptionalEntity.of(accessResultData));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.entity.AccessResult#getAccessResultData()
     */
    @Override
    public AccessResultData getAccessResultData() {
        return getAccessResultDataAsOne().orElse(null);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.codelibs.fess.crawler.entity.AccessResult#setAccessResultData(org.codelibs.fess.crawler
     * .db.exentity.AccessResultData)
     */
    @Override
    public void setAccessResultData(
            final org.codelibs.fess.crawler.entity.AccessResultData<Long> accessResultData) {
        setAccessResultDataAsOne(OptionalEntity
                .of((AccessResultData) accessResultData));
    }

}
