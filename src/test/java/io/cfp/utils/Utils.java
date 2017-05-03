package io.cfp.utils;

import io.cfp.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.joda.time.DateTime;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Utils {
    public static String getContent(String path) throws URISyntaxException, IOException {
        return new String(Files.readAllBytes(Paths.get(Utils.class.getResource(path).toURI())));
    }

    public static String createTokenForUser(User user) {
        return Jwts.builder()
            .setSubject(user.getEmail())
            .setExpiration(DateTime.now().plusSeconds(3600).toDate())
            .signWith(SignatureAlgorithm.HS512, "secret")
            .compact();
    }
}
