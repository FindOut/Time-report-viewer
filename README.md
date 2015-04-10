# Time-report-viewer

To get this project to work there are some things that need to be done.

In the "backend" map there needs to be a file called "backend-config.groovy" containing some configuration that should not be made public,
  these are:
  
```
// For dropbox integration
dropbox.time_report.folder.url = '<URL of the Dropbox-link to tha folder containing the timereports >'

// Spring security/LDAP
grails.plugin.springsecurity.providerNames = ['ldapAuthProvider'] // specify this when you want to skip attempting to load from db and only use LDAP
grails.plugin.springsecurity.password.algorithm = 'SHA'
grails.plugin.springsecurity.ldap.context.server = '<URL to your LDAP server>'
grails.plugin.springsecurity.ldap.context.managerDn = 'cn=admin,dc=find-out,dc=se' // "cn=xxx, dc=xxx, dc=xxx"
grails.plugin.springsecurity.ldap.context.managerPassword = ''
grails.plugin.springsecurity.ldap.search.filter="uid={0}"
grails.plugin.springsecurity.ldap.search.searchSubtree = true
grails.plugin.springsecurity.ldap.auth.hideUserNotFoundExceptions = false
grails.plugin.springsecurity.ldap.mapper.userDetailsClass = 'inetorgperson'
grails.plugin.springsecurity.ldap.authorities.groupSearchBase = '' // "ou=xxx, dc=xxx, dc=xxx"
grails.plugin.springsecurity.ldap.search.base = '' // "ou=xxx, dc=xxx, dc=xxx"
```

For this to work in "production" this file must also be precent in <tomcat_home>/lib

Configuration in tomcat:

-tomcat_home-/conf/server.xml:

```
<Connector port="80" protocol="HTTP/1.1"
               connectionTimeout="20000"
               redirectPort="443" />

<Connector SSLEnabled="true" acceptCount="100" clientAuth="false"
		          disableUploadTimeout="true" enableLookups="false" maxThreads="25"
		          port="443" keystoreFile="c:\keystore" keystorePass="MyGretPaswrd"
		          protocol="org.apache.coyote.http11.Http11NioProtocol" scheme="https"
		          secure="true" sslProtocol="TLS" />
```

-tomcat_home-/conf/web.xml:
to be added just before "\</web-app\>"
```
<security-constraint>
	<web-resource-collection>
	<web-resource-name>Protected Context</web-resource-name>
	<url-pattern>/*</url-pattern>
	</web-resource-collection>
	<!-- auth-constraint goes here if you requre authentication -->
	<user-data-constraint>
	<transport-guarantee>CONFIDENTIAL</transport-guarantee>
	</user-data-constraint>
</security-constraint>
```
