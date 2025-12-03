package com.bj.security.serviceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bj.security.entity.UserEntity;
import com.bj.security.service.JWTService;
import com.bj.security.util.Constants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTServiceImpl implements JWTService{

	@Value("${jwt.token.validity}")
    private long EXPIRATION_TIME; 

	@Override
	public String extractSubject(String token) {
		return extractClaim(token, Claims::getSubject);
	}
	
	private<T> T extractClaim(String token , Function<Claims, T> claimsResolvers) {

		final Claims claims = extactAllClaims(token); 
		return claimsResolvers.apply(claims);
	}


	private java.security.Key getSiginKey() {

		byte[] key = Decoders.BASE64.decode("413F4428472B4B6250655368566D597033676397924422645294840406351");

		return (java.security.Key) Keys.hmacShaKeyFor(key);

	}

	private Claims extactAllClaims(String token) {

		return Jwts.parserBuilder().setSigningKey(getSiginKey()).build().parseClaimsJws(token).getBody();
	}
	
	
	public String generateToken(UserEntity user) {
    	
    	String orgName = user.getOrgName().concat(Constants.HYPHEN).concat(user.getBranch());
    	user.setOrgName(orgName);
    	List<String> authorities = new ArrayList<>();

		Map<String, Object> claims = new HashMap<>();
		claims.put("authorities", authorities);
		claims.put("type", "access");
		claims.put("orgId", user.getOrganizationId());
		claims.put("orgName", orgName);
		claims.put("role", user.getRole());
//		claims.put("userCode", user.getUserCode());
    	
    	
    	return Jwts.builder()
    			.setClaims(claims)
    			.setSubject(user.getUserId()+"~"+user.getSalutation()+Constants.SPACE+user.getFirstName()+Constants.SPACE+user.getLastName()
				+Constants.COMMA+user.getOrgName())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.signWith(getSiginKey(), SignatureAlgorithm.HS256)
				.compact();
    }
	
	public boolean validateToken(String token) {
		return !isTokenExpired(token);
	}

	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}
	
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

}
