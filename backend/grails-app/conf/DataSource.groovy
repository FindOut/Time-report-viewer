dataSource {
//    pooled = true
//    jmxExport = true
//    driverClassName = "org.h2.Driver"
//    username = "sa"
//    password = ""
    properties {
        maxActive = 50
        maxIdle = 25
        minIdle = 5
        initialSize = 5
        minEvictableIdleTimeMillis = 60000
        timeBetweenEvictionRunsMillis = 60000
        maxWait = 10000
        testWhileIdle=true
        validationQuery = "SELECT 1"
    }
    driverClassName = "com.mysql.jdbc.Driver"

    username = 'root'
    password = ''
}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
//    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory' // Hibernate 3
    cache.region.factory_class = 'org.hibernate.cache.ehcache.EhCacheRegionFactory' // Hibernate 4
    singleSession = true // configure OSIV singleSession mode
}

// environment specific settings
environments {
    development {
        dataSource {
            dbCreate = "create" // one of 'create', 'create-drop', 'update', 'validate', ''
            dialect = org.hibernate.dialect.MySQL5InnoDBDialect
            url = "jdbc:mysql://localhost/time_report_viewer?createDatabaseIfNotExist=true&amp;useUnicode=true&amp;Encoding=utf-8"
        }
    }
    test {
        dataSource {
            dbCreate = "update"
            url = "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE"
        }
    }
    production {
        dataSource {
            username = 'timeReportViewer'
            password = 'trapi'
            dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
            dialect = org.hibernate.dialect.MySQL5InnoDBDialect
            url = "jdbc:mysql://localhost/time_report_viewer?createDatabaseIfNotExist=true&amp;useUnicode=true&amp;Encoding=utf-8"
            // For heroku
//            username = 'b6375c83ce7351'
//            password = '66660d32'
//            dbCreate = "create" // one of 'create', 'create-drop', 'update', 'validate', ''
//            dialect = org.hibernate.dialect.MySQL5InnoDBDialect
//            url = "jdbc:mysql://us-cdbr-iron-east-02.cleardb.net/heroku_268065ee841d7dc?createDatabaseIfNotExist=true&amp;useUnicode=true&amp;Encoding=utf-8;reconnect=true"
        }
    }
}
