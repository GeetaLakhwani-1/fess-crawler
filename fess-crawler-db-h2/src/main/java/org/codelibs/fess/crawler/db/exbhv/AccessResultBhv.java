/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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
package org.codelibs.fess.crawler.db.exbhv;

import org.codelibs.fess.crawler.db.bsbhv.BsAccessResultBhv;
import org.codelibs.fess.crawler.db.exbhv.pmbean.AccessResultBySessionIdPmb;

/**
 * The behavior of ACCESS_RESULT.
 * <p>
 * You can implement your original methods here. This class remains when
 * re-generating.
 * </p>
 *
 * @author DBFlute(AutoGenerator)
 */
public class AccessResultBhv extends BsAccessResultBhv {
    public int deleteBySessionId(final String sessionId) {
        // AccessResultCB cb2 = new AccessResultCB();
        // cb2.query().setSessionId_Equal(sessionId);
        // accessResultBhv.queryDelete(cb2);
        final AccessResultBySessionIdPmb pmb = new AccessResultBySessionIdPmb();
        pmb.setSessionId(sessionId);
        return outsideSql().execute(pmb);
    }

    public int deleteAll() {
        // AccessResultCB cb2 = new AccessResultCB();
        // accessResultBhv.queryDelete(cb2);
        final AccessResultBySessionIdPmb pmb = new AccessResultBySessionIdPmb();
        pmb.setSessionId(null);
        return outsideSql().execute(pmb);
    }
}
