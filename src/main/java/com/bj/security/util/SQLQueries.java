package com.bj.security.util;

public class SQLQueries {
	public final static  String FIND_USER_USING_USERID  = "SELECT u.user_id ,u.organizationid ,o.organizationname as orgname ,"
			+ "u.usertypeid ,ro.description as role ,u.salutation ,u.firstname ,u.lastname ,u.email ,u.statuscode "
			+ " ,u.createid ,u.createdate ,u.updateid ,u.updatedate, u.sourceorg ,u.branch ,u.phone ,u.mobile ,u.fax ,u.address ,u.userplatform "
			+ " FROM eps.authentication au inner join eps.user u on u.user_id = au.userid inner join eps.role ro on ro.id = u.roleid "
			+ " inner join eps.organization o on o.organization_id = u.organizationid WHERE au.userid = ? ";
	
	public final static  String FIND_USER_USING_USERNAME_PASSWORD  = "SELECT u.user_id ,u.organizationid ,o.organizationname as orgname ,"
			+ "u.usertypeid ,ro.description as role ,u.salutation ,u.firstname ,u.lastname ,u.email ,u.statuscode "
			+ " ,u.createid ,u.createdate ,u.updateid ,u.updatedate, u.sourceorg ,u.branch ,u.phone ,u.mobile ,u.fax ,u.address ,u.userplatform "
			+ " FROM eps.authentication au inner join eps.user u on u.user_id = au.userid inner join eps.role ro on ro.id = u.roleid "
			+ " inner join eps.organization o on o.organization_id = u.organizationid WHERE au.authenticationcode = ? "
			+ " AND au.authenticationpassword = ?";
}
