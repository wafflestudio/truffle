package io.wafflestudio.truffle.core.store.r2dbc

import com.infobip.spring.data.r2dbc.QuerydslR2dbcRepository

interface QueryDslRepository : QuerydslR2dbcRepository<AppTable, Long>
