package com.example.springsecurityjwtexample.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtil {

    //hash işlemi yapılırken kullanılacak key
    //secret algoritma çalışırken kullanılacak hash değeridir
    private String SECRET_KEY = "secret";

    //verilen token a ait kullanıcı adını döndürür.
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    //verilen token a ait bitiş süresini verir.
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    //verilen token a ait claims bilgisini alır.
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    //token'ın geçerlilik süresinin dolup dolmadığını kontrol eder.
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // userDetails objesini alır. createToken metoduna gönderir.
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    //generateToken metodu bu kısmı çağırır. Bu metod da token'ı oluşturan bazı bilgiler girilir.
    private String createToken(Map<String, Object> claims, String subject) {
        // setExpiration: Jwt token için sona erme süresini ayarlar
        return Jwts.builder()
                //claims nesnesi atanır
                .setClaims(claims)
                //ilgili kullanıcı bilgisi atanır
                .setSubject(subject)
                //tokenın başlangıç zamanı
                .setIssuedAt(new Date(System.currentTimeMillis()))
                //tokenın bitiş zamanı
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                //kullanılan algoritma ve algoritma çalışırken kullanılacak key değeri
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    //token hala geçerli mi? kullanıcı adı dogru ise ve token'ın geçerlilik süresi devam ediyorsa true döner.
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
