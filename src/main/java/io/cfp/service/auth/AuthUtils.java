/*
 * Copyright (c) 2016 BreizhCamp
 * [http://breizhcamp.org]
 *
 * This file is part of CFP.io.
 *
 * CFP.io is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package io.cfp.service.auth;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.ReadOnlyJWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.cfp.domain.token.Token;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

/**
 * Created by tmaugin on 15/05/2015.
 */
public final class AuthUtils {

    private static final JWSHeader JWT_HEADER = new JWSHeader(JWSAlgorithm.HS256);
    public static final String AUTH_HEADER_KEY = "Authorization";

    public static String TOKEN_SECRET;

    /**
     * Get sub from JWT header
     * @param authHeader
     * @return JWT sub param
     * @throws ParseException
     * @throws JOSEException
     */
    public static String getSubject(String authHeader) throws ParseException, JOSEException {
        return decodeToken(authHeader).getSubject();
    }

    /**
     * Return get JWT claims set from http request
     * @param httpRequest
     * @return JWT claims set
     */
    public static JWTClaimsSet getTokenBody(HttpServletRequest httpRequest)
    {
        String authHeader = httpRequest.getHeader(AuthUtils.AUTH_HEADER_KEY);
        if (StringUtils.isBlank(authHeader) || authHeader.split(" ").length != 2) {
        } else {
            JWTClaimsSet claimSet = null;
            try {
                claimSet = (JWTClaimsSet) AuthUtils.decodeToken(authHeader);
                return claimSet;
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (JOSEException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Return get JWT claims set from jwt header
     * @param authHeader
     * @return
     * @throws ParseException
     * @throws JOSEException
     */
    public static ReadOnlyJWTClaimsSet decodeToken(String authHeader) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(getSerializedToken(authHeader));
        if (signedJWT.verify(new MACVerifier(TOKEN_SECRET))) {
            return signedJWT.getJWTClaimsSet();
        } else {
            throw new JOSEException("Signature verification failed");
        }
    }

    /**
     * Create JWT token
     * @param host
     * @param sub
     * @param verified
     * @return
     * @throws JOSEException
     */
    public static Token createToken(String host, String sub, boolean verified) throws JOSEException {
        JWTClaimsSet claim = new JWTClaimsSet();
        claim.setSubject(sub);
        claim.setIssuer(host);
        claim.setCustomClaim("verified", verified);
        claim.setIssueTime(DateTime.now().toDate());
        claim.setExpirationTime(DateTime.now().plusDays(14).toDate());

        JWSSigner signer = new MACSigner(TOKEN_SECRET);
        SignedJWT jwt = new SignedJWT(JWT_HEADER, claim);
        jwt.sign(signer);

        return new Token(jwt.serialize());
    }

    /**
     * get serialized part of JWT header
     * @param authHeader
     * @return Serialized part of JWT header
     */
    public static String getSerializedToken(String authHeader) {
        return authHeader.split(" ")[1];
    }
}
