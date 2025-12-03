package com.bj.security.repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bj.security.util.Constants;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WhitelistRepository {

    private final JdbcTemplate jdbcTemplate;

    public WhitelistData fetchAllAsMap() {

        String sql = "SELECT modu.moduleName, wh.url,wh.JWTValidation,wh.CRCValidation FROM eps.whitelisted_URL wh "
        		+ "INNER JOIN eps.moduleDetails modu ON modu.module_id= wh.moduleid where wh.status = 'A' ";

        List<Row> rows = jdbcTemplate.query(sql, (rs, i) ->
                new Row(
                        rs.getString("moduleName"),
                        rs.getString("url"),
                        rs.getString("JWTValidation"),
                        rs.getString("CRCValidation")                        
                )
        );

        Map<String, List<String>> jwtAllowedUrlMap =  rows.stream().filter(row->Constants.NO.equals(row.JWTValidation))
                .collect(Collectors.groupingBy(
                        Row::serviceName,
                        Collectors.mapping(Row::url, Collectors.toList())
                ));
        Map<String, List<String>> crcAllowedUrlMap =  rows.stream().filter(row->Constants.NO.equals(row.CRCValidation))
                .collect(Collectors.groupingBy(
                        Row::serviceName,
                        Collectors.mapping(Row::url, Collectors.toList())
                ));
        
        return new WhitelistData(jwtAllowedUrlMap,crcAllowedUrlMap);
     }
    
    public int fetchTotalCount() {
        String sql = "SELECT COUNT(*) FROM eps.whitelisted_URL where status = 'A'";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }


    private record Row(String serviceName, String url,String JWTValidation,String CRCValidation ) {}
    public record WhitelistData(
            Map<String, List<String>> jwtMap,
            Map<String, List<String>> crcMap
    ) {}
}
